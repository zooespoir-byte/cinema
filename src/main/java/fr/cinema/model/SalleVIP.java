package fr.cinema.model;

public class SalleVIP extends Salle { // [cite: 6]
    public SalleVIP(int numero, String nom, int nombrePlaces) {
        super(numero, nom, nombrePlaces);
    }

    @Override
    public double getPrixPlace() {
        return 60.0; // Prix fixé à 60 DH [cite: 8]
    }
}