package net.zoungrana.dessin;

import java.io.Serializable;

public interface ObservateurStyle extends Serializable {
    void actualiser(Style style);
}
