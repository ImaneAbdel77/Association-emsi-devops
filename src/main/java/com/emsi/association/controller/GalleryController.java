package com.emsi.association.controller;

import com.emsi.association.entity.GalleryImage;
import com.emsi.association.entity.Member;
import com.emsi.association.repository.GalleryRepository;
import com.emsi.association.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/gallery")
public class GalleryController {
    private final GalleryRepository galleryRepository;
    private final MemberRepository memberRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/uploads/";

    public GalleryController(GalleryRepository galleryRepository, MemberRepository memberRepository) {
        this.galleryRepository = galleryRepository;
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public String listImages(Model model) {
        model.addAttribute("images", galleryRepository.findAll());
        return "gallery/list";
    }
    @GetMapping("/create")
    public String showCreateForm() {
        return "gallery/create";
    }
    @PostMapping("/upload")
    public String uploadImage(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("image") MultipartFile file,
            Authentication authentication) throws IOException {

        if (!file.isEmpty()) {
            String email = authentication.getName();
            Member uploadedBy = memberRepository.findByEmail(email).orElseThrow();

            // Créer le répertoire s'il n'existe pas
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            // Générer un nom de fichier unique
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            // Sauvegarder le fichier
            Files.write(filePath, file.getBytes());

            // Sauvegarder en base de données
            GalleryImage image = new GalleryImage();
            image.setTitle(title);
            image.setDescription(description);
            image.setImagePath("/images/uploads/" + fileName);
            image.setUploadedBy(uploadedBy);
            image.setUploadedAt(LocalDateTime.now());

            galleryRepository.save(image);
        }

        return "redirect:/gallery";
    }
    @PostMapping("/delete/{id}")
    public String deleteImage(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            GalleryImage image = galleryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Image non trouvée"));

            // Supprimer le fichier physique
            Path filePath = Paths.get(UPLOAD_DIR + image.getImagePath().replace("/images/uploads/", ""));
            Files.deleteIfExists(filePath);

            // Supprimer de la base de données
            galleryRepository.delete(image);
            redirectAttributes.addFlashAttribute("success", "Image supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression de l'image");
        }

        return "redirect:/gallery";
    }
}
