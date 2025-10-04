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

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userClient.getUserById("123");
        });

        assertEquals("Erro ao criar URI para buscar usuário", exception.getMessage());
    }

    @Test
    void getUserByUsername_throwsRuntimeExceptionWhenURISyntaxException() throws Exception {
        Field urlField = UserClient.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(userClient, "invalid url");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userClient.getUserByUsername("testuser");
        });

        assertEquals("Erro ao criar URI para buscar usuário", exception.getMessage());
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

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userClient.registerUser(newUser);
        });

        assertEquals("Erro ao criar URI para registrar usuário", exception.getMessage());
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

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userClient.updateUser(user);
        });

        assertEquals("Erro ao criar URI para atualizar senha do usuário", exception.getMessage());
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

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userClient.deleteAccount("testuser");
        });

        assertEquals("Erro ao criar URI para deletar usuário", exception.getMessage());
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
}
