package com.taskmanager.taskmanager.mapper;

import com.taskmanager.taskmanager.dto.AgentDto;
import com.taskmanager.taskmanager.exception.AgentNotFoundException;
import com.taskmanager.taskmanager.mapper.config.MapstructConfig;
import com.taskmanager.taskmanager.model.Agent;
import com.taskmanager.taskmanager.repository.AgentRepository;
import com.taskmanager.taskmanager.util.Constants;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", config = MapstructConfig.class)
public abstract class AgentMapper {

    @Autowired
    AgentRepository agentRepository;

    public abstract Agent fromDto(AgentDto agentDto);

    public abstract AgentDto toDto(Agent agent);

    public String toName(Agent agent) {
        return agent.getName();
    }

    public Agent fromName(String name) {
        return agentRepository.findOneByName(name)
                .orElseThrow(() -> new AgentNotFoundException(Constants.AGENT_NOT_FOUND));
    }
}
