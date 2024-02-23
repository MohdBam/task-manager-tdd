package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.TicketDto;
import org.springframework.stereotype.Service;

public interface TicketService {
    TicketDto createTicket(TicketDto any);

    TicketDto assignTicketToAgent(Long ticketId, Long agentId);

    TicketDto resolveTicket(Long ticketId);

    TicketDto closeTicket(Long ticketId);
}
