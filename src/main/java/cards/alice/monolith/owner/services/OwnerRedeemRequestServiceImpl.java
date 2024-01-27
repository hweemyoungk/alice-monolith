package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.models.RedeemDto;
import cards.alice.monolith.common.models.RedeemRequestDto;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.common.web.mappers.CardMapper;
import cards.alice.monolith.owner.repositories.OwnerCardRepository;
import cards.alice.monolith.owner.repositories.OwnerRedeemRuleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPooled;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static cards.alice.monolith.common.models.RedeemRequestDto.getOwnerRedeemRequestsKey;


@Service
@RequiredArgsConstructor
public class OwnerRedeemRequestServiceImpl implements OwnerRedeemRequestService {
    @Value("${cards.alice.customer.app.watch-redeem-request-duration-seconds}")
    private long watchRedeemRequestDurationSeconds;

    private final OwnerRedeemRuleRepository redeemRuleRepository;
    private final OwnerCardRepository cardRepository;
    private final OwnerAuthenticatedRedeemRequestAccessor authenticatedRedeemRequestAccessor;

    private final Map<String, Future<?>> redeemRequestTtlTaskPool;

    private final OwnerRedeemService ownerRedeemService;
    private final OwnerCardService ownerCardService;
    private final CardMapper cardMapper;
    private final ObjectMapper objectMapper;
    private final JedisPooled jedis;

    @Override
    public Set<RedeemRequestDto> listRedeemRequests(UUID ownerId) {
        final String ownerRedeemRequestsKey = getOwnerRedeemRequestsKey(ownerId.toString());
        final Map<String, String> hash = jedis.hgetAll(ownerRedeemRequestsKey);
        return hash.values().stream().map(s -> {
            final RedeemRequestDto redeemRequestDto;
            try {
                redeemRequestDto = objectMapper.readValue(s, RedeemRequestDto.class);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
            return redeemRequestDto;
        }).collect(Collectors.toSet());
    }

    @Override
    @Transactional
    @PreAuthorize("authentication.name == #redeemRequestDto.ownerId.toString()")
    public void approveRedeemRequest(RedeemRequestDto redeemRequestDto) {
        final String serializedRedeemRequestDto = jedis.hget(redeemRequestDto.getOwnerRedeemRequestsKey(), redeemRequestDto.getFieldName());
        if (serializedRedeemRequestDto == null) {
            throw new IllegalArgumentException("Redeem request not Found");
        }

        final RedeemRequestDto deserializedRedeemRequestDto;
        try {
            deserializedRedeemRequestDto = objectMapper.readValue(serializedRedeemRequestDto, RedeemRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (deserializedRedeemRequestDto.getToken() == redeemRequestDto.getToken()) {
            throw new IllegalArgumentException("Stale token provided");
        }

        // Validate(Includes authenticate)
        validateApproval(deserializedRedeemRequestDto);

        final Card card = cardRepository.findById(deserializedRedeemRequestDto.getCardId()).get();
        final RedeemRule redeemRule = redeemRuleRepository.findById(deserializedRedeemRequestDto.getRedeemRuleId()).get();
        final int numStampsBefore = card.getNumCollectedStamps();
        final int numStampsAfter = numStampsBefore - redeemRule.getConsumes();
        final int numRedeemed = card.getNumRedeemed();
        final int numMaxRedeems = card.getBlueprint().getNumMaxRedeems();

        // Update Card
        final CardDto cardDto = cardMapper.toDto(card);
        cardDto.setNumCollectedStamps(numStampsAfter);
        cardDto.setNumRedeemed(numRedeemed + 1);
        if (numRedeemed + 1 == numMaxRedeems) {
            cardDto.setIsUsedOut(true);
            cardDto.setIsInactive(true);
        }
        ownerCardService.updateCardById(card.getId(), cardDto);

        // Save new Redeem
        final RedeemDto redeemDtoToSave = RedeemDto.builder()
                // TODO: What is good display name for Redeem?
                .displayName(deserializedRedeemRequestDto.getId())
                .numStampsBefore(numStampsBefore)
                .numStampsAfter(numStampsAfter)
                .redeemRuleId(deserializedRedeemRequestDto.getRedeemRuleId())
                .cardId(deserializedRedeemRequestDto.getCardId())
                .token(deserializedRedeemRequestDto.getToken())
                .build();
        ownerRedeemService.saveNewRedeem(redeemDtoToSave);

        // Delete RedeemRequestDto
        deleteRedeemRequest(deserializedRedeemRequestDto);
    }

    @Override
    @PreAuthorize("authentication.name == #redeemRequestDto.ownerId.toString()")
    public void deleteRedeemRequest(RedeemRequestDto redeemRequestDto) {
        authenticatedRedeemRequestAccessor.delete(redeemRequestDto, false);
        final Future<?> redeemRequestTtlTask = redeemRequestTtlTaskPool.remove(redeemRequestDto.getId());
        if (redeemRequestTtlTask != null) {
            redeemRequestTtlTask.cancel(true);
        }
    }

    private void validateApproval(RedeemRequestDto redeemRequestDto) {
        if (redeemRequestDto.getIsRedeemed()) {
            throw new IllegalArgumentException("Redeem request already approved");
        }

        if (redeemRequestDto.getExpMilliseconds() < Instant.now().toEpochMilli()) {
            throw new IllegalArgumentException("Redeem request already expired");
        }

        // Authenticate
        final Card card = cardRepository.findById(redeemRequestDto.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(Card.class, redeemRequestDto.getCardId()));
        final RedeemRule redeemRule = redeemRuleRepository.findById(redeemRequestDto.getRedeemRuleId())
                .orElseThrow(() -> new ResourceNotFoundException(RedeemRule.class, redeemRequestDto.getRedeemRuleId()));

        final Integer numCollectedStamps = card.getNumCollectedStamps();
        final Integer consumes = redeemRule.getConsumes();
        if (numCollectedStamps < consumes) {
            throw new IllegalArgumentException("Not enough collected stamps: " + numCollectedStamps + " collected but consuming " + consumes);
        }

        final Integer numRedeemed = card.getNumRedeemed();
        final Integer numMaxRedeems = card.getBlueprint().getNumMaxRedeems();
        if (numMaxRedeems <= numRedeemed) {
            throw new IllegalArgumentException("Exceeding max num of redeems: Up to " + numRedeemed + " redeems allowed but already " + numRedeemed + " redeemed");
        }
    }

    //private void setTtlToRedeemRequestDto(RedeemRequestDto redeemRequestDto) {
    //    final long exp = Instant.now().plusSeconds(watchRedeemRequestDurationSeconds).toEpochMilli();
    //    redeemRequestDto.setTtl(exp);
    //}
}
