package com.candidate.othercandidateservice.client;

import com.candidate.othercandidateservice.common.util.AuthUrl;
import com.candidate.othercandidateservice.dto.AccommodationRequestDto;
import com.candidate.othercandidateservice.record.CreateUserRequest;
import com.candidate.othercandidateservice.record.CreateUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ServiceCall {
    private final RestTemplate restTemplate;

    public Long createUser(String userName, String password, String role) {
        CreateUserRequest request = new CreateUserRequest(userName, password, role);
        CreateUserResponse response = restTemplate.postForObject(AuthUrl.CREATE_USER_URL, request, CreateUserResponse.class);
        return Objects.requireNonNull(response).userId();
    }

    public List<AccommodationRequestDto> getStudentsAccommodationRequest(String role) {
        ResponseEntity<AccommodationRequestDto[]> response = restTemplate.getForEntity(
                AuthUrl.GET_ACCOMMODATION_REQ_LIST_URL,
                AccommodationRequestDto[].class,
                role);
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    public ResponseEntity<?> saveAccommodationRequest(AccommodationRequestDto dto, String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AccommodationRequestDto> request = new HttpEntity<>(dto, headers);

        try {
            return restTemplate.postForEntity(AuthUrl.SAVE_ACCOMMODATION_REQ_LIST_URL, request, String.class);
        } catch (HttpClientErrorException ex) {
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(ex.getResponseBodyAsString());
        }
    }
}
