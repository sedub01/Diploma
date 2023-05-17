package root.controllers;

import root.gui.BilliardBallPane;
import root.utils.Logger;
import root.utils.Point;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

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
        mSecondBallPane = new BilliardBallPane(secondBall, 135, 5, 1.8);

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
        final var secondAngleSpinner = new Spinner<Double>();
        final var firstInitSpeedText = new TextField();
        final var secondInitSpeedText = new TextField();
        final var firstBallMassText = new TextField();
        final var secondBallMassText = new TextField();
        final var firstClashSpeedText = new TextField();
        final var secondClashSpeedText = new TextField();
        final var firstMomentumText = new TextField();
        final var secondMomentumText = new TextField();

        mModelSettings.put(new Label("Угол наклона 1 шара"), firstAngleSpinner);
        mModelSettings.put(new Label("Угол наклона 2 шара"), secondAngleSpinner);
        mModelSettings.put(new Label("Начальная скорость 1 шара [м/с]"), firstInitSpeedText);
        mModelSettings.put(new Label("Начальная скорость 2 шара [м/с]"), secondInitSpeedText);
        mModelSettings.put(new Label("Масса 1 шара [кг]"), firstBallMassText);
        mModelSettings.put(new Label("Масса 2 шара [кг]"), secondBallMassText);
        mModelSettings.put(new Label("Скорость 1 шара после столкновения [м/с]"), firstClashSpeedText);
        mModelSettings.put(new Label("Скорость 2 шара после столкновения [м/с]"), secondClashSpeedText);
        mModelSettings.put(new Label("Импульс 1 шара [кг*м/с]"), firstMomentumText);
        mModelSettings.put(new Label("Импульс 2 шара [кг*м/с]"), secondMomentumText);

        mFirstBallPane.setAngleSpinnerSettings(firstAngleSpinner);
        mSecondBallPane.setAngleSpinnerSettings(secondAngleSpinner);

        bidirectionalBinding(firstInitSpeedText, mFirstBallPane.initVelocityProperty(), false);
        bidirectionalBinding(secondInitSpeedText, mSecondBallPane.initVelocityProperty(), false);
        bidirectionalBinding(firstBallMassText, mFirstBallPane.ballWeightProperty(), false);
        bidirectionalBinding(secondBallMassText, mSecondBallPane.ballWeightProperty(), false);
        bidirectionalBinding(firstClashSpeedText, mFirstBallPane.clashVelocityProperty());
        bidirectionalBinding(secondClashSpeedText, mSecondBallPane.clashVelocityProperty());
        bidirectionalBinding(firstMomentumText, mFirstBallPane.momentumProperty());
        bidirectionalBinding(secondMomentumText, mSecondBallPane.momentumProperty());
    }

    @Override
    public void execute() {
        Logger.log("Началось...");
        var execProp = mPropertiesMap.get("execButtonProperty");
        var expandProp = mPropertiesMap.get("expandButtonProperty");
        final var firstPath = mFirstBallPane.getPath();
        final var secondPath = mSecondBallPane.getPath();
        firstPath.setVisible(false);
        secondPath.setVisible(false);
        execProp.set(true);
        expandProp.set(true);
        //TODO сделать перемещение двух шаров
    }

    private double f(double i, double startX, double startY, double endX, double endY){
        double k = (endY - startY)/(endX - startX);
        double b = (startY - endY)*startX/(endX - startX) + startY;
        return k*i + b;
    }
}
