package soft.eng.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import soft.eng.persistence.InMemoryManagerRepository;


class AuthServiceTest {

    private AuthService authService;

    
    @BeforeEach
    void setUp() {
        authService = new AuthService(new InMemoryManagerRepository());
    }

   
    @Test
    void loginWithValidCredentialsShouldSucceed() {
        boolean result = authService.login("admin", "admin123");

        assertTrue(result);
        assertTrue(authService.isLoggedIn());
    }

    
    @Test
    void loginWithInvalidCredentialsShouldFail() {
        boolean result = authService.login("admin", "wrong-password");

        assertFalse(result);
        assertFalse(authService.isLoggedIn());
    }

    
    @Test
    void logoutShouldEndCurrentSession() {
        authService.login("admin", "admin123");

        authService.logout();

        assertFalse(authService.isLoggedIn());
    }
}