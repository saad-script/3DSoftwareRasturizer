import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.TransferQueue;


public class Triangle {

    Vertex[] vertices = new Vertex[3];
    Vector3 normal;
    Color color;

    public Triangle(Vertex point1, Vertex point2, Vertex point3) {
        vertices[0] = point1;
        vertices[1] = point2;
        vertices[2] = point3;
        recalculateNormal();
    }

    public Triangle(Vertex[] vertices) {
        if (vertices.length != 3)
            throw new IllegalArgumentException("Triangle can only have 3 vertices.");
        this.vertices = vertices;
        recalculateNormal();
    }

    public Triangle(Vector3 point1, Vector3 point2, Vector3 point3) {
        vertices[0] = new Vertex(point1);
        vertices[1] = new Vertex(point2);
        vertices[2] = new Vertex(point3);
        recalculateNormal();
    }

    public Triangle(Vector3[] points) {
        if (points.length != 3)
            throw new IllegalArgumentException("Triangle can only have 3 points.");
        for (int i = 0; i < points.length; i++)
            vertices[i] = new Vertex(points[i]);
        recalculateNormal();
    }

    public Vertex getVertex(int index) {
        return vertices[index];
    }

    public Vector3 getPosition(int index) {
        return vertices[index].getPosition();
    }

    public Vector3 getTextureCoord(int index) {
        return vertices[index].getTextCoord();
    }

    public Vertex[] getVertices() {
        return vertices;
    }
    public Vector3[] getPositions() {
        return new Vector3[]{vertices[0].getPosition(), vertices[1].getPosition(), vertices[2].getPosition()};
    }
    public Vector3[] getTextureCoords() {
        return new Vector3[]{vertices[0].getTextCoord(), vertices[1].getTextCoord(), vertices[2].getTextCoord()};
    }
    public Vector3 recalculateNormal() {
        Vector3 line1 = vertices[1].getPosition().subtract(vertices[0].getPosition()).normalized();
        Vector3 line2 = vertices[2].getPosition().subtract(vertices[0].getPosition()).normalized();
        normal = line1.cross(line2).normalized();
        return normal;
    }
    public Triangle clone() {
        return new Triangle(vertices[0].clone(), vertices[1].clone(), vertices[2].clone());
    }

}

