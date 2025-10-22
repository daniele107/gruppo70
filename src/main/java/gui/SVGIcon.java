package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
/**
 * Classe per creare e renderizzare icone SVG personalizzate
 */
public class SVGIcon {
    /**
     * Costruttore privato per impedire l'istanziazione
     */
    private SVGIcon() {
        // Utility class - non istanziabile
    }
    /**
     * Classe base astratta per tutti i renderer di icone
     */
    private abstract static class BaseIconRenderer implements Icon {
        protected final int size;
        protected BaseIconRenderer(int size) {
            this.size = size;
        }
        @Override
        public int getIconWidth() {
            return size;
        }
        @Override
        public int getIconHeight() {
            return size;
        }
    }
    /**
     * Crea un'icona SVG rappresentativa del coding
     */
    public static JLabel createCodingIcon(int size) {
        return new JLabel(new CodingIconRenderer(size));
    }
    /**
     * Crea un'icona SVG per Navigation (compass)
     */
    public static Icon createNavigationIcon(int size) {
        return new NavigationIconRenderer(size);
    }
    /**
     * Crea un'icona SVG per Eventi (calendar)
     */
    public static Icon createEventiIcon(int size) {
        return new EventiIconRenderer(size);
    }

    /**
     * Crea l'icona principale dell'applicazione ADS
     */
    public static Icon createADSAppIcon(int size) {
        return new ADSAppIconRenderer(size);
    }
    /**
     * Crea un'icona SVG per Profilo Utente (user)
     */
    public static Icon createProfileIcon(int size) {
        return new ProfileIconRenderer(size);
    }
    /**
     * Crea un'icona SVG per Statistiche (chart)
     */
    public static Icon createStatsIcon(int size) {
        return new StatsIconRenderer(size);
    }
    /**
     * Crea un'icona SVG per Impostazioni (gear)
     */
    public static Icon createSettingsIcon(int size) {
        return new SettingsIconRenderer(size);
    }
    /**
     * Renderer personalizzato per l'icona di coding
     */
    private static class CodingIconRenderer extends BaseIconRenderer {
        public CodingIconRenderer(int size) {
            super(size);
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            // Translate to the icon position
            g2.translate(x, y);
            // Scale to fit the desired size
            double scale = size / 24.0; // Base size is 24x24
            g2.scale(scale, scale);
            // Draw the coding icon (monitor with code brackets)
            drawCodingIcon(g2);
            g2.dispose();
        }
        private void drawCodingIcon(Graphics2D g2) {
            // Set stroke for crisp lines
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Monitor frame (outer rectangle)
            g2.setColor(DesignSystem.PRIMARY_600);
            RoundRectangle2D monitor = new RoundRectangle2D.Double(1, 2, 22, 14, 2, 2);
            g2.draw(monitor);
            // Monitor screen (inner rectangle)
            g2.setColor(DesignSystem.NEUTRAL_900);
            RoundRectangle2D screen = new RoundRectangle2D.Double(2.5, 3.5, 19, 11, 1, 1);
            g2.fill(screen);
            // Monitor stand
            g2.setColor(DesignSystem.PRIMARY_600);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(12, 16, 12, 19);
            g2.drawLine(8, 21, 16, 21);
            // Code brackets on screen
            g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(DesignSystem.PRIMARY_500);
            // Left bracket <
            Path2D leftBracket = new Path2D.Double();
            leftBracket.moveTo(8, 6);
            leftBracket.lineTo(6, 9);
            leftBracket.lineTo(8, 12);
            g2.draw(leftBracket);
            // Right bracket >
            Path2D rightBracket = new Path2D.Double();
            rightBracket.moveTo(16, 6);
            rightBracket.lineTo(18, 9);
            rightBracket.lineTo(16, 12);
            g2.draw(rightBracket);
            // Code lines in the middle
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(DesignSystem.SUCCESS_500);
            // First line
            g2.drawLine(10, 7, 14, 7);
            // Second line
            g2.drawLine(9, 9, 15, 9);
            // Third line
            g2.drawLine(10, 11, 13, 11);
            // Add some coding dots/pixels for detail
            g2.setColor(DesignSystem.WARNING_500);
            g2.fillOval(11, 8, 1, 1);
            g2.fillOval(13, 8, 1, 1);
            g2.fillOval(12, 10, 1, 1);
        }
    }
    /**
     * Crea un'icona SVG alternativa più dettagliata
     */
    public static JLabel createAdvancedCodingIcon(int size) {
        return new JLabel(new AdvancedCodingIconRenderer(size));
    }
    /**
     * Crea un'icona SVG per il profilo utente
     */
    public static Icon createUserIcon(int size) {
        return new UserIconRenderer(size);
    }
    /**
     * Crea un'icona SVG per il tema chiaro (sole)
     */
    public static Icon createSunIcon(int size) {
        return new SunIconRenderer(size);
    }
    /**
     * Crea un'icona SVG per il tema scuro (luna)
     */
    public static Icon createMoonIcon(int size) {
        return new MoonIconRenderer(size);
    }
    /**
     * Crea un'icona SVG per le informazioni di versione/info
     */
    public static Icon createInfoIcon(int size) {
        return new InfoIconRenderer(size);
    }
    /**
     * Crea un'icona SVG per lo status/attività del sistema
     */
    public static Icon createStatusIcon(int size) {
        return new StatusIconRenderer(size);
    }
    /**
     * Renderer per icona di coding più dettagliata
     */
    private static class AdvancedCodingIconRenderer extends BaseIconRenderer {
        public AdvancedCodingIconRenderer(int size) {
            super(size);
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);
            double scale = size / 24.0;
            g2.scale(scale, scale);
            drawAdvancedCodingIcon(g2);
            g2.dispose();
        }
        private void drawAdvancedCodingIcon(Graphics2D g2) {
            // Laptop base
            g2.setColor(DesignSystem.PRIMARY_700);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Laptop screen
            RoundRectangle2D screen = new RoundRectangle2D.Double(3, 1, 18, 12, 2, 2);
            g2.fill(screen);
            // Screen border
            g2.setColor(DesignSystem.PRIMARY_800);
            g2.draw(screen);
            // Laptop keyboard base
            Ellipse2D keyboardBase = new Ellipse2D.Double(1, 13, 22, 8);
            g2.setColor(DesignSystem.PRIMARY_600);
            g2.fill(keyboardBase);
            g2.setColor(DesignSystem.PRIMARY_700);
            g2.draw(keyboardBase);
            // Screen content - terminal window
            g2.setColor(DesignSystem.NEUTRAL_900);
            RoundRectangle2D terminal = new RoundRectangle2D.Double(4, 2, 16, 10, 1, 1);
            g2.fill(terminal);
            // Terminal header
            g2.setColor(DesignSystem.NEUTRAL_200);
            RoundRectangle2D terminalHeader = new RoundRectangle2D.Double(4, 2, 16, 2, 1, 1);
            g2.fill(terminalHeader);
            // Terminal buttons
            g2.setColor(new Color(239, 68, 68)); // Red
            g2.fillOval(5, 3, 1, 1);
            g2.setColor(new Color(245, 158, 11)); // Orange
            g2.fillOval(7, 3, 1, 1);
            g2.setColor(new Color(34, 197, 94)); // Green
            g2.fillOval(9, 3, 1, 1);
            // Code lines
            g2.setStroke(new BasicStroke(0.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(DesignSystem.SECONDARY_400);
            g2.drawLine(5, 6, 12, 6);
            g2.setColor(DesignSystem.PRIMARY_400);
            g2.drawLine(6, 8, 14, 8);
            g2.setColor(DesignSystem.ACCENT_400);
            g2.drawLine(5, 10, 10, 10);
            // Cursor
            g2.setColor(DesignSystem.PRIMARY_500);
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(12, 10, 12, 11);
            // Keyboard keys
            g2.setColor(DesignSystem.NEUTRAL_300);
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 3; j++) {
                    g2.fillRoundRect(6 + i * 2, 15 + (int)(j * 1.5), 1, 1, 1, 1);
                }
            }
        }
    }
    /**
     * Renderer per icona utente (profilo)
     */
    private static class UserIconRenderer extends BaseIconRenderer {
        public UserIconRenderer(int size) {
            super(size);
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            double scale = size / 24.0;
            g2.scale(scale, scale);
            // User icon - head and shoulders
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(DesignSystem.PRIMARY_500);
            // Head (circle)
            g2.drawOval(8, 3, 8, 8);
            // Body (arc for shoulders)
            Path2D body = new Path2D.Double();
            body.moveTo(4, 21);
            body.curveTo(4, 16, 8, 13, 12, 13);
            body.curveTo(16, 13, 20, 16, 20, 21);
            g2.draw(body);
            g2.dispose();
        }
    }
    /**
     * Renderer per icona sole (tema chiaro)
     */
    private static class SunIconRenderer extends BaseIconRenderer {
        public SunIconRenderer(int size) {
            super(size);
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            double scale = size / 24.0;
            g2.scale(scale, scale);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(DesignSystem.WARNING_500);
            // Sun center
            g2.fillOval(8, 8, 8, 8);
            // Sun rays
            g2.drawLine(12, 1, 12, 3);   // top
            g2.drawLine(12, 21, 12, 23); // bottom
            g2.drawLine(1, 12, 3, 12);   // left
            g2.drawLine(21, 12, 23, 12); // right
            // Diagonal rays
            g2.drawLine(5, 5, 6, 6);     // top-left
            g2.drawLine(18, 6, 19, 5);   // top-right
            g2.drawLine(5, 19, 6, 18);   // bottom-left
            g2.drawLine(18, 18, 19, 19); // bottom-right
            g2.dispose();
        }
    }
    /**
     * Renderer per icona luna (tema scuro)
     */
    private static class MoonIconRenderer extends BaseIconRenderer {
        public MoonIconRenderer(int size) {
            super(size);
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            double scale = size / 24.0;
            g2.scale(scale, scale);
            g2.setColor(DesignSystem.PRIMARY_600);
            // Moon crescent shape
            Path2D moon = new Path2D.Double();
            moon.moveTo(12, 2);
            moon.curveTo(17, 2, 21, 6.5, 21, 12);
            moon.curveTo(21, 17.5, 17, 22, 12, 22);
            moon.curveTo(15, 22, 17.5, 18.5, 17.5, 12);
            moon.curveTo(17.5, 5.5, 15, 2, 12, 2);
            moon.closePath();
            g2.fill(moon);
            // Add some stars
            g2.setColor(DesignSystem.ACCENT_400);
            g2.fillOval(6, 6, 1, 1);
            g2.fillOval(8, 4, 1, 1);
            g2.fillOval(5, 9, 1, 1);
            g2.dispose();
        }
    }
    /**
     * Renderer per icona informazioni/versione
     */
    private static class InfoIconRenderer extends BaseIconRenderer {
        public InfoIconRenderer(int size) {
            super(size);
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            double scale = size / 24.0;
            g2.scale(scale, scale);
            // Info icon - circle with "i"
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(DesignSystem.SECONDARY_500);
            // Outer circle
            g2.drawOval(2, 2, 20, 20);
            // Inner "i" 
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Dot above "i"
            g2.fillOval(11, 7, 2, 2);
            // Vertical line of "i"
            g2.drawLine(12, 11, 12, 17);
            g2.dispose();
        }
    }
    /**
     * Renderer per icona status/attività sistema
     */
    private static class StatusIconRenderer extends BaseIconRenderer {
        public StatusIconRenderer(int size) {
            super(size);
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            double scale = size / 24.0;
            g2.scale(scale, scale);
            // Status icon - pulse/activity indicator
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Main circle (system core)
            g2.setColor(DesignSystem.SUCCESS_500);
            g2.fillOval(10, 10, 4, 4);
            // Activity rings (pulse indicators)
            g2.setColor(DesignSystem.SECONDARY_400);
            g2.drawOval(7, 7, 10, 10);
            g2.setColor(DesignSystem.SECONDARY_300);
            g2.drawOval(4, 4, 16, 16);
            // Small activity dots around
            g2.setColor(DesignSystem.PRIMARY_500);
            g2.fillOval(12, 3, 2, 2);  // top
            g2.fillOval(19, 12, 2, 2); // right
            g2.fillOval(12, 19, 2, 2); // bottom
            g2.fillOval(3, 12, 2, 2);  // left
            g2.dispose();
        }
    }
    /**
     * Renderer per icona Navigation (compass)
     */
    private static class NavigationIconRenderer extends BaseIconRenderer {
        public NavigationIconRenderer(int size) {
            super(size);
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);
            double scale = size / 24.0;
            g2.scale(scale, scale);
            drawNavigationIcon(g2);
            g2.dispose();
        }
        private void drawNavigationIcon(Graphics2D g2) {
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Compass circle
            g2.setColor(DesignSystem.PRIMARY_500);
            g2.drawOval(2, 2, 20, 20);
            // Inner compass circle
            g2.setColor(DesignSystem.PRIMARY_300);
            g2.drawOval(6, 6, 12, 12);
            // Compass needle
            g2.setColor(DesignSystem.ERROR_500);
            int[] xPoints = {12, 8, 12, 16};
            int[] yPoints = {4, 12, 20, 12};
            g2.fillPolygon(xPoints, yPoints, 4);
            // Center dot
            g2.setColor(DesignSystem.getTextPrimary());
            g2.fillOval(10, 10, 4, 4);
            // Direction markers
            g2.setColor(DesignSystem.PRIMARY_400);
            g2.setStroke(new BasicStroke(1f));
            // North
            g2.drawLine(12, 1, 12, 3);
            // East  
            g2.drawLine(21, 12, 23, 12);
            // South
            g2.drawLine(12, 21, 12, 23);
            // West
            g2.drawLine(1, 12, 3, 12);
        }
    }
    /**
     * Renderer per icona Eventi (calendar)
     */
    private static class EventiIconRenderer extends BaseIconRenderer {
        public EventiIconRenderer(int size) {
            super(size);
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);
            double scale = size / 24.0;
            g2.scale(scale, scale);
            drawEventiIcon(g2);
            g2.dispose();
        }
        private void drawEventiIcon(Graphics2D g2) {
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Calendar background
            g2.setColor(DesignSystem.getSurfaceElevated());
            g2.fillRoundRect(4, 6, 16, 16, 3, 3);
            // Calendar border
            g2.setColor(DesignSystem.PRIMARY_500);
            g2.drawRoundRect(4, 6, 16, 16, 3, 3);
            // Calendar header
            g2.setColor(DesignSystem.PRIMARY_500);
            g2.fillRoundRect(4, 6, 16, 4, 3, 3);
            // Binding rings
            g2.setColor(DesignSystem.PRIMARY_600);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(8, 3, 8, 8);
            g2.drawLine(16, 3, 16, 8);
            // Calendar grid lines
            g2.setColor(DesignSystem.getBorderLight());
            g2.setStroke(new BasicStroke(0.5f));
            // Horizontal lines
            g2.drawLine(6, 13, 18, 13);
            g2.drawLine(6, 16, 18, 16);
            g2.drawLine(6, 19, 18, 19);
            // Vertical lines
            g2.drawLine(9, 11, 9, 21);
            g2.drawLine(12, 11, 12, 21);
            g2.drawLine(15, 11, 15, 21);
            // Some event dots
            g2.setColor(DesignSystem.SUCCESS_500);
            g2.fillOval(7, 14, 2, 2);
            g2.setColor(DesignSystem.WARNING_500);
            g2.fillOval(13, 17, 2, 2);
            g2.setColor(DesignSystem.ERROR_500);
            g2.fillOval(10, 20, 2, 2);
        }
    }
    /**
     * Renderer per icona Profile (profilo utente)
     */
    private static class ProfileIconRenderer extends BaseIconRenderer {
        public ProfileIconRenderer(int size) {
            super(size);
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);
            double scale = size / 24.0;
            g2.scale(scale, scale);
            drawUserIcon(g2);
            g2.dispose();
        }
        private void drawUserIcon(Graphics2D g2) {
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Head circle
            g2.setColor(DesignSystem.PRIMARY_500);
            g2.fillOval(8, 4, 8, 8);
            // Body
            g2.setColor(DesignSystem.PRIMARY_500);
            g2.fillRoundRect(6, 14, 12, 8, 6, 6);
            // Border
            g2.setColor(DesignSystem.PRIMARY_600);
            g2.drawOval(8, 4, 8, 8);
            g2.drawRoundRect(6, 14, 12, 8, 6, 6);
        }
    }
    /**
     * Renderer per icona Stats (statistiche)
     */
    private static class StatsIconRenderer extends BaseIconRenderer {
        public StatsIconRenderer(int size) {
            super(size);
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);
            double scale = size / 24.0;
            g2.scale(scale, scale);
            drawStatsIcon(g2);
            g2.dispose();
        }
        private void drawStatsIcon(Graphics2D g2) {
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Chart bars
            g2.setColor(DesignSystem.SUCCESS_500);
            g2.fillRect(4, 16, 3, 6);
            g2.setColor(DesignSystem.WARNING_500);
            g2.fillRect(8, 12, 3, 10);
            g2.setColor(DesignSystem.PRIMARY_500);
            g2.fillRect(12, 8, 3, 14);
            g2.setColor(DesignSystem.ERROR_500);
            g2.fillRect(16, 14, 3, 8);
            // Axis
            g2.setColor(DesignSystem.getTextSecondary());
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(3, 22, 21, 22); // X axis
            g2.drawLine(3, 22, 3, 4);   // Y axis
        }
    }
    /**
     * Renderer per icona Settings (impostazioni)
     */
    private static class SettingsIconRenderer extends BaseIconRenderer {
        public SettingsIconRenderer(int size) {
            super(size);
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);
            double scale = size / 24.0;
            g2.scale(scale, scale);
            drawSettingsIcon(g2);
            g2.dispose();
        }
        private void drawSettingsIcon(Graphics2D g2) {
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Outer gear
            g2.setColor(DesignSystem.SECONDARY_500);
            g2.drawOval(4, 4, 16, 16);
            // Inner circle
            g2.setColor(DesignSystem.SECONDARY_600);
            g2.fillOval(9, 9, 6, 6);
            // Gear teeth
            g2.setColor(DesignSystem.SECONDARY_500);
            // Top
            g2.fillRect(11, 2, 2, 3);
            // Right
            g2.fillRect(19, 11, 3, 2);
            // Bottom
            g2.fillRect(11, 19, 2, 3);
            // Left
            g2.fillRect(2, 11, 3, 2);
            // Diagonal teeth
            g2.fillRect(16, 5, 2, 2);
            g2.fillRect(17, 17, 2, 2);
            g2.fillRect(5, 17, 2, 2);
            g2.fillRect(5, 5, 2, 2);
        }
    }

    /**
     * Renderer per l'icona principale dell'applicazione ADS
     */
    private static class ADSAppIconRenderer extends BaseIconRenderer {
        public ADSAppIconRenderer(int size) {
            super(size);
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.translate(x, y);

            // Sfondo circolare moderno con gradiente
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(59, 130, 246), // Blue-500
                size, size, new Color(147, 51, 234) // Purple-600
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, size, size, size/4, size/4);

            // Bordo sottile
            g2.setColor(new Color(255, 255, 255, 40));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, size-2, size-2, size/4, size/4);

            // Testo ADS
            g2.setColor(Color.WHITE);
            Font font = new Font("Inter", Font.BOLD, Math.max(8, size/3));
            g2.setFont(font);
            
            FontMetrics fm = g2.getFontMetrics();
            String text = "ADS";
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            
            int textX = (size - textWidth) / 2;
            int textY = (size + textHeight) / 2 - 2;
            
            // Ombra del testo
            g2.setColor(new Color(0, 0, 0, 50));
            g2.drawString(text, textX + 1, textY + 1);
            
            // Testo principale
            g2.setColor(Color.WHITE);
            g2.drawString(text, textX, textY);

            g2.dispose();
        }
    }
}
