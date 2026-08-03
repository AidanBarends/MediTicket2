package za.ac.cput.gui;

import javax.swing.*;
import java.awt.*;

public class GreetingHeaderPanel extends JPanel {

    public GreetingHeaderPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statusBadge = new StatusPill("PATIENT PORTAL ACTIVE");
        statusBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel headline = new JLabel("Good Evening, Joshua \uD83D\uDC4B");
        headline.setFont(new Font("Serif", Font.BOLD, 32));
        headline.setForeground(UITheme.TEXT_DARK);
        headline.setAlignmentX(Component.LEFT_ALIGNMENT);
        headline.setBorder(BorderFactory.createEmptyBorder(12, 0, 8, 0));

        JLabel subtext = new JLabel(
                "<html><div style='width:420px'>Welcome back to MediTicket. Everything you need to manage " +
                        "your healthcare journey is right here in your clinical command center.</div></html>"
        );
        subtext.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtext.setForeground(UITheme.TEXT_MUTED);
        subtext.setAlignmentX(Component.LEFT_ALIGNMENT);

        QuickActionsPanel quickActions = new QuickActionsPanel();
        quickActions.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(statusBadge);
        add(headline);
        add(subtext);
        add(quickActions);
    }

    private static class StatusPill extends JLabel {
        public StatusPill(String text) {
            super("  ●  " + text + "  ");
            setFont(new Font("SansSerif", Font.BOLD, 11));
            setForeground(UITheme.PRIMARY_TEAL);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0xDD, 0xEE, 0xEE));
            g2.fillRoundRect(0, 0, getPreferredSize().width, getPreferredSize().height, 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}