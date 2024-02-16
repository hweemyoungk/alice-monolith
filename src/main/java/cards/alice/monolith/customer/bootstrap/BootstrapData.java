package cards.alice.monolith.customer.bootstrap;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import cards.alice.monolith.common.repositories.CardRepository;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import cards.alice.monolith.common.repositories.StoreRepository;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Profile("bootstrap")
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
    private Long cardId;

    private final StoreRepository storeRepository;
    private final BlueprintRepository blueprintRepository;
    private final CardRepository cardRepository;
    private final RedeemRuleRepository redeemRuleRepository;
    private final EntityManager entityManager;

    @Override
    public void run(String... args) {
        // Store
        populateStore();
        // Blueprint
        populateBlueprint();
        // Card
        populateCard();
        // RedeemRule
        populateRedeemRules();
        // Modify Blueprint
        modifyBlueprint();
    }

    private void modifyBlueprint() {
        Blueprint blueprint = blueprintRepository.findById(blueprintId).orElseThrow();
        blueprint.setDescription("Modified demo blueprint 1.");
        // Works: RedeemRule#1,2,3,4 still references blueprint#1
        blueprint.setRedeemRules(null);
        Blueprint savedBlueprint = blueprintRepository.saveAndFlush(blueprint);

        // Doesn't work: RedeemRule#3,4 still references blueprint#1
        savedBlueprint.setRedeemRules(Set.of(
                entityManager.getReference(RedeemRule.class, 1), entityManager.getReference(RedeemRule.class, 2)));
        Blueprint savedBlueprint2 = blueprintRepository.saveAndFlush(savedBlueprint);
        System.out.println();
    }

    private void populateCard() {
        final Card card1 = Card.builder()
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
        final Card savedCard = cardRepository.saveAndFlush(card1);
        cardId = savedCard.getId();
        final Card card2 = Card.builder()
                .displayName("My Second Card")
                .numCollectedStamps(48)
                .numGoalStamps(8)
                .isFavorite(true)
                .numRedeemed(0)
                .customerId(customerId)
                .blueprint(entityManager.getReference(Blueprint.class, blueprintId))
                .bgImageId(null)
                .isDiscarded(true)
                .isUsedOut(false)
                .isInactive(true)
                .build();
        cardRepository.saveAndFlush(card2);
        final Card card3 = Card.builder()
                .displayName("My Third Card")
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
        cardRepository.saveAndFlush(card3);
        final Card card4 = Card.builder()
                .displayName("My Fourth Card")
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
        cardRepository.saveAndFlush(card4);
    }

    private void populateStore() {
        final Store store1 = Store.builder()
                .displayName("My Flower Shop 1")
                .description("This is demo store 1.\n" +
                        "Excepteur quis veniam consequat anim et proident exercitation enim Lorem nisi dolore. Laboris fugiat reprehenderit irure velit ut exercitation. Elit et amet ipsum aliqua et esse aliquip adipisicing cillum reprehenderit.\n" +
                        "Aliquip tempor proident elit id tempor aliquip culpa sunt deserunt laborum sit ad tempor. Id eiusmod excepteur in tempor labore esse ut magna anim pariatur et esse sit. Anim anim amet sit qui sit.")
                .zipcode("123")
                .address("Foo city")
                .phone("+818012345678")
                .lat(BigDecimal.valueOf(37.1234567890))
                .lng(BigDecimal.valueOf(137.1234567890))
                .isClosed(false)
                .isInactive(false)
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
                .isClosed(true)
                .isInactive(true)
                .bgImageId("Bx13SaZ")
                .profileImageId("ApRCkR2")
                .ownerId(ownerId)
                .build();
        storeRepository.saveAndFlush(store2);

        final Store store3 = Store.builder()
                .displayName("My Flower Shop 3")
                .description("This is demo store 3.\n" +
                        "Excepteur quis veniam consequat anim et proident exercitation enim Lorem nisi dolore. Laboris fugiat reprehenderit irure velit ut exercitation. Elit et amet ipsum aliqua et esse aliquip adipisicing cillum reprehenderit.\n" +
                        "Aliquip tempor proident elit id tempor aliquip culpa sunt deserunt laborum sit ad tempor. Id eiusmod excepteur in tempor labore esse ut magna anim pariatur et esse sit. Anim anim amet sit qui sit.")
                .zipcode("123")
                .address("Foo city")
                .phone("+818012345678")
                .lat(BigDecimal.valueOf(37.1234567890))
                .lng(BigDecimal.valueOf(137.1234567890))
                .isClosed(false)
                .isInactive(false)
                .bgImageId(null)
                .profileImageId(null)
                .ownerId(ownerId)
                .build();
        storeRepository.saveAndFlush(store3);

        final Store store4 = Store.builder()
                .displayName("My Bakery 4")
                .description("This is demo store 4.")
                .zipcode("456")
                .address("Bar state")
                .phone("+821098765432")
                .lat(BigDecimal.valueOf(-10.234))
                .lng(BigDecimal.valueOf(-20.678))
                .isClosed(false)
                .isInactive(false)
                .bgImageId("Bx13SaZ")
                .profileImageId("ApRCkR2")
                .ownerId(ownerId)
                .build();
        storeRepository.saveAndFlush(store4);
    }

    private void populateBlueprint() {
        Blueprint blueprint1 = Blueprint.builder()
                .displayName("Blueprint 1")
                .description("This is demo blueprint 1.")
                .stampGrantCondDescription("1 stamp per $1")
                .numMaxStamps(50)
                .numMaxRedeems(5)
                .numMaxIssuesPerCustomer(10)
                .numMaxIssues(0)
                .expirationDate(OffsetDateTime.now().plusMonths(1))
                .bgImageId(null)
                .isPublishing(true)
                .store(entityManager.getReference(Store.class, storeId))
                .build();

        Blueprint savedBlueprint = blueprintRepository.saveAndFlush(blueprint1);
        blueprintId = savedBlueprint.getId();

        Blueprint blueprint2 = Blueprint.builder()
                .displayName("Blueprint 2")
                .description("This is demo blueprint 2.")
                .stampGrantCondDescription("2 stamps per $2")
                .numMaxStamps(20)
                .numMaxRedeems(6)
                .numMaxIssuesPerCustomer(3)
                .numMaxIssues(100)
                .expirationDate(OffsetDateTime.now().plusMonths(2))
                .bgImageId(null)
                .isPublishing(false)
                .store(entityManager.getReference(Store.class, storeId))
                .build();
        blueprintRepository.saveAndFlush(blueprint2);

        Blueprint blueprint3 = Blueprint.builder()
                .displayName("Blueprint 3")
                .description("This is demo blueprint 3.")
                .stampGrantCondDescription("3 stamps per $3")
                .numMaxStamps(20)
                .numMaxRedeems(6)
                .numMaxIssuesPerCustomer(3)
                .numMaxIssues(100)
                .expirationDate(OffsetDateTime.now().plusMonths(2))
                .bgImageId(null)
                .isPublishing(true)
                .store(entityManager.getReference(Store.class, storeId))
                .build();
        blueprintRepository.saveAndFlush(blueprint3);
    }

    void populateRedeemRules(){
        RedeemRule redeemRule1 = RedeemRule.builder()
                .displayName("Rule 1")
                .description("This is Rule 1")
                .consumes(1)
                .imageId(null)
                .blueprint(blueprintRepository.getReferenceById(blueprintId))
                .build();
        RedeemRule redeemRule2 = RedeemRule.builder()
                .displayName("Rule 2")
                .description("This is Rule 2")
                .consumes(5)
                .imageId(null)
                .blueprint(blueprintRepository.getReferenceById(blueprintId))
                .build();
        RedeemRule redeemRule3 = RedeemRule.builder()
                .displayName("Rule 3")
                .description("This is Rule 3")
                .consumes(10)
                .imageId(null)
                .blueprint(blueprintRepository.getReferenceById(blueprintId))
                .build();
        RedeemRule redeemRule4 = RedeemRule.builder()
                .displayName("Rule 4")
                .description("Demo redeem rule 4")
                .consumes(3)
                .imageId(null)
                .blueprint(entityManager.getReference(Blueprint.class, blueprintId))
                .build();
        redeemRuleRepository.saveAllAndFlush(List.of(redeemRule1, redeemRule2, redeemRule3, redeemRule4));
    }
}
