package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import cards.alice.monolith.common.repositories.CardRepository;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import cards.alice.monolith.common.repositories.StoreRepository;
import cards.alice.monolith.common.web.mappers.CardMapper;
import cards.alice.monolith.customer.bootstrap.BootstrapData;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"default", "dev", "common", "h2", "monolith", "oauth2_https"})
class CustomerCardServiceImplTest {
    @Value("${cards.alice.customer.user-id}")
    private UUID customerId;
    @Value("${cards.alice.owner.user-id}")
    private UUID ownerId;

    @Autowired
    StoreRepository storeRepository;
    @Autowired
    BlueprintRepository blueprintRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    RedeemRuleRepository redeemRuleRepository;
    @Autowired
    EntityManager entityManager;
    @Autowired
    CardMapper cardMapper;

    @Autowired
    BootstrapData bootstrapData;
    @Autowired
    CustomerCardServiceImpl customerCardService;

    @Test
    @Transactional
    @Rollback
    @WithMockUser(
            username = "de36b13b-2397-445e-89cd-8e817e0f441e",
            roles = {"customer"})
    void updateCardByIdCannotControlNumCollectedStamps() {
        final CardDto cardDto = cardMapper.toDto(cardRepository.findById(bootstrapData.getCardId()).orElseThrow());
        final int numCollectedStampsBefore = cardDto.getNumCollectedStamps();
        cardDto.setNumCollectedStamps(cardDto.getNumCollectedStamps() + 1);
        final CardDto updatedCardDto = customerCardService.updateCardById(cardDto.getId(), cardDto).orElseThrow();
        assertThat(cardRepository.findById(updatedCardDto.getId()).orElseThrow().getNumCollectedStamps())
                .isEqualTo(numCollectedStampsBefore);
    }
}