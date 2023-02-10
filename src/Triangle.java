import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.TransferQueue;


public class Triangle{

    Vertex[] vertices = new Vertex[3];
    Vector3 normal;
    double diffuse;

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

    public Triangle clone() {
        return new Triangle(vertices[0].clone(), vertices[1].clone(), vertices[2].clone());
    }
}

