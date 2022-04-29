import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Mesh {

    Triangle[] triangles;
    Vector3 center;

    Color baseColor = new Color(1, 0, 0, 1);

    Mesh(Triangle[] triangles) {
        this.triangles = triangles;
        center = calculateCenter();
    }

    Mesh(Vertex[] vertices) {
        if (vertices.length < 3 || vertices.length % 3 != 0)
            throw new IllegalArgumentException("Number of vertices specified for mesh must by a multiple of 3.");

        triangles = new Triangle[vertices.length / 3];

        int counter = 0;
        for (int i = 0; i < vertices.length; i+=3) {
            triangles[counter] = new Triangle(vertices[i], vertices[i + 1], vertices[i + 2]);
            counter++;
        }

        center = calculateCenter();
    }

    Mesh(String OBJFilePath) {

        try {
            Scanner scanner = new Scanner(new FileReader(OBJFilePath));
            ArrayList<Vertex> vertices = new ArrayList<>();
            ArrayList<Vector3> positions = new ArrayList<>();
            ArrayList<Vector3> textCoords = new ArrayList<>();
            ArrayList<Triangle> triangles = new ArrayList<>();
            while (scanner.hasNext()) {
                String line = scanner.nextLine().trim();

                if (line.isBlank())
                    continue;

                String[] tokens = line.split(" ");

                if (tokens[0].equals("v")) {
                    double x = Double.parseDouble(tokens[1]);
                    double y = Double.parseDouble(tokens[2]);
                    double z = Double.parseDouble(tokens[3]);
                    positions.add(new Vector3(x, y, z));
                } else if (tokens[0].equals("vt")) {
                    double u = Double.parseDouble(tokens[1]);
                    double v = Double.parseDouble(tokens[2]);
                    textCoords.add(new Vector3(u, v));
                } else if (tokens[0].equals("f")) {
                    String[] components1 = tokens[1].split("/");
                    String[] components2 = tokens[2].split("/");
                    String[] components3 = tokens[3].split("/");

                    int numData = components3.length;

                    int positionIndex1 = Integer.parseInt(components1[0]) - 1;
                    int positionIndex2 = Integer.parseInt(components2[0]) - 1;
                    int positionIndex3 = Integer.parseInt(components3[0]) - 1;

                    int textureIndex1 = 0;
                    int textureIndex2 = 0;
                    int textureIndex3 = 0;
                    if (numData > 1) {
                        textureIndex1 = Integer.parseInt(components1[1]) - 1;
                        textureIndex2 = Integer.parseInt(components2[1]) - 1;
                        textureIndex3 = Integer.parseInt(components3[1]) - 1;
                    }

                    if (textCoords.size() == 0)
                        textCoords.add(new Vector3(0, 0 , 0));
                    Vertex vertex1 = new Vertex(positions.get(positionIndex1), textCoords.get(textureIndex1));
                    Vertex vertex2 = new Vertex(positions.get(positionIndex2), textCoords.get(textureIndex2));
                    Vertex vertex3 = new Vertex(positions.get(positionIndex3), textCoords.get(textureIndex3));
                    triangles.add(new Triangle(vertex1, vertex2, vertex3));
                }

            }


            this.triangles = new Triangle[triangles.size()];
            triangles.toArray(this.triangles);


            center = calculateCenter();

        } catch (IOException e) {
            System.out.println("OBJ File '" + OBJFilePath +  "' not found.");
            e.printStackTrace();
        }
    }

    private Vector3 calculateCenter() {
        Vector3 center = new Vector3(0, 0, 0);
        for (Triangle triangle : triangles) {

            for (Vertex vertex : triangle.vertices) {

                center = center.add(vertex.getPosition());

            }

        }

        return center.divide(triangles.length * 3);
    }



}
