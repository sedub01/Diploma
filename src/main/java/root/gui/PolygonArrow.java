package root.gui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/** Представление объекта стрелки в виде полигональной линии */
public class PolygonArrow extends Polygon {
    /** Высота стрелки в пискелах*/
    private int mArrowHeight = 80;
    public PolygonArrow(Color color){
        this.setFill(color);
        this.setStrokeWidth(2);
        this.setStroke(Color.rgb(0, 0, 0, color.getOpacity()));

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

    public int getHeight(){
        return mArrowHeight;
    }

    //Нужно менять 5 и 6 координаты y (9 и 11 элементы)
    public void setHeight(int height){
        mArrowHeight = height;
        var list = getPoints();
        list.set(9, (double)height);
        list.set(11, (double)height);
    }
}
