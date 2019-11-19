package controleurs;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import modele.IModele;
import util.Bouton;
import util.Etat;

public class ControleurNiveauAtteint implements EventHandler<ActionEvent> {

	private IModele modele;
	
	public ControleurNiveauAtteint(IModele modele) {
		this.modele = modele;
	}
	
	@Override
	public void handle(ActionEvent e) {
		if (modele.getEtatCourant() == Etat.ARRET) {
			System.err.println("Erreur : L'ascenseur n'est pas en mouvement.");
		}
		else {
			System.out.println("Signal NIVEAU_ATTEINT envoyé.");
			modele.niveauAtteint();
		}
	}
	
}
