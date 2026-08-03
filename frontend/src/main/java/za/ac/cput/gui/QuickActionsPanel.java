package za.ac.cput.gui;

import javax.swing.*;
import java.awt.*;

public class QuickActionsPanel extends JPanel {

    public QuickActionsPanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        PillButton appointmentBtn = new PillButton(
                "\uD83D\uDCC5", "Appointment", UITheme.PRIMARY_TEAL, Color.WHITE, false
        );
        appointmentBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Navigate to Appointments (coming soon)")
        );

        PillButton ticketsBtn = new PillButton(
                "\uD83C\uDFAB", "Tickets", Color.WHITE, UITheme.TEXT_DARK, true
        );
        ticketsBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Navigate to Tickets (coming soon)")
        );

        PillButton paymentsBtn = new PillButton(
                "\uD83D\uDCB3", "Payments", UITheme.ACCENT_TERRACOTTA, Color.WHITE, false
        );
        paymentsBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Navigate to Payments (coming soon)")
        );

        add(appointmentBtn);
        add(ticketsBtn);
        add(paymentsBtn);
    }
}