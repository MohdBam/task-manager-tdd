package com.taskmanager.taskmanager.mapper;

import com.taskmanager.taskmanager.dto.TicketDto;
import com.taskmanager.taskmanager.mapper.config.MapstructConfig;
import com.taskmanager.taskmanager.model.Ticket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = MapstructConfig.class,
        uses = AgentMapper.class)
public interface TicketMapper {

    Ticket fromDto(TicketDto ticketDto);

    TicketDto toDto(Ticket savedTicket);
}
