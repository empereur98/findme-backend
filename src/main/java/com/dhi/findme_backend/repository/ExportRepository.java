package com.dhi.findme_backend.repository;

import com.dhi.findme_backend.entity.Export;
import com.dhi.findme_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExportRepository extends JpaRepository<Export, UUID> {

    Optional<Export> findByCode(String code);

    Page<Export> findByUser(User user, Pageable pageable);

    Page<Export> findAll(Pageable pageable);
}
