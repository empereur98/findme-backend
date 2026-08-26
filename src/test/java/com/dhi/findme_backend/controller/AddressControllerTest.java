package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.AddressCreateRequest;
import com.dhi.findme_backend.dto.AddressResponse;
import com.dhi.findme_backend.dto.AddressUpdateRequest;
import com.dhi.findme_backend.exception.BusinessException;
import com.dhi.findme_backend.security.SecurityUtilsInterface;
import com.dhi.findme_backend.service.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AddressControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AddressService addressService;

    @Mock
    private SecurityUtilsInterface securityUtils;

    @InjectMocks
    private AddressController addressController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UUID userId;
    private UUID addressId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(addressController)
                .setControllerAdvice(new com.dhi.findme_backend.exception.GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        userId = UUID.randomUUID();
        addressId = UUID.randomUUID();
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(securityUtils.isAdmin()).thenReturn(false);
    }

    @Test
    void createAddress_withValidData_shouldSucceed() throws Exception {
        AddressCreateRequest request = new AddressCreateRequest("Home", "sn", "Dakar", "Plateau", "Rue 1", "Près du marché", 14.0, -17.0, null, "Personal");
        AddressResponse response = new AddressResponse(addressId.toString(), null, "Home", null, "Dakar", null, null, null, null, null, null, null, null, "En cours", null, "Personal");
        when(addressService.createAddress(any(AddressCreateRequest.class), eq(userId))).thenReturn(response);

        mockMvc.perform(post("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Home")));
    }

    @Test
    void createAddress_whenAddressLimitReached_shouldReturnBadRequest() throws Exception {
        AddressCreateRequest request = new AddressCreateRequest("New Address", "sn", "Dakar", "Plateau", "Rue 2", "Près de l'école", 14.7167, -17.4677, null, "Personnel");
        when(addressService.createAddress(any(AddressCreateRequest.class), eq(userId)))
                .thenThrow(new BusinessException("ADDRESS_LIMIT_REACHED", "Address limit reached"));

        mockMvc.perform(post("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("ADDRESS_LIMIT_REACHED")));
    }

    @Test
    void getAddressById_whenExists_shouldSucceed() throws Exception {
        AddressResponse response = new AddressResponse(addressId.toString(), null, "My Address", null, "City", null, null, null, null, null, null, null, null, "Status", null, null);
        when(addressService.getAddressById(addressId)).thenReturn(response);

        mockMvc.perform(get("/api/addresses/" + addressId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(addressId.toString())));
    }

    @Test
    void updateAddress_whenExists_shouldSucceed() throws Exception {
        AddressUpdateRequest request = new AddressUpdateRequest("New Name", null, "New City", null, null, null, null, null);
        AddressResponse response = new AddressResponse(addressId.toString(), null, "New Name", null, "New City", null, null, null, null, null, null, null, null, "Status", null, null);
        when(addressService.updateAddress(eq(addressId), any(AddressUpdateRequest.class), eq(userId))).thenReturn(response);

        mockMvc.perform(put("/api/addresses/" + addressId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New Name")));
    }

    @Test
    void deleteAddress_whenExists_shouldSucceed() throws Exception {
        doNothing().when(addressService).deleteAddress(eq(addressId), eq(userId));
        mockMvc.perform(delete("/api/addresses/" + addressId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAddresses_withFilters_shouldReturnFilteredResults() throws Exception {
        AddressResponse addressResponse = new AddressResponse(addressId.toString(), null, "My Address", null, "Dakar", null, null, null, null, null, null, null, null, "En cours", null, null);
        when(addressService.getAddresses(any(Pageable.class), any(), eq("sn"), eq("Dakar"), any(), eq("En cours"), eq(userId)))
                .thenReturn(new PageImpl<>(Collections.singletonList(addressResponse), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/addresses?city=Dakar&status=En cours"))
                .andExpect(status().isOk());
    }
}