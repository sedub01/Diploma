package root.utils;

import javafx.scene.input.MouseEvent;

public class Point {
    public double x, y;
    public Point() {}
    public Point(double x1, double y1){
        x = x1;
        y = y1;
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
