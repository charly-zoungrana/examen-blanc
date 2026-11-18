package net.zoungrana;

import net.zoungrana.dessin.*;
import net.zoungrana.interfacegraphique.FenetreDessin;
import net.zoungrana.technique.SecuriteAspect;
import net.zoungrana.technique.Session;
import org.aspectj.lang.Aspects;

import javax.swing.SwingUtilities;
import java.io.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Scanner;

public final class Main {
    private Main() { }

    public static void main(String[] args) throws IOException {
        if (!Aspects.hasAspect(SecuriteAspect.class)) {
            throw new IllegalStateException("Démarrer la démonstration avec l’agent AspectJ.");
        }
        var dessin = new Dessin();
        if (!Session.connecter("editeur", "demo-edition".toCharArray())) {
            throw new IllegalStateException("Impossible d'initialiser la démonstration.");
        }
        try {
            var groupe = new Groupe();
            groupe.ajouter(new Rectangle(new Point(35, 30), 110, 74));
            var sousGroupe = new Groupe();
            sousGroupe.ajouter(new Cercle(new Point(210, 80), 37));
            groupe.ajouter(sousGroupe);
            dessin.ajouter(groupe);
        } finally {
            Session.deconnecter();
        }

        if (args.length == 1 && "--graphique".equals(args[0])) {
            SwingUtilities.invokeLater(() -> FenetreDessin.ouvrir(dessin));
            return;
        }

        Console console = System.console();
        String identifiant;
        char[] motDePasse;
        if (console != null) {
            identifiant = console.readLine("Identifiant : ");
            if (identifiant == null) {
                return;
            }
            motDePasse = console.readPassword("Mot de passe : ");
            if (motDePasse == null) {
                return;
            }
        } else {
            var entree = new Scanner(System.in);
            System.out.print("Identifiant : ");
            if (!entree.hasNextLine()) {
                return;
            }
            identifiant = entree.nextLine();
            System.out.print("Mot de passe : ");
            if (!entree.hasNextLine()) {
                return;
            }
            motDePasse = entree.nextLine().toCharArray();
        }

        if (!Session.connecter(identifiant, motDePasse)) {
            System.out.println("Identifiants invalides.");
            return;
        }
        try {
            dessin.afficher();
            System.out.printf(Locale.ROOT, "Surface totale : %.2f%n", dessin.traiter());
            var fichier = Path.of("target", "dessin.bin");
            Files.createDirectories(fichier.getParent());
            dessin.serialiser(fichier);
            System.out.println("Dessin enregistré dans " + fichier);
        } catch (SecurityException refus) {
            System.out.println(refus.getMessage());
        } finally {
            Session.deconnecter();
        }
    }
}
