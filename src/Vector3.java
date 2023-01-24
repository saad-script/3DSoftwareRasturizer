public class Vector3 {

    public static final Vector3 ZERO = new Vector3(0, 0 ,0);
    public static final Vector3 UP = new Vector3(0, 1, 0);
    public static final Vector3 FORWARD = new Vector3(0, 0, 1);
    public static final Vector3 RIGHT = new Vector3(1, 0, 0);
    public static final Vector3 DOWN = new Vector3(0, -1, 0);
    public static final Vector3 BACKWARD = new Vector3(0, 0, -1);
    public static final Vector3 LEFT = new Vector3(-1, 0, 0);

    private double x;
    private double y;
    private double z;
    private double w = 1;

    public Vector3(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    public double magnitude() {
        return Math.sqrt((x * x) + (y * y) + (z * z));
    }

    public double sqrMagnitude() {
        return ((x * x) + (y * y) + (z * z));
    }

    public double dot(Vector3 otherVector) {
        return (x * otherVector.x) + (y * otherVector.y) + (z * otherVector.z);
    }

    public Vector3 cross(Vector3 otherVector) {
        double newX = y * otherVector.z - z * otherVector.y;
        double newY = z * otherVector.x - x * otherVector.z;
        double newZ = x * otherVector.y - y * otherVector.x;

        return new Vector3(newX, newY, newZ);
    }

    public Vector3 normalized() {
        double magnitude = this.magnitude();
        return new Vector3(x / magnitude, y / magnitude, z / magnitude);
    }

    public Vector3 add(Vector3 otherVector) {
        return new Vector3(x + otherVector.x, y + otherVector.y, z + otherVector.z);
    }

    public Vector3 subtract(Vector3 otherVector) {
        return new Vector3(x - otherVector.x, y - otherVector.y, z - otherVector.z);
    }

    public Vector3 multiply(double number) {
        return new Vector3(x * number, y * number, z * number);
    }

    public Vector3 multiply(Matrix4 m) {
        Vector3 result = new Vector3(0, 0, 0);
        result.x = x * m.get(0, 0) + y * m.get(1, 0) + z * m.get(2, 0) + m.get(3, 0);
        result.y = x * m.get(0, 1) + y * m.get(1, 1) + z * m.get(2, 1) + m.get(3, 1);
        result.z = x * m.get(0, 2) + y * m.get(1, 2) + z * m.get(2, 2) + m.get(3, 2);
        result.w = x * m.get(0, 3) + y * m.get(1, 3) + z * m.get(2, 3) + m.get(3, 3);
        /*if (w != 0) {
            result.x /= w;
            result.y /= w;
            result.z /= w;
        }*/
        return result;
    }

    public Vector3 divide(double number) {
        return new Vector3(x / number, y / number, z / number);
    }

    public Vector3 abs(Vector3 vector) {
        return new Vector3(Math.abs(vector.x), Math.abs(vector.y), Math.abs(vector.z));
    }

    public Vector3 clampNormalized() {
        Vector3 result = this.clone();
        result.x = clamp(x, 0, 1);
        result.y = clamp(y, 0, 1);
        result.z = clamp(z, 0, 1);
        return result;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public Vector3 clone() {
        return new Vector3(x, y, z);
    }

    public void copy(Vector3 otherVector) {
        this.x = otherVector.x;
        this.y = otherVector.y;
        this.z = otherVector.z;
        this.w = otherVector.w;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }


    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

}
