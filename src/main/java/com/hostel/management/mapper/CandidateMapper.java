package com.hostel.management.mapper;

import com.hostel.management.dto.CandidateDto;
import com.hostel.management.dto.StaffDto;
import com.hostel.management.modal.Candidate;
import com.hostel.management.modal.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    @Mapping(target = ".", source = ".")
    Candidate toEntity(CandidateDto dto);

    @Mapping(target = ".", source = ".")
    CandidateDto toDto(Candidate entity);

    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "user", ignore = true)
    Candidate toEntityForSave(CandidateDto dto);
}
