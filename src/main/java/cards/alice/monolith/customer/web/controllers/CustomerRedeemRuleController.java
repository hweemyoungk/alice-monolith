package cards.alice.monolith.customer.web.controllers;

import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.customer.services.CustomerRedeemRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("${cards.alice.customer.web.controllers.path.base}")
@RequiredArgsConstructor
public class CustomerRedeemRuleController {
    private final CustomerRedeemRuleService customerRedeemRuleService;

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.redeem-rule.list}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Set<RedeemRuleDto>> listRedeemRules(@RequestParam Long blueprintId) {
        if (blueprintId == null) {
            return ResponseEntity.badRequest().build();
        }
        final Set<RedeemRuleDto> redeemRuleDtos = customerRedeemRuleService.listRedeemRules(blueprintId);
        return ResponseEntity.ok(redeemRuleDtos);
    }
}
