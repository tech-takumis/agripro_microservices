package com.hashjosh.kafkacommon.application;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@Data
public class ApplicationPayload {
    UUID applicationTypeId;
    UUID userId;
    String status;
    Long version;
}
