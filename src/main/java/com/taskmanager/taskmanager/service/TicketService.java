package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.TicketDto;
import com.taskmanager.taskmanager.dto.TicketFilterDto;

import java.util.List;

public interface TicketService {
    TicketDto createTicket(TicketDto ticketDto);

    TicketDto assignTicketToAgent(Long ticketId, Long agentId);

    TicketDto resolveTicket(Long ticketId);

    TicketDto closeTicket(Long ticketId);

    TicketDto updateTicket(Long id, TicketDto updatedDetailsDto);

    TicketDto getTicketById(Long id);

    List<TicketDto> getTickets(TicketFilterDto filterDto);
}
