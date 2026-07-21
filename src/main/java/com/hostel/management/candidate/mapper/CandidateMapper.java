package com.hostel.management.candidate.mapper;

import com.hostel.management.candidate.dto.CandidateDto;
import com.hostel.management.candidate.entity.Candidate;
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
