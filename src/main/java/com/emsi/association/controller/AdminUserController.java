package com.emsi.association.controller;

import com.emsi.association.entity.Member;
import com.emsi.association.repository.MemberRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final MemberRepository memberRepository;

    public AdminUserController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String query,
            Model model) {

        Page<Member> membersPage = query != null ?
                memberRepository.searchUsers(query, PageRequest.of(page, 10)) :
                memberRepository.findAll(PageRequest.of(page, 10));

        model.addAttribute("totalUsers", memberRepository.count());
        model.addAttribute("adminCount", memberRepository.countByRole("ADMIN"));
        model.addAttribute("moderatorCount", memberRepository.countByRole("MODERATOR"));
        model.addAttribute("memberCount", memberRepository.countByRole("MEMBER"));

        model.addAttribute("members", membersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", membersPage.getTotalPages());
        model.addAttribute("searchQuery", query);

        return "admin/users";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("member", new Member());
        return "admin/user-form";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute @Valid Member member, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/user-form";
        }

        try {
            if (member.getId() != null) {
                Member existing = memberRepository.findById(member.getId()).orElseThrow();

                // Conserver l'ancien mot de passe si champ vide
                if (member.getPassword() == null || member.getPassword().isBlank()) {
                    member.setPassword(existing.getPassword());
                }
            }

            memberRepository.save(member);
            redirectAttributes.addFlashAttribute("success", "Utilisateur enregistré avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement : " + e.getMessage());
            return "redirect:/admin/users";
        }

        return "redirect:/admin/users";
    }


    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        model.addAttribute("member", memberRepository.findById(id).orElseThrow());

        return "admin/user-form";
    }

//    @GetMapping("/delete/{id}")
//    public String deleteUser(@PathVariable Long id) {
//        memberRepository.deleteById(id);
//        return "redirect:/admin/users";
//    }
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            memberRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Événement supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression de l'événement");
        }
        return "redirect:/admin/users";
    }
}