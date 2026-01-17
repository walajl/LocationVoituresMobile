package com.example.gestionvoitures.model;

/**
 * Classe Modele
 * Represente un modele de voiture (Corolla, Clio, etc.)
 * Chaque modele appartient a une marque
 */
public class Modele {
    
    // Attributs
    private String id;          // Identifiant unique
    private String nom;         // Nom du modele
    private String marqueId;    // ID de la marque associee

    /**
     * Constructeur vide
     * Requis par Firebase
     */
    public Modele() {
        this.id = "";
        this.nom = "";
        this.marqueId = "";
    }

    /**
     * Constructeur avec parametres
     */
    public Modele(String id, String nom, String marqueId) {
        this.id = id;
        this.nom = nom;
        this.marqueId = marqueId;
    }

    // ===== GETTERS ET SETTERS =====
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getMarqueId() { return marqueId; }
    public void setMarqueId(String marqueId) { this.marqueId = marqueId; }
}
