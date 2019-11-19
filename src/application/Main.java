package application;
import javafx.application.Application;
import javafx.stage.Stage;
import modele.Modele;
import vues.*;

public class Main extends Application {
	
	private Modele modele;
	private IVue vue;
	
	public Main () {
		modele = new Modele(0, 10);
		vue = new VueClient(modele);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		vue.afficher(primaryStage);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
