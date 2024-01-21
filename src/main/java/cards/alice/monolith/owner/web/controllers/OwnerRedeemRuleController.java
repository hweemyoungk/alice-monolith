package cards.alice.monolith.owner.web.controllers;

import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.owner.services.OwnerRedeemRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("${cards.alice.owner.web.controllers.path.base}")
@RequiredArgsConstructor
public class OwnerRedeemRuleController {
    private final OwnerRedeemRuleService ownerRedeemRuleService;

    @GetMapping(path = "${cards.alice.owner.web.controllers.path.redeem-rule.list}")
    public ResponseEntity<Set<RedeemRuleDto>> listRedeemRules(@RequestParam Long blueprintId) {
        if (blueprintId == null) {
            return ResponseEntity.badRequest().build();
        }
        final Set<RedeemRuleDto> redeemRuleDtos = ownerRedeemRuleService.listRedeemRules(blueprintId);
        return ResponseEntity.ok(redeemRuleDtos);
    }
}
