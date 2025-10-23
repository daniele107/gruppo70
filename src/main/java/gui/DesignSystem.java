package gui;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
/**
 * Design System completo per l'applicazione Hackathon Manager
 * Implementa un design moderno, consistente e professionale
 * Ispirato a Material Design 3 e Fluent UI
 */
public final class DesignSystem {
    private DesignSystem() {
        throw new UnsupportedOperationException("Classe di utilità");
    }
    // ===================== COLOR PALETTE =====================
    // Primary Colors - Gradient blu moderno
    public static final Color PRIMARY_50 = new Color(239, 246, 255);
    public static final Color PRIMARY_100 = new Color(219, 234, 254);
    public static final Color PRIMARY_200 = new Color(191, 219, 254);
    public static final Color PRIMARY_300 = new Color(147, 197, 253);
    public static final Color PRIMARY_400 = new Color(96, 165, 250);
    public static final Color PRIMARY_500 = new Color(59, 130, 246);  // Main primary
    public static final Color PRIMARY_600 = new Color(37, 99, 235);
    public static final Color PRIMARY_700 = new Color(29, 78, 216);
    public static final Color PRIMARY_800 = new Color(30, 64, 175);
    public static final Color PRIMARY_900 = new Color(30, 58, 138);
    // Secondary Colors - Verde elegante
    public static final Color SECONDARY_50 = new Color(236, 253, 245);
    public static final Color SECONDARY_100 = new Color(209, 250, 229);
    public static final Color SECONDARY_200 = new Color(167, 243, 208);
    public static final Color SECONDARY_300 = new Color(110, 231, 183);
    public static final Color SECONDARY_400 = new Color(52, 211, 153);
    public static final Color SECONDARY_500 = new Color(16, 185, 129);  // Main secondary
    public static final Color SECONDARY_600 = new Color(5, 150, 105);
    public static final Color SECONDARY_700 = new Color(4, 120, 87);
    public static final Color SECONDARY_800 = new Color(6, 95, 70);
    public static final Color SECONDARY_900 = new Color(6, 78, 59);
    // Accent Colors - Viola moderno
    public static final Color ACCENT_50 = new Color(250, 245, 255);
    public static final Color ACCENT_100 = new Color(243, 232, 255);
    public static final Color ACCENT_200 = new Color(233, 213, 255);
    public static final Color ACCENT_300 = new Color(196, 181, 253);
    public static final Color ACCENT_400 = new Color(167, 139, 250);
    public static final Color ACCENT_500 = new Color(139, 92, 246);   // Main accent
    public static final Color ACCENT_600 = new Color(124, 58, 237);
    public static final Color ACCENT_700 = new Color(109, 40, 217);
    public static final Color ACCENT_800 = new Color(91, 33, 182);
    public static final Color ACCENT_900 = new Color(76, 29, 149);
    // Neutral Colors - Grigi moderni
    public static final Color NEUTRAL_50 = new Color(249, 250, 251);
    public static final Color NEUTRAL_100 = new Color(243, 244, 246);
    public static final Color NEUTRAL_200 = new Color(229, 231, 235);
    public static final Color NEUTRAL_300 = new Color(209, 213, 219);
    public static final Color NEUTRAL_400 = new Color(156, 163, 175);
    public static final Color NEUTRAL_500 = new Color(107, 114, 128);
    public static final Color NEUTRAL_600 = new Color(75, 85, 99);
    public static final Color NEUTRAL_700 = new Color(55, 65, 81);
    public static final Color NEUTRAL_800 = new Color(31, 41, 55);
    public static final Color NEUTRAL_900 = new Color(17, 24, 39);
    // Semantic Colors
    public static final Color SUCCESS_50 = new Color(240, 253, 244);
    public static final Color SUCCESS_500 = new Color(34, 197, 94);
    public static final Color SUCCESS_600 = new Color(22, 163, 74);
    public static final Color WARNING_50 = new Color(255, 251, 235);
    public static final Color WARNING_500 = new Color(245, 158, 11);
    public static final Color WARNING_600 = new Color(217, 119, 6);
    public static final Color ERROR_50 = new Color(254, 242, 242);
    public static final Color ERROR_500 = new Color(239, 68, 68);
    public static final Color ERROR_600 = new Color(220, 38, 38);
    public static final Color INFO_50 = new Color(239, 246, 255);
    public static final Color INFO_500 = new Color(59, 130, 246);
    public static final Color INFO_600 = new Color(37, 99, 235);
    // ===================== THEME SYSTEM =====================
    public enum Theme {
        LIGHT, DARK
    }
    private static Theme currentTheme = Theme.DARK;
    public static void setTheme(Theme theme) {
        currentTheme = theme;
    }
    public static Theme getCurrentTheme() {
        return currentTheme;
    }
    // Dynamic colors based on theme
    public static Color getBackgroundPrimary() {
        return currentTheme == Theme.LIGHT ? NEUTRAL_50 : NEUTRAL_900;
    }
    public static Color getBackgroundSecondary() {
        return currentTheme == Theme.LIGHT ? Color.WHITE : NEUTRAL_800;
    }
    public static Color getTextPrimary() {
        return currentTheme == Theme.LIGHT ? NEUTRAL_900 : NEUTRAL_50;
    }
    public static Color getTextSecondary() {
        return currentTheme == Theme.LIGHT ? NEUTRAL_600 : NEUTRAL_400;
    }
    public static Color getBorderLight() {
        return currentTheme == Theme.LIGHT ? NEUTRAL_200 : NEUTRAL_700;
    }
    public static Color getSurfaceElevated() {
        return currentTheme == Theme.LIGHT ? Color.WHITE : NEUTRAL_800;
    }
    // ===================== TYPOGRAPHY =====================
    private static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_FAMILY_BASE = new Font(FONT_FAMILY, Font.PLAIN, 14);
    // Display fonts
    public static final Font DISPLAY_LARGE = new Font(FONT_FAMILY, Font.BOLD, 57);
    public static final Font DISPLAY_MEDIUM = new Font(FONT_FAMILY, Font.BOLD, 45);
    public static final Font DISPLAY_SMALL = new Font(FONT_FAMILY, Font.BOLD, 36);
    // Headline fonts
    public static final Font HEADLINE_LARGE = new Font(FONT_FAMILY, Font.BOLD, 32);
    public static final Font HEADLINE_MEDIUM = new Font(FONT_FAMILY, Font.BOLD, 28);
    public static final Font HEADLINE_SMALL = new Font(FONT_FAMILY, Font.BOLD, 24);
    // Title fonts
    public static final Font TITLE_LARGE = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font TITLE_MEDIUM = new Font(FONT_FAMILY, Font.BOLD, 16);
    public static final Font TITLE_SMALL = new Font(FONT_FAMILY, Font.BOLD, 14);
    // Body fonts
    public static final Font BODY_LARGE = new Font(FONT_FAMILY, Font.PLAIN, 16);
    public static final Font BODY_MEDIUM = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font BODY_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    // Label fonts
    public static final Font LABEL_LARGE = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font LABEL_MEDIUM = new Font(FONT_FAMILY, Font.BOLD, 12);
    public static final Font LABEL_SMALL = new Font(FONT_FAMILY, Font.BOLD, 11);
    // ===================== SPACING SYSTEM =====================
    public static final int SPACE_NONE = 0;
    public static final int SPACE_XS = 4;
    public static final int SPACE_SM = 8;
    public static final int SPACE_MD = 12;
    public static final int SPACE_LG = 16;
    public static final int SPACE_XL = 20;
    public static final int SPACE_2XL = 24;
    public static final int SPACE_3XL = 32;
    public static final int SPACE_4XL = 40;
    public static final int SPACE_5XL = 48;
    public static final int SPACE_6XL = 64;
    // ===================== BORDER RADIUS =====================
    public static final int RADIUS_NONE = 0;
    public static final int RADIUS_SM = 4;
    public static final int RADIUS_MD = 6;
    public static final int RADIUS_LG = 8;
    public static final int RADIUS_XL = 12;
    public static final int RADIUS_2XL = 16;
    public static final int RADIUS_3XL = 24;
    public static final int RADIUS_FULL = 9999;
    // ===================== SHADOWS =====================
    public static class Shadows {
        private Shadows() {
            throw new UnsupportedOperationException("Classe di utilità");
        }
        public static final Color SHADOW_COLOR = new Color(0, 0, 0, 0.1f);
        public static final Color SHADOW_COLOR_DARK = new Color(0, 0, 0, 0.25f);
        // Elevation levels
        public static final int ELEVATION_0 = 0;
        public static final int ELEVATION_1 = 1;
        public static final int ELEVATION_2 = 2;
        public static final int ELEVATION_3 = 4;
        public static final int ELEVATION_4 = 6;
        public static final int ELEVATION_5 = 8;
    }
    // ===================== COMPONENT BUILDERS =====================
    /**
     * Crea un bottone moderno con stile Material Design
     */
    public static JButton createButton(String text, ButtonStyle style) {
        JButton button = new JButton(text);
        applyButtonStyle(button, style);
        return button;
    }
    public enum ButtonStyle {
        PRIMARY_FILLED,
        PRIMARY_OUTLINED,
        PRIMARY_TEXT,
        SECONDARY_FILLED,
        SECONDARY_OUTLINED,
        SUCCESS_FILLED,
        WARNING_FILLED,
        ERROR_FILLED
    }
    private static void applyButtonStyle(JButton button, ButtonStyle style) {
        button.setFont(LABEL_LARGE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switch (style) {
            case PRIMARY_FILLED:
                button.setBackground(PRIMARY_500);
                button.setForeground(Color.BLACK);
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
                break;
            case PRIMARY_OUTLINED:
                button.setBackground(new Color(0, 0, 0, 0));
                button.setForeground(PRIMARY_500);
                button.setBorder(new RoundedBorder(RADIUS_LG, PRIMARY_500));
                break;
            case PRIMARY_TEXT:
                button.setBackground(new Color(0, 0, 0, 0));
                button.setForeground(PRIMARY_500);
                button.setBorder(BorderFactory.createEmptyBorder(SPACE_MD, SPACE_LG, SPACE_MD, SPACE_LG));
                break;
            case SECONDARY_OUTLINED:
                button.setBackground(new Color(0, 0, 0, 0));
                button.setForeground(SECONDARY_500);
                button.setBorder(new RoundedBorder(RADIUS_LG, SECONDARY_500));
                break;
            case SECONDARY_FILLED:
                button.setBackground(SECONDARY_500);
                button.setForeground(Color.BLACK);
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
                break;
            case SUCCESS_FILLED:
                button.setBackground(SUCCESS_500);
                button.setForeground(Color.BLACK);
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
                break;
            case WARNING_FILLED:
                button.setBackground(WARNING_500);
                button.setForeground(Color.BLACK);
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
                break;
            case ERROR_FILLED:
                button.setBackground(ERROR_500);
                button.setForeground(Color.BLACK);
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
                break;
        }
        // Add hover effects
        AnimationUtils.addHoverShadowEffect(button);
    }
    /**
     * Crea un campo di input moderno con visibilità migliorata
     */
    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font(FONT_FAMILY, Font.PLAIN, 16)); // Font più leggibile
        field.setBorder(new RoundedBorder(RADIUS_LG, getBorderLight()));
        field.setBackground(new Color(255, 255, 255)); // Sfondo bianco puro
        field.setForeground(new Color(33, 37, 41)); // Testo scuro per massima leggibilità
        field.setCaretColor(new Color(33, 37, 41)); // Cursore scuro visibile
        field.setOpaque(true); // Rende opaco per migliore contrasto
        // Aggiunge funzionalità placeholder con colori migliorati
        if (placeholder != null && !placeholder.isEmpty()) {
            field.setText(placeholder);
            field.setForeground(new Color(156, 163, 175)); // Grigio più chiaro per placeholder
            field.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (field.getText().equals(placeholder)) {
                        field.setText("");
                        field.setForeground(new Color(33, 37, 41)); // Testo scuro quando si digita
                    }
                }
                @Override
                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (field.getText().isEmpty()) {
                        field.setText(placeholder);
                        field.setForeground(new Color(156, 163, 175)); // Grigio più chiaro per placeholder
                    }
                }
            });
        }
        return field;
    }
    /**
     * Crea un pannello card moderno
     */
    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(getSurfaceElevated());
        card.setBorder(new ElevatedBorder(Shadows.ELEVATION_2));
        return card;
    }
    /**
     * Crea un pannello card con contenuto
     */
    public static JPanel createCard(Component content) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout());
        card.add(content, BorderLayout.CENTER);
        return card;
    }
    // ===================== CUSTOM BORDERS =====================
    /**
     * Border con angoli arrotondati
     */
    public static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color borderColor;
        private final int thickness;
        public RoundedBorder(int radius) {
            this(radius, getBorderLight(), 1);
        }
        public RoundedBorder(int radius, Color borderColor) {
            this(radius, borderColor, 1);
        }
        public RoundedBorder(int radius, Color borderColor, int thickness) {
            this.radius = radius;
            this.borderColor = borderColor;
            this.thickness = thickness;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(SPACE_MD, SPACE_LG, SPACE_MD, SPACE_LG);
        }
    }
    /**
     * Border con elevazione (ombra)
     */
    public static class ElevatedBorder extends AbstractBorder {
        private final int elevation;
        public ElevatedBorder(int elevation) {
            this.elevation = elevation;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Draw shadow
            g2.setColor(Shadows.SHADOW_COLOR);
            for (int i = 0; i < elevation; i++) {
                g2.drawRoundRect(x + i, y + i, width - 2*i - 1, height - 2*i - 1, RADIUS_LG, RADIUS_LG);
            }
            // Draw main border
            g2.setColor(getBorderLight());
            g2.drawRoundRect(x, y, width - 1, height - 1, RADIUS_LG, RADIUS_LG);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(SPACE_LG + elevation, SPACE_LG + elevation, 
                            SPACE_LG + elevation, SPACE_LG + elevation);
        }
    }
    // ===================== UTILITY METHODS =====================
    /**
     * Applica ombra a un componente
     */
    public static void applyShadow(JComponent component, int elevation) {
        component.setBorder(new ElevatedBorder(elevation));
    }
    /**
     * Crea un gradiente lineare
     */
    public static Paint createGradient(Color startColor, Color endColor, int height) {
        return new GradientPaint(0, 0, startColor, 0, height, endColor);
    }
    /**
     * Crea un gradiente radiale
     */
    public static Paint createRadialGradient(Color centerColor, Color edgeColor, int centerX, int centerY, int radius) {
        return new RadialGradientPaint(centerX, centerY, radius, 
            new float[]{0f, 1f}, 
            new Color[]{centerColor, edgeColor});
    }
}
