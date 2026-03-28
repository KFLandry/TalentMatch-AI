package org.talentmatch_ai.dto;

import org.talentmatch_ai.model.MatchingResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MatchingResultMapper {

    MatchingDto toDTO(MatchingResult matchingResult);

}
