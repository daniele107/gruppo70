package gui;
import javax.swing.*;
import java.awt.*;
/**
 * TextField ultra-semplificato per testare la visibilità del testo
 */
public class SimpleTextField extends JTextField {
    public SimpleTextField() {
        init();
    }
    public SimpleTextField(String text) {
        super(text);
        init();
    }
    private void init() {
        // Impostazioni BASIC per massima compatibilità
        setFont(new Font("Arial", Font.PLAIN, 16));
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        setCaretColor(Color.RED); // Rosso per essere SICURI di vederlo
        setOpaque(true);
        // Border semplice
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        // Dimensioni fisse
        setPreferredSize(new Dimension(300, 40));
        setMinimumSize(new Dimension(300, 40));
        setMaximumSize(new Dimension(300, 40));
        // Test immediato con testo predefinito
        setText("SCRIVI QUI");
        selectAll(); // Seleziona tutto per facilitare la sovrascrittura
    }
}
