package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.StampGrant;
import cards.alice.monolith.common.models.StampGrantDto;
import cards.alice.monolith.common.repositories.StampGrantRepository;
import cards.alice.monolith.common.web.mappers.StampGrantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerStampGrantServiceImpl implements OwnerStampGrantService {
    private final StampGrantRepository stampGrantRepository;
    private final StampGrantMapper stampGrantMapper;
    private final AuthenticatedCardAccessor authenticatedCardAccessor;

    @Override
    public StampGrantDto saveNewStampGrant(StampGrantDto stampGrantDto) {
        // Authenticate
        authenticatedCardAccessor.authenticatedGetById(stampGrantDto.getCardId());

        final StampGrant stampGrant = stampGrantMapper.toEntity(stampGrantDto);
        stampGrant.setId(null);
        stampGrant.setVersion(null);
        return stampGrantMapper.toDto(
                stampGrantRepository.save(stampGrant));
    }
}
