package net.zoungrana.model;

import net.zoungrana.dessin.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DessinTest {
    private final Point origine = new Point(0, 0);

    @Test
    void calculeLesMesuresDesFigures() {
        var cercle = new Cercle(origine, 3);
        var rectangle = new Rectangle(origine, 4, 5);

        assertEquals(9 * Math.PI, cercle.surface(), 1e-9);
        assertEquals(6 * Math.PI, cercle.perimetre(), 1e-9);
        assertEquals(20, rectangle.surface());
        assertEquals(18, rectangle.perimetre());
        assertThrows(IllegalArgumentException.class, () -> new Cercle(origine, -1));
        assertThrows(IllegalArgumentException.class, () -> new Rectangle(origine, 1, Double.NaN));
    }

    @Test
    void traiteLesGroupesImbriquesEtChangeDeStrategie() {
        var rond = new Cercle(origine, 2);
        var groupeInterne = new Groupe();
        groupeInterne.ajouter(rond);
        var groupe = new Groupe();
        groupe.ajouter(groupeInterne);
        assertThrows(IllegalArgumentException.class, () -> groupeInterne.ajouter(groupe));
        assertTrue(groupe.dessiner().contains("\n    Cercle"));

        var dessin = new Dessin();
        dessin.ajouter(groupe);
        var rectangle = new Rectangle(origine, 2, 3);
        dessin.ajouter(rectangle);
        assertEquals(4 * Math.PI + 6, dessin.traiter(), 1e-9);
        dessin.definirTraitement(new SommePerimetres());
        assertEquals(4 * Math.PI + 10, dessin.traiter(), 1e-9);
        assertTrue(dessin.supprimer(rectangle));
        assertFalse(dessin.supprimer(rectangle));
        assertEquals(4 * Math.PI, dessin.traiter(), 1e-9);
    }

    @Test
    void appliqueLeStyleAuxDescendantsEtAuxNouveauxEnfants() {
        var dessin = new Dessin();
        var groupe = new Groupe();
        var cercle = new Cercle(origine, 2);
        groupe.ajouter(cercle);
        dessin.ajouter(groupe);

        var style = new Style(2, 0x34445A, 0x94A3B8);
        dessin.parametrage().definirStyle(style);
        var rectangle = new Rectangle(origine, 2, 3);
        groupe.ajouter(rectangle);
        assertEquals(style, groupe.style());
        assertEquals(style, cercle.style());
        assertEquals(style, rectangle.style());

        dessin.supprimer(groupe);
        dessin.parametrage().definirStyle(Style.DEFAUT);
        assertEquals(style, cercle.style());
    }

    @Test
    void conserveFiguresStrategieEtAbonnementsApresSerialisation(@TempDir Path dossier)
            throws Exception {
        var dessin = new Dessin();
        var groupe = new Groupe();
        groupe.ajouter(new Rectangle(origine, 2, 3));
        dessin.ajouter(groupe);
        dessin.definirTraitement(new SommePerimetres());
        var fichier = dossier.resolve("dessin.bin");

        dessin.serialiser(fichier);
        try (var entree = new ObjectInputStream(Files.newInputStream(fichier))) {
            var copie = (Dessin) entree.readObject();
            assertEquals(10, copie.traiter());
            var style = new Style(4, 0, 0);
            copie.parametrage().definirStyle(style);
            copie.definirTraitement(figures -> {
                var copieGroupe = (Groupe) figures.getFirst();
                assertEquals(style, copieGroupe.enfants().getFirst().style());
                return 0;
            });
            copie.traiter();
        }
    }
}
