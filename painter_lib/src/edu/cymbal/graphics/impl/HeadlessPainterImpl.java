package edu.cymbal.graphics.impl;

import edu.cymbal.graphics.PainterAPI;
import java.awt.Color;

/** Headless: no GUI; keeps minimal state and can log to stdout if desired. */
public class HeadlessPainterImpl implements PainterAPI {
    private double x = 0.0, y = 0.0;
    private double headingDeg = 0.0;
    private boolean penDown = true;
    private Color penColor = Color.BLACK;
    private float penWidth = 2f;
    private int delayMs = 0;
    private boolean filling = false;
    private Color fillColor = null;

    @Override public void forward(double dist) {
        double rad = Math.toRadians(headingDeg);
        x += dist * Math.cos(rad);
        y += dist * Math.sin(rad);
        sleep();
    }

    @Override public void turnRight(double deg) { headingDeg = (headingDeg + deg) % 360; sleep(); }
    @Override public void turnLeft(double deg)  { headingDeg = (headingDeg - deg) % 360; sleep(); }

    @Override public void liftPen()  { penDown = false; }
    @Override public void lowerPen() { penDown = true;  }

    @Override public void moveTo(double nx, double ny) { x = nx; y = ny; sleep(); }

    @Override public void setPenColor(Color c) { this.penColor = c; }
    @Override public void setPenWidth(float w) { this.penWidth = w; }

    @Override public void startFill(Color c) { filling = true; fillColor = c; }
    @Override public void startFill() { filling = true; fillColor = penColor; }
    @Override public void finishFill()       { filling = false; fillColor = null; }

    @Override public void setSpeed(int ms) { this.delayMs = Math.max(0, ms); }

    @Override public void reset() { x = 0; y = 0; headingDeg = 0; }
    @Override public void clear() { /* no canvas, so nothing to do */ }

    @Override public double getX() { return x; }
    @Override public double getY() { return y; }
    @Override
public void backward(double distance) {
    turnRight(180);
    forward(distance);
    turnLeft(180);
}

@Override
public void circle(double radius) {
    double r = radius;
    double circumference = 2 * Math.PI * Math.abs(r);
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


    private void sleep() {
        if (delayMs > 0) {
            try { Thread.sleep(delayMs); } catch (InterruptedException ignored) {}
        }
    }
}
