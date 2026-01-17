package com.example.gestionvoitures.adapter;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionvoitures.R;
import com.example.gestionvoitures.model.Modele;

import java.util.List;

/**
 * Adaptateur pour la liste des modeles
 * Affiche les modeles avec boutons modifier/supprimer
 */
public class ModeleAdapter extends RecyclerView.Adapter<ModeleAdapter.ModeleViewHolder> {

    /**
     * Interface pour les clics
     */
    public interface OnModeleClickListener {
        void onModifier(Modele modele);
        void onSupprimer(Modele modele);
    }

    // Liste des modeles
    private final List<Modele> liste;
    // Listener
    private final OnModeleClickListener listener;

    /**
     * Constructeur
     */
    public ModeleAdapter(List<Modele> liste, OnModeleClickListener listener) {
        this.liste = liste;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ModeleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_modele, parent, false);
        return new ModeleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModeleViewHolder holder, int position) {
        Modele modele = liste.get(position);
        
        // Afficher le nom du modele
        holder.tvNom.setText(modele.getNom());
        
        // Boutons d'action
        holder.btnModifier.setOnClickListener(v -> listener.onModifier(modele));
        holder.btnSupprimer.setOnClickListener(v -> listener.onSupprimer(modele));
    }

    @Override
    public int getItemCount() {
        return liste.size();
    }

    /**
     * ViewHolder pour un modele
     */
    public static class ModeleViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom;
        ImageButton btnModifier, btnSupprimer;

        ModeleViewHolder(View view) {
            super(view);
            tvNom = view.findViewById(R.id.tvNom);
            btnModifier = view.findViewById(R.id.btnModifier);
            btnSupprimer = view.findViewById(R.id.btnSupprimer);
        }
    }
}
