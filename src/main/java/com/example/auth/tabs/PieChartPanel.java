package com.example.auth.tabs;

import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Простая круговая диаграмма на Graphics2D.
 * Сверху — секторная диаграмма, под ней — легенда
 * с названием, количеством и процентом каждой категории.
 */
public class PieChartPanel extends JComponent {

    /**
     * @param label название категории
     * @param value значение (количество)
     * @param color цвет сектора
     */
    public record Slice(String label, int value, Color color) {
    }

    private static final int CHART_SIZE = 200;
    private static final int LEGEND_ROW_HEIGHT = 22;
    private static final int PADDING = 16;
    private static final int LEGEND_SWATCH = 12;
    // Минимальный угол сектора (в градусах), при котором сектор подписывается,
    // меньшие сектора не подписываются вовсе
    private static final int MIN_LABELED_ANGLE = 20;

    private final List<Slice> slices;

    public PieChartPanel(List<Slice> slices) {
        this.slices = slices;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int total = slices.stream().mapToInt(Slice::value).sum();
        if (total <= 0) {
            return;
        }

        Font labelFont = g.getFont().deriveFont(Font.BOLD, 14f);

        // Общее количество над диаграммой
        g2.setColor(JBColor.foreground());
        g2.setFont(g.getFont().deriveFont(Font.BOLD, 13f));
        String totalText = "Всего: " + total;
        FontMetrics tfm = g2.getFontMetrics();
        g2.drawString(totalText,
                (getWidth() - tfm.stringWidth(totalText)) / 2, PADDING + tfm.getAscent());

        // Диаграмма
        int x = (getWidth() - CHART_SIZE) / 2;
        int y = PADDING + 20; // отступ под строку "Всего"
        int cx = x + CHART_SIZE / 2;
        int cy = y + CHART_SIZE / 2;
        int consumedAngle = 0;
        for (int i = 0; i < slices.size(); i++) {
            Slice slice = slices.get(i);
            // последний сектор добирает остаток, чтобы круг замкнулся
            int angle = (i == slices.size() - 1)
                    ? 360 - consumedAngle
                    : Math.round(360f * slice.value() / total);
            g2.setColor(slice.color());
            g2.fillArc(x, y, CHART_SIZE, CHART_SIZE, 90 - consumedAngle, -angle);

            // Подпись сектора "значение/процент" — только для достаточно
            // крупных секторов, маленькие не подписываем
            if (angle < MIN_LABELED_ANGLE) {
                consumedAngle += angle;
                continue;
            }
            double percent = 100.0 * slice.value() / total;
            String text = slice.value() + "/" + String.format("%.1f%%", percent);
            double midAngle = 90 - consumedAngle - angle / 2.0;
            double radius = CHART_SIZE * 0.32;
            int tx = (int) (cx + radius * Math.cos(Math.toRadians(midAngle)));
            int ty = (int) (cy - radius * Math.sin(Math.toRadians(midAngle)));

            g2.setFont(labelFont);
            FontMetrics lfm = g2.getFontMetrics();
            int textX = tx - lfm.stringWidth(text) / 2;
            int textY = ty + lfm.getAscent() / 2;
            g2.setColor(JBColor.foreground());
            g2.drawString(text, textX, textY);

            consumedAngle += angle;
        }

        // Легенда под диаграммой: название, количество и процент
        FontMetrics fm = g2.getFontMetrics();
        int ly = y + CHART_SIZE + PADDING / 2;
        int lx = PADDING;
        for (Slice slice : slices) {
            g2.setColor(slice.color());
            g2.fillRect(lx, ly + 4, LEGEND_SWATCH, LEGEND_SWATCH);

            g2.setColor(JBColor.foreground());
            double percent = 100.0 * slice.value() / total;
            String text = String.format("%s: %d (%.1f%%)",
                    slice.label(), slice.value(), percent);
            g2.drawString(text, lx + LEGEND_SWATCH + 8, ly + fm.getAscent());

            ly += LEGEND_ROW_HEIGHT;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int totalLineHeight = 20; // строка "Всего" над диаграммой
        int legendHeight = slices.size() * LEGEND_ROW_HEIGHT;
        return new Dimension(
                2 * PADDING + 240,
                2 * PADDING + totalLineHeight + CHART_SIZE + legendHeight);
    }
}
