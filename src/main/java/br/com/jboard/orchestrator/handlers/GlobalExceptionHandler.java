package br.com.jboard.orchestrator.handlers;

import br.com.jboard.orchestrator.models.exceptions.ForbiddenException;
import br.com.jboard.orchestrator.models.exceptions.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException() {
        return new ResponseEntity<>("Campos inválidos", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<String> handleInternalAuthenticationServiceException() {
        return new ResponseEntity<>("Usuário ou senha incorreto", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException() {
        return new ResponseEntity<>("Usuário ou senha incorreto", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException() {
        return new ResponseEntity<>("Não autorizado", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handleForbiddenException() {
        return new ResponseEntity<>("Senha não confere", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpClientErrorException.Conflict.class)
    public ResponseEntity<String> handleConflict() {
        return new ResponseEntity<>("Usuário já cadastrado", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<String> handleRestClientException(RestClientResponseException ex) {
        return new ResponseEntity<>(ex.getResponseBodyAsString(),
                HttpStatus.valueOf(ex.getStatusCode().value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
