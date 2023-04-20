package root.controllers;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import root.utils.Constants;
import root.utils.Logger;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import root.utils.Point;

/** Модель, анализирующая параболическое движение снаряда*/
public class CannonballController extends AbstractModelController {
    @FXML private ImageView barrel;
    @FXML private ImageView wheel;
    @FXML private ImageView floor;
    @FXML private BorderPane borderPane;
    @FXML private AnchorPane barrelPane;

    /** Координаты зажатой ЛКМ*/
    private final Point mStartP = new Point();
    /** Сущность для управления поворотом ствола*/
    private final Rotate mRotate = new Rotate();
    /** Объект, отвечающий за прорисовку кривой траектории снаряда*/
    private TrajectoryLine trLine;
    /** Точка, от которой начинается отсчет кривой траектории*/
    private final ImageView mTrackingPoint = new ImageView();
    /** Точка, совпадающая с точкой вращения*/
    private final ImageView mPivotPoint = new ImageView();
    /** Начался ли процесс бросания снаряда*/
    private boolean mIsTransitionStarted = false;
    /** Изображение снаряда*/
    private ImageView mProjectile;

    @Override
    protected void construct() {
        Logger.log("Загрузилась модель пушечного ядра");
        barrelPane.getTransforms().add(mRotate);
        //Нужно делать иниц. прямо здесь, т.к. до конструктора все @FXML ImageView == null
        trLine = new TrajectoryLine();
        mProjectile = getProjectileObject();

        //Определяем точку опоры
        mRotate.setPivotX(barrel.getFitWidth()/10);
        mRotate.setPivotY(barrel.getFitHeight()/2);

        setTrackingPoint(mTrackingPoint, true);
        setTrackingPoint(mPivotPoint, false);

        barrelPane.addEventHandler(MouseEvent.MOUSE_PRESSED, this::setMouse);
        // Когда край пушки перетаскивается, вращаем её
        barrelPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::barrelDragged);
        barrelPane.setOnMouseClicked(e->{
            if (e.getButton().equals(MouseButton.PRIMARY)){
                if (e.getClickCount() == 2 && !mIsTransitionStarted){
                    execute();
                }
            }
        });
    }

    /** Обработчик перетаскивания ствола*/
    private void barrelDragged(final MouseEvent event){
        if (event.getX() >= barrelPane.getWidth() / 3  && !mIsTransitionStarted){
            /*
             Используется для получения положения крайнего угла объекта на сцене
             Аффинное преобразование - это линейное, которое переводит объект из двумерного(трех-)
             пространство в другое двумерное пространство, сохраняя неизменной прямолинейность
             и параллельность линий. Одна точка А с координатой (x, y, z) в трехмерном пространстве
             переместится в позицию A' с координатой (x', y', z') путем матричного умножения
             https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Node.html#localToSceneTransformProperty
             https://o7planning.org/11157/javafx-transformation
            */
            final Transform localToScene = barrelPane.getLocalToSceneTransform();

            //конечное положение точки (на момент конца локального перемещения)
            //координаты берутся относительно берущегося предмета
            final double endX = event.getSceneX();
            final double endY = event.getSceneY();
            //Аффинные преобразования
            //Параметры tx, ty обозначают трансформированные точки для ствола
            final double px = mRotate.getPivotX() + localToScene.getTx();
            //Получает элемент преобразования координаты Y матрицы 3x4 + координаты т. поворота (она постоянна)
            final double py = mRotate.getPivotY() + localToScene.getTy();

            // Определение углов поворота
            final double th1 = clockAngle(mStartP.x - px, mStartP.y - py);
            final double th2 = clockAngle(endX - px, endY - py);

            final double angle = mRotate.getAngle() + th2 - th1;
            //для изменения положения только в первой четверти
            if (angle <= 0 && angle >= -90){
                mRotate.setAngle(angle);
            }
            setMouse(event);
        }
    }

    private void setMouse(final MouseEvent e){
        mStartP.setCoord(e);
    }

    /** Определение угла поворота по двум точкам */
    public double clockAngle(double dx, double dy) {
        double angle = Math.abs(Math.toDegrees(Math.atan2(dy, dx)));

        if (dy < 0) {
            angle = 360 - angle;
        }
        return angle;
    }

    /** Установка отслеживающих точек для расчета высоты*/
    private void setTrackingPoint(ImageView point, boolean isTracking) {
        final int SIZE = 1;
        final int w = (int)barrel.getFitWidth();
        point.setFitWidth(SIZE);
        point.setFitHeight(SIZE);
        point.setLayoutX(isTracking? w*0.9: mRotate.getPivotX());
        point.setLayoutY(mRotate.getPivotY());
        barrelPane.getChildren().add(point);
    }

    @Override
    protected void createSettings() {
        final var angleSpinner = new Spinner<Double>();
        final var speedText = new TextField();
        final var durationField = new TextField();
        final var distanceField = new TextField();
        final var heightField = new TextField();
        final var maxHeightField = new TextField();

        mModelSettings.put(new Label("Угол наклона ствола"), angleSpinner);
        mModelSettings.put(new Label("Начальная скорость снаряда [м/с]"), speedText);
        mModelSettings.put(new Label("Время полёта [с]"), durationField);
        mModelSettings.put(new Label("Дистанция [м]"), distanceField);
        mModelSettings.put(new Label("Изначальная высота [м]"), heightField);
        mModelSettings.put(new Label("Максимальная высота [м]"), maxHeightField);

        bidirectionalBinding(speedText, trLine.initVelocityProperty());
        speedText.setDisable(false);
        speedText.setText("5");
//Этот сеттер убрать не получится, поскольку в этом объекте mInitVelocity пока что не инициализирован
        trLine.initCurve(speedText.getText());
        bidirectionalBinding(durationField, trLine.durationProperty());
        bidirectionalBinding(distanceField, trLine.distanceProperty());
        bidirectionalBinding(heightField, trLine.initHeightProperty());
        bidirectionalBinding(maxHeightField, trLine.maxHeightProperty());
        speedText.focusedProperty().addListener((obs, oldV, newV)->{
            if (oldV){ //если фокус убран, меняем значение
                trLine.calculateTrajectory();
            }
        });
        speedText.setOnKeyPressed(e->{
            if (e.getCode().equals(KeyCode.ENTER)){
                trLine.calculateTrajectory();
            }
        });

        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 90, 0);
        angleSpinner.setValueFactory(valueFactory);
        angleSpinner.setEditable(true);
        mRotate.setOnTransformChanged(e-> {
            valueFactory.setValue(-mRotate.getAngle());
            trLine.calculateTrajectory();
        });
        valueFactory.valueProperty().addListener(e->
                mRotate.setAngle(-valueFactory.getValue()));
    }

    @Override
    public void execute() {
        var execProp = mPropertiesMap.get("execButtonProperty");
        var expandProp = mPropertiesMap.get("expandButtonProperty");
        final var path = trLine.getPath();
        path.setVisible(false);
        mProjectile.setVisible(true);
        execProp.set(true);
        expandProp.set(true);
        mIsTransitionStarted = true;
        PathTransition trans = new PathTransition(Duration.seconds(trLine.getDuration()), path, mProjectile);
        trans.setInterpolator(Interpolator.LINEAR);
        trans.setOnFinished(e->{
            mIsTransitionStarted = false;
            path.setVisible(true);
            execProp.set(false);
            expandProp.set(false);
            trLine.calculateTrajectory();
        });
        trans.play();
    }

    /** Получение объекта снаряда*/
    private ImageView getProjectileObject() {
        final var input = getClass().getResourceAsStream("/root/img/cannon/projectile.png");
        var projectile = input != null? new ImageView(new Image(input)): null;
        if (projectile != null) {
            projectile.setFitWidth(50);
            projectile.setFitHeight(50);
            projectile.setVisible(false);
            borderPane.getChildren().add(projectile);
        }
        return projectile;
    }

    /** Класс, отвечающий за прорисовку кривой траектории*/
    private class TrajectoryLine{
        /** Структура, хранящая передвижения кривой траектории */
        private final Path mPath = new Path();
        /** Кол-во пикселей на 1 метр */
        @SuppressWarnings("FieldCanBeLocal")
        private final int PIXELS_PER_METER = Constants.PIXELS_PER_UNIT;
        /** Частота обновления значений (чем меньше значение, тем более гладкая кривая)*/
        @SuppressWarnings("FieldCanBeLocal")
        private final double FREQUENCY = 0.005;
        /** Высота крепления ствола в метрах*/
        private final double HEIGHT = wheel.getFitHeight() / 2 / PIXELS_PER_METER;
        /** Изначальная скорость снаряда */
        private final IntegerProperty mInitVelocity;
        /** Разница между начальной позиции и текущей по оси Y (в пикселах)*/
        private double mInitHeight;
        /** Время полета снаряда в секундах*/
        private final DoubleProperty mTimeFlight;
        /** Дальность полета в метрах*/
        private final DoubleProperty mDistance;
        /** Итоговая высота с учетом наклона ствола в метрах*/
        private final DoubleProperty mTotalHeight;
        /** Максимальная высота в метрах*/
        private final DoubleProperty mMaxHeight;

        public TrajectoryLine(){
            mPath.setStroke(Color.RED);
            mPath.setStrokeWidth(3);
            borderPane.getChildren().add(mPath);
            mPath.getStrokeDashArray().addAll(10d, 10d);

            mInitVelocity = new SimpleIntegerProperty(this, "initVelocity");
            mTimeFlight = new SimpleDoubleProperty(this, "duration",
                    (Math.sqrt(2*Constants.g*HEIGHT))/Constants.g);
            mDistance = new SimpleDoubleProperty(this, "distance");
            mTotalHeight = new SimpleDoubleProperty(this, "totalHeight", HEIGHT);
            mMaxHeight = new SimpleDoubleProperty(this, "maxHeight", HEIGHT);

            Platform.runLater(()->{
                mInitHeight = mPivotPoint.localToScene(mPivotPoint.getBoundsInLocal()).getCenterY();
                Stage stage = (Stage)borderPane.getScene().getWindow();
//Т.к. значения GUI обновляются уже после максимизации сцены, а не во время нее, надо использовать runLater()
                stage.maximizedProperty().addListener((obs)-> Platform.runLater(()-> {
                    mProjectile.setVisible(false);
                    //эту переменную надо обновлять каждый раз при изменении размера окна
                    mInitHeight = mPivotPoint.localToScene(mPivotPoint.getBoundsInLocal()).getCenterY();
                    calculateTrajectory();
                }));
            });
        }

        //https://www.omnicalculator.com/physics/projectile-motion
        /** Метод для расчёта координат кривой траектории и ее дальнейшей прорисовки.
         *  Описание логики см. в конфиг. файле
         */
        public void calculateTrajectory() {
            final var pathElements = mPath.getElements();
            pathElements.clear();

            final double angle = -mRotate.getAngle();
            //Местонахождение точки начала траектории в пространстве сцены
            final Bounds bounds = mTrackingPoint.localToScene(mTrackingPoint.getBoundsInLocal());
            //Разница по оси Y между точкой крепления ствола и концом ствола
            final double dHeight = (mInitHeight - bounds.getCenterY()) / PIXELS_PER_METER;
            mTotalHeight.set(HEIGHT+dHeight);
            //Вертикальная составляющая скорости
            final double V_y = (double) mInitVelocity.get() * Math.sin(Math.toRadians(angle));
            //Горизонтальная составляющая скорости
            final double V_x = (double) mInitVelocity.get() * Math.cos(Math.toRadians(angle));
            mTimeFlight.set((V_y + Math.sqrt(Math.pow(V_y, 2) + 2*Constants.g*mTotalHeight.get()))/Constants.g);
            mDistance.set(V_x * mTimeFlight.get());
            mMaxHeight.set(mTotalHeight.get() + Math.pow(V_y, 2)/(2*Constants.g));

            //Смещение точки начала полета по оси X
            final double deltaX = bounds.getCenterX();
            //Смещение точки начала полета по оси Y
            final double deltaY = bounds.getCenterY() - barrel.getFitHeight()/2 + dHeight*PIXELS_PER_METER;
            //Координата по оси Y, при которой траектория будет кончаться при соприкосновении с полом
            final double neededHeight = borderPane.getHeight() - floor.getFitHeight();
            //Множитель для значения X, чтобы не считать его в цикле
            final double xCoeff = mDistance.get() / mTimeFlight.get() * PIXELS_PER_METER;

            for (double time = 0, x, y = 0; y < neededHeight; time += FREQUENCY){
                x = time * xCoeff + deltaX;
                y = -(mTotalHeight.get() + V_y * time -
                        Constants.g*Math.pow(time, 2)/2) * PIXELS_PER_METER + deltaY;
                pathElements.add((time < FREQUENCY - 0.001)? new MoveTo(x, y): new LineTo(x, y));
            }
        }

        /** Первичный метод прорисовки кривой траектории
         *  Нужен, т.к. без начальной скорости и дистанции невозможно нарисовать траекторию
         *  Метод вызывается только при иниц. настройки
         * */
        public void initCurve(String velocity){
            mInitVelocity.set(Integer.parseInt(velocity));
            mDistance.set(mInitVelocity.get() * mTimeFlight.get());
            Platform.runLater(this::calculateTrajectory);
        }

        public IntegerProperty initVelocityProperty(){
            return mInitVelocity;
        }

        public double getDuration() {
            return mTimeFlight.get();
        }

        public DoubleProperty durationProperty(){
            return mTimeFlight;
        }

        public DoubleProperty distanceProperty(){
            return mDistance;
        }

        public Path getPath() {
            return mPath;
        }

        public DoubleProperty initHeightProperty(){
            return mTotalHeight;
        }

        public DoubleProperty maxHeightProperty(){
            return mMaxHeight;
        }
    }
}