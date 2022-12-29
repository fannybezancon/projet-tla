package tla;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Interpreteur {

    private HashMap<String, List<Integer>> portes = new HashMap<>();
    private Niveau niveau = new Niveau();

    public Interpreteur() {
    }

    public Object interpreter(Noeud n) {
        switch (n.getTypeDeNoeud()) {
            case niveau:
                // interprete de préférence dans l'ordre: plateau, joueur, sortie, murs, trappes, fantomes, portes, commutateurs
                List<TypeDeNoeud> ordre = Arrays.asList(TypeDeNoeud.plateau, TypeDeNoeud.joueur, TypeDeNoeud.sortie, TypeDeNoeud.murs, TypeDeNoeud.trappes, TypeDeNoeud.fantomes, TypeDeNoeud.portes, TypeDeNoeud.commutateurs);
                for (TypeDeNoeud type : ordre) {
                    for (int i = 0; i < n.nombreEnfants(); i++) {
                        Noeud enfant = n.enfant(i);
                        if (enfant.getTypeDeNoeud() == type) {
                            interpreter(enfant);
                        }
                    }
                }
                return niveau;
            case joueur:
                if(n.nombreEnfants() == 2) {
                    int x = (int) interpreter(n.enfant(0))-1 ;
                    int y = (int) interpreter(n.enfant(1))-1;
                    niveau.INIT_X_JOUEUR = x;
                    niveau.INIT_Y_JOUEUR = y;
                }
                break;
            case plateau:
                if(n.nombreEnfants() == 2) {
                    int L = (int) interpreter(n.enfant(0))-1;
                    int H = (int) interpreter(n.enfant(1))-1;
                    niveau.LARGEUR_PLATEAU = L;
                    niveau.HAUTEUR_PLATEAU = H;
                }
                break;
            case sortie:
                if(n.nombreEnfants() == 2) {
                    int x = (int) interpreter(n.enfant(0))-1;
                    int y = (int) interpreter(n.enfant(1))-1;
                    niveau.X_SORTIE = x;
                    niveau.Y_SORTIE = y;
                }
                break;
            case ident:
                return portes.get(n.getValeur());
            case intVal:
                return Integer.parseInt(n.getValeur());
            case direction:
                switch (n.getValeur()) {
                    case "haut":
                        return Direction.HAUT;
                    case "bas":
                        return Direction.BAS;
                    case "gauche":
                        return Direction.GAUCHE;
                    case "droite":
                        return Direction.DROITE;
                }
                break;
            case axe:
            case etat:
                return String.valueOf(n.getValeur());
            case murs:
            case portes:
            case trappes:
            case fantomes:
            case commutateurs:
                for (int i = 0; i < n.nombreEnfants(); i++) {
                    interpreter(n.enfant(i));
                }
                break;
            case fantomeMouvements:
                List<Direction> listedirection = new ArrayList();
                for (int i = 0; i < n.nombreEnfants(); i++) {
                    listedirection.addAll((List<Direction>) interpreter(n.enfant(i)));
                }
                return listedirection;
            case commutateur_identifiants:
                ArrayList<List<Integer>> listeporte = new ArrayList();
                for (int i = 0; i < n.nombreEnfants(); i++) {
                    listeporte.add((List<Integer>) interpreter(n.enfant(i)));
                }
                return listeporte;
            case mur:
                if (n.nombreEnfants() > 2) {
                    int x = (int) interpreter(n.enfant(0)) - 1;
                    int y = (int) interpreter(n.enfant(1)) - 1;
                    String axe = (String) interpreter(n.enfant(2));
                    int longueur = (int) interpreter(n.enfant(3));
                    if (axe.equals("H")) { // Horizontal
                        for (int i = 0; i < longueur; i++) {
                            niveau.murs.add(List.of(x + i, y));
                        }
                    } else if (axe.equals("V")) { // Vertical
                        for (int i = 0; i < longueur; i++) {
                            niveau.murs.add(List.of(x, y + i));
                        }
                    }
                } else {
                    int x = (int) interpreter(n.enfant(0)) - 1;
                    int y = (int) interpreter(n.enfant(1)) - 1;
                    niveau.murs.add(List.of(x, y));
                }
                break;
            case trappe:
            	Trappe trappe1 = new Trappe((int)interpreter(n.enfant(0))-1,(int)interpreter(n.enfant(1))-1,
            			(Direction)interpreter(n.enfant(4)), (int)interpreter(n.enfant(3))-1, (int)interpreter(n.enfant(2))-1);
                niveau.trappes.add(trappe1);
                break;
            case fantome:
            	Fantome fantome1 = new Fantome((int)interpreter(n.enfant(0))-1,(int)interpreter(n.enfant(1))-1, 
            			(List<Direction>)interpreter(n.enfant(2)));
                niveau.fantomes.add(fantome1);
                break;
            case fantomeMouvement:
            	List<Direction> listeDirection = new ArrayList();
            	if (n.nombreEnfants()==2){
            		Direction direction = (Direction) interpreter(n.enfant(0));
            		int nb = (int) interpreter(n.enfant(1));
            		for (int i = 0; i < nb; i++) { 
            			listeDirection.add(direction); 
                    }
            	}
                else {
                    Direction direction = (Direction) interpreter(n.enfant(0));
                    listeDirection.add(direction);
            	}           	
                break;
            case porte:
            	if (n.nombreEnfants()==4) {
            		String ident = n.enfant(0).getValeur();
                    int x = (int) interpreter(n.enfant(1)) - 1;
                    int y = (int) interpreter(n.enfant(2)) - 1;
                    String etat = (String) interpreter(n.enfant(3));
                    if(etat.equals("on")) {
                    	niveau.portes.add(List.of(x,y));
               	    }
					if (!portes.containsKey(ident)) {
                        portes.put(ident, new ArrayList<>());
                        portes.get(ident).add(x);
                        portes.get(ident).add(y);
                    }
            	}
                break;
            case commutateur:
            	Commutateur commutateur1 = new Commutateur((int)interpreter(n.enfant(0))-1,(int)interpreter(n.enfant(1))-1);
                niveau.commutateurs.add(commutateur1);
                for (List<Integer> porte : (List<List<Integer>>) interpreter(n.enfant(2))) {
                	commutateur1.addPorte(porte);
                }
            	break;
            default:
                System.out.println("Erreur d'interpretation");
                return null;
        }
        return null;
    }
}
