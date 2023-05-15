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
    @FXML private Circle firstBall;
    @FXML private Circle secondBall;
    @FXML private Pane anchorPane;
    @FXML private Pane firstBallPane;
    @FXML private Pane secondBallPane;

    private final Point mFirstStartP = new Point();
    private final Point mSecondStartP = new Point();

    private BilliardBallPane mFirstArrow;
    private BilliardBallPane mSecondArrow;

    @Override
    protected void construct() {
        Logger.log("Загрузилась модель инерции");

        firstBall.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);
        secondBall.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);

        firstBall.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::ballDragged);
        secondBall.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::ballDragged);

        mFirstArrow = new BilliardBallPane(firstBall, 45, 5, 1.2);
        mSecondArrow = new BilliardBallPane(secondBall, 135, 7, 1.8);
    }

    private void setMouse(final MouseEvent e){
        Circle ball = (Circle) e.getSource();
        (ball == firstBall? mFirstStartP: mSecondStartP).setCoord(e);
    }
    private void ballDragged(final MouseEvent event){
        final Circle ball = (Circle) event.getSource();
        final double endX = event.getSceneX();
        final double endY = event.getSceneY();
        final var startP = ball == firstBall? mFirstStartP: mSecondStartP;
        final var ballPane = (Pane) ball.getParent();

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

        mModelSettings.put(new Label("Угол наклона 1 шара"), firstAngleSpinner);
        mModelSettings.put(new Label("Угол наклона 2 шара"), secondAngleSpinner);
        mModelSettings.put(new Label("Начальная скорость 1 шара"), firstInitSpeedText);
        mModelSettings.put(new Label("Начальная скорость 2 шара"), secondInitSpeedText);
        mModelSettings.put(new Label("Масса 1 шара"), firstBallMassText);
        mModelSettings.put(new Label("Масса 2 шара"), secondBallMassText);
        mModelSettings.put(new Label("Скорость 1 шара после столкновения"), firstClashSpeedText);
        mModelSettings.put(new Label("Скорость 2 шара после столкновения"), secondClashSpeedText);

        mFirstArrow.setAngleSpinnerSettings(firstAngleSpinner);
        mSecondArrow.setAngleSpinnerSettings(secondAngleSpinner);

        bidirectionalBinding(firstInitSpeedText, mFirstArrow.initVelocityProperty(), false);
        bidirectionalBinding(secondInitSpeedText, mSecondArrow.initVelocityProperty(), false);
        bidirectionalBinding(firstBallMassText, mFirstArrow.ballWeightProperty(), false);
        bidirectionalBinding(secondBallMassText, mSecondArrow.ballWeightProperty(), false);
    }

    @Override
    public void execute() {
        Logger.log("Началось...");
    }
}
