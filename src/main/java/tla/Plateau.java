package tla;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.List;

import static tla.Direction.*;

class Plateau {

    final String message = "r : redémarrer la partie / q : quitter pour le menu";

    final static int LARGEUR_CARREAU = 32; // dimension des cotés d'un carreau en nombre de pixel
    static int LARGEUR_PLATEAU = 20; // largeur du plateau en nombre de carreau (par défaut)
    static int HAUTEUR_PLATEAU = 14; // hauteur du plateau en nombre de carreau (par défaut)

    // grille

    protected Carreau carreaux[] = new Carreau[LARGEUR_PLATEAU * HAUTEUR_PLATEAU];

    // coordonnées du joueur

    private int joueur_x;
    private int joueur_y;
    private ImageView imageViewJoueur;

    private TranslateTransition transition;

    // éléments de l'interface utilisateur
    private Label label;
    private Pane pane;

    // indique si le jeu est en cours ou terminé
    private boolean jeuEnCours;

    // le niveau en cours
    private Niveau niveau;

    Plateau(BorderPane borderPane) {
        pane = new Pane();
        pane.setPrefSize(
                Plateau.LARGEUR_PLATEAU * Plateau.LARGEUR_CARREAU,
                Plateau.HAUTEUR_PLATEAU * Plateau.LARGEUR_CARREAU
        );
        borderPane.setCenter(pane);

        label = new Label();
        borderPane.setBottom(label);

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.millis(500),
                        event -> anime()
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    void setNiveau(Niveau niveau) {
        this.niveau = niveau;
    }

    void start() {

        // initialisation de la grille
        Plateau.LARGEUR_PLATEAU = niveau.LARGEUR_PLATEAU;
        Plateau.HAUTEUR_PLATEAU = niveau.HAUTEUR_PLATEAU;

        pane.setPrefSize(
                Plateau.LARGEUR_PLATEAU * Plateau.LARGEUR_CARREAU,
                Plateau.HAUTEUR_PLATEAU * Plateau.LARGEUR_CARREAU
        );

        // message en bas de la fenêtre

        label.setText(message);

        // efface tous les élements visuels précédents

        pane.getChildren().clear();

        // création des carreaux

        System.out.println("Création des carreaux: Hauteur = " + HAUTEUR_PLATEAU + ", Largeur = " + LARGEUR_PLATEAU);
        for(int y = 0; y < HAUTEUR_PLATEAU; y++) {
            for(int x = 0; x < LARGEUR_PLATEAU; x++) {
                carreaux[y* LARGEUR_PLATEAU + x] = new Carreau(x, y, pane);
            }
        }

        // placement des murs et des sorties

        carreaux[niveau.X_SORTIE + niveau.Y_SORTIE * LARGEUR_PLATEAU].setEtat(EtatCarreau.SORTIE);

        for(List<Integer> mur: niveau.murs) {
            int x = mur.get(0);
            int y = mur.get(1);
            carreaux[y * LARGEUR_PLATEAU + x].setEtat(EtatCarreau.MUR);
        }


        // placement des commutateurs

        for(Commutateur commutateur: niveau.commutateurs) {
            commutateur.reset();
            carreaux[commutateur.getY() * LARGEUR_PLATEAU + commutateur.getX()].setEtat(EtatCarreau.COMMUTATEUR_OFF);
        };

        // placement des fantomes

        ObservableList<Node> children = pane.getChildren();

        for(Fantome fantome: niveau.fantomes) {
            fantome.reset();
            children.add(fantome.getNode());
        }

        // position initiale du joueur

        joueur_x = niveau.INIT_X_JOUEUR;
        joueur_y = niveau.INIT_Y_JOUEUR;

        // placement visuel du joueur

        imageViewJoueur = new ImageView(LibrairieImages.imgJoueur);
        imageViewJoueur.setTranslateX(joueur_x * Plateau.LARGEUR_CARREAU - 3);
        imageViewJoueur.setTranslateY(joueur_y * Plateau.LARGEUR_CARREAU - 3);

        children.add(imageViewJoueur);

        // placement des portes fermées

        for(List<Integer> porte: niveau.portes) {
            int x = porte.get(0);
            int y = porte.get(1);
            carreaux[y * LARGEUR_PLATEAU + x].setEtat(EtatCarreau.PORTE_FERMEE);
        }

        // passe à l'état 'jeu en cours'

        jeuEnCours = true;
    }

    void stop() {
        // passe à l'état 'jeu terminé'
        jeuEnCours = false;
    }

    void deplGauche() {
        deplacement(GAUCHE, joueur_x-1, joueur_y);
    }
    void deplDroite() {
        deplacement(DROITE, joueur_x+1, joueur_y);
    }
    void deplHaut() {
        deplacement(HAUT, joueur_x, joueur_y-1);
    }
    void deplBas() {
        deplacement(BAS, joueur_x, joueur_y+1);
    }

    private void deplacement(Direction direction, int destination_x, int destination_y) {

        if (!jeuEnCours) return;
        if (transition != null && transition.getStatus() != Animation.Status.STOPPED) return;

        // recherche si ce déplacement actionne une trappe
        Trappe trappe = niveau.trappes.stream()
                .filter(t ->
                        t.getX() == joueur_x && t.getY() == joueur_y && t.getDirection().equals(direction)
                )
                .findFirst()
                .orElse(null);

        if (trappe != null) {
            joueur_x = trappe.getDestinationX();
            joueur_y = trappe.getDestinationY();
            imageViewJoueur.setTranslateX(joueur_x * Plateau.LARGEUR_CARREAU - 3);
            imageViewJoueur.setTranslateY(joueur_y * Plateau.LARGEUR_CARREAU - 3);
            apresDeplacement();
        } else {
            if (destination_x < 0 ||
                    destination_y < 0 ||
                    destination_x >= LARGEUR_PLATEAU ||
                    destination_y >= HAUTEUR_PLATEAU) return;
            EtatCarreau etatCarreauDestination = carreaux[destination_y * LARGEUR_PLATEAU + destination_x].getEtat();
            if (etatCarreauDestination == EtatCarreau.MUR || etatCarreauDestination == EtatCarreau.PORTE_FERMEE) return;

            joueur_x = destination_x;
            joueur_y = destination_y;
            transition = new TranslateTransition();
            transition.setNode(imageViewJoueur);
            transition.setToX(joueur_x * Plateau.LARGEUR_CARREAU - 3);
            transition.setToY(joueur_y * Plateau.LARGEUR_CARREAU - 3);
            transition.setDuration(Duration.millis(80));
            transition.setOnFinished(event -> apresDeplacement());
            transition.play();
        }
    }

    private void apresDeplacement() {

        if (detecteCollisionFantome()) {
            termineJeu(false);
        } else {

            // recherche si ce déplacement actionne un commutateur
            Commutateur commutateur = niveau.commutateurs.stream()
                    .filter(c ->
                            c.getX() == joueur_x && c.getY() == joueur_y
                    )
                    .findFirst()
                    .orElse(null);

            if (commutateur != null) {
                boolean nvEtat = commutateur.commute();
                // mise à jour visuelle du commutateur
                carreaux[joueur_y * Plateau.LARGEUR_PLATEAU + joueur_x].setEtat(
                        nvEtat ? EtatCarreau.COMMUTATEUR_ON : EtatCarreau.COMMUTATEUR_OFF
                );
            }

            // placement des portes fermées selon l'état des commutateurs
            niveau.hookApresDeplacement(this);

            // recherche si le joueur est sur une sortie
            EtatCarreau etatCarreau = carreaux[this.joueur_y * LARGEUR_PLATEAU + this.joueur_x].getEtat();
            if (etatCarreau == EtatCarreau.SORTIE) {
                termineJeu(true);
            }
        }
    }

    private boolean detecteCollisionFantome() {
        for(Fantome fantome: niveau.fantomes) {
            if (fantome.getX() == joueur_x && fantome.getY() == joueur_y) {
                return true;
            }
        }
        return false;
    }

    private void anime() {
        if (jeuEnCours) {
            for(Fantome fantome: niveau.fantomes) {
                fantome.anime();
            }
            if (detecteCollisionFantome()) {
                termineJeu(false);
            }
        }
    }

    private void termineJeu(Boolean gagne) {
        stop();
        if (gagne) {
            label.setText("--- GAGNE ! --- / " + message);
        } else {
            label.setText("--- PERDU ! --- / " + message);
        }
    }
}
