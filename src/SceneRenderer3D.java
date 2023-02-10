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
    public boolean wireframe = true;
    public boolean fill = true;
    public boolean transparent = false;
    private final GraphicsContext canvasPen;
    private final Queue<Triangle> renderQueue = new LinkedList<>();
    private final double[][] depthBuffer;

    private Mesh currentlyRenderingMesh;
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

    public void nextFrame() {
        clearCanvas();
        clearDepthBuffer();
        render();
    }

    public void clearCanvas() {
        canvasPen.setFill(Color.BLACK);
        canvasPen.fillRect(0, 0, canvasWidth, canvasHeight);
    }

    public void clearDepthBuffer() {
        for (int i = 0; i < canvasWidth; i++) {
            for (int j = 0; j < canvasHeight; j++) {
                depthBuffer[i][j] = 0.0;
            }
        }
    }

    public void render() {
        for (Mesh mesh : meshes) {
            currentlyRenderingMesh = mesh;
            for (Triangle triangle : mesh.triangles) {
                processTriangle(triangle, mesh.center, mesh.baseColor);
            }
        }

        numTrisCurrentlyRendering = 0;
        while (!renderQueue.isEmpty()) {
            Triangle t = renderQueue.remove();
            Vertex[] triPoints = t.getVertices();

                drawScanLines(triPoints, t.diffuse);

                if (wireframe)
                    drawWireframe(triPoints, Color.BEIGE);

                if (fill || wireframe)
                    numTrisCurrentlyRendering++;

        }
        System.out.println("Triangles Currently Being Rendered: " + numTrisCurrentlyRendering);
    }


    public void processTriangle(Triangle triangle, Vector3 center, Color baseColor) {

        Vertex[] position3DCoord = triangle.getVertices();
        Vertex[] transformedPoints = getTransformedPoints(position3DCoord, center);

        Vector3 cameraPos = Vector3.ZERO;
        Vector3 cameraForward =  Vector3.FORWARD;

        Vector3 triNormal = calculateTriNormal(transformedPoints).normalized();
        Vector3 triToLightDirection = cameraPos.subtract(transformedPoints[0].getPosition()).normalized();
        Vector3 cameraToTriDirection = transformedPoints[0].getPosition().subtract(cameraPos).normalized();
        double diffuse = triToLightDirection.dot(triNormal);
        boolean isVisible = triNormal.dot(cameraToTriDirection) < 0 || transparent;

        if (isVisible) {
            Vertex[] nearClippedTriPoints = clipTriAgainstPlane(Vector3.FORWARD, new Vector3(0, 0, 0.5), transformedPoints);
            Vertex[] projectedPoints = perspectiveDivide(getProjectedPoints(nearClippedTriPoints));
            Vertex[] canvasPoints = getCanvasPoints(projectedPoints);

            for (int i = 0; i < canvasPoints.length; i+=3) {
                Vertex[] triToRaster = new Vertex[]{canvasPoints[i], canvasPoints[i + 1], canvasPoints[i + 2]};
                Vertex[] clippedTris = clipTrisAgainstScreenBoundaries(triToRaster);

                for (int j = 0; j < clippedTris.length; j+=3) {
                    Vertex[] triPoints = {clippedTris[j], clippedTris[j + 1], clippedTris[j + 2]};
                    Triangle t = new Triangle(triPoints);
                    t.normal = triNormal;
                    t.diffuse = diffuse;
//                    t.color = new Color(Math.min(1, Math.max(0.1, diffuse) * baseColor.getRed()),
//                                            Math.min(1, Math.max(0.1, diffuse) * baseColor.getGreen()),
//                                            Math.min(1, Math.max(0.1, diffuse) * baseColor.getBlue()), 1);

                    renderQueue.add(t);
                }
            }
        }

    }

    private Vertex[] getTransformedPoints(Vertex[] position3DCoord, Vector3 center) {

        Vertex[] transformedPoints = new Vertex[3];
        for (int i = 0; i < 3; i++) {
            Vector3 pointToOrigin  = position3DCoord[i].getPosition().multiply(Matrix4.getTranslationMatrix(-center.getX(), -center.getY(), -center.getZ()));
            Vector3 scaledPoint = pointToOrigin.multiply(Matrix4.getScaleMatrix(1, 1, 1));
            Vector3 rotatedPoint = scaledPoint.multiply(Matrix4.getRotationMatrix(0, 0, 0));
            Vector3 originToPoint  = rotatedPoint.multiply(Matrix4.getTranslationMatrix(center.getX(), center.getY(), center.getZ()));
            Vector3 viewTransformedPoint = originToPoint.multiply(Matrix4.getTranslationMatrix(-Main.camera.getPosition().getX(), -Main.camera.getPosition().getY(), -Main.camera.getPosition().getZ()));
            Vector3 viewRotatedPoint = viewTransformedPoint.multiply(Matrix4.getViewMatrix(Main.camera.getForward(), Main.camera.getUp()));
            transformedPoints[i] = new Vertex(viewRotatedPoint, position3DCoord[i].getTextCoord(), position3DCoord[i].getNormal());
        }
        return transformedPoints;

    }

    public Vertex[] getProjectedPoints(Vertex[] transformedPoints) {
        Vertex[] projectedPoints = new Vertex[transformedPoints.length];
        Matrix4 projectionMatrix = Matrix4.getProjectionMatrix(Main.width, Main.height, 50, 0.5, 1000);
        for (int i = 0; i < transformedPoints.length; i++) {
            //System.out.println("Z Value before projection: " + transformedPoints[i].getZ());
            Vector3 pos = transformedPoints[i].getPosition().multiply(projectionMatrix);
            projectedPoints[i] = new Vertex(pos, transformedPoints[i].getTextCoord(), transformedPoints[i].getNormal());
        }
        return projectedPoints;
    }

    private Vertex[] getCanvasPoints(Vertex[] projectedPoints) {

        Vertex[] canvasPos = new Vertex[projectedPoints.length];
        for (int i = 0; i < projectedPoints.length; i++) {
            double x = projectedPoints[i].getX();
            double y = projectedPoints[i].getY();
            x = (x + 1) * 0.5 * Main.width;
            y = (y - 1) * -0.5 * Main.height;
            Vector3 pos = new Vector3(x, y, projectedPoints[i].getZ());
            canvasPos[i] = new Vertex(pos, projectedPoints[i].getTextCoord(), projectedPoints[i].getNormal());
        }
        return canvasPos;
    }

    private Vertex[] perspectiveDivide(Vertex[] canvasPoints) {
        Vertex[] perspectivePoints = new Vertex[canvasPoints.length];
        for (int i = 0; i < canvasPoints.length; i++) {
            //System.out.println("Before Perspective Divide: " + canvasPoints[i] + ", " + canvasPoints[i].getW());
            Vector3 pos = canvasPoints[i].getPosition().divide(canvasPoints[i].getW());
            Vector3 textCoord = canvasPoints[i].getTextCoord().divide(canvasPoints[i].getW());
            perspectivePoints[i] = new Vertex(pos, textCoord, canvasPoints[i].getNormal());
            perspectivePoints[i].getPosition().setZ(1 / canvasPoints[i].getW());
            //System.out.println("After Perspective Divide: " + perspectivePoints[i] + ", " + perspectivePoints[i].getW());
        }
        return perspectivePoints;
    }

    private Vector3 calculateTriNormal(Vertex[] translatedPos) {
        Vector3 line1 = translatedPos[1].getPosition().subtract(translatedPos[0].getPosition()).normalized();
        Vector3 line2 = translatedPos[2].getPosition().subtract(translatedPos[0].getPosition()).normalized();
        return line1.cross(line2).normalized();
    }

    private Vertex[] clipTrisAgainstScreenBoundaries(Vertex[] canvasPos) {
        Vertex[] clippedTris;

        Queue<Vertex> q = new LinkedList<>();
        q.addAll(List.of(canvasPos));

        int newTris = q.size() / 3;
        for (int i = 0; i < 4; i++) {

            while (newTris > 0) {

                Vertex[] triPoints = {q.remove(), q.remove(), q.remove()};
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

        Vertex[] result = new Vertex[q.size()];
        result = q.toArray(result);
        return result;

    }

    private Vertex[] clipTriAgainstPlane(Vector3 planeNormal, Vector3 planePoint, Vertex[] triPoints) {
        Vertex[] inPoints = new Vertex[3]; int amountInPoints = 0;
        Vertex[] outPoints = new Vertex[3]; int amountOutPoints = 0;
        for (int i = 0; i < 3; i++) {
            double distanceToPlane = distancePointFromPlane(planeNormal, planePoint, triPoints[i].getPosition());
            if (distanceToPlane >= 0)
                inPoints[amountInPoints++] = triPoints[i];
            else if (distanceToPlane < 0)
                outPoints[amountOutPoints++] = triPoints[i];
        }

        if (amountInPoints == 3) {
            return triPoints;
        } else if (amountInPoints == 0) {
            return new Vertex[0];
        } else if (amountInPoints == 1) {
            Vertex[] newTriPoints = new Vertex[3];
            newTriPoints[0] = inPoints[0];
            newTriPoints[1] = getLineAndPlaneIntersection(planeNormal, planePoint, inPoints[0], outPoints[0]);
            newTriPoints[2] = getLineAndPlaneIntersection(planeNormal, planePoint, inPoints[0], outPoints[1]);
            return newTriPoints;
        } else {
            Vertex[] newTriPoints = new Vertex[6];
            Vertex intersectionPoint1 = getLineAndPlaneIntersection(planeNormal, planePoint, inPoints[0], outPoints[0]);
            Vertex intersectionPoint2 = getLineAndPlaneIntersection(planeNormal, planePoint, inPoints[1], outPoints[0]);

            newTriPoints[0] = inPoints[0];
            newTriPoints[1] = inPoints[1];
            newTriPoints[2] = intersectionPoint1;

            newTriPoints[3] = inPoints[1];
            newTriPoints[4] = intersectionPoint1;
            newTriPoints[5] = intersectionPoint2;
            return newTriPoints;
        }
    }

    private Vertex getLineAndPlaneIntersection(Vector3 planeNormal, Vector3 planePoint, Vertex lineStart, Vertex lineEnd) {
        double d1 = distancePointFromPlane(planeNormal, planePoint, lineStart.getPosition());
        double d2 = distancePointFromPlane(planeNormal, planePoint, lineEnd.getPosition());
        double t = d1 / (d1 - d2);
        Vector3 pos = lineStart.getPosition().add(lineEnd.getPosition().subtract(lineStart.getPosition()).multiply(t));
        Vector3 textCoord = lineStart.getTextCoord().add(lineEnd.getTextCoord().subtract(lineStart.getTextCoord()).multiply(t));
        Vector3 normal = lineStart.getNormal().add(lineEnd.getNormal().subtract(lineStart.getNormal()).multiply(t));
        return new Vertex(pos, textCoord, normal);
    }
    private double distancePointFromPlane(Vector3 planeNormal, Vector3 planePoint, Vector3 point) {
        return planeNormal.dot(point.subtract(planePoint));
    }

    private void drawScanLines(Vertex[] triPoints, double diffuse) {
        sortPoints(triPoints);

        boolean flipped = ((triPoints[1].getX() - triPoints[0].getX()) * (triPoints[2].getY() - triPoints[0].getY()))
                            - ((triPoints[1].getY() - triPoints[0].getY()) * (triPoints[2].getX() - triPoints[0].getX())) < 0;
        EdgeTracker topToBottom = new EdgeTracker(triPoints[0], triPoints[2]);
        EdgeTracker topToMiddle = new EdgeTracker(triPoints[0], triPoints[1]);
        EdgeTracker middleToBottom = new EdgeTracker(triPoints[1], triPoints[2]);

        fillHalfTriangle(topToBottom, topToMiddle, topToMiddle, flipped, diffuse);
        fillHalfTriangle(topToBottom, middleToBottom, middleToBottom, flipped, diffuse);
    }

    private void fillHalfTriangle(EdgeTracker edge1, EdgeTracker edge2, EdgeTracker shorterEdge, boolean flipped, double diffuse) {
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
            double uStart = left.getCurrentPosition().getU();
            double uEnd = right.getCurrentPosition().getU();
            double uInc = (uStart - uEnd) / (xStart - xEnd);
            double vStart = left.getCurrentPosition().getV();
            double vEnd = right.getCurrentPosition().getV();
            double vInc = (vStart - vEnd) / (xStart - xEnd);


            for (int x = xStart; x < xEnd; x++) {
                double pixelDepth = (zStart + (zInc * (x - xStart)));

                if (pixelDepth > depthBuffer[x][y]) {
                    double u = (uStart + (uInc * (x - xStart)));
                    double v = (vStart + (vInc * (x - xStart)));

                    Color pixelColor;
                    if (currentlyRenderingMesh.texture == null)
                        pixelColor = currentlyRenderingMesh.baseColor;
                    else
                        pixelColor = currentlyRenderingMesh.texture.sampleColor(u / pixelDepth, v / pixelDepth);

                    
                    pixelColor = new Color(Math.min(1, Math.max(0.1, diffuse) * pixelColor.getRed()),
                            Math.min(1, Math.max(0.1, diffuse) * pixelColor.getGreen()),
                            Math.min(1, Math.max(0.1, diffuse) * pixelColor.getBlue()), 1);

                    canvasPen.getPixelWriter().setColor(x, y, pixelColor);
                    depthBuffer[x][y] = pixelDepth;
                }
            }
            left.stepY();
            right.stepY();

        }
    }

    private void drawWireframe(Vertex[] triPoints, Color color) {
        drawEdge(triPoints[0], triPoints[2], color);
        drawEdge(triPoints[0], triPoints[1], color);
        drawEdge(triPoints[1], triPoints[2], color);

    }

    private void drawEdge(Vertex startPoint, Vertex endPoint, Color color) {
        boolean useXStep =
                Math.abs(startPoint.getX() - endPoint.getX()) > Math.abs(startPoint.getY() - endPoint.getY());

        EdgeTracker edge = new EdgeTracker(startPoint, endPoint, useXStep);

        int current, end;
        if (useXStep) {
            current = edge.getXStart();
            end = edge.getXEnd();
        } else {
            current = edge.getYStart();
            end = edge.getYEnd();
        }

        while (current < end) {
            int x = (int) Math.ceil(edge.getCurrentPosition().getX());
            int y = (int) Math.ceil(edge.getCurrentPosition().getY());
            double pixelDepth = edge.getCurrentPosition().getZ() * 1.01;

            if (pixelDepth > depthBuffer[x][y] || transparent) {
                canvasPen.getPixelWriter().setColor(x, y, color);
            }

            edge.step();
            if (useXStep)
                current = (int) Math.ceil(edge.getCurrentPosition().getX());
            else
                current = (int) Math.ceil(edge.getCurrentPosition().getY());
        }
    }


    private void sortPoints(Vertex[] points) {
        if (points[0].getY() > points[1].getY()) {
            Vertex temp = points[0];
            points[0] = points[1];
            points[1] = temp;
        }
        if (points[0].getY() > points[2].getY()) {
            Vertex temp = points[0];
            points[0] = points[2];
            points[2] = temp;
        }
        if (points[1].getY() > points[2].getY()) {
            Vertex temp = points[1];
            points[1] = points[2];
            points[2] = temp;
        }
    }


}
