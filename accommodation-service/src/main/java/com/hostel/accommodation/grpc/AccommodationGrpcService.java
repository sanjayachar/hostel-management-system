package com.hostel.accommodation.grpc;

import com.hostel.accommodation.common.enums.RoleEnum;
import com.hostel.accommodation.dto.AccommodationRequestDto;
import com.hostel.accommodation.service.AccommodationService;
import com.hostel.proto.accommodation.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDate;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class AccommodationGrpcService extends AccommodationServiceGrpc.AccommodationServiceImplBase {
    private final AccommodationService accommodationService;

    @Override
    public void getRequests(AccommodationRequestList request, StreamObserver<AccommodationResponseList> responseObserver) {
        RoleEnum roleEnum = RoleEnum.valueOf(request.getRole());
        List<AccommodationRequestDto> list = accommodationService.getRequetList(roleEnum);
        AccommodationResponseList.Builder responseBuilder = AccommodationResponseList.newBuilder();
        for (AccommodationRequestDto dto : list) {
            AccommodationResponse response = AccommodationResponse.newBuilder()
                    .setRequestId(dto.getRequestId())
                    .setReason(dto.getReason())
                    .setStatus(dto.getStatus())
                    .setFromDate(String.valueOf(dto.getFromDate()))
                    .setToDate(String.valueOf(dto.getToDate()))
                    .setNoOfDays(dto.getNoOfDays())
                    .setNoOfPersons(dto.getNoOfPersons())
                    .setUserRole(dto.getUserRole().name())
                    .build();
            responseBuilder.addList(response);
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void saveRequest(SaveAccommodationRequest request, StreamObserver<SaveAccommodationResponse> responseObserver) {
        AccommodationRequestDto dto = new AccommodationRequestDto();
        dto.setRequestType(request.getRequestType());
        dto.setReason(request.getReason());
        dto.setFromDate(LocalDate.parse(request.getFromDate()));
        dto.setToDate(LocalDate.parse(request.getToDate()));
        dto.setNoOfDays(request.getNoOfDays());
        dto.setNoOfPersons(request.getNoOfPersons());
        dto.setUserId(request.getUserId());
        dto.setUserRole(RoleEnum.valueOf(request.getUserRole()));
        accommodationService.saveOrUpdateAccommodation(dto, request.getToken());
        SaveAccommodationResponse response = SaveAccommodationResponse.newBuilder().setMessage("Saved Successfully").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
