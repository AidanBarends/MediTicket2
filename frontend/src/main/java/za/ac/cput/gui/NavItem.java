package za.ac.cput.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NavItem extends JPanel {

    private static final Color ACTIVE_TEXT = Color.WHITE;
    private static final Color INACTIVE_TEXT = UITheme.TEXT_DARK;
    private static final Color HOVER_BG = new Color(0xE4, 0xE8, 0xEC);

    private Color currentBg;

    public NavItem(String icon, String label, boolean active) {
        this.currentBg = active ? UITheme.PRIMARY_TEAL : UITheme.SIDEBAR_WHITE;

        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 12));
        setMaximumSize(new Dimension(196, 44));
        setPreferredSize(new Dimension(196, 44));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 14));
        textLabel.setForeground(active ? ACTIVE_TEXT : INACTIVE_TEXT);

        add(iconLabel);
        add(textLabel);

        if (!active) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    currentBg = HOVER_BG;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    currentBg = UITheme.SIDEBAR_WHITE;
                    repaint();
                }
            });
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(currentBg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
}