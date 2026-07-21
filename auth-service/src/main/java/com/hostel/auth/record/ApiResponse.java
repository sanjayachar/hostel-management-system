package com.hostel.auth.record;

import java.util.Map;


public record ApiResponse (
        String status,
        String message,
        Map<String,String> errors
){
}
