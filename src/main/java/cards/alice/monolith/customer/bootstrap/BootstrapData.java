package cards.alice.monolith.customer.bootstrap;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import cards.alice.monolith.common.repositories.CardRepository;
import cards.alice.monolith.common.repositories.StoreRepository;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Component
public class BootstrapData implements CommandLineRunner {
    @Value("${cards.alice.customer.user-id}")
    private UUID customerId;
    @Value("${cards.alice.owner.user-id}")
    private UUID ownerId;

    private Long storeId;
    private Long blueprintId;

    private final StoreRepository storeRepository;
    private final BlueprintRepository blueprintRepository;
    private final CardRepository cardRepository;
    private final EntityManager entityManager;

    @Override
    public void run(String... args) throws Exception {
        // Store
        populateStore();
        // Blueprint
        populateBlueprint();
        // Card
        populateCard();
    }

    private void populateCard() {
        final Card myFirstCard = Card.builder()
                .displayName("My First Card")
                .numCollectedStamps(48)
                .numGoalStamps(8)
                .isFavorite(true)
                .numRedeemed(0)
                .customerId(customerId)
                .blueprint(entityManager.getReference(Blueprint.class, blueprintId))
                .bgImageId(null)
                .isDiscarded(false)
                .isUsedOut(false)
                .isInactive(false)
                .build();
        cardRepository.saveAndFlush(myFirstCard);
    }

    private void populateStore() {
        final Store store1 = Store.builder()
                .displayName("My Flower Shop 1")
                .description("This is demo store 1.")
                .zipcode("123")
                .address("Foo city")
                .phone("+818012345678")
                .lat(BigDecimal.valueOf(37.1234567890))
                .lng(BigDecimal.valueOf(137.1234567890))
                .bgImageId(null)
                .profileImageId(null)
                .ownerId(ownerId)
                .build();
        final Store savedStore1 = storeRepository.saveAndFlush(store1);
        storeId = savedStore1.getId();

        final Store store2 = Store.builder()
                .displayName("My Bakery 2")
                .description("This is demo store 2.")
                .zipcode("456")
                .address("Bar state")
                .phone("+821098765432")
                .lat(BigDecimal.valueOf(-10.234))
                .lng(BigDecimal.valueOf(-20.678))
                .bgImageId("Bx13SaZ")
                .profileImageId("ApRCkR2")
                .ownerId(ownerId)
                .build();
        storeRepository.saveAndFlush(store2);
    }

    private void populateBlueprint() {
        Blueprint blueprint1 = Blueprint.builder()
                .displayName("Blueprint 1")
                .description("This is demo blueprint 1.")
                .stampGrantCondDescription("1 stamp per $1")
                .numMaxStamps(50)
                .numMaxRedeems(5)
                .numMaxIssues(10)
                .expirationDate(OffsetDateTime.now().plusMonths(1))
                .bgImageId(null)
                .isPublishing(true)
                .store(entityManager.getReference(Store.class, storeId))
                .build();
        RedeemRule redeemRule1 = RedeemRule.builder()
                .displayName("Rule 1")
                .description("This is Rule 1")
                .consumes(1)
                .imageId(null)
                .blueprint(blueprint1)
                .build();
        RedeemRule redeemRule2 = RedeemRule.builder()
                .displayName("Rule 2")
                .description("This is Rule 2")
                .consumes(5)
                .imageId(null)
                .blueprint(blueprint1)
                .build();
        RedeemRule redeemRule3 = RedeemRule.builder()
                .displayName("Rule 3")
                .description("This is Rule 3")
                .consumes(10)
                .imageId(null)
                .blueprint(blueprint1)
                .build();
        blueprint1.setRedeemRules(Set.of(redeemRule1, redeemRule2, redeemRule3));
        Blueprint savedBlueprint = blueprintRepository.saveAndFlush(blueprint1);
        blueprintId = savedBlueprint.getId();

        Blueprint blueprint2 = Blueprint.builder()
                .displayName("Blueprint 2")
                .description("This is demo blueprint 2.")
                .stampGrantCondDescription("2 stamps per $2")
                .numMaxStamps(20)
                .numMaxRedeems(6)
                .numMaxIssues(4)
                .expirationDate(OffsetDateTime.now().plusMonths(2))
                .bgImageId(null)
                .isPublishing(false)
                .store(entityManager.getReference(Store.class, storeId))
                .build();
        blueprintRepository.saveAndFlush(blueprint2);
    }
}
