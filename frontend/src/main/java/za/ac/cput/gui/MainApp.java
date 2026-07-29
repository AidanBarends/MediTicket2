package za.ac.cput.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MediTicket - Patient Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}