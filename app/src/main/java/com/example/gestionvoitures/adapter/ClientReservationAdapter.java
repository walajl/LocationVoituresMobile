package com.example.gestionvoitures.adapter;



import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionvoitures.R;
import com.example.gestionvoitures.model.Reservation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adaptateur pour les reservations du client
 * Affiche l'historique des reservations du client connecte
 */
public class ClientReservationAdapter extends RecyclerView.Adapter<ClientReservationAdapter.ReservationViewHolder> {

    // Liste des reservations
    private final List<Reservation> liste;
    // Format de date
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);

    /**
     * Constructeur
     */
    public ClientReservationAdapter(List<Reservation> liste) {
        this.liste = liste;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = liste.get(position);

        // Afficher les informations
        holder.tvVoiture.setText(reservation.getVoitureNom());
        
        String dates = String.format("%s - %s", 
            dateFormat.format(new Date(reservation.getDateDebut())), 
            dateFormat.format(new Date(reservation.getDateFin())));
        holder.tvDates.setText(dates);
        
        String prix = String.format(Locale.FRENCH, "%.1f DT", reservation.getPrixTotal());
        holder.tvPrix.setText(prix);

        // Afficher le statut avec couleur
        holder.tvStatut.setText(reservation.getStatut().toUpperCase());
        switch (reservation.getStatut()) {
            case "acceptee":
                holder.tvStatut.setTextColor(Color.parseColor("#4CAF50")); // Vert
                break;
            case "annulee":
                holder.tvStatut.setTextColor(Color.parseColor("#F44336")); // Rouge
                break;
            default:
                holder.tvStatut.setTextColor(Color.parseColor("#FF9800")); // Orange
                break;
        }
    }

    @Override
    public int getItemCount() {
        return liste.size();
    }

    /**
     * ViewHolder pour une reservation client
     */
    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvVoiture, tvDates, tvPrix, tvStatut;

        public ReservationViewHolder(View view) {
            super(view);
            tvVoiture = view.findViewById(R.id.tvVoiture);
            tvDates = view.findViewById(R.id.tvDates);
            tvPrix = view.findViewById(R.id.tvPrix);
            tvStatut = view.findViewById(R.id.tvStatut);
        }
    }
}
