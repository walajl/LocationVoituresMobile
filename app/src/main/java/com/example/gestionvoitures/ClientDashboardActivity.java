package com.example.gestionvoitures;



import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Dashboard Client
 * Ecran principal pour les utilisateurs avec le role "client"
 * Permet d'acceder aux voitures disponibles et a ses reservations
 */
public class ClientDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_dashboard);

        // Navigation vers les voitures disponibles
        CardView cardVoitures = findViewById(R.id.cardVoituresDisponibles);
        cardVoitures.setOnClickListener(v -> 
                startActivity(new Intent(this, ClientVoituresActivity.class)));

        // Navigation vers mes reservations
        CardView cardReservations = findViewById(R.id.cardMesReservations);
        cardReservations.setOnClickListener(v -> 
                startActivity(new Intent(this, ClientReservationsActivity.class)));

        // Bouton deconnexion
        findViewById(R.id.btnDeconnexion).setOnClickListener(v -> deconnecter());
    }

    /**
     * Deconnexion de l'utilisateur
     */
    private void deconnecter() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    /**
     * Gestion du bouton retour
     * Demande confirmation avant deconnexion
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new android.app.AlertDialog.Builder(this)
                .setTitle("Deconnexion")
                .setMessage("Voulez-vous vous deconnecter ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    // Deconnexion Firebase
                    FirebaseAuth.getInstance().signOut();
                    // Retour a l'ecran de connexion
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Non", null)
                .show();
    }
}
