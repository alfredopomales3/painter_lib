package edu.cymbal.graphics.impl;

import edu.cymbal.graphics.PainterAPI;

import javax.swing.SwingUtilities;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SwingPainterImpl implements PainterAPI {
    private final SwingCanvasImpl canvas;

    // logical pen state
    private double x, y;                // current position (initialized to canvas center)
    private double headingDeg = 0.0;    // 0 = east
    private boolean penDown = true;
    private Color penColor = Color.BLACK;
    private float penWidth = 2f;
    private int delayMs = 0;
    private final int canvasW;
    private final int canvasH;

    // fill state
    private boolean filling = false;
    private Color currentFill = null;
    private final List/*keep generic*/ <Point> fillPoints = new ArrayList<>();

    /** Construct and start in the center of the provided width/height. */
    public SwingPainterImpl(SwingCanvasImpl canvas, int width, int height) {
        this.canvas = canvas;
        this.canvasW = width;
        this.canvasH = height;
        this.x = width / 2.0;
        this.y = height / 2.0;
    }

    /** Fallback ctor (not used by our canvas, but here for completeness). */
    public SwingPainterImpl(SwingCanvasImpl canvas) {
        this(canvas, 600, 500); // default dims if someone uses this ctor
    }
    @Override
    public void backward(double distance) {
    // Move backward along current heading without changing final heading.
    // Implemented via a 180° right turn, forward, then 180° left turn.
    turnRight(180);
    forward(distance);
    turnLeft(180);
}

@Override
public void circle(double radius) {
    // Polyline approximation. Positive radius -> CCW (turnLeft), negative -> CW (turnRight)
    double r = radius;
    double circumference = 2 * Math.PI * Math.abs(r);

    // Segment count: at least 24, more for larger circles (tune as you like)
    int segments = Math.max(24, (int) Math.round(Math.abs(r) * 2));
    double stepLen = circumference / segments;

    if (r >= 0) {
        double stepDeg = 360.0 / segments;
        for (int i = 0; i < segments; i++) {
            forward(stepLen);
            turnLeft(stepDeg);
        }
    } else {
        double stepDeg = 360.0 / segments;
        for (int i = 0; i < segments; i++) {
            forward(stepLen);
            turnRight(stepDeg);
        }
    }
}


    @Override
    public void forward(double dist) {
        // Animate ~1 px per step so motion is visible with setSpeed(...)
        int steps = (int) Math.max(1, Math.ceil(Math.abs(dist)));
        double step = dist / steps;
        double rad = Math.toRadians(headingDeg);

        for (int i = 0; i < steps; i++) {
            double nx = x + step * Math.cos(rad);
            double ny = y + step * Math.sin(rad);

            if (penDown) {
                LineShape seg = new LineShape(
                        (int) Math.round(x),  (int) Math.round(y),
                        (int) Math.round(nx), (int) Math.round(ny),
                        penColor, penWidth
                );
                canvas.addShape(seg);
                if (filling) fillPoints.add(new Point((int) Math.round(nx), (int) Math.round(ny)));
            }

            x = nx; y = ny;
            sleep();
        }
    }

    @Override public void turnRight(double deg) { headingDeg = (headingDeg + deg) % 360; sleep(); }
    @Override public void turnLeft (double deg) { headingDeg = (headingDeg - deg) % 360; sleep(); }

    @Override
    public void moveTo(double wx, double wy) {
        double sx = canvasW / 2.0 + wx;
        double sy = canvasH / 2.0 - wy;

        if (penDown) {
            LineShape seg = new LineShape(
                (int) Math.round(x),  (int) Math.round(y),
                (int) Math.round(sx), (int) Math.round(sy),
                penColor, penWidth
        );
        canvas.addShape(seg);
        if (filling) fillPoints.add(new Point((int) Math.round(sx), (int) Math.round(sy)));
    }

    // Update current position in *screen* space (your forward() logic expects that)
    x = sx;
    y = sy;

    SwingUtilities.invokeLater(canvas::repaint);
    sleep();
    }
    

    @Override public void liftPen()  { penDown = false; }
    @Override public void lowerPen() { penDown = true;  }

    @Override public void setPenColor(Color c) { penColor = c; }
    @Override public void setPenWidth(float w) { penWidth = Math.max(0.1f, w); }
    @Override public void setSpeed(int ms)     { delayMs = Math.max(0, ms); }

    @Override
    public void startFill(Color c) {
        filling = true;
        currentFill = c;
        fillPoints.clear();
        fillPoints.add(new Point((int) Math.round(x), (int) Math.round(y)));
    }

    @Override
    public void startFill() {
        startFill(penColor);
    }

    @Override
    public void finishFill() {
        if (filling && currentFill != null && fillPoints.size() >= 3) {
            PolygonShape poly = new PolygonShape(fillPoints, currentFill);
            canvas.addShape(poly);
        }
        filling = false;
        currentFill = null;
        fillPoints.clear();
    }

    @Override public void reset() { x = canvasW / 2.0; y = canvasH / 2.0; headingDeg = 0; }
    @Override public void clear() { SwingUtilities.invokeLater(canvas::clearAll); }

    @Override public double getX() { return x; }
    @Override public double getY() { return y; }

    private void sleep() {
        if (delayMs > 0) {
            try { Thread.sleep(delayMs); } catch (InterruptedException ignored) {}
        }
    }
}
