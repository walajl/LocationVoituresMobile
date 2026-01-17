package com.example.gestionvoitures.model;

/**
 * Classe Voiture
 * Represente une voiture disponible a la location
 * Cette classe est utilisee pour stocker les donnees dans Firebase
 */
public class Voiture {
    
    // Attributs de la classe
    private String id;              // Identifiant unique
    private String marqueId;        // ID de la marque
    private String modeleId;        // ID du modele
    private String marqueNom;       // Nom de la marque
    private String modeleNom;       // Nom du modele
    private int annee;              // Annee de fabrication
    private double prix;            // Prix par jour en DT
    private String immatriculation; // Numero d'immatriculation
    private boolean disponible;     // Disponibilite
    private String imageUrl;        // URL de l'image

    /**
     * Constructeur vide
     * Requis par Firebase pour la deserialisation
     */
    public Voiture() {
        this.id = "";
        this.marqueId = "";
        this.modeleId = "";
        this.marqueNom = "";
        this.modeleNom = "";
        this.annee = 2024;
        this.prix = 0.0;
        this.immatriculation = "";
        this.disponible = true;
        this.imageUrl = "";
    }

    /**
     * Constructeur complet
     * Permet de creer une voiture avec tous les attributs
     */
    public Voiture(String id, String marqueId, String modeleId, String marqueNom, 
                   String modeleNom, int annee, double prix, String immatriculation, 
                   boolean disponible, String imageUrl) {
        this.id = id;
        this.marqueId = marqueId;
        this.modeleId = modeleId;
        this.marqueNom = marqueNom;
        this.modeleNom = modeleNom;
        this.annee = annee;
        this.prix = prix;
        this.immatriculation = immatriculation;
        this.disponible = disponible;
        this.imageUrl = imageUrl;
    }

    // ===== GETTERS ET SETTERS =====
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMarqueId() { return marqueId; }
    public void setMarqueId(String marqueId) { this.marqueId = marqueId; }

    public String getModeleId() { return modeleId; }
    public void setModeleId(String modeleId) { this.modeleId = modeleId; }

    public String getMarqueNom() { return marqueNom; }
    public void setMarqueNom(String marqueNom) { this.marqueNom = marqueNom; }

    public String getModeleNom() { return modeleNom; }
    public void setModeleNom(String modeleNom) { this.modeleNom = modeleNom; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
