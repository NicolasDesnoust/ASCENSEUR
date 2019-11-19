package util;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class Bouton extends Button {
	private int niveau;
	
	public Bouton (int niveau, String texte, Node icone) {
		super(texte, icone);
		this.niveau = niveau;
	}
	
	public Bouton (int niveau, String texte) {
		super(texte);
		this.niveau = niveau;
	}
	
	public int getNiveau() {
		return niveau;
	}
}
