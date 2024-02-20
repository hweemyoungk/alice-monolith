package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.RedeemRequestNewDto;
import cards.alice.monolith.customer.models.processors.CustomerRedeemRequestDtoProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class CustomerRedeemRequestServiceSplitImpl implements CustomerRedeemRequestService {
    @Value("${cards.alice.redeemrequest.server.host}:${cards.alice.redeemrequest.server.port}${cards.alice.redeemrequest.web.controllers.path.base}")
    private String redeemRequestServiceUrl;
    @Value("${cards.alice.redeemrequest.web.controllers.path.customer.redeem-request}")
    private String redeemRequestPath;
    @Value("${cards.alice.redeemrequest.web.controllers.path.customer.redeem-request.list}")
    private String redeemRequestListPath;
    @Value("${cards.alice.customer.app.watch-redeem-request-duration-seconds}")
    private long watchRedeemRequestDurationSeconds;

    private final CustomerRedeemRequestDtoProcessor redeemRequestDtoProcessor;

    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public RedeemRequestNewDto handlePostRedeemRequest(RedeemRequestNewDto redeemRequestDtoFromCustomer) {
        RedeemRequestNewDto preprocessedForPost = redeemRequestDtoProcessor
                .preprocessForPostSingle(redeemRequestDtoFromCustomer);

        // Query Redis
        final var savedRedeemRequestDto = new AtomicReference<RedeemRequestNewDto>();
        final Optional<RedeemRequestNewDto> oldRedeemRequestDto = getActiveRedeemRequestByCardIdAndRedeemRuleId(
                preprocessedForPost.getCardId(), preprocessedForPost.getRedeemRuleId());
        oldRedeemRequestDto.ifPresentOrElse(targetRedeemRequestDto -> {
            // refresh TTL
            targetRedeemRequestDto.setTtlSeconds(watchRedeemRequestDurationSeconds);
            targetRedeemRequestDto.setExpMillisecondsFromNow(1_000 * watchRedeemRequestDurationSeconds);
            putRedeemRequest(targetRedeemRequestDto.getId(), targetRedeemRequestDto);
            savedRedeemRequestDto.set(targetRedeemRequestDto);
        }, () -> {
            // Create brand new
            final String newId = postRedeemRequest(preprocessedForPost);
            preprocessedForPost.setId(newId);
            savedRedeemRequestDto.set(preprocessedForPost);
        });

        return savedRedeemRequestDto.get();
    }

    private Optional<RedeemRequestNewDto> getActiveRedeemRequestByCardIdAndRedeemRuleId(Long cardId, Long redeemRuleId) {
        final String url = redeemRequestServiceUrl + redeemRequestListPath +
                "?isRedeemed=false&cardId={cardId}&redeemRuleId={redeemRuleId}";
        final var requestEntity = RequestEntity.get(url, cardId, redeemRuleId).build();
        final var responseType = new ParameterizedTypeReference<Set<RedeemRequestNewDto>>() {
        };
        final Set<RedeemRequestNewDto> redeemRequestDtos = restTemplate.exchange(requestEntity, responseType).getBody();

        if (redeemRequestDtos == null) {
            return Optional.empty();
        }

        final var iterator = redeemRequestDtos.iterator();
        if (!iterator.hasNext()) {
            return Optional.empty();
        }
        RedeemRequestNewDto redeemRequestDto = iterator.next();
        if (iterator.hasNext()) {
            throw new IllegalStateException("Multiple active requests found with the same card ID and redeem rule ID. Please wait for expiration.");
        }
        return Optional.of(redeemRequestDto);
    }

    @Override
    public boolean exists(String id) {
        final String url = redeemRequestServiceUrl + redeemRequestPath + "/{id}/exists";
        final var requestEntity = RequestEntity.get(url, id).build();
        return Boolean.TRUE.equals(restTemplate.exchange(requestEntity, Boolean.class).getBody());
    }

    @Override
    public void deleteRedeemRequest(String id) {
        final String url = redeemRequestServiceUrl + redeemRequestPath + "/{id}";
        final var requestEntity = RequestEntity.delete(url, id).build();
        restTemplate.exchange(requestEntity, Void.class);
    }

    private String postRedeemRequest(RedeemRequestNewDto redeemRequestDto) {
        final String url = redeemRequestServiceUrl + redeemRequestPath;
        final var requestEntity = RequestEntity
                .post(url)
                .body(redeemRequestDto);
        final ResponseEntity<Void> response = restTemplate.exchange(requestEntity, Void.class);
        final String[] pathFrags = Objects.requireNonNull(response.getHeaders().getLocation())
                .getPath().split("/");
        return pathFrags[pathFrags.length - 1];
    }

    private void putRedeemRequest(String id, RedeemRequestNewDto redeemRequestDto) {
        final String url = redeemRequestServiceUrl + redeemRequestPath + "/{id}";
        final var requestEntity = RequestEntity
                .put(url, id)
                .body(redeemRequestDto);
        restTemplate.exchange(requestEntity, Void.class);
    }
}
