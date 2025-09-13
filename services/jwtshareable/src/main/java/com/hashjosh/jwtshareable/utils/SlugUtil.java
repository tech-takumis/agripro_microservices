package com.hashjosh.jwtshareable.utils;

import org.springframework.stereotype.Service;

@Service
public class SlugUtil {

    public  String toSlug(String input) {
        if (input == null) {
            return "";
        }

        // Convert to lowercase, trim whitespace
        String slug = input.trim().toLowerCase();

        // Replace special characters with empty string
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");

        // Replace spaces and consecutive dashes with single dash
        slug = slug.replaceAll("[\\s-]+", "-");

        // Remove leading or trailing dashes
        slug = slug.replaceAll("^-|-$", "");

        return slug;
    }
}
