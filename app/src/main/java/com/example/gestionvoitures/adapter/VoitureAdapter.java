package com.example.gestionvoitures.adapter;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionvoitures.R;
import com.example.gestionvoitures.model.Voiture;

import java.util.List;

import java.util.Locale;

/**
 * Adaptateur pour la liste des voitures
 * Utilise RecyclerView pour afficher les voitures
 * Permet de modifier ou supprimer une voiture
 */
public class VoitureAdapter extends RecyclerView.Adapter<VoitureAdapter.VoitureViewHolder> {

    /**
     * Interface pour les clics sur les boutons
     * Permet de communiquer avec l'activite parente
     */
    public interface OnVoitureClickListener {
        void onModifier(Voiture voiture);
        void onSupprimer(Voiture voiture);
    }

    // Liste des voitures a afficher
    private final List<Voiture> liste;
    
    // Listener pour les actions
    private final OnVoitureClickListener listener;

    /**
     * Constructeur de l'adaptateur
     */
    public VoitureAdapter(List<Voiture> liste, OnVoitureClickListener listener) {
        this.liste = liste;
        this.listener = listener;
    }

    /**
     * Creation du ViewHolder
     * Appele quand RecyclerView a besoin d'un nouveau ViewHolder
     */
    @NonNull
    @Override
    public VoitureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflater le layout de l'item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voiture, parent, false);
        return new VoitureViewHolder(view);
    }

    /**
     * Liaison des donnees au ViewHolder
     * Appele pour afficher les donnees a la position donnee
     */
    @Override
    public void onBindViewHolder(@NonNull VoitureViewHolder holder, int position) {
        // Recuperer la voiture a cette position
        Voiture voiture = liste.get(position);
        
        // Afficher les informations
        holder.tvMarqueModele.setText(String.format("%s %s", voiture.getMarqueNom(), voiture.getModeleNom()));
        holder.tvAnnee.setText(String.format(Locale.FRENCH, "Annee: %d", voiture.getAnnee()));
        holder.tvPrix.setText(String.format(Locale.FRENCH, "%.1f DT/jour", voiture.getPrix()));
        holder.tvImmat.setText(voiture.getImmatriculation());

        // Afficher la disponibilite avec couleur
        if (voiture.isDisponible()) {
            holder.tvDispo.setText("Disponible");
            holder.tvDispo.setTextColor(holder.itemView.getContext()
                    .getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvDispo.setText("Non disponible");
            holder.tvDispo.setTextColor(holder.itemView.getContext()
                    .getColor(android.R.color.holo_red_dark));
        }

        // Configuration des boutons d'action
        holder.btnModifier.setOnClickListener(v -> listener.onModifier(voiture));
        holder.btnSupprimer.setOnClickListener(v -> listener.onSupprimer(voiture));
    }

    /**
     * Retourne le nombre d'elements dans la liste
     */
    @Override
    public int getItemCount() {
        return liste.size();
    }

    /**
     * ViewHolder pour une voiture
     * Contient les references aux vues de l'item
     */
    public static class VoitureViewHolder extends RecyclerView.ViewHolder {
        TextView tvMarqueModele, tvAnnee, tvPrix, tvImmat, tvDispo;
        ImageButton btnModifier, btnSupprimer;

        VoitureViewHolder(View view) {
            super(view);
            // Recuperation des vues par leur ID
            tvMarqueModele = view.findViewById(R.id.tvMarqueModele);
            tvAnnee = view.findViewById(R.id.tvAnnee);
            tvPrix = view.findViewById(R.id.tvPrix);
            tvImmat = view.findViewById(R.id.tvImmat);
            tvDispo = view.findViewById(R.id.tvDispo);
            btnModifier = view.findViewById(R.id.btnModifier);
            btnSupprimer = view.findViewById(R.id.btnSupprimer);
        }
    }
}
