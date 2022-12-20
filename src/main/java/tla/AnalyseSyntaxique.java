package tla;

import java.util.List;

public class AnalyseSyntaxique {

	private int pos;
	private List<Token> tokens;
	
	/*
	effectue l'analyse syntaxique à partir de la liste de tokens
	et retourne le noeud racine de l'arbre syntaxique abstrait
	 */
	public Noeud analyse(List<Token> tokens) throws Exception {
		pos = 0;
		this.tokens = tokens;
		Noeud racine = new Noeud(TypeDeNoeud.niveau);
		racine = S(racine);
		if (pos != tokens.size()) {
			System.out.println("L'analyse syntaxique s'est terminé avant l'examen de tous les tokens");
			throw new IncompleteParsingException();
		}
		return racine;
	}
	
	/*

	Traite la dérivation du symbole non-terminal S

	S -> A S | B S | epsillon

	 */
	private Noeud S(Noeud n_niveau) throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.plateau ||
				getTypeDeToken() == TypeDeToken.joueur ||
				getTypeDeToken() == TypeDeToken.sortie) {

			// production S -> A S
			n_niveau.ajout(A());
			return S(n_niveau);
		} else if (getTypeDeToken() == TypeDeToken.murs ||
				getTypeDeToken() == TypeDeToken.trappes ||
				getTypeDeToken() == TypeDeToken.fantomes ||
				getTypeDeToken() == TypeDeToken.portes ||
				getTypeDeToken() == TypeDeToken.commutateurs) {

			// production S -> B S
			n_niveau.ajout(B());
			return S(n_niveau);
		} else if (!finAtteinte()) {
			// production S -> epsilon
			throw new UnexpectedTokenException("plateau, joueur, sortie, murs, trappes, fantomes, portes ou commutateurs ou fin de fichier attendu");
		}
		return n_niveau;
	}
	
	/*

	Traite la dérivation du symbole non-terminal A

	A -> D : intVal , intVal ;

	 */
	
	private Noeud A() throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.plateau ||
				getTypeDeToken() == TypeDeToken.joueur ||
				getTypeDeToken() == TypeDeToken.sortie) {

			// production A -> D : intVal , intVal ;
			Noeud n_param = D();
			if (lireToken().getTypeDeToken() != TypeDeToken.colon) {
				throw new UnexpectedTokenException("':' attendu");
			}
			Token t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("valeur entière attendue");
			}
			n_param.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.comma) {
				throw new UnexpectedTokenException("',' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("valeur entière attendue");
			}
			n_param.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.semicolon) {
				throw new UnexpectedTokenException("';' attendu");
			}
			return n_param;
		} else {
			throw new UnexpectedTokenException("plateau, joueur ou sortie attendu");
		}
	}
	
	/*

	Traite la dérivation du symbole non-terminal B

	B -> E } ;

	 */
	
	private Noeud B() throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.murs ||
				getTypeDeToken() == TypeDeToken.trappes ||
				getTypeDeToken() == TypeDeToken.fantomes ||
				getTypeDeToken() == TypeDeToken.portes ||
				getTypeDeToken() == TypeDeToken.commutateurs) {

			// production B -> E } ;
			Noeud n_param = E();
			if (lireToken().getTypeDeToken() != TypeDeToken.rightBrace) {
				throw new UnexpectedTokenException("'}' attendu");
			}
			if (lireToken().getTypeDeToken() != TypeDeToken.semicolon) {
				throw new UnexpectedTokenException("';' attendu");
			}
			return n_param;
		} else {
			throw new UnexpectedTokenException("murs, trappes, fantomes, portes ou commutateurs attendu");
		}

	}
	
	/*

	Traite la dérivation du symbole non-terminal E

	E → murs { M | trappes { T | fantomes { F | portes { P | commutateurs { C

	 */
	private Noeud E() throws UnexpectedTokenException {

		Token t = lireToken();
		if (t.getTypeDeToken() == TypeDeToken.murs) {
			// production E -> murs { M
			Noeud n_murs = new Noeud(TypeDeNoeud.murs);
			if (lireToken().getTypeDeToken() != TypeDeToken.leftBrace) {
				throw new UnexpectedTokenException("'{' attendu");
			}
			return M(n_murs);
		} else if (t.getTypeDeToken() == TypeDeToken.trappes) {
			// production E -> trappes { T
			Noeud n_trappes = new Noeud(TypeDeNoeud.trappes);
			if (lireToken().getTypeDeToken() != TypeDeToken.leftBrace) {
				throw new UnexpectedTokenException("'{' attendu");
			}
			return T(n_trappes);
		} else if (t.getTypeDeToken() == TypeDeToken.fantomes) {
			// production E -> fantomes { F
			Noeud n_fantomes = new Noeud(TypeDeNoeud.fantomes);
			if (lireToken().getTypeDeToken() != TypeDeToken.leftBrace) {
				throw new UnexpectedTokenException("'{' attendu");
			}
			return F(n_fantomes);
		} else if (t.getTypeDeToken() == TypeDeToken.portes) {
			// production E -> portes { P
			Noeud n_portes = new Noeud(TypeDeNoeud.portes);
			if (lireToken().getTypeDeToken() != TypeDeToken.leftBrace) {
				throw new UnexpectedTokenException("'{' attendu");
			}
			return P(n_portes);
		} else if (t.getTypeDeToken() == TypeDeToken.commutateurs) {
			// production E -> commutateurs { C
			Noeud n_commutateurs = new Noeud(TypeDeNoeud.commutateurs);
			if (lireToken().getTypeDeToken() != TypeDeToken.leftBrace) {
				throw new UnexpectedTokenException("'{' attendu");
			}
			return C(n_commutateurs);
		} else {
			throw new UnexpectedTokenException("murs, trappes, fantomes, portes ou commutateurs attendu");
		}

	}
	
	/*

	Traite la dérivation du symbole non-terminal D

	D → plateau | joueur | sortie

	 */
	private Noeud D() throws UnexpectedTokenException {

		Token t = lireToken();
		if (t.getTypeDeToken() == TypeDeToken.plateau) {
			// production D -> plateau
			return new Noeud(TypeDeNoeud.plateau);
		} else if (t.getTypeDeToken() == TypeDeToken.joueur) {
			// production D -> joueur
			return new Noeud(TypeDeNoeud.joueur);
		} else if (t.getTypeDeToken() == TypeDeToken.sortie) {
			// production D -> sortie
			return new Noeud(TypeDeNoeud.sortie);
		} else {
			throw new UnexpectedTokenException("plateau, joueur ou sortie attendu");
		}

	}
	
	/*

	Traite la dérivation du symbole non-terminal M

	M → intVal , intVal M’ | ε

	 */
	private Noeud M(Noeud n_murs) throws UnexpectedTokenException {

		Noeud n_mur = new Noeud(TypeDeNoeud.mur);
		if (getTypeDeToken() == TypeDeToken.intVal) {
			// production M -> intVal , intVal M’
			Token t = lireToken();
			n_mur.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.comma) {
				throw new UnexpectedTokenException("',' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n_mur.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			return M_prime(n_murs, n_mur);
		} else if (getTypeDeToken() == TypeDeToken.rightBrace) {
			// production M -> ε
			return n_murs;
		} else {
			throw new UnexpectedTokenException("intVal ou '}' attendu");
		}
	}

	/*

	Traite la dérivation du symbole non-terminal M'

	M’ → ; M | : axe : intVal ; M

	 */
	private Noeud M_prime(Noeud n_murs, Noeud n_mur) throws UnexpectedTokenException {

		Token t = lireToken();
		if (t.getTypeDeToken() == TypeDeToken.semicolon) {
			// production M' -> ; M
			n_murs.ajout(n_mur);
			return M(n_murs);
		} else if (t.getTypeDeToken() == TypeDeToken.colon) {
			// production M' -> : axe : intVal ; M
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.axe) {
				throw new UnexpectedTokenException("axe attendu (H ou V)");
			}
			n_mur.ajout(new Noeud(TypeDeNoeud.axe, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.colon) {
				throw new UnexpectedTokenException("':' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n_mur.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.semicolon) {
				throw new UnexpectedTokenException("';' attendu");
			}
			n_murs.ajout(n_mur);
			return M(n_murs);
		} else {
			throw new UnexpectedTokenException("';' ou ':' attendu");
		}

	}
	
	/*

	Traite la dérivation du symbole non-terminal T

	T → intVal , intVal -> intVal , intVal : direction ; T |  ε 

	 */
	private Noeud T(Noeud n_trappes) throws UnexpectedTokenException {

		Noeud n_trappe = new Noeud(TypeDeNoeud.trappe);
		if (getTypeDeToken() == TypeDeToken.intVal) {
			Token t = lireToken();
			// production T -> intVal , intVal -> intVal , intVal : direction ; T
			n_trappe.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.comma) {
				throw new UnexpectedTokenException("',' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n_trappe.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.arrow) {
				throw new UnexpectedTokenException("'->' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n_trappe.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.comma) {
				throw new UnexpectedTokenException("',' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n_trappe.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.colon) {
				throw new UnexpectedTokenException("':' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.direction) {
				throw new UnexpectedTokenException("direction attendu (haut, bas, gauche ou droite)");
			}
			n_trappe.ajout(new Noeud(TypeDeNoeud.direction, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.semicolon) {
				throw new UnexpectedTokenException("';' attendu");
			}
			n_trappes.ajout(n_trappe);
			return T(n_trappes);
		} else if (getTypeDeToken() == TypeDeToken.rightBrace) {
			// production T -> ε
			return n_trappes;
		} else {
			throw new UnexpectedTokenException("intVal ou '}' attendu");
		}
	}
	
	/*

	Traite la dérivation du symbole non-terminal F

	F → intVal , intVal { F’ } ; F |  ε 

	 */
	private Noeud F(Noeud n_fantomes) throws UnexpectedTokenException {

		Noeud n_fantome = new Noeud(TypeDeNoeud.fantome);
		if (getTypeDeToken() == TypeDeToken.intVal) {
			// production F -> intVal , intVal { F’ } ; F
			Token t = lireToken();
			n_fantome.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.comma) {
				throw new UnexpectedTokenException("',' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n_fantome.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.leftBrace) {
				throw new UnexpectedTokenException("'{' attendu");
			}
			Noeud n_mvmts = new Noeud(TypeDeNoeud.fantomeMouvements);
			n_fantome.ajout(F_prime(n_mvmts));
			if (lireToken().getTypeDeToken() != TypeDeToken.rightBrace) {
				throw new UnexpectedTokenException("'}' attendu");
			}
			if (lireToken().getTypeDeToken() != TypeDeToken.semicolon) {
				throw new UnexpectedTokenException("';' attendu");
			}
			n_fantomes.ajout(n_fantome);
			return F(n_fantomes);
		} else if (getTypeDeToken() == TypeDeToken.rightBrace) {
			// production F -> ε
			return n_fantomes;
		} else {
			throw new UnexpectedTokenException("intVal ou '}' attendu");
		}
	}
	
	/*

	Traite la dérivation du symbole non-terminal F'

	F’→ direction F’’ | ε

	 */
	private Noeud F_prime(Noeud n_mouvements) throws UnexpectedTokenException {

		Noeud n_mouvement = new Noeud(TypeDeNoeud.fantomeMouvement);

		if (getTypeDeToken() == TypeDeToken.direction) {
			// production F' -> direction F''
			Token t = lireToken();
			n_mouvement.ajout(new Noeud(TypeDeNoeud.direction, t.getValeur()));
			return F_seconde(n_mouvements, n_mouvement);
		} else if (getTypeDeToken() == TypeDeToken.rightBrace) {
			// production F' -> ε
			return n_mouvements;
		} else {
			throw new UnexpectedTokenException("direction (haut, bas, gauche ou droite) ou '}' attendu");
		}
	}
	
	/*

	Traite la dérivation du symbole non-terminal F''

	F’’→ : intVal ; F’ | ; F’

	 */
	private Noeud F_seconde(Noeud n_mouvements, Noeud n_mouvement) throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.colon) {
			// production F'' -> : intVal ; F'
			lireToken();
			Token t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n_mouvement.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.semicolon) {
				throw new UnexpectedTokenException("';' attendu");
			}
			n_mouvements.ajout(n_mouvement);
			return F_prime(n_mouvements);
		} else if (getTypeDeToken() == TypeDeToken.semicolon) {
			// production F'' -> ; F'
			lireToken();
			n_mouvements.ajout(n_mouvement);
			return F_prime(n_mouvements);
		} else {
			throw new UnexpectedTokenException("':' ou ';' attendu");
		}

	}
		
	/*

	Traite la dérivation du symbole non-terminal P

	P→ ident : intVal , intVal : etat ; P | ε 

	 */
	private Noeud P(Noeud n_portes) throws UnexpectedTokenException {

		Noeud n_porte = new Noeud(TypeDeNoeud.porte);
		if (getTypeDeToken() == TypeDeToken.ident) {
			// production P -> ident : intVal , intVal : etat ; P
			Token t = lireToken();
			n_porte.ajout(new Noeud(TypeDeNoeud.ident, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.colon) {
				throw new UnexpectedTokenException("':' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n_porte.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.comma) {
				throw new UnexpectedTokenException("',' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n_porte.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.colon) {
				throw new UnexpectedTokenException("':' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.etat) {
				throw new UnexpectedTokenException("etat attendu (vide ou pleine)");
			}
			n_porte.ajout(new Noeud(TypeDeNoeud.etat, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.semicolon) {
				throw new UnexpectedTokenException("';' attendu");
			}
			n_portes.ajout(n_porte);
			return P(n_portes);
		} else if (getTypeDeToken() == TypeDeToken.rightBrace) {
			// production P -> ε
			return n_portes;
		} else {
			throw new UnexpectedTokenException("ident ou '}' attendu");
		}

	}
	
	/*

	Traite la dérivation du symbole non-terminal C

	C→ intVal , intVal : ident C’ ; C | ε 

	 */
	private Noeud C(Noeud n_commutateurs) throws UnexpectedTokenException {

		Noeud n_commutateur = new Noeud(TypeDeNoeud.commutateur);
		if (getTypeDeToken() == TypeDeToken.intVal) {
			// production C -> intVal , intVal : ident C’ ; C
			Token t = lireToken();
			n_commutateur.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.comma) {
				throw new UnexpectedTokenException("',' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n_commutateur.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.colon) {
				throw new UnexpectedTokenException("':' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.ident) {
				throw new UnexpectedTokenException("ident attendu");
			}
			Noeud n_comm_idents = new Noeud(TypeDeNoeud.commutateur_identifiants);
			n_comm_idents.ajout(new Noeud(TypeDeNoeud.ident, t.getValeur()));
			n_commutateur.ajout(C_prime(n_comm_idents));
			if (lireToken().getTypeDeToken() != TypeDeToken.semicolon) {
				throw new UnexpectedTokenException("';' attendu");
			}
			n_commutateurs.ajout(n_commutateur);
			return C(n_commutateurs);
		} else if (getTypeDeToken() == TypeDeToken.rightBrace) {
			// production C -> ε
			return n_commutateurs;
		} else {
			throw new UnexpectedTokenException("intVal ou '}' attendu");
		}

	}
	
	/*

	Traite la dérivation du symbole non-terminal C'

	C’→ , ident C’ | ε 

	 */
	private Noeud C_prime(Noeud n_comm_idents) throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.comma) {
			// production C' -> , ident C'
			lireToken();
			Token t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.ident) {
				throw new UnexpectedTokenException("ident attendu");
			}
			n_comm_idents.ajout(new Noeud(TypeDeNoeud.ident, t.getValeur()));
			return C_prime(n_comm_idents);
		} else if (getTypeDeToken() == TypeDeToken.semicolon) {
			// production C' -> ε
			return n_comm_idents;
		} else {
			throw new UnexpectedTokenException("',' ou ';' attendu");
		}

	}
	
	
	/*

	méthodes utilitaires

	 */

	private boolean finAtteinte() {
		return pos >= tokens.size();
	}

	/*
	 * Retourne la classe du prochain token à lire
	 * SANS AVANCER au token suivant
	 */
	private TypeDeToken getTypeDeToken() {
		if (pos >= tokens.size()) {
			return null;
		} else {
			return tokens.get(pos).getTypeDeToken();
		}
	}

	/*
	 * Retourne le prochain token à lire
	 * ET AVANCE au token suivant
	 */
	private Token lireToken() {
		if (pos >= tokens.size()) {
			return null;
		} else {
			Token t = tokens.get(pos);
			pos++;
			return t;
		}
	}

}
