package fr.cinema.interfaces;

import fr.cinema.model.Film;
import fr.cinema.model.Salle;
import fr.cinema.model.Seance;
import fr.cinema.exceptions.FichierCinemaException;
import fr.cinema.exceptions.AuthenticationException;
import fr.cinema.exceptions.FilmIntrouvableException;

public interface IAdminCinema {
    void ajouterFilm(Film film) throws AuthenticationException; // [cite: 22, 55]
    void ajouterSalle(Salle salle) throws AuthenticationException; // [cite: 23, 55]
    void ajouterSeance(Seance seance) throws AuthenticationException; // [cite: 24, 55]
    double consulterChiffreAffaires() throws AuthenticationException; // [cite: 25, 55]
    double consulterTauxRemplissage(String titreFilm) throws FilmIntrouvableException, AuthenticationException; // [cite: 26, 55]
    void chargerFilmsDepuisFichier(String cheminFichier) throws FichierCinemaException; // [cite: 27]
    void serialiserDonnees(String cheminFichier) throws FichierCinemaException, AuthenticationException; // [cite: 28, 55]
}