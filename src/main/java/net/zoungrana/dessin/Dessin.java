package net.zoungrana.dessin;

import net.zoungrana.technique.ExigeRole;
import net.zoungrana.technique.Role;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Dessin implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Figure> figures = new ArrayList<>();
    private final Parametrage parametrage = new Parametrage();
    private TraitementDessin traitement = new SommeSurfaces();

    @ExigeRole(Role.EDITION)
    public void ajouter(Figure figure) {
        parametrage.abonner(figure);
        figures.add(figure);
    }

    @ExigeRole(Role.EDITION)
    public boolean supprimer(Figure figure) {
        boolean retiree = figures.remove(figure);
        if (retiree && !figures.contains(figure)) {
            parametrage.desabonner(figure);
        }
        return retiree;
    }

    @ExigeRole(Role.EDITION)
    public Parametrage parametrage() {
        return parametrage;
    }

    @ExigeRole(Role.EDITION)
    public void definirTraitement(TraitementDessin traitement) {
        this.traitement = Objects.requireNonNull(traitement);
    }

    @ExigeRole(Role.LECTURE)
    public double traiter() {
        return traitement.traiter(List.copyOf(figures));
    }

    @ExigeRole(Role.LECTURE)
    public void afficher() {
        for (Figure figure : figures) {
            System.out.println(figure.dessiner());
        }
    }

    @ExigeRole(Role.EDITION)
    public void serialiser(Path fichier) throws IOException {
        try (var sortie = new ObjectOutputStream(Files.newOutputStream(fichier))) {
            sortie.writeObject(this);
        }
    }
}


