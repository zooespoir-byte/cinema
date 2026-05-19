package fr.cinema.model;

import java.io.Serializable;

public class Film implements Serializable {
    private static final long serialVersionUID = 1L;
    private String titre; // [cite: 9]
    private String realisateur; // [cite: 9]

    public Film(String titre, String realisateur) {
        this.titre = titre;
        this.realisateur = realisateur;
    }

    public String getTitre() { return titre; }
    public String getRealisateur() { return realisateur; }

    @Override
    public String toString() {
        return "Film: " + titre + " | Réalisateur: " + realisateur;
    }
}