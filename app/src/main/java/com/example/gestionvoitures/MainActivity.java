package com.example.gestionvoitures;



import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Activite principale - Dashboard Admin
 * Affiche les cartes de navigation vers les differentes fonctionnalites
 * Accessible uniquement par les utilisateurs avec le role admin
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ===== NAVIGATION VERS LES DIFFERENTES SECTIONS =====
        
        // Carte Voitures - Gestion du parc automobile
        CardView cardVoitures = findViewById(R.id.cardVoitures);
        cardVoitures.setOnClickListener(v -> 
                startActivity(new Intent(this, VoitureListActivity.class)));

        // Carte Marques - Gestion des marques de voitures
        CardView cardMarques = findViewById(R.id.cardMarques);
        cardMarques.setOnClickListener(v -> 
                startActivity(new Intent(this, MarqueListActivity.class)));

        // Carte Modeles - Gestion des modeles de voitures
        CardView cardModeles = findViewById(R.id.cardModeles);
        cardModeles.setOnClickListener(v -> 
                startActivity(new Intent(this, ModeleListActivity.class)));

        // Carte Reservations - Gestion des demandes de location
        CardView cardReservations = findViewById(R.id.cardReservations);
        cardReservations.setOnClickListener(v -> 
                startActivity(new Intent(this, ReservationListActivity.class)));

        // Carte Factures - Consultation des factures
        CardView cardFactures = findViewById(R.id.cardFactures);
        cardFactures.setOnClickListener(v -> 
                startActivity(new Intent(this, FactureListActivity.class)));

        // Carte Rapports - Generation de rapports
        CardView cardRapports = findViewById(R.id.cardRapports);
        cardRapports.setOnClickListener(v -> 
                startActivity(new Intent(this, RapportActivity.class)));

        // Carte Deconnexion - Fermer la session
        CardView cardDeconnexion = findViewById(R.id.cardDeconnexion);
        cardDeconnexion.setOnClickListener(v -> {
            // Deconnexion de Firebase
            FirebaseAuth.getInstance().signOut();
            // Retour a l'ecran de connexion
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
