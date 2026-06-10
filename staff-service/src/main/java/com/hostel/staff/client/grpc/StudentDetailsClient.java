package com.hostel.staff.client.grpc;

import com.hostel.proto.accommodation.*;
import com.hostel.proto.student_details.*;
import com.hostel.staff.dto.AccommodationRequestDto;
import com.hostel.staff.dto.StudentsDto;
import com.hostel.staff.mapper.StudentMapper;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StudentDetailsClient {

    private final StudentMapper studentMapper;

    @GrpcClient("student-service")
    private StudentDetailsServiceGrpc.StudentDetailsServiceBlockingStub stub;

    public List<StudentsDto> getRequests(String role) {
        StudentDetailsRequestList requestList = StudentDetailsRequestList.newBuilder().setRole(role).build();
        StudentDetailsResponseList responseList = stub.getRequests(requestList);
        return responseList.getListList().stream().map(studentMapper::fromStudentDetailsResponse).collect(Collectors.toList());
    }

    public StudentsDto getRequest(String role, Long studentId) {
        StudentDetailsRequest request = StudentDetailsRequest.newBuilder().setRole(role).setStudentId(studentId).build();
        StudentDetailsResponse response = stub.getRequest(request);
        return studentMapper.fromStudentDetailsResponse(response);
    }
}
