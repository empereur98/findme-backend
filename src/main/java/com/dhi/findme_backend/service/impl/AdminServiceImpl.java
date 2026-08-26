package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.AdminStatsResponse;
import com.dhi.findme_backend.repository.SupportTicketRepository;
import com.dhi.findme_backend.repository.UserRepository;
import com.dhi.findme_backend.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private static final int NEW_USERS_DAYS_PERIOD = 30;

    private final UserRepository userRepository;
    private final SupportTicketRepository supportTicketRepository;

    public AdminServiceImpl(UserRepository userRepository, SupportTicketRepository supportTicketRepository) {
        this.userRepository = userRepository;
        this.supportTicketRepository = supportTicketRepository;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public AdminStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long newUsers = userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(NEW_USERS_DAYS_PERIOD));
        long openTickets = supportTicketRepository.countByStatus("Non traité");
        
        // Le revenu total est une valeur factice pour l'instant
        BigDecimal totalRevenue = new BigDecimal("1234.56");

        return new AdminStatsResponse(totalUsers, newUsers, openTickets, totalRevenue);
    }
}