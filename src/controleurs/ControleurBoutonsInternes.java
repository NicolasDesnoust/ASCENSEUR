package controleurs;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import modele.IModele;
import util.Bouton;

public class ControleurBoutonsInternes implements EventHandler<ActionEvent> {

	private IModele modele;
	
	public ControleurBoutonsInternes(IModele modele) {
		this.modele = modele;
	}
	
	@Override
	public void handle(ActionEvent event) {
		Object source = event.getSource();
		
		if (source instanceof Bouton) {
			Bouton btn = (Bouton)source;
			System.out.println("Signal DEMANDE_NIVEAU_" + btn.getNiveau() + " envoyé.");
			modele.demandeNiveau(btn.getNiveau());
		}
	}
}
