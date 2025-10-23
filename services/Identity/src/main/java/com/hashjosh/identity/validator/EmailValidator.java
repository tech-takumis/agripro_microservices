package com.hashjosh.identity.validator;

import com.hashjosh.identity.exception.ApiException;
import com.hashjosh.identity.exception.EmailValidationException;
import com.vladmihalcea.hibernate.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class EmailValidator {
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public static final Set<String> DISPOSABLE_DOMAINS = Set.of(
            "tempmail.com", "throwawaymail.com", "mailinator.com"
            // Add more disposable email domains as needed
    );

    public static final int MAX_LOCAL_PART_LENGTH = 64;
    public static final int MAX_DOMAIN_PART_LENGTH = 255;



    public void validate(String email) {
        List<String> violations = new ArrayList<>();

        // Check null or empty
        if (StringUtils.isBlank(email)) {
            throw ApiException.badRequest("Email cannot be empty");
        }

        // Basic format check
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            violations.add("Invalid email format");
        }

        // Check lengths
        String[] parts = email.split("@");
        if (parts.length == 2) {
            String localPart = parts[0];
            String domain = parts[1];

            if (localPart.length() > MAX_LOCAL_PART_LENGTH) {
                violations.add("Local part of email exceeds maximum length of 64 characters");
            }

            if (domain.length() > MAX_DOMAIN_PART_LENGTH) {
                violations.add("Domain part of email exceeds maximum length of 255 characters");
            }

            // Check for disposable email domains
            if (DISPOSABLE_DOMAINS.contains(domain.toLowerCase())) {
                violations.add("Disposable email addresses are not allowed");
            }

            // Check for valid domain format
            if (!isValidDomain(domain)) {
                violations.add("Invalid email domain format");
            }

            // Check local part format
            if (!isValidLocalPart(localPart)) {
                violations.add("Invalid email local part format");
            }
        }

        if (!violations.isEmpty()) {
            throw new EmailValidationException(violations);
        }
    }

    public boolean isValid(String email) {
        try {
            validate(email);
            return true;
        } catch (EmailValidationException | ApiException e) {
            return false;
        }
    }
    public boolean isValidDomain(String domain) {
        // Check for valid domain format
        if (domain.startsWith(".") || domain.endsWith(".")) {
            return false;
        }

        // Check for consecutive dots
        if (domain.contains("..")) {
            return false;
        }

        // Check each part of the domain
        String[] parts = domain.split("\\.");
        for (String part : parts) {
            if (part.isEmpty() || !part.matches("^[a-zA-Z0-9-]+$")) {
                return false;
            }
            if (part.startsWith("-") || part.endsWith("-")) {
                return false;
            }
        }

        return true;
    }

    public boolean isValidLocalPart(String localPart) {
        // Check for valid characters in local part
        if (!localPart.matches("^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~.-]+$")) {
            return false;
        }

        // Check for dots at start or end
        if (localPart.startsWith(".") || localPart.endsWith(".")) {
            return false;
        }

        // Check for consecutive dots
        if (localPart.contains("..")) {
            return false;
        }

        return true;
    }
}

