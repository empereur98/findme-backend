package com.dhi.findme_backend.repository;

import com.dhi.findme_backend.entity.Notification;
import com.dhi.findme_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUser(User user, Pageable pageable);

    Page<Notification> findByUserAndRead(User user, Boolean read, Pageable pageable);

    Optional<Notification> findByIdAndUser(UUID id, User user);
}
