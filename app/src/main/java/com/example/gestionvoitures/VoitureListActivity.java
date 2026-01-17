package com.example.gestionvoitures;



import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionvoitures.adapter.VoitureAdapter;
import com.example.gestionvoitures.model.Voiture;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Activite liste des voitures
 * Affiche toutes les voitures avec options modifier/supprimer
 * Bouton FAB pour ajouter une nouvelle voiture
 */
public class VoitureListActivity extends AppCompatActivity implements VoitureAdapter.OnVoitureClickListener {

    // Composants UI
    private RecyclerView recyclerView;
    private VoitureAdapter adapter;
    
    // Liste des voitures
    private List<Voiture> listeVoitures = new ArrayList<>();
    
    // Reference Firebase
    private DatabaseReference voituresRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voiture_list);

        // Initialisation Firebase
        voituresRef = FirebaseDatabase.getInstance().getReference().child("voitures");

        // Configuration du RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VoitureAdapter(listeVoitures, this);
        recyclerView.setAdapter(adapter);

        // Bouton ajouter (FAB)
        FloatingActionButton fab = findViewById(R.id.fabAjouter);
        fab.setOnClickListener(v -> {
            // Ouvrir le formulaire d'ajout
            startActivity(new Intent(this, VoitureFormActivity.class));
        });

        // Charger les voitures
        chargerVoitures();
    }

    /**
     * Charger les voitures depuis Firebase
     */
    private void chargerVoitures() {
        voituresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listeVoitures.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Voiture voiture = child.getValue(Voiture.class);
                    if (voiture != null) {
                        voiture.setId(child.getKey());
                        listeVoitures.add(voiture);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VoitureListActivity.this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Modifier une voiture - ouvre le formulaire
     */
    @Override
    public void onModifier(Voiture voiture) {
        Intent intent = new Intent(this, VoitureFormActivity.class);
        intent.putExtra("voiture_id", voiture.getId());
        startActivity(intent);
    }

    /**
     * Supprimer une voiture
     */
    @Override
    public void onSupprimer(Voiture voiture) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer")
                .setMessage("Voulez-vous supprimer cette voiture ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    voituresRef.child(voiture.getId()).removeValue();
                    Toast.makeText(this, "Voiture supprimee", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Non", null)
                .show();
    }
}
