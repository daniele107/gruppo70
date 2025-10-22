package gui;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
/**
 * Collezione di componenti UI moderni e avanzati
 * Implementa Material Design 3 e Fluent UI principles
 */
public final class ModernComponents {
    // Font constants
    private static final String FONT_UI = "Segoe UI";
    /**
     * Private constructor to prevent instantiation of utility class
     */
    private ModernComponents() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    // ===================== MODERN BUTTON =====================
    public static class ModernButton extends JButton {
        private Color hoverColor;
        private Color pressedColor;
        private boolean isAnimating = false;
        private float animationProgress = 0f;
        private Timer animationTimer;
        public ModernButton(String text) {
            super(text);
            init();
        }
        private void init() {
            setFont(DesignSystem.LABEL_LARGE);
            setForeground(Color.WHITE);
            setBackground(DesignSystem.PRIMARY_500);
            setBorder(new DesignSystem.RoundedBorder(DesignSystem.RADIUS_LG));
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            hoverColor = DesignSystem.PRIMARY_600;
            pressedColor = DesignSystem.PRIMARY_700;
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    startHoverAnimation(true);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    startHoverAnimation(false);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    setBackground(pressedColor);
                    repaint();
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    setBackground(isAnimating ? hoverColor : getBackground());
                    repaint();
                }
            });
        }
        private void startHoverAnimation(boolean entering) {
            if (animationTimer != null) {
                animationTimer.stop();
            }
            isAnimating = true;
            final Color startColor = getBackground();
            final Color endColor = entering ? hoverColor : DesignSystem.PRIMARY_500;
            animationTimer = new Timer(16, e -> {
                animationProgress += entering ? 0.1f : -0.1f;
                animationProgress = Math.max(0f, Math.min(1f, animationProgress));
                Color currentColor = interpolateColor(startColor, endColor, animationProgress);
                setBackground(currentColor);
                repaint();
                if ((entering && animationProgress >= 1f) || (!entering && animationProgress <= 0f)) {
                    animationTimer.stop();
                    isAnimating = false;
                }
            });
            animationTimer.start();
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Paint background with rounded corners
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), DesignSystem.RADIUS_LG, DesignSystem.RADIUS_LG);
            // Paint text
            g2.setColor(getForeground());
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(getText())) / 2;
            int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(getText(), textX, textY);
            g2.dispose();
        }
        private Color interpolateColor(Color start, Color end, float progress) {
            int r = (int) (start.getRed() + progress * (end.getRed() - start.getRed()));
            int g = (int) (start.getGreen() + progress * (end.getGreen() - start.getGreen()));
            int b = (int) (start.getBlue() + progress * (end.getBlue() - start.getBlue()));
            return new Color(r, g, b);
        }
    }
    // ===================== MODERN TEXT FIELD =====================
    public static class ModernTextField extends JTextField {
        private String placeholder;
        private boolean showPlaceholder = true;
        private Color focusColor = DesignSystem.PRIMARY_500;
        private boolean isFocused = false;
        private String iconText = null;
        private JLabel iconLabel;
        public ModernTextField(String placeholder) {
            this.placeholder = placeholder;
            init();
        }
        public ModernTextField(String placeholder, String iconText) {
            this.placeholder = placeholder;
            this.iconText = iconText;
            init();
        }
        private void init() {
            setFont(new Font(FONT_UI, Font.PLAIN, 16)); // Font più leggibile
            setBackground(new Color(255, 255, 255)); // Sfondo bianco puro
            setForeground(new Color(33, 37, 41)); // Grigio scuro per massima leggibilità
            setBorder(new ModernTextFieldBorder());
            setOpaque(true); // Rende opaco per migliore visibilità del testo
            setCaretColor(new Color(33, 37, 41)); // Cursore scuro visibile
            // Dimensioni più generose per migliore usabilità
            setPreferredSize(new Dimension(320, 50));
            setMinimumSize(new Dimension(280, 45));
            setMaximumSize(new Dimension(400, 55));
            // Add icon if provided
            if (iconText != null) {
                setLayout(new BorderLayout());
                iconLabel = new JLabel(iconText);
                iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                iconLabel.setForeground(DesignSystem.getTextSecondary());
                iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 15));
                add(iconLabel, BorderLayout.WEST);
            }
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    isFocused = true;
                    if (showPlaceholder && getText().equals(placeholder)) {
                        setText("");
                        showPlaceholder = false;
                        setForeground(new Color(33, 37, 41)); // Testo scuro leggibile quando si digita
                    }
                    if (iconLabel != null) {
                        iconLabel.setForeground(focusColor);
                    }
                    repaint();
                }
                @Override
                public void focusLost(FocusEvent e) {
                    isFocused = false;
                    if (getText().isEmpty()) {
                        showPlaceholder = true;
                        setText(placeholder);
                        setForeground(new Color(156, 163, 175)); // Grigio più chiaro per placeholder
                    }
                    if (iconLabel != null) {
                        iconLabel.setForeground(DesignSystem.getTextSecondary());
                    }
                    repaint();
                }
            });
            // Imposta placeholder iniziale con colore leggibile
            setText(placeholder);
            setForeground(new Color(156, 163, 175)); // Grigio più chiaro per placeholder
        }
        private class ModernTextFieldBorder extends AbstractBorder {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                // Ombra leggera per profondità
                if (!isFocused) {
                    g2.setColor(new Color(0, 0, 0, 10));
                    g2.fillRoundRect(x + 1, y + 2, width - 2, height - 2, 12, 12);
                }
                // Border principale con colori più visibili
                Color borderColor = isFocused ? new Color(52, 152, 219) : new Color(209, 213, 219);
                int thickness = isFocused ? 2 : 1;
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(thickness));
                g2.drawRoundRect(x, y, width - 1, height - 1, 12, 12);
                g2.dispose();
            }
            @Override
            public Insets getBorderInsets(Component c) {
                int leftInset = (iconText != null) ? 80 : 16;
                // Padding più generoso per migliore leggibilità
                return new Insets(14, leftInset, 14, 16);
            }
        }
        public String getPlaceholder() {
            return placeholder;
        }
        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
            if (showPlaceholder) {
                setText(placeholder);
            }
        }
        @Override
        public String getText() {
            return showPlaceholder ? "" : super.getText();
        }
        public String getDisplayText() {
            return super.getText();
        }
    }
    // ===================== MODERN PASSWORD FIELD =====================
    public static class ModernPasswordField extends JPasswordField {
        private String placeholder;
        private boolean showPlaceholder = true;
        private Color focusColor = DesignSystem.PRIMARY_500;
        private boolean isFocused = false;
        private boolean isPasswordVisible = false;
        private JButton toggleButton;
        public ModernPasswordField(String placeholder) {
            this.placeholder = placeholder;
            init();
        }
        private void init() {
            setFont(new Font(FONT_UI, Font.PLAIN, 16)); // Font più leggibile
            setBackground(new Color(255, 255, 255)); // Sfondo bianco puro
            setForeground(new Color(33, 37, 41)); // Grigio scuro per massima leggibilità
            setBorder(new ModernPasswordFieldBorder());
            setOpaque(true); // Rende opaco per migliore visibilità del testo
            setEchoChar('\u25CF'); // Bullet più visibile per password
            setCaretColor(new Color(33, 37, 41)); // Cursore scuro visibile
            // Dimensioni più generose per migliore usabilità
            setPreferredSize(new Dimension(320, 50));
            setMinimumSize(new Dimension(280, 45));
            setMaximumSize(new Dimension(400, 55));
            // Create toggle button for password visibility
            toggleButton = new JButton("Show");
            toggleButton.setFont(new Font(FONT_UI, Font.PLAIN, 12));
            toggleButton.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            toggleButton.setBackground(new Color(0, 0, 0, 0));
            toggleButton.setFocusPainted(false);
            toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            toggleButton.addActionListener(e -> togglePasswordVisibility());
            setLayout(new BorderLayout());
            add(toggleButton, BorderLayout.EAST);
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    isFocused = true;
                    if (showPlaceholder && new String(getPassword()).equals(placeholder)) {
                        setText("");
                        showPlaceholder = false;
                        setForeground(Color.BLACK); // Black text when typing
                        setEchoChar('\u25CF');
                    }
                    repaint();
                }
                @Override
                public void focusLost(FocusEvent e) {
                    isFocused = false;
                    if (getPassword().length == 0) {
                        showPlaceholder = true;
                        setText(placeholder);
                        setForeground(new Color(156, 163, 175)); // Grigio più chiaro per placeholder
                        setEchoChar((char) 0); // Mostra testo placeholder
                    }
                    repaint();
                }
            });
            // Imposta placeholder iniziale con colore leggibile
            setText(placeholder);
            setForeground(new Color(156, 163, 175)); // Grigio più chiaro per placeholder
            setEchoChar((char) 0); // Mostra placeholder inizialmente
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
        private class ModernPasswordFieldBorder extends AbstractBorder {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color borderColor = isFocused ? focusColor : DesignSystem.getBorderLight();
                int thickness = isFocused ? 2 : 1;
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(thickness));
                g2.drawRoundRect(x, y, width - 1, height - 1, DesignSystem.RADIUS_LG, DesignSystem.RADIUS_LG);
                g2.dispose();
            }
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(DesignSystem.SPACE_3XL, DesignSystem.SPACE_4XL, 
                                DesignSystem.SPACE_3XL, 90); // Extra space for toggle button
            }
        }
        public String getPlaceholder() {
            return placeholder;
        }
        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
            if (showPlaceholder) {
                setText(placeholder);
            }
        }
        @Override
        public char[] getPassword() {
            return showPlaceholder ? new char[0] : super.getPassword();
        }
        public String getPasswordText() {
            return new String(getPassword());
        }
    }
    // ===================== MODERN CARD =====================
    public static class ModernCard extends JPanel {
        private int elevation = 2;
        private Color shadowColor = DesignSystem.Shadows.SHADOW_COLOR;
        public ModernCard() {
            init();
        }
        public ModernCard(LayoutManager layout) {
            super(layout);
            init();
        }
        private void init() {
            setBackground(DesignSystem.getSurfaceElevated());
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(DesignSystem.SPACE_LG, DesignSystem.SPACE_LG,
                                                    DesignSystem.SPACE_LG, DesignSystem.SPACE_LG));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Paint shadow
            for (int i = 0; i < elevation; i++) {
                g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), 
                                    shadowColor.getBlue(), (int)(shadowColor.getAlpha() * 0.3f)));
                g2.fillRoundRect(i, i, getWidth() - 2*i, getHeight() - 2*i, 
                               DesignSystem.RADIUS_LG, DesignSystem.RADIUS_LG);
            }
            // Paint background
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), DesignSystem.RADIUS_LG, DesignSystem.RADIUS_LG);
            g2.dispose();
            super.paintComponent(g);
        }
        public void setElevation(int elevation) {
            this.elevation = elevation;
            repaint();
        }
        public int getElevation() {
            return elevation;
        }
    }
    // ===================== MODERN SCROLL PANE =====================
    public static class ModernScrollPane extends JScrollPane {
        public ModernScrollPane(Component view) {
            super(view);
            init();
        }
        private void init() {
            setBackground(DesignSystem.getBackgroundPrimary());
            setBorder(BorderFactory.createEmptyBorder());
            setOpaque(false);
            getViewport().setOpaque(false);
            // Custom scrollbar UI
            getVerticalScrollBar().setUI(new ModernScrollBarUI());
            getHorizontalScrollBar().setUI(new ModernScrollBarUI());
            // Smooth scrolling
            getVerticalScrollBar().setUnitIncrement(16);
            getHorizontalScrollBar().setUnitIncrement(16);
        }
        private class ModernScrollBarUI extends BasicScrollBarUI {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = DesignSystem.NEUTRAL_400;
                trackColor = DesignSystem.NEUTRAL_100;
                thumbHighlightColor = DesignSystem.NEUTRAL_500;
                thumbLightShadowColor = DesignSystem.NEUTRAL_300;
                thumbDarkShadowColor = DesignSystem.NEUTRAL_600;
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createInvisibleButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createInvisibleButton();
            }
            private JButton createInvisibleButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2,
                               thumbBounds.width - 4, thumbBounds.height - 4, 8, 8);
                g2.dispose();
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                // Don't paint track for cleaner look
            }
        }
    }
    // ===================== MODERN PROGRESS BAR =====================
    public static class ModernProgressBar extends JProgressBar {
        private Color progressColor = DesignSystem.PRIMARY_500;
        private Color backgroundColor = DesignSystem.NEUTRAL_200;
        public ModernProgressBar() {
            init();
        }
        public ModernProgressBar(int min, int max) {
            super(min, max);
            init();
        }
        private void init() {
            setStringPainted(false);
            setBorderPainted(false);
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            int progressWidth = (int) ((double) getValue() / getMaximum() * width);
            // Paint background
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, width, height, height, height);
            // Paint progress
            if (progressWidth > 0) {
                g2.setColor(progressColor);
                g2.fillRoundRect(0, 0, progressWidth, height, height, height);
            }
            g2.dispose();
        }
        public void setProgressColor(Color color) {
            this.progressColor = color;
            repaint();
        }
        public void setBackgroundColor(Color color) {
            this.backgroundColor = color;
            repaint();
        }
    }
    // ===================== MODERN COMBO BOX =====================
    public static class ModernComboBox<T> extends JComboBox<T> {
        public ModernComboBox(T[] items) {
            super(items);
            initializeStyle();
        }
        private void initializeStyle() {
            setFont(DesignSystem.BODY_MEDIUM);
            setBackground(DesignSystem.getBackgroundSecondary());
            setForeground(DesignSystem.getTextPrimary());
            setBorder(new DesignSystem.RoundedBorder(DesignSystem.RADIUS_LG));
            setOpaque(false);
            // Custom renderer for modern look
            setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (isSelected) {
                        setBackground(DesignSystem.PRIMARY_500);
                        setForeground(Color.WHITE);
                    } else {
                        setBackground(DesignSystem.getBackgroundSecondary());
                        setForeground(DesignSystem.getTextPrimary());
                    }
                    setBorder(BorderFactory.createEmptyBorder(DesignSystem.SPACE_SM, 
                                                            DesignSystem.SPACE_MD,
                                                            DesignSystem.SPACE_SM, 
                                                            DesignSystem.SPACE_MD));
                    return this;
                }
            });
        }
    }
    // ===================== TOAST NOTIFICATION =====================
    public static class ToastNotification extends JWindow {
        private static final int TOAST_DURATION = 3000;
        public enum ToastType {
            SUCCESS(DesignSystem.SUCCESS_500),
            WARNING(DesignSystem.WARNING_500),
            ERROR(DesignSystem.ERROR_500),
            INFO(DesignSystem.INFO_500);
            private final Color color;
            ToastType(Color color) {
                this.color = color;
            }
            public Color getColor() {
                return color;
            }
        }
        public ToastNotification(Component parent, String message, ToastType type) {
            setAlwaysOnTop(true);
            JPanel content = new JPanel(new BorderLayout());
            content.setBackground(type.getColor());
            content.setBorder(BorderFactory.createEmptyBorder(DesignSystem.SPACE_LG, 
                                                            DesignSystem.SPACE_2XL,
                                                            DesignSystem.SPACE_LG, 
                                                            DesignSystem.SPACE_2XL));
            JLabel label = new JLabel(message);
            label.setFont(DesignSystem.BODY_MEDIUM);
            label.setForeground(Color.WHITE);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            content.add(label, BorderLayout.CENTER);
            add(content);
            pack();
            // Position toast
            if (parent != null) {
                Point parentLocation = parent.getLocationOnScreen();
                Dimension parentSize = parent.getSize();
                setLocation(parentLocation.x + (parentSize.width - getWidth()) / 2,
                           parentLocation.y + 50);
            }
            // Show with animation
            showWithAnimation();
            // Auto-hide after duration
            Timer hideTimer = new Timer(TOAST_DURATION, e -> hideWithAnimation());
            hideTimer.setRepeats(false);
            hideTimer.start();
        }
        private void showWithAnimation() {
            setOpacity(0f);
            setVisible(true);
            Timer fadeIn = new Timer(16, new ActionListener() {
                float opacity = 0f;
                @Override
                public void actionPerformed(ActionEvent e) {
                    opacity += 0.05f;
                    if (opacity >= 1f) {
                        opacity = 1f;
                        ((Timer) e.getSource()).stop();
                    }
                    setOpacity(opacity);
                }
            });
            fadeIn.start();
        }
        private void hideWithAnimation() {
            Timer fadeOut = new Timer(16, new ActionListener() {
                float opacity = 1f;
                @Override
                public void actionPerformed(ActionEvent e) {
                    opacity -= 0.05f;
                    if (opacity <= 0f) {
                        opacity = 0f;
                        setVisible(false);
                        dispose();
                        ((Timer) e.getSource()).stop();
                    }
                    setOpacity(opacity);
                }
            });
            fadeOut.start();
        }
    }
    // ===================== UTILITY METHODS =====================
    public static void showToast(Component parent, String message, ToastNotification.ToastType type) {
        SwingUtilities.invokeLater(() -> new ToastNotification(parent, message, type));
    }
}
