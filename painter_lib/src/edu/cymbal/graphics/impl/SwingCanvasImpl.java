package edu.cymbal.graphics.impl;

import edu.cymbal.graphics.CanvasAPI;
import edu.cymbal.graphics.PainterAPI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SwingCanvasImpl extends JPanel implements CanvasAPI {
    private final int width;
    private final int height;
    private final SwingPainterImpl painter;
    private final List<Shape> shapes = new ArrayList<>();
    private JFrame frame;
    private Color backgroundColor = Color.WHITE;



    public SwingCanvasImpl(int width, int height, String title) {
        this.width = width;
        this.height = height;

        // Create painter with known dimensions so it can start at center
        this.painter = new SwingPainterImpl(this, width, height);

        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        setPreferredSize(new Dimension(width, height));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        setBackground(backgroundColor);

    }

    @Override
    public PainterAPI getPainter() {
        return painter;
    }

    void addShape(Shape s) {
        synchronized (shapes) { shapes.add(s); }
        repaint();
    }

    /** Clear all drawn shapes from the canvas. */
    void clearAll() {
        synchronized (shapes) { shapes.clear(); }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        synchronized (shapes) {
            for (Shape s : shapes) s.draw(g);
        }
    }
    @Override
    public void awaitClose() {
        if (frame == null) return;

        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);

        // If the frame is already closed/disposed, return immediately
        if (!frame.isDisplayable()) return;

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
    @Override
    public void setBackgroundColor(Color color) {
        if (color == null) return;
        this.backgroundColor = color;
        // Keep the panel background in sync so super.paintComponent(g) clears with this color
        setBackground(backgroundColor);
        repaint();
    }
}
