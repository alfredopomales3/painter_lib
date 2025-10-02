package edu.cymbal.graphics;

import java.awt.Color;

public interface PainterAPI {
    void forward(double dist);
    void turnRight(double deg);
    void turnLeft(double deg);
    void backward(double distance);

    void liftPen();
    void lowerPen();

    void moveTo(double x, double y);
    void setPenColor(Color c);
    void setPenWidth(float w);

    void startFill(Color c);
    void finishFill();

    void setSpeed(int ms);
    void reset();
    void clear();

    double getX();
    double getY();

    void circle(double radius);

}
