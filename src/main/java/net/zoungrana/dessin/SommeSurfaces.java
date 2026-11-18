package net.zoungrana.dessin;

import java.util.List;

public final class SommeSurfaces implements TraitementDessin {
    private static final long serialVersionUID = 1L;

    @Override
    public double traiter(List<Figure> figures) {
        return figures.stream().mapToDouble(Figure::surface).sum();
    }
}
