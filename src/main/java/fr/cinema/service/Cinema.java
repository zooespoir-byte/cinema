package fr.cinema.service;

import fr.cinema.interfaces.IAdminCinema;
import fr.cinema.interfaces.IUserCinema;
import fr.cinema.interfaces.IVendeurCinema;
import fr.cinema.model.*;
import fr.cinema.exceptions.*;

import java.io.*;
import java.util.*;

public class Cinema implements IUserCinema, IVendeurCinema, IAdminCinema, Serializable {
    private static final long serialVersionUID = 1L;

    private List<Salle> salles = new ArrayList<>(); // [cite: 5]
    private List<Film> films = new ArrayList<>(); // [cite: 9]
    private List<Seance> seances = new ArrayList<>(); // [cite: 10]

    // États de connexion simulés [cite: 29]
    private boolean estVendeurConnecte = false;
    private boolean estAdminConnecte = false;

    // Méthodes de simulation de sécurité [cite: 29]
    public void connecterVendeur(String login, String mdp) {
        if ("vendeur".equals(login) && "vendeur123".equals(mdp)) {
            this.estVendeurConnecte = true;
        }
    }

    public void connecterAdmin(String login, String mdp) {
        if ("admin".equals(login) && "admin123".equals(mdp)) {
            this.estAdminConnecte = true;
        }
    }

    public void deconnecter() {
        this.estVendeurConnecte = false;
        this.estAdminConnecte = false;
    }

    // --- INTERFACE USER ---
    @Override
    public Film consulterFilm(String titre) throws FilmIntrouvableException { // [cite: 14, 52]
        return films.stream()
                .filter(f -> f.getTitre().equalsIgnoreCase(titre))
                .findFirst()
                .orElseThrow(() -> new FilmIntrouvableException("Le film '" + titre + "' n'existe pas dans le cinéma.")); //
    }

    @Override
    public List<Film> consulterFilmsParMotCle(String motCle) throws FilmIntrouvableException { // [cite: 15]
        List<Film> result = films.stream()
                .filter(f -> f.getTitre().toLowerCase().contains(motCle.toLowerCase()))
                .toList();
        if (result.isEmpty()) {
            throw new FilmIntrouvableException("Aucun film ne contient le mot-clé : " + motCle); // [cite: 34]
        }
        return result;
    }

    @Override
    public Salle consulterSalle(int numero) throws SalleIntrouvableException { // [cite: 16, 53]
        return salles.stream()
                .filter(s -> s.getNumero() == numero)
                .findFirst()
                .orElseThrow(() -> new SalleIntrouvableException("La salle N°" + numero + " est introuvable.")); // [cite: 53]
    }

    @Override
    public List<Film> consulterFilmsProgrammes() { // [cite: 17]
        return seances.stream().map(Seance::getFilm).distinct().toList();
    }

    @Override
    public void acheterPlace(String titreFilm, int numeroSalle) throws FilmIntrouvableException, PlaceIndisponibleException { // [cite: 18]
        // Recherche de la séance associée
        Seance seance = seances.stream()
                .filter(s -> s.getFilm().getTitre().equalsIgnoreCase(titreFilm) && s.getSalle().getNumero() == numeroSalle)
                .findFirst()
                .orElseThrow(() -> new FilmIntrouvableException("Aucune séance programmée pour ce film dans cette salle."));

        seance.vendrePlace(1); // Propagation de PlaceIndisponibleException [cite: 54]
    }


    // --- INTERFACE VENDEUR ---
    @Override
    public void vendrePlace(String titreFilm, int numeroSalle, int nombre)
            throws FilmIntrouvableException, PlaceIndisponibleException, AuthenticationException { // [cite: 20]
        if (!estVendeurConnecte && !estAdminConnecte) {
            throw new AuthenticationException("Action refusée : Vous devez être authentifié en tant que Vendeur."); // [cite: 55]
        }
        Seance seance = seances.stream()
                .filter(s -> s.getFilm().getTitre().equalsIgnoreCase(titreFilm) && s.getSalle().getNumero() == numeroSalle)
                .findFirst()
                .orElseThrow(() -> new FilmIntrouvableException("Aucune séance programmée pour ce film dans cette salle."));

        seance.vendrePlace(nombre); // Propagation automatique [cite: 54]
    }


