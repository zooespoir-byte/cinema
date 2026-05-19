package fr.cinema.model;

import java.io.Serializable;

public abstract class Salle implements Serializable {
    private static final long serialVersionUID = 1L;
    private int numero; // [cite: 5]
    private String nom; // [cite: 5]
    private int nombrePlaces; // [cite: 5]

    public Salle(int numero, String nom, int nombrePlaces) {
        this.numero = numero;
        this.nom = nom;
        this.nombrePlaces = nombrePlaces;
    }

    public int getNumero() { return numero; }
    public String getNom() { return nom; }
    public int getNombrePlaces() { return nombrePlaces; }

    // Méthode abstraite pour récupérer le prix selon le type [cite: 7]
    public abstract double getPrixPlace();

    @Override
    public String toString() {
        return "Salle N°" + numero + " (" + nom + ") - " + nombrePlaces + " places";
    }
}