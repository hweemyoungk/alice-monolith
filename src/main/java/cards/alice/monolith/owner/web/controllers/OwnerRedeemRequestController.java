package cards.alice.monolith.owner.web.controllers;

import cards.alice.monolith.common.models.RedeemRequestDto;
import cards.alice.monolith.owner.services.OwnerRedeemRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("${cards.alice.owner.web.controllers.path.base}")
@RequiredArgsConstructor
public class OwnerRedeemRequestController {
    private final OwnerRedeemRequestService ownerRedeemRequestService;

    @GetMapping(path = "${cards.alice.owner.web.controllers.path.redeem-request.list}")
    @PreAuthorize("authentication.name == #ownerId.toString()")
    public ResponseEntity<Set<RedeemRequestDto>> listRedeemRequests(@RequestParam UUID ownerId) {
        final Set<RedeemRequestDto> redeemRuleDtos = ownerRedeemRequestService.listRedeemRequests(ownerId);
        return ResponseEntity.ok(redeemRuleDtos);
    }
}
