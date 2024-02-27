//package cards.alice.monolith.customer.services;
//
//import cards.alice.monolith.common.domain.Blueprint;
//import cards.alice.monolith.common.domain.Card;
//import cards.alice.monolith.common.domain.RedeemRule;
//import cards.alice.monolith.common.models.RedeemRequestDto;
//import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
//import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
//import cards.alice.monolith.customer.repositories.CustomerCardRepository;
//import cards.alice.monolith.customer.repositories.CustomerRedeemRuleRepository;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import redis.clients.jedis.JedisPooled;
//
//import java.util.*;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//
//@Service
//@RequiredArgsConstructor
//public class CustomerRedeemRequestServiceImpl {
//    @Value("${cards.alice.customer.redeem-request.watch-redeem-request-duration-seconds}")
//    private long watchRedeemRequestDurationSeconds;
//    @Value("${cards.alice.customer.user-id}")
//    private UUID customerId;
//
//    private final CustomerCardRepository cardRepository;
//    private final CustomerRedeemRuleRepository redeemRuleRepository;
//    private final CustomerAuthenticatedRedeemRequestAccessor authenticatedRedeemRequestAccessor;
//    private final ObjectMapper objectMapper;
//    private final JedisPooled jedis;
//    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
//    private final Map<String, Future<?>> redeemRequestTtlTaskPool;
//
//    private RedeemRequestDto preprocessNewRedeemRequestDto(RedeemRequestDto redeemRequestDto) {
//        // Validate
//        Set<String> violationMessages = new HashSet<>();
//        final Card card = cardRepository.findById(redeemRequestDto.getCardId())
//                .orElseThrow(() -> new ResourceNotFoundException(Card.class, redeemRequestDto.getCardId()));
//        if (!(card.getCustomerId().equals(redeemRequestDto.getCustomerId()))) {
//            violationMessages.add("Card doesn't belong to current customer");
//        }
//
//        final Long redeemRuleId = redeemRequestDto.getRedeemRuleId();
//        final RedeemRule redeemRule = redeemRuleRepository.findById(redeemRuleId)
//                .orElseThrow(() -> new ResourceNotFoundException(RedeemRule.class, redeemRuleId));
//        if (!card.getBlueprint().equals(redeemRule.getBlueprint())) {
//            violationMessages.add("Blueprint of card and that of redeemRule don't match");
//        }
//
//        if (!violationMessages.isEmpty()) {
//            throw new DtoProcessingException("Failed to preprocess RedeemRequestDto", violationMessages);
//        }
//
//        // id, version must be null
//        redeemRequestDto.setId(null);
//        // isDeleted must be false
//        redeemRequestDto.setIsDeleted(false);
//
//        // customerId
//        // Validated by @PreAuthorize postRedeemRequest
//
//        // customerDisplayName, cardId, redeemRuleId
//        // Validated by @Validated
//
//        // isRedeemed must be false
//        redeemRequestDto.setIsRedeemed(false);
//
//        // Set blueprintDisplayName
//        final Blueprint blueprint = card.getBlueprint();
//        redeemRequestDto.setBlueprintDisplayName(blueprint.getDisplayName());
//
//        // Set ownerId
//        redeemRequestDto.setOwnerId(blueprint.getStore().getOwnerId());
//
//        // Set expMilliseconds
//        redeemRequestDto.setTtlMillisecondsFromNow(watchRedeemRequestDurationSeconds * 1000);
//
//        // Set new token
//        redeemRequestDto.setToken(UUID.randomUUID());
//
//        return redeemRequestDto;
//    }
//
//    //@Override
//    @Transactional
//    public RedeemRequestDto handlePostRedeemRequest(RedeemRequestDto redeemRequestDtoFromCustomer) {
//        RedeemRequestDto preprocessedRedeemRequestDto = preprocessNewRedeemRequestDto(redeemRequestDtoFromCustomer);
//
//        // Query Redis
//        final String ownerRedeemRequestsKey = preprocessedRedeemRequestDto.getOwnerRedeemRequestsKey();
//        final String fieldName = preprocessedRedeemRequestDto.getFieldName();
//        final String serializedRedeemRequestDto = jedis.hget(ownerRedeemRequestsKey, fieldName);
//
//        final RedeemRequestDto targetRedeemRequestDto;
//        if (serializedRedeemRequestDto != null) {
//            // Deserialize
//            try {
//                targetRedeemRequestDto = objectMapper.readValue(serializedRedeemRequestDto, RedeemRequestDto.class);
//            } catch (JsonProcessingException e) {
//                throw new DtoProcessingException(e.getMessage());
//            }
//
//            // Check redeemed
//            if (targetRedeemRequestDto.getIsRedeemed()) {
//                // If redeemed, new timer is not needed
//                return targetRedeemRequestDto;
//            }
//
//            // Not redeemed yet
//            // Refresh ttl
//            targetRedeemRequestDto.setTtlMillisecondsFromNow(watchRedeemRequestDurationSeconds * 1000);
//            try {
//                final String serializedTargetRedeemRequestDto = objectMapper.writeValueAsString(targetRedeemRequestDto);
//                jedis.hset(targetRedeemRequestDto.getOwnerRedeemRequestsKey(), targetRedeemRequestDto.getFieldName(), serializedTargetRedeemRequestDto);
//            } catch (JsonProcessingException e) {
//                throw new DtoProcessingException(e.getMessage());
//            }
//
//            // Cancel old timer
//            final Future<?> oldRedeemRequestTtlFuture = redeemRequestTtlTaskPool.remove(targetRedeemRequestDto.getId());
//            if (oldRedeemRequestTtlFuture != null) {
//                // If not redeemed, cancel old timer (and set new timer later)
//                oldRedeemRequestTtlFuture.cancel(true);
//            }
//        } else {
//            // dto not exists: Create new
//            targetRedeemRequestDto = newRedeemRequest(preprocessedRedeemRequestDto);
//        }
//
//        // Set new timer
//        final Future<?> newRedeemRequestTtlFuture = threadPoolTaskExecutor.submit(() -> {
//            // TODO: do not block IO
//            try {
//                TimeUnit.SECONDS.sleep(watchRedeemRequestDurationSeconds);
//            } catch (InterruptedException e) {
//                System.out.println("[Timer]Interruption detected");
//                return;
//            }
//            System.out.println("[Timer]Executing HDEL... " + targetRedeemRequestDto.getId());
//            jedis.hdel(ownerRedeemRequestsKey, fieldName);
//        });
//        redeemRequestTtlTaskPool.put(targetRedeemRequestDto.getId(), newRedeemRequestTtlFuture);
//
//        return targetRedeemRequestDto;
//    }
//
//    private RedeemRequestDto newRedeemRequest(RedeemRequestDto redeemRequestDto) {
//        final String serializedNewRedeemRequestDto;
//        try {
//            serializedNewRedeemRequestDto = objectMapper.writeValueAsString(redeemRequestDto);
//        } catch (JsonProcessingException e) {
//            throw new IllegalArgumentException("Failed to serialize RedeemRequestDto", e);
//        }
//
//        jedis.hset(redeemRequestDto.getOwnerRedeemRequestsKey(), redeemRequestDto.getFieldName(), serializedNewRedeemRequestDto);
//        final Future<?> oldRedeemRequestTtlFuture = redeemRequestTtlTaskPool.remove(redeemRequestDto.getId());
//        if (oldRedeemRequestTtlFuture != null) {
//            oldRedeemRequestTtlFuture.cancel(true);
//        }
//
//        // Should NOT happen but if timer not found, create new timer anyway.
//
//        return redeemRequestDto;
//    }
//
//    private RedeemRequestDto getRedeemRequestById(String id) {
//        final RedeemRequestDto redeemRequestDto = new RedeemRequestDto(id);
//        final String serializedRedeemRequestDto =
//                jedis.hget(redeemRequestDto.getOwnerRedeemRequestsKey(), redeemRequestDto.getFieldName());
//        if (serializedRedeemRequestDto == null) {
//            return null;
//        }
//        final RedeemRequestDto targetRedeemRequestDto;
//        try {
//            targetRedeemRequestDto = objectMapper.readValue(serializedRedeemRequestDto, RedeemRequestDto.class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//        return targetRedeemRequestDto;
//    }
//
//    //@Override
//    public boolean exists(RedeemRequestDto redeemRequestDtoFromCustomer) {
//        final Optional<RedeemRequestDto> targetRedeemRequestDto
//                = authenticatedRedeemRequestAccessor.findById(redeemRequestDtoFromCustomer.getId());
//
//        if (targetRedeemRequestDto.isEmpty()) {
//            return false;
//        }
//
//        if (targetRedeemRequestDto.get().getToken()
//                .compareTo(redeemRequestDtoFromCustomer.getToken()) != 0) {
//            return false;
//        }
//
//        return true;
//    }
//
//    //@Override
//    public void deleteRedeemRequest(RedeemRequestDto redeemRequestDto) {
//        final RedeemRequestDto targetRedeemRequestDto = authenticatedRedeemRequestAccessor.findById(redeemRequestDto.getId())
//                .orElseThrow(() -> new IllegalArgumentException("Redeem request not found"));
//        jedis.hdel(targetRedeemRequestDto.getOwnerRedeemRequestsKey(), targetRedeemRequestDto.getFieldName());
//        final Future<?> redeemRequestTtlTask = redeemRequestTtlTaskPool.remove(targetRedeemRequestDto.getId());
//        if (redeemRequestTtlTask != null) {
//            redeemRequestTtlTask.cancel(true);
//        }
//    }
//}
