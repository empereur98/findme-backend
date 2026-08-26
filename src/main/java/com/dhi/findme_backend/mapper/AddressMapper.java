package com.dhi.findme_backend.mapper;

import com.dhi.findme_backend.dto.AddressResponse;
import com.dhi.findme_backend.dto.GpsCoordinates;
import com.dhi.findme_backend.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "district", source = "district")
    @Mapping(target = "street", source = "street")
    @Mapping(target = "landmark", source = "landmark")
    @Mapping(target = "gps", expression = "java(new GpsCoordinates(entity.getGpsLat(), entity.getGpsLng()))")
    @Mapping(target = "imageFacade", source = "imageFacade")
    @Mapping(target = "codePlus", source = "codePlus")
    @Mapping(target = "ownerName", source = "ownerName")
    @Mapping(target = "ownerEmail", source = "ownerEmail")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "date", source = "addressDate")
    @Mapping(target = "type", source = "type")
    AddressResponse toAddressResponse(Address entity);
}