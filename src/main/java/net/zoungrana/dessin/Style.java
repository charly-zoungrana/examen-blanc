package net.zoungrana.dessin;

import java.io.Serializable;

public record Style(int epaisseur, int couleurContour, int couleurRemplissage)
        implements Serializable {
    public static final Style DEFAUT = new Style(1, 0x263548, 0xCBD5E1);

    public Style {
        if (epaisseur < 0) {
            throw new IllegalArgumentException("L'épaisseur doit être positive ou nulle.");
        }
    }
}
