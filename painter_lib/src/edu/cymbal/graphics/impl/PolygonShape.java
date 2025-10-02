package edu.cymbal.graphics.impl;

import java.awt.*;
import java.util.List;

/** Polygon filled with a solid color, used for filled shapes. */
public class PolygonShape implements Shape {
    private final int[] xs;
    private final int[] ys;
    private final Color fill;

    public PolygonShape(List<Point> pts, Color fill) {
        this.fill = fill;
        this.xs = new int[pts.size()];
        this.ys = new int[pts.size()];
        for (int i = 0; i < pts.size(); i++) {
            xs[i] = pts.get(i).x;
            ys[i] = pts.get(i).y;
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(fill);
        g.fillPolygon(xs, ys, xs.length);
    }
}
