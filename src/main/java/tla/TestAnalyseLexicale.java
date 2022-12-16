package tla;

import java.util.List;

public class TestAnalyseLexicale {
	
	public static void main(String[] args) {

		testAnalyseLexicale("sortie:1,6;");

	}

	/*
	effectue l'analyse lexicale de la chaine entree,
	affiche la liste des tokens reconnus
	 */
	private static void testAnalyseLexicale(String entree) {
		System.out.println("debut du test d'analyse lexicale");
		try {
			List<Token> tokens = new AnalyseLexicale().analyse(entree);
			for (Token t : tokens) {
				System.out.println(t);
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		System.out.println();
	}


}
