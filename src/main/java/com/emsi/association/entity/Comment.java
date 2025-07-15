package com.emsi.association.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le commentaire ne peut pas être vide")
    private String content;

    @ManyToOne
    private Member author;

    @ManyToOne
    private Post post;

    private LocalDateTime createdAt = LocalDateTime.now();
}
