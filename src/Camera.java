public class Camera {

    private Vector3 position;
    private Vector3 forward;
    private Vector3 up;

    public Camera(Vector3 position, Vector3 forward, Vector3 up) {
        this.position = position;
        this.forward = forward.normalized();
        this.up = up.normalized();
    }

    public Camera() {
        this(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(0, 1, 0));
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Vector3 getForward() {
        return forward;
    }

    public void setForward(Vector3 forward) {
        this.forward = forward.normalized();
    }

    public Vector3 getUp() {
        return up;
    }

    public void setUp(Vector3 up) {
        this.up = up.normalized();
    }

    public Vector3 getLeft() {
        Vector3 left = forward.cross(up);
        return left.normalized();
    }

    public Vector3 getRight() {
        Vector3 right = up.cross(forward);
        return right.normalized();
    }

    public Vector3 getDown() {
        return up.multiply(-1).normalized();
    }

    public Vector3 getBackward() {
        return forward.multiply(-1).normalized();
    }

    public void move(Vector3 direction, double amount) {
        position = position.add(direction.multiply(amount));
    }

    public void rotate(double angleX, double angleY, double angleZ) {
        Vector3 horizontalAxis = Vector3.UP.cross(forward).normalized();

        Matrix4 m = Matrix4.getRotationMatrix(angleX, angleY, angleZ);
        forward = forward.multiply(m).normalized();

        up = forward.cross(horizontalAxis).normalized();

    }

}
