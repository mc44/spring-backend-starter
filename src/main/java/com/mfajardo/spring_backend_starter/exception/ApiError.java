package com.mfajardo.spring_backend_starter.exception;

import java.time.Instant;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String traceId
) {}

