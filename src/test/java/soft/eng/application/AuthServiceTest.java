package soft.eng.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import soft.eng.persistence.InMemoryManagerRepository;

/**
 * Unit tests for AuthService.
 */
class AuthServiceTest {

    /**
     * The authentication service under test.
     */
    private AuthService authService;

    /**
     * Initializes test dependencies.
     */
    @BeforeEach
    void setUp() {
        authService = new AuthService(new InMemoryManagerRepository());
    }

    /**
     * Tests login with valid credentials.
     */
    @Test
    void loginWithValidCredentialsShouldSucceed() {
        boolean result = authService.login("admin", "admin123");

        assertTrue(result);
        assertTrue(authService.isLoggedIn());
    }

    /**
     * Tests login with invalid credentials.
     */
    @Test
    void loginWithInvalidCredentialsShouldFail() {
        boolean result = authService.login("admin", "wrong-password");

        assertFalse(result);
        assertFalse(authService.isLoggedIn());
    }

    /**
     * Tests logout.
     */
    @Test
    void logoutShouldEndCurrentSession() {
        authService.login("admin", "admin123");

        authService.logout();

        assertFalse(authService.isLoggedIn());
    }
}