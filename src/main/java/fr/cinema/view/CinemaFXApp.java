package fr.cinema.view;

import fr.cinema.model.*;
import fr.cinema.service.Cinema;
import fr.cinema.exceptions.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Date;
import java.util.List;

public class CinemaFXApp extends Application {

    private Cinema cinemaService = new Cinema();
    private TextArea zoneConsole = new TextArea();
    private ListView<String> listeFilmsVisuels = new ListView<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🎬 Système de Gestion du Cinéma");

        initialiserDonneesDemo();

        BorderPane layoutPrincipal = new BorderPane();
        layoutPrincipal.setPadding(new Insets(15));

        Label labelTitre = new Label("Gestionnaire de Cinéma OOO");
        labelTitre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        layoutPrincipal.setTop(labelTitre);

        VBox zoneUtilisateur = new VBox(10);
        zoneUtilisateur.setPadding(new Insets(10));
        zoneUtilisateur.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-color: #ecf0f1;");
        zoneUtilisateur.setPrefWidth(350);

        Label titreUser = new Label("🛒 Espace Client / Vendeur");
        titreUser.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TextField champRecherche = new TextField();
        champRecherche.setPromptText("Mot-clé du film...");
        Button btnRechercher = new Button("🔍 Rechercher Film");

        TextField champSalle = new TextField();
        champSalle.setPromptText("Numéro de la salle...");
        Button btnAcheter = new Button("🎟️ Acheter 1 Place");

        zoneUtilisateur.getChildren().addAll(
                titreUser, new Separator(),
                new Label("Rechercher un film :"), champRecherche, btnRechercher,
                new Separator(),
                new Label("Acheter un billet :"), champSalle, btnAcheter
        );

        VBox zoneAdmin = new VBox(10);
        zoneAdmin.setPadding(new Insets(10));
        zoneAdmin.setStyle("-fx-border-color: #e74c3c; -fx-border-radius: 5; -fx-background-color: #f9ebea;");
        zoneAdmin.setPrefWidth(350);

        Label titreAdmin = new Label("🔒 Espace Administration");
        titreAdmin.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #c0392b;");

        Button btnConnexionAdmin = new Button("🔑 Connexion Admin");
        TextField champNouveauFilm = new TextField();
        champNouveauFilm.setPromptText("Titre du nouveau film...");
        TextField champNouveauReal = new TextField();
        champNouveauReal.setPromptText("Réalisateur...");
        Button btnAjouterFilm = new Button("➕ Ajouter le Film");

        Button btnChiffreAffaires = new Button("💰 Consulter Chiffre d'Affaires");

        zoneAdmin.getChildren().addAll(
                titreAdmin, new Separator(),
                btnConnexionAdmin, new Separator(),
                new Label("Nouveau Film :"), champNouveauFilm, champNouveauReal, btnAjouterFilm,
                new Separator(),
                btnChiffreAffaires
        );

        VBox zoneCentrale = new VBox(10);
        zoneCentrale.setPadding(new Insets(0, 15, 0, 15));

        Label titreFilms = new Label("🎞️ Films à l'affiche :");
        titreFilms.setStyle("-fx-font-weight: bold;");

        zoneConsole.setEditable(false);
        zoneConsole.setPrefHeight(150);
        zoneConsole.setPromptText("Log des actions de l'application...");

        zoneCentrale.getChildren().addAll(titreFilms, listeFilmsVisuels, new Label("📜 Console d'état :"), zoneConsole);

        layoutPrincipal.setLeft(zoneUtilisateur);
        layoutPrincipal.setCenter(zoneCentrale);
        layoutPrincipal.setRight(zoneAdmin);

        btnRechercher.setOnAction(e -> {
            String motCle = champRecherche.getText();
            try {
                List<Film> resultats = cinemaService.consulterFilmsParMotCle(motCle);
                log("🔍 " + resultats.size() + " film(s) trouvé(s) pour '" + motCle + "'.");
                listeFilmsVisuels.getItems().clear();
                resultats.forEach(f -> listeFilmsVisuels.getItems().add(f.toString()));
            } catch (FilmIntrouvableException ex) {
                log("❌ Erreur : " + ex.getMessage());
                alerteErreur("Film Introuvable", ex.getMessage());
            }
        });

