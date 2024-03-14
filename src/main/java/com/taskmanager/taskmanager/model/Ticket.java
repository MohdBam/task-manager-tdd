package com.taskmanager.taskmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Ticket {

    @Id
    private Long id;

    private String description;

    private Status status;

    private LocalDateTime createdDate;

    private LocalDateTime closedDate;

    private String resolutionSummary;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Ticket ticket = (Ticket) o;
        return getId() != null && Objects.equals(getId(), ticket.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
