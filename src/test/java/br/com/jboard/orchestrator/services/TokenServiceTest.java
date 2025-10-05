package br.com.jboard.orchestrator.services;

import br.com.jboard.orchestrator.models.JWTSubject;
import br.com.jboard.orchestrator.models.User;
import br.com.jboard.orchestrator.models.enums.RoleEnum;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @InjectMocks
    TokenService tokenService;

    @BeforeEach
    void setUp() throws Exception {
        Field secretField = TokenService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(tokenService, "test-secret-key");
    }

    @Test
    void generateToken_validUser_returnsValidJwtToken() {
        User user = new User("testuser", "password", RoleEnum.FREE);

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void generateToken_userWithPremiumRole_returnsValidToken() {
        User user = new User("premiumuser", "password", RoleEnum.PREMIUM);

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void validateToken_validToken_returnsJWTSubject() {
        User user = new User("testuser", "password", RoleEnum.FREE);
        String token = tokenService.generateToken(user);

        JWTSubject jwtSubject = tokenService.validateToken(token);

        assertNotNull(jwtSubject);
        assertEquals("testuser", jwtSubject.getUsername());
        assertEquals("free", jwtSubject.getRole());
    }

    @Test
    void validateToken_invalidToken_returnsNull() {
        String invalidToken = "invalid.token.here";

        JWTSubject result = tokenService.validateToken(invalidToken);

        assertNull(result);
    }

    @Test
    void validateToken_expiredToken_returnsNull() {
        String expiredToken = JWT.create()
                .withIssuer("jboard")
                .withSubject("testuser")
                .withClaim("role", "free")
                .withExpiresAt(java.time.Instant.now().minusSeconds(3600))
                .sign(Algorithm.HMAC256("test-secret-key"));

        JWTSubject result = tokenService.validateToken(expiredToken);

        assertNull(result);
    }

    @Test
    void validateToken_tokenWithWrongIssuer_returnsNull() {
        String tokenWithWrongIssuer = JWT.create()
                .withIssuer("wrong-issuer")
                .withSubject("testuser")
                .withClaim("role", "free")
                .withExpiresAt(java.time.Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256("test-secret-key"));

        JWTSubject result = tokenService.validateToken(tokenWithWrongIssuer);

        assertNull(result);
    }

    @Test
    void validateToken_tokenWithDifferentSecret_returnsNull() {
        String tokenWithDifferentSecret = JWT.create()
                .withIssuer("jboard")
                .withSubject("testuser")
                .withClaim("role", "free")
                .withExpiresAt(java.time.Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256("different-secret"));

        JWTSubject result = tokenService.validateToken(tokenWithDifferentSecret);

        assertNull(result);
    }

    @Test
    void validateToken_nullToken_returnsNull() {
        JWTSubject result = tokenService.validateToken(null);

        assertNull(result);
    }

    @Test
    void validateToken_emptyToken_returnsNull() {
        JWTSubject result = tokenService.validateToken("");

        assertNull(result);
    }

    @Test
    void generateToken_userWithNullUsername_generatesTokenSuccessfully() {
        User user = new User(null, "password", RoleEnum.FREE);

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateToken_nullUser_throwsException() {
        assertThrows(NullPointerException.class, () -> tokenService.generateToken(null));
    }

    @Test
    void generateAndValidateToken_roundTrip_worksCorrectly() {
        User user = new User("roundtripuser", "password", RoleEnum.PREMIUM);

        String token = tokenService.generateToken(user);
        JWTSubject jwtSubject = tokenService.validateToken(token);

        assertNotNull(jwtSubject);
        assertEquals("roundtripuser", jwtSubject.getUsername());
        assertEquals("premium", jwtSubject.getRole());
    }

    @Test
    void generateToken_userWithEmptyUsername_generatesTokenWithEmptySubject() {
        User user = new User("", "password", RoleEnum.FREE);

        String token = tokenService.generateToken(user);
        JWTSubject jwtSubject = tokenService.validateToken(token);

        assertNotNull(jwtSubject);
        assertEquals("", jwtSubject.getUsername());
        assertEquals("free", jwtSubject.getRole());
    }

    @Test
    void validateToken_malformedToken_returnsNull() {
        String malformedToken = "not.a.valid.jwt.token.format";

        JWTSubject result = tokenService.validateToken(malformedToken);

        assertNull(result);
    }

    @Test
    void generateToken_userWithSpecialCharactersInUsername_generatesValidToken() {
        User user = new User("user@domain.com", "password", RoleEnum.FREE);

        String token = tokenService.generateToken(user);
        JWTSubject jwtSubject = tokenService.validateToken(token);

        assertNotNull(jwtSubject);
        assertEquals("user@domain.com", jwtSubject.getUsername());
        assertEquals("free", jwtSubject.getRole());
    }

    @Test
    void generateToken_userWithUnicodeCharactersInUsername_generatesValidToken() {
        User user = new User("usuário_teste", "password", RoleEnum.PREMIUM);

        String token = tokenService.generateToken(user);
        JWTSubject jwtSubject = tokenService.validateToken(token);

        assertNotNull(jwtSubject);
        assertEquals("usuário_teste", jwtSubject.getUsername());
        assertEquals("premium", jwtSubject.getRole());
    }

    @Test
    void generateToken_userWithVeryLongUsername_generatesValidToken() {
        String longUsername = "a".repeat(255);
        User user = new User(longUsername, "password", RoleEnum.FREE);

        String token = tokenService.generateToken(user);
        JWTSubject jwtSubject = tokenService.validateToken(token);

        assertNotNull(jwtSubject);
        assertEquals(longUsername, jwtSubject.getUsername());
        assertEquals("free", jwtSubject.getRole());
    }

    @Test
    void validateToken_tokenWithPremiumRole_returnsCorrectRole() {
        User user = new User("premiumuser", "password", RoleEnum.PREMIUM);
        String token = tokenService.generateToken(user);

        JWTSubject jwtSubject = tokenService.validateToken(token);

        assertNotNull(jwtSubject);
        assertEquals("premiumuser", jwtSubject.getUsername());
        assertEquals("premium", jwtSubject.getRole());
    }

    @Test
    void generateToken_multipleCallsWithSameUser_mayGenerateSameTokens() {
        User user = new User("testuser", "password", RoleEnum.FREE);

        String token1 = tokenService.generateToken(user);
        String token2 = tokenService.generateToken(user);

        assertNotNull(token1);
        assertNotNull(token2);

        JWTSubject jwtSubject1 = tokenService.validateToken(token1);
        JWTSubject jwtSubject2 = tokenService.validateToken(token2);

        assertNotNull(jwtSubject1);
        assertNotNull(jwtSubject2);
        assertEquals("testuser", jwtSubject1.getUsername());
        assertEquals("testuser", jwtSubject2.getUsername());
    }

    @Test
    void validateToken_whitespaceOnlyToken_returnsNull() {
        String whitespaceToken = "   ";

        JWTSubject result = tokenService.validateToken(whitespaceToken);

        assertNull(result);
    }

    @Test
    void generateToken_verifyTokenStructure_containsExpectedParts() {
        User user = new User("testuser", "password", RoleEnum.FREE);

        String token = tokenService.generateToken(user);

        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
        assertFalse(parts[0].isEmpty());
        assertFalse(parts[1].isEmpty());
        assertFalse(parts[2].isEmpty());
    }

    @Test
    void generateToken_withInvalidSecret_throwsIllegalArgumentException() throws Exception {
        Field secretField = TokenService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(tokenService, null);

        User user = new User("testuser", "password", RoleEnum.FREE);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> tokenService.generateToken(user));

        assertEquals("The Secret cannot be null", exception.getMessage());
    }
}
