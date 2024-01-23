package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.models.StampGrantDto;

public interface OwnerStampGrantService {
    StampGrantDto saveNewStampGrant(StampGrantDto stampGrantDto);
}
