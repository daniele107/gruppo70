package app;
import gui.ModernMainFrame;
import javax.swing.*;
import java.util.logging.Logger;
import java.util.logging.Level;
/**
 * Classe principale per avviare l'applicazione Hackathon Manager.
 * Ora con design moderno e interfaccia ultra-fluida!
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    /**
     * Metodo principale per avviare l'applicazione
     *
     * @param args argomenti da riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        // Configura un look and feel moderno
        setupModernLookAndFeel();
        // Avvia l'applicazione Swing nell'Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                ModernMainFrame mainFrame = new ModernMainFrame();
                mainFrame.setVisible(true);
                LOGGER.info("🚀 Hackathon Manager avviato con successo!");
                LOGGER.info("✨ Modern UI caricata - Design ultra-fluido attivo!");
            } catch (Exception e) {
                final String errorMsg = "Errore nell'avvio dell'applicazione: " + e.getMessage();
                LOGGER.log(Level.SEVERE, errorMsg, e);
                JOptionPane.showMessageDialog(null, 
                    errorMsg, 
                    "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    /**
     * Configura il look and feel moderno per l'applicazione
     */
    private static void setupModernLookAndFeel() {
        try {
            // Utilizza il look and feel del sistema per una migliore integrazione
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Abilita funzionalità moderne se disponibili
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            System.setProperty("swing.plaf.metal.controlFont", "Segoe UI");
            System.setProperty("swing.plaf.metal.userFont", "Segoe UI");
            // Miglioramenti specifici per macOS
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                System.setProperty("apple.awt.application.name", "Hackathon Manager");
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("apple.awt.application.appearance", "system");
            }
        } catch (Exception e) {
            LOGGER.warning("Impossibile impostare il look and feel: " + e.getMessage());
        }
    }
}
