package com.dhi.findme_backend.dto;

import java.math.BigDecimal;

public record AdminStatsResponse(
    long totalUsers,
    long newUsersLast30Days,
    long openSupportTickets,
    BigDecimal totalRevenue // Pour l'instant, ce sera une valeur factice
) {}