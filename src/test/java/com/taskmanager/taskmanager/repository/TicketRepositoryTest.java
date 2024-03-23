package com.taskmanager.taskmanager.repository;

import com.taskmanager.taskmanager.model.Status;
import com.taskmanager.taskmanager.model.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@Sql("/filterTestData.sql")
public class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    public void givenNoFilters_whenGettingTickets_thenAllTicketsAreReturned() {
        // When
        List<Ticket> tickets = ticketRepository.findWithFilters(null, null, null, null);

        // Then
        Assertions.assertEquals(5, tickets.size());
    }

    @Test
    void givenStatus_whenGettingTickets_thenTicketsWithMatchingStatusAreReturned() {
        // Given
        Status status = Status.NEW;

        // When
        List<Ticket> tickets = ticketRepository.findWithFilters(List.of(status), null, null, null);

        // Then
        Assertions.assertTrue(tickets.stream().allMatch(ticket -> ticket.getStatus().equals(status)));
    }

    @Test
    void givenDateRange_whenGettingTickets_thenTicketsWithinRangeAreReturned() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        List<Ticket> ticketsCreatedWithinLastThreeDays = ticketRepository.findWithFilters(
                null, null, now.minusDays(3), now);

        Assertions.assertEquals(3, ticketsCreatedWithinLastThreeDays.size());
    }

    @Test
    void givenStartDate_whenGettingTickets_thenTicketsAfterStartDateAreReturned() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        List<Ticket> tickets = ticketRepository.findWithFilters(
                null, null, now, null);

        Assertions.assertEquals(1, tickets.size());
        Assertions.assertTrue(tickets.stream().allMatch(ticket -> ticket.getCreatedDate().isAfter(now)));
    }

    @Test
    void givenEndDate_whenGettingTickets_thenTicketsBeforeEndDateAreReturned() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        List<Ticket> tickets = ticketRepository.findWithFilters(
                null, null, null, now);

        Assertions.assertEquals(4, tickets.size());
        Assertions.assertTrue(tickets.stream().allMatch(ticket -> ticket.getCreatedDate().isBefore(now)));
    }
    @Test
    public void givenAgent_whenGettingTickets_thenTicketsWithMatchingAgentAreReturned() {
        // Given
        String agentName = "Agent 1";

        // When
        List<Ticket> tickets = ticketRepository.findWithFilters(null, agentName, null, null);

        // Then
        Assertions.assertTrue(tickets.stream().allMatch(ticket -> ticket.getAssignedAgent().getName().equals(agentName)));
    }

    @Test
    public void givenStatusAndAgent_whenGettingTickets_thenTicketsWithMatchingStatusAndAgentAreReturned() {
        // Given
        Status status = Status.RESOLVED;
        String agentName = "Agent002";

        // When
        List<Ticket> tickets = ticketRepository.findWithFilters(List.of(status), agentName, null, null);

        // Then
        Assertions.assertEquals(2, tickets.size());
        Assertions.assertTrue(tickets.stream().allMatch(ticket -> ticket.getStatus().equals(status) && ticket.getAssignedAgent().getName().equals(agentName)));
    }
}
