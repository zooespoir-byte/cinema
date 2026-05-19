package fr.cinema.model;

import fr.cinema.exceptions.PlaceIndisponibleException;
import java.io.Serializable;
import java.util.Date;

public class Seance implements Serializable {
    private static final long serialVersionUID = 1L;
    private Film film; // [cite: 10]
    private Salle salle; // [cite: 10]
    private Date dateProjection; // [cite: 11]
    private int placesVendues; // [cite: 11]

    public Seance(Film film, Salle salle, Date dateProjection) {
        // Illustration Q8.d : Validation dans le constructeur
        if (salle == null || salle.getNombrePlaces() <= 0) {
            throw new IllegalArgumentException("Impossible de créer la séance : La salle a un nombre de places nul ou négatif."); //
        }
        this.film = film;
        this.salle = salle;
        this.dateProjection = dateProjection;
        this.placesVendues = 0;
    }

    public void vendrePlace(int nombre) throws PlaceIndisponibleException { // [cite: 12, 41]
        if (this.placesVendues + nombre > salle.getNombrePlaces()) {
            throw new PlaceIndisponibleException("Places insuffisantes ! Restantes : "
                    + (salle.getNombrePlaces() - this.placesVendues) + ", Demandées : " + nombre); // [cite: 33, 41]
        }
        this.placesVendues += nombre; // [cite: 12]
    }

    public Film getFilm() { return film; }
    public Salle getSalle() { return salle; }
    public Date getDateProjection() { return dateProjection; }
    public int getPlacesVendues() { return placesVendues; }

    public double getChiffreAffaires() {
        return this.placesVendues * salle.getPrixPlace();
    }
}