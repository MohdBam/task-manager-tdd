package com.taskmanager.taskmanager.dto;

import com.taskmanager.taskmanager.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public record TicketFilterDto(List<Status> status,
                              LocalDateTime startDate,
                              LocalDateTime endDate,
                              String assignedAgent) {

}
