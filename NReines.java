// Un module pour les couleurs

import java.awt.Color;
// Trois modules pour l'interface graphique
import IG.ZoneCliquable;
import IG.Grille;
import IG.Fenetre;


/* 

   Classe principale NReines.
   Ici on initialise le jeu et l'affichage.

   Pour l'affichage, on fait appel à la classe [IG.Fenetre],
   et on utilise deux méthodes de cette classe dont les
   signatures sont :

   public void ajouteElement([composant graphique]);
   public void dessineFenetre();

*/

public class NReines {

    public static void main(String[] args) {
        // Récupération du premier paramètre, et conversion en entier.
        int nb = Integer.parseInt(args.length <1 ? "8" : args[0]);
        // Création d'une fenêtre graphique, d'un échiquiers
        // et de deux boutons.
        Fenetre fenetre = new Fenetre(nb + " reines");
        Plateau plateau = new Plateau(nb);
        Validation validation = new Validation(plateau);
        Indice indice = new Indice(plateau);
        // On précise que l'échiquier et les boutons doivent
        // être affichés dans la fenêtre graphique.
        fenetre.ajouteElement(plateau);
        fenetre.ajouteElement(validation);
        fenetre.ajouteElement(indice);
        fenetre.dessineFenetre();
    }
}

/* 

   Bouton de validation. On ne demande à ce bouton de ne réagir
   qu'aux clics gauches. Le bouton doit se colorer en vert si
   la configuration actuelle du plateau est licite, et en rouge
   sinon.

   Les deux appels de méthode suivants permettent respectivement
   de colorer le bouton en vert ou rouge :

   setBackground(Color.GREEN);
   setBackground(Color.RED);

   On fait appel à la classe abstraite [IG.ZoneCliquable].
   Pour cela, il faut définir deux méthodes correspondant aux
   actions à effectuer lors d'un clic avec le bouton gauche ou
   avec le bouton droit. Leurs signatures sont :

   public void clicGauche();
   public void clicDroit();

*/

class Validation extends ZoneCliquable {

    private Plateau plateau;

    public Validation(Plateau plateau) {
        // Création d'une zone cliquable de dimensions 80*25 pixels,
        // et contenant le texte "Valider".
        super("Valider", 80, 25);
        this.plateau = plateau;
    }

    /**
     * Makes the button go green if the current configuration is Valid.
     */
    public void clicGauche() {
        if (plateau.verifieConfiguration()){
            plateau.updateQueenDisplay();
            setBackground(Color.green);
        }else{
            plateau.afficheThreateningQueens();
            setBackground(Color.red);
        }
    }

    public void clicDroit() {
    }
}

/*

  Bouton de demande d'indice. On ne demande à ce bouton de ne
  réagir qu'aux clics gauches. Si la configuration actuelle du
  plateau peut être complétée en une configuration complète,
  alors le bouton doit se colorer en vert, et doit indiquer un
  prochain coup possible en colorant la case correspondante en
  bleu.

*/

class Indice extends ZoneCliquable {

    private Plateau plateau;

    public Indice(Plateau plateau) {
        // Création d'une zone cliquable de dimensions 80*25 pixels,
        // et contenant le texte "Indice".
        super("Indice", 80, 25);
        this.plateau = plateau;
    }

    public void clicGauche() {
        if(plateau.verifieResolubilite()){
            setBackground(Color.green);
            plateau.afficheIndice();
        }else{
            setBackground(Color.red);
        }
    }

    public void clicDroit() {
    }
}



/* 

   Une classe pour l'échiquier.

   La mention [extends Grille] permet d'afficher les cases
   sous la forme d'une grille, en utilisant la classe [IG.Grille].

   Lorsqu'une case [c] est créée, pour l'intégrer à l'affichage
   graphique il faut réaliser l'appel de méthode suivante :

   this.ajouteElement(c);

*/

class Plateau extends Grille {

    // Attributs : taille et tableau de cases
    private final int taille;
    private Case[][] plateau;
    private int indiceL;
    private int indiceC;

