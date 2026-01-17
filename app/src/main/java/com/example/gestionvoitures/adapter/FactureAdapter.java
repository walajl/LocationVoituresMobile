package com.example.gestionvoitures.adapter;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionvoitures.R;
import com.example.gestionvoitures.model.Facture;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adaptateur pour la liste des factures
 * Affiche les factures avec les details de paiement
 */
public class FactureAdapter extends RecyclerView.Adapter<FactureAdapter.FactureViewHolder> {

    // Liste des factures
    private final List<Facture> liste;
    // Format de date
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);

    /**
     * Constructeur
     */
    public FactureAdapter(List<Facture> liste) {
        this.liste = liste;
    }

    @NonNull
    @Override
    public FactureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_facture, parent, false);
        return new FactureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FactureViewHolder holder, int position) {
        Facture facture = liste.get(position);

        // Afficher les informations de la facture
        holder.tvNumero.setText(String.format(Locale.FRENCH, "Facture #%d", position + 1));
        holder.tvClient.setText(String.format("Client: %s", facture.getUtilisateurNom()));
        holder.tvVoiture.setText(String.format("Voiture: %s", facture.getVoitureNom()));
        
        String dates = String.format(Locale.FRENCH, "%s - %s (%d jours)",
                dateFormat.format(new Date(facture.getDateDebut())),
                dateFormat.format(new Date(facture.getDateFin())),
                facture.getNombreJours());
        holder.tvDates.setText(dates);
        
        holder.tvMontant.setText(String.format(Locale.FRENCH, "Montant: %.1f DT", facture.getMontantTotal()));
    }

    @Override
    public int getItemCount() {
        return liste.size();
    }

    /**
     * ViewHolder pour une facture
     */
    public static class FactureViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumero, tvClient, tvVoiture, tvDates, tvMontant;

        public FactureViewHolder(View view) {
            super(view);
            tvNumero = view.findViewById(R.id.tvNumero);
            tvClient = view.findViewById(R.id.tvClient);
            tvVoiture = view.findViewById(R.id.tvVoiture);
            tvDates = view.findViewById(R.id.tvDates);
            tvMontant = view.findViewById(R.id.tvMontant);
        }
    }
}
