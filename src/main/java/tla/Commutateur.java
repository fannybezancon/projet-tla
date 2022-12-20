package tla;

/*
Description d'un commutateur : coordonnées x,y et état

Attention à ne pas confondre la description d'un commutateur (décrit dans cette classe)
et les effets des commutateurs sur les portes fermées (doivent être décrit dans hookApresDeplacement)
*/

import java.util.ArrayList;
import java.util.List;

class Commutateur {
    private int x;
    private int y;
    private boolean etat;
    private boolean a_commute = false;
    private List<List<Integer>> portes = new ArrayList<>();

    Commutateur(int x, int y) {
        this.x = x;
        this.y = y;
        etat = false;
    }

    void reset() {
        a_commute = false;
        etat=false;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    boolean getEtat() {
        return etat;
    }

    boolean getACommute() {
        boolean ret = a_commute;
        if (a_commute) { a_commute = false; }
        return ret;
    }

    boolean commute() {
        etat = !etat;
        a_commute = true;
        return etat;
    }

    void addPorte(List<Integer> coordonneesPorte) {
        if (!portes.contains(coordonneesPorte)) {
            portes.add(coordonneesPorte);
        }
    }

    List<List<Integer>> getPortes() {
        return portes;
    }
}
