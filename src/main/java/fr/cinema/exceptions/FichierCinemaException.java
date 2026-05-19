package fr.cinema.exceptions;

public class FichierCinemaException extends Exception {
    public FichierCinemaException(String message) {
        super(message); // [cite: 39]
    }

    // Constructeur pour le chaînage d'exceptions [cite: 72]
    public FichierCinemaException(String message, Throwable cause) {
        super(message, cause); // [cite: 72]
    }
}