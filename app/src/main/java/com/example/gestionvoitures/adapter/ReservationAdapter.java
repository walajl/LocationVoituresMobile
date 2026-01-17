package com.example.gestionvoitures.adapter;



import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
 * Adaptateur pour la liste des reservations
 * Affiche les reservations avec leur statut
 * Permet d'accepter, annuler ou mettre en attente
 */
public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    /**
     * Interface pour gerer les clics sur les boutons
     */
    public interface OnReservationClickListener {
        void onAccepter(Reservation reservation);
        void onAnnuler(Reservation reservation);
        void onAttente(Reservation reservation);
    }

    // Liste des reservations
    private final List<Reservation> liste;
    // Listener pour les actions
    private final OnReservationClickListener listener;
    // Format de date
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);

    /**
     * Constructeur
     */
    public ReservationAdapter(List<Reservation> liste, OnReservationClickListener listener) {
        this.liste = liste;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflater le layout de l'item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = liste.get(position);

        // Afficher les informations
        // Afficher les informations
        holder.tvClient.setText(String.format("Client: %s", reservation.getUtilisateurNom()));
        holder.tvVoiture.setText(String.format("Voiture: %s", reservation.getVoitureNom()));
        String dates = String.format("%s - %s",
                dateFormat.format(new Date(reservation.getDateDebut())),
                dateFormat.format(new Date(reservation.getDateFin())));
        holder.tvDates.setText(dates);
        holder.tvPrix.setText(String.format(Locale.FRENCH, "%.1f DT", reservation.getPrixTotal()));

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

        // Configuration des boutons d'action
        holder.btnAccepter.setOnClickListener(v -> listener.onAccepter(reservation));
        holder.btnAnnuler.setOnClickListener(v -> listener.onAnnuler(reservation));
        holder.btnAttente.setOnClickListener(v -> listener.onAttente(reservation));
    }

    @Override
    public int getItemCount() {
        return liste.size();
    }

    /**
     * ViewHolder pour une reservation
     */
    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvClient, tvVoiture, tvDates, tvPrix, tvStatut;
        ImageButton btnAccepter, btnAnnuler, btnAttente;

        ReservationViewHolder(View view) {
            super(view);
            tvClient = view.findViewById(R.id.tvClient);
            tvVoiture = view.findViewById(R.id.tvVoiture);
            tvDates = view.findViewById(R.id.tvDates);
            tvPrix = view.findViewById(R.id.tvPrix);
            tvStatut = view.findViewById(R.id.tvStatut);
            btnAccepter = view.findViewById(R.id.btnAccepter);
            btnAnnuler = view.findViewById(R.id.btnAnnuler);
            btnAttente = view.findViewById(R.id.btnAttente);
        }
    }
}
