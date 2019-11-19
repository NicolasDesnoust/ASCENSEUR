package controleurs;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import modele.IModele;
import util.Bouton;

public class ControleurBoutonsExtMonter implements EventHandler<ActionEvent> {

	private IModele modele;
	
	public ControleurBoutonsExtMonter(IModele modele) {
		this.modele = modele;
	}
	
	@Override
	public void handle(ActionEvent e) {
		Object source = e.getSource();
		
		if (source instanceof Bouton) {
			Bouton btn = (Bouton)source;
			System.out.println("Signal MONTER_DEPUIS_NIVEAU_" + btn.getNiveau() + " envoyé.");
			modele.monterNiveau(btn.getNiveau());
		}
	}
}
