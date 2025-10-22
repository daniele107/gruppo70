package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * Utility class per animazioni e effetti visivi avanzati
 * Fornisce metodi riutilizzabili per creare animazioni fluide
 */
public final class AnimationUtils {
    /**
     * Costruttore privato per impedire l'istanziazione della classe di utilità
     */
    private AnimationUtils() {
        throw new UnsupportedOperationException("Classe di utilità");
    }
    // Costanti per animazioni
    public static final int ANIMATION_FPS = 60;
    public static final int ANIMATION_DURATION = 300; // millisecondi
    public static final float ANIMATION_STEP = 16.0f / ANIMATION_DURATION; // 16ms per frame
    // Colori per effetti
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);
    public static final Color GLOW_COLOR = new Color(255, 255, 255, 50);
    /**
     * Crea un timer per animazioni fluide
     */
    public static Timer createAnimationTimer(ActionListener action) {
        Timer timer = new Timer(1000 / ANIMATION_FPS, action);
        timer.setRepeats(true);
        return timer;
    }
    /**
     * Crea un effetto di pulsazione (pulse) per un componente
     */
    public static void addPulseEffect(JComponent component) {
        final float[] pulseProgress = {0.0f};
        final boolean[] expanding = {true};
        final Timer[] pulseTimer = new Timer[1];
        pulseTimer[0] = createAnimationTimer(e -> {
            if (expanding[0]) {
                pulseProgress[0] += 0.1f;
                if (pulseProgress[0] >= 1.0f) {
                    expanding[0] = false;
                }
            } else {
                pulseProgress[0] -= 0.1f;
                if (pulseProgress[0] <= 0.0f) {
                    expanding[0] = true;
                }
            }
            // Applica la scala
            float scale = 1.0f + (pulseProgress[0] * 0.05f);
            component.setSize(
                (int)(component.getPreferredSize().width * scale),
                (int)(component.getPreferredSize().height * scale)
            );
            component.revalidate();
            component.repaint();
        });
        pulseTimer[0].start();
    }
    /**
     * Crea un effetto di hover con ombra dinamica
     */
    public static void addHoverShadowEffect(JComponent component) {
        component.addMouseListener(new MouseAdapter() {
            private Timer shadowTimer = null;
            private float shadowIntensity = 0.0f;
            @Override
            public void mouseEntered(MouseEvent e) {
                if (shadowTimer != null) {
                    shadowTimer.stop();
                }
                shadowTimer = createAnimationTimer(evt -> {
                    shadowIntensity += 0.1f;
                    if (shadowIntensity >= 1.0f) {
                        shadowIntensity = 1.0f;
                        shadowTimer.stop();
                    }
                    component.repaint();
                });
                shadowTimer.start();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (shadowTimer != null) {
                    shadowTimer.stop();
                }
                shadowTimer = createAnimationTimer(evt -> {
                    shadowIntensity -= 0.1f;
                    if (shadowIntensity <= 0.0f) {
                        shadowIntensity = 0.0f;
                        shadowTimer.stop();
                    }
                    component.repaint();
                });
                shadowTimer.start();
            }
        });
        // Override del paintComponent per disegnare l'ombra
        component.setOpaque(false);
        component.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                component.repaint();
            }
        });
    }
    /**
     * Crea un effetto di transizione fade per un pannello
     */
    public static void addFadeTransition(JPanel panel, float startAlpha, float endAlpha, int duration) {
        Timer fadeTimer = new Timer(1000 / ANIMATION_FPS, null);
        fadeTimer.setRepeats(true);
        final float[] currentAlpha = {startAlpha};
        final float alphaStep = (endAlpha - startAlpha) / (duration / 16.0f);
        fadeTimer.addActionListener(e -> {
            currentAlpha[0] += alphaStep;
            if ((alphaStep > 0 && currentAlpha[0] >= endAlpha) || 
                (alphaStep < 0 && currentAlpha[0] <= endAlpha)) {
                currentAlpha[0] = endAlpha;
                fadeTimer.stop();
            }
            // Applica l'alpha al pannello
            panel.setOpaque(false);
            panel.repaint();
        });
        fadeTimer.start();
    }
    /**
     * Crea un effetto di slide per un componente
     */
    public static void addSlideEffect(JComponent component, Point startPos, Point endPos, int duration) {
        Timer slideTimer = new Timer(1000 / ANIMATION_FPS, null);
        slideTimer.setRepeats(true);
        final float[] progress = {0.0f};
        final float step = 1.0f / (duration / 16.0f);
        slideTimer.addActionListener(e -> {
            progress[0] += step;
            if (progress[0] >= 1.0f) {
                progress[0] = 1.0f;
                slideTimer.stop();
            }
            // Calcola la posizione interpolata
            int x = (int)(startPos.x + (endPos.x - startPos.x) * progress[0]);
            int y = (int)(startPos.y + (endPos.y - startPos.y) * progress[0]);
            component.setLocation(x, y);
            component.revalidate();
        });
        slideTimer.start();
    }
    /**
     * Crea un effetto di rotazione per un componente
     */
    public static void addRotationEffect(JComponent component, float startAngle, float endAngle, int duration) {
        Timer rotationTimer = new Timer(1000 / ANIMATION_FPS, null);
        rotationTimer.setRepeats(true);
        final float[] currentAngle = {startAngle};
        final float angleStep = (endAngle - startAngle) / (duration / 16.0f);
        rotationTimer.addActionListener(e -> {
            currentAngle[0] += angleStep;
            if ((angleStep > 0 && currentAngle[0] >= endAngle) || 
                (angleStep < 0 && currentAngle[0] <= endAngle)) {
                currentAngle[0] = endAngle;
                rotationTimer.stop();
            }
            // Applica la rotazione
            component.setSize(component.getPreferredSize());
            component.repaint();
        });
        rotationTimer.start();
    }
    /**
     * Crea un effetto di bounce per un bottone
     */
    public static void addBounceEffect(JButton button) {
        button.addActionListener(e -> {
            Timer bounceTimer = new Timer(1000 / ANIMATION_FPS, null);
            bounceTimer.setRepeats(true);
            final float[] bounceProgress = {0.0f};
            final boolean[] bouncing = {true};
            bounceTimer.addActionListener(evt -> {
                if (bouncing[0]) {
                    bounceProgress[0] += 0.2f;
                    if (bounceProgress[0] >= 1.0f) {
                        bouncing[0] = false;
                    }
                } else {
                    bounceProgress[0] -= 0.1f;
                    if (bounceProgress[0] <= 0.0f) {
                        bounceProgress[0] = 0.0f;
                        bounceTimer.stop();
                    }
                }
                // Calcola la scala con effetto bounce
                float scale = 1.0f + (float)Math.sin(bounceProgress[0] * Math.PI) * 0.1f;
                button.setSize(
                    (int)(button.getPreferredSize().width * scale),
                    (int)(button.getPreferredSize().height * scale)
                );
                button.revalidate();
                button.repaint();
            });
            bounceTimer.start();
        });
    }
    /**
     * Crea un effetto di glow per un componente
     */
    public static void addGlowEffect(JComponent component) {
        component.addMouseListener(new MouseAdapter() {
            private Timer glowTimer = null;
            private float glowIntensity = 0.0f;
            @Override
            public void mouseEntered(MouseEvent e) {
                if (glowTimer != null) {
                    glowTimer.stop();
                }
                glowTimer = createAnimationTimer(evt -> {
                    glowIntensity += 0.1f;
                    if (glowIntensity >= 1.0f) {
                        glowIntensity = 1.0f;
                        glowTimer.stop();
                    }
                    component.repaint();
                });
                glowTimer.start();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (glowTimer != null) {
                    glowTimer.stop();
                }
                glowTimer = createAnimationTimer(evt -> {
                    glowIntensity -= 0.1f;
                    if (glowIntensity <= 0.0f) {
                        glowIntensity = 0.0f;
                        glowTimer.stop();
                    }
                    component.repaint();
                });
                glowTimer.start();
            }
        });
    }
    /**
     * Crea un effetto di ripple per un bottone
     */
    public static void addRippleEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            private Timer rippleTimer = null;
            @Override
            public void mousePressed(MouseEvent e) {
                final float[] rippleProgress = {0.0f};
                if (rippleTimer != null) {
                    rippleTimer.stop();
                }
                rippleTimer = createAnimationTimer(evt -> {
                    rippleProgress[0] += 0.1f;
                    if (rippleProgress[0] >= 1.0f) {
                        rippleProgress[0] = 1.0f;
                        rippleTimer.stop();
                    }
                    button.repaint();
                });
                rippleTimer.start();
            }
        });
        // Override del paintComponent per disegnare l'effetto ripple
        button.setOpaque(false);
    }
    /**
     * Crea un effetto di typing per un campo di testo
     */
    public static void addTypingEffect(JTextField textField, String text, int delay) {
        Timer typingTimer = new Timer(1000 / ANIMATION_FPS, null);
        typingTimer.setRepeats(true);
        final int[] currentIndex = {0};
        final int[] frameCount = {0};
        typingTimer.addActionListener(e -> {
            frameCount[0]++;
            if (frameCount[0] >= delay / 16) {
                if (currentIndex[0] < text.length()) {
                    textField.setText(text.substring(0, currentIndex[0] + 1));
                    currentIndex[0]++;
                } else {
                    typingTimer.stop();
                }
                frameCount[0] = 0;
            }
        });
        typingTimer.start();
    }
    /**
     * Crea un effetto di loading spinner
     */
    public static JPanel createLoadingSpinner(int size) {
        final float[] rotationAngle = {0.0f};
        JPanel spinner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                // Abilita antialiasing
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = size / 2;
                // Disegna il cerchio di caricamento
                g2d.setColor(new Color(72, 201, 176)); // Tiffany color
                g2d.setStroke(new BasicStroke(3));
                int startAngle = (int)(rotationAngle[0] * 180 / Math.PI);
                g2d.drawArc(centerX - radius, centerY - radius, 
                           radius * 2, radius * 2, startAngle, 270);
                g2d.dispose();
            }
        };
        // Timer per l'animazione di rotazione
        Timer rotationTimer = new Timer(1000 / ANIMATION_FPS, null);
        rotationTimer.setRepeats(true);
        rotationTimer.addActionListener(e -> {
            rotationAngle[0] += 0.2f;
            spinner.repaint();
        });
        rotationTimer.start();
        spinner.setPreferredSize(new Dimension(size, size));
        spinner.setOpaque(false);
        return spinner;
    }
    /**
     * Crea un effetto di progress bar animata
     */
    public static JProgressBar createAnimatedProgressBar(int min, int max) {
        JProgressBar progressBar = new JProgressBar(min, max);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        progressBar.setForeground(new Color(72, 201, 176)); // Tiffany color
        progressBar.setBackground(new Color(240, 240, 240));
        progressBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        // Animazione del progresso
        Timer progressTimer = new Timer(1000 / ANIMATION_FPS, null);
        progressTimer.setRepeats(true);
        final int[] currentValue = {min};
        progressTimer.addActionListener(e -> {
            if (currentValue[0] < max) {
                currentValue[0]++;
                progressBar.setValue(currentValue[0]);
            } else {
                progressTimer.stop();
            }
        });
        return progressBar;
    }
} 
