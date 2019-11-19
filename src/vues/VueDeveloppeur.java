package vues;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import controleurs.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modele.IModele;
import util.Bouton;
import util.Etat;

/**
 * 
 * @author desno
 *
 */
public class VueDeveloppeur implements IVue, PropertyChangeListener {
	private IModele modele;

	private Button arretUrgence, niveauAtteint;
	private Bouton[] demandeNiveau, descendreNiveau, monterNiveau;

	public VueDeveloppeur(IModele modele) {
		this.modele = modele;
		modele.ajouterEcouteur(this);

		int premierNiveau = modele.getPremierNiveau(), dernierNiveau = modele.getDernierNiveau();
		demandeNiveau = new Bouton[dernierNiveau - premierNiveau + 1];
		descendreNiveau = new Bouton[dernierNiveau - premierNiveau];
		monterNiveau = new Bouton[dernierNiveau - premierNiveau];

		initialiserBoutons();
		ajouterControleurs();
	}

	public void initialiserBoutons() {
		arretUrgence = new Button("Arrêt d'urgence");
		niveauAtteint = new Button("Niveau atteint");

		int premierNiveau = modele.getPremierNiveau();

		for (int i = 0; i < demandeNiveau.length; i++) {
			Integer niveau = i + premierNiveau;
			demandeNiveau[i] = new Bouton(niveau, niveau.toString());
		}

		for (int i = 0; i < monterNiveau.length; i++) {
			int niveau = i + premierNiveau;
			monterNiveau[i] = new Bouton(niveau, "monter " + niveau);
		}

		for (int i = 0; i < descendreNiveau.length; i++) {
			int niveau = i + premierNiveau + 1;
			descendreNiveau[i] = new Bouton(niveau, "desc " + niveau);
		}
	}

	public void ajouterControleurs() {
		/* Déclaration et initialisation des controleurs */
		ControleurArretUrgence cau = new ControleurArretUrgence(modele);
		ControleurBoutonsExtDesc cbed = new ControleurBoutonsExtDesc(modele);
		ControleurBoutonsExtMonter cbem = new ControleurBoutonsExtMonter(modele);
		ControleurBoutonsInternes cbi = new ControleurBoutonsInternes(modele);
		ControleurNiveauAtteint cna = new ControleurNiveauAtteint(modele);

		/* Ajout des controleurs */
		arretUrgence.setOnAction(cau);
		niveauAtteint.setOnAction(cna);

		for (Bouton bouton : demandeNiveau)
			bouton.setOnAction(cbi);

		for (Bouton bouton : monterNiveau)
			bouton.setOnAction(cbem);

		for (Bouton bouton : descendreNiveau)
			bouton.setOnAction(cbed);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String nomProp = event.getPropertyName();
		
		if (nomProp.equals("etatCourant")) {
			Etat oldValue = (Etat) event.getOldValue();
			Etat newValue = (Etat) event.getNewValue();

			if (newValue == Etat.ARRET_URGENCE)
				definirStyles("arretUrgence");
			else if (oldValue == Etat.ARRET_URGENCE)
				definirStyles("signalInactif");
			
			System.out.println("transition : " + oldValue + " -> " + newValue);
		}
		else if (nomProp.contains("Niveau")) {
			String[] signal = nomProp.split("Niveau");

			int niveau = Integer.parseInt(signal[1]);

			String style = (boolean) event.getNewValue() ? "signalActif" : "signalInactif";

			Bouton bouton = null;

			switch (signal[0]) {
				case "demande":
					bouton = demandeNiveau[niveau - modele.getPremierNiveau()];
					break;
				case "monter":
					bouton = monterNiveau[niveau - modele.getPremierNiveau()];
					break;
				case "descendre":
					bouton = descendreNiveau[niveau - modele.getPremierNiveau() - 1];
					break;
				default:
					System.err.println("Impossible d'identifier le signal observé.");
					break;
			}

			definirStyle(bouton, style);
		}
	}

	public void afficher(Stage primaryStage) {

		VBox root = new VBox(7);
		root.setPadding(new Insets(15, 15, 15, 15));

		HBox boite1 = new HBox(7);
		HBox boite2 = new HBox(7);
		HBox boite3 = new HBox(7);
		HBox boite4 = new HBox(7);

		for (Bouton bouton : demandeNiveau)
			boite1.getChildren().add(bouton);

		boite1.getChildren().add(arretUrgence);

		for (Bouton bouton : monterNiveau)
			boite2.getChildren().add(bouton);

		for (Bouton bouton : descendreNiveau)
			boite3.getChildren().add(bouton);

		boite4.getChildren().add(niveauAtteint);

		root.getChildren().add(boite1);
		root.getChildren().add(boite2);
		root.getChildren().add(boite3);
		root.getChildren().add(boite4);

		Scene scene = new Scene(root, 1500, 300);
		String cheminFichierCSS = this.getClass().getResource("style.css").toExternalForm();
		scene.getStylesheets().add(cheminFichierCSS);
		definirStyles("signalInactif");
		primaryStage.setTitle("Ascenseur - Developpement");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void definirStyles(String style) {
		definirStyle(arretUrgence, style);
		definirStyle(niveauAtteint, style);

		for (Bouton bouton : demandeNiveau)
			definirStyle(bouton, style);

		for (Bouton bouton : monterNiveau)
			definirStyle(bouton, style);

		for (Bouton bouton : descendreNiveau)
			definirStyle(bouton, style);
	}

	public void definirStyle(Button bouton, String style) {

		bouton.getStyleClass().remove("signalInactif");
		bouton.getStyleClass().remove("signalActif");
		bouton.getStyleClass().remove("arretUrgence");

		bouton.getStyleClass().add(style);
	}
}
