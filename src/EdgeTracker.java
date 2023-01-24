public class EdgeTracker {

    private static final double EPSILON = 0.0001;

    private Vertex startEdgePoint;
    private Vertex endEdgePoint;
    private Vertex currentPosition;
    private double xIncY = 0;
    private double zIncY = 0;
    private double uIncY = 0;
    private double vIncY = 0;

    private double yIncX = 0;
    private double zIncX = 0;
    private double uIncX = 0;
    private double vIncX = 0;

    private boolean useXStep;

    public EdgeTracker(Vertex startEdgePoint, Vertex endEdgePoint) {
        this(startEdgePoint, endEdgePoint, false);
    }

    public EdgeTracker(Vertex startEdgePoint, Vertex endEdgePoint, boolean useXStep) {

        if (useXStep) {
            if ((int) Math.ceil(startEdgePoint.getX()) > (int) Math.ceil(endEdgePoint.getX())) {
                Vertex temp = startEdgePoint;
                startEdgePoint = endEdgePoint;
                endEdgePoint = temp;
            }
        } else {
            if ((int) Math.ceil(startEdgePoint.getY()) > (int) Math.ceil(endEdgePoint.getY())) {
                Vertex temp = startEdgePoint;
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
            uIncX = (startEdgePoint.getU() - endEdgePoint.getU()) / (startEdgePoint.getX() - endEdgePoint.getX());
            vIncX = (startEdgePoint.getV() - endEdgePoint.getV()) / (startEdgePoint.getX() - endEdgePoint.getX());
            double yPreIncX = yIncX * (Math.ceil(currentPosition.getX()) - currentPosition.getX());
            double zPreIncX = zIncX * (Math.ceil(currentPosition.getX()) - currentPosition.getX());
            double uPreIncX = uIncX * (Math.ceil(currentPosition.getX()) - currentPosition.getX());
            double vPreIncX = vIncX * (Math.ceil(currentPosition.getX()) - currentPosition.getX());
            currentPosition.getPosition().setX(Math.ceil(currentPosition.getX()));
            currentPosition.getPosition().setY(currentPosition.getY() + yPreIncX);
            currentPosition.getPosition().setZ(currentPosition.getZ() + zPreIncX);
            currentPosition.getTextCoord().setX(currentPosition.getU() + uPreIncX);
            currentPosition.getTextCoord().setY(currentPosition.getV() + vPreIncX);
        } else {
            xIncY = (startEdgePoint.getX() - endEdgePoint.getX()) / (startEdgePoint.getY() - endEdgePoint.getY());
            zIncY = (startEdgePoint.getZ() - endEdgePoint.getZ()) / (startEdgePoint.getY() - endEdgePoint.getY());
            uIncY = (startEdgePoint.getU() - endEdgePoint.getU()) / (startEdgePoint.getY() - endEdgePoint.getY());
            vIncY = (startEdgePoint.getV() - endEdgePoint.getV()) / (startEdgePoint.getY() - endEdgePoint.getY());
            double xPreIncY = xIncY * (Math.ceil(currentPosition.getY()) - currentPosition.getY());
            double zPreIncY = zIncY * (Math.ceil(currentPosition.getY()) - currentPosition.getY());
            double uPreIncY = uIncY * (Math.ceil(currentPosition.getY()) - currentPosition.getY());
            double vPreIncY = vIncY * (Math.ceil(currentPosition.getY()) - currentPosition.getY());
            currentPosition.getPosition().setY(Math.ceil(currentPosition.getY()));
            currentPosition.getPosition().setX(currentPosition.getX() + xPreIncY);
            currentPosition.getPosition().setZ(currentPosition.getZ() + zPreIncY);
            currentPosition.getTextCoord().setX(currentPosition.getU() + uPreIncY);
            currentPosition.getTextCoord().setY(currentPosition.getV() + vPreIncY);
        }

    }

    public void step() {
        if (useXStep)
            stepX();
        else
            stepY();
    }
    public void stepX() {
        currentPosition.getPosition().setX(currentPosition.getX() + 1);
        currentPosition.getPosition().setY(currentPosition.getY() + yIncX);
        currentPosition.getPosition().setZ(currentPosition.getZ() + zIncX);

        currentPosition.getTextCoord().setX(currentPosition.getU() + uIncX);
        currentPosition.getTextCoord().setY(currentPosition.getV() + vIncX);
    }
    public void stepY() {
        currentPosition.getPosition().setY(currentPosition.getY() + 1);
        currentPosition.getPosition().setX(currentPosition.getX() + xIncY);
        currentPosition.getPosition().setZ(currentPosition.getZ() + zIncY);

        currentPosition.getTextCoord().setX(currentPosition.getU() + uIncY);
        currentPosition.getTextCoord().setY(currentPosition.getV() + vIncY);
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

    public Vertex getStartEdgePoint() {
        return startEdgePoint;
    }

    public Vertex getEndEdgePoint() {
        return endEdgePoint;
    }

    public Vertex getCurrentPosition() {
        return currentPosition;
    }


}
