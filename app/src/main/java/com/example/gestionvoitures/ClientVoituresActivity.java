package com.example.gestionvoitures;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionvoitures.adapter.ClientVoitureAdapter;
import com.example.gestionvoitures.model.Marque;
import com.example.gestionvoitures.model.Modele;
import com.example.gestionvoitures.model.Reservation;
import com.example.gestionvoitures.model.Voiture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Activite pour afficher les voitures disponibles
 * Le client peut rechercher et reserver des voitures
 */
public class ClientVoituresActivity extends AppCompatActivity implements ClientVoitureAdapter.OnReserverClickListener {

    // les vues
    private RecyclerView recyclerView;
    private ClientVoitureAdapter adapter;
    private TextView tvEmpty, tvResultCount;
    private AutoCompleteTextView autoCompleteRecherche;
    private Button btnReinitialiser;
    
    // les listes
    private List<Voiture> toutesLesVoitures = new ArrayList<>();
    private List<Voiture> voituresFiltrees = new ArrayList<>();
    private List<Marque> listeMarques = new ArrayList<>();
    private List<Modele> listeModeles = new ArrayList<>();
    
    // pour autocomplete
    private List<String> suggestionsRecherche = new ArrayList<>();
    private ArrayAdapter<String> adapterAutoComplete;
    
    // filtre actuel
    private String texteRecherche = "";
    
    // firebase
    private DatabaseReference voituresRef, reservationsRef, utilisateursRef, marquesRef, modelesRef;
    private FirebaseAuth auth;
    
    // format date
    private SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_voitures);

        // init firebase
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        voituresRef = db.getReference().child("voitures");
        reservationsRef = db.getReference().child("reservations");
        utilisateursRef = db.getReference().child("utilisateurs");
        marquesRef = db.getReference().child("marques");
        modelesRef = db.getReference().child("modeles");
        auth = FirebaseAuth.getInstance();

        // recuperer les vues
        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvResultCount = findViewById(R.id.tvResultCount);
        autoCompleteRecherche = findViewById(R.id.autoCompleteRecherche);
        btnReinitialiser = findViewById(R.id.btnReinitialiser);

        // configurer recyclerview
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClientVoitureAdapter(voituresFiltrees, this);
        recyclerView.setAdapter(adapter);

        // configurer autocomplete
        configurerAutoComplete();
        
        // bouton reinitialiser
        btnReinitialiser.setOnClickListener(v -> reinitialiser());

        // charger les donnees
        chargerMarques();
        chargerModeles();
        chargerVoitures();
    }

    /**
     * Configurer l'autocomplete pour la recherche
     */
    private void configurerAutoComplete() {
        adapterAutoComplete = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, suggestionsRecherche);
        autoCompleteRecherche.setAdapter(adapterAutoComplete);
        
        // quand on tape du texte
        autoCompleteRecherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                texteRecherche = s.toString().toLowerCase();
                filtrerVoitures();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // quand on clique sur une suggestion
        autoCompleteRecherche.setOnItemClickListener((parent, view, position, id) -> {
            String selection = parent.getItemAtPosition(position).toString();
            texteRecherche = selection.toLowerCase().trim();
            filtrerVoitures();
        });
    }

    /**
     * Mettre a jour les suggestions pour l'autocomplete
     */
    private void mettreAJourSuggestions() {
        suggestionsRecherche.clear();
        
        // ajouter toutes les marques
        for (Marque m : listeMarques) {
            if (!suggestionsRecherche.contains(m.getNom())) {
                suggestionsRecherche.add(m.getNom());
            }
        }
        
        // ajouter tous les modeles
        for (Modele m : listeModeles) {
            if (!suggestionsRecherche.contains(m.getNom())) {
                suggestionsRecherche.add(m.getNom());
            }
        }
        
        adapterAutoComplete.notifyDataSetChanged();
    }

    /**
     * Charger les marques depuis firebase
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
                mettreAJourSuggestions();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Charger les modeles depuis firebase
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
                mettreAJourSuggestions();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Charger les voitures disponibles
     */
    private void chargerVoitures() {
        voituresRef.orderByChild("disponible").equalTo(true)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        toutesLesVoitures.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Voiture v = child.getValue(Voiture.class);
                            if (v != null) {
                                v.setId(child.getKey());
                                toutesLesVoitures.add(v);
                            }
                        }
                        filtrerVoitures();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    /**
     * Filtrer les voitures selon les criteres
     */
    private void filtrerVoitures() {
        voituresFiltrees.clear();
        
        for (Voiture v : toutesLesVoitures) {
            // verifier texte recherche (autocomplete)
            boolean okTexte = texteRecherche.isEmpty() || 
                v.getMarqueNom().toLowerCase().contains(texteRecherche) ||
                v.getModeleNom().toLowerCase().contains(texteRecherche);
            
            if (okTexte) {
                voituresFiltrees.add(v);
            }
        }
        
        adapter.notifyDataSetChanged();
        afficherResultat();
    }

    /**
     * Afficher le nombre de resultats
     */
    private void afficherResultat() {
        int nb = voituresFiltrees.size();
        tvResultCount.setText(nb + " voiture(s) trouvée(s)");
        
        if (nb == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setText("Aucune voiture trouvée");
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Reinitialiser tous les filtres
     */
    private void reinitialiser() {
        autoCompleteRecherche.setText("");
        texteRecherche = "";
        filtrerVoitures();
        Toast.makeText(this, "Filtres réinitialisés", Toast.LENGTH_SHORT).show();
    }

    /**
     * Quand on clique sur reserver
     */
    @Override
    public void onReserver(Voiture voiture) {
        final long[] dateDebut = {0};
        final long[] dateFin = {0};

        Calendar cal = Calendar.getInstance();
        
        // choisir date debut
        new DatePickerDialog(this, (view, year, month, day) -> {
            cal.set(year, month, day, 0, 0, 0);
            dateDebut[0] = cal.getTimeInMillis();
            
            // choisir date fin
            new DatePickerDialog(this, (view2, year2, month2, day2) -> {
                cal.set(year2, month2, day2, 23, 59, 59);
                dateFin[0] = cal.getTimeInMillis();
                
                if (dateFin[0] <= dateDebut[0]) {
                    Toast.makeText(this, "Date fin doit etre apres date debut!", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                creerReservation(voiture, dateDebut[0], dateFin[0]);
                
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
            
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Creer une reservation dans firebase
     */
    private void creerReservation(Voiture voiture, long dateDebut, long dateFin) {
        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();
        
        utilisateursRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nomClient = "Client";
                if (snapshot.exists()) {
                    String nom = snapshot.child("nom").getValue(String.class);
                    if (nom != null) nomClient = nom;
                }

                // calculer prix
                long diff = dateFin - dateDebut;
                int nbJours = (int) (diff / (1000 * 60 * 60 * 24)) + 1;
                double prixTotal = voiture.getPrix() * nbJours;

                // creer reservation
                String id = reservationsRef.push().getKey();
                Reservation resa = new Reservation(
                        id,
                        userId,
                        nomClient,
                        voiture.getId(),
                        voiture.getMarqueNom() + " " + voiture.getModeleNom(),
                        dateDebut,
                        dateFin,
                        "en_attente",
                        prixTotal,
                        System.currentTimeMillis()
                );

                reservationsRef.child(id).setValue(resa)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ClientVoituresActivity.this, 
                                    "Reservation envoyee! " + nbJours + " jours = " + prixTotal + " DT", 
                                    Toast.LENGTH_LONG).show();
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
