package cards.alice.monolith.customer.web.controllers;

import cards.alice.monolith.common.models.RedeemRequestDto;
import cards.alice.monolith.customer.services.CustomerRedeemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${cards.alice.customer.web.controllers.path.base}")
@RequiredArgsConstructor
public class CustomerRedeemController {
    private final CustomerRedeemService customerRedeemService;

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.redeem.exist}")
    public ResponseEntity<Boolean> getRedeemExists(@RequestParam String redeemRequestId) {
        final RedeemRequestDto redeemRequestDto = new RedeemRequestDto(redeemRequestId);
        final Boolean exists = customerRedeemService.exists(redeemRequestDto);
        return ResponseEntity.ok(exists);
    }
}
