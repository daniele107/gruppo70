package gui;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
 * Pannello per visualizzazioni grafiche personalizzate.
 * Implementa grafici a barre, torta e linee senza dipendenze esterne.
 */
public class ChartPanel extends JPanel {
    public enum ChartType {
        BAR_CHART,
        PIE_CHART,
        LINE_CHART
    }
    private ChartType chartType;
    private Map<String, Number> data;
    private Color[] colors;
    // Costanti per lo styling
    private static final Color[] DEFAULT_COLORS = {
        new Color(52, 152, 219),   // Blue
        new Color(155, 89, 182),   // Purple
        new Color(46, 204, 113),   // Green
        new Color(241, 196, 15),   // Yellow
        new Color(230, 126, 34),   // Orange
        new Color(231, 76, 60),    // Red
        new Color(149, 165, 166),  // Gray
        new Color(44, 62, 80)      // Dark Blue
    };
    /**
     * Costruttore per grafico a barre
     */
    public static ChartPanel createBarChart(String title, Map<String, Number> data) {
        ChartPanel panel = new ChartPanel(ChartType.BAR_CHART, title, data);
        panel.setPreferredSize(new Dimension(400, 300));
        return panel;
    }
    /**
     * Costruttore per grafico a torta
     */
    public static ChartPanel createPieChart(String title, Map<String, Number> data) {
        ChartPanel panel = new ChartPanel(ChartType.PIE_CHART, title, data);
        panel.setPreferredSize(new Dimension(400, 300));
        return panel;
    }
    /**
     * Costruttore per grafico a linee
     */
    public static ChartPanel createLineChart(String title, Map<String, Number> data) {
        ChartPanel panel = new ChartPanel(ChartType.LINE_CHART, title, data);
        panel.setPreferredSize(new Dimension(400, 300));
        return panel;
    }
    /**
     * Costruttore privato
     */
    private ChartPanel(ChartType chartType, String title, Map<String, Number> data) {
        this.chartType = chartType;
        this.data = new HashMap<>(data);
        this.colors = DEFAULT_COLORS;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(52, 73, 94)
        ));
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        // Margini
        int margin = 40;
        int chartWidth = width - 2 * margin;
        int chartHeight = height - 2 * margin - 30; // spazio per titolo
        switch (chartType) {
            case BAR_CHART:
                drawBarChart(g2d, margin, margin + 30, chartWidth, chartHeight);
                break;
            case PIE_CHART:
                drawPieChart(g2d, margin, margin + 30, chartWidth, chartHeight);
                break;
            case LINE_CHART:
                drawLineChart(g2d, margin, margin + 30, chartWidth, chartHeight);
                break;
        }
        g2d.dispose();
    }
    /**
     * Disegna un grafico a barre
     */
    private void drawBarChart(Graphics2D g2d, int x, int y, int width, int height) {
        if (data.isEmpty()) {
            drawNoDataMessage(g2d, x, y, width, height);
            return;
        }
        List<String> labels = new ArrayList<>(data.keySet());
        int numBars = labels.size();
        if (numBars == 0) return;
        // Calcola dimensioni
        int barWidth = Math.max(20, (width - 60) / numBars);
        int spacing = Math.max(5, (width - 60 - barWidth * numBars) / (numBars + 1));
        int maxBarHeight = height - 60; // spazio per etichette
        // Trova il valore massimo per scalare
        double maxValue = data.values().stream().mapToDouble(Number::doubleValue).max().orElse(1.0);
        // Disegna assi
        g2d.setColor(Color.BLACK);
        g2d.drawLine(x, y, x, y + maxBarHeight); // Asse Y
        g2d.drawLine(x, y + maxBarHeight, x + width, y + maxBarHeight); // Asse X
        // Disegna barre
        for (int i = 0; i < numBars; i++) {
            String label = labels.get(i);
            double value = data.get(label).doubleValue();
            int barHeight = (int) ((value / maxValue) * maxBarHeight);
            int barX = x + spacing + i * (barWidth + spacing);
            int barY = y + maxBarHeight - barHeight;
            // Colore barra
            g2d.setColor(colors[i % colors.length]);
            g2d.fillRect(barX, barY, barWidth, barHeight);
            // Bordo barra
            g2d.setColor(Color.BLACK);
            g2d.drawRect(barX, barY, barWidth, barHeight);
            // Etichetta valore sopra la barra
            g2d.setColor(Color.BLACK);
            String valueText = String.valueOf(Math.round(value));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = barX + barWidth / 2 - fm.stringWidth(valueText) / 2;
            int textY = barY - 5;
            g2d.drawString(valueText, textX, textY);
            // Etichetta sotto la barra
            g2d.setColor(Color.DARK_GRAY);
            String shortLabel = label.length() > 10 ? label.substring(0, 7) + "..." : label;
            int labelX = barX + barWidth / 2 - fm.stringWidth(shortLabel) / 2;
            int labelY = y + maxBarHeight + 15;
            g2d.drawString(shortLabel, labelX, labelY);
        }
        // Disegna scala Y
        drawYAxisScale(g2d, x, y, maxBarHeight, maxValue);
    }
    /**
     * Disegna un grafico a torta
     */
    private void drawPieChart(Graphics2D g2d, int x, int y, int width, int height) {
        if (data.isEmpty()) {
            drawNoDataMessage(g2d, x, y, width, height);
            return;
        }
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int radius = Math.min(width, height) / 2 - 20;
        // Calcola totale per percentuali
        double total = data.values().stream().mapToDouble(Number::doubleValue).sum();
        if (total == 0) return;
        List<String> labels = new ArrayList<>(data.keySet());
        double currentAngle = 0;
        // Disegna sezioni della torta
        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            double value = data.get(label).doubleValue();
            double angle = (value / total) * 360;
            // Colore sezione
            g2d.setColor(colors[i % colors.length]);
            g2d.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2,
                       (int) currentAngle, (int) angle);
            // Bordo sezione
            g2d.setColor(Color.BLACK);
            g2d.drawArc(centerX - radius, centerY - radius, radius * 2, radius * 2,
                       (int) currentAngle, (int) angle);
            currentAngle += angle;
        }
        // Disegna legenda
        drawPieLegend(g2d, x + width - 120, y + 20, labels);
    }
    /**
     * Disegna un grafico a linee
     */
    private void drawLineChart(Graphics2D g2d, int x, int y, int width, int height) {
        if (data.isEmpty()) {
            drawNoDataMessage(g2d, x, y, width, height);
            return;
        }
        List<String> labels = new ArrayList<>(data.keySet());
        int numPoints = labels.size();
        if (numPoints < 2) {
            drawNoDataMessage(g2d, x, y, width, height);
            return;
        }
        // Calcola dimensioni
        int chartHeight = height - 60;
        int chartWidth = width - 60;
        // Trova valori min/max per scalare
        double maxValue = data.values().stream().mapToDouble(Number::doubleValue).max().orElse(1.0);
        double minValue = data.values().stream().mapToDouble(Number::doubleValue).min().orElse(0.0);
        // Disegna assi
        g2d.setColor(Color.BLACK);
        g2d.drawLine(x, y, x, y + chartHeight); // Asse Y
        g2d.drawLine(x, y + chartHeight, x + chartWidth, y + chartHeight); // Asse X
        // Calcola punti della linea
        int[] xPoints = new int[numPoints];
        int[] yPoints = new int[numPoints];
        for (int i = 0; i < numPoints; i++) {
            double value = data.get(labels.get(i)).doubleValue();
            xPoints[i] = x + (i * chartWidth) / (numPoints - 1);
            yPoints[i] = y + chartHeight - (int) (((value - minValue) / (maxValue - minValue)) * chartHeight);
        }
        // Disegna linea
        g2d.setColor(colors[0]);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawPolyline(xPoints, yPoints, numPoints);
        // Disegna punti
        g2d.setColor(Color.RED);
        for (int i = 0; i < numPoints; i++) {
            g2d.fillOval(xPoints[i] - 4, yPoints[i] - 4, 8, 8);
        }
        // Disegna etichette valori
        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();
        for (int i = 0; i < numPoints; i++) {
            String valueText = String.valueOf(Math.round(data.get(labels.get(i)).doubleValue()));
            int textX = xPoints[i] - fm.stringWidth(valueText) / 2;
            int textY = yPoints[i] - 10;
            g2d.drawString(valueText, textX, textY);
        }
        // Disegna etichette X
        g2d.setColor(Color.DARK_GRAY);
        for (int i = 0; i < numPoints; i++) {
            String label = labels.get(i).length() > 8 ? labels.get(i).substring(0, 5) + "..." : labels.get(i);
            int textX = xPoints[i] - fm.stringWidth(label) / 2;
            int textY = y + chartHeight + 15;
            g2d.drawString(label, textX, textY);
        }
        // Disegna scala Y
        drawYAxisScale(g2d, x, y, chartHeight, maxValue);
    }
    /**
     * Disegna la scala dell'asse Y
     */
    private void drawYAxisScale(Graphics2D g2d, int x, int y, int height, double maxValue) {
        g2d.setColor(Color.GRAY);
        FontMetrics fm = g2d.getFontMetrics();
        for (int i = 0; i <= 5; i++) {
            double value = (maxValue * i) / 5;
            int yPos = y + height - (i * height) / 5;
            // Linea orizzontale
            g2d.drawLine(x - 5, yPos, x + 5, yPos);
            // Etichetta valore
            String valueText = String.valueOf(Math.round(value));
            int textX = x - 10 - fm.stringWidth(valueText);
            int textY = yPos + 4;
            g2d.drawString(valueText, textX, textY);
        }
    }
    /**
     * Disegna la legenda per il grafico a torta
     */
    private void drawPieLegend(Graphics2D g2d, int x, int y, List<String> labels) {
        int lineHeight = 20;
        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            // Colore quadrato
            g2d.setColor(colors[i % colors.length]);
            g2d.fillRect(x, y + i * lineHeight, 12, 12);
            // Bordo quadrato
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y + i * lineHeight, 12, 12);
            // Testo etichetta
            g2d.setColor(Color.BLACK);
            String shortLabel = label.length() > 15 ? label.substring(0, 12) + "..." : label;
            g2d.drawString(shortLabel, x + 18, y + i * lineHeight + 10);
        }
    }
    /**
     * Disegna messaggio quando non ci sono dati
     */
    private void drawNoDataMessage(Graphics2D g2d, int x, int y, int width, int height) {
        g2d.setColor(Color.GRAY);
        FontMetrics fm = g2d.getFontMetrics();
        String message = "Nessun dato disponibile";
        int textX = x + width / 2 - fm.stringWidth(message) / 2;
        int textY = y + height / 2;
        g2d.drawString(message, textX, textY);
    }
    /**
     * Crea dati di esempio per testare i grafici
     */
    public static Map<String, Number> createSampleData() {
        Map<String, Number> data = new HashMap<>();
        data.put("Team A", 25);
        data.put("Team B", 18);
        data.put("Team C", 32);
        data.put("Team D", 15);
        data.put("Team E", 28);
        return data;
    }
    /**
     * Crea dati di esempio per grafico a torta
     */
    public static Map<String, Number> createPieSampleData() {
        Map<String, Number> data = new HashMap<>();
        data.put("Partecipanti", 45);
        data.put("Giudici", 8);
        data.put("Organizzatori", 3);
        return data;
    }
    /**
     * Crea dati di esempio per grafico a linee
     */
    public static Map<String, Number> createLineSampleData() {
        Map<String, Number> data = new HashMap<>();
        data.put("Gen", 10);
        data.put("Feb", 15);
        data.put("Mar", 12);
        data.put("Apr", 20);
        data.put("Mag", 18);
        data.put("Giu", 25);
        return data;
    }
}
