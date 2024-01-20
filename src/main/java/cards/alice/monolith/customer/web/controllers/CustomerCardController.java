package cards.alice.monolith.customer.web.controllers;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.customer.services.CustomerCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("${cards.alice.customer.web.controllers.path.base}")
@RequiredArgsConstructor
public class CustomerCardController {
    private final CustomerCardService customerCardService;


    @PostMapping(path = "${cards.alice.customer.web.controllers.path.card}")
    public ResponseEntity postCard(@RequestBody CardDto cardDto) {
        final CardDto savedCardDto = customerCardService.saveNewCard(cardDto);
        return ResponseEntity.created(URI.create("http://localhost:8080/api/v1/card/" + savedCardDto.getId())).build();
    }

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.card}/{id}")
    public ResponseEntity<CardDto> getCard(@PathVariable Long id) {
        final Optional<CardDto> cardDto = customerCardService.getCardById(id);
        return ResponseEntity.ok(cardDto.orElseThrow(() -> new ResourceNotFoundException(Card.class, id)));
    }

    @PutMapping(path = "${cards.alice.customer.web.controllers.path.card}/{id}")
    public ResponseEntity putCard(@PathVariable Long id, @Validated @RequestBody CardDto cardDto) {
        Optional<CardDto> updatedCardDto = customerCardService.updateCardById(id, cardDto);
        updatedCardDto.orElseThrow(() -> new ResourceNotFoundException(Card.class, id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "${cards.alice.customer.web.controllers.path.card}/{id}")
    public ResponseEntity softDeleteCard(@PathVariable Long id) {
        final Optional<CardDto> cardDto = customerCardService.softDeleteCardById(id);
        cardDto.orElseThrow(() -> new ResourceNotFoundException(Card.class, id));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
