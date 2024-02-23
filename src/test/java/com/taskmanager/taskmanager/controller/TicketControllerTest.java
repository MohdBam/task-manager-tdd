package com.taskmanager.taskmanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.taskmanager.taskmanager.dto.TicketDto;
import com.taskmanager.taskmanager.exception.AgentNotFoundException;
import com.taskmanager.taskmanager.exception.InvalidTicketStateException;
import com.taskmanager.taskmanager.exception.MissingResolutionSummaryException;
import com.taskmanager.taskmanager.exception.TicketNotFoundException;
import com.taskmanager.taskmanager.model.Status;
import com.taskmanager.taskmanager.service.TicketService;
import com.taskmanager.taskmanager.util.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TicketController.class)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketService ticketService;

    @Test
    void givenTicketDetails_whenTicketIsCreated_thenTicketIsSaved() throws Exception {
        // given
        String ticketDescription = "Sample ticket description";
        TicketDto ticketDto = TicketDto.builder()
                        .description(ticketDescription)
                                .status(Status.NEW)
                                        .build();

        //when
        when(ticketService.createTicket(any(TicketDto.class))).thenReturn(ticketDto);

        //then
        mockMvc.perform(post("/tickets")
                    .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value(ticketDescription))
                .andExpect(jsonPath("$.status").value(Status.NEW.name()));
    }

    @Test
    void givenNewTicket_whenAssigningAgent_thenStatusIsInProgress() throws Exception {
        //given
        Long ticketId = 1L;
        Long agentId = 1L;
        String agentName = "Agent001";
        String ticketDescription = "Description";
        TicketDto ticketDto = TicketDto.builder()
                .id(ticketId)
                .description(ticketDescription)
                .status(Status.IN_PROGRESS)
                .assignedAgent(agentName)
                .build();

        //when
        when(ticketService.assignTicketToAgent(ticketId, agentId)).thenReturn(ticketDto);

        //then
        mockMvc.perform(put("/tickets/{id}/agent/{agentId}", ticketId, agentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.IN_PROGRESS.name()))
                .andExpect(jsonPath("$.assignedAgent").value(agentName));
    }

    @Test
    void givenTicketIsNotInNewState_whenAssigningAgent_thenThrowException() throws Exception {
        //given
        Long ticketId = 1L;
        Long agentId = 1L;

        //when
        when(ticketService.assignTicketToAgent(ticketId, agentId)).thenThrow(new InvalidTicketStateException(Constants.ONLY_NEW_TICKETS_CAN_BE_ASSIGNED_TO_AN_AGENT));

        //then
        mockMvc.perform(put("/tickets/{id}/agent/{agentId}", ticketId, agentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Constants.ONLY_NEW_TICKETS_CAN_BE_ASSIGNED_TO_AN_AGENT));
    }

    @Test
    void givenAgentIsNotFound_whenAssigningAgent_thenThrowException() throws Exception {
        //given
        Long ticketId = 1L;
        Long agentId = 999L;

        //when
        when(ticketService.assignTicketToAgent(ticketId, agentId)).thenThrow(new AgentNotFoundException(Constants.AGENT_NOT_FOUND));

        //then
        mockMvc.perform(put("/tickets/{id}/agent/{agentId}", ticketId, agentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Constants.AGENT_NOT_FOUND));
    }

    @Test
    void givenTicketIsNotFound_whenAssigningAgent_thenThrowException() throws Exception {
        //given
        Long ticketId = 999L;
        Long agentId = 1L;

        //when
        when(ticketService.assignTicketToAgent(ticketId, agentId)).thenThrow(new TicketNotFoundException(Constants.TICKET_NOT_FOUND));

        //then
        mockMvc.perform(put("/tickets/{id}/agent/{agentId}", ticketId, agentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Constants.TICKET_NOT_FOUND));
    }

    @Test
    void givenTicketInProgress_whenResolved_thenStatusIsResolved() throws Exception {
        //given
        Long ticketId = 1L;
        String agentName = "Agent001";
        String ticketDescription = "Description";
        TicketDto ticketDto = TicketDto.builder()
                .id(ticketId)
                .description(ticketDescription)
                .status(Status.RESOLVED)
                .assignedAgent(agentName)
                .build();

        //when
        when(ticketService.resolveTicket(ticketId)).thenReturn(ticketDto);

        //then
        mockMvc.perform(put("/tickets/{id}/resolve", ticketId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.RESOLVED.name()));
    }

    @Test
    void givenResolvedTicketWithSummary_whenClosing_thenStatusIsClosed() throws Exception {
        //given
        Long ticketId = 1L;
        String agentName = "Agent001";
        String ticketDescription = "Description";
        TicketDto ticketDto = TicketDto.builder()
                .id(ticketId)
                .description(ticketDescription)
                .status(Status.CLOSED)
                .assignedAgent(agentName)
                .build();

        //when
        when(ticketService.closeTicket(ticketId)).thenReturn(ticketDto);

        //then
        mockMvc.perform(put("/tickets/{id}/close", ticketId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.CLOSED.name()));
    }

    @Test
    void givenResolvedTicketWithoutSummary_whenClosing_thenThrowException() throws Exception {
        // given
        Long ticketId = 1L;
        String agentName = "Agent001";
        TicketDto ticketDto = TicketDto.builder()
                .status(Status.RESOLVED)
                .assignedAgent(agentName)
                .build();

        // when
        when(ticketService.closeTicket(ticketId)).thenThrow(new MissingResolutionSummaryException(Constants.MISSING_RESOLUTION_SUMMARY_EXCEPTION));

        // then
        mockMvc.perform(put("/tickets/{id}/close", ticketId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Constants.MISSING_RESOLUTION_SUMMARY_EXCEPTION));
    }
}
