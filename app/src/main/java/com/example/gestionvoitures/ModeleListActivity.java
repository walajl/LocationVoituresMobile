package com.example.gestionvoitures;



import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionvoitures.adapter.ModeleAdapter;
import com.example.gestionvoitures.model.Marque;
import com.example.gestionvoitures.model.Modele;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Activite de gestion des modeles
 * Permet d'ajouter, modifier et supprimer des modeles de voitures
 * Chaque modele est lie a une marque via un Spinner
 */
public class ModeleListActivity extends AppCompatActivity implements ModeleAdapter.OnModeleClickListener {

    // Composants UI
    private RecyclerView recyclerView;
    private ModeleAdapter adapter;
    
    // Donnees
    private List<Modele> listeModeles = new ArrayList<>();
    private List<Marque> listeMarques = new ArrayList<>();
    
    // References Firebase
    private DatabaseReference modelesRef, marquesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modele_list);

        // Initialisation Firebase
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        modelesRef = db.getReference().child("modeles");
        marquesRef = db.getReference().child("marques");

        // Configuration du RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ModeleAdapter(listeModeles, this);
        recyclerView.setAdapter(adapter);

        // Bouton ajouter (FAB)
        FloatingActionButton fab = findViewById(R.id.fabAjouter);
        fab.setOnClickListener(v -> afficherDialog(null));

        // Charger les donnees
        chargerMarques();
        chargerModeles();
    }

    /**
     * Charger les marques pour le Spinner
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Charger les modeles depuis Firebase
     */
    private void chargerModeles() {
        modelesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listeModeles.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Modele modele = child.getValue(Modele.class);
                    if (modele != null) {
                        modele.setId(child.getKey());
                        listeModeles.add(modele);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Afficher le dialog d'ajout/modification
     */
    private void afficherDialog(Modele modele) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_modele, null);
        EditText etNom = dialogView.findViewById(R.id.etNom);
        Spinner spinnerMarque = dialogView.findViewById(R.id.spinnerMarque);

        // Remplir le Spinner avec les marques
        List<String> nomsMarques = new ArrayList<>();
        for (Marque m : listeMarques) {
            nomsMarques.add(m.getNom());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nomsMarques);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarque.setAdapter(spinnerAdapter);

        // Si modification, remplir les champs
        if (modele != null) {
            etNom.setText(modele.getNom());
            for (int i = 0; i < listeMarques.size(); i++) {
                if (listeMarques.get(i).getId().equals(modele.getMarqueId())) {
                    spinnerMarque.setSelection(i);
                    break;
                }
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(modele == null ? "Nouveau Modele" : "Modifier Modele")
                .setView(dialogView)
                .setPositiveButton("Enregistrer", (dialog, which) -> {
                    String nom = etNom.getText().toString().trim();
                    int position = spinnerMarque.getSelectedItemPosition();
                    
                    if (!nom.isEmpty() && position >= 0 && position < listeMarques.size()) {
                        String marqueId = listeMarques.get(position).getId();
                        
                        if (modele == null) {
                            // Ajouter nouveau modele
                            String id = modelesRef.push().getKey();
                            Modele nouveau = new Modele(id, nom, marqueId);
                            modelesRef.child(id).setValue(nouveau);
                        } else {
                            // Modifier modele existant
                            modele.setNom(nom);
                            modele.setMarqueId(marqueId);
                            modelesRef.child(modele.getId()).setValue(modele);
                        }
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public void onModifier(Modele modele) {
        afficherDialog(modele);
    }

    @Override
    public void onSupprimer(Modele modele) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer")
                .setMessage("Voulez-vous supprimer ce modele ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    modelesRef.child(modele.getId()).removeValue();
                    Toast.makeText(this, "Modele supprime", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Non", null)
                .show();
    }
}
