package com.dhi.findme_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class Address extends Auditable {

    public Address() {
    }

    public Address(String name, User user) {
        this.name = name;
        this.user = user;
    }

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "country", nullable = false, length = 5)
    private String country;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Column(name = "street", length = 255)
    private String street;

    @Column(name = "landmark", nullable = false, length = 500)
    private String landmark;

    @Column(name = "gps_lat", nullable = false)
    private Double gpsLat;

    @Column(name = "gps_lng", nullable = false)
    private Double gpsLng;

    @Column(name = "image_facade", columnDefinition = "TEXT")
    private String imageFacade;

    @Column(name = "code_plus", nullable = false, length = 20)
    private String codePlus;

    @Column(name = "owner_name", nullable = false, length = 100)
    private String ownerName;

    @Column(name = "owner_email", nullable = false, length = 255)
    private String ownerEmail;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "En cours";

    @Column(name = "address_date", nullable = false, length = 20)
    private String addressDate;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }

    public Double getGpsLat() { return gpsLat; }
    public void setGpsLat(Double gpsLat) { this.gpsLat = gpsLat; }

    public Double getGpsLng() { return gpsLng; }
    public void setGpsLng(Double gpsLng) { this.gpsLng = gpsLng; }

    public String getImageFacade() { return imageFacade; }
    public void setImageFacade(String imageFacade) { this.imageFacade = imageFacade; }

    public String getCodePlus() { return codePlus; }
    public void setCodePlus(String codePlus) { this.codePlus = codePlus; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAddressDate() { return addressDate; }
    public void setAddressDate(String addressDate) { this.addressDate = addressDate; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
