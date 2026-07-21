package com.candidate.othercandidateservice.common.util;

public class AuthUrl {
    public static final String CREATE_USER_URL = "http://localhost:8081/auth-service-api/auth/internal/create-user";
    public static final String GET_ACCOMMODATION_REQ_LIST_URL = "http://localhost:8082/accomm-service-api/candidate/request/list?role={role}";
    public static final String SAVE_ACCOMMODATION_REQ_LIST_URL = "http://localhost:8082/accomm-service-api/candidate/request";
}
