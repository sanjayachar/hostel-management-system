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
        List<AccommodationRequestDto> list = request.getUserId() > 0
                ? accommodationService.getRequestListByUser(roleEnum, request.getUserId())
                : accommodationService.getRequetList(roleEnum);
        AccommodationResponseList.Builder responseBuilder = AccommodationResponseList.newBuilder();
        for (AccommodationRequestDto dto : list) {
            AccommodationResponse.Builder response = AccommodationResponse.newBuilder()
                    .setRequestId(dto.getRequestId())
                    .setRequestType(dto.getRequestType() == null ? "" : dto.getRequestType())
                    .setReason(dto.getReason() == null ? "" : dto.getReason())
                    .setStatus(dto.getStatus() == null ? "" : dto.getStatus())
                    .setFromDate(String.valueOf(dto.getFromDate()))
                    .setToDate(String.valueOf(dto.getToDate()))
                    .setNoOfDays(dto.getNoOfDays())
                    .setNoOfPersons(dto.getNoOfPersons())
                    .setUserRole(dto.getUserRole() == null ? "" : dto.getUserRole().name())
                    .setDecisionNote(dto.getDecisionNote() == null ? "" : dto.getDecisionNote())
                    .setRequesterCode(dto.getRequesterCode() == null ? "" : dto.getRequesterCode())
                    .setRequesterName(dto.getRequesterName() == null ? "" : dto.getRequesterName())
                    .setHostelName(dto.getHostelName() == null ? "" : dto.getHostelName())
                    .setRoomNumber(dto.getRoomNumber() == null ? "" : dto.getRoomNumber())
                    .setBedNumber(dto.getBedNumber() == null ? "" : dto.getBedNumber())
                    .setAllocationStatus(dto.getAllocationStatus() == null ? "" : dto.getAllocationStatus());

            if (dto.getUserId() != null) {
                response.setUserId(dto.getUserId());
            }
            if (dto.getAllocationId() != null) {
                response.setAllocationId(dto.getAllocationId());
            }
            if (dto.getHostelId() != null) {
                response.setHostelId(dto.getHostelId());
            }
            if (dto.getRoomId() != null) {
                response.setRoomId(dto.getRoomId());
            }

            responseBuilder.addList(response.build());
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
