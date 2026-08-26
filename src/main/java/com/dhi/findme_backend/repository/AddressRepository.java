package com.dhi.findme_backend.repository;

import com.dhi.findme_backend.entity.Address;
import com.dhi.findme_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID>, JpaSpecificationExecutor<Address> {

    Optional<Address> findByCode(String code);

    Optional<Address> findByCodePlus(String codePlus);

    Page<Address> findByUser(User user, Pageable pageable);

    long countByUser(User user);
}
