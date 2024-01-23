package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.RedeemRequestDto;
import cards.alice.monolith.common.repositories.CardRepository;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.common.web.mappers.CardMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;

import java.util.Map;
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
    private final CardMapper cardMapper;
    private final ObjectMapper objectMapper;
    private final JedisPooled jedis;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final Map<String, Future<?>> redeemRequestTtlTaskPool;

    public Card validateRedeemRequestDto(RedeemRequestDto redeemRequestDto) {
        // Validate
        final Long cardId = redeemRequestDto.getCardId();
        final Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException(Card.class, cardId));
        if (!(card.getCustomerId().equals(redeemRequestDto.getCustomerId()))) {
            throw new IllegalArgumentException("Card doesn't belong to current customer");
        }

        final Long redeemRuleId = redeemRequestDto.getRedeemRuleId();
        final RedeemRule redeemRule = redeemRuleRepository.findById(redeemRuleId)
                .orElseThrow(() -> new ResourceNotFoundException(RedeemRule.class, redeemRuleId));
        if (!card.getBlueprint().equals(redeemRule.getBlueprint())) {
            throw new IllegalArgumentException("Blueprint of card and that of redeemRule don't match");
        }

        return card;
    }

    @Override
    public RedeemRequestDto saveNewRedeemRequest(RedeemRequestDto redeemRequestDto) {
        redeemRequestDto.setId(null);
        redeemRequestDto.setIsRedeemed(false);

        final Card card = validateRedeemRequestDto(redeemRequestDto);

        // Configure RedeemRequestDto
        final Blueprint blueprint = card.getBlueprint();
        final UUID ownerId = blueprint.getStore().getOwnerId();
        redeemRequestDto.setOwnerId(ownerId);
        final String ownerRedeemRequestsKey = redeemRequestDto.getOwnerRedeemRequestsKey();
        final String fieldName = redeemRequestDto.getFieldName();
        final String serializedRedeemRequestDto = jedis.hget(ownerRedeemRequestsKey, fieldName);
        final RedeemRequestDto targetRedeemRequestDto;
        if (serializedRedeemRequestDto != null) {
            try {
                targetRedeemRequestDto = objectMapper.readValue(serializedRedeemRequestDto, RedeemRequestDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if (!targetRedeemRequestDto.getIsRedeemed()) {
                final Future<?> oldRedeemRequestTtlFuture = redeemRequestTtlTaskPool.remove(targetRedeemRequestDto.getId());
                if (oldRedeemRequestTtlFuture != null) {
                    // If not redeemed, cancel old timer (and set new timer later)
                    oldRedeemRequestTtlFuture.cancel(true);
                }
            } else {
                // If redeemed, new timer is not needed
                return targetRedeemRequestDto;
            }
        } else {
            // dto not exists or timer not found: Create new
            targetRedeemRequestDto = newRedeemRequest(redeemRequestDto, blueprint);
        }

        // Common: Set new timer
        /*Executor executor = CompletableFuture.delayedExecutor(watchRedeemRequestDurationSeconds, TimeUnit.SECONDS);
        executor.execute(() -> {
            jedis.hdel(ownerRedeemRequestsKey, fieldName);
        });*/
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
        redeemRequestDto.setTtl(watchRedeemRequestDurationSeconds * 1000);
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

    @Override
    public Boolean exists(String id) {
        final RedeemRequestDto redeemRequestDto = new RedeemRequestDto(id);
        final String serializedRedeemRequestDto =
                jedis.hget(redeemRequestDto.getOwnerRedeemRequestsKey(), redeemRequestDto.getFieldName());
        if (serializedRedeemRequestDto == null) {
            return false;
        }

        final RedeemRequestDto targetRedeemRequestDto;
        try {
            targetRedeemRequestDto = objectMapper.readValue(serializedRedeemRequestDto, RedeemRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return targetRedeemRequestDto.getToken()
                .compareTo(redeemRequestDto.getToken()) == 0;
    }

    @Override
    public void deleteRedeemRequestById(String id) {
        final RedeemRequestDto redeemRequestDto = new RedeemRequestDto(id);
        long deleted = jedis.hdel(redeemRequestDto.getOwnerRedeemRequestsKey(), redeemRequestDto.getFieldName());
        final Future<?> redeemRequestTtlTask = redeemRequestTtlTaskPool.remove(id);
        if (redeemRequestTtlTask != null) {
            redeemRequestTtlTask.cancel(true);
        }
    }
}
