package com.candidate.othercandidateservice.mapper;


import com.candidate.othercandidateservice.dto.CandidateDto;
import com.candidate.othercandidateservice.entity.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    @Mapping(target = ".", source = ".")
    Candidate toEntity(CandidateDto dto);

    @Mapping(target = ".", source = ".")
    CandidateDto toDto(Candidate entity);

    @Mapping(target = "candidateId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Candidate toEntityForSave(CandidateDto dto);
}
