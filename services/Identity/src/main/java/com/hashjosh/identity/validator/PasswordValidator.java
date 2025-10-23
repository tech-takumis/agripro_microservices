package com.hashjosh.identity.validator;

import com.hashjosh.identity.exception.ApiException;
import com.hashjosh.identity.exception.PasswordValidationException;
import com.vladmihalcea.hibernate.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class PasswordValidator {
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    private static final Pattern HAS_UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern HAS_LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern HAS_NUMBER = Pattern.compile("\\d");
    private static final Pattern HAS_SPECIAL_CHAR = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
    private static final Pattern COMMON_PATTERNS = Pattern.compile(
            "(?i)(password|123456|qwerty|admin|letmein|welcome|abc123)"
    );

    public void validate(String password) {
        List<String> violations = new ArrayList<>();

        // Check null or empty
        if (StringUtils.isBlank(password)) {
            throw ApiException.badRequest("Password cannot be empty");
        }

        // Check length
        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            violations.add(String.format("Password must be between %d and %d characters", MIN_LENGTH, MAX_LENGTH));
        }

        // Check for uppercase letters
        if (!HAS_UPPERCASE.matcher(password).find()) {
            violations.add("Password must contain at least one uppercase letter");
        }

        // Check for lowercase letters
        if (!HAS_LOWERCASE.matcher(password).find()) {
            violations.add("Password must contain at least one lowercase letter");
        }

        // Check for numbers
        if (!HAS_NUMBER.matcher(password).find()) {
            violations.add("Password must contain at least one number");
        }

        // Check for special characters
        if (!HAS_SPECIAL_CHAR.matcher(password).find()) {
            violations.add("Password must contain at least one special character");
        }

        // Check for common patterns
        if (COMMON_PATTERNS.matcher(password).find()) {
            violations.add("Password contains a common or easily guessable pattern");
        }

        // Check for repeating characters
        if (hasRepeatingCharacters(password)) {
            violations.add("Password cannot contain repeating characters more than 3 times");
        }

        // Check for sequential characters
        if (hasSequentialCharacters(password)) {
            violations.add("Password cannot contain sequential characters");
        }

        if (!violations.isEmpty()) {
            throw new PasswordValidationException(violations);
        }
    }

    public boolean isValid(String password) {
        try {
            validate(password);
            return true;
        } catch (PasswordValidationException | ApiException e) {
            return false;
        }
    }


    private boolean hasRepeatingCharacters(String password) {
        for (int i = 0; i < password.length() - 3; i++) {
            char c = password.charAt(i);
            if (c == password.charAt(i + 1) &&
                    c == password.charAt(i + 2) &&
                    c == password.charAt(i + 3)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSequentialCharacters(String password) {
        String lowerPass = password.toLowerCase();
        String sequences = "abcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < sequences.length() - 3; i++) {
            String forward = sequences.substring(i, i + 4);
            String reverse = new StringBuilder(forward).reverse().toString();

            if (lowerPass.contains(forward) || lowerPass.contains(reverse)) {
                return true;
            }
        }
        return false;
    }
}

