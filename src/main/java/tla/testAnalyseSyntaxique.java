package tla;

import java.util.List;

public class testAnalyseSyntaxique {

	public static void main(String[] args) {

		testAnalyseSyntaxique("3+2*(1+2)");

	}
	
	/*
	effectue l'analyse lexicale et syntaxique de la chaine entree
	 */
	private static void testAnalyseSyntaxique(String entree) {
		System.out.println("test analyse syntaxique");
		try {
			List<Token> tokens = new AnalyseLexicale().analyse(entree);
			Integer res = new AnalyseSyntaxique().analyse(tokens);
			System.out.println("La valeur de l'expression " + entree + " est " + res);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		System.out.println();
	}

}
