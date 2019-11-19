package controleurs;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import modele.IModele;

public class ControleurArretUrgence implements EventHandler<ActionEvent> {
	
	private IModele modele;
	
	public ControleurArretUrgence(IModele modele) {
		this.modele = modele;
	}
	
	@Override
	public void handle(ActionEvent event) {
		System.out.println("Signal ARRET_URGENCE envoyé.");
		modele.arretUrgence();
	}
	
}
