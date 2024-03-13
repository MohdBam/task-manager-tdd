package com.taskmanager.taskmanager.dto;

import com.taskmanager.taskmanager.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public record TicketFilterDto(List<Status> statusList,
                              LocalDateTime startDate,
                              LocalDateTime endDate,
                              String assignedAgent) {

}