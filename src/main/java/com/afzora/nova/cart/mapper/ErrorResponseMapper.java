package com.afzora.nova.cart.mapper;

import com.afzora.nova.cart.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ErrorResponseMapper {

  private ErrorResponseMapper(){}

    public static ErrorResponse buildErrorResponse(HttpStatus httpStatus, String message,
                                            HttpServletRequest request)
    {
        return  ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(httpStatus.value())
                .error(httpStatus.name())
                .message(message)
                .path(request.getRequestURI())
                .build();
    }
}
