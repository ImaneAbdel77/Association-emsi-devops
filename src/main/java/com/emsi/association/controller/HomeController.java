package com.emsi.association.controller;

import com.emsi.association.repository.EventRepository;
import com.emsi.association.repository.GalleryRepository;
import com.emsi.association.repository.MemberRepository;
import com.emsi.association.repository.PostRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final PostRepository postRepository;
    private final EventRepository eventRepository;
    private final GalleryRepository galleryRepository;
    private final MemberRepository memberRepository;

    public HomeController(PostRepository postRepository, EventRepository eventRepository , MemberRepository memberRepository , GalleryRepository galleryRepository) {
        this.postRepository = postRepository;
        this.eventRepository = eventRepository;
        this.galleryRepository = galleryRepository ;
        this.memberRepository = memberRepository ;
    }

    @GetMapping("/index")
    public String home(Model model) {
        model.addAttribute("posts", postRepository.findTop3ByOrderByCreatedAtDesc());
        model.addAttribute("events", eventRepository.findUpcomingEvents());
        return "index";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Récupération des counts
        long postsCount = postRepository.count();
        long eventsCount = eventRepository.count();
        long imagesCount = galleryRepository.count();
        long usersCount = memberRepository.countByRole("MEMBER");

        // Ajout des attributs au modèle
        model.addAttribute("postsCount", postsCount);
        model.addAttribute("eventsCount", eventsCount);
        model.addAttribute("imagesCount", imagesCount);
        model.addAttribute("usersCount", usersCount);

        return "dashboard";
    }

    @GetMapping("/")
    public String presentation() {
        return "presentation";
    }
}
