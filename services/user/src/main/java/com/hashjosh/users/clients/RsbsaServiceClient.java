package com.hashjosh.users.clients;

import com.hashjosh.users.exception.RsbsaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class RsbsaServiceClient {

    private final RestClient restClient;

    RsbsaServiceClient(RestClient.Builder builder){
        this.restClient = builder
                .baseUrl("http://localhost:9001/api/v1/rsbsa")
                .build();
    }

    public RsbsaResponseDto getRsbsa(String rsbsaId){
        return restClient.get()
                .uri("/public/{rsbsa-id}",rsbsaId)
                .exchange((req,res) -> {
                    if (res.getStatusCode().is2xxSuccessful()) {
                        return res.bodyTo(RsbsaResponseDto.class);
                    } else if (res.getStatusCode() == HttpStatus.NOT_FOUND) {
                        throw new RsbsaException(
                                "Rsbsa Id "+rsbsaId+" not found!",
                                HttpStatus.NOT_FOUND.value()
                        );
                    }else{
                        throw  new RsbsaException(
                                "Failed to get rsbsa id " + rsbsaId + "status code: "+ res.getStatusCode(),
                                HttpStatus.BAD_REQUEST.value()
                        );
                    }});
    }
}
