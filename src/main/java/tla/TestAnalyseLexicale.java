package tla;

import java.util.List;

public class TestAnalyseLexicale {
	
	public static void main(String[] args) {

		testAnalyseLexicale("plateau:5,6;\n" +
				"joueur:1,1;\n" +
				"sortie:5,6;\n" +
				"murs{\n" +
				"    3,1 : H : 3;\n" +
				"    5,2:V:4; 1,4:V:2;\n" +
				"    1,2;\n" +
				"    3,2;\n" +
				"    3,4:V:3;\n" +
				"};\n" +
				"trappes{\n" +
				"    4,4->4,2:bas;\n" +
				"    1,6->4,6:haut;\n" +
				"};\n" +
				"fantomes{\n" +
				"    2,5{\n" +
				"        haut:2;\n" +
				"        droite;droite;\n" +
				"    };\n" +
				"};\n" +
				"portes{\n" +
				"    p1:2,6:off;\n" +
				"    p2:4,5:on;\n" +
				"};\n" +
				"commutateurs{\n" +
				"    1,3:p1,p2;\n" +
				"};\n");

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
