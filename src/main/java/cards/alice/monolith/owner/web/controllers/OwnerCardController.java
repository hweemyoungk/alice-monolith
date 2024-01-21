package cards.alice.monolith.owner.web.controllers;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.services.OwnerCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("${cards.alice.customer.web.controllers.path.base}")
@RequiredArgsConstructor
public class OwnerCardController {
    private final OwnerCardService ownerCardService;

    @GetMapping(path = "${cards.alice.owner.web.controllers.path.card}/{id}")
    public ResponseEntity<CardDto> getCard(@PathVariable Long id) {
        final Optional<CardDto> cardDto = ownerCardService.getCardById(id);
        return ResponseEntity.ok(cardDto.orElseThrow(() -> new ResourceNotFoundException(Card.class, id)));
    }
}
