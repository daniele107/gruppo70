package gui;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Sistema di layout moderno e responsivo
 * Implementa grid system e componenti layout avanzati
 */
public final class ModernLayout {
    // Constants for error messages
    private static final String LAYOUT_NOT_USED_MSG = "Layout method not used in this implementation";
    /**
     * Private constructor to prevent instantiation of utility class
     */
    private ModernLayout() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    // ===================== FLEX LAYOUT =====================
    public static class FlexPanel extends JPanel {
        public enum Direction {
            ROW, COLUMN
        }
        public enum JustifyContent {
            START, CENTER, END, SPACE_BETWEEN, SPACE_AROUND, SPACE_EVENLY
        }
        public enum AlignItems {
            START, CENTER, END, STRETCH
        }
        private Direction direction = Direction.ROW;
        private JustifyContent justifyContent = JustifyContent.START;
        private AlignItems alignItems = AlignItems.START;
        private int gap = 0;
        public FlexPanel() {
            setLayout(new FlexLayout());
        }
        public FlexPanel(Direction direction) {
            this();
            this.direction = direction;
        }
        public FlexPanel setDirection(Direction direction) {
            this.direction = direction;
            revalidate();
            return this;
        }
        public FlexPanel setJustifyContent(JustifyContent justifyContent) {
            this.justifyContent = justifyContent;
            revalidate();
            return this;
        }
        public FlexPanel setAlignItems(AlignItems alignItems) {
            this.alignItems = alignItems;
            revalidate();
            return this;
        }
        public FlexPanel setGap(int gap) {
            this.gap = gap;
            revalidate();
            return this;
        }
        private class FlexLayout implements LayoutManager2 {
            @Override
            public void addLayoutComponent(Component comp, Object constraints) {
                // Not used in this layout - components are managed by the panel
                throw new UnsupportedOperationException(LAYOUT_NOT_USED_MSG);
            }
            @Override
            public void addLayoutComponent(String name, Component comp) {
                // Not used in this layout - components are managed by the panel
                throw new UnsupportedOperationException(LAYOUT_NOT_USED_MSG);
            }
            @Override
            public void removeLayoutComponent(Component comp) {
                // Not used in this layout - components are managed by the panel
                throw new UnsupportedOperationException(LAYOUT_NOT_USED_MSG);
            }
            @Override
            @SuppressWarnings("java:S1172") // parametro richiesto dall'interfaccia
            public Dimension preferredLayoutSize(Container parent) {
                return calculateSize(parent, false);
            }
            @Override
            @SuppressWarnings("java:S1172") // parametro richiesto dall'interfaccia
            public Dimension minimumLayoutSize(Container parent) {
                return calculateSize(parent, true);
            }
            @Override
            public Dimension maximumLayoutSize(Container target) {
                return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
            }
            @Override
            public float getLayoutAlignmentX(Container target) {
                return 0.5f;
            }
            @Override
            public float getLayoutAlignmentY(Container target) {
                return 0.5f;
            }
            @Override
            public void invalidateLayout(Container target) {
                // Not used in this layout - no caching implemented
                throw new UnsupportedOperationException(LAYOUT_NOT_USED_MSG);
            }
            @Override
            public void layoutContainer(Container parent) {
                Component[] components = parent.getComponents();
                if (components.length == 0) return;
                Insets insets = parent.getInsets();
                int width = parent.getWidth() - insets.left - insets.right;
                int height = parent.getHeight() - insets.top - insets.bottom;
                if (direction == Direction.ROW) {
                    layoutRow(parent, components, width, height, insets);
                } else {
                    layoutColumn(parent, components, width, height, insets);
                }
            }
            @SuppressWarnings("java:S1172") // parent parameter required by interface but not used in this implementation
            private void layoutRow(Container parent, Component[] components, int width, int height, Insets insets) {
                int totalGap = gap * (components.length - 1);
                int availableWidth = width - totalGap;
                // Calculate preferred widths
                int[] preferredWidths = new int[components.length];
                int totalPreferredWidth = 0;
                for (int i = 0; i < components.length; i++) {
                    preferredWidths[i] = components[i].getPreferredSize().width;
                    totalPreferredWidth += preferredWidths[i];
                }
                // Distribute space based on justify content
                int[] actualWidths = new int[components.length];
                int[] positions = new int[components.length];
                if (totalPreferredWidth <= availableWidth) {
                    System.arraycopy(preferredWidths, 0, actualWidths, 0, components.length);
                    distributeExtraSpace(positions, actualWidths, availableWidth - totalPreferredWidth, components.length);
                } else {
                    // Shrink components proportionally
                    if (totalPreferredWidth == 0) totalPreferredWidth = 1;
                    for (int i = 0; i < components.length; i++) {
                        actualWidths[i] = (int) ((double) preferredWidths[i] / totalPreferredWidth * availableWidth);
                    }
                    calculatePositions(positions, actualWidths, 0);
                }
                // Position components
                for (int i = 0; i < components.length; i++) {
                    int componentHeight = calculateComponentHeight(components[i], height);
                    int y = calculateAlignmentY(componentHeight, height, insets.top);
                    components[i].setBounds(insets.left + positions[i], y, actualWidths[i], componentHeight);
                }
            }
            @SuppressWarnings("java:S1172") // parent parameter required by interface but not used in this implementation
            private void layoutColumn(Container parent, Component[] components, int width, int height, Insets insets) {
                int totalGap = gap * (components.length - 1);
                int availableHeight = height - totalGap;
                // Calculate preferred heights
                int[] preferredHeights = new int[components.length];
                int totalPreferredHeight = 0;
                for (int i = 0; i < components.length; i++) {
                    preferredHeights[i] = components[i].getPreferredSize().height;
                    totalPreferredHeight += preferredHeights[i];
                }
                // Distribute space
                int[] actualHeights = new int[components.length];
                int[] positions = new int[components.length];
                if (totalPreferredHeight <= availableHeight) {
                    System.arraycopy(preferredHeights, 0, actualHeights, 0, components.length);
                    distributeExtraSpace(positions, actualHeights, availableHeight - totalPreferredHeight, components.length);
                } else {
                    if (totalPreferredHeight == 0) totalPreferredHeight = 1;
                    for (int i = 0; i < components.length; i++) {
                        actualHeights[i] = (int) ((double) preferredHeights[i] / totalPreferredHeight * availableHeight);
                    }
                    calculatePositions(positions, actualHeights, 0);
                }
                // Position components
                for (int i = 0; i < components.length; i++) {
                    int componentWidth = calculateComponentWidth(components[i], width);
                    int x = calculateAlignmentX(componentWidth, width, insets.left);
                    components[i].setBounds(x, insets.top + positions[i], componentWidth, actualHeights[i]);
                }
            }
            private void distributeExtraSpace(int[] positions, int[] sizes, int extraSpace, int count) {
                if (justifyContent == JustifyContent.START) {
                    calculatePositions(positions, sizes, 0);
                } else if (justifyContent == JustifyContent.CENTER) {
                    calculatePositions(positions, sizes, extraSpace / 2);
                } else if (justifyContent == JustifyContent.END) {
                    calculatePositions(positions, sizes, extraSpace);
                } else if (justifyContent == JustifyContent.SPACE_BETWEEN) {
                    if (count > 1) {
                        int spaceBetween = extraSpace / (count - 1);
                        calculatePositionsWithSpacing(positions, sizes, 0, spaceBetween);
                    } else {
                        calculatePositions(positions, sizes, 0);
                    }
                } else if (justifyContent == JustifyContent.SPACE_AROUND) {
                    int spaceAround = extraSpace / count;
                    calculatePositionsWithSpacing(positions, sizes, spaceAround / 2, spaceAround);
                } else if (justifyContent == JustifyContent.SPACE_EVENLY) {
                    int spaceEvenly = extraSpace / (count + 1);
                    calculatePositionsWithSpacing(positions, sizes, spaceEvenly, spaceEvenly);
                }
            }
            private void calculatePositions(int[] positions, int[] sizes, int startOffset) {
                int currentPos = startOffset;
                for (int i = 0; i < positions.length; i++) {
                    positions[i] = currentPos;
                    currentPos += sizes[i] + gap;
                }
            }
            private void calculatePositionsWithSpacing(int[] positions, int[] sizes, int startOffset, int extraSpacing) {
                int currentPos = startOffset;
                for (int i = 0; i < positions.length; i++) {
                    positions[i] = currentPos;
                    currentPos += sizes[i] + gap + extraSpacing;
                }
            }
            private int calculateComponentHeight(Component component, int availableHeight) {
                if (alignItems == AlignItems.STRETCH) {
                    return availableHeight;
                } else {
                    return component.getPreferredSize().height;
                }
            }
            private int calculateComponentWidth(Component component, int availableWidth) {
                if (alignItems == AlignItems.STRETCH) {
                    return availableWidth;
                } else {
                    return component.getPreferredSize().width;
                }
            }
            private int calculateAlignmentY(int componentHeight, int availableHeight, int top) {
                if (alignItems == AlignItems.CENTER) {
                    return top + (availableHeight - componentHeight) / 2;
                } else if (alignItems == AlignItems.END) {
                    return top + availableHeight - componentHeight;
                } else {
                    return top;
                }
            }
            private int calculateAlignmentX(int componentWidth, int availableWidth, int left) {
                if (alignItems == AlignItems.CENTER) {
                    return left + (availableWidth - componentWidth) / 2;
                } else if (alignItems == AlignItems.END) {
                    return left + availableWidth - componentWidth;
                } else {
                    return left;
                }
            }
            private Dimension calculateSize(Container parent, boolean minimum) {
                Component[] components = parent.getComponents();
                if (components.length == 0) return new Dimension(0, 0);
                int width = 0;
                int height = 0;
                if (direction == Direction.ROW) {
                    for (Component comp : components) {
                        Dimension size = minimum ? comp.getMinimumSize() : comp.getPreferredSize();
                        width += size.width;
                        height = Math.max(height, size.height);
                    }
                    width += gap * (components.length - 1);
                } else {
                    for (Component comp : components) {
                        Dimension size = minimum ? comp.getMinimumSize() : comp.getPreferredSize();
                        height += size.height;
                        width = Math.max(width, size.width);
                    }
                    height += gap * (components.length - 1);
                }
                Insets insets = parent.getInsets();
                return new Dimension(width + insets.left + insets.right, 
                                   height + insets.top + insets.bottom);
            }
        }
    }
    // ===================== GRID LAYOUT =====================
    public static class GridPanel extends JPanel {
        private int columns;
        private int gap;
        private transient List<GridItem> items = new ArrayList<>();
        public GridPanel(int columns) {
            this(columns, DesignSystem.SPACE_LG);
        }
        public GridPanel(int columns, int gap) {
            this.columns = columns;
            this.gap = gap;
            setLayout(new GridLayout());
        }
        public GridPanel addItem(Component component) {
            return addItem(component, 1, 1);
        }
        public GridPanel addItem(Component component, int colSpan) {
            return addItem(component, colSpan, 1);
        }
        public GridPanel addItem(Component component, int colSpan, int rowSpan) {
            items.add(new GridItem(component, colSpan, rowSpan));
            add(component);
            return this;
        }
        private static class GridItem {
            final Component component;
            final int colSpan;
            final int rowSpan;
            GridItem(Component component, int colSpan, int rowSpan) {
                this.component = component;
                this.colSpan = colSpan;
                this.rowSpan = rowSpan;
            }
        }
        private class GridLayout implements LayoutManager2 {
            @Override
            public void addLayoutComponent(Component comp, Object constraints) {
                // Not used in this layout - components are managed by the panel
                throw new UnsupportedOperationException(LAYOUT_NOT_USED_MSG);
            }
            @Override
            public void addLayoutComponent(String name, Component comp) {
                // Not used in this layout - components are managed by the panel
                throw new UnsupportedOperationException(LAYOUT_NOT_USED_MSG);
            }
            @Override
            public void removeLayoutComponent(Component comp) {
                // Not used in this layout - components are managed by the panel
                throw new UnsupportedOperationException(LAYOUT_NOT_USED_MSG);
            }
            @Override
            @SuppressWarnings("java:S1172") // parametro richiesto dall'interfaccia
            public Dimension preferredLayoutSize(Container parent) {
                return calculateLayoutSize(parent, false);
            }
            @Override
            @SuppressWarnings("java:S1172") // parametro richiesto dall'interfaccia
            public Dimension minimumLayoutSize(Container parent) {
                return calculateLayoutSize(parent, true);
            }
            @Override
            public Dimension maximumLayoutSize(Container target) {
                return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
            }
            @Override
            public float getLayoutAlignmentX(Container target) {
                return 0.5f;
            }
            @Override
            public float getLayoutAlignmentY(Container target) {
                return 0.5f;
            }
            @Override
            public void invalidateLayout(Container target) {
                // Not used in this layout - no caching implemented
                throw new UnsupportedOperationException(LAYOUT_NOT_USED_MSG);
            }
            @Override
            public void layoutContainer(Container parent) {
                if (items.isEmpty()) return;
                Insets insets = parent.getInsets();
                int width = parent.getWidth() - insets.left - insets.right;
                int height = parent.getHeight() - insets.top - insets.bottom;
                // Calculate grid dimensions
                int rows = calculateRows();
                int cellWidth = (width - gap * (columns - 1)) / columns;
                int cellHeight = height / rows - gap;
                // Position items
                int currentRow = 0;
                int currentCol = 0;
                for (GridItem item : items) {
                    // Check if item fits in current row
                    if (currentCol + item.colSpan > columns) {
                        currentRow++;
                        currentCol = 0;
                    }
                    int x = insets.left + currentCol * (cellWidth + gap);
                    int y = insets.top + currentRow * (cellHeight + gap);
                    int itemWidth = cellWidth * item.colSpan + gap * (item.colSpan - 1);
                    int itemHeight = cellHeight * item.rowSpan + gap * (item.rowSpan - 1);
                    item.component.setBounds(x, y, itemWidth, itemHeight);
                    currentCol += item.colSpan;
                }
            }
            private int calculateRows() {
                int rows = 0;
                int currentCol = 0;
                for (GridItem item : items) {
                    if (currentCol + item.colSpan > columns) {
                        rows++;
                        currentCol = 0;
                    }
                    currentCol += item.colSpan;
                }
                return rows + 1; // Add one for the last row
            }
            private Dimension calculateLayoutSize(Container parent, boolean minimum) {
                if (items.isEmpty()) return new Dimension(0, 0);
                int maxWidth = 0;
                int totalHeight = 0;
                for (GridItem item : items) {
                    Dimension size = minimum ? item.component.getMinimumSize() : 
                                             item.component.getPreferredSize();
                    maxWidth = Math.max(maxWidth, size.width * item.colSpan);
                    totalHeight += size.height * item.rowSpan;
                }
                int rows = calculateRows();
                Insets insets = parent.getInsets();
                return new Dimension(
                    maxWidth * columns + gap * (columns - 1) + insets.left + insets.right,
                    totalHeight / items.size() * rows + gap * (rows - 1) + insets.top + insets.bottom
                );
            }
        }
    }
    // ===================== RESPONSIVE CONTAINER =====================
    public static class ResponsiveContainer extends JPanel {
        private final List<BreakpointLayout> breakpoints = new ArrayList<>();
        private transient LayoutManager currentLayout;
        public ResponsiveContainer() {
            addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    updateLayout();
                }
            });
        }
        public ResponsiveContainer addBreakpoint(int minWidth, LayoutManager layout) {
            breakpoints.add(new BreakpointLayout(minWidth, layout));
            breakpoints.sort((a, b) -> Integer.compare(b.minWidth, a.minWidth)); // Sort descending
            return this;
        }
        private void updateLayout() {
            int width = getWidth();
            LayoutManager newLayout = null;
            for (BreakpointLayout bp : breakpoints) {
                if (width >= bp.minWidth) {
                    newLayout = bp.layout;
                    break;
                }
            }
            if (newLayout != null && newLayout != currentLayout) {
                currentLayout = newLayout;
                setLayout(newLayout);
                revalidate();
                repaint();
            }
        }
        private static class BreakpointLayout {
            final int minWidth;
            final LayoutManager layout;
            BreakpointLayout(int minWidth, LayoutManager layout) {
                this.minWidth = minWidth;
                this.layout = layout;
            }
        }
    }
    // ===================== STACK PANEL =====================
    public static class StackPanel extends FlexPanel {
        public StackPanel() {
            super(Direction.COLUMN);
            setAlignItems(AlignItems.STRETCH);
            setGap(DesignSystem.SPACE_MD);
        }
        public StackPanel(int gap) {
            this();
            setGap(gap);
        }
    }
    // ===================== INLINE PANEL =====================
    public static class InlinePanel extends FlexPanel {
        public InlinePanel() {
            super(Direction.ROW);
            setAlignItems(AlignItems.CENTER);
            setGap(DesignSystem.SPACE_MD);
        }
        public InlinePanel(int gap) {
            this();
            setGap(gap);
        }
    }
    // ===================== SPACING UTILITIES =====================
    public static Component createHorizontalSpacer(int width) {
        return Box.createRigidArea(new Dimension(width, 0));
    }
    public static Component createVerticalSpacer(int height) {
        return Box.createRigidArea(new Dimension(0, height));
    }
    public static Component createSpacer(int width, int height) {
        return Box.createRigidArea(new Dimension(width, height));
    }
    public static JPanel createPaddedPanel(Component component, int padding) {
        return createPaddedPanel(component, padding, padding, padding, padding);
    }
    public static JPanel createPaddedPanel(Component component, int top, int left, int bottom, int right) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        panel.add(component, BorderLayout.CENTER);
        panel.setOpaque(false);
        return panel;
    }
}
