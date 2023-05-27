package root.controllers;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Transform;
import root.gui.BilliardBallPane;
import root.utils.Logger;
import root.utils.Point;

//TODO переделать модель, значительно упростив ее
//Демонстрация следующая: есть шар слева, он может удариться о шар справа
//Если шары не пересекаются, выводится предупреждение, иначе шар начинается двигаться
//После соударения рисуется отрезок, соединяющий центры шаров, и от этой линии считаются углы отклонения
public class BilliardballController extends AbstractModelController {
    @FXML
    private Circle firstBall;
    @FXML
    private Circle secondBall;
    @FXML
    private Pane anchorPane;
    @FXML
    private Pane firstBallPane;
    @FXML
    private Pane secondBallPane;

    /** Координаты зажатой ЛКМ первого шара*/
    private final Point mFirstStartP = new Point();
    /** Координаты зажатой ЛКМ второго шара*/
    private final Point mSecondStartP = new Point();

    private BilliardBallPane mFirstBallPane;
    private BilliardBallPane mSecondBallPane;

    @Override
    protected void construct() {
        Logger.log("Загрузилась модель инерции");

        firstBall.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);
        secondBall.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);

        firstBall.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::ballDragged);
        secondBall.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::ballDragged);

        mFirstBallPane = new BilliardBallPane(firstBall, 45, 7, 1.2);
        mSecondBallPane = new BilliardBallPane(secondBall, 1.8);

        //TODO оставить только массу второго шара
        mFirstBallPane.setAnotherBallProperties(mSecondBallPane.initVelocityProperty(),
                mSecondBallPane.ballWeightProperty(),
                mSecondBallPane.angleProperty());
        mSecondBallPane.setAnotherBallProperties(mFirstBallPane.initVelocityProperty(),
                mFirstBallPane.ballWeightProperty(),
                mFirstBallPane.angleProperty());
        mFirstBallPane.calculateSpeed();
        mSecondBallPane.calculateSpeed();
    }

    private void setMouse(final MouseEvent e) {
        Circle ball = (Circle) e.getSource();
        (ball == firstBall ? mFirstStartP : mSecondStartP).setCoord(e);
    }

    private void ballDragged(final MouseEvent event) {
        final Circle ball = (Circle) event.getSource();
        final double endX = event.getSceneX();
        final double endY = event.getSceneY();
        final var startP = ball == firstBall ? mFirstStartP : mSecondStartP;
        final var ballPane = (Pane) ball.getParent();

        // startP - это не координата центра, а просто координата нажатия мыши"
        // Logger.log(ball.getCenterX(), startP.x);

        // double dx = ball.getCenterX() - startP.x; //не для панели, а для круга
        // ball.setCenterX(endX + dx);
        final double dx = ballPane.getLayoutX() - startP.x;
        final double dy = ballPane.getLayoutY() - startP.y;
        ballPane.setLayoutX(endX + dx);
        ballPane.setLayoutY(endY + dy);

        setMouse(event);
    }

    @Override
    protected void createSettings() {
        final var firstAngleSpinner = new Spinner<Double>();
        final var firstInitSpeedText = new TextField();
        final var firstBallMassText = new TextField();
        final var secondBallMassText = new TextField();
        final var firstClashSpeedText = new TextField();
        final var secondClashSpeedText = new TextField();

        mModelSettings.put(new Label("Угол наклона 1 шара"), firstAngleSpinner);
        mModelSettings.put(new Label("Начальная скорость 1 шара [м/с]"), firstInitSpeedText);
        mModelSettings.put(new Label("Масса 1 шара [кг]"), firstBallMassText);
        mModelSettings.put(new Label("Масса 2 шара [кг]"), secondBallMassText);
        mModelSettings.put(new Label("Скорость 1 шара после столкновения [м/с]"), firstClashSpeedText);
        mModelSettings.put(new Label("Скорость 2 шара после столкновения [м/с]"), secondClashSpeedText);

        mFirstBallPane.setAngleSpinnerSettings(firstAngleSpinner);

        bidirectionalBinding(firstInitSpeedText, mFirstBallPane.initVelocityProperty(), false);
        bidirectionalBinding(firstBallMassText, mFirstBallPane.ballWeightProperty(), false);
        bidirectionalBinding(secondBallMassText, mSecondBallPane.ballWeightProperty(), false);
        bidirectionalBinding(firstClashSpeedText, mFirstBallPane.clashVelocityProperty());
        bidirectionalBinding(secondClashSpeedText, mSecondBallPane.clashVelocityProperty());

        firstInitSpeedText.focusedProperty().addListener((obs, oldV, newV)->{
            if (oldV){ //если фокус убран, меняем значение
                mFirstBallPane.calculateSpeed();
                mSecondBallPane.calculateSpeed();
            }
        });
    }

    @Override
    public void execute() {
        Logger.log("Началось...");
        //TODO мб надо передавать не путь, а сам объект, а 
        //потом в нем использовать bounds, и по ним вычислить линию?
        // if (!hasRoots(mFirstBallPane, secondBall)){
        //     Logger.displayOnAlertWindow("Шар не сможет столкнуться с целью");
        //     return;
        // }

        var execProp = mPropertiesMap.get("execButtonProperty");
        var expandProp = mPropertiesMap.get("expandButtonProperty");
        mFirstBallPane.executionStarted();
        mSecondBallPane.executionStarted();
        execProp.set(true);
        expandProp.set(true);
        // //TODO сделать перемещение двух шаров
        // final Bounds firstBallBounds = firstBallPane.localToScene(firstBallPane.getBoundsInLocal());
        // Logger.log(firstBallBounds.getCenterX(), firstBallBounds.getCenterY());
        TranslateTransition trans = new TranslateTransition();
        trans.setNode(firstBall);
        trans.setByX(100);
        trans.setByY(100*mFirstBallPane.getPath().getK() + mFirstBallPane.getPath().getB());
        trans.setInterpolator(Interpolator.LINEAR);

        trans.play();
    }

    //Проблема в том, что т.к. линия находится внутри Pane, k == 0, а 
    //вместо линии вращается Pane
    //TODO посмотреть, можно ли пересечь круг линией по абсолютным координатам 
    // (переместить линию "выше")
    // private boolean hasRoots(BilliardBallPane mFirstBallPane2, Circle ball) {
    //     //3231
    //     final var lineBounds = firstBallPane.localToScene(firstBallPane.getBoundsInLocal());
    //     final var sbBounds = secondBallPane.localToScene(secondBallPane.getBoundsInLocal());
    //     Logger.log(lineBounds);
    //     Logger.log(sbBounds);
    //     Logger.log("intersects:", lineBounds.intersects(sbBounds));
    //     Logger.log("secondMetric:", firstBallPane.getBoundsInParent().intersects(secondBallPane.getBoundsInParent()));

    //     final Bounds ballBounds = ball.localToScene(ball.getBoundsInLocal());
    //     final double k = mFirstBallPane2.getPath().getK();
    //     final double b = mFirstBallPane2.getPath().getB();
    //     final double x0 = ballBounds.getCenterX();
    //     final double y0 = ballBounds.getCenterY();
    //     final double r = ball.getRadius();
    //     final double D = Math.pow(2*k*(b-y0) - 2*x0, 2) - 4*(k*k+1)*(x0*x0 + Math.pow(b-y0, 2) - r*r);
    //     return D >= 0;
    // }
}
