package tla;

/* si l'analyse est bloquée parce qu'il n'y a pas de transition possible
 * (c'est à dire si prochainEtat est null)
 */
public class LexicalErrorException extends Exception{
	
	public LexicalErrorException(String message) {
		super(message);
	}

}
