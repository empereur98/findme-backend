package com.dhi.findme_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends Auditable {

    public User() {
    }

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "default_location", length = 255)
    private String defaultLocation;

    @Column(name = "verified", nullable = false)
    private Boolean verified = false;

    @Column(name = "role", nullable = false, length = 20)
    private String role = "user";

    @Column(name = "addresses_created_count", nullable = false)
    private Integer addressesCreatedCount = 0;

    @Column(name = "max_addresses", nullable = false)
    private Integer maxAddresses = 4;

    @Column(name = "plan", nullable = false, length = 20)
    private String plan = "free";

    @Column(name = "registration_date")
    private java.time.LocalDate registrationDate;

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getDefaultLocation() { return defaultLocation; }
    public void setDefaultLocation(String defaultLocation) { this.defaultLocation = defaultLocation; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getAddressesCreatedCount() { return addressesCreatedCount; }
    public void setAddressesCreatedCount(Integer addressesCreatedCount) { this.addressesCreatedCount = addressesCreatedCount; }

    public Integer getMaxAddresses() { return maxAddresses; }
    public void setMaxAddresses(Integer maxAddresses) { this.maxAddresses = maxAddresses; }

    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }

    public java.time.LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(java.time.LocalDate registrationDate) { this.registrationDate = registrationDate; }
}
