package fr.cinema.exceptions;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message); // [cite: 39]
    }
}