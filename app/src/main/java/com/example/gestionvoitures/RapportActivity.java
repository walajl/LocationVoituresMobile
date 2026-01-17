package com.example.gestionvoitures;



import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestionvoitures.model.Facture;
import com.example.gestionvoitures.model.Reservation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Activite pour la generation des rapports
 * Permet de generer 3 types de rapports:
 * 1. Historique des demandes de reservation
 * 2. Locations confirmees (voitures louees)
 * 3. Chiffre d'affaires
 */
public class RapportActivity extends AppCompatActivity {

    // Composants UI
    private EditText etDateDebut, etDateFin;
    private Button btnReservations, btnLocations, btnChiffreAffaire;
    private TextView tvResultat;

    // References Firebase
    private DatabaseReference reservationsRef, facturesRef;
    
    // Format de date
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);

    // Dates du filtre (en millisecondes)
    private long dateDebutFiltre = 0L;
    private long dateFinFiltre = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapport);

        // Initialisation Firebase
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        reservationsRef = db.getReference().child("reservations");
        facturesRef = db.getReference().child("factures");

        // Recuperation des vues
        etDateDebut = findViewById(R.id.etDateDebut);
        etDateFin = findViewById(R.id.etDateFin);
        btnReservations = findViewById(R.id.btnReservees);
        btnLocations = findViewById(R.id.btnLouees);
        btnChiffreAffaire = findViewById(R.id.btnChiffreAffaire);
        tvResultat = findViewById(R.id.tvResultat);

        // Configuration des selecteurs de date
        etDateDebut.setOnClickListener(v -> choisirDate(true));
        etDateFin.setOnClickListener(v -> choisirDate(false));

        // Configuration des boutons de rapport
        btnReservations.setOnClickListener(v -> genererRapportReservations());
        btnLocations.setOnClickListener(v -> genererRapportLocations());
        btnChiffreAffaire.setOnClickListener(v -> genererRapportCA());
    }

    /**
     * Afficher un DatePickerDialog pour choisir une date
     */
    private void choisirDate(boolean isDebut) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            if (isDebut) {
                cal.set(year, month, day, 0, 0, 0);
                dateDebutFiltre = cal.getTimeInMillis();
                etDateDebut.setText(dateFormat.format(new Date(dateDebutFiltre)));
            } else {
                cal.set(year, month, day, 23, 59, 59);
                dateFinFiltre = cal.getTimeInMillis();
                etDateFin.setText(dateFormat.format(new Date(dateFinFiltre)));
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Generer le rapport des demandes de reservation
     * Affiche toutes les reservations (acceptees, annulees, en attente)
     */
    private void genererRapportReservations() {
        if (dateDebutFiltre == 0L || dateFinFiltre == 0L) {
            Toast.makeText(this, "Veuillez selectionner les dates", Toast.LENGTH_SHORT).show();
            return;
        }

        reservationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder result = new StringBuilder();
                result.append("=== HISTORIQUE DES DEMANDES ===\n");
                result.append("Periode: ").append(etDateDebut.getText()).append(" - ").append(etDateFin.getText()).append("\n\n");

                int total = 0, acceptees = 0, annulees = 0, enAttente = 0;

                for (DataSnapshot child : snapshot.getChildren()) {
                    Reservation r = child.getValue(Reservation.class);
                    if (r != null && r.getDateDebut() >= dateDebutFiltre && r.getDateDebut() <= dateFinFiltre) {
                        total++;
                        switch (r.getStatut()) {
                            case "acceptee": acceptees++; break;
                            case "annulee": annulees++; break;
                            default: enAttente++; break;
                        }
                        result.append("- ").append(r.getVoitureNom()).append(" (").append(r.getStatut()).append(")\n");
                    }
                }

                result.append("\n------------------------\n");
                result.append("Total: ").append(total).append("\n");
                result.append("Acceptees: ").append(acceptees).append("\n");
                result.append("Annulees: ").append(annulees).append("\n");
                result.append("En attente: ").append(enAttente).append("\n");

                tvResultat.setText(result.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Generer le rapport des locations confirmees
     * Affiche uniquement les reservations acceptees
     */
    private void genererRapportLocations() {
        if (dateDebutFiltre == 0L || dateFinFiltre == 0L) {
            Toast.makeText(this, "Veuillez selectionner les dates", Toast.LENGTH_SHORT).show();
            return;
        }

        reservationsRef.orderByChild("statut").equalTo("acceptee")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        StringBuilder result = new StringBuilder();
                        result.append("=== LOCATIONS CONFIRMEES ===\n");
                        result.append("Periode: ").append(etDateDebut.getText()).append(" - ").append(etDateFin.getText()).append("\n\n");

                        int count = 0;
                        int totalJours = 0;

                        for (DataSnapshot child : snapshot.getChildren()) {
                            Reservation r = child.getValue(Reservation.class);
                            if (r != null && r.getDateDebut() >= dateDebutFiltre && r.getDateDebut() <= dateFinFiltre) {
                                count++;
                                long diff = r.getDateFin() - r.getDateDebut();
                                int jours = (int) (diff / (1000 * 60 * 60 * 24)) + 1;
                                totalJours += jours;
                                result.append("- ").append(r.getVoitureNom()).append(" : ").append(jours).append(" jours\n");
                            }
                        }

                        result.append("\n------------------------\n");
                        result.append("Total locations: ").append(count).append("\n");
                        result.append("Jours cumules: ").append(totalJours).append("\n");

                        tvResultat.setText(result.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    /**
     * Generer le rapport du chiffre d'affaires
     * Calcule le total des factures sur la periode
     */
    private void genererRapportCA() {
        if (dateDebutFiltre == 0L || dateFinFiltre == 0L) {
            Toast.makeText(this, "Veuillez selectionner les dates", Toast.LENGTH_SHORT).show();
            return;
        }

        facturesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder result = new StringBuilder();
                result.append("=== CHIFFRE D'AFFAIRE ===\n");
                result.append("Periode: ").append(etDateDebut.getText()).append(" - ").append(etDateFin.getText()).append("\n\n");

                int count = 0;
                double totalCA = 0;

                for (DataSnapshot child : snapshot.getChildren()) {
                    Facture f = child.getValue(Facture.class);
                    if (f != null && f.getDateFacture() >= dateDebutFiltre && f.getDateFacture() <= dateFinFiltre) {
                        count++;
                        totalCA += f.getMontantTotal();
                        result.append("- ").append(f.getVoitureNom()).append(" : ").append(f.getMontantTotal()).append(" DT\n");
                    }
                }

                result.append("\n------------------------\n");
                result.append("TOTAL: ").append(totalCA).append(" DT\n");
                result.append("Nombre de factures: ").append(count).append("\n");

                tvResultat.setText(result.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
