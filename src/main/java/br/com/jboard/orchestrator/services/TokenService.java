package br.com.jboard.orchestrator.services;

import br.com.jboard.orchestrator.models.JWTSubject;
import br.com.jboard.orchestrator.models.User;
import br.com.jboard.orchestrator.models.exceptions.InternalServerErrorException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value( "${spring.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("jboard")
                    .withSubject(user.getUsername())
                    .withClaim("role", user.getRole().getRole())
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException ex) {
            throw new InternalServerErrorException("Erro interno ao gerar token", ex);
        }
    }

    public JWTSubject validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer("jboard")
                    .build()
                    .verify(token);

            String username = decodedJWT.getSubject();
            String role = decodedJWT.getClaim("role").asString();

            return new JWTSubject(username, role);
        } catch (JWTVerificationException ex) {
            return null;
        }
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
    }
}
