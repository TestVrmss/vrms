package soft.eng.domain.model;

import java.util.Objects;

public final class Manager {

    private final String username;

    private final String password;

    public Manager(String username, String password) {
        this.username = requireText(username, "username");
        this.password = requireText(password, "password");
    }

    public String getUsername() {
        return username;
    }

   
    public boolean passwordMatches(String suppliedPassword) {
        return password.equals(suppliedPassword);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Manager manager)) {
            return false;
        }
        return username.equals(manager.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "Manager{username='" + username + "'}";
    }
}
