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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Value("${cards.alice.customer.web.controllers.path.base}${cards.alice.customer.web.controllers.path.card}")
    private String customerCardPath;

    private final CustomerCardService customerCardService;

    // Tested
    @PostMapping(path = "${cards.alice.customer.web.controllers.path.card}")
    @PreAuthorize("authentication.name == #cardDto.customerId.toString()")
    public ResponseEntity postCard(@RequestBody CardDto cardDto) {
        final CardDto savedCardDto = customerCardService.saveNewCard(cardDto);
        return ResponseEntity.created(URI.create(customerHostname + customerCardPath + "/" + savedCardDto.getId())).build();
    }

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.card}/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<CardDto> getCard(@PathVariable Long id) {
        final Optional<CardDto> cardDto = customerCardService.getCardById(id);
        return ResponseEntity.ok(cardDto.orElseThrow(() -> new ResourceNotFoundException(Card.class, id)));
    }

    @PutMapping(path = "${cards.alice.customer.web.controllers.path.card}/{id}")
    @PreAuthorize("authentication.name == #cardDto.customerId.toString()")
    public ResponseEntity putCard(@PathVariable Long id, @Validated @RequestBody CardDto cardDto) {
        Optional<CardDto> updatedCardDto = customerCardService.updateCardById(id, cardDto);
        updatedCardDto.orElseThrow(() -> new ResourceNotFoundException(Card.class, id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "${cards.alice.customer.web.controllers.path.card}/{id}")
    public ResponseEntity discardCard(@PathVariable Long id) {
        final Optional<CardDto> cardDto = customerCardService.discardCardById(id);
        cardDto.orElseThrow(() -> new ResourceNotFoundException(Card.class, id));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.card.list}", produces = "application/json;charset=UTF-8")
    @PreAuthorize("#customerId != null ? authentication.name == #customerId.toString() : true")
    public ResponseEntity<Set<CardDto>> listCards(@RequestParam(required = false) UUID customerId, @RequestParam(required = false) List<Long> ids) {
        if (customerId == null && CollectionUtils.isEmpty(ids)) {
            return ResponseEntity.badRequest().build();
        }
        final Set<CardDto> cardDtos = customerCardService.listCards(customerId, ids == null ? null : new HashSet<>(ids));
        return ResponseEntity.ok(cardDtos);
    }

    // Tested
    @GetMapping(path = "${cards.alice.customer.web.controllers.path.card.num-issues}")
    @PreAuthorize("authentication.name == #customerId.toString()")
    public ResponseEntity<Long> getNumIssues(@NotNull @RequestParam UUID customerId, @NotNull @RequestParam Long blueprintId) {
        final Long numIssues = customerCardService.getNumIssues(customerId, blueprintId);
        return ResponseEntity.ok(numIssues);
    }
}
