package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.TicketDto;
import com.taskmanager.taskmanager.exception.AgentNotFoundException;
import com.taskmanager.taskmanager.exception.InvalidTicketStateException;
import com.taskmanager.taskmanager.exception.MissingDescriptionException;
import com.taskmanager.taskmanager.exception.TicketNotFoundException;
import com.taskmanager.taskmanager.mapper.AgentMapper;
import com.taskmanager.taskmanager.mapper.AgentMapperImpl;
import com.taskmanager.taskmanager.mapper.TicketMapper;
import com.taskmanager.taskmanager.mapper.TicketMapperImpl;
import com.taskmanager.taskmanager.model.Agent;
import com.taskmanager.taskmanager.model.Status;
import com.taskmanager.taskmanager.model.Ticket;
import com.taskmanager.taskmanager.repository.AgentRepository;
import com.taskmanager.taskmanager.repository.TicketRepository;
import com.taskmanager.taskmanager.service.impl.TicketServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TicketServiceTest {

    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private AgentRepository agentRepository;

    private final AgentMapper agentMapper = new AgentMapperImpl();

    private final TicketMapper ticketMapper = new TicketMapperImpl(agentMapper);

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(agentMapper, "agentRepository", agentRepository);
        ticketService = new TicketServiceImpl(ticketRepository, ticketMapper, agentRepository);
    }

    @Test
    void givenTicketDetails_whenTicketIsCreated_thenCallsRepositorySave() {
        //given
        String description = "description";
        LocalDateTime now = LocalDateTime.now();
        long ticketId = 1L;
        String agentName = "Agent001";

        Agent agent = Agent.builder()
                .id(1L)
                .name(agentName)
                .build();

        TicketDto ticketDto = TicketDto.builder()
                .description(description)
                .assignedAgent(agentName)
                .build();

        when(agentRepository.findOneByName(agentName)).thenReturn(Optional.of(agent));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(
                Ticket.builder()
                        .id(ticketId)
                        .status(Status.NEW)
                        .assignedAgent(agent)
                        .description(description)
                        .createdDate(now).build()
        );

        //when
        ticketService.createTicket(ticketDto);

        //then
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void givenTicketDetails_whenTicketIsCreated_thenSetsNewStatusAndCreationDate() {
        //given
        String description = "description";
        LocalDateTime now = LocalDateTime.now();
        long ticketId = 1L;
        String agentName = "Agent001";

        Agent agent = Agent.builder()
                .id(1L)
                .name(agentName)
                .build();

        TicketDto ticketDto = TicketDto.builder()
                .description(description)
                .assignedAgent(agentName)
                .build();

        when(agentRepository.findOneByName(agentName)).thenReturn(Optional.of(agent));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(
                Ticket.builder()
                        .id(ticketId)
                        .status(Status.NEW)
                        .assignedAgent(agent)
                        .description(description)
                        .createdDate(now).build()
        );

        //when
        TicketDto createdTicket = ticketService.createTicket(ticketDto);

        //then
        Assertions.assertEquals(Status.NEW, createdTicket.status());
        Assertions.assertEquals(ticketId, createdTicket.id());
        Assertions.assertEquals(now, createdTicket.createdDate());
    }

    @Test
    void givenTicketDetailsWithoutDescription_whenTicketIsCreated_thenExceptionIsThrown() {

        LocalDateTime now = LocalDateTime.now();
        long id = 1L;

        TicketDto ticketDto = TicketDto.builder()
                .id(id)
                .createdDate(now)
                .build();

        Assertions.assertThrows(MissingDescriptionException.class, () -> ticketService.createTicket(ticketDto));
    }

    @Test
    void givenNewTicket_whenAssigningAgent_thenTicketIsInProgress() {

        Long agentId = 1L;
        Long ticketId = 1L;

        String agentName = "Agent001";
        String description = "description";

        LocalDateTime now = LocalDateTime.now();


        Agent agentByReference = Agent.builder()
                .id(agentId)
                .build();


        Agent agent = Agent.builder()
                .name(agentName)
                .id(agentId)
                .build();

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .description(description)
                .status(Status.NEW)
                .createdDate(LocalDateTime.now())
                .build();

        Ticket savedTicket = Ticket.builder()
                .id(ticketId)
                .description(description)
                .assignedAgent(agent)
                .status(Status.IN_PROGRESS)
                .createdDate(now)
                .build();

        when(agentRepository.getReferenceById(agentId)).thenReturn(agentByReference);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        TicketDto actualTicketDto = ticketService.assignTicketToAgent(ticketId, agentId);

        Assertions.assertEquals(agentName, actualTicketDto.assignedAgent());
        Assertions.assertEquals(Status.IN_PROGRESS, actualTicketDto.status());
        Assertions.assertEquals(ticketId, actualTicketDto.id());
    }

    @Test
    void givenNonExistingTicket_whenAssigningAgent_thenThrowException() {
        //given
        Long nonExistingTicketId = 999L;
        Long agentId = 1L;


        Agent agentByReference = Agent.builder()
                .id(agentId)
                .build();

        Ticket mockedTicketByReference = Mockito.mock(Ticket.class);

        when(ticketRepository.findById(nonExistingTicketId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(TicketNotFoundException.class, () -> ticketService.assignTicketToAgent(nonExistingTicketId, agentId));

    }

    @Test
    void givenNonExistingAgent_whenAssigningAgent_thenThrowException() {
        // given
        Long ticketId = 1L;
        Long nonExistingAgentId = 999L;


        Agent agentByReference = Agent.builder()
                .id(nonExistingAgentId)
                .build();

        Ticket ticketByReference = Ticket.builder()
                .id(ticketId)
                .description("description")
                .status(Status.NEW)
                .build();

        //when
        when(agentRepository.getReferenceById(nonExistingAgentId)).thenReturn(agentByReference);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticketByReference));

        when(ticketRepository.save(any(Ticket.class))).thenThrow(new EntityNotFoundException());

        //then
        Assertions.assertThrows(AgentNotFoundException.class, () -> ticketService.assignTicketToAgent(ticketId, nonExistingAgentId));

    }

    @Test
    void givenTicketNotInNewState_whenAssigningAgent_thenThrowException() {
        //given
        Long ticketId = 1L;
        Long agentId = 1L;
        String description = "description";
        LocalDateTime now = LocalDateTime.now();

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .description(description)
                .status(Status.IN_PROGRESS)
                .createdDate(now)
                .build();

        //when
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        //then
        Assertions.assertThrows(InvalidTicketStateException.class,
                () -> ticketService.assignTicketToAgent(ticketId, agentId));
    }

}
