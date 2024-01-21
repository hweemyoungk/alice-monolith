package cards.alice.monolith.customer.web.controllers;

import cards.alice.monolith.customer.services.CustomerRedeemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${cards.alice.customer.web.controllers.path.base}")
@RequiredArgsConstructor
public class CustomerRedeemController {
    private final CustomerRedeemService customerRedeemService;

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.redeem.exist}/{id}")
    public ResponseEntity<Boolean> getRedeemExists(@PathVariable String id) {
        final Boolean exists = customerRedeemService.exists(id);
        return ResponseEntity.ok(exists);
    }
}
