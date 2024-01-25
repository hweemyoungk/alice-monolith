package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.RedeemRequestDto;
import cards.alice.monolith.common.repositories.CardRepository;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPooled;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

// TODO: Implement
@Service
@RequiredArgsConstructor
public class CustomerRedeemRequestServiceImpl implements CustomerRedeemRequestService {
    @Value("${cards.alice.customer.app.watch-redeem-request-duration-seconds}")
    private long watchRedeemRequestDurationSeconds;
    @Value("${cards.alice.customer.user-id}")
    private UUID customerId;

    private final CardRepository cardRepository;
    private final RedeemRuleRepository redeemRuleRepository;
    private final CustomerAuthenticatedCardAccessor authenticatedCardAccessor;
    private final CustomerAuthenticatedRedeemRequestAccessor authenticatedRedeemRequestAccessor;
    private final ObjectMapper objectMapper;
    private final JedisPooled jedis;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final Map<String, Future<?>> redeemRequestTtlTaskPool;

    private void validateRedeemRequestDto(RedeemRequestDto redeemRequestDto) {
        final Card card = authenticatedCardAccessor.findById(redeemRequestDto.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(Card.class, redeemRequestDto.getCardId()));

        // Validate
        if (!(card.getCustomerId().equals(redeemRequestDto.getCustomerId()))) {
            throw new IllegalArgumentException("Card doesn't belong to current customer");
        }

        final Long redeemRuleId = redeemRequestDto.getRedeemRuleId();
        final RedeemRule redeemRule = redeemRuleRepository.findById(redeemRuleId)
                .orElseThrow(() -> new ResourceNotFoundException(RedeemRule.class, redeemRuleId));
        if (!card.getBlueprint().equals(redeemRule.getBlueprint())) {
            throw new IllegalArgumentException("Blueprint of card and that of redeemRule don't match");
        }
    }

