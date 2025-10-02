package edu.cymbal.graphics;
import java.awt.Color;
public interface CanvasAPI {
    PainterAPI getPainter();
    void awaitClose();
    void setBackgroundColor(Color color);

}
