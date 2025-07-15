package com.emsi.association.controller;

import com.emsi.association.entity.Member;
import com.emsi.association.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("member", new Member());
        return "auth/register";
    }

    @PostMapping
    public String registerMember(@ModelAttribute Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setRegisteredAt(LocalDateTime.now());
        memberRepository.save(member);
        return "redirect:/login?registered";
    }
}
