package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.config.AuthenticatedEntityAccessor;
import cards.alice.monolith.common.models.RedeemRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPooled;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OwnerAuthenticatedRedeemRequestAccessor implements AuthenticatedEntityAccessor<RedeemRequestDto, String> {
    private final JedisPooled jedis;
    private final ObjectMapper objectMapper;

    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().ownerId.toString()")
    public Optional<RedeemRequestDto> findById(String id) {
        final RedeemRequestDto redeemRequestDto = new RedeemRequestDto(id);
        final String serializedRedeemRequestDto = jedis.hget(redeemRequestDto.getOwnerRedeemRequestsKey(), redeemRequestDto.getFieldName());
        if (serializedRedeemRequestDto == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(serializedRedeemRequestDto, RedeemRequestDto.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("authentication.name == #redeemRequestDto.ownerId.toString()")
    public void delete(RedeemRequestDto redeemRequestDto, boolean tokenSensitive) {
        if (tokenSensitive) {
            final Optional<RedeemRequestDto> deserializedRedeemRequestDto = findById(redeemRequestDto.getId());
            if (deserializedRedeemRequestDto.isEmpty()) {
                return;
            }
            if (redeemRequestDto.getToken()
                    .compareTo(deserializedRedeemRequestDto.get().getToken()) == 0) {
                return;
            }
        }
        jedis.hdel(redeemRequestDto.getOwnerRedeemRequestsKey(), redeemRequestDto.getFieldName());
    }
}
