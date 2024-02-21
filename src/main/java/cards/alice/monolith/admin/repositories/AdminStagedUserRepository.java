package cards.alice.monolith.admin.repositories;

import cards.alice.monolith.common.domain.StagedUser;
import cards.alice.monolith.common.repositories.StagedUserRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface AdminStagedUserRepository extends StagedUserRepository {
    @Query("select s from StagedUser s where s.userId = :userId and s.isDeleted = :isDeleted")
    Optional<StagedUser> findByUserIdAndIsDeleted(@Param("userId") @NonNull UUID userId, @Param("isDeleted") @NonNull Boolean isDeleted);
}