    // Constructeur
    public Plateau(int taille) {

        // Initialisation de la grille graphique de dimensions taille*taille
        super(taille, taille);
        this.taille = taille;
		this.plateau = new Case[taille][taille];
		for (int y = 0; y < taille; y += 1){
		    for (int x = 0; x < taille; x += 1){
		        plateau[y][x] = new Case(this);
                this.ajouteElement(plateau[y][x]);
            }
        }
		setBackground(Color.gray);
    }


    // Méthode de vérification générale.
    public boolean verifieConfiguration() {
        return verifieLignes() && verifieColonnes() && verifieDiagonales() && verifieAntidiagonales();
    }

    /**
     * Colore en rouge les reines qui empêchent la grille d'être valide.
     * Ne fonctionne pas quand il y a plus de deux reines posant problème.
     *
     */
    public void afficheThreateningQueens(){
        for (int y = 0; y < taille; y +=1){
            for (int x  =0; x < taille; x+= 1){
                if (plateau[y][x].estOccupee()){
                    plateau[y][x].clicGauche();
                    if (verifieConfiguration()){
                        plateau[y][x].clicGauche();
                        plateau[y][x].setBackground(Color.red);
                    }else{
                        plateau[y][x].clicGauche();
                        plateau[y][x].setBackground(Color.black);
                    }

                }
            }
        }
    }

    /**
     * remet les reines en noirs.
     */
    public void updateQueenDisplay(){
        for (int y = 0; y < taille; y +=1){
            for (int x  =0; x < taille; x+= 1){
                if (plateau[y][x].estOccupee()){
                    plateau[y][x].setBackground(Color.black);
                }
            }
        }
    }
    /**Compte le nombre de reines sur la ligne.
     * @param l : la ligne sur laquelle compter
     * @return entier contenant le nombre de reines sur la ligne l.
     */
    private int compteLigne(Case [] l){
        int sum = 0;
        for (Case c : l){
            sum+=c.estOccupee()? 1:0;
        }
        return sum;
    }

    /**Verifie s'il n'ya pas de reine qui en menace une autre par ligne.
     *
     * @return true si et seulement s'il n'y en a aucune
     */
    private boolean verifieLignes(){
        for (Case [] l : plateau){
            if (compteLigne(l) > 1){
                return false;
            }
        }
        return true;
    }

    /**Compte le nombre de reines sur la colonne d'indice x.
     * @param x : la coordonnee de la colonne
     * @return entier contenant le nombre de reines sur la colonne d'indice x.
     */
    private int compteColonne(int x){
        int sum = 0;
        for (int y  =0; y < taille; y += 1){
            sum += plateau[y][x].estOccupee() ? 1 : 0;
        }
        return sum;
    }

    /**Verifie s'il n'ya pas de reine qui en menace une autre par colonne.
     *
     * @return true si et seulement s'il n'y en a aucune
     */
    private boolean verifieColonnes(){
        for (int x= 0; x < taille; x += 1){
            if (compteColonne(x) > 1){
                return false;
            }
        }
        return true;
    }

    // Cadeau : vérification des diagonales et des antidiagonales.

    // Les méthodes de vérification [verifieDiagonales] et
    // [verifieAntidiagonales] font appel à des méthodes auxiliaires
    // [compteDiagonale] et [compteAntidiagonale] dénombrant les reines
    // présentes sur une diagonale ou antidiagonale donnée par son indice.

    // Pour un plateau de côté [N], et [k] un entier tel que [-N < k < N], la
    // diagonale d'indice [k] est formée par les cases de coordonnées [i, i+k]
    // (pour les [i] tels que cette paire de coordonnées est valide).
    private int compteDiagonale(int k) {
        int nb = 0;
        // Les variables [min, max] sont définies avec les valeurs minimales
        // et maximales de [i] telles que [i, i+k] soit une paire de
        // coordonnées valides.
        int min, max;
        if (k < 0) {
            min = -k;
            max = this.taille;
        } else {
            min = 0;
            max = this.taille - k;
        }
        for (int i = min; i < max; i++) {
            if (plateau[i][i + k].estOccupee()) {
                nb++;
            }
        }
        return nb;
    }

