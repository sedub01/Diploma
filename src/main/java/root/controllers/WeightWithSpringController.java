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
import root.utils.Point;
import root.utils.Constants;

/** Модель, описывающая зависимость растяжения пружины от силы упругости */
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
    /**Начальная высота груза */
    private double initY;
    /** Величина в пикселах, на которую изображение груза
     * может накладываться на изображение пружины
     */
    private int springLambda;
    /** */
    private double weightY;

    /** Вес груза в килограммах */
    private DoubleProperty mCargoWeight;
    /** Жёсткость пружины */
    private IntegerProperty mStiffness;
    /** Прилагаемая сила */
    private DoubleProperty mManualPower;
    /** Удлинение пружины от груза в метрах */
    private DoubleProperty mWeightExtension;
    /** Удлинение пружины усилиями пользователя в метрах */
    private DoubleProperty mManualExtension;
    /** Итоговое удлинение пружины в метрах */
    private DoubleProperty mTotalSpringExtension;
    /** Количество пикселей на один метр (1 у.е. = 5 см = 0.05 м)*/
    private final double pixelsPerMeter = Constants.PIXELS_PER_UNIT/0.05;

    @Override
    protected void construct() {
        hbox.minWidthProperty().bind(hbox.getScene().widthProperty());

        trapezoid.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);
        trapezoid.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::objectDragged);

        initY = trapezoid.getLayoutY();
        springLambda = (int)(spring.getFitHeight() - initY);

        mCargoWeight = new SimpleDoubleProperty(this, "cargoWeight", 0.76);
        mStiffness = new SimpleIntegerProperty(this, "stiffness", 300);
        mManualPower = new SimpleDoubleProperty(this, "manualPower");
        mManualExtension = new SimpleDoubleProperty(this, "manualExtension");
        final double weightExtension = mCargoWeight.get() * Constants.g / mStiffness.get();
        mWeightExtension = new SimpleDoubleProperty(this, "weightExtension", weightExtension);
        mTotalSpringExtension = new SimpleDoubleProperty(this, "springExtension", weightExtension);
        weightY = initY + weightExtension*pixelsPerMeter;
        
        calculate();
    }

    //Есть два вида растяжения: растяжение от груза и растяжение от приложенной силы
    //Вторую можно применять только в objectDragged, а в остальных случаях она будет равна 0
    //В текущей версии программы растяжение считается за этим методом
    //TODO посмотреть, можно ли два вида растяжений считать в одном методе
    private void calculate() {
        //Это - расширение в метрах
        // final double extensionInPX = (mSpringExtension.get())*Constants.PIXELS_PER_UNIT*20;
        // trapezoid.setLayoutY(initY + extensionInPX);

        mTotalSpringExtension.set(mManualExtension.get() + mWeightExtension.get());
        final double weightExtensionInPX = mTotalSpringExtension.get()*pixelsPerMeter;
        trapezoid.setLayoutY(initY + weightExtensionInPX);
        spring.setFitHeight(springLambda + trapezoid.getLayoutY());
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
        bidirectionalBinding(springExtensionText, mTotalSpringExtension);

        weightText.focusedProperty().addListener((obs, oldV, newV)->{
            if (oldV){ //если фокус убран, меняем значение
                final double weightExtension = mCargoWeight.get() * Constants.g / mStiffness.get();
                mWeightExtension.set(weightExtension);
                weightY = initY + weightExtension*pixelsPerMeter;
                mManualPower.set(0);
                mManualExtension.set(0);

                calculate();
            }
        });
    }

    private void setMouse(MouseEvent event) {
        mStartP.setCoord(event);
    }

    private void objectDragged(MouseEvent event) {
        final double endY = event.getSceneY();
        final double dy = trapezoid.getLayoutY() - mStartP.y;
        if (endY + dy > initY){
            trapezoid.setLayoutY(endY + dy);
            //Есть два способа, как может растягиваться пружина: либо через ввод настроек (то же самое при иниц.),
            //либо при перетаскивании предмета
            //А итоговое растяжение получается из суммы двух растяжений (и, получается, две проперти)
            final double manualExtension = (trapezoid.getLayoutY() - weightY)/ pixelsPerMeter;
            mManualExtension.set(manualExtension);
            calculate();
            final double F = mManualExtension.get() * mStiffness.get();
            mManualPower.set(F);
        }
        
        setMouse(event);
    }
}
