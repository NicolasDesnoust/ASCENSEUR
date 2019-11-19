package vues;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import controleurs.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import modele.IModele;
import util.Bouton;
import util.Etat;
import util.Sens;

/**
 * 
 * @author desno
 *
 * @see <a href="https://pngtree.com/free-vectors">Auteur de l'image de l'ascenseur</a>
 */
public class VueClient implements IVue, PropertyChangeListener {
	private IModele modele;

	/* Chemins pour exécuter le projet via le script run.sh */
	private final String soundsPath = "../resources/sounds", picturesPath = "../resources/pictures",
			viewsPath = "vues";
	/*
	 * Chemins pour exécuter le projet via eclipse.
	private final String soundsPath = "resources/sounds", picturesPath = "resources/pictures",
			viewsPath = "src/vues";
	*/
	private final int LARGEUR_SCENE = 1500, MARGE_BOUTONS = 7, LARGEUR_BOUTON_INTERNE = 90, LARGEUR_BOUTON_EXTERNE = 110;
	
	private Label niveau;
	private Button arretUrgence, niveauAtteint;
	private Bouton[] demandeNiveau, descendreNiveau, monterNiveau;
	private ImageView interieurImg, porteGaucheImg, porteDroiteImg, cabineImg;
	private Image fleche, cabineMontImg, cabineDescImg;
	private AudioClip ding, boutonEnfonce, boutonRelache;
	
	private Timeline timeline;

