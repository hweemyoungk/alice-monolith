package cards.alice.monolith.customer.web.controllers;

import cards.alice.monolith.common.domain.Redeem;
import cards.alice.monolith.customer.services.CustomerRedeemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("${cards.alice.customer.web.controllers.path.base}")
@RequiredArgsConstructor
public class CustomerRedeemController {
    private final CustomerRedeemService customerRedeemService;

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.redeem.exist}")
    public ResponseEntity<Boolean> getRedeemExists(@RequestParam String redeemRequestId) {
        final Optional<Redeem> redeem = customerRedeemService.existByRedeemRequestId(redeemRequestId);
        return ResponseEntity.ok(redeem.isPresent());
    }
}
