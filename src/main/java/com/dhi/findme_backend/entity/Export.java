package com.dhi.findme_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "exports")
public class Export extends Auditable {

    public Export() {
    }

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    @Column(name = "download_url", nullable = false, length = 500)
    private String downloadUrl;

    @Column(name = "expires_at", nullable = false)
    private java.time.LocalDateTime expiresAt;

    @Column(name = "size", nullable = false, length = 50)
    private String size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public java.time.LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(java.time.LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}