	public VueClient(IModele modele) {
		this.modele = modele;
		modele.ajouterEcouteur(this);

		int premierNiveau = modele.getPremierNiveau(), dernierNiveau = modele.getDernierNiveau();
		demandeNiveau = new Bouton[dernierNiveau - premierNiveau + 1];
		descendreNiveau = new Bouton[dernierNiveau - premierNiveau];
		monterNiveau = new Bouton[dernierNiveau - premierNiveau];
		
		initialiserImages();
		initialiserBoutons();
		ajouterControleurs();
		initialiserSons();
		initialiserAnimation();
		
		niveau = new Label(Integer.toString(modele.getPremierNiveau()));
		niveau.setTextFill(Color.RED);
		niveau.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 19));
		niveau.setTextAlignment(TextAlignment.RIGHT);
	}
	
	public void initialiserSons() {
		ding = new AudioClip("file:" + soundsPath + "/ding.wav");
		boutonEnfonce = new AudioClip("file:" + soundsPath + "/boutonEnfonce.mp3");
		boutonRelache = new AudioClip("file:" + soundsPath + "/boutonRelache.mp3");
	}

	private void initialiserImages() {
		interieurImg = new ImageView(new Image("file:" + picturesPath + "/interieur.png"));
		porteGaucheImg = new ImageView("file:" + picturesPath + "/porteGauche.png");
		porteDroiteImg = new ImageView("file:" + picturesPath + "/porteDroite.png");
		cabineMontImg = new Image("file:" + picturesPath + "/cabineMonter.png");
		cabineDescImg = new Image("file:" + picturesPath + "/cabineDescendre.png");
		cabineImg = new ImageView(cabineMontImg);
		
		interieurImg.setPreserveRatio(true);
		porteGaucheImg.setPreserveRatio(true);
		porteDroiteImg.setPreserveRatio(true);
		cabineImg.setPreserveRatio(true);
		
		interieurImg.setFitWidth(LARGEUR_SCENE);
		porteGaucheImg.setFitWidth(LARGEUR_SCENE);
		porteDroiteImg.setFitWidth(LARGEUR_SCENE);
		cabineImg.setFitWidth(LARGEUR_SCENE);
		
		porteGaucheImg.setX(-100);
		
		fleche = new Image("file:" + picturesPath + "/fleche.png", 16, 16, true, true);
	}

	public void initialiserBoutons() {
		arretUrgence = new Button("Arrêt d'urgence");
		niveauAtteint = new Button("Niveau atteint");

		int premierNiveau = modele.getPremierNiveau();

		for (int i = 0; i < demandeNiveau.length; i++) {
			Integer niveau = i + premierNiveau;
			demandeNiveau[i] = new Bouton(niveau, niveau.toString());
			demandeNiveau[i].setMinWidth(LARGEUR_BOUTON_INTERNE);
		}

		for (int i = 0; i < monterNiveau.length; i++) {
			Integer niveau = i + premierNiveau;
			monterNiveau[i] = new Bouton(niveau, niveau.toString(), new ImageView(fleche));
			monterNiveau[i].setMinWidth(LARGEUR_BOUTON_EXTERNE);
		}

		for (int i = 0; i < descendreNiveau.length; i++) {
			Integer niveau = i + premierNiveau + 1;
			ImageView temp = new ImageView(fleche);
			temp.setRotate(180);
			descendreNiveau[i] = new Bouton(niveau, niveau.toString(), temp);
			descendreNiveau[i].setMinWidth(LARGEUR_BOUTON_EXTERNE);
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

			if (newValue == Etat.ARRET_URGENCE) {
				definirStyles("arretUrgence");
				niveau.setText("OFF");
			}
			else if (oldValue == Etat.ARRET_URGENCE) {
				definirStyles("signalInactif");
				niveau.setText(Integer.toString(modele.getNiveauCourant()));
			}
			else if (newValue == Etat.ARRET) {
				ding.play();
				timeline.play();
			}
			
			System.out.println("transition : " + oldValue + " -> " + newValue);
		}
		else if (nomProp.equals("sens")) {
			Sens newValue = (Sens) event.getNewValue();
			
			cabineImg.setImage(newValue == Sens.MONTER ? cabineMontImg : cabineDescImg);
		}
		else if (nomProp.equals("niveauCourant")) {
			niveau.setText(Integer.toString((int)event.getNewValue()));
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
	
	public void initialiserAnimation() {
		int dureeAnimation = modele.getDureePause();
		int dureeMouvPortes = 2000, dureeAttente = dureeAnimation - 2 * dureeMouvPortes;
		
		timeline = new Timeline();
		timeline.getKeyFrames().addAll(
			// Position initiale des portes
            new KeyFrame(Duration.ZERO, new KeyValue(porteGaucheImg.translateXProperty(), 0),
            		new KeyValue(porteDroiteImg.translateXProperty(), 0)),
            // Position des portes après ouverture
            new KeyFrame(new Duration(dureeMouvPortes), new KeyValue(porteGaucheImg.translateXProperty(), -100),
            		new KeyValue(porteDroiteImg.translateXProperty(), 100)),
            // KeyFrame similaire à la précédente pour créer une attente
            new KeyFrame(new Duration(dureeMouvPortes + dureeAttente), new KeyValue(porteGaucheImg.translateXProperty(), -100),
            		new KeyValue(porteDroiteImg.translateXProperty(), 100)),
            // Retour à la position initiale des portes	
            new KeyFrame(new Duration(dureeAnimation), new KeyValue(porteGaucheImg.translateXProperty(), 0),
            		new KeyValue(porteDroiteImg.translateXProperty(), 0))
        );
	}

	public void afficher(Stage primaryStage) {
		StackPane root = new StackPane();
		AnchorPane boutonsAP = new AnchorPane();
		HBox boutonsExternesHBox = new HBox(MARGE_BOUTONS);
		VBox boutonsMonterVBox = new VBox(MARGE_BOUTONS), boutonsDescendreVBox = new VBox(MARGE_BOUTONS);
		VBox boutonsInternesVBox = new VBox(MARGE_BOUTONS);
		GridPane boutonsInternesGP = new GridPane();
		HBox boutonsInternesUtil = new HBox(MARGE_BOUTONS);
	
		boutonsMonterVBox.setPrefWidth(LARGEUR_BOUTON_EXTERNE);
		boutonsDescendreVBox.setPrefWidth(LARGEUR_BOUTON_EXTERNE);
		boutonsInternesGP.setPrefWidth(LARGEUR_BOUTON_INTERNE);
		boutonsInternesGP.setHgap(MARGE_BOUTONS);
		boutonsInternesGP.setVgap(MARGE_BOUTONS);
		
		for (int i = 0; i < demandeNiveau.length; i++) {
			GridPane.setConstraints(demandeNiveau[i], i%4, i/4);
			boutonsInternesGP.getChildren().add(demandeNiveau[i]);
		}
		boutonsInternesUtil.getChildren().add(arretUrgence);
		boutonsInternesUtil.getChildren().add(niveauAtteint);
		
		boutonsInternesVBox.getChildren().add(boutonsInternesGP);
		boutonsInternesVBox.getChildren().add(boutonsInternesUtil);
		
		for (Bouton bouton : monterNiveau)
			boutonsMonterVBox.getChildren().add(bouton);
		for (Bouton bouton : descendreNiveau)
			boutonsDescendreVBox.getChildren().add(bouton);
		
		boutonsExternesHBox.getChildren().add(boutonsMonterVBox);
		boutonsExternesHBox.getChildren().add(boutonsDescendreVBox);

		AnchorPane.setTopAnchor(boutonsExternesHBox, 50.0);
		AnchorPane.setRightAnchor(boutonsExternesHBox, 100.0);
		boutonsAP.getChildren().add(boutonsExternesHBox);
		
		AnchorPane.setTopAnchor(boutonsInternesVBox, 345.0);
		AnchorPane.setLeftAnchor(boutonsInternesVBox, 100.0);
		boutonsAP.getChildren().add(boutonsInternesVBox);
		
		AnchorPane.setBottomAnchor(niveau, 534.0);
		AnchorPane.setRightAnchor(niveau, LARGEUR_SCENE/2.0 - 60.0);
		boutonsAP.getChildren().add(niveau);
				
		root.getChildren().add(interieurImg);
		interieurImg.toBack();
		root.getChildren().add(porteGaucheImg);
		root.getChildren().add(porteDroiteImg);
		root.getChildren().add(cabineImg);
		root.getChildren().add(boutonsAP);
		boutonsAP.toFront();
        
		Scene scene = new Scene(root, LARGEUR_SCENE, LARGEUR_SCENE*(1080.0/1920.0));
		
		String cheminFichierCSS = "file:" + viewsPath + "/style.css";
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
