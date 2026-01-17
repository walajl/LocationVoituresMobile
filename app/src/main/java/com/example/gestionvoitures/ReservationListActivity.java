package com.example.gestionvoitures;



import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionvoitures.adapter.ReservationAdapter;
import com.example.gestionvoitures.model.Facture;
import com.example.gestionvoitures.model.Reservation;
import com.example.gestionvoitures.model.Voiture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Activite de gestion des reservations
 * Permet a l'admin de:
 * - Voir toutes les reservations
 * - Accepter une reservation (cree une facture)
 * - Annuler une reservation
 * - Remettre en attente une reservation
 */
public class ReservationListActivity extends AppCompatActivity implements ReservationAdapter.OnReservationClickListener {

    // Composants UI
    private RecyclerView recyclerView;
    private ReservationAdapter adapter;
    
    // Liste des reservations
    private List<Reservation> listeReservations = new ArrayList<>();
    
    // References Firebase
    private DatabaseReference reservationsRef, facturesRef, voituresRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);

        // Initialisation des references Firebase
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        reservationsRef = db.getReference().child("reservations");
        facturesRef = db.getReference().child("factures");
        voituresRef = db.getReference().child("voitures");

        // Configuration du RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReservationAdapter(listeReservations, this);
        recyclerView.setAdapter(adapter);

        // Charger les reservations depuis Firebase
        chargerReservations();
    }

    /**
     * Charger toutes les reservations depuis Firebase
     */
    private void chargerReservations() {
        reservationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listeReservations.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Reservation r = child.getValue(Reservation.class);
                    if (r != null) {
                        r.setId(child.getKey());
                        listeReservations.add(r);
                    }
                }
                // Notifier l'adaptateur du changement
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gestion d'erreur
            }
        });
    }

    /**
     * Accepter une reservation
     * - Change le statut en "acceptee"
     * - Cree une facture automatiquement
     * - Marque la voiture comme non disponible
     */
    @Override
    public void onAccepter(Reservation reservation) {
        reservation.setStatut("acceptee");
        reservationsRef.child(reservation.getId()).setValue(reservation)
                .addOnSuccessListener(aVoid -> {
                    creerFacture(reservation);
                    mettreAJourDisponibiliteVoiture(reservation.getVoitureId(), false);
                    Toast.makeText(this, "Reservation acceptee", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Annuler une reservation
     * - Change le statut en "annulee"
     * - Supprime la facture si elle existe
     * - Remet la voiture disponible
     */
    @Override
    public void onAnnuler(Reservation reservation) {
        reservation.setStatut("annulee");
        reservationsRef.child(reservation.getId()).setValue(reservation)
                .addOnSuccessListener(aVoid -> {
                    supprimerFacture(reservation.getId());
                    mettreAJourDisponibiliteVoiture(reservation.getVoitureId(), true);
                    Toast.makeText(this, "Reservation annulee", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Remettre une reservation en attente
     */
    @Override
    public void onAttente(Reservation reservation) {
        reservation.setStatut("en_attente");
        reservationsRef.child(reservation.getId()).setValue(reservation)
                .addOnSuccessListener(aVoid -> {
                    supprimerFacture(reservation.getId());
                    mettreAJourDisponibiliteVoiture(reservation.getVoitureId(), true);
                    Toast.makeText(this, "Reservation mise en attente", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Creer une facture pour une reservation acceptee
     */
    private void creerFacture(Reservation reservation) {
        // Verifier si une facture existe deja
        facturesRef.orderByChild("reservationId").equalTo(reservation.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            // Calculer le nombre de jours
                            long diff = reservation.getDateFin() - reservation.getDateDebut();
                            int jours = (int) (diff / (1000 * 60 * 60 * 24)) + 1;
                            double prixJour = reservation.getPrixTotal() / jours;

                            // Creer la facture
                            String id = facturesRef.push().getKey();
                            Facture facture = new Facture(
                                    id,
                                    reservation.getId(),
                                    reservation.getUtilisateurId(),
                                    reservation.getUtilisateurNom(),
                                    reservation.getVoitureNom(),
                                    reservation.getDateDebut(),
                                    reservation.getDateFin(),
                                    jours,
                                    prixJour,
                                    reservation.getPrixTotal(),
                                    System.currentTimeMillis()
                            );
                            facturesRef.child(id).setValue(facture);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    /**
     * Supprimer la facture associee a une reservation
     */
    private void supprimerFacture(String reservationId) {
        facturesRef.orderByChild("reservationId").equalTo(reservationId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            child.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    /**
     * Mettre a jour la disponibilite d'une voiture
     */
    private void mettreAJourDisponibiliteVoiture(String voitureId, boolean disponible) {
        voituresRef.child(voitureId).child("disponible").setValue(disponible);
    }
}
