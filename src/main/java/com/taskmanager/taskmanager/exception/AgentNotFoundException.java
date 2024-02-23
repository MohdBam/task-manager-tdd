package com.taskmanager.taskmanager.exception;

public class AgentNotFoundException extends RuntimeException {
    public AgentNotFoundException(String msg) {
        super(msg);
    }
}
