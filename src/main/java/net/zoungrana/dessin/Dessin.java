package net.zoungrana.dessin;

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

    public void ajouter(Figure figure) {
        parametrage.abonner(figure);
        figures.add(figure);
    }

    public boolean supprimer(Figure figure) {
        boolean retiree = figures.remove(figure);
        if (retiree && !figures.contains(figure)) {
            parametrage.desabonner(figure);
        }
        return retiree;
    }

    public Parametrage parametrage() {
        return parametrage;
    }

    public void definirTraitement(TraitementDessin traitement) {
        this.traitement = Objects.requireNonNull(traitement);
    }

    public double traiter() {
        return traitement.traiter(List.copyOf(figures));
    }

    public void afficher() {
        for (Figure figure : figures) {
            System.out.println(figure.dessiner());
        }
    }

    public void serialiser(Path fichier) throws IOException {
        try (var sortie = new ObjectOutputStream(Files.newOutputStream(fichier))) {
            sortie.writeObject(this);
        }
    }
}

