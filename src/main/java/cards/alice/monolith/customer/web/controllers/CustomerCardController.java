package cards.alice.monolith.customer.web.controllers;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.customer.services.CustomerCardService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("${cards.alice.customer.web.controllers.path.base}")
@RequiredArgsConstructor
public class CustomerCardController {
    @Value("${cards.alice.customer.server.host}:${cards.alice.customer.server.port}")
    private String customerHostname;
    @Value("${cards.alice.customer.web.controllers.path.base}/${cards.alice.customer.web.controllers.path.card}")
    private String customerCardPath;

    private final CustomerCardService customerCardService;


    @PostMapping(path = "${cards.alice.customer.web.controllers.path.card}")
    public ResponseEntity postCard(@RequestBody CardDto cardDto) {
        final CardDto savedCardDto = customerCardService.saveNewCard(cardDto);
        return ResponseEntity.created(URI.create(customerHostname + customerCardPath + "/" + savedCardDto.getId())).build();
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

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.card.list}")
    public ResponseEntity<Set<CardDto>> listCards(@RequestParam UUID customerId, @RequestParam List<Long> ids) {
        if (customerId == null && CollectionUtils.isEmpty(ids)) {
            return ResponseEntity.badRequest().build();
        }
        final Set<CardDto> cardDtos = customerCardService.listCards(customerId, new HashSet<>(ids));
        return ResponseEntity.ok(cardDtos);
    }

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.card.num-issues}")
    public ResponseEntity<Long> getNumIssues(@NotNull @RequestParam UUID customerId, @NotNull @RequestParam Long blueprintId) {
        final Long numIssues = customerCardService.getNumIssues(customerId, blueprintId);
        return ResponseEntity.ok(numIssues);
    }
}
