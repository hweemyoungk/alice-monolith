package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Query(value = """
            select count(0) from (
            select 0 from card as c
            where c.customer_id = :customerId
            and c.is_deleted = :isDeleted
            and c.is_inactive = :isInactive
            for update)""",
            nativeQuery = true)
    long exclusiveCountByCustomerIdAndIsDeletedAndIsInactive(@Param("customerId") @NonNull UUID customerId, @Param("isDeleted") @NonNull Boolean isDeleted, @Param("isInactive") @NonNull Boolean isInactive);

    @Query(value = """
            select count(0) from (
            select 0 from card as c
            where c.customer_id = :customerId
            and c.is_deleted = :isDeleted
            for update)""",
            nativeQuery = true)
    long exclusiveCountByCustomerIdAndIsDeleted(@Param("customerId") @NonNull UUID customerId, @Param("isDeleted") @NonNull Boolean isDeleted);

    @Query(value = """
            select count(0) from (
            select 0 from card as c
            where c.customer_id = :customerId
            for update)""",
            nativeQuery = true)
    long exclusiveCountByCustomerId(@Param("customerId") @NonNull UUID customerId);
}