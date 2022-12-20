package tla;

import java.util.List;

public class testAnalyseSyntaxique {

	public static void main(String[] args) {

		testAnalyseSyntaxique("plateau:5,6;\n" +
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
	effectue l'analyse lexicale et syntaxique de la chaine entree
	 */
	private static void testAnalyseSyntaxique(String entree) {
		System.out.println("test analyse syntaxique");
		try {
			List<Token> tokens = new AnalyseLexicale().analyse(entree);
			Noeud racine = new AnalyseSyntaxique().analyse(tokens);
			Noeud.afficheNoeud(racine, 0);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		System.out.println();
	}

}
