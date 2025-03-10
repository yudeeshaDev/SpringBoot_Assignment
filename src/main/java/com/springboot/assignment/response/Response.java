package com.springboot.assignment.response;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class Response {

    //method for success response
    public static ResponseEntity<Map<String, Object>> success(Object data, String message, HttpStatus status){
        Map<String, Object> response = new HashMap<>();
        response.put("code", status.value());
        response.put("status", status);
        response.put("message", message);
        response.put("data", data);

        return ResponseEntity.status(status).body(response);
    }

    // Method for error responses
    public static ResponseEntity<Map<String, Object>> error(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", status.value());
        response.put("status", status);
        response.put("message", message);
        response.put("data", null);

        return ResponseEntity.status(status).body(response);
    }

}
