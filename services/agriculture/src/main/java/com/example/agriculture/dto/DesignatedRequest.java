package com.example.agriculture.dto;

import com.example.agriculture.enums.DesignatedType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DesignatedRequest {

    @NotBlank(message = "Username must not be empty")
    @JsonProperty("username")
    private String username;

    @NotNull(message = "Type must not be null")
    @JsonProperty("type")
    private DesignatedType type;
}