    private boolean verifieDiagonales() {
        // Les diagonales d'indices [-N+1] et [N-1] ne sont pas analysées, car
        // elles ne sont constituées que d'une case et ne peuvent donc pas
        // contenir plus d'une reine.
        for (int k = 2 - taille; k < taille - 1; k++) {
            if (compteDiagonale(k) > 1) {
                return false;
            }
        }
        return true;
    }

    // L'antidiagonale d'indice [k] est définie similairement à la diagonale
    // d'indice [k], avec les cases de coordonnées [N-1-i, i+k].
    private int compteAntidiagonale(int k) {
        int nb = 0;
        int min, max;
        if (k < 0) {
            min = -k;
            max = this.taille;
        } else {
            min = 0;
            max = this.taille - k;
        }
        for (int i = min; i < max; i++) {
            if (plateau[taille - 1 - i][i + k].estOccupee()) {
                nb++;
            }
        }
        return nb;
    }

    private boolean verifieAntidiagonales() {
        for (int k = 2 - taille; k < taille - 1; k++) {
            if (compteAntidiagonale(k) > 1) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return le total des reines sur le plateau.
     */
    private int compteToutesReines(){
        int sum = 0;
        for (Case [] l : plateau){
            sum += compteLigne(l);
        }
        return sum;
    }

    /**
     * permet d'afficher le dernier indice calculé par verifieResolubilite.
     * si il la grille est finie n'affiche pas de case
     * si la grille est invalide, ne doit pas être appelée.
     */
    public void afficheIndice(){
        if(indiceC >= 0 && indiceL >=0)
            plateau[indiceL][indiceC].setBackground(Color.blue);
    }

    // Méthode vérifiant que la configuration actuelle est
    // résoluble et plaçant le cas échéant dans [indiceL] et
    // [indiceR] les coordonnées d'un coup possible vers une
    // solution.
    // La méthode est récursive, et explore tous les coups valides.
    // Lors de l'exploration d'un coup, la méthode modifie l'échiquier,
    // puis annule ses modifications lors du "backtrack".
    // si la grille est déjà finie, l'indice retourne true,
    // mais les coordonées de la case à colorier deviennent -1, -1.
    public boolean verifieResolubilite() {
        if (!verifieConfiguration()){
            return false;
        }
        if (compteToutesReines() == taille){
            indiceC = -1;
            indiceL = -1;
            return true;
        }
        int numberOfQueens = 0;
        for (int y = 0; y < taille; y +=1){
            for (int x = 0; x < taille; x+= 1){
                if (! plateau[y][x].estOccupee()){
                    plateau[y][x].clicGauche();
                    if (verifieResolubilite()){
                        plateau[y][x].clicGauche();
                        indiceL = y;
                        indiceC = x;
                        return true;
                    }
                    plateau[y][x].clicGauche();
                }
            }
        }
        return false;
    }

}

/*

   Une classe pour les cases du terrain de jeu.

   On demande à ces cases de réagir aux clics gauches.
   Lorsque l'on clique sur une case libre (blanche), celle-ci doit
   être colorée en noir et indiquée comme occupée.
   Lorsque l'on clique sur une case occupée (noire), celle-ci doit
   être colorée en blanc et indiquée comme libre.

   La mention [extends ZoneCliquable] permet de faire réagir les
   cases aux clics de souris, en utilisant [IG.ZoneCliquable] et
   les méthodes

   public void clicGauche();
   public void clicDroit();

*/

class Case extends ZoneCliquable {
	private boolean occupee;
    // Constructeur
    public Case(Plateau plateau) {
        // Initialisation d'une case cliquable, de dimensions 40*40 pixels.
        super(40, 40);
		occupee = false;
		setBackground(Color.WHITE);
        /* À compléter ! */

    }

    // Permet de tester si une case est occupée.
    public boolean estOccupee() {
        return occupee; /* À remplacer */
    }

    // Action à effectuer lors d'un clic gauche.
    public void clicGauche() {
    	occupee = ! occupee;
    	if (occupee){
    	    setBackground(Color.black);
        }else{
    	    setBackground(Color.white);
        }

	}

    // Action à effectuer lors d'un clic droit.
    public void clicDroit() {
    }

}
