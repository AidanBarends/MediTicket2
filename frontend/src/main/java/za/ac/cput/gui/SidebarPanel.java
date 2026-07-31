package za.ac.cput.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SidebarPanel extends JPanel {

    public SidebarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UITheme.SIDEBAR_WHITE);
        setPreferredSize(new Dimension(220, 0));
        setBorder(new EmptyBorder(20, 12, 20, 12));

        add(new NavItem("🏠", "Dashboard", true));
        add(Box.createVerticalStrut(4));
        add(new NavItem("📅", "Appointments", false));
        add(Box.createVerticalStrut(4));
        add(new NavItem("🎫", "Tickets", false));
        add(Box.createVerticalStrut(4));
        add(new NavItem("💳", "Payments", false));
        add(Box.createVerticalStrut(4));
        add(new NavItem("🔔", "Notifications", false));
        add(Box.createVerticalStrut(4));
        add(new NavItem("👤", "Profile", false));

        add(Box.createVerticalGlue());
        add(new NavItem("🚪", "Logout", false));
    }
}