package com.bschooleventmanager.eventmanager.model;

import com.bschooleventmanager.eventmanager.model.enums.NiveauExpertise;
import com.bschooleventmanager.eventmanager.model.enums.TypeConcert;
import com.bschooleventmanager.eventmanager.model.enums.TypeEvenement;
import com.bschooleventmanager.eventmanager.model.enums.TypeSpectacle;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventTotal {
    private Integer idEvenement;
    private Integer organisateurId;
    private String nom;
    private LocalDateTime dateEvenement;
    private String lieu;
    private TypeEvenement typeEvenement;     // enum
    private String description;

    private Integer placesStandardDisponibles;
    private Integer placesVipDisponibles;
    private Integer placesPremiumDisponibles;

    private BigDecimal prixStandard;
    private BigDecimal prixVip;
    private BigDecimal prixPremium;

    private LocalDateTime dateCreation;
    private String statut;            // enum

    private String artisteGroupe;
    private Integer ageMin;
    private String domaine;
    private String intervenant;

    private TypeConcert typeConcert;       // enum
    private TypeSpectacle typeSpectacle;     // enum
    private NiveauExpertise niveauExpertise;   // enum

    private Integer placeStandardVendues;
    private Integer placeVipVendues;
    private Integer placePremiumVendues;

    private Integer etatEvent;

    public void Evenement() {}

    // Getters & setters
    public Integer getIdEvenement() { return idEvenement; }
    public void setIdEvenement(Integer idEvenement) { this.idEvenement = idEvenement; }

    public Integer getOrganisateurId() { return organisateurId; }
    public void setOrganisateurId(Integer organisateurId) { this.organisateurId = organisateurId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public LocalDateTime getDateEvenement() { return dateEvenement; }
    public void setDateEvenement(LocalDateTime dateEvenement) { this.dateEvenement = dateEvenement; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public TypeEvenement getTypeEvenement() { return typeEvenement; }
    public void setTypeEvenement(TypeEvenement typeEvenement) { this.typeEvenement = typeEvenement; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPlacesStandardDisponibles() { return placesStandardDisponibles; }
    public void setPlacesStandardDisponibles(Integer placesStandardDisponibles) { this.placesStandardDisponibles = placesStandardDisponibles; }

    public Integer getPlacesVipDisponibles() { return placesVipDisponibles; }
    public void setPlacesVipDisponibles(Integer placesVipDisponibles) { this.placesVipDisponibles = placesVipDisponibles; }

    public Integer getPlacesPremiumDisponibles() { return placesPremiumDisponibles; }
    public void setPlacesPremiumDisponibles(Integer placesPremiumDisponibles) { this.placesPremiumDisponibles = placesPremiumDisponibles; }

    public BigDecimal getPrixStandard() { return prixStandard; }
    public void setPrixStandard(BigDecimal prixStandard) { this.prixStandard = prixStandard; }

    public BigDecimal getPrixVip() { return prixVip; }
    public void setPrixVip(BigDecimal prixVip) { this.prixVip = prixVip; }

    public BigDecimal getPrixPremium() { return prixPremium; }
    public void setPrixPremium(BigDecimal prixPremium) { this.prixPremium = prixPremium; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getArtisteGroupe() { return artisteGroupe; }
    public void setArtisteGroupe(String artisteGroupe) { this.artisteGroupe = artisteGroupe; }

    public Integer getAgeMin() { return ageMin; }
    public void setAgeMin(Integer ageMin) { this.ageMin = ageMin; }

    public String getDomaine() { return domaine; }
    public void setDomaine(String domaine) { this.domaine = domaine; }

    public String getIntervenant() { return intervenant; }
    public void setIntervenant(String intervenant) { this.intervenant = intervenant; }

    public TypeConcert getTypeConcert() { return typeConcert; }
    public void setTypeConcert(TypeConcert typeConcert) { this.typeConcert = typeConcert; }

    public TypeSpectacle getTypeSpectacle() { return typeSpectacle; }
    public void setTypeSpectacle(TypeSpectacle typeSpectacle) { this.typeSpectacle = typeSpectacle; }

    public NiveauExpertise getNiveauExpertise() { return niveauExpertise; }
    public void setNiveauExpertise(NiveauExpertise niveauExpertise) { this.niveauExpertise = niveauExpertise; }

    public Integer getPlaceStandardVendues() { return placeStandardVendues; }
    public void setPlaceStandardVendues(Integer placeStandardVendues) { this.placeStandardVendues = placeStandardVendues; }

    public Integer getPlaceVipVendues() { return placeVipVendues; }
    public void setPlaceVipVendues(Integer placeVipVendues) { this.placeVipVendues = placeVipVendues; }

    public Integer getPlacePremiumVendues() { return placePremiumVendues; }
    public void setPlacePremiumVendues(Integer placePremiumVendues) { this.placePremiumVendues = placePremiumVendues; }

    public Integer getEtatEvent() { return etatEvent; }
    public void setEtatEvent(Integer etatEvent) { this.etatEvent = etatEvent; }
}

