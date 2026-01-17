package com.example.gestionvoitures.model;

/**
 * Classe Marque
 * Represente une marque de voiture (Toyota, Renault, etc.)
 * Utilisee pour categoriser les voitures
 */
public class Marque {
    
    // Attributs
    private String id;      // Identifiant unique
    private String nom;     // Nom de la marque

    /**
     * Constructeur vide
     * Requis par Firebase
     */
    public Marque() {
        this.id = "";
        this.nom = "";
    }

    /**
     * Constructeur avec parametres
     */
    public Marque(String id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    // ===== GETTERS ET SETTERS =====
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}
