package com.hashjosh.application.utils;

import com.hashjosh.application.dto.SuccessResponse;
import org.springframework.stereotype.Component;

@Component
public class ResponseUtils {


    public SuccessResponse getSuccessResponse(int statusCode,String message, Object data) {
        return   new SuccessResponse(
                statusCode,
                message,
                data
        );
    }
}
