package gui;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
/**
 * Campo password ULTRAFIX che GARANTISCE la visibilita del testo
 * Nessun override complesso, solo funzionalita base che FUNZIONA
 */
public class FixedPasswordField extends JPasswordField {
    private String placeholder;
    private boolean isPlaceholder = false;
    private JButton toggleButton;
    private boolean isPasswordVisible = false;
    // Colori FISSI che funzionano sempre
    private static final Color TEXT_COLOR = new Color(0, 0, 0); // NERO ASSOLUTO
    private static final Color PLACEHOLDER_COLOR = new Color(128, 128, 128); // GRIGIO STANDARD
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255); // BIANCO ASSOLUTO
    private static final Color BORDER_COLOR = new Color(200, 200, 200); // GRIGIO BORDO
    private static final Color FOCUS_BORDER_COLOR = new Color(0, 120, 215); // BLU WINDOWS
    public FixedPasswordField(String placeholder) {
        super();
        this.placeholder = placeholder;
        setupPasswordField();
    }
    private void setupPasswordField() {
        // Font SEMPLICE e GRANDE
        setFont(new Font("Arial", Font.PLAIN, 16));
        // Colori FISSI
        setBackground(BACKGROUND_COLOR);
        setForeground(TEXT_COLOR);
        setCaretColor(TEXT_COLOR);
        setOpaque(true);
        setEchoChar('*');
        // Dimensioni
        setPreferredSize(new Dimension(350, 45));
        setMinimumSize(new Dimension(300, 40));
        // Layout per il toggle button
        setLayout(new BorderLayout());
        // Border semplice
        setBorder(new SimpleBorder(false));
        // Toggle button
        setupToggleButton();
        // Placeholder SEMPLICE
        showPlaceholder();
        // Focus listener SEMPLICE
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isPlaceholder) {
                    clearPlaceholder();
                }
                setBorder(new SimpleBorder(true));
                // FORZA il colore nero
                setForeground(TEXT_COLOR);
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (getPassword().length == 0) {
                    showPlaceholder();
                } else {
                    // FORZA il colore nero anche quando perde focus
                    setForeground(TEXT_COLOR);
                }
                setBorder(new SimpleBorder(false));
            }
        });
    }
    private void setupToggleButton() {
        toggleButton = new JButton("Show");
        toggleButton.setFont(new Font("Arial", Font.BOLD, 10));
        toggleButton.setForeground(new Color(0, 120, 215));
        toggleButton.setBackground(Color.WHITE);
        toggleButton.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        toggleButton.setFocusPainted(false);
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.addActionListener(e -> togglePasswordVisibility());
        add(toggleButton, BorderLayout.EAST);
    }
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            setEchoChar((char) 0);
            toggleButton.setText("Hide");
        } else {
            setEchoChar('*');
            toggleButton.setText("Show");
        }
    }
    private void showPlaceholder() {
        isPlaceholder = true;
        super.setText(placeholder);
        setForeground(PLACEHOLDER_COLOR);
        setEchoChar((char) 0); // Mostra placeholder senza echo
    }
    private void clearPlaceholder() {
        isPlaceholder = false;
        super.setText("");
        setForeground(TEXT_COLOR);
        if (!isPasswordVisible) {
            setEchoChar('*'); // Ripristina echo per password
        }
    }
    @Override
    public char[] getPassword() {
        return isPlaceholder ? new char[0] : super.getPassword();
    }
    public String getPasswordText() {
        return isPlaceholder ? "" : new String(super.getPassword());
    }
    @Override
    protected void paintComponent(Graphics g) {
        // FORZA SEMPRE il colore giusto prima del paint
        if (isPlaceholder) {
            setForeground(PLACEHOLDER_COLOR);
        } else {
            setForeground(TEXT_COLOR);
        }
        // Paint normale
        super.paintComponent(g);
    }
    /**
     * Border SEMPLICISSIMO che funziona sempre
     */
    private static class SimpleBorder extends AbstractBorder {
        private boolean focused;
        public SimpleBorder(boolean focused) {
            this.focused = focused;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Border color
            Color borderColor = focused ? FOCUS_BORDER_COLOR : BORDER_COLOR;
            int thickness = focused ? 2 : 1;
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, 8, 8);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 50); // Spazio extra a destra per il toggle button
        }
    }
}
