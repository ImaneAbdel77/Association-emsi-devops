package com.emsi.association.controller;

import com.emsi.association.entity.Member;
import com.emsi.association.entity.Post;
import com.emsi.association.repository.MemberRepository;
import com.emsi.association.repository.PostRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PostController(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public String listPosts(Model model) {
        model.addAttribute("posts", postRepository.findAllByOrderByCreatedAtDesc());
        return "posts/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        return "posts/create";
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute Post post, Authentication authentication) {
        String email = authentication.getName();
        Member author = memberRepository.findByEmail(email).orElseThrow();
        post.setAuthor(author);
        postRepository.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post non trouvé"));
        model.addAttribute("post", post);
        return "posts/view"; // ou le nom de votre template
    }

    @PostMapping("/delete/{id}")
    public String deletepost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            postRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Événement supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression de l'événement");
        }
        return "redirect:/posts";
    }
}
