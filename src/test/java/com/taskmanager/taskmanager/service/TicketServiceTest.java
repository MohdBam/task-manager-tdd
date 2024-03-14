package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.TicketDto;
import com.taskmanager.taskmanager.exception.MissingDescriptionException;
import com.taskmanager.taskmanager.mapper.TicketMapper;
import com.taskmanager.taskmanager.mapper.TicketMapperImpl;
import com.taskmanager.taskmanager.model.Status;
import com.taskmanager.taskmanager.model.Ticket;
import com.taskmanager.taskmanager.repository.TicketRepository;
import com.taskmanager.taskmanager.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @BeforeEach
    void setup() {
        TicketMapperImpl ticketMapper = new TicketMapperImpl();
        ticketService = new TicketServiceImpl(ticketRepository, ticketMapper);
    }

    @Test
    void givenTicketDetails_whenTicketIsCreated_thenCallsRepositorySave() {
        TicketDto ticketDto = TicketDto.builder()
                .description("description")
                .build();

        when(ticketRepository.save(any(Ticket.class))).thenReturn(new Ticket());


        ticketService.createTicket(ticketDto);

        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }
    @Test
    void givenTicketDetails_whenTicketIsCreated_thenSetsNewStatusAndCreationDate() {

        String description = "description";
        LocalDateTime now = LocalDateTime.now();
        long id = 1L;

        TicketDto ticketDto = TicketDto.builder()
                .description(description)
                .build();

        when(ticketRepository.save(any(Ticket.class))).thenReturn(
                Ticket.builder()
                        .id(id)
                        .status(Status.NEW)
                        .description(description)
                        .createdDate(now).build()
        );


        TicketDto createdTicket = ticketService.createTicket(ticketDto);

        Assertions.assertEquals(Status.NEW, createdTicket.status());
        Assertions.assertEquals(id, createdTicket.id());
        Assertions.assertEquals(now, createdTicket.createdDate());
    }

    @Test
    void givenTicketDetailsWithoutDescription_whenTicketIsCreated_thenExceptionIsThrown() {

        LocalDateTime now = LocalDateTime.now();
        long id = 1L;

        TicketDto ticketDto = TicketDto.builder()
                .build();

        Assertions.assertThrows(MissingDescriptionException.class, () -> ticketService.createTicket(ticketDto));

    }

}
