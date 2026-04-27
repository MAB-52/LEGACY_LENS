package com.legacy_lens.repository;

import com.legacy_lens.entity.OtpVerification;
import com.legacy_lens.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    // Latest unused OTP for this user
    Optional<OtpVerification> findTopByUserAndUsedFalseOrderByCreatedAtDesc(User user);

    // Delete all OTPs for a user (cleanup after verify or resend)
    @Modifying
    @Query("DELETE FROM OtpVerification o WHERE o.user = :user")
    void deleteAllByUser(User user);
}
