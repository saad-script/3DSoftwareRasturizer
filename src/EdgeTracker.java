public class EdgeTracker {

    private static final double EPSILON = 0.0001;

    private Vector3 startEdgePoint;
    private Vector3 endEdgePoint;
    private Vector3 currentPosition;
    private double xIncY = 0;
    private double zIncY = 0;

    private double yIncX = 0;
    private double zIncX = 0;

    private boolean useXStep = false;

    public EdgeTracker(Vector3 startEdgePoint, Vector3 endEdgePoint) {
        this(startEdgePoint, endEdgePoint, false);
    }

    public EdgeTracker(Vector3 startEdgePoint, Vector3 endEdgePoint, boolean useXStep) {

        if (useXStep) {
            if ((int) Math.ceil(startEdgePoint.getX()) > (int) Math.ceil(endEdgePoint.getX())) {
                Vector3 temp = startEdgePoint;
                startEdgePoint = endEdgePoint;
                endEdgePoint = temp;
            }
        } else {
            if ((int) Math.ceil(startEdgePoint.getY()) > (int) Math.ceil(endEdgePoint.getY())) {
                Vector3 temp = startEdgePoint;
                startEdgePoint = endEdgePoint;
                endEdgePoint = temp;
            }
        }

        this.startEdgePoint = startEdgePoint;
        this.endEdgePoint = endEdgePoint;
        this.useXStep = useXStep;

        this.currentPosition = startEdgePoint.clone();

        if (useXStep) {
            yIncX = (startEdgePoint.getY() - endEdgePoint.getY()) / (startEdgePoint.getX() - endEdgePoint.getX());
            zIncX = (startEdgePoint.getZ() - endEdgePoint.getZ()) / (startEdgePoint.getX() - endEdgePoint.getX());
            double yPreIncX = yIncX * (Math.ceil(currentPosition.getX()) - currentPosition.getX());
            double zPreIncX = zIncX * (Math.ceil(currentPosition.getX()) - currentPosition.getX());
            currentPosition.setX(Math.ceil(currentPosition.getX()));
            currentPosition.setY(currentPosition.getY() + yPreIncX);
            currentPosition.setZ(currentPosition.getZ() + zPreIncX);
        } else {
            xIncY = (startEdgePoint.getX() - endEdgePoint.getX()) / (startEdgePoint.getY() - endEdgePoint.getY());
            zIncY = (startEdgePoint.getZ() - endEdgePoint.getZ()) / (startEdgePoint.getY() - endEdgePoint.getY());
            double xPreIncY = xIncY * (Math.ceil(currentPosition.getY()) - currentPosition.getY());
            double zPreIncY = zIncY * (Math.ceil(currentPosition.getY()) - currentPosition.getY());
            currentPosition.setY(Math.ceil(currentPosition.getY()));
            currentPosition.setX(currentPosition.getX() + xPreIncY);
            currentPosition.setZ(currentPosition.getZ() + zPreIncY);

        }

    }

    public void step() {
        if (useXStep)
            stepX();
        else
            stepY();
    }
    public void stepX() {
        currentPosition.setX(currentPosition.getX() + 1);
        currentPosition.setY(currentPosition.getY() + yIncX);
        currentPosition.setZ(currentPosition.getZ() + zIncX);
    }
    public void stepY() {
        currentPosition.setY(currentPosition.getY() + 1);
        currentPosition.setX(currentPosition.getX() + xIncY);
        currentPosition.setZ(currentPosition.getZ() + zIncY);
    }

    public int getYStart() {
        return (int) Math.ceil(startEdgePoint.getY());
    }

    public int getYEnd() {
        return (int) Math.ceil(endEdgePoint.getY());
    }

    public int getXStart() {
        return (int) Math.ceil(startEdgePoint.getX());
    }

    public int getXEnd() {
        return (int) Math.ceil(endEdgePoint.getX());
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


}
