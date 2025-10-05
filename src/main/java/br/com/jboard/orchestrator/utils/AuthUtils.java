package br.com.jboard.orchestrator.utils;

import br.com.jboard.orchestrator.models.JWTSubject;
import br.com.jboard.orchestrator.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {
    private final TokenService tokenService;

    public AuthUtils(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public String getUsernameFromRequest(HttpServletRequest request) {
        var token = request.getHeader("Authorization").substring(7);
        JWTSubject jwtSubject = tokenService.validateToken(token);
        return jwtSubject != null ? jwtSubject.getUsername() : null;
    }
}
