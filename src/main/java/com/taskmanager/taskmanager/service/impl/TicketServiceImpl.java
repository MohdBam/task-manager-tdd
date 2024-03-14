package com.taskmanager.taskmanager.service.impl;

import com.taskmanager.taskmanager.dto.TicketDto;
import com.taskmanager.taskmanager.dto.TicketFilterDto;
import com.taskmanager.taskmanager.exception.MissingDescriptionException;
import com.taskmanager.taskmanager.mapper.TicketMapper;
import com.taskmanager.taskmanager.model.Status;
import com.taskmanager.taskmanager.model.Ticket;
import com.taskmanager.taskmanager.repository.TicketRepository;
import com.taskmanager.taskmanager.service.TicketService;
import com.taskmanager.taskmanager.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

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
        return null;
    }

    @Override
    public TicketDto resolveTicket(Long ticketId) {
        return null;
    }

    @Override
    public TicketDto closeTicket(Long ticketId) {
        return null;
    }

    @Override
    public TicketDto updateTicket(Long id, TicketDto updatedDetailsDto) {
        return null;
    }

    @Override
    public TicketDto getTicketById(Long id) {
        return null;
    }

    @Override
    public List<TicketDto> getTickets(TicketFilterDto filterDto) {
        return null;
    }
}
