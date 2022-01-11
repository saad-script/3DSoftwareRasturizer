import javafx.scene.paint.Color;


public class Triangle {

    Vertex[] vertices = new Vertex[3];

    public Triangle(Vertex point1, Vertex point2, Vertex point3) {
        vertices[0] = point1;
        vertices[1] = point2;
        vertices[2] = point3;
    }

    public Triangle(Vertex[] vertices) {
        if (vertices.length != 3)
            throw new IllegalArgumentException("Triangle can only have 3 vertices.");
        this.vertices = vertices;
    }

    public Triangle(Vector3 point1, Vector3 point2, Vector3 point3) {
        vertices[0] = new Vertex(point1);
        vertices[1] = new Vertex(point2);
        vertices[2] = new Vertex(point3);
    }

    public Triangle(Vector3[] points) {
        if (points.length != 3)
            throw new IllegalArgumentException("Triangle can only have 3 points.");
        for (int i = 0; i < points.length; i++)
            vertices[i] = new Vertex(points[i]);
    }

    public void draw(Vector3 center, Color color, boolean fill, boolean wireFrame) {


        Vector3[] translatedPos = new Vector3[3];
        for (int i = 0; i < vertices.length; i++) {
            Vector3 coord = vertices[i].getPosition();

            Vector3 pointToOrigin  = coord.multiply(Matrix4.getTranslationMatrix(-center.getX(), -center.getY(), -center.getZ()));
            Vector3 rotatedPos = pointToOrigin.multiply(Matrix4.getRotationMatrix(0, 0, 0));
            Vector3 originToPoint  = rotatedPos.multiply(Matrix4.getTranslationMatrix(center.getX(), center.getY(), center.getZ()));

            Vector3 viewTransformed = originToPoint.multiply(Matrix4.getTranslationMatrix(-Main.camera.getPosition().getX(), -Main.camera.getPosition().getY(), -Main.camera.getPosition().getZ()));
            Vector3 viewRotated = viewTransformed.multiply(Matrix4.getViewMatrix(Main.camera.getForward(), Main.camera.getUp(), Main.camera.getPosition()));

            translatedPos[i] = new Vector3(viewRotated.getX(), viewRotated.getY(), viewRotated.getZ());

        }

        Vector3 ambientLightColor = new Vector3(0.05, 0.05, 0.05);
        Vector3 lightDir = new Vector3(1, 0, 0).normalized();
        Vector3 normalDir = normal(translatedPos).normalized();
        Vector3 cameraDir = translatedPos[0].subtract(Main.camera.getForward());
        boolean isVisible = normalDir.dot(cameraDir) < 0;
        //isVisible = true;

        if (isVisible) {

            Vector2[] canvasPos = new Vector2[3];
            for (int i = 0; i < 3; i++) {
                Vector3 projectedPos =
                        translatedPos[i].multiply(Matrix4.getProjectionMatrix(Main.width, Main.height, 50, 0.1, 1000));
                double w = projectedPos.getW();
                projectedPos = projectedPos.divide(w);
                double x = projectedPos.x;
                double y = projectedPos.y;
                x = (x + 1) * 0.5 * Main.width;
                y = (y - 1) * -0.5 * Main.height;
                Vector2 result = new Vector2(x, y);
                result.setW(1/w);
                canvasPos[i] = result;
            }


            Vector3 colorRGBA = new Vector3(color.getRed(), color.getGreen(), color.getBlue());
            colorRGBA.setW(color.getOpacity());
            colorRGBA = colorRGBA.multiply(Math.max(0.1, lightDir.dot(normalDir)));
            colorRGBA = colorRGBA.clampNormalized();
            Color drawColor = new Color(colorRGBA.getX(), colorRGBA.getY(), colorRGBA.getZ(), colorRGBA.getW());


            Main.renderer.setLineWidth(1);
            Main.renderer.setFill(drawColor);
            Main.renderer.setStroke(drawColor);


            if (fill)
                rasterize(canvasPos, drawColor);

            if (wireFrame) {
                Main.renderer.setStroke(Color.BEIGE);
                Main.renderer.strokePolygon(
                        new double[]{canvasPos[0].getX(), canvasPos[1].getX(), canvasPos[2].getX()},
                        new double[]{canvasPos[0].getY(), canvasPos[1].getY(), canvasPos[2].getY()}, 3);
            }

        }

    }



    private void rasterize(Vector2[] points, Color color) {

        sortPoints(points);

        double dx0 = points[1].getX() - points[0].getX();
        double dy0 = points[1].getY() - points[0].getY();
        double dw0 = points[1].getW() - points[0].getW();
        double dx1 = points[2].getX() - points[0].getX();
        double dy1 = points[2].getY() - points[0].getY();
        double dw1 = points[2].getW() - points[0].getW();
        double dx2 = points[2].getX() - points[1].getX();
        double dy2 = points[2].getY() - points[1].getY();
        double dw2 = points[2].getW() - points[1].getW();

        double xStep0 = 0;
        double xStep1 = 0;
        double xStep2 = 0;
        double depthStep0 = 0;
        double depthStep1 = 0;


        if (dy0 != 0) xStep0 = dx0 / dy0;
        if (dy1 != 0) xStep1 = dx1 / dy1;
        if (dy2 != 0) xStep2 = dx2 / dy2;
        if (dy0 != 0) depthStep0 = dw0 / dy0;
        if (dy1 != 0) depthStep1 = dw1 / dy1;


        for (double i = points[0].getY(); i <= points[1].getY(); i++) {
            double ax = points[0].getX() + (i - points[0].getY()) * xStep0;
            double bx = points[0].getX() + (i - points[0].getY()) * xStep1;
            double depth = points[0].getW() + (i - points[0].getY()) * depthStep0;

            if (ax > bx) {
                double temp = ax;
                ax = bx;
                bx = temp;
            }

            for (double j = ax; j < bx; j++) {

                //color = new Color(depth, depth, depth, 1);
                Main.renderer.getPixelWriter().setColor((int)j, (int)i, color);

            }

        }


        for (double i = points[1].getY(); i <= points[2].getY(); i++) {
            double ax = points[1].getX() + (i - points[1].getY()) * xStep2;
            double bx = points[0].getX() + (i - points[0].getY()) * xStep1;
            double depth = points[0].getW() + (i - points[1].getY()) * depthStep0;
            if (ax > bx) {
                double temp = ax;
                ax = bx;
                bx = temp;
            }

            for (double j = ax; j < bx; j++) {

                //color = new Color(depth, depth, depth, 1);
                Main.renderer.getPixelWriter().setColor((int)j, (int)i, color);

            }

        }


    }




    private Vector3 normal(Vector3[] translatedPos) {
        Vector3 line1 = translatedPos[1].subtract(translatedPos[0]).normalized();
        Vector3 line2 = translatedPos[2].subtract(translatedPos[0]).normalized();
        return line1.cross(line2).normalized();
    }



    private Vector2[] sortPoints(Vector2[] points) {

        if (points[0].getY() > points[1].getY()) {
            Vector2 temp = points[0];
            points[0] = points[1];
            points[1] = temp;
        }
        if (points[0].getY() > points[2].getY()) {
            Vector2 temp = points[0];
            points[0] = points[2];
            points[2] = temp;
        }
        if (points[1].getY() > points[2].getY()) {
            Vector2 temp = points[1];
            points[1] = points[2];
            points[2] = temp;
        }
        return points;
    }

}

