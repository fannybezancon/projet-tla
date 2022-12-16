package tla;

/*L'analyse lexicale a detecté en entrée, un sumbole inconnu
 * que l'analyse syntaxique n'accepte pas
 */
public class IllegalCharacterException extends Exception {
	
	public IllegalCharacterException(String message) {
		super(message);
	}

}
