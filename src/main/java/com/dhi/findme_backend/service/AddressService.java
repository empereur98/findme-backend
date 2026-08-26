package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.AddressCreateRequest;
import com.dhi.findme_backend.dto.AddressResponse;
import com.dhi.findme_backend.dto.AddressUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AddressService {

    AddressResponse createAddress(AddressCreateRequest request, UUID userId);

    Page<AddressResponse> getAddresses(Pageable pageable, String search, String country, String city, String type, String status, UUID userId);

    AddressResponse getAddressById(UUID addressId);

    AddressResponse updateAddress(UUID addressId, AddressUpdateRequest request, UUID userId);

    void deleteAddress(UUID addressId, UUID userId);

    AddressResponse verifyAddress(UUID addressId);

    AddressResponse lookupByCodePlus(String codePlus);
}