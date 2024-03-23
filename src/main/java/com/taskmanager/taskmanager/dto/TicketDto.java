package com.taskmanager.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taskmanager.taskmanager.model.Status;
import lombok.Builder;

import java.time.LocalDateTime;

//TODO split readDto, createDto, and updateDto
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
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
