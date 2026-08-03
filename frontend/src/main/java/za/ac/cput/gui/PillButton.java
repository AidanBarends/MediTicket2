package za.ac.cput.gui;

import javax.swing.*;
import java.awt.*;

public class PillButton extends JButton {

    private final Color baseColor;
    private final Color hoverColor;
    private Color currentColor;

    public PillButton(String icon, String text, Color baseColor, Color textColor, boolean bordered) {
        super("  " + icon + "  " + text + "  ");
        this.baseColor = baseColor;
        this.hoverColor = baseColor.darker();
        this.currentColor = baseColor;

        setFont(new Font("SansSerif", Font.BOLD, 14));
        setForeground(textColor);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(150, 44));

        if (bordered) {
            setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true));
        }

        addChangeListener(e -> {
            currentColor = getModel().isRollover() ? hoverColor : this.baseColor;
            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
}