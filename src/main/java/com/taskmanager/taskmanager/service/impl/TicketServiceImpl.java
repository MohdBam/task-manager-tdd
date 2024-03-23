package com.taskmanager.taskmanager.service.impl;

import com.taskmanager.taskmanager.dto.TicketDto;
import com.taskmanager.taskmanager.dto.TicketFilterDto;
import com.taskmanager.taskmanager.exception.*;
import com.taskmanager.taskmanager.mapper.TicketMapper;
import com.taskmanager.taskmanager.model.Agent;
import com.taskmanager.taskmanager.model.Status;
import com.taskmanager.taskmanager.model.Ticket;
import com.taskmanager.taskmanager.repository.AgentRepository;
import com.taskmanager.taskmanager.repository.TicketRepository;
import com.taskmanager.taskmanager.service.TicketService;
import com.taskmanager.taskmanager.util.Constants;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    private final AgentRepository agentRepository;

    @Override
    public TicketDto createTicket(TicketDto ticketDto) {
        if (!StringUtils.hasText(ticketDto.description())) {
            throw new MissingDescriptionException(Constants.MISSING_DESCRIPTION_EXCEPTION);
        }
        Ticket ticket = ticketMapper.fromDto(ticketDto);
        ticket.setStatus(Status.NEW);
        ticket.setCreatedDate(LocalDateTime.now());
        Ticket savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(savedTicket);
    }

    @Override
    public TicketDto assignTicketToAgent(Long ticketId, Long agentId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(Constants.TICKET_NOT_FOUND));

        if (ticket.getStatus() != Status.NEW) {
            throw new InvalidTicketStateException(Constants.ONLY_NEW_TICKETS_CAN_BE_ASSIGNED_TO_AN_AGENT);
        }

        Agent agentReference = agentRepository.getReferenceById(agentId);

        ticket.setAssignedAgent(agentReference);
        ticket.setStatus(Status.IN_PROGRESS);

        Ticket updatedTicket;

        //TODO find a better way to handle the entity not found exception when using getReferenceById() especially when multiple entities are used
        try {
            updatedTicket = ticketRepository.save(ticket);
        } catch (EntityNotFoundException e) {
            log.debug("Agent not found", e);
            throw new AgentNotFoundException(Constants.AGENT_NOT_FOUND);
        }

        return ticketMapper.toDto(updatedTicket);
    }

    @Override
    public TicketDto resolveTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(Constants.TICKET_NOT_FOUND));

        if (ticket.getStatus() != Status.IN_PROGRESS) {
            throw new InvalidTicketStateException(Constants.ONLY_IN_PROGRESS_TICKETS_CAN_BE_RESOLVED);
        }

        ticket.setStatus(Status.RESOLVED);

        Ticket savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(savedTicket);
    }

    @Override
    public TicketDto closeTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(Constants.TICKET_NOT_FOUND));

        Status ticketStatus = ticket.getStatus();

        if (ticketStatus != Status.RESOLVED) {
            throw new InvalidTicketStateException(Constants.ONLY_RESOLVED_TICKETS_CAN_BE_CLOSED);
        }

        if (!StringUtils.hasText(ticket.getResolutionSummary())) {
            throw new MissingResolutionSummaryException(Constants.MISSING_RESOLUTION_SUMMARY_EXCEPTION);
        }


        ticket.setStatus(Status.CLOSED);
        ticket.setClosedDate(LocalDateTime.now());

        Ticket savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(savedTicket);
    }

    @Override
    public TicketDto updateTicket(Long id, TicketDto updatedDetailsDto) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(Constants.TICKET_NOT_FOUND));

        if (ticket.getStatus() == Status.CLOSED) {
            throw new InvalidTicketStateException(Constants.CLOSED_TICKETS_CANNOT_BE_UPDATED);
        }

        ticket.setDescription(updatedDetailsDto.description());
        ticket.setResolutionSummary(updatedDetailsDto.resolutionSummary());

        Ticket savedTicket = ticketRepository.save(ticket);

        return ticketMapper.toDto(savedTicket);
    }

    @Override
    public TicketDto getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(Constants.TICKET_NOT_FOUND));

        return ticketMapper.toDto(ticket);
    }

    @Override
    public List<TicketDto> getTickets(TicketFilterDto filterDto) {
        return null;
    }
}
