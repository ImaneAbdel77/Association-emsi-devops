package com.emsi.association.repository;

import com.emsi.association.entity.GalleryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GalleryRepository extends JpaRepository<GalleryImage, Long> {
    @Query("SELECT g FROM GalleryImage g ")


    // Trouver les images les plus récentes (pour le dashboard)
    List<GalleryImage> findTop5ByOrderByUploadedAtDesc();

    // Statistiques mensuelles
    @Query("SELECT COUNT(g), MONTH(g.uploadedAt) FROM GalleryImage g WHERE YEAR(g.uploadedAt) = :year GROUP BY MONTH(g.uploadedAt)")
    List<Object[]> countImagesByMonth(@Param("year") int year);
}
