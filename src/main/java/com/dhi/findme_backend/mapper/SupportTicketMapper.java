package com.dhi.findme_backend.mapper;

import com.dhi.findme_backend.dto.SupportTicketResponse;
import com.dhi.findme_backend.entity.SupportTicket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupportTicketMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", expression = "java(entity.getCreatedAt())")
    @Mapping(target = "avatarUrl", expression = "java(entity.getUser() != null ? entity.getUser().getAvatarUrl() : null)")
    @Mapping(target = "attachmentUrl", source = "attachmentUrl")
    SupportTicketResponse toSupportTicketResponse(SupportTicket entity);
}
