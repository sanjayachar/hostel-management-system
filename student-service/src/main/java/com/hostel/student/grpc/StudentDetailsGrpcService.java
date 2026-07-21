package com.hostel.student.grpc;

import com.hostel.proto.student_details.StudentDetailsRequest;
import com.hostel.proto.student_details.StudentDetailsRequestList;
import com.hostel.proto.student_details.StudentDetailsResponse;
import com.hostel.proto.student_details.StudentDetailsResponseList;
import com.hostel.proto.student_details.StudentDetailsServiceGrpc;
import com.hostel.student.dto.StudentsDto;
import com.hostel.student.service.StudentsService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class StudentDetailsGrpcService extends StudentDetailsServiceGrpc.StudentDetailsServiceImplBase {

    private final StudentsService studentsService;

    @Override
    public void getRequests(StudentDetailsRequestList request, StreamObserver<StudentDetailsResponseList> responseObserver) {
        List<StudentsDto> students = studentsService.getAllStudents();
        StudentDetailsResponseList.Builder responseBuilder = StudentDetailsResponseList.newBuilder();
        students.stream()
                .map(this::toResponse)
                .forEach(responseBuilder::addList);
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getRequest(StudentDetailsRequest request, StreamObserver<StudentDetailsResponse> responseObserver) {
        StudentsDto student = studentsService.getStudentById(request.getStudentId());
        responseObserver.onNext(toResponse(student));
        responseObserver.onCompleted();
    }

    private StudentDetailsResponse toResponse(StudentsDto dto) {
        StudentDetailsResponse.Builder builder = StudentDetailsResponse.newBuilder()
                .setStudentId(defaultLong(dto.getStudentId()))
                .setAdmissionNumber(defaultString(dto.getAdmissionNumber()))
                .setFirstName(defaultString(dto.getFirstName()))
                .setLastName(defaultString(dto.getLastName()))
                .setGender(defaultString(dto.getGender()))
                .setDateOfBirth(dto.getDateOfBirth() == null ? "" : dto.getDateOfBirth().toString())
                .setContactNumber(defaultString(dto.getContactNumber()))
                .setPersonalEmail(defaultString(dto.getPersonalEmail()))
                .setFatherName(defaultString(dto.getFatherName()))
                .setMotherName(defaultString(dto.getMotherName()))
                .setAddress(defaultString(dto.getAddress()))
                .setHostelStatus(Boolean.TRUE.equals(dto.getHostelStatus()));

        if (dto.getUserId() != null) {
            builder.setUserId(dto.getUserId());
        }
        return builder.build();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private long defaultLong(Long value) {
        return value == null ? 0L : value;
    }
}
