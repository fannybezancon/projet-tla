package tla;

import java.util.ArrayList;
import java.util.List;

/*
description d'un niveau
*/
public class Niveau {

    /*
    murs du niveau
     */
    public List<List<Integer>> murs = new ArrayList<>();

    /*
    portes du niveau
     */
    public List<List<Integer>> portes = new ArrayList<>();

    /*
    position sortie
     */
    int X_SORTIE = 0;
    int Y_SORTIE = 0;

    /*
    position initiale du joueur
     */
    int INIT_X_JOUEUR = 0;
    int INIT_Y_JOUEUR = 0;

    /*
    Largeur et hauteur du plateau
     */
    int LARGEUR_PLATEAU = 20;
    int HAUTEUR_PLATEAU = 14;

    /*
    liste des trappes
    */
    List<Trappe> trappes = new ArrayList<>();

    /*
    liste des fantomes
    (pas une constante car un fantome possède un état)
    */
    List<Fantome> fantomes = new ArrayList<>();

    /*
    liste des commutateurs
    (pas une constante car un commutateur possède un état)
    */
    List<Commutateur> commutateurs = new ArrayList<>();

    /*
    placement des portes fermées selon l'état des commutateurs, à appliquer
    à l'initialisation du niveau et après chaque déplacement du joueur

    il est également possible de décrire dans hookApresDeplacement() d'autres effets
    qui doivent également être appliqués à l'initialisation du niveau et après chaque déplacement du joueur
    */
    void hookApresDeplacement(Plateau plateau) {
        for(Commutateur commutateur: commutateurs) {
            if(commutateur.getACommute()) {
                for(List<Integer> porte: commutateur.getPortes()) {
                    int x = porte.get(0);
                    int y = porte.get(1);
                    EtatCarreau etat_courant = plateau.carreaux[y * LARGEUR_PLATEAU + x].getEtat();
                    plateau.carreaux[y * LARGEUR_PLATEAU + x].setEtat(etat_courant == EtatCarreau.VIDE ? EtatCarreau.PORTE_FERMEE : EtatCarreau.VIDE);
                }
            }
        }
    };
}
