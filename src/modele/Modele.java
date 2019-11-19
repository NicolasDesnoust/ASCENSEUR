package modele;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Timer;

import util.Etat;
import util.GestionnaireArret;
import util.Mouvement;
import util.Sens;
import util.Signaux;

/**
 * 	<p>
 * 		<b>La classe Modele représente le système de contrôle-commande d'un Ascenseur.</b>
 * 		Son nom est tiré de l'implémentation du <b>pattern MVC</b> permettant une plus grande maintenance du code.
 * 	</p>
 * 	<p>
 * 		Les actions extérieures (envois de signaux) sont simulées par l'appel des méthodes suivantes: 
 * 	</p>
 * 	<ul>
 *		<li>{@link #arretUrgence() arretUrgence}</li>
 *		<li>{@link #niveauAtteint() niveauAtteint}</li>
 * 		<li>{@link #demandeNiveau(int) demandeNiveau}</li>
 * 		<li>{@link #descendreNiveau(int) descendreNiveau}</li>
 * 		<li>{@link #monterNiveau(int) monterNiveau}</li>
 * 	</ul>
 * 	<p>
 * 		Le système est conçu de façon à commander une interface d'un moteur de traction qui dispose de 4 commandes:
 *	</p>		
 * 	<ul>
 * 		<li>"monter"</li>   
 * 		<li>"descendre"</li>
 * 		<li>"arrêter au prochain niveau"</li>
 * 		<li>"arrêter d'urgence"</li>
 * 	</ul>
 *	<p>
 * 		L'envoi d'une commande coïncide avec les valeurs possibles de l'attribut <code>etatCourant</code>.
 *  	Ainsi pour recevoir les commandes il suffit de s'inscrire en tant qu'écouteur du modèle et d'observer les changements sur cet attribut.
 *	</p>	
 *	<p>
 *  	Un écouteur s'inscrit via la méthode {@link #ajouterEcouteur(PropertyChangeListener) ajouterEcouteur} et doit implémenter l'interface 
 *  	<a href="https://docs.oracle.com/javase/7/docs/api/java/beans/PropertyChangeListener.html">PropertyChangeListener</a>.
 *		Les notifications seront traitées dans la méthode 
 *		<a href="https://docs.oracle.com/javase/7/docs/api/java/beans/PropertyChangeListener.html#propertyChange(java.beans.PropertyChangeEvent)">propertyChange</a>
 * 		 à implémenter.
 *  </p>
 *  <p>
 *  	Voici un exemple d'écouteur de commandes :
 *  </p>	
 *  <pre>
 * 		public class Ecouteur implements PropertyChangeListener {
 *			private Modele modele;
 *	
 *			public Ecouteur(Modele modele) {
 *				this.modele = modele;
 *				modele.ajouterEcouteur(this);
 *			}
 *
 *			&#064;Override
 *			public void propertyChange(PropertyChangeEvent event) {
 *				String oldValue, newValue;
 *				
 *				if (event.getPropertyName().equals("etatCourant")) {
 *					oldValue = modele.convertirEtat((int)event.getOldValue());
 *					newValue = modele.convertirEtat((int)event.getNewValue());
 *						
 *					System.out.println("transition : " + oldValue + " -&gt; " + newValue);
 *				}	
 *			}
 *		}
 *  </pre>
 *  
 *	@see <a href="https://docs.oracle.com/javase/7/docs/api/java/beans/PropertyChangeSupport.html">PropertyChangeSupport</a>
 *	@see <a href="https://docs.oracle.com/javase/7/docs/api/java/beans/PropertyChangeListener.html">PropertyChangeListener</a>
 * 
 *	@author DESNOUST Nicolas
 *	@author NIABA Victoria
 *	@version 1.0
 */
public class Modele implements IModele {
	/**
     * Constantes contenant le premier et dernier niveau de l'ascenseur. Les niveaux peuvent être négatifs mais
     * <code>PREMIER_NIVEAU</code> doit être strictement inférieur à <code>DERNIER_NIVEAU</code>.
     * 
     * @see #Modele(int, int)
     */
	private final int PREMIER_NIVEAU, DERNIER_NIVEAU;

