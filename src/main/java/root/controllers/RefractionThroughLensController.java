package root.controllers;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import root.gui.PolygonArrow;
import root.utils.Constants;
import root.utils.LineWithOdds;
import root.utils.Logger;
import root.utils.Point;

public class RefractionThroughLensController extends AbstractModelController {
    @FXML
    private Pane anchorPane;
    @FXML
    private Line OXline;
    @FXML
    private Line lensLine;

    /** Координаты зажатой ЛКМ */
    private final Point mStartP = new Point();
    private Polygon mPolygonArrow;
    private Line mFromObjectToLensLine;
    private LineWithOdds mFromLensToFocusLine;
    private LineWithOdds mFromObjectToOXYLine;

    // Изначальное значение - двойное фокусное расстояние
    private DoubleProperty mLensDistance;
    private IntegerProperty mPartOfOriginalSize;

    @Override
    protected void construct() {
        mPolygonArrow = new PolygonArrow(Color.RED);
        anchorPane.getChildren().add(mPolygonArrow);
        mPolygonArrow.setLayoutX(3 * Constants.PIXELS_PER_UNIT); // - половина ширины
        mPolygonArrow.setLayoutY(3 * Constants.PIXELS_PER_UNIT - 10);

        OXline.endXProperty().bind(anchorPane.widthProperty()/* .divide(2) */);
        lensLine.endYProperty().bind(lensLine.getScene().heightProperty());
        lensLine.setLayoutX(5 * Constants.PIXELS_PER_UNIT);

        mFromObjectToLensLine = new Line(mPolygonArrow.getLayoutX(), mPolygonArrow.getLayoutY(),
                lensLine.getLayoutX(), mPolygonArrow.getLayoutY());
        mFromObjectToLensLine.setStroke(Color.rgb(130, 130, 130));
        mFromLensToFocusLine = new LineWithOdds(lensLine.getLayoutX(), mPolygonArrow.getLayoutY(),
                lensLine.getLayoutX() + Constants.PIXELS_PER_UNIT, OXline.getLayoutY());
        mFromLensToFocusLine.setStroke(Color.rgb(130, 130, 130));
        mFromObjectToOXYLine = new LineWithOdds(mPolygonArrow.getLayoutX(), mPolygonArrow.getLayoutY(),
                lensLine.getLayoutX(), OXline.getLayoutY());
        mFromObjectToOXYLine.setStroke(Color.rgb(130, 130, 130));

        anchorPane.getChildren().add(mFromObjectToLensLine);
        anchorPane.getChildren().add(mFromLensToFocusLine);
        anchorPane.getChildren().add(mFromObjectToOXYLine);

        mLensDistance = new SimpleDoubleProperty(this, "lensDistance", 0.2);
        mPartOfOriginalSize = new SimpleIntegerProperty(this, "partOfOriginalSize", 100);
        
        calculate();

        mPolygonArrow.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);
        mPolygonArrow.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::arrowDragged);
    }

    @Override
    protected void createSettings() {
        final var lensDistanceText = new TextField();
        final var partOfOriginalSizeText = new TextField();

        mModelSettings.put(new Label("Расстояние от линзы [м]"), lensDistanceText);
        mModelSettings.put(new Label("Доля от изначального размера [%]"), partOfOriginalSizeText);

        bidirectionalBinding(lensDistanceText, mLensDistance, false);
        bidirectionalBinding(partOfOriginalSizeText, mPartOfOriginalSize);

        //TODO доделать модуль, поменять координаты стрелки, чтобы нигде не добавлять +-30
        lensDistanceText.focusedProperty().addListener((obs, oldV, newV)->{
            if (oldV){ //если фокус убран, меняем значение
                final double dist = mLensDistance.get()*Constants.PIXELS_PER_UNIT*10;
                final double startX = mFromObjectToLensLine.getEndX() - dist;
                mPolygonArrow.setLayoutX(startX); // половина ширины
                //Начать выделять метод отсюда
                calculate();
            }
        });
    }

    private void setMouse(MouseEvent event) {
        mStartP.setCoord(event);
    }

    private void arrowDragged(MouseEvent event) {
        final double endX = event.getSceneX();
        final double dx = mPolygonArrow.getLayoutX() - mStartP.x;
        if (endX + dx < lensLine.getLayoutX() - 1) { // Половина ширины
            mPolygonArrow.setLayoutX(endX + dx);
            calculate();
        }

        setMouse(event);
    }

    private void calculate() {
        mFromObjectToLensLine.setStartX(mPolygonArrow.getLayoutX());
        mFromObjectToOXYLine.setStartX(mPolygonArrow.getLayoutX());
        mFromObjectToOXYLine.setEndX(lensLine.getLayoutX());
        mFromObjectToOXYLine.setEndY(OXline.getLayoutY());

        final Point p = LineWithOdds.getIntersectionPoint(mFromLensToFocusLine, mFromObjectToOXYLine);
        mFromLensToFocusLine.setEndX(p.x);
        mFromLensToFocusLine.setEndY(p.y);
        mFromObjectToOXYLine.setEndX(p.x);
        mFromObjectToOXYLine.setEndY(p.y);

        final double distance = (mFromObjectToLensLine.getEndX() - mFromObjectToLensLine.getStartX()) * 0.1;
        mLensDistance.set(distance / Constants.PIXELS_PER_UNIT);
        final double originalHeight = OXline.getLayoutY() - mFromObjectToLensLine.getEndY();
        final double distortedHeight = p.y - OXline.getLayoutY();
        final int percentage = (int) (distortedHeight / originalHeight * 100);
        mPartOfOriginalSize.set(percentage);
    }
}
