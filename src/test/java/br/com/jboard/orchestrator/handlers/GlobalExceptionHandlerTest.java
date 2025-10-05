package br.com.jboard.orchestrator.handlers;

import br.com.jboard.orchestrator.models.exceptions.ForbiddenException;
import br.com.jboard.orchestrator.models.exceptions.InternalServerErrorException;
import br.com.jboard.orchestrator.models.exceptions.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleMethodArgumentNotValidException_returnsBadRequestResponse() {
        ResponseEntity<String> response = handler.handleMethodArgumentNotValidException();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Campos inválidos", response.getBody());
    }

    @Test
    void handleInternalAuthenticationServiceException_returnsUnauthorizedResponse() {
        ResponseEntity<String> response = handler.handleInternalAuthenticationServiceException();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Usuário ou senha incorreto", response.getBody());
    }

    @Test
    void handleBadCredentialsException_returnsUnauthorizedResponse() {
        ResponseEntity<String> response = handler.handleBadCredentialsException();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Usuário ou senha incorreto", response.getBody());
    }

    @Test
    void handleUnauthorizedException_withMessage_returnsCustomMessage() {
        UnauthorizedException ex = new UnauthorizedException("Token inválido");
        ResponseEntity<String> response = handler.handleUnauthorizedException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Token inválido", response.getBody());
    }

    @Test
    void handleUnauthorizedException_withNullMessage_returnsDefaultMessage() {
        UnauthorizedException ex = new UnauthorizedException(null);
        ResponseEntity<String> response = handler.handleUnauthorizedException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Não autorizado", response.getBody());
    }

    @Test
    void handleForbiddenException_withMessage_returnsCustomMessage() {
        ForbiddenException ex = new ForbiddenException("Acesso negado ao recurso");
        ResponseEntity<String> response = handler.handleForbiddenException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Acesso negado ao recurso", response.getBody());
    }

    @Test
    void handleForbiddenException_withNullMessage_returnsDefaultMessage() {
        ForbiddenException ex = new ForbiddenException(null);
        ResponseEntity<String> response = handler.handleForbiddenException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Acesso negado", response.getBody());
    }

    @Test
    void handleAccessDeniedException_returnsForbiddenResponse() {
        ResponseEntity<String> response = handler.handleAccessDeniedException();

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Acesso negado - permissões insuficientes", response.getBody());
    }

    @Test
    void handleInternalServerErrorException_withMessage_returnsCustomMessage() {
        InternalServerErrorException ex = new InternalServerErrorException("Erro no banco de dados");
        ResponseEntity<String> response = handler.handleInternalServerErrorException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro no banco de dados", response.getBody());
    }

    @Test
    void handleInternalServerErrorException_withNullMessage_returnsDefaultMessage() {
        InternalServerErrorException ex = new InternalServerErrorException(null);
        ResponseEntity<String> response = handler.handleInternalServerErrorException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno do servidor", response.getBody());
    }

    @Test
    void handleHttpServerErrorException_returnsInternalServerErrorResponse() {
        ResponseEntity<String> response = handler.handleHttpServerErrorException();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro no servidor externo", response.getBody());
    }

    @Test
    void handleRestClientException_returnsInternalServerErrorResponse() {
        ResponseEntity<String> response = handler.handleRestClientException();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro de comunicação com serviço externo", response.getBody());
    }

    @Test
    void handleRuntimeException_returnsInternalServerErrorResponse() {
        ResponseEntity<String> response = handler.handleRuntimeException();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno do servidor", response.getBody());
    }

    @Test
    void handleConflict_returnsConflictResponse() {
        ResponseEntity<String> response = handler.handleConflict();

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Usuário já cadastrado", response.getBody());
    }

    @Test
    void handleHttpClientUnauthorized_returnsUnauthorizedResponse() {
        ResponseEntity<String> response = handler.handleHttpClientUnauthorized();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Não autorizado", response.getBody());
    }

    @Test
    void handleHttpClientForbidden_returnsForbiddenResponse() {
        ResponseEntity<String> response = handler.handleHttpClientForbidden();

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Acesso negado", response.getBody());
    }

    @Test
    void handleRestClientResponseException_withServerError_returnsInternalServerError() {
        RestClientResponseException ex = new RestClientResponseException("Test", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error", null, "Server Error".getBytes(), null);

        ResponseEntity<String> response = handler.handleRestClientResponseException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno do servidor", response.getBody());
    }

    @Test
    void handleRestClientResponseException_withBadGateway_returnsInternalServerError() {
        RestClientResponseException ex = new RestClientResponseException("Test", HttpStatus.BAD_GATEWAY.value(),
                "Bad Gateway", null, "Bad Gateway".getBytes(), null);

        ResponseEntity<String> response = handler.handleRestClientResponseException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno do servidor", response.getBody());
    }

    @Test
    void handleRestClientResponseException_withForbidden_returnsForbidden() {
        RestClientResponseException ex = new RestClientResponseException("Test", HttpStatus.FORBIDDEN.value(),
                "Forbidden", null, "Forbidden".getBytes(), null);

        ResponseEntity<String> response = handler.handleRestClientResponseException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Acesso negado", response.getBody());
    }

    @Test
    void handleRestClientResponseException_withUnauthorized_returnsUnauthorized() {
        RestClientResponseException ex = new RestClientResponseException("Test", HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized", null, "Unauthorized".getBytes(), null);

        ResponseEntity<String> response = handler.handleRestClientResponseException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Não autorizado", response.getBody());
    }

    @Test
    void handleRestClientResponseException_withClientError_returnsOriginalStatusAndBody() {
        String responseBody = "Bad Request Error";
        RestClientResponseException ex = new RestClientResponseException("Test", HttpStatus.BAD_REQUEST.value(),
                "Bad Request", null, responseBody.getBytes(), null);

        ResponseEntity<String> response = handler.handleRestClientResponseException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(responseBody, response.getBody());
    }

    @Test
    void handleRestClientResponseException_withNotFound_returnsOriginalStatusAndBody() {
        String responseBody = "Resource not found";
        RestClientResponseException ex = new RestClientResponseException("Test", HttpStatus.NOT_FOUND.value(),
                "Not Found", null, responseBody.getBytes(), null);

        ResponseEntity<String> response = handler.handleRestClientResponseException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(responseBody, response.getBody());
    }

    @Test
    void handleGenericException_returnsInternalServerErrorResponse() {
        ResponseEntity<String> response = handler.handleGenericException();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno do servidor", response.getBody());
    }
}