	private final int DUREE_PAUSE = 6_000;
	/**
	 * 	<p>
	 * 		Etat courant du système de contrôle-commande.
	 * 		Ses valeurs possibles sont les constantes suivantes:
	 * 	</p>
	 * 	<p>
	 * 		<b>ARRET</b>, <b>DESCENDRE</b>, <b>MONTER</b>, <b>ARRET_URGENCE</b>, <b>ARRET_PRO_NIV</b>.
	 *	</p>
	 */
	private volatile Etat etatCourant;
	/**
	 * 	<p>
	 * 		Sens courant de progression de l'ascenseur.
	 * 		Ses valeurs possibles sont les constantes suivantes:
	 * 	</p>
	 * 	<p>
	 *  	<b>DESCENDRE</b>, <b>MONTER</b>.
	 *	</p>
	 */
	private volatile Sens sens;
	/**
	 * 	<p>
	 * 		Niveau courant de l'ascenseur compris entre les constantes <code>PREMIER_NIVEAU</code> et <code>DERNIER_NIVEAU</code>.
	 * 	</p>
	 */
	private volatile int niveauCourant;
	/**
	 * 	<p>
	 * 		Objet facilitant l'enregistrement et la suppression de signaux.
	 *	</p>
	 */
	private Signaux signaux;
	
	private Timer timer;

	// TODO: Implémenter le changement de stratégie (private IStrategie strat;)

	/**
	 * 	<p>
	 * 		Objet permettant de simplifier la gestion de la liste des écouteurs du modèle
	 * 		(les vues) et de les informer des changements de valeur d'une ou plusieurs
	 * 		propriétés.
	 * 	</p>
	 */
	private PropertyChangeSupport changeSupport;

	/**
	 * 	<p>
	 * 		Constructeur d'un système de contrôle-commande d'un ascenseur.
	 * 
	 * 		Le système suppose qu'initialement la cabine de l'ascenseur se trouve au premier niveau,
	 * 		que le sens de progression est la montée et que l'ascenseur est à l'arrêt.
	 * 	</p>
	 * 
	 * @param premierNiveau Premier niveau de l'ascenseur à commander.
	 * @param dernierNiveau Dernier niveau de l'ascenseur à commander.
	 * 
	 * @see #PREMIER_NIVEAU
	 * @see #DERNIER_NIVEAU
	 * @see #etatCourant
	 * @see sens
	 * @see niveauCourant
	 * @see signaux
	 * @see changeSupport
	 */
	public Modele(int premierNiveau, int dernierNiveau) {
		PREMIER_NIVEAU = premierNiveau;
		DERNIER_NIVEAU = dernierNiveau;
		niveauCourant = PREMIER_NIVEAU;
		sens = Sens.MONTER;
		changeSupport = new PropertyChangeSupport(this);
		signaux = new Signaux(PREMIER_NIVEAU, DERNIER_NIVEAU, changeSupport);
		etatCourant = Etat.ATTENTE_SIGNAL;
		timer = new Timer();
	}
	
	/**
	 * Retourne l'état courant du système de contrôle-commande.
	 * 
	 * @return L'état courant du système de contrôle-commande, sous forme d'entier.
	 */
	@Override
	public Etat getEtatCourant() {
		return etatCourant;
	}
	
	@Override
	public int getPremierNiveau() {
		 return PREMIER_NIVEAU;
	}
	
	@Override
	public int getDernierNiveau() {
		 return DERNIER_NIVEAU;
	}
	
	@Override
	public int getNiveauCourant() {
		 return niveauCourant;
	}
	
	@Override
	public int getDureePause() {
		 return DUREE_PAUSE;
	}
	
	/**
	 * Change l'état courant en notifiant les observateurs du système.
	 * Les observateurs sont notifiés d'un changement d'une propriété nommée <code>"etatCourant"</code>.
	 * 
	 * @param nouvelEtat La nouvelle valeur de l'état courant du système, sous forme d'entier.
	 * 
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/beans/PropertyChangeSupport.html">PropertyChangeSupport</a>
	 * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/beans/PropertyChangeListener.html">PropertyChangeListener</a>
	 */
	public void changerEtatCourant(Etat nouvelEtat) {
		Etat ancienEtat = etatCourant;
		etatCourant = nouvelEtat;
		
		// if (nouvelEtat != ARRET)
			changeSupport.firePropertyChange("etatCourant", ancienEtat, nouvelEtat);
	}
	
	public void changerSens(Sens nouveauSens) {
		Sens ancienSens = sens;
		sens = nouveauSens;
		
		// if (nouvelEtat != ARRET)
			changeSupport.firePropertyChange("sens", ancienSens, nouveauSens);
	}
	
