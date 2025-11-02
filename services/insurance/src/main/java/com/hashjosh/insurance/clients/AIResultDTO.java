package com.hashjosh.insurance.clients;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AIResultDTO {

    private int id;
    private String result;
    private String prediction;
    private String accuracy;
}
