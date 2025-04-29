package com.snackbar.iam.domain.entity;

import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.exceptions.InvalidUserDataException;

import java.util.regex.Pattern;

/**
 * User domain entity representing a user in the system.
 * This class is framework-agnostic and follows clean architecture principles.
 */
public class User {
    private String id;
    private String name;
    private String email;
    private String cpf;
    private IamRole role;
    private String password;

    // Constructor with validation
    public User(String id, String name, String email, String cpf, IamRole role, String password) {
        validateUser(name, email, cpf, role, password);
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.role = role;
        this.password = password;
    }

    // Business rules for user validation
    private static void validateUser(String name, String email, String cpf, IamRole role, String password) {
        StringBuilder errors = new StringBuilder();

        if (name == null || name.trim().isEmpty()) {
            errors.append("Name cannot be empty. ");
        }

        if (email == null || email.trim().isEmpty()) {
            errors.append("Email cannot be empty. ");
        } else if (!isValidEmail(email)) {
            errors.append("Invalid email format. ");
        }

        if (cpf == null || cpf.trim().isEmpty()) {
            errors.append("CPF cannot be empty. ");
        } else if (!isValidCpf(cpf)) {
            errors.append("Invalid CPF format. ");
        }

        if (role == null) {
            errors.append("Role cannot be null. ");
        }

        if (password == null || password.trim().isEmpty()) {
            errors.append("Password cannot be empty. ");
        } else if (password.length() < 8) {
            errors.append("Password must be at least 8 characters long. ");
        }

        if (errors.length() > 0) {
            throw new InvalidUserDataException(errors.toString());
        }
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private static boolean isValidCpf(String cpf) {
        // Remove non-numeric characters
        cpf = cpf.replaceAll("[^0-9]", "");
        
        // CPF must have 11 digits
        if (cpf.length() != 11) {
            return false;
        }

        // Check if all digits are the same (invalid CPF)
        boolean allDigitsEqual = true;
        for (int i = 1; i < cpf.length(); i++) {
            if (cpf.charAt(i) != cpf.charAt(0)) {
                allDigitsEqual = false;
                break;
            }
        }
        if (allDigitsEqual) {
            return false;
        }

        // Validate CPF using the verification algorithm
        // First verification digit
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }
        int remainder = sum % 11;
        int firstVerificationDigit = (remainder < 2) ? 0 : 11 - remainder;
        
        if ((cpf.charAt(9) - '0') != firstVerificationDigit) {
            return false;
        }
        
        // Second verification digit
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }
        remainder = sum % 11;
        int secondVerificationDigit = (remainder < 2) ? 0 : 11 - remainder;
        
        return (cpf.charAt(10) - '0') == secondVerificationDigit;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public IamRole getRole() {
        return role;
    }

    public void setRole(IamRole role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
