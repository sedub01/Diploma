package root.utils;

import javafx.scene.input.MouseEvent;

public class Point {
    public double x, y;
    public Point() {}

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
