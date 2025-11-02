package com.hashjosh.insurance.clients;

import com.hashjosh.constant.program.dto.ScheduleRequestDto;
import com.hashjosh.constant.program.dto.ScheduleResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@Slf4j
public class ScheduleClient {

    private final RestClient restClient;
    @Value("${spring.application.name}")
    private String applicationName;

    public ScheduleClient(RestClient.Builder builder){
        this.restClient = builder
                .baseUrl("http://localhost:8070/api/v1/schedules")
                .build();
    }

    public ScheduleResponseDto createSchedule(ScheduleRequestDto request){
        try {
            return restClient.post()
                    .uri("") // baseUrl already has /api/v1/schedules
                    .header("X-Internal-Service", applicationName)
                    .body(request)
                    .exchange((req, res) -> {
                        if (res.getStatusCode().is2xxSuccessful()) {
                            return res.bodyTo(ScheduleResponseDto.class);
                        } else if (res.getStatusCode() == HttpStatus.BAD_REQUEST) {
                            throw new RuntimeException("Invalid request payload when creating schedule");
                        } else if (res.getStatusCode() == HttpStatus.UNAUTHORIZED ||
                                res.getStatusCode() == HttpStatus.FORBIDDEN) {
                            throw new RuntimeException("Unauthorized to create schedule");
                        } else if (res.getStatusCode() == HttpStatus.NOT_FOUND) {
                            throw new RuntimeException("Schedule API not found at: " + req.getURI());
                        } else if (res.getStatusCode().is5xxServerError()) {
                            throw new RuntimeException("Schedule service error: " + res.getStatusCode());
                        } else {
                            throw new RuntimeException("Unexpected response: " + res.getStatusCode());
                        }
                    });
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to call Schedule service", e);
        }
    }

}
