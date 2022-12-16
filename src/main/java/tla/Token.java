package tla;

public class Token {
	
	private TypeDeToken typeDeToken;
	private String valeur;
	
	//Constructor
	public Token(TypeDeToken typeDeToken, String valeur) {
		this.typeDeToken = typeDeToken;
		this.valeur = valeur;
	}
	
	public Token(TypeDeToken typeDeToken) {
		this.typeDeToken = typeDeToken;
	}
	
	//Getters
	public TypeDeToken getTypeDeToken() {
		return typeDeToken;
	}

	public String getValeur() {
		return valeur;
	}

	@Override
	public String toString() {
		String res = typeDeToken.toString();
		if(valeur != null) res += " "+valeur;
		return res;
	}
}

