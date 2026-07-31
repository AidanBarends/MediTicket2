package za.ac.cput.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TopBarPanel extends JPanel {

    public TopBarPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(0, 70));
        setBorder(new EmptyBorder(0, 24, 0, 24));

        JLabel logo = new JLabel("MediTicket2");
        logo.setFont(new Font("Serif", Font.BOLD, 20));
        logo.setForeground(UITheme.PRIMARY_TEAL);
        add(logo, BorderLayout.WEST);

        JTextField searchField = new JTextField("Search medical records...");
        searchField.setForeground(UITheme.TEXT_MUTED);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        searchField.setPreferredSize(new Dimension(420, 40));

        JPanel searchWrapper = new JPanel();
        searchWrapper.setOpaque(false);
        searchWrapper.add(searchField);
        add(searchWrapper, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        rightPanel.setOpaque(false);

        JLabel bell = new JLabel("\uD83D\uDD14");
        bell.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JLabel userName = new JLabel("Joshua Sterling");
        userName.setFont(new Font("SansSerif", Font.BOLD, 13));
        userName.setForeground(UITheme.TEXT_DARK);

        rightPanel.add(bell);
        rightPanel.add(userName);
        add(rightPanel, BorderLayout.EAST);
    }
}