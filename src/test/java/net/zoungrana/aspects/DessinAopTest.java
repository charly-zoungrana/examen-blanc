package net.zoungrana.aspects;

import net.zoungrana.dessin.Cercle;
import net.zoungrana.dessin.Dessin;
import net.zoungrana.dessin.Point;
import net.zoungrana.technique.JournalAspect;
import net.zoungrana.technique.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class DessinAopTest {
    @AfterEach
    void fermeLaSession() {
        Session.deconnecter();
    }

    @Test
    void exigeUneAuthentificationEtEffaceUneSessionApresUnEchec() {
        var dessin = new Dessin();
        assertThrows(SecurityException.class, dessin::traiter);

        char[] motDePasse = "demo-lecture".toCharArray();
        assertTrue(Session.connecter("lecteur", motDePasse));
        assertArrayEquals(new char[motDePasse.length], motDePasse);
        assertEquals(0, dessin.traiter());

        assertFalse(Session.connecter("lecteur", "incorrect".toCharArray()));
        assertThrows(SecurityException.class, dessin::traiter);
    }

    @Test
    void distingueLesDroitsDeLectureEtEdition(@TempDir Path dossier) throws Exception {
        var dessin = new Dessin();
        var cercle = new Cercle(new Point(0, 0), 2);
        var fichier = dossier.resolve("dessin.bin");

        assertTrue(Session.connecter("lecteur", "demo-lecture".toCharArray()));
        assertDoesNotThrow(dessin::afficher);
        assertThrows(SecurityException.class, () -> dessin.ajouter(cercle));
        assertThrows(SecurityException.class, () -> dessin.serialiser(fichier));
        assertFalse(Files.exists(fichier));

        assertTrue(Session.connecter("editeur", "demo-edition".toCharArray()));
        dessin.ajouter(cercle);
        assertEquals(4 * Math.PI, dessin.traiter(), 1e-9);
        dessin.serialiser(fichier);
        assertTrue(Files.size(fichier) > 0);
    }

    @Test
    void journaliseEntreeEtSortieMemeSiLAppelEstRefuse() {
        var logger = Logger.getLogger(JournalAspect.class.getName());
        List<String> messages = new ArrayList<>();
        var collecteur = new Handler() {
            @Override
            public void publish(LogRecord record) {
                messages.add(record.getMessage());
            }

            @Override
            public void flush() { }

            @Override
            public void close() { }
        };
        logger.addHandler(collecteur);
        try {
            var dessin = new Dessin();
            assertThrows(SecurityException.class, dessin::traiter);
            assertTrue(Session.connecter("lecteur", "demo-lecture".toCharArray()));
            assertEquals(0, dessin.traiter());
        } finally {
            logger.removeHandler(collecteur);
        }

        assertEquals(2, messages.stream()
                .filter(message -> message.startsWith("Entrée  Dessin.traiter(")).count());
        assertEquals(2, messages.stream()
                .filter(message -> message.matches("Sortie  Dessin\\.traiter\\(\\).*\\(\\d+\\.\\d{3} ms\\)"))
                .count());
        assertTrue(messages.stream().noneMatch(message -> message.contains("demo-lecture")));
    }
}
