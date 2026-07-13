package soft.eng.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soft.eng.domain.model.Manager;
import soft.eng.persistence.ManagerRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private ManagerRepository managerRepository;

    private AuthService service;


    @BeforeEach
    void setUp() {
        service = new AuthService(managerRepository);
    }


    @Test
    void validCredentialsLoginSuccessfully() {
        Manager manager = new Manager("manager", "secret");
        when(managerRepository.findByUsername("manager")).thenReturn(Optional.of(manager));

        assertTrue(service.login(" manager ", "secret"));
        assertTrue(service.isLoggedIn());
        assertEquals(manager, service.getCurrentManager().orElseThrow());
        verify(managerRepository).findByUsername("manager");
    }


    @Test
    void invalidCredentialsAreRejected() {
        Manager manager = new Manager("manager", "secret");
        when(managerRepository.findByUsername("manager")).thenReturn(Optional.of(manager));
        assertTrue(service.login("manager", "secret"));

        assertFalse(service.login("manager", "wrong"));
        assertFalse(service.isLoggedIn());
        assertTrue(service.getCurrentManager().isEmpty());
    }


    @Test
    void missingAndNullCredentialsAreRejected() {
        when(managerRepository.findByUsername("nobody")).thenReturn(Optional.empty());
        assertFalse(service.login("nobody", "x"));
        assertFalse(service.login(null, "x"));
        assertFalse(service.login("manager", null));
    }


    @Test
    void logoutRequiresRelogin() {
        Manager manager = new Manager("manager", "secret");
        when(managerRepository.findByUsername("manager")).thenReturn(Optional.of(manager));
        service.login("manager", "secret");
        service.requireAuthenticated();
        service.logout();
        assertThrows(IllegalStateException.class, service::requireAuthenticated);
    }


    @Test
    void constructorRejectsNullRepository() {
        assertThrows(NullPointerException.class, () -> new AuthService(null));
    }
}
