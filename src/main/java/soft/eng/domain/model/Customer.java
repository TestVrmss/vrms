package soft.eng.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Customer {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final String id;

    private final String fullName;

    private final String email;

    private final int age;

    
    private final boolean specialLicense;

  
    public Customer(String id, String fullName, String email, int age, boolean specialLicense) {
        this.id = requireText(id, "id");
        this.fullName = requireText(fullName, "fullName");
        this.email = validateEmail(email);
        if (age < 0 || age > 120) {
            throw new IllegalArgumentException("age must be between 0 and 120");
        }
        this.age = age;
        this.specialLicense = specialLicense;
    }

    
    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public boolean hasSpecialLicense() {
        return specialLicense;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    private static String validateEmail(String value) {
        String emailValue = requireText(value, "email");
        if (!EMAIL_PATTERN.matcher(emailValue).matches()) {
            throw new IllegalArgumentException("email format is invalid");
        }
        return emailValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Customer customer)) {
            return false;
        }
        return id.equals(customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Customer{id='" + id + "', fullName='" + fullName + "', email='" + email + "'}";
    }
}