    // --- INTERFACE ADMIN ---
    @Override
    public void ajouterFilm(Film film) throws AuthenticationException { // [cite: 22]
        if (!estAdminConnecte) throw new AuthenticationException("Droits insuffisants. Admin requis."); // [cite: 55]
        films.add(film);
    }

    @Override
    public void ajouterSalle(Salle salle) throws AuthenticationException { // [cite: 23]
        if (!estAdminConnecte) throw new AuthenticationException("Droits insuffisants. Admin requis."); // [cite: 55]
        salles.add(salle);
    }

    @Override
    public void ajouterSeance(Seance seance) throws AuthenticationException { // [cite: 24]
        if (!estAdminConnecte) throw new AuthenticationException("Droits insuffisants. Admin requis."); // [cite: 55]
        seances.add(seance);
    }

    @Override
    public double consulterChiffreAffaires() throws AuthenticationException { // [cite: 25]
        if (!estAdminConnecte) throw new AuthenticationException("Droits insuffisants. Admin requis."); // [cite: 55]
        return seances.stream().mapToDouble(Seance::getChiffreAffaires).sum();
    }

    @Override
    public double consulterTauxRemplissage(String titreFilm) throws FilmIntrouvableException, AuthenticationException { // [cite: 26]
        if (!estAdminConnecte) throw new AuthenticationException("Droits insuffisants. Admin requis."); // [cite: 55]

        List<Seance> seancesFilm = seances.stream()
                .filter(s -> s.getFilm().getTitre().equalsIgnoreCase(titreFilm))
                .toList();

        if (seancesFilm.isEmpty()) throw new FilmIntrouvableException("Aucune séance pour calculer le taux du film : " + titreFilm);

        int placesTotalesOffertes = seancesFilm.stream().mapToInt(s -> s.getSalle().getNombrePlaces()).sum();
        int placesTotalesVendues = seancesFilm.stream().mapToInt(Seance::getPlacesVendues).sum();

        if (placesTotalesOffertes == 0) return 0.0;
        return ((double) placesTotalesVendues / placesTotalesOffertes) * 100;
    }

    @Override
    public void chargerFilmsDepuisFichier(String cheminFichier) throws FichierCinemaException { // [cite: 27, 48]
        BufferedReader reader = null;
        int numLigne = 0;
        try {
            reader = new BufferedReader(new FileReader(cheminFichier));
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                numLigne++;
                String[] tokens = ligne.split(";"); // Séparateur ';' [cite: 47]
                if (tokens.length != 2) {
                    // [cite: 50]
                    throw new FichierCinemaException("Erreur Format Ligne " + numLigne + " : Attendu 'titre;realisateur'.");
                }
                films.add(new Film(tokens[0].trim(), tokens[1].trim()));
            }
        } catch (FileNotFoundException e) {
            // Chaînage de l'exception native FileNotFoundException [cite: 49, 72]
            throw new FichierCinemaException("Fichier introuvable à l'adresse : " + cheminFichier, e);
        } catch (IOException e) {
            // Chaînage de l'exception globale d'entrée/sortie [cite: 72]
            throw new FichierCinemaException("Erreur technique lors de la lecture du fichier.", e);
        } finally {
            // Question 8.c : Le bloc finally s'exécute TOUJOURS pour libérer la ressource
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Impossible de fermer proprement le fichier.");
                }
            }
        }
    }

    @Override
    public void serialiserDonnees(String cheminFichier) throws FichierCinemaException, AuthenticationException { //
        if (!estAdminConnecte) throw new AuthenticationException("Droits insuffisants. Admin requis."); // [cite: 55]

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cheminFichier))) {
            oos.writeObject(this);
        } catch (IOException e) {
            throw new FichierCinemaException("Échec de la sérialisation des données du cinéma.", e); // [cite: 72]
        }
    }
}