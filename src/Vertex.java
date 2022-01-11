public class Vertex {

    private Vector3 position;
    private Vector2 textCoord;

    public Vertex(Vector3 position) {
        this.position = position;
    }
    public Vertex(Vector3 position, Vector2 textCoord) {
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

    public Vector2 getTextCoord() {
        return textCoord;
    }

    public void setTextCoord(Vector2 textCoord) {
        this.textCoord = textCoord;
    }
}
