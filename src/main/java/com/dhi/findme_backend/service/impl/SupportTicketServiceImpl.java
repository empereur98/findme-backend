package com.dhi.findme_backend.service.impl;

import com.dhi.findme_backend.dto.SupportAttachmentUploadResponse;
import com.dhi.findme_backend.dto.SupportTicketCreateRequest;
import com.dhi.findme_backend.dto.SupportTicketResponse;
import com.dhi.findme_backend.dto.SupportTicketTriageRequest;
import com.dhi.findme_backend.entity.SupportTicket;
import com.dhi.findme_backend.entity.User;
import com.dhi.findme_backend.exception.BusinessException;
import com.dhi.findme_backend.exception.ResourceNotFoundException;
import com.dhi.findme_backend.mapper.SupportTicketMapper;
import com.dhi.findme_backend.repository.SupportTicketRepository;
import com.dhi.findme_backend.repository.UserRepository;
import com.dhi.findme_backend.security.SecurityUtilsInterface;
import com.dhi.findme_backend.service.SupportTicketService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class SupportTicketServiceImpl implements SupportTicketService {

    private static final int MAX_TICKET_CODE_DIGITS = 999;
    private static final long MAX_ATTACHMENT_SIZE_BYTES = 10 * 1024 * 1024; // 10MB

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;
    private final SupportTicketMapper supportTicketMapper;
    private final SecurityUtilsInterface securityUtils;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public SupportTicketServiceImpl(SupportTicketRepository supportTicketRepository,
                                    UserRepository userRepository,
                                    SupportTicketMapper supportTicketMapper,
                                    SecurityUtilsInterface securityUtils) {
        this.supportTicketRepository = supportTicketRepository;
        this.userRepository = userRepository;
        this.supportTicketMapper = supportTicketMapper;
        this.securityUtils = securityUtils;
    }

    @Override
    public SupportTicketResponse createTicket(SupportTicketCreateRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));

        String code = generateTicketCode();

        SupportTicket ticket = new SupportTicket();
        ticket.setCode(code);
        ticket.setUserName(user.getFirstName() + " " + user.getLastName());
        ticket.setUserEmail(user.getEmail());
        ticket.setSubject(request.subject());
        ticket.setMessage(request.message());
        ticket.setType(request.type());
        ticket.setStatus("Non traité");
        ticket.setAttachmentUrl(request.attachmentUrl());
        ticket.setUser(user);

        ticket = supportTicketRepository.save(ticket);
        return supportTicketMapper.toSupportTicketResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupportTicketResponse> getTickets(Pageable pageable, String search, String status, String type) {
        // Si admin, voir tous les tickets, sinon filtrer par utilisateur connecté
        UUID currentUserId = securityUtils.isAdmin() ? null : securityUtils.getCurrentUserId();
        
        if (currentUserId != null) {
            User user = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND", "Utilisateur non trouvé"));
            Page<SupportTicket> tickets = supportTicketRepository.findByUser(user, pageable);
            return tickets.map(supportTicketMapper::toSupportTicketResponse);
        } else {
            // Admin voit tous les tickets
            Page<SupportTicket> tickets = supportTicketRepository.findAll(pageable);
            return tickets.map(supportTicketMapper::toSupportTicketResponse);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public SupportTicketResponse triageTicket(UUID ticketId, SupportTicketTriageRequest request) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("TICKET_NOT_FOUND", "Ticket introuvable"));
        
        ticket.setStatus(request.status());
        // Ici, vous pourriez ajouter la logique pour la priorité, etc.
        
        ticket = supportTicketRepository.save(ticket);
        return supportTicketMapper.toSupportTicketResponse(ticket);
    }

    private String generateTicketCode() {
        Random random = new Random();
        int digits = 1 + random.nextInt(MAX_TICKET_CODE_DIGITS);
        return "MSG-" + String.format("%03d", digits);
    }

    @Override
    public SupportAttachmentUploadResponse uploadSupportAttachment(MultipartFile file) {
        // Valider la taille du fichier (max 10MB)
        if (file.getSize() > MAX_ATTACHMENT_SIZE_BYTES) {
            throw new BusinessException("FILE_TOO_LARGE", "La taille du fichier ne doit pas dépasser 10MB");
        }

        try {
            // Créer le répertoire d'upload s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir, "support-attachments");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            String newFilename = UUID.randomUUID() + "_" + System.currentTimeMillis() + "." + extension;
            Path filePath = uploadPath.resolve(newFilename);

            // Sauvegarder le fichier
            file.transferTo(filePath.toFile());

            // Construire l'URL publique
            String attachmentUrl = baseUrl + "/uploads/support-attachments/" + newFilename;

            return new SupportAttachmentUploadResponse(attachmentUrl, "Fichier uploadé avec succès");
        } catch (IOException e) {
            throw new BusinessException("UPLOAD_ERROR", "Erreur lors de l'upload du fichier: " + e.getMessage());
        }
    }
}