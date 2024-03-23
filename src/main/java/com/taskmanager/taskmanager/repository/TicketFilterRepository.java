package com.taskmanager.taskmanager.repository;

import com.taskmanager.taskmanager.model.Status;
import com.taskmanager.taskmanager.model.Ticket;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketFilterRepository {
    List<Ticket> findWithFilters(List<Status> status, String assignedAgent, LocalDateTime startDate, LocalDateTime endDate);
}
