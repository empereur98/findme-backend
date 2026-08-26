package com.dhi.findme_backend.service;

import com.dhi.findme_backend.dto.SupportAttachmentUploadResponse;
import com.dhi.findme_backend.dto.SupportTicketCreateRequest;
import com.dhi.findme_backend.dto.SupportTicketResponse;
import com.dhi.findme_backend.dto.SupportTicketTriageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface SupportTicketService {

    SupportTicketResponse createTicket(SupportTicketCreateRequest request, UUID userId);

    Page<SupportTicketResponse> getTickets(Pageable pageable, String search, String status, String type);

    SupportTicketResponse triageTicket(UUID ticketId, SupportTicketTriageRequest request);

    SupportAttachmentUploadResponse uploadSupportAttachment(MultipartFile file);
}