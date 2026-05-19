package fr.cinema.interfaces;

import fr.cinema.model.Film;
import fr.cinema.model.Salle;
import fr.cinema.exceptions.FilmIntrouvableException;
import fr.cinema.exceptions.SalleIntrouvableException;
import fr.cinema.exceptions.PlaceIndisponibleException;
import java.util.List;

public interface IUserCinema {
    Film consulterFilm(String titre) throws FilmIntrouvableException; // [cite: 14, 43, 52]
    List<Film> consulterFilmsParMotCle(String motCle) throws FilmIntrouvableException; // [cite: 15, 43]
    Salle consulterSalle(int numero) throws SalleIntrouvableException; // [cite: 16, 43, 53]
    List<Film> consulterFilmsProgrammes(); // [cite: 17]
    void acheterPlace(String titreFilm, int numeroSalle) throws FilmIntrouvableException, PlaceIndisponibleException; // [cite: 18, 43, 54]
}