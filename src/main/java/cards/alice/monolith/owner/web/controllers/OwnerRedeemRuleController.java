package cards.alice.monolith.owner.web.controllers;

import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.owner.services.OwnerRedeemRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("${cards.alice.owner.web.controllers.path.base}")
@RequiredArgsConstructor
public class OwnerRedeemRuleController {
    private final OwnerRedeemRuleService ownerRedeemRuleService;

    @GetMapping(path = "${cards.alice.owner.web.controllers.path.redeem-rule.list}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Set<RedeemRuleDto>> listRedeemRules(
            @RequestParam(required = false) Long blueprintId,
            @RequestParam(required = false) List<Long> ids) {
        if (blueprintId == null && CollectionUtils.isEmpty(ids)) {
            return ResponseEntity.badRequest().build();
        }
        final Set<RedeemRuleDto> redeemRuleDtos = ownerRedeemRuleService.listRedeemRules(
                blueprintId, ids == null ? null : new HashSet<>(ids));
        return ResponseEntity.ok(redeemRuleDtos);
    }
}
