package com.example.gestionvoitures;



import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Formulaire de reservation
 * Cette activite est un placeholder pour une eventuelle
 * fonctionnalite de modification de reservation
 * (non implementee dans cette version)
 */
public class ReservationFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cette activite n'est pas utilisee dans la version actuelle
        // Les reservations sont faites directement depuis ClientVoituresActivity
        finish();
    }
}
