package com.example.gestionvoitures.adapter;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionvoitures.R;
import com.example.gestionvoitures.model.Voiture;

import java.util.List;

import java.util.Locale;

/**
 * Adaptateur pour la liste des voitures (cote client)
 * Affiche les voitures disponibles avec bouton reserver
 */
public class ClientVoitureAdapter extends RecyclerView.Adapter<ClientVoitureAdapter.VoitureViewHolder> {

    /**
     * Interface pour le clic sur reserver
     */
    public interface OnReserverClickListener {
        void onReserver(Voiture voiture);
    }

    // Liste des voitures
    private final List<Voiture> liste;
    // Listener
    private final OnReserverClickListener listener;

    /**
     * Constructeur
     */
    public ClientVoitureAdapter(List<Voiture> liste, OnReserverClickListener listener) {
        this.liste = liste;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VoitureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client_voiture, parent, false);
        return new VoitureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoitureViewHolder holder, int position) {
        Voiture voiture = liste.get(position);
        
        // Afficher les informations de la voiture
        holder.tvMarqueModele.setText(String.format("%s %s", voiture.getMarqueNom(), voiture.getModeleNom()));
        holder.tvAnnee.setText(String.format(Locale.FRENCH, "Annee: %d", voiture.getAnnee()));
        holder.tvPrix.setText(String.format(Locale.FRENCH, "%.1f DT/jour", voiture.getPrix()));
        
        // Bouton reserver
        holder.btnReserver.setOnClickListener(v -> listener.onReserver(voiture));
    }

    @Override
    public int getItemCount() {
        return liste.size();
    }

    /**
     * ViewHolder pour une voiture client
     */
    public static class VoitureViewHolder extends RecyclerView.ViewHolder {
        TextView tvMarqueModele, tvAnnee, tvPrix;
        Button btnReserver;

        VoitureViewHolder(View view) {
            super(view);
            tvMarqueModele = view.findViewById(R.id.tvMarqueModele);
            tvAnnee = view.findViewById(R.id.tvAnnee);
            tvPrix = view.findViewById(R.id.tvPrix);
            btnReserver = view.findViewById(R.id.btnReserver);
        }
    }
}
