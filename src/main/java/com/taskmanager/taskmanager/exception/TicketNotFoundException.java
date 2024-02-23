package com.taskmanager.taskmanager.exception;

public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(String msg) {
        super(msg);
    }
}
