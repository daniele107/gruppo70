// File: Main.java
package model;

import controller.Controller;
import gui.MainMenuGUI;    // importa la tua home
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Carica/persiste lo stato come prima...
        Controller ctrl = Controller.loadState();
        Runtime.getRuntime().addShutdownHook(new Thread(ctrl::saveState));

        // Avvia la home invece del login
        SwingUtilities.invokeLater(() -> new MainMenuGUI(ctrl));
    }
}
