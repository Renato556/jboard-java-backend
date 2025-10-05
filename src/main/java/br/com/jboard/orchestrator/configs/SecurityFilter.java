package br.com.jboard.orchestrator.configs;

import br.com.jboard.orchestrator.services.AuthorizationService;
import br.com.jboard.orchestrator.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final AuthorizationService authorizationService;

    public SecurityFilter(TokenService tokenService, AuthorizationService authorizationService) {
        this.tokenService = tokenService;
        this.authorizationService = authorizationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = recoverToken(request);

        if (token != null) {
            try {
                var subject = tokenService.validateToken(token);
                if (subject != null && !subject.isEmpty()) {
                    UserDetails user = authorizationService.loadUserByUsername(subject);
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("Token inválido ou expirado recebido");
                }
            } catch (Exception ex) {
                log.warn("Erro ao validar token: {}", ex.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;

        return authHeader.substring(7);
    }
}
