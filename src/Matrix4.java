public class Matrix4 {

    public static final Matrix4 identityMatrix = getIdentityMatrix();
    public static Matrix4 translationMatrix = getIdentityMatrix();
    public static Matrix4 rotationMatrix = getIdentityMatrix();
    public static Matrix4 scaleMatrix = getIdentityMatrix();
    public static Matrix4 projectionMatrix = new Matrix4();
    public static Matrix4 viewMatrix = new Matrix4();
    private double[][] values;

    public Matrix4() {
        values = new double[4][4];
    }

    public Matrix4 initIdentity() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == j)
                    values[i][j] = 1.0f;
                else
                    values[i][j] = 0.0f;
            }
        }
        return this;
    }

    public Matrix4 multiply(Matrix4 otherMatrix) {
        Matrix4 result = new Matrix4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {

                result.values[i][j] =
                        values[i][0] * otherMatrix.values[0][j] +
                        values[i][1] * otherMatrix.values[1][j] +
                        values[i][2] * otherMatrix.values[2][j] +
                        values[i][3] * otherMatrix.values[3][j];

            }
        }
        return result;
    }

    public double get(int x, int y) {
        return values[x][y];
    }

    public void set(int x, int y, double value) {
        values[x][y] = value;
    }

    public double[][] getMatrix() {
        return values;
    }

    public void setMatrix(double[][] values) {
        this.values = values;
    }

    public static Matrix4 getTranslationMatrix(double x, double y, double z) {

        translationMatrix.values[3][0] = x;
        translationMatrix.values[3][1] = y;
        translationMatrix.values[3][2] = z;

        return translationMatrix;
    }

    public static Matrix4 getRotationMatrix(double x, double y, double z) {
        Matrix4 rx = getIdentityMatrix();
        Matrix4 ry = getIdentityMatrix();
        Matrix4 rz = getIdentityMatrix();

        x = Math.toRadians(x);
        y = Math.toRadians(y);
        z = Math.toRadians(z);

        rx.values[1][1] = Math.cos(x); rx.values[1][2] = -Math.sin(x);
        rx.values[2][1] = Math.sin(x); rx.values[2][2] = Math.cos(x);

        ry.values[0][0] = Math.cos(y); ry.values[0][2] = -Math.sin(y);
        ry.values[2][0] = Math.sin(y); ry.values[2][2] = Math.cos(y);

        rz.values[0][0] = Math.cos(z); rz.values[0][1] = -Math.sin(z);
        rz.values[1][0] = Math.sin(z); rz.values[1][1] = Math.cos(z);

        return rx.multiply(ry).multiply(rz);
    }

    public static Matrix4 getScaleMatrix(double x, double y, double z) {
        scaleMatrix.values[0][0] = x;
        scaleMatrix.values[1][1] = y;
        scaleMatrix.values[2][2] = z;
        scaleMatrix.values[3][3] = 1.0f;
        return scaleMatrix;
    }

    public static Matrix4 getIdentityMatrix() {
        Matrix4 m = new Matrix4();
        m.values[0][0] = 1.0f;
        m.values[1][1] = 1.0f;
        m.values[2][2] = 1.0f;
        m.values[3][3] = 1.0f;
        return m;
    }

    public static Matrix4 getProjectionMatrix(double width, double height, double FOV, double zNearClip, double zFarClip) {

        double tanHalfFOV = (Math.tan(Math.toRadians(FOV / 2)));
        double aspectRatio = height / width;
        projectionMatrix.values[0][0] = aspectRatio / (tanHalfFOV);
        projectionMatrix.values[1][1] = 1.0f / tanHalfFOV;
        projectionMatrix.values[2][2] = (zFarClip) / (zFarClip - zNearClip);
        projectionMatrix.values[3][2] = (-zFarClip * zNearClip) / (zFarClip - zNearClip);
        projectionMatrix.values[2][3] = 1;

        return projectionMatrix;
    }

    public static Matrix4 getViewMatrix(Vector3 forward, Vector3 up) {

        Vector3 forwardCopy = forward.clone().normalized();
        Vector3 upCopy = up.clone().normalized();
        Vector3 right = upCopy.cross(forwardCopy);

        viewMatrix.values[0][0] = right.getX();          viewMatrix.values[0][1] = upCopy.getX();         viewMatrix.values[0][2] = forwardCopy.getX();
        viewMatrix.values[1][0] = right.getY();          viewMatrix.values[1][1] = upCopy.getY();         viewMatrix.values[1][2] = forwardCopy.getY();
        viewMatrix.values[2][0] = right.getZ();          viewMatrix.values[2][1] = upCopy.getZ();         viewMatrix.values[2][2] = forwardCopy.getZ();
        viewMatrix.values[3][3] = 1;

        return viewMatrix;
    }


}
