package root.utils;

import javafx.scene.input.MouseEvent;

/** Представление точки */
public class Point {
    public double x, y;
    public Point() {}
    public Point(double _x, double _y){
        x = _x;
        y = _y;
    }

    public void setCoord(MouseEvent e) {
        x = e.getSceneX();
        y = e.getSceneY();
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
