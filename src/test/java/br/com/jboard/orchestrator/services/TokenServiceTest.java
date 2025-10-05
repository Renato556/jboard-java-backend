package br.com.jboard.orchestrator.services;

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
    void validateToken_validToken_returnsUsername() {
        User user = new User("testuser", "password", RoleEnum.FREE);
        String token = tokenService.generateToken(user);

        String username = tokenService.validateToken(token);

        assertEquals("testuser", username);
    }

    @Test
    void validateToken_invalidToken_returnsEmptyString() {
        String invalidToken = "invalid.token.here";

        String result = tokenService.validateToken(invalidToken);

        assertEquals("", result);
    }

    @Test
    void validateToken_expiredToken_returnsEmptyString() {
        String expiredToken = JWT.create()
                .withIssuer("jboard")
                .withSubject("testuser")
                .withExpiresAt(java.time.Instant.now().minusSeconds(3600))
                .sign(Algorithm.HMAC256("test-secret-key"));

        String result = tokenService.validateToken(expiredToken);

        assertEquals("", result);
    }

    @Test
    void validateToken_tokenWithWrongIssuer_returnsEmptyString() {
        String tokenWithWrongIssuer = JWT.create()
                .withIssuer("wrong-issuer")
                .withSubject("testuser")
                .withExpiresAt(java.time.Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256("test-secret-key"));

        String result = tokenService.validateToken(tokenWithWrongIssuer);

        assertEquals("", result);
    }

    @Test
    void validateToken_tokenWithDifferentSecret_returnsEmptyString() {
        String tokenWithDifferentSecret = JWT.create()
                .withIssuer("jboard")
                .withSubject("testuser")
                .withExpiresAt(java.time.Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256("different-secret"));

        String result = tokenService.validateToken(tokenWithDifferentSecret);

        assertEquals("", result);
    }

    @Test
    void validateToken_nullToken_returnsEmptyString() {
        String result = tokenService.validateToken(null);

        assertEquals("", result);
    }

    @Test
    void validateToken_emptyToken_returnsEmptyString() {
        String result = tokenService.validateToken("");

        assertEquals("", result);
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
        String validatedUsername = tokenService.validateToken(token);

        assertEquals("roundtripuser", validatedUsername);
    }

    @Test
    void generateToken_userWithEmptyUsername_generatesTokenWithEmptySubject() {
        User user = new User("", "password", RoleEnum.FREE);

        String token = tokenService.generateToken(user);
        String validatedUsername = tokenService.validateToken(token);

        assertEquals("", validatedUsername);
    }

    @Test
    void validateToken_malformedToken_returnsEmptyString() {
        String malformedToken = "not.a.valid.jwt.token.format";

        String result = tokenService.validateToken(malformedToken);

        assertEquals("", result);
    }

    @Test
    void generateToken_userWithSpecialCharactersInUsername_generatesValidToken() {
        User user = new User("user@domain.com", "password", RoleEnum.FREE);

        String token = tokenService.generateToken(user);
        String validatedUsername = tokenService.validateToken(token);

        assertEquals("user@domain.com", validatedUsername);
    }

    @Test
    void generateToken_userWithUnicodeCharactersInUsername_generatesValidToken() {
        User user = new User("usuário_teste", "password", RoleEnum.PREMIUM);

        String token = tokenService.generateToken(user);
        String validatedUsername = tokenService.validateToken(token);

        assertEquals("usuário_teste", validatedUsername);
    }

    @Test
    void generateToken_userWithVeryLongUsername_generatesValidToken() {
        String longUsername = "a".repeat(255);
        User user = new User(longUsername, "password", RoleEnum.FREE);

        String token = tokenService.generateToken(user);
        String validatedUsername = tokenService.validateToken(token);

        assertEquals(longUsername, validatedUsername);
    }

    @Test
    void validateToken_tokenWithoutExpirationClaim_returnsUsername() {
        String tokenWithoutExpiration = JWT.create()
                .withIssuer("jboard")
                .withSubject("testuser")
                .sign(Algorithm.HMAC256("test-secret-key"));

        String result = tokenService.validateToken(tokenWithoutExpiration);

        assertEquals("testuser", result);
    }

    @Test
    void validateToken_tokenWithoutSubjectClaim_returnsNull() {
        String tokenWithoutSubject = JWT.create()
                .withIssuer("jboard")
                .withExpiresAt(java.time.Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256("test-secret-key"));

        String result = tokenService.validateToken(tokenWithoutSubject);

        assertNull(result);
    }

    @Test
    void validateToken_tokenSignedWithDifferentAlgorithm_returnsEmptyString() {
        String tokenWithDifferentAlgorithm = JWT.create()
                .withIssuer("jboard")
                .withSubject("testuser")
                .withExpiresAt(java.time.Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC512("test-secret-key"));

        String result = tokenService.validateToken(tokenWithDifferentAlgorithm);

        assertEquals("", result);
    }

    @Test
    void generateToken_multipleCallsWithSameUser_mayGenerateSameTokens() {
        User user = new User("testuser", "password", RoleEnum.FREE);

        String token1 = tokenService.generateToken(user);
        String token2 = tokenService.generateToken(user);

        assertNotNull(token1);
        assertNotNull(token2);
        assertEquals("testuser", tokenService.validateToken(token1));
        assertEquals("testuser", tokenService.validateToken(token2));
    }

    @Test
    void validateToken_whitespaceOnlyToken_returnsEmptyString() {
        String whitespaceToken = "   ";

        String result = tokenService.validateToken(whitespaceToken);

        assertEquals("", result);
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
