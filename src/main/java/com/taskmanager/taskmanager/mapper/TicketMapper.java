package com.taskmanager.taskmanager.mapper;

import com.taskmanager.taskmanager.dto.TicketDto;
import com.taskmanager.taskmanager.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.WARN, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TicketMapper {

    Ticket fromDto(TicketDto ticketDto);

    @Mapping(target = "assignedAgent", ignore = true) //todo add it to Ticket
    TicketDto toDto(Ticket savedTicket);
}
