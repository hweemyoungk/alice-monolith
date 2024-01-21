package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.repositories.CardRepository;
import cards.alice.monolith.common.web.mappers.CardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OwnerCardServiceImpl implements OwnerCardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public Optional<CardDto> getCardById(Long id) {
        return Optional.ofNullable(
                cardMapper.toDto(
                        cardRepository.findById(id).orElse(null)));
    }
}
