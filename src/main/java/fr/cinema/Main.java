package fr.cinema;

import fr.cinema.model.*;
import fr.cinema.service.Cinema;
import fr.cinema.exceptions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== INITIALISATION DE L'APPLICATION CINÉMA ===");

        // Étape préliminaire : création du fichier texte 'cinema.txt' requis [cite: 46, 47]
        creerFichierExemple();

        // a. Créer un nouvel objet Cinema [cite: 57]
        Cinema monCinema = new Cinema();

        // b. Charger les films à partir d'un fichier texte avec try-catch [cite: 57, 61]
        try {
            System.out.println("\n[Action] Chargement des films...");
            monCinema.chargerFilmsDepuisFichier("cinema.txt");
            System.out.println("-> Films chargés avec succès !");
        } catch (FichierCinemaException e) {
            System.out.println("❌ Erreur de fichier détectée : " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("   Cause technique d'origine : " + e.getCause()); // Démonstration chaînage [cite: 72]
            }
        }

        // Pour pouvoir effectuer les opérations Admin (c, d, e, f, g, h), on se connecte [cite: 29]
        monCinema.connecterAdmin("admin", "admin123");

        try {
            // c. Ajouter un nouveau film [cite: 58]
            System.out.println("\n[Action] Ajout du film X3D...");
            Film x3d = new Film("X3D", "Lana Wachowski");
            monCinema.ajouterFilm(x3d);

            // d. Ajouter une nouvelle salle [cite: 58]
            System.out.println("[Action] Ajout de la salle 4 (VIP)...");
            Salle salle4 = new SalleVIP(4, "Premium Suite", 50); // Salle VIP [cite: 6]
            monCinema.ajouterSalle(salle4);

            // e. Ajouter une nouvelle séance concernant le film "X3D" dans la salle 4 [cite: 59]
            System.out.println("[Action] Programmation de la séance pour X3D dans la salle 4...");
            Seance seanceX3D = new Seance(x3d, salle4, new Date());
            monCinema.ajouterSeance(seanceX3D);

            // Simulation d'une vente de places pour avoir du chiffre d'affaires
            monCinema.vendrePlace("X3D", 4, 10); // Vente de 10 places VIP (10 * 60 = 600 DH) [cite: 8, 20]

            // f. Consulter le chiffre d'affaires [cite: 59]
            System.out.println("\n[Consultation] Chiffre d'Affaires global :");
            double ca = monCinema.consulterChiffreAffaires();
            System.out.println("-> CA Total : " + ca + " DH"); // Doit afficher 600.0 DH [cite: 8]

            // g. Consulter le taux de remplissage pour le film "X3D" [cite: 60]
            System.out.println("\n[Consultation] Taux de remplissage :");
            double taux = monCinema.consulterTauxRemplissage("X3D");
            System.out.println("-> Taux de remplissage pour X3D : " + taux + "%"); // (10/50)*100 = 20%

            // h. Sérialiser les données du cinéma [cite: 60]
            System.out.println("\n[Action] Sauvegarde (Sérialisation) de l'état du cinéma...");
            monCinema.serialiserDonnees("cinema_backup.dat");
            System.out.println("-> Données sauvegardées avec succès.");

        } catch (AuthenticationException e) {
            System.out.println("❌ Erreur de Sécurité : " + e.getMessage());
        } catch (FilmIntrouvableException e) {
            System.out.println("❌ Erreur Métier : Film non trouvé -> " + e.getMessage());
        } catch (PlaceIndisponibleException e) {
            System.out.println("❌ Erreur Guichet : " + e.getMessage());
        } catch (FichierCinemaException e) {
            System.out.println("❌ Erreur d'écriture : " + e.getMessage());
        } finally {
            // Déconnexion finale sécurisée
            monCinema.deconnecter();
            System.out.println("\n=== FIN DU PROGRAMME ===");
        }
    }

    // Fonction utilitaire pour instancier le fichier initial de test
    private static void creerFichierExemple() {
        try (FileWriter writer = new FileWriter("cinema.txt")) {
            writer.write("Inception;Christopher Nolan\n"); // [cite: 47]
            writer.write("Avatar;James Cameron\n"); // [cite: 47]
            writer.write("Interstellar;Christopher Nolan\n"); // [cite: 47]
        } catch (IOException e) {
            System.out.println("Impossible de générer le fichier de démonstration cinema.txt");
        }
    }
}