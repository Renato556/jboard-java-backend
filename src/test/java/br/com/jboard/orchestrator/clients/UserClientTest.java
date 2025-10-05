package br.com.jboard.orchestrator.clients;

import br.com.jboard.orchestrator.models.User;
import br.com.jboard.orchestrator.models.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Field;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserClientTest {
    @Mock
    RestClient restClient;
    @InjectMocks
    UserClient userClient;

    @BeforeEach
    void setUp() throws Exception {
        Field urlField = UserClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(userClient, "http://localhost");
    }

    @Test
    void getUserById_returnsUserSuccessfully() {
        String userId = "123";
        User expectedUser = new User("testuser", "password", RoleEnum.FREE);

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        when(spec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(User.class)).thenReturn(expectedUser);

        User result = userClient.getUserById(userId);

        assertNotNull(result);
        assertEquals(expectedUser.getUsername(), result.getUsername());
        verify(restClient).get();
    }

    @Test
    void getUserByUsername_returnsUserSuccessfully() {
        String username = "testuser";
        User expectedUser = new User(username, "password", RoleEnum.FREE);

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        when(spec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(User.class)).thenReturn(expectedUser);

        User result = userClient.getUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(restClient).get();
    }

    @Test
    void getUserById_throwsRuntimeExceptionWhenURISyntaxException() throws Exception {
        Field urlField = UserClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(userClient, "invalid url");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userClient.getUserById("123"));

        assertEquals("Erro interno - URI inválida", exception.getMessage());
    }

    @Test
    void getUserByUsername_throwsRuntimeExceptionWhenURISyntaxException() throws Exception {
        Field urlField = UserClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(userClient, "invalid url");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userClient.getUserByUsername("testuser"));

        assertEquals("Erro interno - URI inválida", exception.getMessage());
    }

    @Test
    void registerUser_successfullyRegistersUser() {
        User newUser = new User("newuser", "password", RoleEnum.FREE);

        RestClient.RequestBodyUriSpec bodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(any(URI.class))).thenReturn(bodySpec);
        when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
        when(bodySpec.body(newUser)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        assertDoesNotThrow(() -> userClient.registerUser(newUser));

        verify(restClient).post();
        verify(bodySpec).contentType(MediaType.APPLICATION_JSON);
        verify(bodySpec).body(newUser);
    }

    @Test
    void registerUser_throwsRuntimeExceptionWhenURISyntaxException() throws Exception {
        Field urlField = UserClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(userClient, "invalid url");

        User newUser = new User("newuser", "password", RoleEnum.FREE);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userClient.registerUser(newUser));

        assertEquals("Erro interno - URI inválida", exception.getMessage());
    }

    @Test
    void updateUser_successfullyUpdatesUser() {
        User user = new User("testuser", "newpassword", RoleEnum.PREMIUM);

        RestClient.RequestBodyUriSpec bodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.put()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(any(URI.class))).thenReturn(bodySpec);
        when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
        when(bodySpec.body(user)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        assertDoesNotThrow(() -> userClient.updateUser(user));

        verify(restClient).put();
        verify(bodySpec).contentType(MediaType.APPLICATION_JSON);
        verify(bodySpec).body(user);
    }

    @Test
    void updateUser_throwsRuntimeExceptionWhenURISyntaxException() throws Exception {
        Field urlField = UserClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(userClient, "invalid url");

        User user = new User("testuser", "newpassword", RoleEnum.PREMIUM);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userClient.updateUser(user));

        assertEquals("Erro interno - URI inválida", exception.getMessage());
    }

    @Test
    void deleteAccount_successfullyDeletesAccount() {
        String username = "testuser";

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.delete()).thenReturn(spec);
        when(spec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        assertDoesNotThrow(() -> userClient.deleteAccount(username));

        verify(restClient).delete();
    }

    @Test
    void deleteAccount_throwsRuntimeExceptionWhenURISyntaxException() throws Exception {
        Field urlField = UserClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(userClient, "invalid url");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userClient.deleteAccount("testuser"));

        assertEquals("Erro interno - URI inválida", exception.getMessage());
    }

    @Test
    void getUserById_withNullId_returnsUser() {
        User expectedUser = new User("testuser", "password", RoleEnum.FREE);

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        when(spec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(User.class)).thenReturn(expectedUser);

        User result = userClient.getUserById(null);

        assertNotNull(result);
        verify(restClient).get();
    }

    @Test
    void getUserByUsername_withEmptyUsername_returnsUser() {
        User expectedUser = new User("", "password", RoleEnum.FREE);

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        when(spec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(User.class)).thenReturn(expectedUser);

        User result = userClient.getUserByUsername("");

        assertNotNull(result);
        verify(restClient).get();
    }

    @Test
    void deleteAccount_withSpecialCharactersInUsername_successfullyDeletes() {
        String username = "test@user.com";

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.delete()).thenReturn(spec);
        when(spec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        assertDoesNotThrow(() -> userClient.deleteAccount(username));

        verify(restClient).delete();
    }

    @Test
    void getUserById_throwsRestClientExceptionWhenServiceUnavailable() {
        String userId = "123";

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        when(spec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(User.class)).thenThrow(new org.springframework.web.client.RestClientException("Service unavailable"));

        assertThrows(org.springframework.web.client.RestClientException.class, () -> userClient.getUserById(userId));
    }

    @Test
    void getUserByUsername_throwsRestClientExceptionWhenServiceUnavailable() {
        String username = "testuser";

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        when(spec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(User.class)).thenThrow(new org.springframework.web.client.RestClientException("Service unavailable"));

        assertThrows(org.springframework.web.client.RestClientException.class, () -> userClient.getUserByUsername(username));
    }

    @Test
    void registerUser_throwsRestClientExceptionWhenServiceError() {
        User newUser = new User("newuser", "password", RoleEnum.FREE);

        RestClient.RequestBodyUriSpec bodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(any(URI.class))).thenReturn(bodySpec);
        when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
        when(bodySpec.body(newUser)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenThrow(new org.springframework.web.client.RestClientException("Server error"));

        assertThrows(org.springframework.web.client.RestClientException.class, () -> userClient.registerUser(newUser));
    }

    @Test
    void updateUser_throwsRestClientExceptionWhenServiceError() {
        User user = new User("testuser", "newpassword", RoleEnum.PREMIUM);

        RestClient.RequestBodyUriSpec bodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.put()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(any(URI.class))).thenReturn(bodySpec);
        when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
        when(bodySpec.body(user)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenThrow(new org.springframework.web.client.RestClientException("Server error"));

        assertThrows(org.springframework.web.client.RestClientException.class, () -> userClient.updateUser(user));
    }

    @Test
    void deleteAccount_throwsRestClientExceptionWhenServiceError() {
        String username = "testuser";

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.delete()).thenReturn(spec);
        when(spec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenThrow(new org.springframework.web.client.RestClientException("Server error"));

        assertThrows(org.springframework.web.client.RestClientException.class, () -> userClient.deleteAccount(username));
    }

    @Test
    void getUserById_withVeryLongId_handlesCorrectly() {
        String longId = "a".repeat(1000);
        User expectedUser = new User("testuser", "password", RoleEnum.FREE);

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        when(spec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(User.class)).thenReturn(expectedUser);

        User result = userClient.getUserById(longId);

        assertNotNull(result);
        assertEquals(expectedUser.getUsername(), result.getUsername());
    }

    @Test
    void getUserByUsername_withUsernameContainingSpecialCharacters_handlesCorrectly() {
        String specialUsername = "user@domain.com";
        User expectedUser = new User(specialUsername, "password", RoleEnum.FREE);

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        when(spec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(User.class)).thenReturn(expectedUser);

        User result = userClient.getUserByUsername(specialUsername);

        assertNotNull(result);
        assertEquals(specialUsername, result.getUsername());
    }

    @Test
    void registerUser_withNullUser_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> userClient.registerUser(null));
    }

    @Test
    void updateUser_withNullUser_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> userClient.updateUser(null));
    }

    @Test
    void deleteAccount_withNullUsername_handlesCorrectly() {
        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.delete()).thenReturn(spec);
        when(spec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        assertDoesNotThrow(() -> userClient.deleteAccount(null));
    }

    @Test
    void getUserById_returnsNullWhenUserNotFound() {
        String userId = "999";

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        when(spec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(User.class)).thenReturn(null);

        User result = userClient.getUserById(userId);

        assertNull(result);
    }

    @Test
    void getUserByUsername_returnsNullWhenUserNotFound() {
        String username = "nonexistentuser";

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(spec);
        when(spec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(User.class)).thenReturn(null);

        User result = userClient.getUserByUsername(username);

        assertNull(result);
    }

    @Test
    void registerUser_withUserContainingUnicodeCharacters_handlesCorrectly() {
        User unicodeUser = new User("usuário_测试", "password", RoleEnum.FREE);

        RestClient.RequestBodyUriSpec bodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(any(URI.class))).thenReturn(bodySpec);
        when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
        when(bodySpec.body(unicodeUser)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        assertDoesNotThrow(() -> userClient.registerUser(unicodeUser));
    }

    @Test
    void updateUser_withUserContainingEmptyPassword_handlesCorrectly() {
        User userWithEmptyPassword = new User("testuser", "", RoleEnum.PREMIUM);

        RestClient.RequestBodyUriSpec bodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.put()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(any(URI.class))).thenReturn(bodySpec);
        when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
        when(bodySpec.body(userWithEmptyPassword)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        assertDoesNotThrow(() -> userClient.updateUser(userWithEmptyPassword));
    }

    @Test
    void deleteAccount_withEmptyUsername_handlesCorrectly() {
        String emptyUsername = "";

        RestClient.RequestHeadersUriSpec spec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.delete()).thenReturn(spec);
        when(spec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        assertDoesNotThrow(() -> userClient.deleteAccount(emptyUsername));
    }
}
