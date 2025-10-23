package com.hashjosh.identity.validator;

import com.hashjosh.identity.dto.RegistrationRequest;
import com.hashjosh.identity.exception.ApiException;
import com.vladmihalcea.hibernate.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationValidator {
    public final PasswordValidator passwordValidator;
    public final EmailValidator emailValidator;

    public void validate(RegistrationRequest request) {
        validateRequired(request);
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
        validateUsername(request.getUsername());
    }

    public void validateRequired(RegistrationRequest request) {
        if (StringUtils.isBlank(request.getTenantKey())) {
            throw ApiException.badRequest("Tenant key is required");
        }
        if (StringUtils.isBlank(request.getUsername())) {
            throw ApiException.badRequest("Username is required");
        }
    }

    public void validateEmail(String email) {
        if (!emailValidator.isValid(email)) {
            throw ApiException.badRequest("Invalid email format");
        }
    }

    public void validatePassword(String password) {
        if (!passwordValidator.isValid(password)) {
            throw ApiException.badRequest("Password does not meet security requirements");
        }
    }

    public void validateUsername(String username) {
        if (!username.matches("^[a-zA-Z0-9._-]{3,20}$")) {
            throw ApiException.badRequest("Invalid username format");
        }
    }
}
