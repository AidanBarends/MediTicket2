package za.ac.cput.gui;

import javax.swing.*;
import java.awt.*;

public class SidebarPanel extends JPanel {

    public SidebarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0xF1, 0xF3, 0xF5));
        setPreferredSize(new Dimension(220, 0));

        add(Box.createVerticalStrut(16));
        add(new NavItem("🏠", "Dashboard", false));
        add(new NavItem("🧑‍⚕️", "Staff", false));
        add(new NavItem("🩺", "Patients", true));
        add(new NavItem("📅", "Appointments", false));
        add(new NavItem("🎫", "Tickets", false));
        add(new NavItem("💳", "Payments", false));
        add(new NavItem("📨", "Employee Onboarding", false));
        add(new NavItem("🔔", "Notifications", false));
        add(new NavItem("👤", "Profile", false));

        add(Box.createVerticalGlue());
        add(new NavItem("🚪", "Logout", false));
        add(Box.createVerticalStrut(16));
    }
}