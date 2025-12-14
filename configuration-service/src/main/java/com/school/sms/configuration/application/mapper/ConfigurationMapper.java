package com.school.sms.configuration.application.mapper;

import com.school.sms.configuration.domain.model.Configuration;
import com.school.sms.configuration.application.dto.response.ConfigurationResponse;
import com.school.sms.configuration.application.dto.response.ConfigurationPageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConfigurationMapper {

    @Mapping(target = "category", expression = "java(configuration.getCategory().name())")
    ConfigurationResponse toResponse(Configuration configuration);

    default ConfigurationPageResponse toPageResponse(Page<Configuration> page) {
        return ConfigurationPageResponse.builder()
                .content(page.getContent().stream()
                        .map(this::toResponse)
                        .toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
