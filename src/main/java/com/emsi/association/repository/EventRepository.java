package com.emsi.association.repository;

import com.emsi.association.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e WHERE e.startDate > CURRENT_TIMESTAMP ORDER BY e.startDate ASC")
    List<Event> findUpcomingEvents();
}
