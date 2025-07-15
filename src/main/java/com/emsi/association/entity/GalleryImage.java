package com.emsi.association.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class GalleryImage {

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String title;
        private String description;
        private String imagePath;
        private LocalDateTime uploadedAt = LocalDateTime.now();

        @ManyToOne
        private Member uploadedBy;
}
