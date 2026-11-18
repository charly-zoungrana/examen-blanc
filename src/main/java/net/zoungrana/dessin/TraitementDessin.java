package net.zoungrana.dessin;

import java.io.Serializable;
import java.util.List;

public interface TraitementDessin extends Serializable {
    double traiter(List<Figure> figures);
}
