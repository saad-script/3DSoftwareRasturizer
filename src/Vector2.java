
public class Vector2 {

    protected double x;
    protected double y;
    protected double w = 1;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double magnitude() {
        return Math.sqrt((x * x) + (y * y));
    }

    public double sqrMagnitude() {
        return ((x * x) + (y * y));
    }

    public double dot(Vector2 otherVector) {
        return (x * otherVector.x) + (y + otherVector.y);
    }

    public Vector2 normalized() {
        double magnitude = this.magnitude();
        return new Vector2(x / magnitude, y / magnitude);
    }

    public Vector2 rotate(double angle) {

        double radians = Math.toRadians(angle);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);


        return new Vector2((x * cos - y * sin), (x * sin + y * cos));
    }

    public Vector2 add(Vector2 otherVector) {
        return new Vector2(x + otherVector.x, y + otherVector.y);
    }

    public Vector2 subtract(Vector2 otherVector) {
        return new Vector2(x - otherVector.x, y - otherVector.y);
    }

    public Vector2 multiply(double number) {
        return new Vector2(x * number, y * number);
    }

    public Vector2 divide(double number) {
        return new Vector2(x / number, y / number);
    }

    public Vector2 abs(Vector2 vector) {
        return new Vector2(Math.abs(vector.x), Math.abs(vector.y));
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

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public Vector2 clone() {
        return new Vector2(this.x, this.y);
    }
}
