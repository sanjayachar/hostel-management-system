package com.hostel.management.mapper;

import com.hostel.management.dto.CandidateDto;
import com.hostel.management.modal.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    @Mapping(target = ".", source = ".")
    Candidate toEntity(CandidateDto dto);

    @Mapping(target = ".", source = ".")
    CandidateDto toDto(Candidate entity);
}
