package com.emsi.association.controller;

import com.emsi.association.entity.Event;
import com.emsi.association.entity.Member;
import com.emsi.association.repository.EventRepository;
import com.emsi.association.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/events")
public class EventController {
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    public EventController(EventRepository eventRepository, MemberRepository memberRepository) {
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public String listEvents(Model model) {
        model.addAttribute("events", eventRepository.findAll());
        return "events/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        return "events/create";
    }

    @PostMapping("/create")
    public String createEvent(@ModelAttribute Event event, Authentication authentication) {
        String email = authentication.getName();
        Member organizer = memberRepository.findByEmail(email).orElseThrow();
        event.setOrganizer(organizer);

        if (event.getEndDate() == null) {
            event.setEndDate(event.getStartDate().plusHours(2));
        }

        eventRepository.save(event);
        return "redirect:/events";
    }
    @PostMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Événement supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression de l'événement");
        }
        return "redirect:/events";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Événement non trouvé"));
        model.addAttribute("event", event);
        return "events/create";
    }

    @PostMapping("/update/{id}")
    public String updateEvent(@PathVariable Long id, @ModelAttribute Event event,
                              RedirectAttributes redirectAttributes) {
        try {
            Event existingEvent = eventRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Événement non trouvé"));

            existingEvent.setTitle(event.getTitle());
            existingEvent.setDescription(event.getDescription());
            existingEvent.setLocation(event.getLocation());
            existingEvent.setStartDate(event.getStartDate());
            existingEvent.setEndDate(event.getEndDate());

            eventRepository.save(existingEvent);
            redirectAttributes.addFlashAttribute("success", "Événement mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour de l'événement");
        }
        return "redirect:/events";
    }
}
