package gui;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
/**
 * Componenti moderni che FUNZIONANO davvero
 * Basati sui SimpleTextField ma con estetica moderna
 */
public class WorkingModernComponents {
    private static final String FONT_UI = "Segoe UI";
    /**
     * Costruttore privato per impedire l'istanziazione
     */
    private WorkingModernComponents() {
        // Utility class - non istanziabile
    }
    // ===================== WORKING MODERN TEXT FIELD =====================
    public static class WorkingModernTextField extends JTextField {
        private String placeholder;
        private boolean isPlaceholderShown = false;
        public WorkingModernTextField(String placeholder) {
            this.placeholder = placeholder;
            init();
        }
        private void init() {
            // Impostazioni BASE che FUNZIONANO con estetica migliorata
            setFont(new Font(FONT_UI, Font.PLAIN, 18)); // Font più grande
            setBackground(Color.WHITE); // Sfondo bianco puro per massimo contrasto
            setForeground(Color.BLACK); // NERO puro per massima visibilità
            setCaretColor(Color.BLACK); // Cursore NERO sempre visibile
            setOpaque(true);
            setSelectedTextColor(Color.WHITE); // Testo selezionato bianco
            setSelectionColor(new Color(52, 152, 219)); // Sfondo selezione blu
            // Border moderno con ombra
            setBorder(new WorkingModernBorder());
            // Dimensioni responsive e eleganti
            setPreferredSize(new Dimension(350, 55));
            setMinimumSize(new Dimension(300, 50));
            setMaximumSize(new Dimension(400, 60));
            // Gestione placeholder SEMPLICE
            showPlaceholder();
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (isPlaceholderShown) {
                        hidePlaceholder();
                    }
                    // Forza il colore nero quando si ha il focus
                    setForeground(Color.BLACK);
                    setBorder(new WorkingModernBorder(true));
                    repaint();
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().trim().isEmpty()) {
                        showPlaceholder();
                    } else {
                        // Mantieni il colore nero quando perde il focus
                        setForeground(Color.BLACK);
                    }
                    setBorder(new WorkingModernBorder(false));
                    repaint();
                }
            });
        }
        private void showPlaceholder() {
            isPlaceholderShown = true;
            super.setText(placeholder);
            setForeground(Color.GRAY); // Grigio standard per placeholder
        }
        private void hidePlaceholder() {
            isPlaceholderShown = false;
            super.setText("");
            setForeground(Color.BLACK); // NERO puro per il testo
        }
        @Override
        public String getText() {
            return isPlaceholderShown ? "" : super.getText();
        }
        @Override
        public void setText(String text) {
            if (text == null || text.trim().isEmpty()) {
                showPlaceholder();
            } else {
                isPlaceholderShown = false;
                setForeground(Color.BLACK); // NERO puro per il testo
                super.setText(text);
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            // Forza il colore del testo per sicurezza
            if (!isPlaceholderShown) {
                setForeground(Color.BLACK);
            }
            super.paintComponent(g);
        }
    }
    // ===================== WORKING MODERN PASSWORD FIELD =====================
    public static class WorkingModernPasswordField extends JPasswordField {
        private String placeholder;
        private boolean isPlaceholderShown = false;
        private JButton toggleButton;
        private boolean isPasswordVisible = false;
        public WorkingModernPasswordField(String placeholder) {
            this.placeholder = placeholder;
            init();
        }
        private void init() {
            // Impostazioni BASE che FUNZIONANO con estetica migliorata
            setFont(new Font(FONT_UI, Font.PLAIN, 18)); // Font più grande
            setBackground(Color.WHITE); // Sfondo bianco puro per massimo contrasto
            setForeground(Color.BLACK); // NERO puro per massima visibilità
            setCaretColor(Color.BLACK); // Cursore NERO sempre visibile
            setOpaque(true);
            setEchoChar('●'); // Bullet elegante
            setSelectedTextColor(Color.WHITE); // Testo selezionato bianco
            setSelectionColor(new Color(52, 152, 219)); // Sfondo selezione blu
            // Border moderno con ombra
            setBorder(new WorkingModernBorder());
            // Dimensioni responsive e eleganti
            setPreferredSize(new Dimension(350, 55));
            setMinimumSize(new Dimension(300, 50));
            setMaximumSize(new Dimension(400, 60));
            // Toggle button semplice
            setupToggleButton();
            // Gestione placeholder SEMPLICE
            showPlaceholder();
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (isPlaceholderShown) {
                        hidePlaceholder();
                    }
                    // Forza il colore nero quando si ha il focus
                    setForeground(Color.BLACK);
                    setBorder(new WorkingModernBorder(true));
                    repaint();
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (getPassword().length == 0) {
                        showPlaceholder();
                    } else {
                        // Mantieni il colore nero quando perde il focus
                        setForeground(Color.BLACK);
                    }
                    setBorder(new WorkingModernBorder(false));
                    repaint();
                }
            });
        }
        private void setupToggleButton() {
            setLayout(new BorderLayout());
            toggleButton = new JButton("Show");
            toggleButton.setFont(new Font(FONT_UI, Font.BOLD, 11));
            toggleButton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            toggleButton.setBackground(new Color(0, 0, 0, 0)); // Trasparente
            toggleButton.setForeground(DesignSystem.PRIMARY_600);
            toggleButton.setFocusPainted(false);
            toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            toggleButton.addActionListener(e -> togglePasswordVisibility());
            // Effetto hover elegante
            toggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    toggleButton.setForeground(DesignSystem.PRIMARY_700);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    toggleButton.setForeground(DesignSystem.PRIMARY_600);
                }
            });
            add(toggleButton, BorderLayout.EAST);
        }
        private void togglePasswordVisibility() {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                setEchoChar((char) 0);
                toggleButton.setText("Hide");
            } else {
                setEchoChar('●');
                toggleButton.setText("Show");
            }
        }
        private void showPlaceholder() {
            isPlaceholderShown = true;
            super.setText(placeholder);
            setForeground(Color.GRAY); // Grigio standard per placeholder
            setEchoChar((char) 0); // Mostra il placeholder
        }
        private void hidePlaceholder() {
            isPlaceholderShown = false;
            super.setText("");
            setForeground(Color.BLACK); // NERO puro per il testo
            setEchoChar('●'); // Torna a nascondere
        }
        @Override
        public char[] getPassword() {
            return isPlaceholderShown ? new char[0] : super.getPassword();
        }
        public String getPasswordText() {
            return isPlaceholderShown ? "" : new String(super.getPassword());
        }
        @Override
        protected void paintComponent(Graphics g) {
            // Forza il colore del testo per sicurezza
            if (!isPlaceholderShown) {
                setForeground(Color.BLACK);
            }
            super.paintComponent(g);
        }
    }
    // ===================== WORKING MODERN BORDER =====================
    private static class WorkingModernBorder extends AbstractBorder {
        private boolean isFocused;
        public WorkingModernBorder() {
            this(false);
        }
        public WorkingModernBorder(boolean focused) {
            this.isFocused = focused;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int radius = 12;
            // Ombra softà (solo se non è focused)
            if (!isFocused) {
                g2.setColor(new Color(0, 0, 0, 15)); // Ombra molto leggera
                g2.fillRoundRect(x + 1, y + 2, width - 2, height - 2, radius, radius);
            }
            // Sfondo principale
            g2.setColor(c.getBackground());
            g2.fillRoundRect(x, y, width - 1, height - 1, radius, radius);
            // Border principale
            if (isFocused) {
                // Gradiente per il focus
                GradientPaint gradient = new GradientPaint(
                    0, 0, DesignSystem.PRIMARY_400,
                    width, 0, DesignSystem.PRIMARY_600
                );
                g2.setPaint(gradient);
                g2.setStroke(new BasicStroke(2.5f));
            } else {
                g2.setColor(new Color(209, 213, 219)); // Grigio elegante
                g2.setStroke(new BasicStroke(1.5f));
            }
            g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);
            // Highlight interno per effetto glass
            if (isFocused) {
                g2.setColor(new Color(255, 255, 255, 40));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(x + 2, y + 2, width - 5, height - 5, radius - 2, radius - 2);
            }
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(14, 18, 14, 18); // Padding più generoso
        }
    }
}
