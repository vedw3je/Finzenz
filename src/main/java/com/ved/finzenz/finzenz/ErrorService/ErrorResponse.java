package com.ved.finzenz.finzenz.ErrorService;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ErrorResponse {

    private int status;
    private String message;
    private Object errors;
    private LocalDateTime timestamp;
}