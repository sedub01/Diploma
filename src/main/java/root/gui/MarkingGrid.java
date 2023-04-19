package root.gui;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import static root.utils.Constants.PIXELS_PER_UNIT;

public class MarkingGrid{
    /** Количество столбцов сетки*/
    @SuppressWarnings("FieldCanBeLocal")
    private final int mGridSize = 20;
    /** размер клетки*/
    @SuppressWarnings("FieldCanBeLocal")
    private final int mCellSize = PIXELS_PER_UNIT;
    /** Примерная высота вкладки таб панели*/
    @SuppressWarnings("FieldCanBeLocal")
    private final int tabOffset = 30;
    private final Pane mGridPane;
    public MarkingGrid(Pane gridPane){
        mGridPane = gridPane;
        mGridPane.setMouseTransparent(true);
        mGridPane.setVisible(false);
        final int max = mCellSize * mGridSize;
        // Создание вертикальных линий
        for (int i = 1; i <= mGridSize; i++){
            //startX, startY, endX, endY
            Line verLine = new Line(i * mCellSize, tabOffset, i * mCellSize, max);
            verLine.setStroke(Color.BLACK);
            verLine.getStrokeDashArray().addAll(10d, 10d);
            mGridPane.getChildren().add(verLine);
        }
        for (int i = 1; i <= mGridSize; i++){
            Line horLine = new Line(0, i * mCellSize, max, i * mCellSize);
            horLine.setStroke(Color.BLACK);
            horLine.getStrokeDashArray().addAll(10d, 10d);
            mGridPane.getChildren().add(horLine);
        }
    }

    public boolean isVisible() {
        return mGridPane.isVisible();
    }

    public void setVisible(boolean visible) {
        mGridPane.setVisible(visible);
    }
}
