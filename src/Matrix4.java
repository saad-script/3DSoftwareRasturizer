public class Matrix4 {

    private double[][] matrix;

    public Matrix4() {
        matrix = new double[4][4];
    }

    public Matrix4 initIdentity() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == j)
                    matrix[i][j] = 1.0f;
                else
                    matrix[i][j] = 0.0f;
            }
        }
        return this;
    }

    public Matrix4 multiply(Matrix4 otherMatrix) {
        Matrix4 result = new Matrix4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {

                result.matrix[i][j] =
                        matrix[i][0] * otherMatrix.matrix[0][j] +
                        matrix[i][1] * otherMatrix.matrix[1][j] +
                        matrix[i][2] * otherMatrix.matrix[2][j] +
                        matrix[i][3] * otherMatrix.matrix[3][j];

            }
        }
        return result;
    }

    public Matrix4 transpose() {
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                matrix[i][j] = matrix[j][i];
        return this;
    }

    public double get(int x, int y) {
        return matrix[x][y];
    }

    public void set(int x, int y, double value) {
        matrix[x][y] = value;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public static Matrix4 getTranslationMatrix(double x, double y, double z) {
        Matrix4 m = Matrix4.getIdentityMatrix();

        m.matrix[3][0] = x;
        m.matrix[3][1] = y;
        m.matrix[3][2] = z;

        return m;
    }

    public static Matrix4 getRotationMatrix(double x, double y, double z) {
        Matrix4 rx = Matrix4.getIdentityMatrix();
        Matrix4 ry = Matrix4.getIdentityMatrix();
        Matrix4 rz = Matrix4.getIdentityMatrix();

        x = Math.toRadians(x);
        y = Math.toRadians(y);
        z = Math.toRadians(z);

        rx.matrix[1][1] = Math.cos(x); rx.matrix[1][2] = Math.sin(x);
        rx.matrix[2][1] = -Math.sin(x); rx.matrix[2][2] = Math.cos(x);

        ry.matrix[0][0] = Math.cos(y); ry.matrix[0][2] = -Math.sin(y);
        ry.matrix[2][0] = Math.sin(y); ry.matrix[2][2] = Math.cos(y);

        rz.matrix[0][0] = Math.cos(z); rz.matrix[0][1] = -Math.sin(z);
        rz.matrix[1][0] = Math.sin(z); rz.matrix[1][1] = Math.cos(z);

        return rx.multiply(ry).multiply(rz);
    }

    public static Matrix4 getScaleMatrix(double x, double y, double z) {
        Matrix4 m = new Matrix4();
        m.matrix[0][0] = x;
        m.matrix[1][1] = y;
        m.matrix[2][2] = z;
        m.matrix[3][3] = 1.0f;
        return m;
    }

    public static Matrix4 getIdentityMatrix() {
        Matrix4 m = new Matrix4();
        m.matrix[0][0] = 1.0f;
        m.matrix[1][1] = 1.0f;
        m.matrix[2][2] = 1.0f;
        m.matrix[3][3] = 1.0f;
        return m;
    }

    public static Matrix4 getProjectionMatrix(double width, double height, double FOV, double zNearClip, double zFarClip) {
        Matrix4 m = new Matrix4();
        double tanHalfFOV = (Math.tan(Math.toRadians(FOV / 2)));
        double aspectRatio = height / width;
        m.matrix[0][0] = aspectRatio / (tanHalfFOV);
        m.matrix[1][1] = 1.0f / tanHalfFOV;
        m.matrix[2][2] = (zFarClip) / (zFarClip - zNearClip);
        m.matrix[3][2] = (-zFarClip * zNearClip) / (zFarClip - zNearClip);
        m.matrix[2][3] = 1;

        return m;
    }

    public static Matrix4 getViewMatrix(Vector3 forward, Vector3 up, Vector3 pos) {

        Matrix4 m = new Matrix4();
        Vector3 forwardCopy = forward.clone().normalized();
        Vector3 upCopy = up.clone().normalized();

        Vector3 right = upCopy.cross(forwardCopy);


        m.matrix[0][0] = right.getX();          m.matrix[0][1] = upCopy.getX();         m.matrix[0][2] = forwardCopy.getX();        //m.matrix[0][3] = pos.getX();
        m.matrix[1][0] = right.getY();          m.matrix[1][1] = upCopy.getY();         m.matrix[1][2] = forwardCopy.getY();        //m.matrix[1][3] = pos.getY();
        m.matrix[2][0] = right.getZ();          m.matrix[2][1] = upCopy.getZ();         m.matrix[2][2] = forwardCopy.getZ();        //m.matrix[2][3] = pos.getZ();
        m.matrix[3][3] = 1;

        return m;
    }


}
