package com.dhi.findme_backend.mapper;

import com.dhi.findme_backend.dto.ExportResponse;
import com.dhi.findme_backend.entity.Export;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExportMapperTest {

    private ExportMapper exportMapper;

    private Export testExport;

    @BeforeEach
    void setUp() {
        exportMapper = Mappers.getMapper(ExportMapper.class);
        testExport = new Export();
        testExport.setId(UUID.randomUUID());
        testExport.setFilename("ADREES_Maison.pdf");
        testExport.setDownloadUrl("https://cdn.adrees.africa/exports/EXP-123.pdf");
        testExport.setCreatedAt(LocalDateTime.now());
        testExport.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testToExportResponse_WithAllFields_ShouldMapCorrectly() {
        ExportResponse response = exportMapper.toExportResponse(testExport);

        assertNotNull(response);
        assertEquals(testExport.getFilename(), response.filename());
        assertEquals(testExport.getDownloadUrl(), response.downloadUrl());
    }

    @Test
    void testToExportResponse_WithNullOptionalFields_ShouldMapCorrectly() {
        testExport.setFilename(null);
        testExport.setDownloadUrl(null);

        ExportResponse response = exportMapper.toExportResponse(testExport);

        assertNotNull(response);
        assertNull(response.filename());
        assertNull(response.downloadUrl());
    }

    @Test
    void testToExportResponse_WithNullEntity_ShouldReturnNull() {
        ExportResponse response = exportMapper.toExportResponse(null);

        assertNull(response);
    }

    @Test
    void testToExportResponse_WithEmptyStrings_ShouldMapCorrectly() {
        testExport.setFilename("");
        testExport.setDownloadUrl("");

        ExportResponse response = exportMapper.toExportResponse(testExport);

        assertNotNull(response);
        assertEquals("", response.filename());
        assertEquals("", response.downloadUrl());
    }
}
