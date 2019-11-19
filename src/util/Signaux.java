package util;

import java.beans.PropertyChangeSupport;
import java.util.concurrent.atomic.AtomicBoolean;

public class Signaux {
	private final int PREMIER_NIVEAU, DERNIER_NIVEAU;
	
	// Demandes à l'interieur de la cabine
	private AtomicBoolean[] demandeNiveau;
	
	// Demandes à l'exterieur de la cabine
	private AtomicBoolean[] monterNiveau; 
	private AtomicBoolean[] descendreNiveau;
	
	private PropertyChangeSupport changeSupport;
	
	public Signaux(int premierNiveau, int dernierNiveau, PropertyChangeSupport changeSupport) {
		PREMIER_NIVEAU = premierNiveau;
		DERNIER_NIVEAU = dernierNiveau;
		this.changeSupport = changeSupport;
		
		/* Initialise les tableaux et les booléens contenus à false */
		demandeNiveau = new AtomicBoolean[DERNIER_NIVEAU - PREMIER_NIVEAU + 1];
		monterNiveau = new AtomicBoolean[DERNIER_NIVEAU - PREMIER_NIVEAU];
		descendreNiveau = new AtomicBoolean[DERNIER_NIVEAU - PREMIER_NIVEAU];
		
		for (int i = 0; i < demandeNiveau.length; i++)
			demandeNiveau[i] = new AtomicBoolean();
		for (int i = 0; i < monterNiveau.length; i++)
			monterNiveau[i] = new AtomicBoolean();
		for (int i = 0; i < descendreNiveau.length; i++)
			descendreNiveau[i] = new AtomicBoolean();
	}

	public boolean demandeNiveau(int niveau) {
		return demandeNiveau[niveau - PREMIER_NIVEAU].get();
	}

	public void setDemandeNiveau(int niveau, boolean valeur) {
		boolean anciValeur = demandeNiveau[niveau - PREMIER_NIVEAU].get();
		demandeNiveau[niveau - PREMIER_NIVEAU].set(valeur);
		
		changeSupport.firePropertyChange("demandeNiveau"+niveau, anciValeur, valeur);
	}

	public boolean monterNiveau(int niveau) {
		return monterNiveau[niveau - PREMIER_NIVEAU].get();
	}
	
	public void setMonterNiveau(int niveau, boolean valeur) {
		boolean anciValeur = monterNiveau[niveau - PREMIER_NIVEAU].get();
		monterNiveau[niveau - PREMIER_NIVEAU].set(valeur);
		
		changeSupport.firePropertyChange("monterNiveau"+niveau, anciValeur, valeur);
	}
	
	public boolean descendreNiveau(int niveau) {
		return descendreNiveau[niveau - PREMIER_NIVEAU - 1].get();
	}

	public void setDescendreNiveau(int niveau, boolean valeur) {
		boolean anciValeur = descendreNiveau[niveau - PREMIER_NIVEAU - 1].get();
		descendreNiveau[niveau - PREMIER_NIVEAU - 1].set(valeur);
		
		changeSupport.firePropertyChange("descendreNiveau"+niveau, anciValeur, valeur);
	}
	
	public void supprimerTousLesSignaux() {
		for (int i = 0; i < demandeNiveau.length; i++)
			demandeNiveau[i].set(false);
		
		for (int i = 0; i < monterNiveau.length; i++) {
			monterNiveau[i].set(false);
			descendreNiveau[i].set(false);
		}
	}
	
	public void supprimerSignaux(int niveau, Sens sens) {
		setDemandeNiveau(niveau, false);
		
		if (niveau > PREMIER_NIVEAU && sens == Sens.DESCENDRE)
			setDescendreNiveau(niveau, false);
		
		if (niveau < DERNIER_NIVEAU && sens == Sens.MONTER)
			setMonterNiveau(niveau, false);
	}
}
