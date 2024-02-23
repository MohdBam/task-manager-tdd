package com.taskmanager.taskmanager.dto;

import com.taskmanager.taskmanager.model.Status;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TicketDto(
        Long id,
        String description,
        Status status,
        LocalDateTime createdDate,
        LocalDateTime closedDate,
        String assignedAgent,
        String resolutionSummary
) {
}
