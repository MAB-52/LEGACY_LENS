package com.legacy_lens.repository;

import com.legacy_lens.entity.User;
import com.legacy_lens.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            SELECT u FROM User u
            WHERE u.role = :role
              AND u.deletedAt IS NULL
              AND (:search IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<User> findAllByRoleAndNotDeleted(
            @Param("role") Role role,
            @Param("search") String search,
            Pageable pageable
    );

    // CSV Export
    @Query("""
            SELECT u FROM User u
            WHERE u.role = :role
              AND u.deletedAt IS NULL
              AND (:search IS NULL
                   OR LOWER(u.name)  LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY u.createdAt DESC
            """)
    List<User> findAllByRoleAndNotDeletedUnpaged(
            @Param("role")   Role   role,
            @Param("search") String search
    );

    long countByRoleAndDeletedAtIsNull(Role role);
}
