package br.com.jboard.orchestrator.handlers;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {
    @Test
    void handleAllExceptions_returnsInternalServerError() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Exception ex = new Exception("erro");
        ResponseEntity<String> response = handler.handleAllExceptions(ex);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("erro", response.getBody());
    }
}


