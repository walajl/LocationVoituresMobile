package com.example.gestionvoitures.adapter;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionvoitures.R;
import com.example.gestionvoitures.model.Marque;

import java.util.List;

/**
 * Adaptateur pour la liste des marques
 * Affiche les marques avec boutons modifier/supprimer
 */
public class MarqueAdapter extends RecyclerView.Adapter<MarqueAdapter.MarqueViewHolder> {

    /**
     * Interface pour les clics
     */
    public interface OnMarqueClickListener {
        void onModifier(Marque marque);
        void onSupprimer(Marque marque);
    }

    // Liste des marques
    private final List<Marque> liste;
    // Listener
    private final OnMarqueClickListener listener;

    /**
     * Constructeur
     */
    public MarqueAdapter(List<Marque> liste, OnMarqueClickListener listener) {
        this.liste = liste;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MarqueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_marque, parent, false);
        return new MarqueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarqueViewHolder holder, int position) {
        Marque marque = liste.get(position);
        
        // Afficher le nom de la marque
        holder.tvNom.setText(marque.getNom());
        
        // Boutons d'action
        holder.btnModifier.setOnClickListener(v -> listener.onModifier(marque));
        holder.btnSupprimer.setOnClickListener(v -> listener.onSupprimer(marque));
    }

    @Override
    public int getItemCount() {
        return liste.size();
    }

    /**
     * ViewHolder pour une marque
     */
    public static class MarqueViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom;
        ImageButton btnModifier, btnSupprimer;

        MarqueViewHolder(View view) {
            super(view);
            tvNom = view.findViewById(R.id.tvNom);
            btnModifier = view.findViewById(R.id.btnModifier);
            btnSupprimer = view.findViewById(R.id.btnSupprimer);
        }
    }
}
