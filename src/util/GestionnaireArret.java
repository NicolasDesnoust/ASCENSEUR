package util;

import java.util.TimerTask;

import modele.IModele;

public class GestionnaireArret extends TimerTask {

	private IModele modele;
	
	public GestionnaireArret (IModele modele) {
		this.modele = modele;
	}
	
	@Override
	public void run() {
		modele.temporisation();
	}

}
