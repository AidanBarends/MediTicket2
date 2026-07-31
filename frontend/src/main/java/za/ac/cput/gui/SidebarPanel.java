package za.ac.cput.GUI;

import javax.swing.*;
import java.awt.*;

public class SidebarPanel extends JPanel {

    public SidebarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UITheme.SIDEBAR_WHITE);
        setPreferredSize(new Dimension(220, 0));

        add(Box.createVerticalStrut(16));
        add(new NavItem("🏠", "Dashboard", true));
        add(new NavItem("📅", "Appointments", false));
        add(new NavItem("🎫", "Tickets", false));
        add(new NavItem("💳", "Payments", false));
        add(new NavItem("🔔", "Notifications", false));
        add(new NavItem("👤", "Profile", false));

        add(Box.createVerticalGlue());
        add(new NavItem("🚪", "Logout", false));
        add(Box.createVerticalStrut(16));
    }
}