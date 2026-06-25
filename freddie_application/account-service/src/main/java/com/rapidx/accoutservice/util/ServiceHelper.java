package com.rapidx.accoutservice.util;

import org.springframework.http.HttpStatus;
import com.rapidx.accoutservice.dto.ResponseStatusDTO;

public class ServiceHelper {
    public static ResponseStatusDTO setRespSts(HttpStatus status, String message) {
        return new ResponseStatusDTO(status.value(), message);
    }
}
