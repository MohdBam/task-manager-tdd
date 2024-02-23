package com.taskmanager.taskmanager.exception;

public class InvalidTicketStateException extends RuntimeException {
    public InvalidTicketStateException(String msg) {
        super(msg);
    }
}
