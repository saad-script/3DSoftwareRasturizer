import javafx.scene.paint.Color;

import java.awt.*;

public class Vertex {

    private Vector3 position;
    private Vector3 textCoord = new Vector3(0, 0);
    private Vector3 normal;
    private Color color;

    public Vertex(Vector3 position) {
        this.position = position;
    }
    public Vertex(Vector3 position, Vector3 textCoord) {
        this.position = position;
        this.textCoord = textCoord;
    }

    public Vertex(double xPos, double yPos, double zPos) {
       this(new Vector3(xPos, yPos, zPos));
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Vector3 getTextCoord() {
        return textCoord;
    }

    public void setTextCoord(Vector3 textCoord) {
        this.textCoord = textCoord;
    }

    public double getX() {
        return getPosition().getX();
    }

    public double getY() {
        return getPosition().getY();
    }

    public double getZ() {
        return getPosition().getZ();
    }

    public double getU() {
        return getTextCoord().getX();
    }

    public double getV() {
        return getTextCoord().getY();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Vector3 getNormal() {
        return normal;
    }

    public Vertex clone() {
        return new Vertex(position.clone(), textCoord.clone());
    }
}
