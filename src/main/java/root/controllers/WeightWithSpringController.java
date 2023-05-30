package root.controllers;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import root.utils.Point;
import root.utils.Constants;

//Есть два вида растяжения: растяжение от груза и растяжение от приложенной силы
//Второе можно применять только при перетягивании груза зажатой мышью
//Растяжение от груза задается через ввод настроек,
//ручное растяжение - при перетаскивании предмета
//А итоговое растяжение получается из суммы двух растяжений
//Ручное удлинение считается как разница между положением груза при перемещении
//и при расположении без перетягивания, деленная на количество пикселей на один метр
/** Контроллер модели, описывающей зависимость растяжения пружины от силы упругости */
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
    private int mInitY;
    /** Величина в пикселах, на которую изображение груза
     * может накладываться на изображение пружины */
    private int mSpringLambda;
    /** Положение по оси Y только при удлинении весом груза*/
    private int mWeightY;
    /** Вес груза в килограммах */
    private DoubleProperty mCargoWeight;
    /** Жёсткость пружины */
    private IntegerProperty mStiffness;
    /** Прилагаемая сила в Ньютонах*/
    private DoubleProperty mManualPower;
    /** Удлинение пружины от груза в метрах */
    private DoubleProperty mWeightExtension;
    /** Удлинение пружины усилиями пользователя в метрах */
    private DoubleProperty mManualExtension;
    /** Итоговое удлинение пружины в метрах */
    private DoubleProperty mTotalSpringExtension;
    /** Количество пикселей на один метр (1 у.е. = 5 см = 0.05 м)*/
    private final int pixelsPerMeter = (int)(Constants.PIXELS_PER_UNIT/0.05);

    @Override
    protected void construct() {
        hbox.minWidthProperty().bind(hbox.getScene().widthProperty());

        mInitY = (int)trapezoid.getLayoutY();
        mSpringLambda = (int)(spring.getFitHeight() - mInitY);

        mCargoWeight = new SimpleDoubleProperty(this, "cargoWeight", 1.52);
        mStiffness = new SimpleIntegerProperty(this, "stiffness", 300);
        mManualPower = new SimpleDoubleProperty(this, "manualPower");
        mManualExtension = new SimpleDoubleProperty(this, "manualExtension");
        mWeightExtension = new SimpleDoubleProperty(this, "weightExtension");
        mTotalSpringExtension = new SimpleDoubleProperty(this, "springExtension");
        
        calculate();

        trapezoid.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);
        trapezoid.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::objectDragged);
        trapezoid.addEventHandler(MouseEvent.MOUSE_RELEASED, this::objectReleased);
    }

    //Ручное удлинение задается до вызова этого метода
    /* Метод для расчета координат объектов модели и их параметров */
    private void calculate() {
        final double weightExtension = mCargoWeight.get() * Constants.g / mStiffness.get();
        mWeightExtension.set(weightExtension);
        mWeightY = (int)(mInitY + mWeightExtension.get()*pixelsPerMeter);
        final double manualPower = mManualExtension.get() * mStiffness.get();
        mManualPower.set(manualPower);
        mTotalSpringExtension.set(mManualExtension.get() + mWeightExtension.get());
        final double totalExtensionInPX = mTotalSpringExtension.get()*pixelsPerMeter;
        trapezoid.setLayoutY(mInitY + totalExtensionInPX);
        spring.setFitHeight(mSpringLambda + trapezoid.getLayoutY());
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
                mManualExtension.set(0);
                calculate();
            }
        });
        weightText.setOnKeyPressed(e->{
            if (e.getCode().equals(KeyCode.ENTER)){
                mManualExtension.set(0);
                calculate();
            }
        });
        springStiffnessText.focusedProperty().addListener((obs, oldV, newV)->{
            if (oldV){ //если фокус убран, меняем значение
                mManualExtension.set(0);
                calculate();
            }
        });
        springStiffnessText.setOnKeyPressed(e->{
            if (e.getCode().equals(KeyCode.ENTER)){
                mManualExtension.set(0);
                calculate();
            }
        });
    }

    private void setMouse(MouseEvent event) {
        mStartP.setCoord(event);
    }

    /** Обработчик перетаскивания груза */
    private void objectDragged(MouseEvent event) {
        final double endY = event.getSceneY();
        final double dy = trapezoid.getLayoutY() - mStartP.y;
        final double finalY = endY + dy;
        if (finalY > mInitY){
            mManualExtension.set((finalY - mWeightY)/ pixelsPerMeter);
            calculate();
        }
        
        setMouse(event);
    }

    //afterInvest: устранить баг, связанный с экстренным выбросом исключения,
    //а также корректное отклонение пружины (не для диплома)
    /** Обработчик перемещения груза после разжатия мыши*/
    // Алгоритм следующий: после разжатия мыши груз начинает двигаться в
    // противоположном направлении до рассчитаной величины, затем двигается в
    // противоположную сторону, и так до тех пор, пока
    // расстояние перемещения не становится слишком маленьким
    private void objectReleased(MouseEvent event) {
        final double manualExtensionInPX = mManualExtension.get()*pixelsPerMeter*0.5;
        final double tempDeltaY = manualExtensionInPX > mWeightY-mInitY? mWeightY-mInitY: manualExtensionInPX;
        //Итоговое растяжение в пикселах за одну итерацию (которое будет меняться в другом потоке)
        final double pxPath = mManualExtension.get()*pixelsPerMeter + tempDeltaY;
        //Текущая скорость - 150 px/сек
        final double timeForT = 150; //В милисекундах

        Thread motionThread = new Thread(() -> {
            // Дистанция в пискелах, которую надо пройти
            double pixelPath = Math.abs(pxPath);
            // Если <0, груз движется вверх, иначе вниз
            int deltaP = pxPath < 0 ? 1 : -1;
            final int pixelLambda = 2;
            while (pixelPath > pixelLambda) {
                // Груз должен идти вверх в два раза быстрее, чем вниз
                int speedTime = (int) (deltaP < 0 ? timeForT / 2 : timeForT);
                // Время, на которое должен прерываться поток
                final int deltaT = (int)(speedTime / pixelPath);
                // Координата Y, по которой груз перестает двигаться и начинает двигаться в
                // другую сторону
                final double cancelY = trapezoid.getLayoutY() + pixelPath * deltaP;
                for (int i = 0; Math.abs(trapezoid.getLayoutY() - cancelY) > 1; i++) {
                    // Координата Y на след. итерации
                    final double deltaY = trapezoid.getLayoutY() + deltaP;
                    trapezoid.setLayoutY(deltaY);
                    spring.setFitHeight(deltaY + mSpringLambda);
                    try {
                        Thread.sleep(deltaT == 0 ? i % 2 :deltaT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                pixelPath = Math.abs(mWeightY - trapezoid.getLayoutY()) * 1.5;
                deltaP *= -1;
            }
            trapezoid.setLayoutY(mWeightY);
            spring.setFitHeight(mWeightY + mSpringLambda);
            mManualPower.set(0);
            mTotalSpringExtension.set(mWeightExtension.get());
        });
        motionThread.start();
    }
}
