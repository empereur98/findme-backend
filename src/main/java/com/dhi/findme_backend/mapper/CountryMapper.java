package com.dhi.findme_backend.mapper;

import com.dhi.findme_backend.dto.CountryResponse;
import com.dhi.findme_backend.entity.Country;
import org.mapstruct.Mapper;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    default CountryResponse toCountryResponse(Country entity) {
        return new CountryResponse(
            entity.getCode(),
            entity.getNameFr(),
            entity.getNameEn(),
            entity.getCities() != null ? Arrays.asList(entity.getCities().split(",")) : List.of()
        );
    }
}
