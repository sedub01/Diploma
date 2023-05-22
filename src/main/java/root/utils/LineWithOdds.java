package root.utils;

import javafx.scene.shape.Line;

public class LineWithOdds extends Line {
    private double k;
    private double b;
    public LineWithOdds(double startX, double startY, double endX, double endY){
        super(startX, startY, endX, endY);
    }

    public double getK(){
        return k;
    }
    public double getB(){
        return b;
    }

    public double getYByX(double x){
        return k*x + b;
    }

    public static Point getIntersectionPoint(LineWithOdds line1, LineWithOdds line2){
        line1.updateOdds();
        line2.updateOdds();

        double resK = line1.getK() - line2.getK();
        double resB = line1.getB() - line2.getB();
        double foundX = -resB / resK;
        double foundY = line2.getK() * foundX + line2.getB();
        return new Point(foundX, foundY);
    }

    public void updateOdds() {
        k = (getEndY() - getStartY()) / (getEndX() - getStartX());
        b = getStartX() * (getStartY() - getEndY()) / (getEndX() - getStartX()) + getStartY();
    }
}
