package com.taskmanager.taskmanager.dto;

import com.taskmanager.taskmanager.model.Status;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TicketFilterDto(List<Status> status,
                              LocalDateTime startDate,
                              LocalDateTime endDate,
                              String assignedAgent) {

}
