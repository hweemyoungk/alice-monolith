package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.models.RedeemDto;
import cards.alice.monolith.common.models.RedeemRequestNewDto;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.common.web.mappers.CardMapper;
import cards.alice.monolith.owner.repositories.OwnerCardRepository;
import cards.alice.monolith.owner.repositories.OwnerRedeemRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerRedeemRequestServiceSplitImpl implements OwnerRedeemRequestService {
    @Value("${cards.alice.redeemrequest.server.host}:${cards.alice.redeemrequest.server.port}${cards.alice.redeemrequest.web.controllers.path.base}")
    private String redeemRequestServiceUrl;
    @Value("${cards.alice.redeemrequest.web.controllers.path.owner.redeem-request}")
    private String redeemRequestPath;
    @Value("${cards.alice.redeemrequest.web.controllers.path.owner.redeem-request.list}")
    private String redeemRequestListPath;

    private final OwnerRedeemRuleRepository redeemRuleRepository;
    private final OwnerCardRepository cardRepository;

    private final OwnerRedeemService redeemService;
    private final OwnerCardService cardService;
    private final CardMapper cardMapper;

    private final RestTemplate restTemplate;

    @Override
    public Set<RedeemRequestNewDto> listRedeemRequests(UUID ownerId) {
        final String url = redeemRequestServiceUrl + redeemRequestListPath + "?ownerId={ownerId}";
        final var requestEntity = RequestEntity.get(url, ownerId).build();
        final var responseType = new ParameterizedTypeReference<Set<RedeemRequestNewDto>>() {
        };
        return restTemplate.exchange(requestEntity, responseType).getBody();
    }

    @Override
    @Transactional
    // Done by Redeem request service
    //@PreAuthorize("authentication.name == #redeemRequestDto.ownerId.toString()")
    public void approveRedeemRequest(String redeemRequestId) {
        final RedeemRequestNewDto redeemRequestDto = getRedeemRequestById(redeemRequestId).orElseThrow();

        // Validate(Includes authenticate)
        validateApproval(redeemRequestDto);

        final Card card = cardRepository.findById(redeemRequestDto.getCardId()).orElseThrow();
        final RedeemRule redeemRule = redeemRuleRepository.findById(redeemRequestDto.getRedeemRuleId()).orElseThrow();
        final int numStampsBefore = card.getNumCollectedStamps();
        final int numStampsAfter = numStampsBefore - redeemRule.getConsumes();
        final int numRedeemed = card.getNumRedeemed();
        final int numMaxRedeems = card.getBlueprint().getNumMaxRedeems();

        // Update Card
        final CardDto cardDto = cardMapper.toDto(card);
        cardDto.setNumCollectedStamps(numStampsAfter);
        cardDto.setNumRedeemed(numRedeemed + 1);
        if (numMaxRedeems <= numRedeemed + 1) {
            cardDto.setIsUsedOut(true);
            cardDto.setIsInactive(true);
        }
        cardService.updateCardById(card.getId(), cardDto);

        // Save new Redeem
        final RedeemDto redeemDtoToSave = RedeemDto.builder()
                .isDeleted(false)
                .displayName("Dummy Redeem Name")
                .redeemRequestId(redeemRequestId)
                .numStampsBefore(numStampsBefore)
                .numStampsAfter(numStampsAfter)
                .redeemRuleId(redeemRequestDto.getRedeemRuleId())
                .cardId(redeemRequestDto.getCardId())
                .build();
        redeemService.saveNewRedeem(redeemDtoToSave);

        // Delete RedeemRequestDto
        deleteRedeemRequestById(redeemRequestId);
    }

    @Override
    // Done by Redeem request service
    //@PreAuthorize("authentication.name == #redeemRequestDto.ownerId.toString()")
    public void deleteRedeemRequestById(String id) {
        final String url = redeemRequestServiceUrl + redeemRequestPath + "/{id}";
        final var requestEntity = RequestEntity.delete(url, id).build();
        restTemplate.exchange(requestEntity, Void.class);
    }

    private void validateApproval(RedeemRequestNewDto redeemRequestDto) {
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

    private Optional<RedeemRequestNewDto> getRedeemRequestById(String id) {
        final String url = redeemRequestServiceUrl + redeemRequestPath + "/{id}";
        final var requestEntity = RequestEntity.get(url, id).build();
        return Optional.ofNullable(restTemplate.exchange(requestEntity, RedeemRequestNewDto.class).getBody());
    }
}
