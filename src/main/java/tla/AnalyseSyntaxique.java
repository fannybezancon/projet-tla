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
		Noeud expr = S();
		if (pos != tokens.size()) {
			System.out.println("L'analyse syntaxique s'est terminé avant l'examen de tous les tokens");
			throw new IncompleteParsingException();
		}
		return expr;
	}
	
	/*

	Traite la dérivation du symbole non-terminal S

	S -> A S | B S | epsillon

	 */
	private Noeud S() throws UnexpectedTokenException {

		Noeud n = new Noeud(TypeDeNoeud.niveau);

		if (getTypeDeToken() == TypeDeToken.plateau ||
				getTypeDeToken() == TypeDeToken.joueur ||
				getTypeDeToken() == TypeDeToken.sortie) {

			// production S -> A S
			n.ajout(A());
			n.ajout(S());
		} else if (getTypeDeToken() == TypeDeToken.murs ||
				getTypeDeToken() == TypeDeToken.trappes ||
				getTypeDeToken() == TypeDeToken.fantomes ||
				getTypeDeToken() == TypeDeToken.portes ||
				getTypeDeToken() == TypeDeToken.commutateurs) {

			// production S -> B S
			n.ajout(B());
			n.ajout(S());
		} else if (finAtteinte()) {
			// production S -> epsilon
			return null;
		} else {
			// production S -> epsilon
			throw new UnexpectedTokenException("plateau, joueur, sortie, murs, trappes, fantomes, porte ou commutateurs attendu");
		}

		return n;
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
			Noeud n = D();
			if (lireToken().getTypeDeToken() != TypeDeToken.colon) {
				throw new UnexpectedTokenException("':' attendu");
			}
			Token t = lireToken();
			if (getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("valeur entière attendue");
			}
			n.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.comma) {
				throw new UnexpectedTokenException("',' attendu");
			}
			t = lireToken();
			if (getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("valeur entière attendue");
			}
			n.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.semicolon) {
				throw new UnexpectedTokenException("';' attendu");
			}
			return n;
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
			Noeud n = E();
			if (lireToken().getTypeDeToken() != TypeDeToken.rightBrace) {
				throw new UnexpectedTokenException("'}' attendu");
			}
			if (lireToken().getTypeDeToken() != TypeDeToken.semicolon) {
				throw new UnexpectedTokenException("';' attendu");
			}
			return n;
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
			Noeud n = new Noeud(TypeDeNoeud.trappes);
			if (lireToken().getTypeDeToken() != TypeDeToken.leftBrace) {
				throw new UnexpectedTokenException("'{' attendu");
			}
			n.ajout(T());
			return n;
		} else if (t.getTypeDeToken() == TypeDeToken.fantomes) {
			// production E -> fantomes { F
			Noeud n = new Noeud(TypeDeNoeud.fantomes);
			if (lireToken().getTypeDeToken() != TypeDeToken.leftBrace) {
				throw new UnexpectedTokenException("'{' attendu");
			}
			n.ajout(F());
			return n;
		} else if (t.getTypeDeToken() == TypeDeToken.portes) {
			// production E -> portes { P
			Noeud n = new Noeud(TypeDeNoeud.portes);
			if (lireToken().getTypeDeToken() != TypeDeToken.leftBrace) {
				throw new UnexpectedTokenException("'{' attendu");
			}
			n.ajout(P());
			return n;
		} else if (t.getTypeDeToken() == TypeDeToken.commutateurs) {
			// production E -> commutateurs { C
			Noeud n = new Noeud(TypeDeNoeud.commutateurs);
			if (lireToken().getTypeDeToken() != TypeDeToken.leftBrace) {
				throw new UnexpectedTokenException("'{' attendu");
			}
			n.ajout(C());
			return n;
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
		Token t = lireToken();
		if (t.getTypeDeToken() == TypeDeToken.intVal) {
			// production M -> intVal , intVal M’
			n_mur.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.comma) {
				throw new UnexpectedTokenException("',' attendu");
			}
			t = lireToken();
			if (getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n_mur.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			n_murs.ajout(n_mur);
			return M_prime(n_murs);
		} else if (t.getTypeDeToken() == TypeDeToken.rightBrace) {
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
	private Noeud M_prime(Noeud n_mur) throws UnexpectedTokenException {

		if (lireToken().getTypeDeToken() == TypeDeToken.semicolon) {
			// production M' -> ; M
			return M(n_mur);
		} else if (lireToken().getTypeDeToken() == TypeDeToken.colon) {
			// production M' -> : axe : intVal ; M
			Token t = lireToken();
			if (t.getTypeDeToken() == TypeDeToken.axe) {
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
				return M(n_mur);
			} else {
				throw new UnexpectedTokenException("axe attendu (H ou V)");
			}
		} else {
			throw new UnexpectedTokenException("';' ou ':' attendu");
		}

	}
	
	/*

	Traite la dérivation du symbole non-terminal T

	T → intVal , intVal -> intVal , intVal : direction ; T |  ε 

	 */
	private Noeud T(Noeud n) throws UnexpectedTokenException {

		Noeud n2 = new Noeud(TypeDeNoeud.trappe);
		Token t = lireToken();
		if (t.getTypeDeToken() == TypeDeToken.intVal) {
			// production T -> intVal , intVal -> intVal , intVal : direction ; T
			n2.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.comma) {
				throw new UnexpectedTokenException("',' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n2.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.arrow) {
				throw new UnexpectedTokenException("'->' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n2.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.comma) {
				throw new UnexpectedTokenException("',' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n2.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.colon) {
				throw new UnexpectedTokenException("':' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.direction) {
				throw new UnexpectedTokenException("direction attendue (N, S, E ou W)");
			}
			n2.ajout(new Noeud(TypeDeNoeud.direction, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.semicolon) {
				throw new UnexpectedTokenException("';' attendu");
			}
			n.ajout(n2);
			return T(n);
		} else if (t.getTypeDeToken() == TypeDeToken.rightBrace) {
			// production T -> ε
			return n;
		} else {
			throw new UnexpectedTokenException("intVal ou '}' attendu");
		}
	}
	
	/*

	Traite la dérivation du symbole non-terminal F

	F → intVal , intVal { F’ } ; F |  ε 

	 */
	private Noeud F(Noeud n) throws UnexpectedTokenException {

		Noeud n2 = new Noeud(TypeDeNoeud.fantome);
		Token t = lireToken();
		if (t.getTypeDeToken() == TypeDeToken.intVal) {
			// production F -> intVal , intVal { F’ } ; F
			n2.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.comma) {
				throw new UnexpectedTokenException("',' attendu");
			}
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n2.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.leftBrace) {
				throw new UnexpectedTokenException("'{' attendu");
			}
			Noeud n_mouvements = new Noeud(TypeDeNoeud.fantomeMouvements);
			F_prime(n_mouvements);
			n2.ajout(n_mouvements);
			if (lireToken().getTypeDeToken() != TypeDeToken.rightBrace) {
				throw new UnexpectedTokenException("'}' attendu");
			}
			if (lireToken().getTypeDeToken() != TypeDeToken.semicolon) {
				throw new UnexpectedTokenException("';' attendu");
			}
			n.ajout(n2);
			return F(n);
		} else if (t.getTypeDeToken() == TypeDeToken.rightBrace) {
			// production F -> ε
			return n;
		} else {
			throw new UnexpectedTokenException("intVal ou '}' attendu");
		}
	}
	
	/*

	Traite la dérivation du symbole non-terminal F'

	F’→ direction F’’

	 */
	private void F_prime(Noeud n_mouvements) throws UnexpectedTokenException {

		Noeud n_mouvement = new Noeud(TypeDeNoeud.fantomeMouvement);
		Token t = lireToken();
		if (t.getTypeDeToken() == TypeDeToken.direction) {
			// production F’ -> direction F’’
			n_mouvement.ajout(new Noeud(TypeDeNoeud.direction, t.getValeur()));
			F_seconde(n_mouvements ,n_mouvement);
			n_mouvements.ajout(n_mouvement);
		} else {
			throw new UnexpectedTokenException("direction attendue (N, S, E ou W)");
		}

	}
	
	/*

	Traite la dérivation du symbole non-terminal F''

	F’’→ : intVal ; F’ | ; F’ | ε 

	 */
	private void F_seconde(Noeud n_mouvements, Noeud n_mouvement) throws UnexpectedTokenException {

		Token t = lireToken();
		if (t.getTypeDeToken() == TypeDeToken.colon) {
			// production F’’ -> : intVal ; F’
			t = lireToken();
			if (t.getTypeDeToken() != TypeDeToken.intVal) {
				throw new UnexpectedTokenException("intVal attendu");
			}
			n_mouvement.ajout(new Noeud(TypeDeNoeud.intVal, t.getValeur()));
			if (lireToken().getTypeDeToken() != TypeDeToken.semicolon) {
				throw new UnexpectedTokenException("';' attendu");
			}
			F_prime(n_mouvements);
		} else if (t.getTypeDeToken() == TypeDeToken.semicolon) {
			// production F’’ -> ; F’
			F_prime(n_mouvements);
		} else if (t.getTypeDeToken() != TypeDeToken.rightBrace) {
			// production F’’ -> ε
			throw new UnexpectedTokenException("';' ou '}' attendu");
		}

	}
		
	/*

	Traite la dérivation du symbole non-terminal P

	P→ ident : intVal , intVal : etat ; P | ε 

	 */
	private Noeud P() throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.ident) {

			// production P→ ident : intVal , intVal : etat ; P

			Token t = lireToken();
			printToken("ident : intVal , intVal : etat ;");
			niveauIndentation++;
			Integer p = P();
			niveauIndentation--;
			Integer i = Integer.valueOf(t.getValeur());
			return i + p;
		}
		if (getTypeDeToken() == TypeDeToken.rightBrace) {

			// production T -> epsilon
			return null;
		}
		throw new UnexpectedTokenException("intVal attendu");
	}
	
	/*

	Traite la dérivation du symbole non-terminal C

	C→ intVal , intVal : ident C’ ; C | ε 

	 */
	private Noeud C() throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.intVal) {

			// production C→ intVal , intVal : ident C’ ; C

			lireToken();
			niveauIndentation++;
			Integer c_prime = C_prime();
			niveauIndentation--;

			if (getTypeDeToken() == TypeDeToken.semicolon) {
				lireToken();
				return C(c_prime);
			}
			throw new UnexpectedTokenException("; attendu");
		}
		if (getTypeDeToken() == TypeDeToken.rightBrace) {

			// production F -> epsilon
			return null;
		}
		throw new UnexpectedTokenException("intVal, ; ou } attendu");
	}
	
	/*

	Traite la dérivation du symbole non-terminal C'

	C’→ , ident C’ | ε 

	 */
	private Integer C_prime(Integer i) throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.comma) {

			// production C’→ , ident C’

			Token t = lireToken();
			printToken(", ident");
			niveauIndentation++;
			Integer c_prime = C_prime();
			niveauIndentation--;
			return i + c_prime;
		}
		if (getTypeDeToken() == TypeDeToken.semicolon) {

			// production F'' -> epsilon
			return null;
		}
		throw new UnexpectedTokenException(", ou ; attendu");
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

	/*
	 * Affiche le token t avec un certain niveau d'identation
	 */
	private void printToken(String s) {
		for(int i=0;i<niveauIndentation;i++) {
			System.out.print("      ");
		}
		System.out.println(s);
	}

}