    @Override
    @Transactional
    public RedeemRequestDto handlePostRedeemRequest(RedeemRequestDto redeemRequestDtoFromCustomer) {
        validateRedeemRequestDto(redeemRequestDtoFromCustomer);

        redeemRequestDtoFromCustomer.setId(null);
        redeemRequestDtoFromCustomer.setIsRedeemed(false);

        // Configure RedeemRequestDto
        final Card card = cardRepository.findById(redeemRequestDtoFromCustomer.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(Card.class, redeemRequestDtoFromCustomer.getCardId()));
        final Blueprint blueprint = card.getBlueprint();
        final UUID ownerId = blueprint.getStore().getOwnerId();
        redeemRequestDtoFromCustomer.setOwnerId(ownerId);

        // Query Redis
        final String ownerRedeemRequestsKey = redeemRequestDtoFromCustomer.getOwnerRedeemRequestsKey();
        final String fieldName = redeemRequestDtoFromCustomer.getFieldName();
        final String serializedRedeemRequestDto = jedis.hget(ownerRedeemRequestsKey, fieldName);
        final RedeemRequestDto targetRedeemRequestDto;
        if (serializedRedeemRequestDto != null) {
            // Deserialize
            try {
                targetRedeemRequestDto = objectMapper.readValue(serializedRedeemRequestDto, RedeemRequestDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            // Check redeemed
            if (targetRedeemRequestDto.getIsRedeemed()) {
                // If redeemed, new timer is not needed
                return targetRedeemRequestDto;
            }

            // Not redeemed yet
            // Refresh ttl
            targetRedeemRequestDto.setTtlMillisecondsFromNow(watchRedeemRequestDurationSeconds * 1000);
            try {
                final String serializedTargetRedeemRequestDto  = objectMapper.writeValueAsString(targetRedeemRequestDto);
                jedis.hset(targetRedeemRequestDto.getOwnerRedeemRequestsKey(), targetRedeemRequestDto.getFieldName(), serializedTargetRedeemRequestDto);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            // Cancel old timer
            final Future<?> oldRedeemRequestTtlFuture = redeemRequestTtlTaskPool.remove(targetRedeemRequestDto.getId());
            if (oldRedeemRequestTtlFuture != null) {
                // If not redeemed, cancel old timer (and set new timer later)
                oldRedeemRequestTtlFuture.cancel(true);
            }
        } else {
            // dto not exists: Create new
            targetRedeemRequestDto = newRedeemRequest(redeemRequestDtoFromCustomer, blueprint);
        }

        // Set new timer
        final Future<?> newRedeemRequestTtlFuture = threadPoolTaskExecutor.submit(() -> {
            // TODO: do not block IO
            try {
                TimeUnit.SECONDS.sleep(watchRedeemRequestDurationSeconds);
            } catch (InterruptedException e) {
                System.out.println("[Timer]Interruption detected");
                return;
            }
            System.out.println("[Timer]Executing HDEL... " + targetRedeemRequestDto.getId());
            jedis.hdel(ownerRedeemRequestsKey, fieldName);
        });
        redeemRequestTtlTaskPool.put(targetRedeemRequestDto.getId(), newRedeemRequestTtlFuture);

        return targetRedeemRequestDto;
    }

    private RedeemRequestDto newRedeemRequest(RedeemRequestDto redeemRequestDto, Blueprint blueprint) {
        redeemRequestDto.setBlueprintDisplayName(blueprint.getDisplayName());
        redeemRequestDto.setTtlMillisecondsFromNow(watchRedeemRequestDurationSeconds * 1000);
        redeemRequestDto.setIsRedeemed(false);
        redeemRequestDto.setToken(UUID.randomUUID());

        final String serializedNewRedeemRequestDto;
        try {
            serializedNewRedeemRequestDto = objectMapper.writeValueAsString(redeemRequestDto);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize RedeemRequestDto", e);
        }

        jedis.hset(redeemRequestDto.getOwnerRedeemRequestsKey(), redeemRequestDto.getFieldName(), serializedNewRedeemRequestDto);
        final Future<?> oldRedeemRequestTtlFuture = redeemRequestTtlTaskPool.remove(redeemRequestDto.getId());
        if (oldRedeemRequestTtlFuture != null) {
            // Try canceling but DO NOT interrupt if removing: Always guarantee TTL.
            oldRedeemRequestTtlFuture.cancel(true);
        }
        // Should NOT happen but if timer not found, create new timer anyway.

        return redeemRequestDto;
    }

    private RedeemRequestDto getRedeemRequestById(String id) {
        final RedeemRequestDto redeemRequestDto = new RedeemRequestDto(id);
        final String serializedRedeemRequestDto =
                jedis.hget(redeemRequestDto.getOwnerRedeemRequestsKey(), redeemRequestDto.getFieldName());
        if (serializedRedeemRequestDto == null) {
            return null;
        }
        final RedeemRequestDto targetRedeemRequestDto;
        try {
            targetRedeemRequestDto = objectMapper.readValue(serializedRedeemRequestDto, RedeemRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return targetRedeemRequestDto;
    }

    @Override
    public boolean exists(RedeemRequestDto redeemRequestDtoFromCustomer) {
        final Optional<RedeemRequestDto> targetRedeemRequestDto
                = authenticatedRedeemRequestAccessor.findById(redeemRequestDtoFromCustomer.getId());

        if (targetRedeemRequestDto.isEmpty()) {
            return false;
        }

        if (targetRedeemRequestDto.get().getToken()
                .compareTo(redeemRequestDtoFromCustomer.getToken()) != 0) {
            return false;
        }

        return true;
    }

    @Override
    public void deleteRedeemRequest(RedeemRequestDto redeemRequestDto) {
        final RedeemRequestDto targetRedeemRequestDto = authenticatedRedeemRequestAccessor.findById(redeemRequestDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Redeem request not found"));
        jedis.hdel(targetRedeemRequestDto.getOwnerRedeemRequestsKey(), targetRedeemRequestDto.getFieldName());
        final Future<?> redeemRequestTtlTask = redeemRequestTtlTaskPool.remove(targetRedeemRequestDto.getId());
        if (redeemRequestTtlTask != null) {
            redeemRequestTtlTask.cancel(true);
        }
    }
}
