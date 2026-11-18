package net.zoungrana.dessin;

import net.zoungrana.technique.ExigeRole;
import net.zoungrana.technique.Role;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class Parametrage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Set<ObservateurStyle> observateurs = new LinkedHashSet<>();
    private Style style = Style.DEFAUT;

    public Style style() {
        return style;
    }

    @ExigeRole(Role.EDITION)
    public void abonner(ObservateurStyle observateur) {
        observateurs.add(Objects.requireNonNull(observateur));
        observateur.actualiser(style);
    }

    @ExigeRole(Role.EDITION)
    public void desabonner(ObservateurStyle observateur) {
        observateurs.remove(observateur);
    }

    @ExigeRole(Role.EDITION)
    public void definirStyle(Style nouveauStyle) {
        Objects.requireNonNull(nouveauStyle);
        if (style.equals(nouveauStyle)) {
            return;
        }
        style = nouveauStyle;
        for (ObservateurStyle observateur : observateurs) {
            observateur.actualiser(style);
        }
    }
}
