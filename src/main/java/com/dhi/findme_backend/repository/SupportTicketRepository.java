package com.dhi.findme_backend.repository;

import com.dhi.findme_backend.entity.SupportTicket;
import com.dhi.findme_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {

    Optional<SupportTicket> findByCode(String code);

    Page<SupportTicket> findByUser(User user, Pageable pageable);

    Page<SupportTicket> findAll(Pageable pageable);

    long countByStatus(String status);
}