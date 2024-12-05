import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class morpion extends JFrame {
    private JButton[][] boutons;
    private boolean tourJoueurX;
    private JLabel etiquetteStatut;
    private boolean modeIA;
    private JButton boutonModeJeu;

    // Chemin de l'image de fond pour la page d'accueil
    private static final String IMAGE_PATH = "src/images/bg.jpg"; // Assurez-vous de mettre l'image dans ce dossier

    // Panneau pour la page d'accueil
    private JPanel panneauAccueil;

    public morpion() {
        setTitle("Morpion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600); // Nouvelle taille de fenêtre
        setResizable(false); // Empêche le redimensionnement
        setLocationRelativeTo(null); // Centrer la fenêtre

        // Page d'accueil avec un fond d'écran
        panneauAccueil = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Charger et dessiner l'image de fond
                ImageIcon background = new ImageIcon(IMAGE_PATH);
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Bouton "Commencer le jeu"
        JButton boutonCommencer = new JButton("Commencer le jeu");
        boutonCommencer.setFont(new Font("Arial", Font.BOLD, 20));
        boutonCommencer.setFocusPainted(false);
        boutonCommencer.setForeground(Color.BLACK);
        boutonCommencer.setBackground(new Color(255, 255, 255, 128)); // Blanc avec 50% de transparence
        boutonCommencer.setOpaque(true); // Rendre le fond visible
        boutonCommencer.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Bordure noire pour le bouton
        boutonCommencer.setPreferredSize(new Dimension(200, 50));
        boutonCommencer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commencerJeu();
            }
        });

        // Ajouter le bouton au panneau d'accueil
        panneauAccueil.setLayout(new BorderLayout());
        panneauAccueil.add(boutonCommencer, BorderLayout.SOUTH);
        add(panneauAccueil); // Afficher la page d'accueil

        setVisible(true);
    }

    // Méthode pour commencer le jeu en remplaçant la page d'accueil par la grille de jeu
    private void commencerJeu() {
        panneauAccueil.setVisible(false); // Masquer la page d'accueil
        JPanel panneauJeu = new JPanel(new GridLayout(3, 3));
        boutons = new JButton[3][3];
        tourJoueurX = true;
        modeIA = false;

        // Création des boutons de la grille
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boutons[i][j] = new JButton("");
                boutons[i][j].setFont(new Font("Arial", Font.BOLD, 60));
                final int ligne = i;
                final int colonne = j;
                boutons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        boutonClique(ligne, colonne);
                    }
                });
                panneauJeu.add(boutons[i][j]);
            }
        }

        // Étiquette pour afficher le statut du jeu
        etiquetteStatut = new JLabel("Au tour du joueur X");
        etiquetteStatut.setHorizontalAlignment(JLabel.CENTER);
        etiquetteStatut.setFont(new Font("Arial", Font.BOLD, 20));

        // Panneau pour les boutons de contrôle
        JPanel panneauControle = new JPanel(new FlowLayout());

        // Bouton pour recommencer
        JButton boutonRecommencer = new JButton("Nouvelle partie");
        boutonRecommencer.addActionListener(e -> nouvellePartie());

        // Bouton pour changer le mode de jeu
        boutonModeJeu = new JButton("Mode: 2 Joueurs");
        boutonModeJeu.addActionListener(e -> changerMode());

        panneauControle.add(boutonRecommencer);
        panneauControle.add(boutonModeJeu);

        add(etiquetteStatut, BorderLayout.NORTH);
        add(panneauJeu, BorderLayout.CENTER);
        add(panneauControle, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void changerMode() {
        modeIA = !modeIA;
        boutonModeJeu.setText("Mode: " + (modeIA ? "Contre IA" : "2 Joueurs"));
        nouvellePartie();  // Redémarre la partie avec le nouveau mode
    }

    private void boutonClique(int ligne, int colonne) {
        if (boutons[ligne][colonne].getText().equals("")) {
            // Tour du joueur
            if (tourJoueurX) {
                boutons[ligne][colonne].setText("X");
                boutons[ligne][colonne].setForeground(Color.RED); // X en rouge
                etiquetteStatut.setText("Au tour du joueur O");

                if (verifierVictoire("X")) {
                    etiquetteStatut.setText("Le joueur X a gagné!");
                    desactiverBoutons();
                    return;
                } else if (grillePleine()) {
                    etiquetteStatut.setText("Match nul!");
                    return;
                }

                tourJoueurX = !tourJoueurX;

                // Tour de l'IA si le mode IA est activé
                if (modeIA && !tourJoueurX) {
                    SwingUtilities.invokeLater(() -> {
                        jouerIA();
                    });
                }
            } else if (!modeIA) {
                boutons[ligne][colonne].setText("O");
                boutons[ligne][colonne].setForeground(Color.BLUE); // O en bleu
                etiquetteStatut.setText("Au tour du joueur X");

                if (verifierVictoire("O")) {
                    etiquetteStatut.setText("Le joueur O a gagné!");
                    desactiverBoutons();
                    return;
                } else if (grillePleine()) {
                    etiquetteStatut.setText("Match nul!");
                    return;
                }

                tourJoueurX = !tourJoueurX;
            }
        }
    }

    private void jouerIA() {
        int[] meilleurCoup = trouverMeilleurCoup();
        if (meilleurCoup != null) {
            boutons[meilleurCoup[0]][meilleurCoup[1]].setText("O");
            boutons[meilleurCoup[0]][meilleurCoup[1]].setForeground(Color.BLUE); // O en bleu
            if (verifierVictoire("O")) {
                etiquetteStatut.setText("L'IA a gagné!");
                desactiverBoutons();
            } else if (grillePleine()) {
                etiquetteStatut.setText("Match nul!");
            } else {
                etiquetteStatut.setText("Au tour du joueur X");
                tourJoueurX = true;
            }
        }
    }

    private int[] trouverMeilleurCoup() {
        int meilleurScore = Integer.MIN_VALUE;
        int[] meilleurCoup = null;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (boutons[i][j].getText().equals("")) {
                    boutons[i][j].setText("O");
                    int score = minimax(0, false);
                    boutons[i][j].setText("");

                    if (score > meilleurScore) {
                        meilleurScore = score;
                        meilleurCoup = new int[]{i, j};
                    }
                }
            }
        }
        return meilleurCoup;
    }

    private int minimax(int profondeur, boolean estMax) {
        if (verifierVictoire("X")) {
            return -1;
        }
        if (verifierVictoire("O")) {
            return 1;
        }
        if (grillePleine()) {
            return 0;
        }

        if (estMax) {
            int meilleurScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (boutons[i][j].getText().equals("")) {
                        boutons[i][j].setText("O");
                        meilleurScore = Math.max(meilleurScore, minimax(profondeur + 1, false));
                        boutons[i][j].setText("");
                    }
                }
            }
            return meilleurScore;
        } else {
            int meilleurScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (boutons[i][j].getText().equals("")) {
                        boutons[i][j].setText("X");
                        meilleurScore = Math.min(meilleurScore, minimax(profondeur + 1, true));
                        boutons[i][j].setText("");
                    }
                }
            }
            return meilleurScore;
        }
    }

    private boolean verifierVictoire(String joueur) {
        // Vérifier les lignes, les colonnes et les diagonales
        for (int i = 0; i < 3; i++) {
            if ((boutons[i][0].getText().equals(joueur) && boutons[i][1].getText().equals(joueur) && boutons[i][2].getText().equals(joueur)) ||
                (boutons[0][i].getText().equals(joueur) && boutons[1][i].getText().equals(joueur) && boutons[2][i].getText().equals(joueur))) {
                return true;
            }
        }
        if ((boutons[0][0].getText().equals(joueur) && boutons[1][1].getText().equals(joueur) && boutons[2][2].getText().equals(joueur)) ||
            (boutons[0][2].getText().equals(joueur) && boutons[1][1].getText().equals(joueur) && boutons[2][0].getText().equals(joueur))) {
            return true;
        }
        return false;
    }

    private boolean grillePleine() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (boutons[i][j].getText().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void nouvellePartie() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boutons[i][j].setText("");
                boutons[i][j].setEnabled(true);
            }
        }
        etiquetteStatut.setText("Au tour du joueur X");
        tourJoueurX = true;
    }

    private void desactiverBoutons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boutons[i][j].setEnabled(false);
            }
        }
    }

    public static void main(String[] args) {
        new morpion();
    }
}
