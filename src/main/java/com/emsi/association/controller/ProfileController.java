package com.emsi.association.controller;

import com.emsi.association.entity.Member;
import com.emsi.association.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class ProfileController {

    private final MemberRepository memberRepository;
    private static final String UPLOAD_DIR = "src/main/resources/static/images/profiles/";

    public ProfileController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/Profile")
    public String profile(Authentication authentication, Model model) {
        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        model.addAttribute("member", member);
        return "profile";
    }

    @PostMapping("/Profile/update")
    @Transactional
    public String updateProfile(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) MultipartFile profileImage,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Member member = memberRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

            // Vérifier si l'email est déjà utilisé par un autre membre
            if (!member.getEmail().equals(email) && memberRepository.findByEmail(email).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Cet email est déjà utilisé");
                return "redirect:/Profile";
            }

            member.setFirstName(firstName);
            member.setLastName(lastName);
            member.setEmail(email);

            // Gestion de l'image de profil
            if (profileImage != null && !profileImage.isEmpty()) {
                // Créer le répertoire s'il n'existe pas
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Générer un nom de fichier unique
                String fileName = System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);

                // Sauvegarder le fichier en remplaçant s'il existe déjà
                Files.copy(profileImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour le chemin de l'image dans le membre
                member.setProfileImage("/images/profiles/" + fileName);
            }

            memberRepository.save(member);
            redirectAttributes.addFlashAttribute("success", "Profil mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la mise à jour");
            e.printStackTrace(); // Pour le débogage
        }

        return "redirect:/Profile";
    }
}