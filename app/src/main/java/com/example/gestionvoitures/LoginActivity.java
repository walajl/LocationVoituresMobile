package com.example.gestionvoitures;



import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Activite de connexion
 * Cette classe gere l'authentification des utilisateurs avec Firebase
 * Elle verifie le role (admin ou client) et redirige vers le bon dashboard
 */
public class LoginActivity extends AppCompatActivity {

    // Declaration des variables Firebase
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    
    // Champs de saisie
    private TextInputEditText etEmail, etMotDePasse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisation de Firebase Auth et Database
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Recuperation des vues depuis le layout XML
        etEmail = findViewById(R.id.etEmail);
        etMotDePasse = findViewById(R.id.etMotDePasse);

        // Verifier si l'utilisateur est deja connecte
        if (auth.getCurrentUser() != null) {
            verifierRoleEtRediriger();
        }

        // Configuration du bouton de connexion
        MaterialButton btnConnexion = findViewById(R.id.btnConnexion);
        btnConnexion.setOnClickListener(v -> connecter());

        // Configuration du lien vers l'inscription
        TextView tvInscription = findViewById(R.id.tvInscription);
        tvInscription.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    /**
     * Methode pour connecter l'utilisateur
     * Utilise Firebase Auth avec email et mot de passe
     */
    private void connecter() {
        // Recuperer les valeurs saisies
        String email = etEmail.getText().toString().trim();
        String mdp = etMotDePasse.getText().toString().trim();

        // Validation des champs
        if (email.isEmpty() || mdp.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mdp.length() < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show();
            return;
        }

        // Connexion avec Firebase Authentication
        auth.signInWithEmailAndPassword(email, mdp)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Connexion reussie, verifier le role
                        verifierRoleEtRediriger();
                    } else {
                        // Echec de connexion
                        Toast.makeText(this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Verifier le role de l'utilisateur dans la base de donnees
     * et rediriger vers le dashboard approprie
     */
    private void verifierRoleEtRediriger() {
        if (auth.getCurrentUser() == null) return;
        
        String userId = auth.getCurrentUser().getUid();
        String userEmail = auth.getCurrentUser().getEmail();

        // Lecture du role depuis Firebase Realtime Database
        database.getReference().child("utilisateurs").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Utilisateur trouve, recuperer son role
                            String role = snapshot.child("role").getValue(String.class);
                            if (role == null) role = "client";
                            redirigerVersRole(role);
                        } else {
                            // Chercher par email si pas trouve par ID
                            chercherParEmail(userEmail, userId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Erreur de lecture
                    }
                });
    }

    /**
     * Rechercher l'utilisateur par son email
     * Methode alternative si l'ID ne correspond pas
     */
    private void chercherParEmail(String email, String uid) {
        database.getReference().child("utilisateurs")
                .orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String role = "client"; // Role par defaut
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String foundRole = child.child("role").getValue(String.class);
                            if (foundRole != null) {
                                role = foundRole;
                            }
                            break;
                        }
                        redirigerVersRole(role);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // En cas d'erreur, rediriger vers client
                        redirigerVersRole("client");
                    }
                });
    }

    /**
     * Rediriger vers le dashboard selon le role
     * admin -> MainActivity
     * client -> ClientDashboardActivity
     */
    private void redirigerVersRole(String role) {
        Intent intent;
        if ("admin".equals(role)) {
            // Utilisateur admin
            intent = new Intent(this, MainActivity.class);
        } else {
            // Utilisateur client
            intent = new Intent(this, ClientDashboardActivity.class);
        }
        // Nettoyer la pile d'activites
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
