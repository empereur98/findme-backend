package com.dhi.findme_backend.mapper;

import com.dhi.findme_backend.dto.SettingsRequest;
import com.dhi.findme_backend.dto.SettingsResponse;
import com.dhi.findme_backend.entity.Settings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SettingsMapper {

    SettingsResponse toSettingsResponse(Settings entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Settings toSettingsEntity(SettingsRequest request);
}