package com.emsi.association.controller;

import com.emsi.association.entity.Comment;
import com.emsi.association.entity.Member;
import com.emsi.association.entity.Post;
import com.emsi.association.repository.CommentRepository;
import com.emsi.association.repository.MemberRepository;
import com.emsi.association.repository.PostRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public CommentController(CommentRepository commentRepository,
                             PostRepository postRepository,
                             MemberRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    @PostMapping("/create")
    public String createComment(@RequestParam String content,
                                @RequestParam Long postId,
                                Authentication authentication) {
        // Création du commentaire
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(postRepository.findById(postId).orElseThrow());
        comment.setAuthor(memberRepository.findByEmail(authentication.getName()).orElseThrow());

        commentRepository.save(comment);

        return "redirect:/posts/" + postId;
    }

    @GetMapping("/delete/{id}")
    public String deleteComment(@PathVariable Long id, Authentication authentication) {
        Comment comment = commentRepository.findById(id).orElseThrow();

        // Vérification que l'utilisateur est l'auteur
        if (comment.getAuthor().getEmail().equals(authentication.getName())) {
            Long postId = comment.getPost().getId();
            commentRepository.delete(comment);
            return "redirect:/posts/" + postId;
        }

        return "redirect:/posts/" + comment.getPost().getId() + "?error=unauthorized";
    }
}
