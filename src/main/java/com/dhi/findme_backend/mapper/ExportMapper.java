package com.dhi.findme_backend.mapper;

import com.dhi.findme_backend.dto.ExportResponse;
import com.dhi.findme_backend.entity.Export;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExportMapper {

    @Mapping(target = "id", ignore = true)
    ExportResponse toExportResponse(Export entity);
}
