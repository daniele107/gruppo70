package gui;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
/**
 * Campo di testo ULTRAFIX che GARANTISCE la visibilità del testo
 * Nessun override complesso, solo funzionalità base che FUNZIONA
 */
public class FixedTextField extends JTextField {
    private String placeholder;
    private boolean isPlaceholder = false;
    // Colori FISSI che funzionano sempre
    private static final Color TEXT_COLOR = new Color(0, 0, 0); // NERO ASSOLUTO
    private static final Color PLACEHOLDER_COLOR = new Color(128, 128, 128); // GRIGIO STANDARD
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255); // BIANCO ASSOLUTO
    private static final Color BORDER_COLOR = new Color(200, 200, 200); // GRIGIO BORDO
    private static final Color FOCUS_BORDER_COLOR = new Color(0, 120, 215); // BLU WINDOWS
    public FixedTextField(String placeholder) {
        super();
        this.placeholder = placeholder;
        setupTextField();
    }
    private void setupTextField() {
        // Font SEMPLICE e GRANDE
        setFont(new Font("Arial", Font.PLAIN, 16));
        // Colori FISSI
        setBackground(BACKGROUND_COLOR);
        setForeground(TEXT_COLOR);
        setCaretColor(TEXT_COLOR);
        setOpaque(true);
        // Dimensioni
        setPreferredSize(new Dimension(350, 45));
        setMinimumSize(new Dimension(300, 40));
        // Border semplice
        setBorder(new SimpleBorder(false));
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
                if (getText().trim().isEmpty()) {
                    showPlaceholder();
                } else {
                    // FORZA il colore nero anche quando perde focus
                    setForeground(TEXT_COLOR);
                }
                setBorder(new SimpleBorder(false));
            }
        });
    }
    private void showPlaceholder() {
        isPlaceholder = true;
        super.setText(placeholder);
        setForeground(PLACEHOLDER_COLOR);
    }
    private void clearPlaceholder() {
        isPlaceholder = false;
        super.setText("");
        setForeground(TEXT_COLOR);
    }
    @Override
    public String getText() {
        return isPlaceholder ? "" : super.getText();
    }
    @Override
    public void setText(String text) {
        if (text == null || text.trim().isEmpty()) {
            showPlaceholder();
        } else {
            isPlaceholder = false;
            setForeground(TEXT_COLOR);
            super.setText(text);
        }
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
        // EXTRA: Disegna il testo manualmente se necessario
        if (!isPlaceholder && !getText().isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                               RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(TEXT_COLOR);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = getInsets().left;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            // Disegna il testo in NERO ASSOLUTO
            g2.drawString(super.getText(), x, y);
            g2.dispose();
        }
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
            return new Insets(8, 12, 8, 12);
        }
    }
}
