package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.TicketDto;
import com.taskmanager.taskmanager.dto.TicketFilterDto;
import com.taskmanager.taskmanager.exception.*;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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

    @Test
    void givenTicketInProgress_whenResolving_thenStatusIsResolved() {
        Long ticketId = 1L;
        String description = "description";
        LocalDateTime now = LocalDateTime.now();

        Agent agent = Agent.builder()
                .id(1L)
                .name("Agent001")
                .build();

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .description(description)
                .status(Status.IN_PROGRESS)
                .createdDate(now)
                .assignedAgent(agent)
                .build();

        Ticket savedTicket = Ticket.builder()
                .id(ticketId)
                .description(description)
                .status(Status.RESOLVED)
                .createdDate(now)
                .assignedAgent(agent)
                .build();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.ofNullable(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        TicketDto actualTicketDto = ticketService.resolveTicket(ticketId);

        Assertions.assertEquals(Status.RESOLVED, actualTicketDto.status());
    }

    @Test
    void givenNonExistingTicket_whenResolving_thenThrowException() {
        Long nonExistingTicketId = 999L;

        when(ticketRepository.findById(nonExistingTicketId)).thenReturn(Optional.empty());

        Assertions.assertThrows(TicketNotFoundException.class, () -> ticketService.resolveTicket(nonExistingTicketId));
    }

    @Test
    void givenTicketNotInProgress_whenResolving_thenThrowException() {
        Long ticketId = 1L;
        String description = "description";
        LocalDateTime now = LocalDateTime.now();

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .description(description)
                .status(Status.NEW)
                .createdDate(now)
                .build();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        RuntimeException ex = Assertions.assertThrows(InvalidTicketStateException.class, () -> ticketService.resolveTicket(ticketId));
        Assertions.assertEquals("Only IN_PROGRESS tickets can be resolved.", ex.getMessage());
    }

    @Test
    void givenTicketIsResolvedWithSummary_whenClosingTicket_thenStatusIsClosed() {
        Long ticketId = 1L;
        String description = "description";
        String resolutionSummary = "summary";
        LocalDateTime beforeTwoDays = LocalDateTime.now().minusDays(2);
        LocalDateTime now = LocalDateTime.now();

        Agent agent = Agent.builder()
                .id(1L)
                .name("Agent001")
                .build();

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .description(description)
                .status(Status.RESOLVED)
                .resolutionSummary(resolutionSummary)
                .createdDate(beforeTwoDays)
                .assignedAgent(agent)
                .build();

        Ticket savedTicket = Ticket.builder()
                .id(ticketId)
                .description(description)
                .status(Status.CLOSED)
                .resolutionSummary(resolutionSummary)
                .createdDate(beforeTwoDays)
                .closedDate(now)
                .assignedAgent(agent)
                .build();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.ofNullable(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        TicketDto actualTicketDto = ticketService.closeTicket(ticketId);

        Assertions.assertEquals(Status.CLOSED, actualTicketDto.status());
        Assertions.assertEquals(now, actualTicketDto.closedDate());
    }

    @Test
    void givenNonExistingTicket_whenClosingTicket_thenThrowException() {
        Long nonExistingTicketId = 999L;

        when(ticketRepository.findById(nonExistingTicketId)).thenReturn(Optional.empty());

        Assertions.assertThrows(TicketNotFoundException.class, () -> ticketService.closeTicket(nonExistingTicketId));
    }

    @Test
    void givenTicketNotResolved_whenClosingTicket_thenThrowException() {
        Long ticketId = 1L;
        String description = "description";
        LocalDateTime now = LocalDateTime.now();

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .description(description)
                .status(Status.IN_PROGRESS)
                .createdDate(now)
                .build();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.ofNullable(ticket));

        RuntimeException ex = Assertions.assertThrows(InvalidTicketStateException.class, () -> ticketService.closeTicket(ticketId));
        Assertions.assertEquals("Only RESOLVED tickets can be closed!", ex.getMessage());
    }

    @Test
    void givenResolvedTicketWithoutSummary_whenClosingTicket_thenThrowException() {
        Long ticketId = 1L;
        String description = "description";
        LocalDateTime beforeTwoDays = LocalDateTime.now().minusDays(2);

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .description(description)
                .status(Status.RESOLVED)
                .createdDate(beforeTwoDays)
                .build();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.ofNullable(ticket));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> ticketService.closeTicket(ticketId));
        Assertions.assertEquals("Resolution summary is missing!", ex.getMessage());
    }

    @Test
    void givenTicketDescriptionAndResolutionSummary_whenUpdatingTicket_thenDescriptionAndResolutionSummaryAreUpdated() {
        Long ticketId = 1L;
        String description = "description";
        String resolutionSummary = "summary";
        String updatedDescription = "updated description";
        String updatedResolutionSummary = "updated summary";
        LocalDateTime beforeTwoDays = LocalDateTime.now().minusDays(2);
        LocalDateTime now = LocalDateTime.now();

        Agent agent = Agent.builder()
                .id(1L)
                .name("Agent001")
                .build();

        TicketDto ticketDto = TicketDto.builder()
                .id(ticketId)
                .description(description)
                .status(Status.RESOLVED)
                .resolutionSummary(resolutionSummary)
                .createdDate(beforeTwoDays)
                .build();

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .description(description)
                .status(Status.RESOLVED)
                .resolutionSummary(resolutionSummary)
                .createdDate(beforeTwoDays)
                .assignedAgent(agent)
                .build();

        Ticket savedTicket = Ticket.builder()
                .id(ticketId)
                .description(updatedDescription)
                .status(Status.RESOLVED)
                .resolutionSummary(updatedResolutionSummary)
                .createdDate(beforeTwoDays)
                .assignedAgent(agent)
                .build();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.ofNullable(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        TicketDto actualTicketDto = ticketService.updateTicket(ticketId, ticketDto);

        Assertions.assertEquals(updatedDescription, actualTicketDto.description());
        Assertions.assertEquals(updatedResolutionSummary, actualTicketDto.resolutionSummary());
    }

    @Test
    void givenNonExistingTicket_whenUpdatingTicket_thenThrowException() {
        Long nonExistingTicketId = 999L;

        when(ticketRepository.findById(nonExistingTicketId)).thenReturn(Optional.empty());

        Assertions.assertThrows(TicketNotFoundException.class, () -> ticketService.updateTicket(nonExistingTicketId, TicketDto.builder().build()));
    }

    @Test
    void givenTicketIsClosed_whenUpdatingTicket_thenThrowException() {
        Long closedTicketId = 2L;

        Long ticketId = 1L;
        String description = "description";
        String resolutionSummary = "summary";
        LocalDateTime beforeTwoDays = LocalDateTime.now().minusDays(2);

        Agent agent = Agent.builder()
                .id(1L)
                .name("Agent001")
                .build();

        TicketDto ticketDto = TicketDto.builder()
                .id(ticketId)
                .description(description)
                .status(Status.CLOSED)
                .resolutionSummary(resolutionSummary)
                .createdDate(beforeTwoDays)
                .build();


        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .description(description)
                .status(Status.CLOSED)
                .resolutionSummary(resolutionSummary)
                .createdDate(beforeTwoDays)
                .assignedAgent(agent)
                .build();

        when(ticketRepository.findById(closedTicketId)).thenReturn(Optional.of(ticket));

        Assertions.assertThrows(InvalidTicketStateException.class,
                () -> ticketService.updateTicket(closedTicketId, ticketDto)
        );
    }

    @Test
    void givenExistingTicketId_whenGettingTicketById_thenCorrectTicketIsReturned() {
        //given
        Long ticketId = 1L;
        Agent agent = Agent.builder()
                .id(1L)
                .name("Agent001")
                .build();

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .description("description")
                .status(Status.NEW)
                .createdDate(LocalDateTime.now())
                .assignedAgent(agent)
                .build();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        //when
        TicketDto actualTicketDto = ticketService.getTicketById(ticketId);

        //then
        verify(ticketRepository, times(1)).findById(ticketId);
        Assertions.assertEquals(ticketMapper.toDto(ticket), actualTicketDto);
    }

    @Test
    void givenNonExistingTicketId_whenGettingTicketById_thenThrowException() {
        //given
        Long nonExistingTicketId = 999L;

        when(ticketRepository.findById(nonExistingTicketId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(TicketNotFoundException.class, () -> ticketService.getTicketById(nonExistingTicketId));
    }

    @Test
    void givenFilterCriteria_whenGettingTickets_thenReturnFilteredTickets() {
        //given
        String agentName = "Agent001";

        TicketFilterDto ticketFilterDto = TicketFilterDto.builder()
                .status(Collections.singletonList(Status.NEW))
                .assignedAgent(agentName)
                .build();


        List<Ticket> filteredTickets = List.of(
                Ticket.builder()
                        .id(1L)
                        .description("description")
                        .status(Status.NEW)
                        .createdDate(LocalDateTime.now())
                        .build(),
                Ticket.builder()
                        .id(2L)
                        .description("description")
                        .status(Status.NEW)
                        .createdDate(LocalDateTime.now())
                        .build()
        );

        when(ticketRepository.findWithFilters(
                ticketFilterDto.status(),
                ticketFilterDto.assignedAgent(),
                ticketFilterDto.startDate(),
                ticketFilterDto.endDate()
        )).thenReturn(filteredTickets);

        List<TicketDto> expectedTicketDtos = filteredTickets
                .stream().map(ticketMapper::toDto)
                .toList();


        //when
        List<TicketDto> actualTicketDtos = ticketService.getTickets(ticketFilterDto);

        //then
        verify(ticketRepository, times(1)).findWithFilters(
                ticketFilterDto.status(),
                ticketFilterDto.assignedAgent(),
                ticketFilterDto.startDate(),
                ticketFilterDto.endDate()
        );
        Assertions.assertEquals(expectedTicketDtos, actualTicketDtos);
    }

    @Test
    void givenInvalidDateRange_whenGettingTickets_thenReturnFilteredTickets() {
        //given
        String agentName = "Agent001";

        TicketFilterDto ticketFilterDto = TicketFilterDto.builder()
                .status(Collections.singletonList(Status.NEW))
                .assignedAgent(agentName)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().minusDays(1))
                .build();;



        //when
        List<TicketDto> actualTicketDtos = ticketService.getTickets(ticketFilterDto);

        //then

        Assertions.assertThrows(InvalidDateRangeException.class, () -> ticketService.getTickets(ticketFilterDto));

    }

}
