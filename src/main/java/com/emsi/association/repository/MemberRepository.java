package com.emsi.association.repository;

import com.emsi.association.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    @Query("SELECT m FROM Member m WHERE " +
            "LOWER(m.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(m.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(m.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Member> searchUsers(@Param("query") String query, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.role = :role")
    long countByRole(@Param("role") String role);
}