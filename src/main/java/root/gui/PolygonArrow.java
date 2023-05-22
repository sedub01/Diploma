package root.gui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class PolygonArrow extends Polygon {
    public PolygonArrow(Color color){
        this.setFill(color);
        this.setStrokeWidth(2);
        this.setStroke(Color.BLACK);

        var list = getPoints();
        list.addAll(-30.0, 30.0); // коорд. наконечника стрелки
        list.addAll(0.0, 0.0);
        list.addAll(30.0, 30.0);
        list.addAll(10.0, 30.0);
        list.addAll(10.0, 80.0);
        list.addAll(-10.0, 80.0);
        list.addAll(-10.0, 30.0);

        //Вариант отображения горизонтальной стрелки
        // list.addAll(80.0, 30.0);
        // list.addAll(50.0, 60.0);
        // list.addAll(50.0, 40.0);
        // list.addAll(0.0, 40.0);
        // list.addAll(0.0, 20.0);
        // list.addAll(50.0, 20.0);
        // list.addAll(50.0, 0.0);
    }
}
