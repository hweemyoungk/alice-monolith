package cards.alice.monolith.owner.web.controllers;

import cards.alice.monolith.common.models.StampGrantDto;
import cards.alice.monolith.owner.services.OwnerStampGrantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("${cards.alice.owner.web.controllers.path.base}")
@RequiredArgsConstructor
public class OwnerStampGrantController {
    @Value("${cards.alice.owner.server.host}:${cards.alice.owner.server.port}")
    private String ownerHostname;
    @Value("${cards.alice.owner.web.controllers.path.base}${cards.alice.owner.web.controllers.path.stamp-grant}")
    private String ownerStampGrantPath;

    private final OwnerStampGrantService ownerStampGrantService;

    @PostMapping(path = "${cards.alice.owner.web.controllers.path.stamp-grant}")
    public ResponseEntity postStampGrant(@RequestBody StampGrantDto stampGrantDto) {
        final StampGrantDto savedStampGrantDto = ownerStampGrantService.grantStampsToCard(stampGrantDto);
        return ResponseEntity.created(URI.create(ownerHostname + ownerStampGrantPath + "/" + savedStampGrantDto.getId())).build();
    }
}