	/**
     * Permet d'ajouter un objet de type <code>PropertyChangeListener</code> à la liste des observateurs
     * du système. Délègue l'ajout à l'objet {@link #changeSupport changeSupport}.
     * 
     * @param listener Un écouteur du système.
     * 
     * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/beans/PropertyChangeListener.html">PropertyChangeListener</a> 
     */
	public void ajouterEcouteur(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}
	
	/**
     * Permet de retirer un objet de type <code>PropertyChangeListener</code> de la liste des observateurs
     * du système. Délègue la suppression à l'objet {@link #changeSupport changeSupport}.
     * 
     * @param listener Un écouteur du système.
     * 
     * @see <a href="https://docs.oracle.com/javase/7/docs/api/java/beans/PropertyChangeListener.html">PropertyChangeListener</a> 
     */
	public void supprimerEcouteur(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	/*														*/
	/* méthodes appelées lors de la reception de signaux 	*/
	/* 														*/
	
	/**	<p>
	 * 		Méthode appelée lorsque l'on souhaite simuler la reception d'un signal de type
	 * 		"arrêt d'urgence".
	 * 	</p>
	 * 	<p>
	 * 		Ce signal permet de passer en mode arrêt d'urgence ou de le quitter seulement après un certain délais.
	 * 	</p>
	 */
	@Override
	public void arretUrgence() {
		if (etatCourant != Etat.ARRET_URGENCE) {
			changerEtatCourant(Etat.ARRET_URGENCE);
			signaux.supprimerTousLesSignaux();
		} else {
			changerEtatCourant(Etat.ATTENTE_SIGNAL);
		}
	}

	/**	<p>
	 * 		Méthode appelée lorsque l'on souhaite simuler la reception d'un signal de type
	 * 		"niveau atteint".
	 * 	</p>
	 * 	<p>
	 * 		Ce signal permet au système de savoir lorsque la cabine de l'ascenseur dépasse ou s'arrête à un niveau quelconque.
	 * 	</p>
	 */
	@Override
	public void niveauAtteint() {
		majNiveauCourant();

		switch (etatCourant) {
			case ARRET_PRO_NIV:
				changerEtatCourant(Etat.ARRET);
				// Supprime les signaux dans le sens de progression de l'ascenseur
				signaux.supprimerSignaux(niveauCourant, sens);
				System.out.println("nom du thread1: " + Thread.currentThread().getName());
				timer.schedule(new GestionnaireArret(this), DUREE_PAUSE);
				break;
			case MONTER:
			case DESCENDRE:
				if (doitArreter())
					changerEtatCourant(Etat.ARRET_PRO_NIV);
				break;
			default:
				break;
		}
	}
	
	public void temporisation() {
		boolean recommencer = true;
		System.out.println("nom du thread2: " + Thread.currentThread().getName());
		System.err.println("Fin de premiere tempo");
		
		while (recommencer) {
			if(sens == Sens.MONTER) {
				//TODO: peut etre prendre verrou du modele pour acceder/modifier les signaux
				if (!requeteAuDessus(niveauCourant) && signaux.descendreNiveau(niveauCourant)) {
					System.err.println("Changement de sens");
					signaux.setDescendreNiveau(niveauCourant, false);
					changerSens(Sens.DESCENDRE);
					try {Thread.sleep(10000);} catch (InterruptedException e) {e.printStackTrace();}
					System.err.println("Fin de seconde tempo");
				}
				else
					recommencer = false;
			}
			else if(sens == Sens.DESCENDRE)
				if (!requeteEnDessous(niveauCourant) && signaux.monterNiveau(niveauCourant)) {
					System.err.println("Changement de sens");
					signaux.setMonterNiveau(niveauCourant, false);
					changerSens(Sens.MONTER);
					try {Thread.sleep(10000);} catch (InterruptedException e) {e.printStackTrace();}
					System.err.println("Fin de seconde tempo");
				}
				else
					recommencer = false;
		}

		gererDeplacement();
		if (etatCourant == Etat.ARRET)
			changerEtatCourant(Etat.ATTENTE_SIGNAL);
		else if (doitArreter())
			changerEtatCourant(Etat.ARRET_PRO_NIV);
	}
	
	/**	<p>
	 * 		Méthode appelée lorsque l'on souhaite simuler la reception d'un signal de type
	 * 		"demande de niveau X".
	 * 	</p>
	 * 	<p>
	 * 		Ce signal correspond à l'appui d'un bouton à l'intérieur de la cabine.
	 * 		Il contient le numéro du niveau auquel la personne ayant appuyé souhaite sortir.
	 * 	</p>
	 * 
	 * 	@param niveau Le niveau demandé.
	 */
	@Override
	public void demandeNiveau(int niveau) {
		//TODO: gérer les signaux à l'étage courant
		if (etatCourant != Etat.ARRET_URGENCE)
			signaux.setDemandeNiveau(niveau, true);

		if (etatCourant == Etat.ATTENTE_SIGNAL)
			gererDeplacement();
		if (etatCourant == Etat.MONTER || etatCourant == Etat.DESCENDRE)
			if (doitArreter())
				changerEtatCourant(Etat.ARRET_PRO_NIV);
	}

	/**	<p>
	 * 		Méthode appelée lorsque l'on souhaite simuler la reception d'un signal de type
	 * 		"descendre depuis le niveau X".
	 * 	</p>
	 * 	<p>
	 * 		Ce signal correspond à l'appui d'un bouton à l'extérieur de la cabine pour descendre.
	 * 		Il contient le numéro du niveau à partir duquel la personne souhaite descendre.
	 * 	</p>
	 * 
	 * 	@param niveau Le niveau depuis lequel une personne souhaite descendre.
	 */
	@Override
	public void descendreNiveau(int niveau) {
		//TODO: gérer les signaux à l'étage courant
		if (etatCourant != Etat.ARRET_URGENCE)
			signaux.setDescendreNiveau(niveau, true);

		if (etatCourant == Etat.ATTENTE_SIGNAL)
			gererDeplacement();
		if (etatCourant == Etat.MONTER || etatCourant == Etat.DESCENDRE)
			if (doitArreter())
				changerEtatCourant(Etat.ARRET_PRO_NIV);
	}

	/**	<p>
	 * 		Méthode appelée lorsque l'on souhaite simuler la reception d'un signal de type
	 * 		"monter depuis le niveau X".
	 * 	</p>
	 * 	<p>
	 * 		Ce signal correspond à l'appui d'un bouton à l'extérieur de la cabine pour monter.
	 * 		Il contient le numéro du niveau à partir duquel la personne souhaite monter.
	 * 	</p>
	 * 
	 * 	@param niveau Le niveau depuis lequel une personne souhaite monter.
	 */
	@Override
	public void monterNiveau(int niveau) {
		//TODO: gérer les signaux à l'étage courant
		if (etatCourant != Etat.ARRET_URGENCE)
			signaux.setMonterNiveau(niveau, true);

		if (etatCourant == Etat.ATTENTE_SIGNAL)
			gererDeplacement();
		if (etatCourant == Etat.MONTER || etatCourant == Etat.DESCENDRE)
			if (doitArreter())
				changerEtatCourant(Etat.ARRET_PRO_NIV);
	}

	/******************************************************************/

	/**
	 * Met à jour le niveau courant de l'ascenseur.
	 * Cette méthode est automatiquement appelée lors de la réception du signal "niveau atteint".
	 * Elle détermine le nouveau niveau en fonction du précédent et du sens de progression de l'ascenseur.
	 * 
	 * @see #niveauAtteint()
	 */
	public void majNiveauCourant() {
		int ancienNiveau = niveauCourant;

		if (sens == Sens.MONTER)
			niveauCourant++;
		else
			niveauCourant--;

		changeSupport.firePropertyChange("niveauCourant", ancienNiveau, niveauCourant);
		
		/* Affiche l'état du système dans la console. */
		System.out.println(this);
	}
	
	/**
	 * 	Détermine si il existe des requêtes utilisateur à satisfaire aux niveaux situés en dessous de <code>niveau</code>.
	 *  
	 * 	@param niveau Le niveau à partir duquel regarder si il a des requêtes en dessous ou non.
	 * 	@return <code>true</code> si il y a une requête en dessous de <code>niveau</code>, <code>false</code> sinon.
	 *
	 *	@see #signaux
	 *	@see #niveauCourant
	 */
	public boolean requeteEnDessous(int niveau) {
		for (int i = PREMIER_NIVEAU; i < niveau; i++)
			if (signaux.demandeNiveau(i) || signaux.monterNiveau(i))
				return true;
		// Attention au bouton pour descendre qui n'existe pas au premier niveau
		for (int i = PREMIER_NIVEAU + 1; i < niveau; i++)
			if (signaux.descendreNiveau(i))
				return true;

		return false;
	}
	
	/**
	 * 	Détermine si il existe des requêtes utilisateur à satisfaire aux niveaux situés au dessus de <code>niveau</code>.
	 *  
	 * 	@param niveau Le niveau à partir duquel regarder si il a des requêtes au dessus ou non.
	 * 	@return <code>true</code> si il y a une requête au dessus de <code>niveau</code>, <code>false</code> sinon.
	 *
	 *	@see #signaux
	 *	@see #niveauCourant
	 */
	public boolean requeteAuDessus(int niveau) {
		for (int i = niveau + 1; i <= DERNIER_NIVEAU; i++)
			if (signaux.demandeNiveau(i) || signaux.descendreNiveau(i))
				return true;
		// Attention au bouton pour monter qui n'existe pas au dernier niveau
		for (int i = niveau + 1; i < DERNIER_NIVEAU; i++)
			if (signaux.monterNiveau(i))
				return true;

		return false;
	}

	/**
	 * 	Détermine si l'ascenseur doit s'arrêter au prochain niveau.
	 *  La stratégie adoptée ici est de favoriser les requêtes utilisateurs situées dans le sens de progression de l'ascenseur.
	 *  
	 * 	@return <code>true</code> si l'ascenseur doit s'arrêter, <code>false</code> sinon.
	 *
	 *  @see #sens
	 *	@see #requeteAuDessus(int)
	 *	@see #requeteEnDessous(int)
	 */
	public boolean doitArreter() {
		if (sens == Sens.MONTER) {
			if (signaux.demandeNiveau(niveauCourant + 1)
			|| (signaux.descendreNiveau(niveauCourant + 1) && !requeteAuDessus(niveauCourant + 1))) {
				return true;
			}
			if (niveauCourant + 1 != DERNIER_NIVEAU)
				if (signaux.monterNiveau(niveauCourant + 1))
					return true;
		}
		else {
			if (signaux.demandeNiveau(niveauCourant - 1)
			|| (signaux.monterNiveau(niveauCourant - 1) && !requeteEnDessous(niveauCourant - 1))) {
				return true;
			}
			if (niveauCourant - 1 != PREMIER_NIVEAU)
				if (signaux.descendreNiveau(niveauCourant - 1))
					return true;
		}

		return false;
	}
	
	/**
	 * 	Gère le passage de l'état courant du système aux valeurs DESCENDRE, MONTER en calculant
	 * 	son nouveau deplacement ainsi que s'il doit s'arrêter ou non.
	 * 	
	 *	@see #changerEtatCourant(int)
	 *	@see #calculDeplacement()
	 */
	public void gererDeplacement() {
		Mouvement dep = calculDeplacement();

		if (dep == Mouvement.DESCENDRE) {
			changerSens(Sens.DESCENDRE);
			changerEtatCourant(Etat.DESCENDRE);

		} else if (dep == Mouvement.MONTER) {
			changerSens(Sens.MONTER);
			changerEtatCourant(Etat.MONTER);
		}
	}

	/**
	 * 	Calcule le nouveau déplacement de l'ascenseur en fonction du sens de progression et des requêtes
	 * 	utilisateur existantes.
	 * 	
	 *  @return <code>MONTER</code> si l'ascenseur doit monter, <code>DESCENDRE</code> si l'ascenseur doit descendre, <code>AUCUN</code> si il ne doit pas se déplacer.
	 *
	 *  @see #sens
	 *	@see #requeteAuDessus(int)
	 *	@see #requeteEnDessous(int)
	 */
	public Mouvement calculDeplacement() {

		if (sens == Sens.MONTER) {
			if (requeteAuDessus(niveauCourant))
				return Mouvement.MONTER;
			else if (requeteEnDessous(niveauCourant))
				return Mouvement.DESCENDRE;
		}
		else {
			if (requeteEnDessous(niveauCourant))
				return Mouvement.DESCENDRE;
			else if (requeteAuDessus(niveauCourant))
				return Mouvement.MONTER;
		}
		
		return Mouvement.AUCUN;
	}

	/**
	 * 	Réécriture de la méthode <code>toString()</code> provenant de la classe <code>Object</code>.
	 * 
	 *  @return Une chaîne de caractères décrivant brièvement le système de contrôle-commande.
	 *
	 *  @see #sens
	 *	@see #etatCourant
	 *	@see #convertirEtat(int)
	 *	@see <a href="https://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#toString()">Object.toString()</a>
	 */
	@Override
	public String toString() {
		return "SYSTEME: {etat: " + etatCourant + " sens: " + sens + " niveau: " + niveauCourant + "}";
	}
}
