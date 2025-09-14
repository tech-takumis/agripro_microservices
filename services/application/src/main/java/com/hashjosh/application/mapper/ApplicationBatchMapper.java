package com.hashjosh.application.mapper;


import com.hashjosh.application.clients.UserResponse;
import com.hashjosh.application.clients.UserServiceClient;
import com.hashjosh.application.dto.BatchApplicationResponse;
import com.hashjosh.application.dto.BatchResponse;
import com.hashjosh.application.model.Application;
import com.hashjosh.application.model.ApplicationBatch;
import com.hashjosh.application.repository.ApplicationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationBatchMapper {
    private final ApplicationTypeRepository applicationTypeRepository;
    private final UserServiceClient userClient;

    public BatchResponse toApplicationBatchResponse(ApplicationBatch save, String token) {

        List<Application> applications = save.getApplications();

        List<BatchApplicationResponse> batchApplicationResponses = new ArrayList<>(List.of());

        if (applications != null) {
            for (Application application : applications) {
                application.setApplicationType(applicationTypeRepository.findById(
                        application.getApplicationType().getId()).isPresent()
                        ? applicationTypeRepository.findById(application.getApplicationType().getId()).get()
                        : application.getApplicationType());

                UserResponse user = userClient.getUserById(application.getUserId(), token);

                batchApplicationResponses.add(BatchApplicationResponse.builder()
                                .applicationId(application.getId())
                                .fullName(user.getFirstName() + " " + user.getLastName())
                                .applicationName(application.getApplicationType().getName())
                                .dynamicFields(application.getDynamicFields())
                                .status(application.getStatus().name())
                        .build());
            }
        }

        return BatchResponse.builder()
                .batchId(save.getId())
                .name(save.getName())
                .createdBy(save.getCreatedBy())
                .applications(batchApplicationResponses) // Null since we currently create a batch before we create the applications
                .build();
    }
}
