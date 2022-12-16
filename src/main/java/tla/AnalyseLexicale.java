package tla;

import java.util.ArrayList;
import java.util.List;

public class AnalyseLexicale {
	
	// Table de transition de l'analyse lexicale
	private static Integer TRANSITIONS[][] = {
			//            espace     :     {     }      ,     -     >     ;  chiffre  lettre
			/*  0 */    {      0,  101,  102,  103,  104,    1, null,  106,       2,       3  },
			/*  1 */    {   null, null, null, null, null, null,  105, null,    null,    null  },
			/*  2 */    {    107,  107,  107,  107,  107,  107,  107,  107,       2,     107  },
			/*  3 */    {    108,  108,  108,  108,  108,  108,  108,  108,       3,       3  }
			
			// 101 acceptation token :
			// 102 acceptation token {
			// 103 acceptation token }
			// 104 acceptation token ,
			// 105 acceptation token ->
			// 106 acceptation token ;                       
			// 107 acceptation token intVal					 (retourArriere)
			// 108 acceptation token mot-clé/ident           (retourArriere)
	};
		
	private String entree; // variable pour stocker la chaine de caractère à analyser
	private int pos;    // variable pour reperer la position du prochain caractère à analyser
	private static final int ETAT_INITIAL = 0;
	
	
	//méthode qui décrémente la variable 'pos'
	private void retourArriere() {
		pos--;
	}
	
	//méthode qui retourne caractère en position 'pos' de la variable entree, puis incrémente pos
	private Character lireCaractere() {
		Character c;
		try {
			c = entree.charAt(pos);
			pos++;
		} catch (IndexOutOfBoundsException e) {
			c = null;
		}
		return c;	
	}
	
	
	/*
	Pour chaque symbole terminal acceptable en entrée de l'analyse syntaxique
	retourne un indice identifiant soit un symbole, soit une classe de symbole :
	 */	
	private static int indiceSymbole(Character c) throws IllegalCharacterException {
		if(c==null) return 0;
		if(Character.isWhitespace(c)) return 0;
		if(c==':') return 1;
		if(c=='{') return 2;
		if(c=='}') return 3;
		if(c==',') return 4;
		if(c=='-') return 5;
		if(c=='>') return 6;
		if(c==';') return 7;
		if(Character.isDigit(c)) return 8;
		if(Character.isLetter(c)) return 9;
		
		System.out.println("symbole inconnu : " + c);
		throw new IllegalCharacterException(c.toString());
	}
	
	//méthode qui effectue l'analyse lexicale et retourne une liste de Token
	public List<Token> analyse(String entree) throws Exception {
		this.entree = entree;
		pos = 0;
		List <Token> tokens = new ArrayList<>();   //permet de stocker tokens reconnus
		
		/* copie des symboles en entrée
		- permet de distinguer les mots-clés des identifiants
		- permet de conserver une copie des valeurs particulières des tokens de type ident et intval
		 */
		String buf ="";
		
		Integer etat = ETAT_INITIAL;
		
		Character c;
		do {
			c = lireCaractere();
			Integer indice = indiceSymbole(c);
			Integer e = TRANSITIONS[etat][indice];
			if (e == null) {
				System.out.println("pas de transition depuis état " + etat + " avec symbole " + c);
				throw new LexicalErrorException("pas de transition depuis état " + etat + " avec symbole " + c);
			}
			//cas particulier lorsqu'un etat d'acceptation est atteint
			if (e >= 100) {
				if(e==101) tokens.add(new Token(TypeDeToken.colon));
				if(e==102) tokens.add(new Token(TypeDeToken.leftBrace));
				if(e==103) tokens.add(new Token(TypeDeToken.rightBrace));
				if(e==104) tokens.add(new Token(TypeDeToken.comma));
				if(e==105) tokens.add(new Token(TypeDeToken.arrow));
				if(e==106) tokens.add(new Token(TypeDeToken.semicolon));
				if(e==107) { tokens.add(new Token(TypeDeToken.intVal, buf)); retourArriere();}
				if(e==108) { tokens.add(new Token(TypeDeToken.ident, buf)); retourArriere();}
				
				// un état d'acceptation ayant été atteint, retourne à l'état 0
				etat = 0;
				// reinitialise buf
				buf ="";
				
			}else {
				//enregistre le nouvel état
				etat = e;
				// ajoute le symbole qui vient d'être examiné à buf
				// sauf s'il s'agit un espace ou assimilé
				if (etat>0) buf = buf + c;
			}
			
		}while (c != null);
		return tokens;
	}
}

