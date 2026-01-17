package com.example.gestionvoitures;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionvoitures.adapter.ClientReservationAdapter;
import com.example.gestionvoitures.model.Reservation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Activite mes reservations (client)
 * Affiche l'historique des reservations du client connecte
 * Permet de voir le statut (en attente, acceptee, annulee)
 */
public class ClientReservationsActivity extends AppCompatActivity {

    // Composants UI
    private RecyclerView recyclerView;
    private ClientReservationAdapter adapter;
    
    // Liste des reservations
    private List<Reservation> listeReservations = new ArrayList<>();
    
    // References Firebase
    private DatabaseReference reservationsRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_reservations);

        // Initialisation Firebase
        reservationsRef = FirebaseDatabase.getInstance().getReference().child("reservations");
        auth = FirebaseAuth.getInstance();

        // Configuration du RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClientReservationAdapter(listeReservations);
        recyclerView.setAdapter(adapter);

        // Charger mes reservations
        chargerMesReservations();
    }

    /**
     * Charger les reservations du client connecte
     */
    private void chargerMesReservations() {
        if (auth.getCurrentUser() == null) return;
        
        String userId = auth.getCurrentUser().getUid();
        
        // Filtrer par utilisateurId
        reservationsRef.orderByChild("utilisateurId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listeReservations.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Reservation reservation = child.getValue(Reservation.class);
                            if (reservation != null) {
                                reservation.setId(child.getKey());
                                listeReservations.add(reservation);
                            }
                        }
                        // Notifier l'adaptateur
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Gestion d'erreur
                    }
                });
    }
}
