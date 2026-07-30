package za.ac.cput.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("MediTicket - Patient Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        SidebarPanel sidebar = new SidebarPanel();
        add(sidebar, BorderLayout.WEST);

        JPanel topbar = new JPanel();
        topbar.setBackground(new Color(0x1A, 0x23, 0x32));
        topbar.setPreferredSize(new Dimension(0, 60));
        add(topbar, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        add(content, BorderLayout.CENTER);
    }
}