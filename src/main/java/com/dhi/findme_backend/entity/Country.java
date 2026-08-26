package com.dhi.findme_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "countries")
public class Country extends Auditable {

    public Country() {
    }

    @Column(name = "code", nullable = false, unique = true, length = 5)
    private String code;

    @Column(name = "name_fr", nullable = false, length = 100)
    private String nameFr;

    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;

    @Column(name = "cities", columnDefinition = "TEXT")
    private String cities;

    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNameFr() { return nameFr; }
    public void setNameFr(String nameFr) { this.nameFr = nameFr; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getCities() { return cities; }
    public void setCities(String cities) { this.cities = cities; }
}
