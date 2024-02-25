package com.taskmanager.taskmanager.exception;

public class IllegalTicketStateException extends RuntimeException {
    public IllegalTicketStateException(String msg) {
        super(msg);
    }
}
