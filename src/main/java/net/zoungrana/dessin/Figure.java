package net.zoungrana.dessin;

import java.util.Objects;

public abstract class Figure implements ObservateurStyle {
    private static final long serialVersionUID = 1L;
    private Style style = Style.DEFAUT;

    public final Style style() {
        return style;
    }

    @Override
    public void actualiser(Style style) {
        this.style = Objects.requireNonNull(style);
    }

    public abstract double perimetre();
    public abstract double surface();

    /** Retourne l'état à afficher, y compris pour un groupe imbriqué. */
    public abstract String dessiner();

    protected static double dimension(double valeur) {
        if (!Double.isFinite(valeur) || valeur <= 0) {
            throw new IllegalArgumentException("Une dimension doit être finie et strictement positive.");
        }
        return valeur;
    }
}

