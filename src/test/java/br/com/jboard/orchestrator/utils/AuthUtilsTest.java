package br.com.jboard.orchestrator.utils;

import br.com.jboard.orchestrator.models.JWTSubject;
import br.com.jboard.orchestrator.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUtilsTest {
    @Mock
    TokenService tokenService;
    @Mock
    HttpServletRequest request;
    @InjectMocks
    AuthUtils authUtils;

    @Test
    void getUsernameFromRequest_validTokenWithUsername_returnsUsername() {
        String authHeader = "Bearer valid.jwt.token";
        JWTSubject jwtSubject = new JWTSubject("testuser", "free");

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenService.validateToken("valid.jwt.token")).thenReturn(jwtSubject);

        String result = authUtils.getUsernameFromRequest(request);

        assertEquals("testuser", result);
    }

    @Test
    void getUsernameFromRequest_validTokenWithEmptyUsername_returnsEmptyString() {
        String authHeader = "Bearer valid.jwt.token";
        JWTSubject jwtSubject = new JWTSubject("", "free");

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenService.validateToken("valid.jwt.token")).thenReturn(jwtSubject);

        String result = authUtils.getUsernameFromRequest(request);

        assertEquals("", result);
    }

    @Test
    void getUsernameFromRequest_validTokenWithNullUsername_returnsNull() {
        String authHeader = "Bearer valid.jwt.token";
        JWTSubject jwtSubject = new JWTSubject(null, "free");

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenService.validateToken("valid.jwt.token")).thenReturn(jwtSubject);

        String result = authUtils.getUsernameFromRequest(request);

        assertNull(result);
    }

    @Test
    void getUsernameFromRequest_invalidToken_returnsNull() {
        String authHeader = "Bearer invalid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenService.validateToken("invalid.jwt.token")).thenReturn(null);

        String result = authUtils.getUsernameFromRequest(request);

        assertNull(result);
    }

    @Test
    void getUsernameFromRequest_expiredToken_returnsNull() {
        String authHeader = "Bearer expired.jwt.token";

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenService.validateToken("expired.jwt.token")).thenReturn(null);

        String result = authUtils.getUsernameFromRequest(request);

        assertNull(result);
    }

    @Test
    void getUsernameFromRequest_malformedToken_returnsNull() {
        String authHeader = "Bearer malformed-token";

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenService.validateToken("malformed-token")).thenReturn(null);

        String result = authUtils.getUsernameFromRequest(request);

        assertNull(result);
    }

    @Test
    void getUsernameFromRequest_emptyTokenAfterBearer_returnsNull() {
        String authHeader = "Bearer ";

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenService.validateToken("")).thenReturn(null);

        String result = authUtils.getUsernameFromRequest(request);

        assertNull(result);
    }

    @Test
    void getUsernameFromRequest_tokenWithSpecialCharactersInUsername_returnsUsername() {
        String authHeader = "Bearer valid.jwt.token";
        JWTSubject jwtSubject = new JWTSubject("user@domain.com", "premium");

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenService.validateToken("valid.jwt.token")).thenReturn(jwtSubject);

        String result = authUtils.getUsernameFromRequest(request);

        assertEquals("user@domain.com", result);
    }

    @Test
    void getUsernameFromRequest_tokenWithUnicodeCharactersInUsername_returnsUsername() {
        String authHeader = "Bearer valid.jwt.token";
        JWTSubject jwtSubject = new JWTSubject("usuário_teste", "free");

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenService.validateToken("valid.jwt.token")).thenReturn(jwtSubject);

        String result = authUtils.getUsernameFromRequest(request);

        assertEquals("usuário_teste", result);
    }

    @Test
    void getUsernameFromRequest_tokenWithVeryLongUsername_returnsUsername() {
        String authHeader = "Bearer valid.jwt.token";
        String longUsername = "a".repeat(255);
        JWTSubject jwtSubject = new JWTSubject(longUsername, "premium");

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenService.validateToken("valid.jwt.token")).thenReturn(jwtSubject);

        String result = authUtils.getUsernameFromRequest(request);

        assertEquals(longUsername, result);
    }

    @Test
    void getUsernameFromRequest_nullAuthorizationHeader_throwsStringIndexOutOfBoundsException() {
        when(request.getHeader("Authorization")).thenReturn(null);

        assertThrows(NullPointerException.class, () -> authUtils.getUsernameFromRequest(request));
    }

    @Test
    void getUsernameFromRequest_authorizationHeaderTooShort_throwsStringIndexOutOfBoundsException() {
        when(request.getHeader("Authorization")).thenReturn("Bearer");

        assertThrows(StringIndexOutOfBoundsException.class, () -> authUtils.getUsernameFromRequest(request));
    }

    @Test
    void getUsernameFromRequest_authorizationHeaderExactlySevenCharacters_returnsNull() {
        String authHeader = "Bearer ";

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenService.validateToken("")).thenReturn(null);

        String result = authUtils.getUsernameFromRequest(request);

        assertNull(result);
    }

    @Test
    void getUsernameFromRequest_differentRoles_returnsUsernameRegardlessOfRole() {
        String authHeader = "Bearer valid.jwt.token";
        JWTSubject premiumUser = new JWTSubject("premiumuser", "premium");

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(tokenService.validateToken("valid.jwt.token")).thenReturn(premiumUser);

        String result = authUtils.getUsernameFromRequest(request);

        assertEquals("premiumuser", result);
    }
}
