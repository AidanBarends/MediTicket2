package za.ac.cput.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("MediTicket - Patient Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        SidebarPanel sidebar = new SidebarPanel();
        add(sidebar, BorderLayout.WEST);

        TopBarPanel topbar = new TopBarPanel();
        add(topbar, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(UITheme.BACKGROUND_GRAY);
        content.setBorder(new EmptyBorder(32, 32, 32, 32));

        GreetingHeaderPanel greeting = new GreetingHeaderPanel();
        content.add(greeting, BorderLayout.NORTH);

        add(content, BorderLayout.CENTER);
    }
}