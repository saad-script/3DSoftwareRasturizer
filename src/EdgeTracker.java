public class EdgeTracker {

    private static final double EPSILON = 0.0001;

    private Vector3 startEdgePoint;
    private Vector3 endEdgePoint;
    private Vector3 currentPosition;
    private double xIncY = 0;
    private double zIncY = 0;
    private double slope = 0;


    public EdgeTracker(Vector3 startEdgePoint, Vector3 endEdgePoint) {

        this.startEdgePoint = startEdgePoint;
        this.endEdgePoint = endEdgePoint;

        this.currentPosition = startEdgePoint.clone();

        if (Math.abs(startEdgePoint.getY() - endEdgePoint.getY()) > EPSILON) {
            this.slope = (startEdgePoint.getX() - endEdgePoint.getX()) / (startEdgePoint.getY() - endEdgePoint.getY());
            xIncY = (startEdgePoint.getX() - endEdgePoint.getX()) / (startEdgePoint.getY() - endEdgePoint.getY());
            zIncY = (startEdgePoint.getZ() - endEdgePoint.getZ()) / (startEdgePoint.getY() - endEdgePoint.getY());
            double xPreIncY = xIncY * (Math.ceil(currentPosition.getY()) - currentPosition.getY());
            double zPreIncY = zIncY * (Math.ceil(currentPosition.getY()) - currentPosition.getY());
            currentPosition.setY(Math.ceil(currentPosition.getY()));
            currentPosition.setX(currentPosition.getX() + xPreIncY);
            currentPosition.setZ(currentPosition.getZ() + zPreIncY);

        }

    }

    public void stepY() {
        currentPosition.setY(currentPosition.getY() + 1);
        currentPosition.setX(currentPosition.getX() + xIncY);
        currentPosition.setZ(currentPosition.getZ() + zIncY);
    }

    public void stepY(double amount) {
        currentPosition.setY(currentPosition.getY() + amount);
        currentPosition.setX(currentPosition.getX() + (xIncY * amount));
        currentPosition.setZ(currentPosition.getZ() + (zIncY * amount));
    }

    public int getYStart() {
        return (int) Math.ceil(startEdgePoint.getY());
    }

    public int getYEnd() {
        return (int) Math.ceil(endEdgePoint.getY());
    }

    public Vector3 getStartEdgePoint() {
        return startEdgePoint;
    }

    public Vector3 getEndEdgePoint() {
        return endEdgePoint;
    }

    public Vector3 getCurrentPosition() {
        return currentPosition;
    }

    public double getSlope() {
        return slope;
    }


}
