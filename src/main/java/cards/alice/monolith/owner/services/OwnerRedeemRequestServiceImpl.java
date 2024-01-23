package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.models.RedeemRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static cards.alice.monolith.common.models.RedeemRequestDto.getOwnerRedeemRequestsKey;


@Service
@RequiredArgsConstructor
public class OwnerRedeemRequestServiceImpl implements OwnerRedeemRequestService {
    @Value("${cards.alice.customer.app.watch-redeem-request-duration-seconds}")
    private long watchRedeemRequestDurationSeconds;

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

    //private void setTtlToRedeemRequestDto(RedeemRequestDto redeemRequestDto) {
    //    final long exp = Instant.now().plusSeconds(watchRedeemRequestDurationSeconds).toEpochMilli();
    //    redeemRequestDto.setTtl(exp);
    //}
}
