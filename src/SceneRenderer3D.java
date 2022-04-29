import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SceneRenderer3D extends Canvas{

    int canvasWidth;
    int canvasHeight;
    public ArrayList<Mesh> meshes = new ArrayList<>();
    public boolean wireframe = false;
    public boolean fill = true;
    public boolean transparent = false;
    private final GraphicsContext canvasPen;
    private final Queue<Triangle> renderQueue = new LinkedList<>();
    private final double[][] depthBuffer;
    private int numTrisCurrentlyRendering = 0;

    public SceneRenderer3D(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        setWidth(canvasWidth);
        setHeight(canvasHeight);
        depthBuffer = new double[canvasWidth][canvasWidth];
        canvasPen = getGraphicsContext2D();
        clearCanvas();
    }

    public void clearCanvas() {
        canvasPen.setFill(Color.BLACK);
        canvasPen.fillRect(0, 0, canvasWidth, canvasHeight);

        for (int i = 0; i < canvasWidth; i++) {
            for (int j = 0; j < canvasHeight; j++) {
                depthBuffer[i][j] = Double.MAX_VALUE;
            }
        }
    }

    public void render() {
        for (Mesh mesh : meshes)
            for (Triangle triangle : mesh.triangles)
                processTriangle(triangle, mesh.center, mesh.baseColor);

        numTrisCurrentlyRendering = 0;
        while (!renderQueue.isEmpty()) {
            Triangle t = renderQueue.remove();
            Vector3[] triPoints = t.getPositions();

                if (fill)
                    drawScanLines(triPoints, t.color);
                if (wireframe)
                    drawWireframe(triPoints, Color.BEIGE);
                if (fill || wireframe)
                    numTrisCurrentlyRendering++;

        }
        System.out.println("Triangles Currently Being Rendered: " + numTrisCurrentlyRendering);
    }


    public void processTriangle(Triangle triangle, Vector3 center, Color baseColor) {

        Vector3[] position3DCoord = triangle.getPositions();
        Vector3[] transformedPoints = getTransformedPoints(position3DCoord, center);

        Vector3 cameraPos = Vector3.ZERO;
        Vector3 cameraForward =  Vector3.FORWARD;

        Vector3 triNormal = calculateTriNormal(transformedPoints).normalized();
        Vector3 triToLightDirection = cameraPos.subtract(transformedPoints[0]).normalized();
        Vector3 cameraToTriDirection = transformedPoints[0].subtract(cameraPos).normalized();
        double diffuse = triToLightDirection.dot(calculateTriNormal(transformedPoints));
        boolean isVisible = triNormal.dot(cameraToTriDirection) < 0 || transparent;

        if (isVisible) {
            Vector3[] nearClippedTriPoints = clipTriAgainstPlane(Vector3.FORWARD, new Vector3(0, 0, 0.5), transformedPoints);
            Vector3[] projectedPoints = perspectiveDivide(getProjectedPoints(nearClippedTriPoints));
            Vector3[] canvasPoints = getCanvasPoints(projectedPoints);

            for (int i = 0; i < canvasPoints.length; i+=3) {
                Vector3[] triToRaster = new Vector3[]{canvasPoints[i], canvasPoints[i + 1], canvasPoints[i + 2]};
                Vector3[] clippedTris = clipTrisAgainstScreenBoundaries(triToRaster);

                for (int j = 0; j < clippedTris.length; j+=3) {
                    Vector3[] triPoints = {clippedTris[j], clippedTris[j + 1], clippedTris[j + 2]};
                    Triangle t = new Triangle(triPoints);

                    t.color = new Color(Math.max(0.1, diffuse) * baseColor.getRed(),
                                            Math.max(0.1, diffuse) * baseColor.getGreen(),
                                            Math.max(0.1, diffuse) * baseColor.getBlue(), 1);

                    renderQueue.add(t);
                }
            }
        }

    }

    private Vector3[] getTransformedPoints(Vector3[] position3DCoord, Vector3 center) {

        Vector3[] transformedPoints = new Vector3[3];
        for (int i = 0; i < 3; i++) {
            Vector3 pointToOrigin  = position3DCoord[i].multiply(Matrix4.getTranslationMatrix(-center.getX(), -center.getY(), -center.getZ()));
            Vector3 scaledPoint = pointToOrigin.multiply(Matrix4.getScaleMatrix(1, 1, 1));
            Vector3 rotatedPoint = scaledPoint.multiply(Matrix4.getRotationMatrix(0, 0, 0));
            Vector3 originToPoint  = rotatedPoint.multiply(Matrix4.getTranslationMatrix(center.getX(), center.getY(), center.getZ()));
            Vector3 viewTransformedPoint = originToPoint.multiply(Matrix4.getTranslationMatrix(-Main.camera.getPosition().getX(), -Main.camera.getPosition().getY(), -Main.camera.getPosition().getZ()));
            Vector3 viewRotatedPoint = viewTransformedPoint.multiply(Matrix4.getViewMatrix(Main.camera.getForward(), Main.camera.getUp()));
            transformedPoints[i] = viewRotatedPoint;
        }
        return transformedPoints;

    }

    public Vector3[] getProjectedPoints(Vector3[] transformedPoints) {
        Vector3[] projectedPoints = new Vector3[transformedPoints.length];
        Matrix4 projectionMatrix = Matrix4.getProjectionMatrix(Main.width, Main.height, 50, 0.5, 1000);
        for (int i = 0; i < transformedPoints.length; i++) {
            //System.out.println("Z Value before projection: " + transformedPoints[i].getZ());
            projectedPoints[i] = transformedPoints[i].multiply(projectionMatrix);
        }
        return projectedPoints;
    }

    private Vector3[] getCanvasPoints(Vector3[] projectedPoints) {

        Vector3[] canvasPos = new Vector3[projectedPoints.length];
        for (int i = 0; i < projectedPoints.length; i++) {
            double x = projectedPoints[i].getX();
            double y = projectedPoints[i].getY();
            x = (x + 1) * 0.5 * Main.width;
            y = (y - 1) * -0.5 * Main.height;
            canvasPos[i] = new Vector3(x, y, projectedPoints[i].getZ());
        }
        return canvasPos;
    }

    private Vector3[] perspectiveDivide(Vector3[] canvasPoints) {
        Vector3[] perspectivePoints = new Vector3[canvasPoints.length];
        for (int i = 0; i < canvasPoints.length; i++) {
            //System.out.println("Before Perspective Divide: " + canvasPoints[i] + ", " + canvasPoints[i].getW());
            perspectivePoints[i] = canvasPoints[i].divide(canvasPoints[i].getW());
            perspectivePoints[i].setZ(canvasPoints[i].getW());
            //System.out.println("After Perspective Divide: " + perspectivePoints[i] + ", " + perspectivePoints[i].getW());
        }
        return perspectivePoints;
    }

    private Vector3 calculateTriNormal(Vector3[] translatedPos) {
        Vector3 line1 = translatedPos[1].subtract(translatedPos[0]).normalized();
        Vector3 line2 = translatedPos[2].subtract(translatedPos[0]).normalized();
        return line1.cross(line2).normalized();
    }

    private Vector3[] clipTrisAgainstScreenBoundaries(Vector3[] canvasPos) {
        Vector3[] clippedTris;

        Queue<Vector3> q = new LinkedList<>();
        q.addAll(List.of(canvasPos));

        int newTris = q.size() / 3;
        for (int i = 0; i < 4; i++) {

            while (newTris > 0) {

                Vector3[] triPoints = {q.remove(), q.remove(), q.remove()};
                newTris--;

                if (i == 0)
                    clippedTris = clipTriAgainstPlane(new Vector3(0, 1, 0), new Vector3(0, 0, 0), triPoints);
                else if (i == 1)
                    clippedTris = clipTriAgainstPlane(new Vector3(0, -1, 0), new Vector3(0, Main.height - 1, 0), triPoints);
                else if (i == 2)
                    clippedTris = clipTriAgainstPlane(new Vector3(1, 0, 0), new Vector3(0, 0, 0), triPoints);
                else
                    clippedTris = clipTriAgainstPlane(new Vector3(-1, 0, 0), new Vector3(Main.width - 1, 0, 0), triPoints);

                q.addAll(List.of(clippedTris));
            }
            newTris = q.size() / 3;
        }

        Vector3[] result = new Vector3[q.size()];
        result = q.toArray(result);
        return result;

    }

    private Vector3[] clipTriAgainstPlane(Vector3 planeNormal, Vector3 planePoint, Vector3[] triPoints) {
        Vector3[] inPoints = new Vector3[3]; int amountInPoints = 0;
        Vector3[] outPoints = new Vector3[3]; int amountOutPoints = 0;
        for (int i = 0; i < 3; i++) {
            double distanceToPlane = distancePointFromPlane(planeNormal, planePoint, triPoints[i]);
            if (distanceToPlane >= 0)
                inPoints[amountInPoints++] = triPoints[i];
            else if (distanceToPlane < 0)
                outPoints[amountOutPoints++] = triPoints[i];
        }

        if (amountInPoints == 3) {
            return triPoints;
        } else if (amountInPoints == 0) {
            return new Vector3[0];
        } else if (amountInPoints == 1) {
            Vector3[] newTriPoints = new Vector3[3];
            newTriPoints[0] = inPoints[0];
            newTriPoints[1] = getLineAndPlaneIntersection(planeNormal, planePoint, inPoints[0], outPoints[0]);
            newTriPoints[2] = getLineAndPlaneIntersection(planeNormal, planePoint, inPoints[0], outPoints[1]);
            return newTriPoints;
        } else {
            Vector3[] newTriPoints = new Vector3[6];
            Vector3 intersectionPoint1 = getLineAndPlaneIntersection(planeNormal, planePoint, inPoints[0], outPoints[0]);
            Vector3 intersectionPoint2 = getLineAndPlaneIntersection(planeNormal, planePoint, inPoints[1], outPoints[0]);

            newTriPoints[0] = inPoints[0];
            newTriPoints[1] = inPoints[1];
            newTriPoints[2] = intersectionPoint1;

            newTriPoints[3] = inPoints[1];
            newTriPoints[4] = intersectionPoint1;
            newTriPoints[5] = intersectionPoint2;
            return newTriPoints;
        }
    }

    private Vector3 getLineAndPlaneIntersection(Vector3 planeNormal, Vector3 planePoint, Vector3 lineStart, Vector3 lineEnd) {
        double d1 = distancePointFromPlane(planeNormal, planePoint, lineStart);
        double d2 = distancePointFromPlane(planeNormal, planePoint, lineEnd);
        double t = d1 / (d1 - d2);
        return lineStart.add(lineEnd.subtract(lineStart).multiply(t));
    }
    private double distancePointFromPlane(Vector3 planeNormal, Vector3 planePoint, Vector3 point) {
        return planeNormal.dot(point.subtract(planePoint));
    }

    private void drawScanLines(Vector3[] triPoints, Color color) {
        sortPoints(triPoints);

        boolean flipped = ((triPoints[1].getX() - triPoints[0].getX()) * (triPoints[2].getY() - triPoints[0].getY()))
                            - ((triPoints[1].getY() - triPoints[0].getY()) * (triPoints[2].getX() - triPoints[0].getX())) < 0;
        EdgeTracker topToBottom = new EdgeTracker(triPoints[0], triPoints[2]);
        EdgeTracker topToMiddle = new EdgeTracker(triPoints[0], triPoints[1]);
        EdgeTracker middleToBottom = new EdgeTracker(triPoints[1], triPoints[2]);

        fillHalfTriangle(topToBottom, topToMiddle, topToMiddle, flipped, color);
        fillHalfTriangle(topToBottom, middleToBottom, middleToBottom, flipped, color);

    }

    private void fillHalfTriangle(EdgeTracker edge1, EdgeTracker edge2, EdgeTracker shorterEdge, boolean flipped, Color color) {
        EdgeTracker left = edge1;
        EdgeTracker right = edge2;
        if (flipped) {
            EdgeTracker temp = left;
            left = right;
            right = temp;
        }

        for (int y = shorterEdge.getYStart(); y < shorterEdge.getYEnd(); y++) {

            int xStart = (int) Math.ceil(left.getCurrentPosition().getX());
            int xEnd = (int) Math.ceil(right.getCurrentPosition().getX());
            double zStart = left.getCurrentPosition().getZ();
            double zEnd = right.getCurrentPosition().getZ();
            double zInc = (zStart - zEnd) / (xStart - xEnd);

            for (int x = xStart; x < xEnd; x++) {
                double pixelDepth = zStart + (zInc * (x - xStart));
                if (pixelDepth < depthBuffer[x][y] || transparent) {
                    canvasPen.getPixelWriter().setColor(x, y, color);
                    depthBuffer[x][y] = pixelDepth;
                }
            }
            left.stepY();
            right.stepY();

        }
    }

    private void drawWireframe(Vector3[] triPoints, Color color) {
        sortPoints(triPoints);
        drawLine((int) Math.ceil(triPoints[0].getX()), (int) Math.ceil(triPoints[0].getY()),
                (int) Math.ceil(triPoints[2].getX()), (int) Math.ceil(triPoints[2].getY()), color);
        drawLine((int) Math.ceil(triPoints[0].getX()), (int) Math.ceil(triPoints[0].getY()),
                (int) Math.ceil(triPoints[1].getX()), (int) Math.ceil(triPoints[1].getY()), color);
        drawLine((int) Math.ceil(triPoints[1].getX()), (int) Math.ceil(triPoints[1].getY()),
                (int) Math.ceil(triPoints[2].getX()), (int) Math.ceil(triPoints[2].getY()), color);

    }

    private void drawLine(int x0, int y0, int x1, int y1, Color color) {
        boolean steep = false;
        if (Math.abs(x0 - x1) < Math.abs(y0 - y1)) {
            int temp = x0; x0 = y0; y0 = temp;
            temp = x1; x1 = y1; y1 = temp;
            steep = true;
        }
        if (x0 > x1) {
            int temp = x0; x0 = x1; x1 = temp;
            temp = y0; y0 = y1; y1 = temp;
        }
        int dx = x1 - x0;
        int dy = y1 - y0;
        int derror2 = Math.abs(dy) * 2;
        int error2 = 0;
        int y = y0;
        for (int x = x0; x <= x1; x++) {
            if (steep) {
                canvasPen.getPixelWriter().setColor(y, x, color);
            } else {
                canvasPen.getPixelWriter().setColor(x, y, color);
            }
            error2 += derror2;
            if (error2 > dx) {
                y += (y1 > y0 ? 1 : -1);
                error2 -= dx*2;
            }
        }
    }


    private void sortPoints(Vector3[] points) {
        if (points[0].getY() > points[1].getY()) {
            Vector3 temp = points[0];
            points[0] = points[1];
            points[1] = temp;
        }
        if (points[0].getY() > points[2].getY()) {
            Vector3 temp = points[0];
            points[0] = points[2];
            points[2] = temp;
        }
        if (points[1].getY() > points[2].getY()) {
            Vector3 temp = points[1];
            points[1] = points[2];
            points[2] = temp;
        }
    }



}
