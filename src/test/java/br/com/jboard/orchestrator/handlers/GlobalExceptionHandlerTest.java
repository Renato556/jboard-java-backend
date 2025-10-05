package br.com.jboard.orchestrator.handlers;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBadCredentialsException_returnsUnauthorizedResponse() {
        ResponseEntity<String> response = handler.handleBadCredentialsException();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Usuário ou senha incorreto", response.getBody());
    }

    @Test
    void handleUnauthorizedException_returnsUnauthorizedResponse() {
        ResponseEntity<String> response = handler.handleUnauthorizedException();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Não autorizado", response.getBody());
    }

    @Test
    void handleForbiddenException_returnsForbiddenResponse() {
        ResponseEntity<String> response = handler.handleForbiddenException();

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Senha não confere", response.getBody());
    }

    @Test
    void handleConflict_returnsConflictResponse() {
        ResponseEntity<String> response = handler.handleConflict();

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Usuário já cadastrado", response.getBody());
    }

    @Test
    void handleRestClientException_returnsResponseWithOriginalStatusAndBody() {
        String responseBody = "Error from external service";
        RestClientResponseException ex = new RestClientResponseException("Test", HttpStatus.BAD_REQUEST.value(),
                "Bad Request", null, responseBody.getBytes(), null);

        ResponseEntity<String> response = handler.handleRestClientException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(responseBody, response.getBody());
    }

    @Test
    void handleAllExceptions_returnsInternalServerError() {
        Exception ex = new Exception("erro");
        ResponseEntity<String> response = handler.handleAllExceptions(ex);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("erro", response.getBody());
    }

    @Test
    void handleAllExceptions_withNullMessage_returnsInternalServerError() {
        Exception ex = new Exception((String) null);
        ResponseEntity<String> response = handler.handleAllExceptions(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void handleRestClientException_withEmptyResponseBody_returnsEmptyBody() {
        RestClientResponseException ex = new RestClientResponseException("Test", HttpStatus.NOT_FOUND.value(),
                "Not Found", null, new byte[0], null);

        ResponseEntity<String> response = handler.handleRestClientException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void handleMethodArgumentNotValidException_returnsBadRequestResponse() {
        ResponseEntity<String> response = handler.handleMethodArgumentNotValidException();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Campos inválidos", response.getBody());
    }

    @Test
    void handleInternalAuthenticationServiceException_returnsNotFoundResponse() {
        ResponseEntity<String> response = handler.handleInternalAuthenticationServiceException();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Usuário ou senha incorreto", response.getBody());
    }

    @Test
    void handleInternalAuthenticationServiceException_withNullMessage_returnsNotFoundResponse() {
        ResponseEntity<String> response = handler.handleInternalAuthenticationServiceException();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Usuário ou senha incorreto", response.getBody());
    }
}
