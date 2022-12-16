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
		
		if (getTypeDeToken() == TypeDeToken.plateau ||
				getTypeDeToken() == TypeDeToken.joueur ||
				getTypeDeToken() == TypeDeToken.sortie) {

			// production S -> A S

			Noeud a = A();
			return S(a);
		}		
		
		if (getTypeDeToken() == TypeDeToken.murs ||
				getTypeDeToken() == TypeDeToken.trappes ||
				getTypeDeToken() == TypeDeToken.fantomes ||
				getTypeDeToken() == TypeDeToken.portes ||
				getTypeDeToken() == TypeDeToken.commutateurs) {

			// production S -> B S

			Noeud b = B();
			return S(b);
		}
		
		if (finAtteinte()) {

			// production S -> epsilon
			return null;
		}
		
		throw new UnexpectedTokenException("plateau, joueur, sortie, murs, trappes, fantomes, porte ou commutateurs attendu");
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
			
			lireToken();
			Noeud s = D();

			if (getTypeDeToken() == TypeDeToken.colon) {
				lireToken(); //est-ce qu'on devrait pas faire une boucle pour lire tous les tokens qui suivent ?
				return s;
			}
		}
		throw new UnexpectedTokenException("plateau, joueur ou sortie attendu");

	}
	
	/*

	Traite la dérivation du symbole non-terminal B

	B -> E } ;

	 */
	
	private Integer B() throws UnexpectedTokenException {
	
		if (getTypeDeToken() == TypeDeToken.murs ||
				getTypeDeToken() == TypeDeToken.trappes ||
				getTypeDeToken() == TypeDeToken.fantomes ||
				getTypeDeToken() == TypeDeToken.portes ||
				getTypeDeToken() == TypeDeToken.commutateurs) {
			
			// production B -> E } ;
			
			lireToken();
			Noeud s = E();

			if (getTypeDeToken() == TypeDeToken.rightBrace) {
				lireToken(); //est-ce qu'on devrait pas faire une boucle pour lire tous les tokens qui suivent ?
				return s;
			}
		}
		throw new UnexpectedTokenException("murs, trappes, fantomes, portes ou commutateurs attendu");

	}
	
	/*

	Traite la dérivation du symbole non-terminal E

	E → murs { M | trappes { T | fantomes { F | portes { P | commutateurs { C

	 */
	private Noeud E() throws UnexpectedTokenException {
		
		if (getTypeDeToken() == TypeDeToken.murs) { //mur ou leftBrace
			
			// production E -> murs { M
			lireToken();	//non ici il faudrait des Noeud
			return M();
		}
		
		if (getTypeDeToken() == TypeDeToken.trappes) {
			
			// production E -> trappes { T
			lireToken();
			niveauIndentation++;
			Integer t = T();
			niveauIndentation--;
		}
		if (getTypeDeToken() == TypeDeToken.fantomes) {
			
			// production E -> fantomes { F
			lireToken();
			niveauIndentation++;
			Integer f = F();
			niveauIndentation--;
		}
		if (getTypeDeToken() == TypeDeToken.portes) {
			
			// production E -> portes { P
			lireToken();
			niveauIndentation++;
			Integer p = P();
			niveauIndentation--;
		}
		if (getTypeDeToken() == TypeDeToken.commutateurs) {
			
			// production E -> commutateurs { C
			Token t = lireToken();
			niveauIndentation++;
			printToken("commutateurs {");
			niveauIndentation--;
			Integer i = Integer.valueOf(t.getValeur());
			return C(i);
		}
		throw new UnexpectedTokenException("murs, trappes, fantomes, portes ou commutateurs attendu");
	}
	
	/*

	Traite la dérivation du symbole non-terminal D

	D → plateau | joueur | sortie

	 */
	private void D() throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.plateau) {
			// production D → plateau
			Token t = lireToken();
			printToken("plateau");
		}
		if (getTypeDeToken() == TypeDeToken.joueur) {
			// production D → joueur
			Token t = lireToken();
			printToken("joueur");
		}
		if (getTypeDeToken() == TypeDeToken.sortie) {
			// production D → sortie
			Token t = lireToken();
			printToken("sortie");
		}
		throw new UnexpectedTokenException("plateau, joueur ou sortie attendu");
	}
	
	/*

	Traite la dérivation du symbole non-terminal M

	M → intVal , intVal M’ | ε

	 */
	private Integer M() throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.intVal) {

			// production M -> intVal , intVal M’

			Token t = lireToken();
			niveauIndentation++;
			printToken("intVal , intVal");
			niveauIndentation--;
			Integer i = Integer.valueOf(t.getValeur());
			return M_prime(i);
		}

		if (getTypeDeToken() == TypeDeToken.rightBrace) {

			// production M -> epsilon
			return null;
		}
		throw new UnexpectedTokenException("intVal ou } attendu");
	}

	/*

	Traite la dérivation du symbole non-terminal M'

	M’ → ; M | : axe : intVal ; M

	 */
	private Integer M_prime(Integer i) throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.semicolon) {

			// production M’ → ; M

			Token t = lireToken();
			printToken(";");
			niveauIndentation++;
			Integer m = M();
			niveauIndentation--;
			return i + m;
		}
		if (getTypeDeToken() == TypeDeToken.colon) {

			// production M’ → : axe : intVal ; M

			Token t = lireToken();
			printToken(": axe : intVal ;");
			niveauIndentation++;
			Integer m = M();
			niveauIndentation--;
			return i + m;
		}
		throw new UnexpectedTokenException("; ou : attendu");
	}
	
	/*

	Traite la dérivation du symbole non-terminal T

	T → intVal , intVal -> intVal , intVal : direction ; T |  ε 

	 */
	private Integer T() throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.intVal) {

			// production T → intVal , intVal -> intVal , intVal : direction ; T

			Token t = lireToken();
			printToken("intVal , intVal -> intVal , intVal : direction ;");
			niveauIndentation++;
			Integer t = T();
			niveauIndentation--;
			Integer i = Integer.valueOf(t.getValeur());
			return i + t;
		}
		if (getTypeDeToken() == TypeDeToken.rightBrace) {

			// production T -> epsilon
			return null;
		}
		throw new UnexpectedTokenException("intVal attendu");
	}
	
	/*

	Traite la dérivation du symbole non-terminal F

	F → intVal , intVal { F’ } ; F |  ε 

	 */
	private Integer F() throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.intVal) {

			// production F → intVal , intVal { F’ } ; F

			lireToken();
			niveauIndentation++;
			Integer f_prime = F_prime();
			niveauIndentation--;

			if (getTypeDeToken() == TypeDeToken.rightBrace) {
				lireToken();
				return F(f_prime);
			}
			throw new UnexpectedTokenException("} attendu");
		}
		if (getTypeDeToken() == TypeDeToken.rightBrace) {

			// production F -> epsilon
			return null;
		}
		throw new UnexpectedTokenException("intVal ou } attendu");
	}
	
	/*

	Traite la dérivation du symbole non-terminal F'

	F’→ direction F’’

	 */
	private Integer F_prime(Integer i) throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.direction) {

			// production F’ → direction F’’

			Token t = lireToken();
			printToken(t.getValeur());
			niveauIndentation++;
			Integer f_seconde = F_seconde();
			niveauIndentation--;
			return i + f_seconde;
		}
		throw new UnexpectedTokenException("direction attendu");
	}
	
	/*

	Traite la dérivation du symbole non-terminal F''

	F’’→ : intVal ; F’ | ; F’ | ε 

	 */
	private Integer F_seconde(Integer i) throws UnexpectedTokenException {

		if (getTypeDeToken() == TypeDeToken.colon) {

			// production F’’→ : intVal ; F’

			Token t = lireToken();
			printToken(": intVal ;");
			niveauIndentation++;
			Integer f_prime = F_prime();
			niveauIndentation--;
			return i + f_prime;
		}
		if (getTypeDeToken() == TypeDeToken.semicolon) {

			// production F’’→ ; F’

			Token t = lireToken();
			printToken(";");
			niveauIndentation++;
			Integer f_prime = F_prime();
			niveauIndentation--;
			return i + f_prime;
		}
		if (getTypeDeToken() == TypeDeToken.rightBrace) {

			// production F'' -> epsilon
			return null;
		}
		throw new UnexpectedTokenException(":, ; ou } attendu");
	}
		
	/*

	Traite la dérivation du symbole non-terminal P

	P→ ident : intVal , intVal : etat ; P | ε 

	 */
	private Integer P() throws UnexpectedTokenException {

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
	private Integer C() throws UnexpectedTokenException {

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
