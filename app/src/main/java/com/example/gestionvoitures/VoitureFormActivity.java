package com.example.gestionvoitures;



import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestionvoitures.model.Marque;
import com.example.gestionvoitures.model.Modele;
import com.example.gestionvoitures.model.Voiture;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Formulaire voiture
 * Permet d'ajouter ou modifier une voiture
 * Utilise des Spinners pour marque et modele
 */
public class VoitureFormActivity extends AppCompatActivity {

    // Composants UI
    private Spinner spinnerMarque, spinnerModele;
    private EditText etAnnee, etPrix, etImmat;
    private CheckBox cbDispo;
    private MaterialButton btnSave;

    // Donnees
    private List<Marque> listeMarques = new ArrayList<>();
    private List<Modele> listeModeles = new ArrayList<>();
    
    // References Firebase
    private DatabaseReference voituresRef, marquesRef, modelesRef;
    
    // ID de la voiture en modification (null si ajout)
    private String voitureId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voiture_form);

        // Initialisation Firebase
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        voituresRef = db.getReference().child("voitures");
        marquesRef = db.getReference().child("marques");
        modelesRef = db.getReference().child("modeles");

        // Recuperation des vues
        spinnerMarque = findViewById(R.id.spinnerMarque);
        spinnerModele = findViewById(R.id.spinnerModele);
        etAnnee = findViewById(R.id.etAnnee);
        etPrix = findViewById(R.id.etPrix);
        etImmat = findViewById(R.id.etImmat);
        cbDispo = findViewById(R.id.cbDispo);
        btnSave = findViewById(R.id.btnSave);

        // Verifier si modification
        voitureId = getIntent().getStringExtra("voiture_id");

        // Charger les marques et modeles
        chargerMarques();
        chargerModeles();

        // Bouton enregistrer
        btnSave.setOnClickListener(v -> enregistrer());
    }

    /**
     * Charger les marques pour le Spinner
     */
    private void chargerMarques() {
        marquesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listeMarques.clear();
                List<String> noms = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Marque marque = child.getValue(Marque.class);
                    if (marque != null) {
                        marque.setId(child.getKey());
                        listeMarques.add(marque);
                        noms.add(marque.getNom());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(VoitureFormActivity.this,
                        android.R.layout.simple_spinner_item, noms);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMarque.setAdapter(adapter);
                
                // Si modification, charger les donnees
                if (voitureId != null) {
                    chargerVoiture();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Charger les modeles pour le Spinner
     */
    private void chargerModeles() {
        modelesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listeModeles.clear();
                List<String> noms = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Modele modele = child.getValue(Modele.class);
                    if (modele != null) {
                        modele.setId(child.getKey());
                        listeModeles.add(modele);
                        noms.add(modele.getNom());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(VoitureFormActivity.this,
                        android.R.layout.simple_spinner_item, noms);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerModele.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Charger les donnees de la voiture en modification
     */
    private void chargerVoiture() {
        voituresRef.child(voitureId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Voiture voiture = snapshot.getValue(Voiture.class);
                if (voiture != null) {
                    etAnnee.setText(String.valueOf(voiture.getAnnee()));
                    etPrix.setText(String.valueOf(voiture.getPrix()));
                    etImmat.setText(voiture.getImmatriculation());
                    cbDispo.setChecked(voiture.isDisponible());
                    
                    // Selectionner la marque et le modele
                    for (int i = 0; i < listeMarques.size(); i++) {
                        if (listeMarques.get(i).getId().equals(voiture.getMarqueId())) {
                            spinnerMarque.setSelection(i);
                            break;
                        }
                    }
                    for (int i = 0; i < listeModeles.size(); i++) {
                        if (listeModeles.get(i).getId().equals(voiture.getModeleId())) {
                            spinnerModele.setSelection(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Enregistrer la voiture
     */
    private void enregistrer() {
        // Validation
        if (etAnnee.getText().toString().isEmpty() ||
            etPrix.getText().toString().isEmpty() ||
            etImmat.getText().toString().isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Recuperer les valeurs
        int posMarque = spinnerMarque.getSelectedItemPosition();
        int posModele = spinnerModele.getSelectedItemPosition();
        
        if (posMarque < 0 || posModele < 0) {
            Toast.makeText(this, "Veuillez selectionner marque et modele", Toast.LENGTH_SHORT).show();
            return;
        }

        Marque marque = listeMarques.get(posMarque);
        Modele modele = listeModeles.get(posModele);

        int annee = Integer.parseInt(etAnnee.getText().toString());
        double prix = Double.parseDouble(etPrix.getText().toString());
        String immat = etImmat.getText().toString();
        boolean dispo = cbDispo.isChecked();

        // Creer ou modifier la voiture
        String id = voitureId != null ? voitureId : voituresRef.push().getKey();
        Voiture voiture = new Voiture(id, marque.getId(), modele.getId(), marque.getNom(),
                modele.getNom(), annee, prix, immat, dispo, "");

        voituresRef.child(id).setValue(voiture)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Voiture enregistree", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur d'enregistrement", Toast.LENGTH_SHORT).show();
                });
    }
}
