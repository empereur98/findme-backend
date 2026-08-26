package com.dhi.findme_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notifications")
public class Notification extends Auditable {

    public Notification() {
    }

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "read", nullable = false)
    private Boolean read = false;

    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Boolean getRead() { return read; }
    public void setRead(Boolean read) { this.read = read; }

    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
