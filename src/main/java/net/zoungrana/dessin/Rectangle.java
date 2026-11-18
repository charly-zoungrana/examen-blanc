package net.zoungrana.dessin;

import java.util.Objects;

public final class Rectangle extends Figure {
    private static final long serialVersionUID = 1L;
    private final Point coinSuperieurGauche;
    private final double largeur;
    private final double hauteur;

    public Rectangle(Point coinSuperieurGauche, double largeur, double hauteur) {
        this.coinSuperieurGauche = Objects.requireNonNull(coinSuperieurGauche);
        this.largeur = dimension(largeur);
        this.hauteur = dimension(hauteur);
    }

    @Override
    public double perimetre() {
        return 2 * (largeur + hauteur);
    }

    @Override
    public double surface() {
        return largeur * hauteur;
    }

    @Override
    public String dessiner() {
        return "Rectangle [coin=" + coinSuperieurGauche + ", largeur=" + largeur
                + ", hauteur=" + hauteur + ", style=" + style() + "]";
    }
}
