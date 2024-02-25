package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.TicketDto;

public interface TicketService {
    TicketDto createTicket(TicketDto ticketDto);

    TicketDto assignTicketToAgent(Long ticketId, Long agentId);

    TicketDto resolveTicket(Long ticketId);

    TicketDto closeTicket(Long ticketId);

    TicketDto updateTicket(Long id, TicketDto updatedDetailsDto);
}
