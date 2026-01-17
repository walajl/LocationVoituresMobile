package com.example.gestionvoitures.model;

/**
 * Classe Utilisateur
 * Represente un utilisateur de l'application (admin ou client)
 * Stockee dans Firebase sous le noeud "utilisateurs"
 */
public class Utilisateur {
    
    // Attributs
    private String id;      // Identifiant unique (UID Firebase)
    private String nom;     // Nom de l'utilisateur
    private String email;   // Adresse email
    private String role;    // Role: "admin" ou "client"

    /**
     * Constructeur vide
     * Requis par Firebase
     */
    public Utilisateur() {
        this.id = "";
        this.nom = "";
        this.email = "";
        this.role = "client"; // Role par defaut
    }

    /**
     * Constructeur complet
     */
    public Utilisateur(String id, String nom, String email, String role) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.role = role;
    }

    // ===== GETTERS ET SETTERS =====
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
