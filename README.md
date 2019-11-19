# ASCENSEUR - GENIE LOGICIEL

## Lancer la simulation
Pour lancer la simulation, ouvrez un terminal, placez-vous dans le dossier ASCENSEUR/src
et exécutez les commandes suivantes (LINUX):
$ chmod +x run.sh  
$ ./run.sh

Pour supprimer les fichiers .class générés à l'étape précédente:
$ chmod +x clean.sh  
$ ./clean.sh

##Description du projet
Ce projet est réalisé dans le cadre du cours de Génie logiciel du Master 1 Ingénierie du logiciel et des données. 
Il s'agit de réaliser une simulation du système de contrôle commande d'un ascenseur. Le projet est réalisé seul et en Java.

L'interface met à disposition de l'utilisateur les éléments suivants:
- Des boutons externes à l'ascenseur pour signaler une montée ou une descente.
- Des boutons internes pour demander un arrêt à un étage.
- Un bouton d'arrêt d'urgence pour stopper l'ascenseur.
- Un bouton pour simuler le fait que la cabine a atteint le prochain niveau dans le sens de progression.
- Un panneau affichant le dernier niveau atteint et le sens de progression.
