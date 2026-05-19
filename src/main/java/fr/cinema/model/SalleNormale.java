package fr.cinema.model;

public class SalleNormale extends Salle { // [cite: 6]
    public SalleNormale(int numero, String nom, int nombrePlaces) {
        super(numero, nom, nombrePlaces);
    }

    @Override
    public double getPrixPlace() {
        return 30.0; // Prix fixé à 30 DH [cite: 8]
    }
}