package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.AddressCreateRequest;
import com.dhi.findme_backend.dto.AddressResponse;
import com.dhi.findme_backend.dto.AddressUpdateRequest;
import com.dhi.findme_backend.entity.Address;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.exception.BusinessException;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.mapper.AddressMapper;
import com.dhi.findme_backend.repository.AddressRepository;
import com.dhi.findme_backend.repository.UserRepository;
import com.dhi.findme_backend.repository.specification.AddressSpecification;
import com.dhi.findme_backend.service.AddressService;
import com.google.openlocationcode.OpenLocationCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressServiceImpl.class);
    private static final int MIN_CODE_DIGITS = 1000;
    private static final int MAX_CODE_DIGITS = 9000;

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.addressMapper = addressMapper;
    }

    @Override
    public AddressResponse createAddress(AddressCreateRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));

        if (user.getAddressesCreatedCount() >= user.getMaxAddresses()) {
            throw new BusinessException("ADDRESS_LIMIT_REACHED", "Limite du plan atteinte");
        }

        String typeCode = "Personnel".equals(request.type()) ? "XX" : "YY";
        String code = generateAddressCode(typeCode);
        String codePlus = new OpenLocationCode(request.gpsLat(), request.gpsLng()).getCode();

        Address address = new Address(request.name(), user);
        address.setCode(code);
        address.setName(request.name());
        address.setCountry(request.country());
        address.setCity(request.city());
        address.setDistrict(request.district());
        address.setStreet(request.street());
        address.setLandmark(request.landmark());
        address.setGpsLat(request.gpsLat());
        address.setGpsLng(request.gpsLng());
        address.setImageFacade(request.imageFacade());
        address.setCodePlus(codePlus);
        address.setOwnerName(user.getFirstName() + " " + user.getLastName());
        address.setOwnerEmail(user.getEmail());
        address.setStatus("En cours");
        address.setAddressDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        address.setType(request.type());
        address.setUser(user);

        address = addressRepository.save(address);

        user.setAddressesCreatedCount(user.getAddressesCreatedCount() + 1);
        userRepository.save(user);

        return addressMapper.toAddressResponse(address);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AddressResponse> getAddresses(Pageable pageable, String search, String country, String city, String type, String status, UUID userId) {
        LOGGER.info("getAddresses appelé avec userId: {}", userId);
        User user = (userId != null) ? userRepository.findById(userId).orElse(null) : null;
        if (user != null) {
            LOGGER.info("Utilisateur trouvé: {} (email: {})", user.getId(), user.getEmail());
        }
        Specification<Address> spec = AddressSpecification.filterBy(search, country, city, type, status, user);
        Page<Address> addresses = addressRepository.findAll(spec, pageable);
        LOGGER.info("Nombre d'adresses trouvées: {}", addresses.getTotalElements());
        return addresses.map(addressMapper::toAddressResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(UUID addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("ADDRESS_NOT_FOUND", "Adresse introuvable"));
        return addressMapper.toAddressResponse(address);
    }

    @Override
    public AddressResponse updateAddress(UUID addressId, AddressUpdateRequest request, UUID userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("ADDRESS_NOT_FOUND", "Adresse introuvable"));

        if (!address.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier cette adresse");
        }

        if (request.name() != null) {
            address.setName(request.name());
        }
        if (request.district() != null) {
            address.setDistrict(request.district());
        }
        if (request.street() != null) {
            address.setStreet(request.street());
        }
        if (request.landmark() != null) {
            address.setLandmark(request.landmark());
        }
        if (request.gpsLat() != null && request.gpsLng() != null) {
            address.setGpsLat(request.gpsLat());
            address.setGpsLng(request.gpsLng());
            address.setCodePlus(new OpenLocationCode(request.gpsLat(), request.gpsLng()).getCode());
        }
        if (request.imageFacade() != null) {
            address.setImageFacade(request.imageFacade());
        }
        if (request.type() != null) {
            address.setType(request.type());
        }

        address = addressRepository.save(address);
        return addressMapper.toAddressResponse(address);
    }

    @Override
    public void deleteAddress(UUID addressId, UUID userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("ADDRESS_NOT_FOUND", "Adresse introuvable"));

        if (!address.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à supprimer cette adresse");
        }

        addressRepository.delete(address);

        // Décrémenter le compteur d'adresses de l'utilisateur
        User user = address.getUser();
        if (user.getAddressesCreatedCount() > 0) {
            user.setAddressesCreatedCount(user.getAddressesCreatedCount() - 1);
            userRepository.save(user);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public AddressResponse verifyAddress(UUID addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("ADDRESS_NOT_FOUND", "Adresse introuvable"));
        address.setStatus("Vérifié");
        address = addressRepository.save(address);
        return addressMapper.toAddressResponse(address);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse lookupByCodePlus(String codePlus) {
        Address address = addressRepository.findByCodePlus(codePlus)
                .orElseThrow(() -> new ResourceNotFoundException("ADDRESS_NOT_FOUND", "Adresse introuvable"));
        return addressMapper.toAddressResponse(address);
    }

    private String generateAddressCode(String typeCode) {
        Random random = new Random();
        int digits = MIN_CODE_DIGITS + random.nextInt(MAX_CODE_DIGITS);
        return "ADR-" + digits + "-" + typeCode;
    }
}