package net.zoungrana.dessin;

import net.zoungrana.technique.ExigeRole;
import net.zoungrana.technique.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Groupe extends Figure {
    private static final long serialVersionUID = 1L;
    private final List<Figure> enfants = new ArrayList<>();

    @ExigeRole(Role.EDITION)
    public void ajouter(Figure figure) {
        Objects.requireNonNull(figure);
        if (figure == this || figure instanceof Groupe groupe && groupe.contient(this)) {
            throw new IllegalArgumentException("Un groupe ne peut pas contenir un de ses ancêtres.");
        }
        figure.actualiser(style());
        enfants.add(figure);
    }

    @Override
    @ExigeRole(Role.EDITION)
    public void actualiser(Style style) {
        super.actualiser(style);
        for (Figure enfant : enfants) {
            enfant.actualiser(style);
        }
    }

    @ExigeRole(Role.EDITION)
    public boolean supprimer(Figure figure) {
        return enfants.remove(figure);
    }

    public List<Figure> enfants() {
        return List.copyOf(enfants);
    }

    private boolean contient(Groupe cible) {
        return enfants.stream().anyMatch(
                enfant -> enfant == cible || enfant instanceof Groupe groupe && groupe.contient(cible));
    }

    @Override
    public double perimetre() {
        return enfants.stream().mapToDouble(Figure::perimetre).sum();
    }

    @Override
    public double surface() {
        return enfants.stream().mapToDouble(Figure::surface).sum();
    }

    @Override
    public String dessiner() {
        StringBuilder texte = new StringBuilder("Groupe [" + style() + "]");
        for (Figure enfant : enfants) {
            texte.append('\n').append(enfant.dessiner().indent(2).stripTrailing());
        }
        return texte.toString();
    }
}
