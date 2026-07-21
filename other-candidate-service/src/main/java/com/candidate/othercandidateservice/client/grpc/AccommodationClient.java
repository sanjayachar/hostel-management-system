package com.candidate.othercandidateservice.client.grpc;

import com.candidate.othercandidateservice.dto.AccommodationRequestDto;
import com.candidate.othercandidateservice.mapper.AccommodationMapper;
import com.hostel.proto.accommodation.*;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccommodationClient {

    private final AccommodationMapper accommodationMapper;

    @GrpcClient("accommodation-service")
    private AccommodationServiceGrpc.AccommodationServiceBlockingStub stub;

    public List<AccommodationRequestDto> getRequests(String role, Long userId) {
        AccommodationRequestList requestList = AccommodationRequestList.newBuilder()
                .setRole(role)
                .setUserId(userId == null ? 0 : userId)
                .build();
        AccommodationResponseList responseList = stub.getRequests(requestList);
        return responseList.getListList().stream().map(accommodationMapper::toDto).collect(Collectors.toList());
    }

    public String saveRequest(AccommodationRequestDto dto, String token) {
        SaveAccommodationRequest request = SaveAccommodationRequest.newBuilder()
                .setRequestType(dto.getRequestType())
                .setReason(dto.getReason())
                .setFromDate(dto.getFromDate().toString())
                .setToDate(dto.getToDate().toString())
                .setNoOfDays(dto.getNoOfDays())
                .setNoOfPersons(dto.getNoOfPersons())
                .setUserId(dto.getUserId())
                .setUserRole(dto.getUserRole().name())
                .setToken(token)
                .build();
        SaveAccommodationResponse response = stub.saveRequest(request);
        return response.getMessage();
    }
}
