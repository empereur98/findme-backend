package com.dhi.findme_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "settings")
public class Settings extends Auditable {

    public Settings() {
    }

    @Column(name = "strict_formatting", nullable = false)
    private Boolean strictFormatting = true;

    @Column(name = "auto_correct", nullable = false)
    private Boolean autoCorrect = true;

    @Column(name = "validation_threshold", nullable = false)
    private Integer validationThreshold = 85;

    @Column(name = "rate_limit", nullable = false)
    private Integer rateLimit = 1000;

    @Column(name = "webhook_timeout", nullable = false)
    private Integer webhookTimeout = 5000;

    // Getters and Setters
    public Boolean getStrictFormatting() { return strictFormatting; }
    public void setStrictFormatting(Boolean strictFormatting) { this.strictFormatting = strictFormatting; }

    public Boolean getAutoCorrect() { return autoCorrect; }
    public void setAutoCorrect(Boolean autoCorrect) { this.autoCorrect = autoCorrect; }

    public Integer getValidationThreshold() { return validationThreshold; }
    public void setValidationThreshold(Integer validationThreshold) { this.validationThreshold = validationThreshold; }

    public Integer getRateLimit() { return rateLimit; }
    public void setRateLimit(Integer rateLimit) { this.rateLimit = rateLimit; }

    public Integer getWebhookTimeout() { return webhookTimeout; }
    public void setWebhookTimeout(Integer webhookTimeout) { this.webhookTimeout = webhookTimeout; }
}
