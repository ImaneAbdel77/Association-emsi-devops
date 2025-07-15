package com.emsi.association.repository;

import com.emsi.association.entity.Member;
import com.emsi.association.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();

    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC LIMIT 3")
    List<Post> findTop3ByOrderByCreatedAtDesc();

    Object findByAuthorOrderByCreatedAtDesc(Member member);
}
