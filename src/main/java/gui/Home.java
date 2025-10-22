package gui;
import controller.Controller;
import database.ConnectionManager;
import database.DataSourceFactory;
import javax.swing.*;
import static javax.swing.WindowConstants.*;
public class Home {
    private JPanel mainPanel;
    public static void main(String[] args) {
        JFrame frameHome = new JFrame("Home");
        frameHome.setContentPane(new Home().mainPanel);
        frameHome.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frameHome.pack();
        frameHome.setVisible(true);
    }
    public Home() {
        ConnectionManager connectionManager = new ConnectionManager(DataSourceFactory.createDataSource());
        new Controller(connectionManager);
        // Add action listeners or other initialization code here
    }
}
