package edu.cymbal.graphics.impl;

import edu.cymbal.graphics.*;
import java.awt.Color;


public class HeadlessCanvasImpl implements CanvasAPI {
    private final HeadlessPainterImpl painter;

    public HeadlessCanvasImpl(int width, int height, String title) {
        this.painter = new HeadlessPainterImpl();
        System.out.println("Headless canvas created: " + title + " (" + width + "x" + height + ")");
    }

    @Override
    public PainterAPI getPainter() {
        return painter;
    }
    @Override
    public void awaitClose() {
     // Headless: nothing to wait for.
    }

    @Override
    public void setBackgroundColor(Color color) {
        // Headless: ignore (no visible surface).
    }

}