        btnAcheter.setOnAction(e -> {
            String selection = listeFilmsVisuels.getSelectionModel().getSelectedItem();
            if (selection == null) {
                alerteErreur("Sélection requise", "Veuillez d'abord sélectionner un film dans la liste centrale.");
                return;
            }
            String titreFilm = selection.split("\\|")[0].replace("Film: ", "").trim();

            try {
                int numSalle = Integer.parseInt(champSalle.getText());
                cinemaService.acheterPlace(titreFilm, numSalle);
                log("🎟️ 1 place achetée avec succès pour le film : " + titreFilm + " en Salle " + numSalle);
            } catch (NumberFormatException ex) {
                alerteErreur("Format Incorrect", "Le numéro de salle doit être un nombre valide.");
            } catch (FilmIntrouvableException | PlaceIndisponibleException ex) {
                log("❌ Échec achat : " + ex.getMessage());
                alerteErreur("Erreur de Réservation", ex.getMessage());
            }
        });

        btnConnexionAdmin.setOnAction(e -> {
            cinemaService.connecterAdmin("admin", "admin123");
            log("🔒 Authentifié en tant qu'Administrateur.");
            btnConnexionAdmin.setText("✅ Connecté (Admin)");
            btnConnexionAdmin.setDisable(true);
        });

        btnAjouterFilm.setOnAction(e -> {
            try {
                String titre = champNouveauFilm.getText();
                String real = champNouveauReal.getText();
                if (titre.isEmpty() || real.isEmpty()) {
                    alerteErreur("Champs Vides", "Veuillez remplir le titre et le réalisateur.");
                    return;
                }
                cinemaService.ajouterFilm(new Film(titre, real));
                log("➕ Film ajouté : " + titre);
                rafraichirListeFilms();
                champNouveauFilm.clear();
                champNouveauReal.clear();
            } catch (AuthenticationException ex) {
                log("❌ Sécurité : " + ex.getMessage());
                alerteErreur("Accès Refusé", ex.getMessage());
            }
        });

        btnChiffreAffaires.setOnAction(e -> {
            try {
                double ca = cinemaService.consulterChiffreAffaires();
                log("💰 Chiffre d'Affaires Actuel : " + ca + " DH");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Chiffre d'Affaires");
                alert.setHeaderText(null);
                alert.setContentText("Le chiffre d'affaires total accumulé est de : " + ca + " DH");
                alert.showAndWait();
            } catch (AuthenticationException ex) {
                log("❌ Sécurité : " + ex.getMessage());
                alerteErreur("Accès Refusé", ex.getMessage());
            }
        });

        rafraichirListeFilms();

        Scene scene = new Scene(layoutPrincipal, 1000, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void log(String message) {
        zoneConsole.appendText(message + "\n");
    }

    private void alerteErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void rafraichirListeFilms() {
        listeFilmsVisuels.getItems().clear();
        cinemaService.consulterFilmsProgrammes().forEach(f -> listeFilmsVisuels.getItems().add(f.toString()));
    }

    private void initialiserDonneesDemo() {
        try {
            cinemaService.connecterAdmin("admin", "admin123");
            Film f1 = new Film("Inception", "Christopher Nolan");
            Film f2 = new Film("Avatar", "James Cameron");
            Film f3 = new Film("X3D", "Lana Wachowski");

            cinemaService.ajouterFilm(f1);
            cinemaService.ajouterFilm(f2);
            cinemaService.ajouterFilm(f3);

            Salle s1 = new SalleNormale(1, "Standard-A", 100);
            Salle s4 = new SalleVIP(4, "Premium Luxe", 50);
            cinemaService.ajouterSalle(s1);
            cinemaService.ajouterSalle(s4);

            cinemaService.ajouterSeance(new Seance(f3, s4, new Date()));
            cinemaService.ajouterSeance(new Seance(f1, s1, new Date()));

            cinemaService.deconnecter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}