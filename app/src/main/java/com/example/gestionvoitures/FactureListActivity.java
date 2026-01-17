package com.example.gestionvoitures;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionvoitures.adapter.FactureAdapter;
import com.example.gestionvoitures.model.Facture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Activite liste des factures
 * Affiche toutes les factures generees
 * Une facture est creee quand une reservation est acceptee
 */
public class FactureListActivity extends AppCompatActivity {

    // Composants UI
    private RecyclerView recyclerView;
    private FactureAdapter adapter;
    
    // Liste des factures
    private List<Facture> listeFactures = new ArrayList<>();
    
    // Reference Firebase
    private DatabaseReference facturesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facture_list);

        // Initialisation Firebase
        facturesRef = FirebaseDatabase.getInstance().getReference().child("factures");

        // Configuration du RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FactureAdapter(listeFactures);
        recyclerView.setAdapter(adapter);

        // Charger les factures
        chargerFactures();
    }

    /**
     * Charger les factures depuis Firebase
     */
    private void chargerFactures() {
        facturesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listeFactures.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Facture facture = child.getValue(Facture.class);
                    if (facture != null) {
                        facture.setId(child.getKey());
                        listeFactures.add(facture);
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
}
