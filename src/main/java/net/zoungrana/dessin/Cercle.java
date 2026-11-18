package net.zoungrana.dessin;

import java.util.Objects;

public final class Cercle extends Figure {
    private static final long serialVersionUID = 1L;
    private final Point centre;
    private final double rayon;

    public Cercle(Point centre, double rayon) {
        this.centre = Objects.requireNonNull(centre);
        this.rayon = dimension(rayon);
    }

    @Override
    public double perimetre() {
        return 2 * Math.PI * rayon;
    }

    @Override
    public double surface() {
        return Math.PI * rayon * rayon;
    }

    @Override
    public String dessiner() {
        return "Cercle [centre=" + centre + ", rayon=" + rayon + ", style=" + style() + "]";
    }
}
