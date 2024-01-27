package cards.alice.monolith.owner.web.controllers;

import cards.alice.monolith.common.models.RedeemRequestDto;
import cards.alice.monolith.owner.services.OwnerRedeemRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("${cards.alice.owner.web.controllers.path.base}")
@RequiredArgsConstructor
public class OwnerRedeemRequestController {
    private final OwnerRedeemRequestService ownerRedeemRequestService;

    @GetMapping(path = "${cards.alice.owner.web.controllers.path.redeem-request.list}", produces = "application/json;charset=UTF-8")
    @PreAuthorize("authentication.name == #ownerId.toString()")
    public ResponseEntity<Set<RedeemRequestDto>> listRedeemRequests(@RequestParam UUID ownerId) {
        final Set<RedeemRequestDto> redeemRuleDtos = ownerRedeemRequestService.listRedeemRequests(ownerId);
        return ResponseEntity.ok(redeemRuleDtos);
    }

    @PostMapping(path = "${cards.alice.owner.web.controllers.path.redeem-request}/{id}/approve")
    public ResponseEntity approveRedeemRequest(@PathVariable String id) {
        RedeemRequestDto redeemRequestDto = new RedeemRequestDto(id);
        ownerRedeemRequestService.approveRedeemRequest(redeemRequestDto);
        return ResponseEntity.ok().build();
    }
}
