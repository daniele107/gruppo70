// File: src/main/java/model/Main.java
package model;

import javax.swing.SwingUtilities;
import controller.Controller;
import gui.MainMenuGUI;

/**
 * Punto d'ingresso dell'applicazione.
 */
public class Main {
    public static void main(String[] args) {
        // Carica lo stato dell'applicazione (o ne crea uno nuovo)
        Controller ctrl = Controller.loadState();
        // Aggiunge uno shutdown hook per salvare lo stato alla chiusura
        Runtime.getRuntime().addShutdownHook(new Thread(ctrl::saveState));

        // Avvia la GUI principale sul thread Swing
        SwingUtilities.invokeLater(() -> {
            MainMenuGUI menu = new MainMenuGUI(ctrl);
            menu.setVisible(true);
        });
    }
}
