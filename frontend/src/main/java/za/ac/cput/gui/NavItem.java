package za.ac.cput.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NavItem extends JPanel {

    private static final Color ACTIVE_BG = new Color(0x2E, 0x5B, 0xE0);
    private static final Color ACTIVE_TEXT = Color.WHITE;
    private static final Color INACTIVE_TEXT = new Color(0x33, 0x3B, 0x44);
    private static final Color HOVER_BG = new Color(0xE4, 0xE8, 0xEC);

    private final boolean active;

    public NavItem(String icon, String label, boolean active) {
        this.active = active;

        setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
        setMaximumSize(new Dimension(220, 44));
        setPreferredSize(new Dimension(220, 44));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(icon);
        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        add(iconLabel);
        add(textLabel);

        if (active) {
            setBackground(ACTIVE_BG);
            textLabel.setForeground(ACTIVE_TEXT);
        } else {
            setBackground(new Color(0xF1, 0xF3, 0xF5));
            textLabel.setForeground(INACTIVE_TEXT);
        }

        if (!active) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(HOVER_BG);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(new Color(0xF1, 0xF3, 0xF5));
                }
            });
        }
    }
}