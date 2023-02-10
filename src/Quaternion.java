public class Quaternion {

    private double x;
    private double y;
    private double z;
    private double w;

    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public double magnitude() {
        return (Math.sqrt((x * x) + (y * y) + (z * z) + (w * w)));
    }

    public double sqrMagnitude() {
        return (x * x) + (y * y) + (z * z) + (w * w);
    }

    public Quaternion normalized() {
        double magnitude = this.magnitude();
        return new Quaternion(x / magnitude, y / magnitude, z / magnitude, w / magnitude);
    }

    public Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }

    public Quaternion multiply(Quaternion quaternion) {
        double newW = w * quaternion.getW() - x * quaternion.getX() - y * quaternion.getY() - z * quaternion.getZ();
        double newX = x * quaternion.getW() + w * quaternion.getX() + y * quaternion.getZ() - z * quaternion.getY();
        double newY = y * quaternion.getW() + w * quaternion.getY() + z * quaternion.getX() - x * quaternion.getZ();
        double newZ = z * quaternion.getW() + w * quaternion.getZ() + x * quaternion.getY() - y * quaternion.getX();

        return new Quaternion(newX, newY, newZ, newW);
    }

    public Quaternion multiply(Vector3 vector) {
        double newW = -x * vector.getX() - y * vector.getY() - z * vector.getZ();
        double newX =  w * vector.getX() + y * vector.getZ() - z * vector.getY();
        double newY =  w * vector.getY() + z * vector.getX() - x * vector.getZ();
        double newZ =  w * vector.getZ() + x * vector.getY() - y * vector.getX();

        return new Quaternion(newX, newY, newZ, newW);
    }

    public static Quaternion fromAxisAngle(Vector3 axis, double angle) {
        double sinHalfAngle = Math.sin(Math.toRadians(angle) / 2);
        double cosHalfAngle = Math.cos(Math.toRadians(angle) / 2);

        return new Quaternion(
                axis.getX() * sinHalfAngle,
                axis.getY() * sinHalfAngle,
                axis.getZ() * sinHalfAngle,
                cosHalfAngle
        );
    }

    public Matrix4 toRotationMatrix() {
        Matrix4 matrix = new Matrix4();

        double xy = x * y;
        double xz = x * z;
        double xw = x * w;
        double yz = y * z;
        double yw = y * w;
        double zw = z * w;
        double xSquared = x * x;
        double ySquared = y * y;
        double zSquared = z * z;

        matrix.set(0, 0, 1 - 2 * (ySquared + zSquared));
        matrix.set(0, 1, 2 * (xy - zw));
        matrix.set(0, 2, 2 * (xz + yw));
        matrix.set(1, 0, 2 * (xy + zw));
        matrix.set(1, 1, 1 - 2 * (xSquared + zSquared));
        matrix.set(1, 2, 2 * (yz - xw));
        matrix.set(2, 0, 2 * (xz - yw));
        matrix.set(2, 1, 2 * (yz + xw));
        matrix.set(2, 2, 1 - 2 * (xSquared + ySquared));
        matrix.set(3, 3, 1);

        return matrix;
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

    public Quaternion clone() {
        return new Quaternion(this.x, this.y, this.z, this.w);
    }
}
