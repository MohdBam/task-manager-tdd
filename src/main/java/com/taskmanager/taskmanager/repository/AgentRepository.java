package com.taskmanager.taskmanager.repository;

import com.taskmanager.taskmanager.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findOneByName(String name);
}
