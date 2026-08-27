package com.dhi.findme_backend.controller;

import com.dhi.findme_backend.dto.SupportAttachmentUploadResponse;
import com.dhi.findme_backend.dto.SupportTicketCreateRequest;
import com.dhi.findme_backend.dto.SupportTicketResponse;
import com.dhi.findme_backend.dto.SupportTicketTriageRequest;
import com.dhi.findme_backend.security.SecurityUtilsInterface;
import com.dhi.findme_backend.service.SupportTicketService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SupportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SupportTicketService supportTicketService;

    @Mock
    private SecurityUtilsInterface securityUtils;

    @InjectMocks
    private SupportController supportController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UUID userId;
    private UUID ticketId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(supportController)
                .setControllerAdvice(new com.dhi.findme_backend.exception.GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        userId = UUID.randomUUID();
        ticketId = UUID.randomUUID();
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
    }

    @Test
    void createTicket_withValidData_shouldSucceed() throws Exception {
        SupportTicketCreateRequest request = new SupportTicketCreateRequest("Subject", "Message", "Type", null);
        SupportTicketResponse response = new SupportTicketResponse(ticketId.toString(), null, null, null, "Subject", null, null, null, null, null, null);
        when(supportTicketService.createTicket(any(SupportTicketCreateRequest.class), eq(userId))).thenReturn(response);

        mockMvc.perform(post("/api/support/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.subject", is("Subject")));
    }

    @Test
    void getTickets_whenCalled_shouldReturnPageOfTickets() throws Exception {
        SupportTicketResponse response = new SupportTicketResponse(ticketId.toString(), null, null, null, "Subject", null, null, null, null, null, null);
        when(supportTicketService.getTickets(any(Pageable.class), any(), any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(response), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/support/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].id", is(ticketId.toString())));
    }

    @Test
    void triageTicket_whenCalled_shouldUpdateStatus() throws Exception {
        SupportTicketTriageRequest request = new SupportTicketTriageRequest("In Progress");
        SupportTicketResponse response = new SupportTicketResponse(ticketId.toString(), null, null, null, null, null, null, "In Progress", null, null, null);
        when(supportTicketService.triageTicket(eq(ticketId), any(SupportTicketTriageRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/support/tickets/" + ticketId + "/triage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("In Progress")));
    }

    @Test
    void uploadAttachment_withValidFile_shouldSucceed() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test content".getBytes());
        SupportAttachmentUploadResponse response = new SupportAttachmentUploadResponse("http://localhost:8080/uploads/support-attachments/test.pdf", "Fichier uploadé avec succès");
        when(supportTicketService.uploadSupportAttachment(any(org.springframework.web.multipart.MultipartFile.class))).thenReturn(response);

        mockMvc.perform(multipart("/api/support/tickets/upload-attachment")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attachmentUrl", is("http://localhost:8080/uploads/support-attachments/test.pdf")));
    }
}