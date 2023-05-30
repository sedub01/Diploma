package root.controllers;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import root.gui.PolygonArrow;
import root.utils.Constants;
import root.utils.LineWithOdds;
import root.utils.Logger;
import root.utils.Point;

/** Контроллер модели, позволяющей исследовать закономерность между расстоянием до
 * линзы и размером отображающегося изображения
 */
public class RefractionThroughLensController extends AbstractModelController {
    @FXML
    private Pane anchorPane;
    @FXML
    private Line OXline;
    @FXML
    private Line lensLine;

    /** Координаты зажатой ЛКМ */
    private final Point mStartP = new Point();
    /** Объект полигональной стрелки-объекта */
    private Polygon mPolygonArrow;
    /** Объект полигональной стрелки-изображения */
    private PolygonArrow mReflectionArrow;
    /** Линия от объекта до линзы */
    private Line mFromObjectToLensLine;
    /** Линия от линзы до фокуса линзы */
    private LineWithOdds mFromLensToFocusLine;
    /** Линия от объекта до оптического центра линзы */
    private LineWithOdds mFromObjectToOXYLine;

    // Изначальное значение - двойное фокусное расстояние
    /** Расстояние до линзы в метрах */
    private DoubleProperty mLensDistance;
    /** Кешируемое значение mLensDistance */
    private Number oldLensDistanceValue;
    /** Оптическая сила линзы в диоптриях*/
    private DoubleProperty mOpticalPower;
    /** Доля в процентах от размера предмета */
    private IntegerProperty mPartOfOriginalSize;

    @Override
    protected void construct() {
        mPolygonArrow = new PolygonArrow(Color.RED);
        anchorPane.getChildren().add(mPolygonArrow);
        mPolygonArrow.setLayoutX(3 * Constants.PIXELS_PER_UNIT);
        mPolygonArrow.setLayoutY(3 * Constants.PIXELS_PER_UNIT - 10);
        mReflectionArrow = new PolygonArrow(Color.rgb(255, 0, 0, 0.25));
        mReflectionArrow.setMouseTransparent(true);
        anchorPane.getChildren().add(mReflectionArrow);

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
        mOpticalPower = new SimpleDoubleProperty(this, "opticalPower", 10);
        mPartOfOriginalSize = new SimpleIntegerProperty(this, "partOfOriginalSize", 100);
        
        calculate();

        mPolygonArrow.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);
        mPolygonArrow.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::arrowDragged);
    }

    @Override
    protected void createSettings() {
        final var lensDistanceText = new TextField();
        final var opticalPowerText = new TextField();
        final var partOfOriginalSizeText = new TextField();

        mModelSettings.put(new Label("Расстояние от линзы [м]"), lensDistanceText);
        mModelSettings.put(new Label("Оптическая сила линзы [Дптр]"), opticalPowerText);
        mModelSettings.put(new Label("Доля от изначального размера [%]"), partOfOriginalSizeText);

        bidirectionalBinding(lensDistanceText, mLensDistance, false);
        bidirectionalBinding(opticalPowerText, mOpticalPower, false);
        bidirectionalBinding(partOfOriginalSizeText, mPartOfOriginalSize);

        HandleTextInput handler = () -> {
            final double dist = mLensDistance.get() * Constants.PIXELS_PER_UNIT * mOpticalPower.get();
            final double startX = mFromObjectToLensLine.getEndX() - dist;
            final double focalDist = 4 * Constants.PIXELS_PER_UNIT;
            final int lambda = 5;
            if (Math.abs(startX - focalDist) < lambda) {
                mLensDistance.set((Double) oldLensDistanceValue);
                Logger.displayOnAlertWindow("Нельзя устанавливать объект на фокусном расстоянии");
            } else if (mLensDistance.get() <= 0) {
                mLensDistance.set((Double) oldLensDistanceValue);
                Logger.displayOnAlertWindow("Дистанция до линзы должна быть больше 0");
            }
            else {
                mPolygonArrow.setLayoutX(startX);
                calculate();
            }
        };

        lensDistanceText.focusedProperty().addListener((obs, oldV, newV)->{
            if (oldV){ //если фокус убран, меняем значение
                handler.handleTextInput();
            }
            else{
                oldLensDistanceValue = mLensDistance.get();
            }
        });
        lensDistanceText.setOnKeyPressed(e->{
            if (e.getCode().equals(KeyCode.ENTER)){
                handler.handleTextInput();
            }
        });
        opticalPowerText.focusedProperty().addListener((obs, oldV, newV)->{
            if (oldV){ //если фокус убран, меняем значение
                calculate();
            }
        });
        opticalPowerText.setOnKeyPressed(e->{
            if (e.getCode().equals(KeyCode.ENTER)){
                calculate();
            }
        });
    }

    /*Инструмент для метода обратного вызова */
    private interface HandleTextInput{
        public void handleTextInput();
    }

    private void setMouse(MouseEvent event) {
        mStartP.setCoord(event);
    }

    /** Обработчик перетаскивания предмета*/
    private void arrowDragged(MouseEvent event) {
        final double endX = event.getSceneX();
        //Если dx > 0, объект движется справа налево, иначе слева направо
        final double dx = mPolygonArrow.getLayoutX() - mStartP.x;
        final double layoutX = endX + dx;
        if (layoutX < lensLine.getLayoutX() - 1) {
            final int lambda = 5;
            final double focalDist = 4*Constants.PIXELS_PER_UNIT;
            //Если объект близок к фокусному расстоянию, то 
            //если dx>0, его надо сдвинуть левее на две лямбды, иначе
            //если dx<0, его надо сдвинуть правее на то же расстояние
            if (Math.abs(layoutX - focalDist) < lambda){
                if (dx > 0){
                    mPolygonArrow.setLayoutX(layoutX - 2*lambda);
                }
                else if (dx < 0){
                    mPolygonArrow.setLayoutX(layoutX + 2*lambda);
                }
            }
            else mPolygonArrow.setLayoutX(layoutX);
            calculate();
        }

        setMouse(event);
    }

    /* Метод для расчета координат объектов модели и их параметров */
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

        final double distance = (mFromObjectToLensLine.getEndX() - mFromObjectToLensLine.getStartX()) / mOpticalPower.get();
        mLensDistance.set(distance / Constants.PIXELS_PER_UNIT);
        final double originalHeight = OXline.getLayoutY() - mFromObjectToLensLine.getEndY();
        final double distortedHeight = p.y - OXline.getLayoutY();
        final double percentage = (distortedHeight / originalHeight);
        mPartOfOriginalSize.set((int)Math.abs(percentage*100));

        final int reflectionHeight = (int)Math.abs(originalHeight * percentage);
        mReflectionArrow.setHeight((int)reflectionHeight);
        mReflectionArrow.setLayoutX(p.x);
        //Если расстояние от линзы больше фокусного расстояния, тогда всё хорошо, иначе 
        //задать поворот стрелки в 0 градусов и задать другие координаты отображения
        final double focalDist = 4*Constants.PIXELS_PER_UNIT;
        if (mPolygonArrow.getLayoutX() - focalDist < 0){
            mReflectionArrow.setRotate(180);
            mReflectionArrow.setLayoutY(OXline.getLayoutY());
        }
        else{
            mReflectionArrow.setRotate(0);
            mReflectionArrow.setLayoutY(p.y);
        }
    }
}
