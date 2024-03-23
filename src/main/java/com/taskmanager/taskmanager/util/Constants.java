package com.taskmanager.taskmanager.util;

public final class Constants {


    public static final String START_DATE_CANNOT_BE_AFTER_END_DATE = "Start date cannot be after end date";

    private Constants() {} // Prevents instantiation



    public static final String AGENT_NOT_FOUND = "Agent not found!";
    public static final String TICKET_NOT_FOUND = "Ticket not found!";
    public static final String MISSING_RESOLUTION_SUMMARY_EXCEPTION = "Resolution summary is missing!" ;
    public static final String MISSING_DESCRIPTION_EXCEPTION = "Description is missing!";
    public static final String CLOSED_TICKETS_CANNOT_BE_UPDATED = "Closed tickets cannot be updated!";
    public static final String ONLY_NEW_TICKETS_CAN_BE_ASSIGNED_TO_AN_AGENT = "Only NEW tickets can be assigned to an agent.";
    public static final String ONLY_IN_PROGRESS_TICKETS_CAN_BE_RESOLVED = "Only IN_PROGRESS tickets can be resolved.";
    public static final String ONLY_RESOLVED_TICKETS_CAN_BE_CLOSED = "Only RESOLVED tickets can be closed!";
}
