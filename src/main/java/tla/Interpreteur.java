package tla;
import java.util.HashMap;

public class Interpreteur {

    private Niveau niveau;

    // stocke les variables lues au clavier durant l'interprétation
    private HashMap<String, Integer> variables;

    public Interpreteur() {
        variables = new HashMap<>();
    }


    public Object interpreter(Noeud n) {
        switch (n.getTypeDeNoeud()) {
            case niveau:
                interpreter(n.enfant(0));
                if (n.nombreEnfants() > 1) {
                    interpreter(n.enfant(1));
                }

                break;
            case joueur:

                break;
            case plateau:
                break;
            case sortie:
                break;
            case ident:
                return variables.get(n.getValeur());
            case intVal:
                return Integer.valueOf(n.getValeur());
            case direction:
                return String.valueOf(n.getValeur());
            case axe:
                return String.valueOf(n.getValeur());
            case etat:
                return String.valueOf(n.getValeur());
            case murs:
                break;
            case trappes:
                break;
            case fantomes:

                break;
            case fantomeMouvements:
                break;
            case portes:
                break;
            case commutateurs:
                break;
            case commutateur_identifiants:
                break;
            case mur:
                break;
            case trappe:
                break;
            case fantome:
                break;
            case fantomeMouvement:
                break;
            case porte:
                break;
            case commutateur:
                break;
            default:
                System.out.println("Erreur: noeud de type " + n.getTypeDeNoeud() + " non reconnu");
        }
        return null;
    }


}

