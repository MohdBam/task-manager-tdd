package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.TicketDto;
import com.taskmanager.taskmanager.dto.TicketFilterDto;

import java.util.List;

public interface TicketService {

    /**
     * Create a new ticket
     * @param ticketDto The ticket details.
     * @return The created TicketDto.
     * @throws MissingDescriptionException if the description is missing.
     */
    TicketDto createTicket(TicketDto ticketDto);

    /**
     * Assign ticket to agent
     * @param ticketId id of the ticket to be assigned.
     * @param agentId id of the agent to be assigned to.
     * @return The updated TicketDto with the assigned agent.
     * @throws TicketNotFoundException if the ticket is not found.
     * @throws AgentNotFoundException if the agent is not found.
     * @throws IllegalTicketStateException if the ticket was not new.
     */
    TicketDto assignTicketToAgent(Long ticketId, Long agentId);

    /**
     * Resolve ticket
     * @param ticketId id of the ticket to be resolved.
     * @return The updated TicketDto marked as resolved.
     * @throws TicketNotFoundException if the ticket is not found.
     * @throws IllegalTicketStateException if the ticket was not in progress.
     */
    TicketDto resolveTicket(Long ticketId);

    /**
     * Close ticket
     * @param ticketId id of the ticket to be closed.
     * @return The updated TicketDto marked as closed.
     * @throws TicketNotFoundException if the ticket is not found.
     * @throws IllegalTicketStateException if the ticket was not resolved already.
     * @throws MissingResolutionSummaryException if the resolution summary is missing.
     */
    TicketDto closeTicket(Long ticketId);

    /**
     * Update ticket
     * @param id id of the ticket to be updated.
     * @param updatedDetailsDto The updated ticket details.
     * @return The updated TicketDto.
     * @throws TicketNotFoundException if the ticket is not found.
     * @throws InvalidTicketStateException if the ticket was closed already.
     */
    TicketDto updateTicket(Long id, TicketDto updatedDetailsDto);
    
    /**
     * Get ticket by id
     * @param id id of the ticket to be retrieved.
     * @return The TicketDto.
     * @throws TicketNotFoundException if the ticket is not found.
     */
    TicketDto getTicketById(Long id);

    List<TicketDto> getTickets(TicketFilterDto filterDto);
}
