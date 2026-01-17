package com.example.gestionvoitures.model;

/**
 * Classe Reservation
 * Represente une demande de location d'une voiture par un client
 * Stockee dans Firebase Realtime Database
 */
public class Reservation {
    
    // Attributs
    private String id;              // Identifiant unique
    private String utilisateurId;   // ID du client
    private String utilisateurNom;  // Nom du client
    private String voitureId;       // ID de la voiture
    private String voitureNom;      // Nom complet de la voiture
    private long dateDebut;         // Date debut (timestamp)
    private long dateFin;           // Date fin (timestamp)
    private String statut;          // en_attente, acceptee, annulee
    private double prixTotal;       // Prix total de la location
    private long dateCreation;      // Date de creation

    /**
     * Constructeur vide
     * Necessaire pour Firebase
     */
    public Reservation() {
        this.id = "";
        this.utilisateurId = "";
        this.utilisateurNom = "";
        this.voitureId = "";
        this.voitureNom = "";
        this.dateDebut = 0L;
        this.dateFin = 0L;
        this.statut = "en_attente";
        this.prixTotal = 0.0;
        this.dateCreation = System.currentTimeMillis();
    }

    /**
     * Constructeur complet
     */
    public Reservation(String id, String utilisateurId, String utilisateurNom,
                       String voitureId, String voitureNom, long dateDebut,
                       long dateFin, String statut, double prixTotal, long dateCreation) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.utilisateurNom = utilisateurNom;
        this.voitureId = voitureId;
        this.voitureNom = voitureNom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.prixTotal = prixTotal;
        this.dateCreation = dateCreation;
    }

    // ===== GETTERS ET SETTERS =====
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(String utilisateurId) { this.utilisateurId = utilisateurId; }

    public String getUtilisateurNom() { return utilisateurNom; }
    public void setUtilisateurNom(String utilisateurNom) { this.utilisateurNom = utilisateurNom; }

    public String getVoitureId() { return voitureId; }
    public void setVoitureId(String voitureId) { this.voitureId = voitureId; }

    public String getVoitureNom() { return voitureNom; }
    public void setVoitureNom(String voitureNom) { this.voitureNom = voitureNom; }

    public long getDateDebut() { return dateDebut; }
    public void setDateDebut(long dateDebut) { this.dateDebut = dateDebut; }

    public long getDateFin() { return dateFin; }
    public void setDateFin(long dateFin) { this.dateFin = dateFin; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }

    public long getDateCreation() { return dateCreation; }
    public void setDateCreation(long dateCreation) { this.dateCreation = dateCreation; }
}
