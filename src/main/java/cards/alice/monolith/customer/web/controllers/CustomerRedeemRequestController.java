package cards.alice.monolith.customer.web.controllers;

import cards.alice.monolith.common.models.RedeemRequestDto;
import cards.alice.monolith.customer.services.CustomerRedeemRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("${cards.alice.customer.web.controllers.path.base}")
@RequiredArgsConstructor
public class CustomerRedeemRequestController {
    @Value("${cards.alice.customer.server.host}:${cards.alice.customer.server.port}")
    private String customerHostname;
    @Value("${cards.alice.customer.web.controllers.path.base}${cards.alice.customer.web.controllers.path.redeem-request}")
    private String customerRedeemRequestPath;

    private final CustomerRedeemRequestService customerRedeemRequestService;

    @PostMapping(path = "${cards.alice.customer.web.controllers.path.redeem-request}")
    @PreAuthorize("authentication.name == #redeemRequestDto.customerId.toString()")
    public ResponseEntity postRedeemRequest(@RequestBody RedeemRequestDto redeemRequestDto) {
        RedeemRequestDto savedRedeemRequest = customerRedeemRequestService.handlePostRedeemRequest(redeemRequestDto);
        return ResponseEntity.created(URI.create(customerHostname + customerRedeemRequestPath + "/" + savedRedeemRequest.getId())).build();
    }

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.redeem-request.exist}")
    public ResponseEntity<Boolean> getRedeemRequestExists(@RequestParam String id) {
        final boolean exists = customerRedeemRequestService.exists(new RedeemRequestDto(id));
        return ResponseEntity.ok(exists);
    }

    @DeleteMapping(path = "${cards.alice.customer.web.controllers.path.redeem-request}/{id}")
    public ResponseEntity deleteRedeemRequest(@PathVariable String id) {
        customerRedeemRequestService.deleteRedeemRequest(new RedeemRequestDto(id));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
