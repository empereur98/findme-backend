package com.dhi.findme_backend.mapper;

import com.dhi.findme_backend.dto.InvoiceResponse;
import com.dhi.findme_backend.entity.Invoice;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    InvoiceResponse toInvoiceResponse(Invoice invoice);
}