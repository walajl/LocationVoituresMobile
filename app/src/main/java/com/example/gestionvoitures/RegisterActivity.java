package com.example.gestionvoitures;



import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestionvoitures.model.Utilisateur;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Activite d'inscription
 * Permet aux nouveaux utilisateurs de creer un compte
 * Tous les nouveaux inscrits ont le role "client" par defaut
 */
public class RegisterActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    
    // Champs de saisie
    private TextInputEditText etNom, etEmail, etMotDePasse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialisation Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Recuperation des vues
        etNom = findViewById(R.id.etNom);
        etEmail = findViewById(R.id.etEmail);
        etMotDePasse = findViewById(R.id.etMotDePasse);

        // Bouton inscription
        MaterialButton btnInscrire = findViewById(R.id.btnInscrire);
        btnInscrire.setOnClickListener(v -> inscrire());

        // Lien vers connexion
        TextView tvConnexion = findViewById(R.id.tvConnexion);
        tvConnexion.setOnClickListener(v -> finish());
    }

    /**
     * Methode pour inscrire un nouvel utilisateur
     */
    private void inscrire() {
        // Recuperer les valeurs saisies
        String nom = etNom.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String mdp = etMotDePasse.getText().toString().trim();

        // Validation des champs
        if (nom.isEmpty() || email.isEmpty() || mdp.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mdp.length() < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creation du compte avec Firebase Auth
        auth.createUserWithEmailAndPassword(email, mdp)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && auth.getCurrentUser() != null) {
                        // Creer l'utilisateur dans la base de donnees
                        String uid = auth.getCurrentUser().getUid();
                        Utilisateur utilisateur = new Utilisateur(uid, nom, email, "client");
                        
                        database.getReference().child("utilisateurs").child(uid)
                                .setValue(utilisateur)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Inscription reussie", Toast.LENGTH_SHORT).show();
                                    // Rediriger vers le dashboard client
                                    Intent intent = new Intent(this, ClientDashboardActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                });
                    } else {
                        Toast.makeText(this, "Erreur d'inscription", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
