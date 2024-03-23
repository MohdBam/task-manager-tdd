package com.taskmanager.taskmanager.repository.impl;

import com.taskmanager.taskmanager.model.Agent;
import com.taskmanager.taskmanager.model.Status;
import com.taskmanager.taskmanager.model.Ticket;
import com.taskmanager.taskmanager.repository.TicketFilterRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketFilterRepositoryImpl implements TicketFilterRepository {

    //TODO use hibernate metamodel
    public static final String STATUS = "status";
    public static final String CREATED_DATE = "createdDate";
    public static final String ASSIGNED_AGENT = "assignedAgent";
    public static final String NAME = "name";
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Ticket> findWithFilters(List<Status> status, String assignedAgent, LocalDateTime startDate, LocalDateTime endDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Ticket> query = cb.createQuery(Ticket.class);
        Root<Ticket> ticketRoot = query.from(Ticket.class);

        List<Predicate> predicates = getPredicates(status, assignedAgent, startDate, endDate, cb, ticketRoot);

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }

    private static List<Predicate> getPredicates(List<Status> status, String assignedAgent, LocalDateTime startDate, LocalDateTime endDate, CriteriaBuilder cb, Root<Ticket> ticketRoot) {
        List<Predicate> predicates = new ArrayList<>();

        if (status != null && !status.isEmpty()) {
            predicates.add(ticketRoot.get(STATUS).in(status));
        }

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(ticketRoot.get(CREATED_DATE), startDate));
        }

        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(ticketRoot.get(CREATED_DATE), endDate));
        }

        if (StringUtils.hasText(assignedAgent)) {
            Join<Ticket, Agent> agentJoin = ticketRoot.join(ASSIGNED_AGENT, JoinType.LEFT);
            predicates.add(cb.equal(agentJoin.get(NAME), assignedAgent));
        }
        return predicates;
    }
}
