package com.example.gestionvoitures;



import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionvoitures.adapter.MarqueAdapter;
import com.example.gestionvoitures.model.Marque;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Activite de gestion des marques
 * Permet d'ajouter, modifier et supprimer des marques de voitures
 * Utilise un RecyclerView pour afficher la liste
 */
public class MarqueListActivity extends AppCompatActivity implements MarqueAdapter.OnMarqueClickListener {

    // Composants UI
    private RecyclerView recyclerView;
    private MarqueAdapter adapter;
    
    // Liste des marques
    private List<Marque> listeMarques = new ArrayList<>();
    
    // Reference Firebase
    private DatabaseReference marquesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marque_list);

        // Initialisation Firebase
        marquesRef = FirebaseDatabase.getInstance().getReference().child("marques");

        // Configuration du RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MarqueAdapter(listeMarques, this);
        recyclerView.setAdapter(adapter);

        // Bouton ajouter (FAB)
        FloatingActionButton fab = findViewById(R.id.fabAjouter);
        fab.setOnClickListener(v -> afficherDialog(null));

        // Charger les marques
        chargerMarques();
    }

    /**
     * Charger les marques depuis Firebase
     */
    private void chargerMarques() {
        marquesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listeMarques.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Marque marque = child.getValue(Marque.class);
                    if (marque != null) {
                        marque.setId(child.getKey());
                        listeMarques.add(marque);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MarqueListActivity.this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Afficher le dialog d'ajout/modification
     */
    private void afficherDialog(Marque marque) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_marque, null);
        EditText etNom = dialogView.findViewById(R.id.etNom);

        // Si modification, remplir le champ
        if (marque != null) {
            etNom.setText(marque.getNom());
        }

        new AlertDialog.Builder(this)
                .setTitle(marque == null ? "Nouvelle Marque" : "Modifier Marque")
                .setView(dialogView)
                .setPositiveButton("Enregistrer", (dialog, which) -> {
                    String nom = etNom.getText().toString().trim();
                    if (!nom.isEmpty()) {
                        if (marque == null) {
                            // Ajouter nouvelle marque
                            String id = marquesRef.push().getKey();
                            Marque nouvelle = new Marque(id, nom);
                            marquesRef.child(id).setValue(nouvelle);
                        } else {
                            // Modifier marque existante
                            marque.setNom(nom);
                            marquesRef.child(marque.getId()).setValue(marque);
                        }
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    /**
     * Modifier une marque
     */
    @Override
    public void onModifier(Marque marque) {
        afficherDialog(marque);
    }

    /**
     * Supprimer une marque
     */
    @Override
    public void onSupprimer(Marque marque) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer")
                .setMessage("Voulez-vous supprimer cette marque ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    marquesRef.child(marque.getId()).removeValue();
                    Toast.makeText(this, "Marque supprimee", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Non", null)
                .show();
    }
}
