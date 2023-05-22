package root.controllers;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import root.utils.Logger;
import root.utils.Point;
import root.utils.Constants;

public class WeightWithSpringController extends AbstractModelController {
    @FXML
    private Pane topPane;
    @FXML 
    private Polygon trapezoid;
    @FXML
    private ImageView spring;
    @FXML
    private Pane hbox;

    /** Координаты зажатой ЛКМ*/
    private final Point mStartP = new Point();

    private double initY;
    private double initHeight;

    private DoubleProperty mCargoWeight;
    private IntegerProperty mStiffness;
    private DoubleProperty mManualPower;
    private DoubleProperty mSpringExtension;

    @Override
    protected void construct() {
        hbox.minWidthProperty().bind(hbox.getScene().widthProperty());

        trapezoid.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);
        trapezoid.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::objectDragged);

        initY = trapezoid.getLayoutY();
        initHeight = spring.getFitHeight();

        mCargoWeight = new SimpleDoubleProperty(this, "cargoWeight", 0.76);
        mStiffness = new SimpleIntegerProperty(this, "stiffness", 300);
        mManualPower = new SimpleDoubleProperty(this, "manualPower");
        final double startExtension = mCargoWeight.get() * Constants.g / mStiffness.get();
        mSpringExtension = new SimpleDoubleProperty(this, "springExtension", startExtension);

        final double extensionInPX = (mSpringExtension.get())*Constants.PIXELS_PER_UNIT*20;
        trapezoid.setLayoutY(initY + extensionInPX);
        calculate();
    }

    private void calculate() {
        //Это - расширение в метрах
        // final double extensionInPX = (mSpringExtension.get())*Constants.PIXELS_PER_UNIT*20;
        // trapezoid.setLayoutY(initY + extensionInPX);
        double changeY = trapezoid.getLayoutY() - initY; 
        spring.setFitHeight(initHeight + changeY);
    }

    @Override
    protected void createSettings() {
        final var weightText = new TextField();
        final var springStiffnessText = new TextField();
        final var manualPowerText = new TextField();
        final var springExtensionText = new TextField();

        mModelSettings.put(new Label("Жёсткость пружины [Н/м]"), springStiffnessText);
        mModelSettings.put(new Label("Масса груза [кг]"), weightText);
        mModelSettings.put(new Label("Приложенная сила [Н]"), manualPowerText);
        mModelSettings.put(new Label("Удлинение пружины [м]"), springExtensionText);

        bidirectionalBinding(springStiffnessText, mStiffness, false);
        bidirectionalBinding(weightText, mCargoWeight, false);
        bidirectionalBinding(manualPowerText, mManualPower);
        bidirectionalBinding(springExtensionText, mSpringExtension);

        weightText.focusedProperty().addListener((obs, oldV, newV)->{
            if (oldV){ //если фокус убран, меняем значение
                final double startExtension = mCargoWeight.get() * Constants.g / mStiffness.get();
                mSpringExtension.set(startExtension);
                trapezoid.setLayoutY(startExtension*Constants.PIXELS_PER_UNIT*20 + initY);
                double changeY = trapezoid.getLayoutY() - initY; 
                spring.setFitHeight(initHeight + changeY);
            }
        });
    }

    private void setMouse(MouseEvent event) {
        mStartP.setCoord(event);
    }

    private void objectDragged(MouseEvent event) {
        final double endY = event.getSceneY();
        final double dy = trapezoid.getLayoutY() - mStartP.y;
        if (endY + dy > initY) {
            trapezoid.setLayoutY(endY + dy);
            // TODO продумать, как менять размер пружины
            double changeY = trapezoid.getLayoutY() - initY; 
            spring.setFitHeight(initHeight + changeY);
            

            final double startExtension = changeY / Constants.PIXELS_PER_UNIT / 20;
            mSpringExtension.set(startExtension);
            final double F = startExtension * mStiffness.get();
            mManualPower.set(F);
            // calculate();
        }
        
        setMouse(event);
    }
}
