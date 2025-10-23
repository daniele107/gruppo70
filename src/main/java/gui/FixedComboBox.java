package gui;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
/**
 * ComboBox ULTRAFIX che GARANTISCE la visibilità del testo selezionato
 * Nessun rendering complesso, solo funzionalità base che FUNZIONA
 */
public class FixedComboBox<E> extends JComboBox<E> {
    // Colori e font FISSI che funzionano sempre
    private static final String FONT_FAMILY = "Arial";
    private static final Color TEXT_COLOR = new Color(0, 0, 0); // NERO ASSOLUTO
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255); // BIANCO ASSOLUTO
    private static final Color BORDER_COLOR = new Color(200, 200, 200); // GRIGIO BORDO
    private static final Color FOCUS_BORDER_COLOR = new Color(0, 120, 215); // BLU WINDOWS
    private static final Color ARROW_COLOR = new Color(100, 100, 100); // GRIGIO FRECCIA
    public FixedComboBox(E[] items) {
        super(items);
        setupComboBox();
    }
    private void setupComboBox() {
        // Font SEMPLICE e GRANDE
        setFont(new Font(FONT_FAMILY, Font.PLAIN, 16));
        // Colori FISSI
        setBackground(BACKGROUND_COLOR);
        setForeground(TEXT_COLOR);
        setOpaque(true);
        // Dimensioni
        setPreferredSize(new Dimension(350, 45));
        setMinimumSize(new Dimension(300, 40));
        // UI personalizzata SEMPLICE
        setUI(new FixedComboBoxUI());
        // Border semplice
        setBorder(new SimpleBorder(false));
        // Focus listener SEMPLICE
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(new SimpleBorder(true));
                setForeground(TEXT_COLOR);
                repaint();
            }
            @Override
            public void focusLost(FocusEvent e) {
                setBorder(new SimpleBorder(false));
                setForeground(TEXT_COLOR);
                repaint();
            }
        });
        // Renderer personalizzato per elementi della lista
        setRenderer(new FixedComboBoxRenderer());
    }
    /**
     * UI SEMPLICISSIMA che garantisce visibilità
     */
    private static class FixedComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton();
            button.setBackground(BACKGROUND_COLOR);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            return button;
        }
        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            ListCellRenderer<Object> renderer = comboBox.getRenderer();
            Component c = renderer.getListCellRendererComponent(
                listBox, comboBox.getSelectedItem(), -1, false, false);
            // FORZA i colori giusti
            c.setForeground(TEXT_COLOR);
            c.setBackground(BACKGROUND_COLOR);
            c.setFont(comboBox.getFont());
            currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y,
                                          bounds.width, bounds.height, c instanceof JPanel);
        }
        @Override
        protected ComboPopup createPopup() {
            return new FixedComboPopup(comboBox);
        }
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // Sfondo BIANCO FISSO
            g.setColor(BACKGROUND_COLOR);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            // Freccia NERA
            int arrowX = bounds.x + bounds.width - 20;
            int arrowY = bounds.y + bounds.height / 2;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(ARROW_COLOR);
            // Disegna freccia verso il basso
            int[] xPoints = {arrowX - 4, arrowX + 4, arrowX};
            int[] yPoints = {arrowY - 2, arrowY - 2, arrowY + 3};
            g2.fillPolygon(xPoints, yPoints, 3);
            g2.dispose();
        }
    }
    /**
     * Popup SEMPLICE che garantisce visibilità
     */
    private static class FixedComboPopup extends BasicComboPopup {
        @SuppressWarnings({"rawtypes", "unchecked"})
        public FixedComboPopup(JComboBox combo) {
            super(combo);
        }
        @Override
        protected void configureList() {
            super.configureList();
            list.setFont(new Font(FONT_FAMILY, Font.PLAIN, 16));
            list.setBackground(BACKGROUND_COLOR);
            list.setForeground(TEXT_COLOR);
            list.setSelectionBackground(new Color(0, 120, 215));
            list.setSelectionForeground(Color.WHITE);
        }
    }
    /**
     * Renderer SEMPLICE che garantisce visibilità
     */
    private static class FixedComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            // FORZA sempre i colori giusti
            setFont(new Font(FONT_FAMILY, Font.PLAIN, 16));
            if (isSelected) {
                setBackground(new Color(0, 120, 215));
                setForeground(Color.BLACK);
            } else {
                setBackground(BACKGROUND_COLOR);
                setForeground(TEXT_COLOR);
            }
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            return this;
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
            return new Insets(8, 12, 8, 32); // Spazio extra a destra per la freccia
        }
    }
}
