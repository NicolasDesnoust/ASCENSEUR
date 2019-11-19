package modele;

import java.beans.PropertyChangeListener;

import util.Etat;

public interface IModele {
	public void arretUrgence();
	
	public void niveauAtteint();
	
	public void demandeNiveau(int niveau);
	
	public void descendreNiveau(int niveau);
	
	public void monterNiveau(int niveau);
	
	public Etat getEtatCourant();
	
	public int getPremierNiveau();
	
	public int getDernierNiveau();
	
	public void ajouterEcouteur(PropertyChangeListener listener);
	
	public void supprimerEcouteur(PropertyChangeListener listener);

	public void temporisation();

	public int getNiveauCourant();

	int getDureePause();
}
