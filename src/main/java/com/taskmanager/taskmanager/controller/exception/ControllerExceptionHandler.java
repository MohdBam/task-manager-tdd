package com.taskmanager.taskmanager.controller.exception;

import com.taskmanager.taskmanager.exception.AgentNotFoundException;
import com.taskmanager.taskmanager.exception.InvalidTicketStateException;
import com.taskmanager.taskmanager.exception.MissingResolutionSummaryException;
import com.taskmanager.taskmanager.exception.TicketNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(InvalidTicketStateException.class)
    public ResponseEntity<String> handleInvalidTicketState(InvalidTicketStateException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AgentNotFoundException.class)
    public ResponseEntity<String> handleInvalidTicketState(AgentNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<String> handleInvalidTicketState(TicketNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingResolutionSummaryException.class)
    public ResponseEntity<String> handleMissingResolutionSummary(MissingResolutionSummaryException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
