import javax.swing.*;
import java.net.URL;

public class Accueil extends JFrame {
    public Accueil() {
        // Charger l'image depuis le dossier 'ressources'
        URL url = getClass().getClassLoader().getResource("ressources/bg.jpg");

        // Vérifier si l'image a été trouvée
        if (url != null) {
            ImageIcon imageIcon = new ImageIcon(url);
            JLabel backgroundLabel = new JLabel(imageIcon);
            setContentPane(backgroundLabel);
        } else {
            System.out.println("Image non trouvée!");
        }

        // Paramétrer la fenêtre
        setTitle("Page d'accueil");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Centre la fenêtre
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Accueil accueil = new Accueil();
            accueil.setVisible(true);
        });
    }
}
