package fr.cinema.interfaces;

import fr.cinema.exceptions.FilmIntrouvableException;
import fr.cinema.exceptions.PlaceIndisponibleException;
import fr.cinema.exceptions.AuthenticationException;

public interface IVendeurCinema extends IUserCinema { // Le vendeur hérite des droits utilisateur [cite: 19]
    void vendrePlace(String titreFilm, int numeroSalle, int nombre)
            throws FilmIntrouvableException, PlaceIndisponibleException, AuthenticationException; // [cite: 20, 43, 54, 55]
}