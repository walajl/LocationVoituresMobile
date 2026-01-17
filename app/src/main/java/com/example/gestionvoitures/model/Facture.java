package com.example.gestionvoitures.model;

/**
 * Classe Facture
 * Represente une facture generee apres acceptation d'une reservation
 * Contient les informations de paiement et de location
 */
public class Facture {
    
    // Attributs de la facture
    private String id;              // Identifiant unique
    private String reservationId;   // ID de la reservation associee
    private String utilisateurId;   // ID du client
    private String utilisateurNom;  // Nom du client
    private String voitureNom;      // Nom de la voiture louee
    private long dateDebut;         // Date debut de location
    private long dateFin;           // Date fin de location
    private int nombreJours;        // Nombre de jours de location
    private double prixParJour;     // Prix par jour
    private double montantTotal;    // Montant total a payer
    private long dateFacture;       // Date de creation de la facture

    /**
     * Constructeur vide
     * Requis par Firebase pour la deserialisation
     */
    public Facture() {
        this.id = "";
        this.reservationId = "";
        this.utilisateurId = "";
        this.utilisateurNom = "";
        this.voitureNom = "";
        this.dateDebut = 0L;
        this.dateFin = 0L;
        this.nombreJours = 0;
        this.prixParJour = 0.0;
        this.montantTotal = 0.0;
        this.dateFacture = System.currentTimeMillis();
    }

    /**
     * Constructeur complet
     */
    public Facture(String id, String reservationId, String utilisateurId, String utilisateurNom,
                   String voitureNom, long dateDebut, long dateFin, int nombreJours,
                   double prixParJour, double montantTotal, long dateFacture) {
        this.id = id;
        this.reservationId = reservationId;
        this.utilisateurId = utilisateurId;
        this.utilisateurNom = utilisateurNom;
        this.voitureNom = voitureNom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.nombreJours = nombreJours;
        this.prixParJour = prixParJour;
        this.montantTotal = montantTotal;
        this.dateFacture = dateFacture;
    }

    // ===== GETTERS ET SETTERS =====
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public String getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(String utilisateurId) { this.utilisateurId = utilisateurId; }

    public String getUtilisateurNom() { return utilisateurNom; }
    public void setUtilisateurNom(String utilisateurNom) { this.utilisateurNom = utilisateurNom; }

    public String getVoitureNom() { return voitureNom; }
    public void setVoitureNom(String voitureNom) { this.voitureNom = voitureNom; }

    public long getDateDebut() { return dateDebut; }
    public void setDateDebut(long dateDebut) { this.dateDebut = dateDebut; }

    public long getDateFin() { return dateFin; }
    public void setDateFin(long dateFin) { this.dateFin = dateFin; }

    public int getNombreJours() { return nombreJours; }
    public void setNombreJours(int nombreJours) { this.nombreJours = nombreJours; }

    public double getPrixParJour() { return prixParJour; }
    public void setPrixParJour(double prixParJour) { this.prixParJour = prixParJour; }

    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }

    public long getDateFacture() { return dateFacture; }
    public void setDateFacture(long dateFacture) { this.dateFacture = dateFacture; }
}
