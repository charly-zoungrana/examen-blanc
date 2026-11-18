package net.zoungrana.interfacegraphique;

import net.zoungrana.dessin.*;
import net.zoungrana.technique.Role;
import net.zoungrana.technique.Session;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public final class FenetreDessin {
    private static final Color ARDOISE = new Color(0x263548);
    private static final Color FOND = new Color(0xF1F5F9);
    private static final Style STYLE_BLEU = new Style(3, 0x365D80, 0xA9C3D8);

    private FenetreDessin() { }

    public static void ouvrir(Dessin dessin) {
        var identifiant = new JTextField(16);
        var motDePasse = new JPasswordField(16);
        var formulaire = new JPanel(new GridLayout(0, 1, 0, 4));
        formulaire.add(new JLabel("Identifiant"));
        formulaire.add(identifiant);
        formulaire.add(new JLabel("Mot de passe"));
        formulaire.add(motDePasse);

        while (JOptionPane.showConfirmDialog(null, formulaire, "Connexion",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            boolean connecte = Session.connecter(identifiant.getText(), motDePasse.getPassword());
            motDePasse.setText("");
            if (connecte) {
                afficher(dessin, Session.possede(Role.EDITION));
                return;
            }
            JOptionPane.showMessageDialog(null, "Identifiants invalides.", "Connexion",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void afficher(Dessin dessin, boolean edition) {
        var fenetre = new JFrame("Atelier de dessin");
        fenetre.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contenu = new JPanel(new BorderLayout(0, 12));
        contenu.setBackground(FOND);
        contenu.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        fenetre.setContentPane(contenu);

        var titre = new JLabel("Atelier de dessin  ·  " + (edition ? "Édition" : "Lecture"));
        titre.setForeground(ARDOISE);
        titre.setFont(titre.getFont().deriveFont(Font.BOLD, 20f));
        contenu.add(titre, BorderLayout.NORTH);

        var toile = new Toile(dessin.figures());
        contenu.add(toile, BorderLayout.CENTER);

        var commandes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        commandes.setOpaque(false);
        var mesure = new JLabel();
        var choixPerimetre = new JToggleButton("Périmètre");
        var tailleBouton = choixPerimetre.getPreferredSize();
        choixPerimetre.setPreferredSize(new Dimension(tailleBouton.width + 20, tailleBouton.height));
        choixPerimetre.setEnabled(edition);
        choixPerimetre.addActionListener(e -> {
            boolean perimetre = choixPerimetre.isSelected();
            dessin.definirTraitement(perimetre ? new SommePerimetres() : new SommeSurfaces());
            afficherMesure(dessin, mesure, perimetre);
        });
        var style = new JButton("Changer le style");
        style.setEnabled(edition);
        style.addActionListener(e -> {
            var parametrage = dessin.parametrage();
            parametrage.definirStyle(parametrage.style().equals(STYLE_BLEU) ? Style.DEFAUT : STYLE_BLEU);
            toile.repaint();
        });
        var enregistrer = new JButton("Enregistrer");
        enregistrer.setEnabled(edition);
        enregistrer.addActionListener(e -> enregistrer(dessin, fenetre));
        commandes.add(choixPerimetre);
        commandes.add(style);
        commandes.add(enregistrer);
        commandes.add(mesure);
        contenu.add(commandes, BorderLayout.SOUTH);

        afficherMesure(dessin, mesure, false);
        fenetre.pack();
        fenetre.setMinimumSize(fenetre.getSize());
        fenetre.setLocationRelativeTo(null);
        fenetre.setVisible(true);
    }

    private static void afficherMesure(Dessin dessin, JLabel mesure, boolean perimetre) {
        String libelle = perimetre ? "Périmètre total" : "Surface totale";
        mesure.setText(String.format(Locale.ROOT, "%s : %.2f", libelle, dessin.traiter()));
    }

    private static void enregistrer(Dessin dessin, JFrame fenetre) {
        var chemin = Path.of("target", "dessin.bin");
        try {
            Files.createDirectories(chemin.getParent());
            dessin.serialiser(chemin);
            JOptionPane.showMessageDialog(fenetre, "Dessin enregistré dans " + chemin);
        } catch (IOException erreur) {
            JOptionPane.showMessageDialog(fenetre, erreur.getMessage(), "Échec de l’enregistrement",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static final class Toile extends JPanel {
        private final List<Figure> figures;

        Toile(List<Figure> figures) {
            this.figures = figures;
            setPreferredSize(new Dimension(535, 285));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(0xCBD5E1)));
        }

        @Override
        protected void paintComponent(Graphics contexte) {
            super.paintComponent(contexte);
            var pinceau = (Graphics2D) contexte.create();
            try {
                pinceau.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                pinceau.translate(25, 20);
                pinceau.scale(1.75, 1.75);
                for (Figure figure : figures) {
                    peindre(pinceau, figure);
                }
            } finally {
                pinceau.dispose();
            }
        }

        private void peindre(Graphics2D g, Figure figure) {
            Shape forme;
            switch (figure) {
                case Cercle cercle -> {
                    double r = cercle.rayon();
                    var centre = cercle.centre();
                    forme = new Ellipse2D.Double(centre.x() - r, centre.y() - r, 2 * r, 2 * r);
                }
                case Rectangle rectangle -> {
                    var coin = rectangle.coinSuperieurGauche();
                    forme = new Rectangle2D.Double(coin.x(), coin.y(), rectangle.largeur(), rectangle.hauteur());
                }
                case Groupe groupe -> {
                    for (Figure enfant : groupe.enfants()) {
                        peindre(g, enfant);
                    }
                    return;
                }
                default -> throw new IllegalArgumentException("Figure non prise en charge.");
            }
            var style = figure.style();
            g.setColor(new Color(style.couleurRemplissage()));
            g.fill(forme);
            if (style.epaisseur() > 0) {
                g.setColor(new Color(style.couleurContour()));
                g.setStroke(new BasicStroke(style.epaisseur()));
                g.draw(forme);
            }
        }
    }
}

