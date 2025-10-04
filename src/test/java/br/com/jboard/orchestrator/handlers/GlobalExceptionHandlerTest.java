package br.com.jboard.orchestrator.handlers;

import br.com.jboard.orchestrator.models.exceptions.ForbiddenException;
import br.com.jboard.orchestrator.models.exceptions.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBadCredentialsException_returnsUnauthorizedResponse() {
        BadCredentialsException ex = new BadCredentialsException("Invalid credentials");

        ResponseEntity<String> response = handler.handleBadCredentialsException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Usuário ou senha incorreto", response.getBody());
    }

    @Test
    void handleUnauthorizedException_returnsUnauthorizedResponse() {
        UnauthorizedException ex = new UnauthorizedException("Token invalid");

        ResponseEntity<String> response = handler.handleUnauthorizedException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Token invalid", response.getBody());
    }

    @Test
    void handleForbiddenException_returnsForbiddenResponse() {
        ForbiddenException ex = new ForbiddenException("Access denied");

        ResponseEntity<String> response = handler.handleForbiddenException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Senha não confere", response.getBody());
    }

    @Test
    void handleConflict_returnsConflictResponse() {
        HttpClientErrorException.Conflict ex = (HttpClientErrorException.Conflict) HttpClientErrorException.create(
                HttpStatus.CONFLICT, "Conflict", null, null, StandardCharsets.UTF_8);

        ResponseEntity<String> response = handler.handleConflict(ex);

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
        assertEquals(null, response.getBody());
    }

    @Test
    void handleUnauthorizedException_withNullMessage_returnsUnauthorizedWithNull() {
        UnauthorizedException ex = new UnauthorizedException(null);

        ResponseEntity<String> response = handler.handleUnauthorizedException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void handleRestClientException_withEmptyResponseBody_returnsEmptyBody() {
        RestClientResponseException ex = new RestClientResponseException("Test", HttpStatus.NOT_FOUND.value(),
                "Not Found", null, new byte[0], null);

        ResponseEntity<String> response = handler.handleRestClientException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("", response.getBody());
    }
}
