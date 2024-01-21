package cards.alice.monolith.customer.web.controllers;

import cards.alice.monolith.common.models.RedeemRequestDto;
import cards.alice.monolith.customer.services.CustomerRedeemRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("${cards.alice.customer.web.controllers.path.base}")
@RequiredArgsConstructor
public class CustomerRedeemRequestController {
    @Value("${cards.alice.customer.server.host}:${cards.alice.customer.server.port}")
    private String customerHostname;
    @Value("${cards.alice.customer.web.controllers.path.base}/${cards.alice.customer.web.controllers.path.redeem-request}")
    private String customerRedeemRequestPath;

    private final CustomerRedeemRequestService customerRedeemRequestService;
    @PostMapping(path = "${cards.alice.customer.web.controllers.path.redeem-request}")
    public ResponseEntity postRedeemRequest(@RequestBody RedeemRequestDto redeemRequestDto) {
        final RedeemRequestDto savedRedeemRequestDto = customerRedeemRequestService.saveNewRedeemRequest(redeemRequestDto);
        return ResponseEntity.created(URI.create(customerHostname + customerRedeemRequestPath + "/" + savedRedeemRequestDto.getId())).build();
    }

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.redeem-request.exist}/{id}")
    public ResponseEntity<Boolean> getRedeemRequestExists(@PathVariable String id) {
        final Boolean exists = customerRedeemRequestService.exists(id);
        return ResponseEntity.ok(exists);
    }
}
