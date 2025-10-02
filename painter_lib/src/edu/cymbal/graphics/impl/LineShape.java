package edu.cymbal.graphics.impl;

import java.awt.*;

/** Simple line segment with a color and width. */
public class LineShape implements Shape {
    private final int x1, y1, x2, y2;
    private final Color color;
    private final float width;

    public LineShape(int x1, int y1, int x2, int y2, Color color, float width) {
        this.x1 = x1; this.y1 = y1;
        this.x2 = x2; this.y2 = y2;
        this.color = color;
        this.width = width;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Stroke oldStroke = g2.getStroke();
        Color oldColor = g2.getColor();

        g2.setColor(color);
        g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x1, y1, x2, y2);

        g2.setColor(oldColor);
        g2.setStroke(oldStroke);
    }
}
