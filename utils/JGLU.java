package utils;

public class JGLU {

    public static final int GL_MODELVIEW = 0;
    public static final int GL_PROJECTION = 1;

    private MyStack<MyMatrix> modelview_stack;
    private MyStack<MyMatrix> projection_stack;

    private int stack_active = GL_MODELVIEW;

    public JGLU() {
        MyMatrix m = new MyMatrix();

        this.modelview_stack = new MyStack<MyMatrix>();
        this.projection_stack = new MyStack<MyMatrix>();
        this.modelview_stack.insert(m);
        this.projection_stack.insert(m);
    }

    public void gluMatrixMode(int type_matrix) {
        if (type_matrix == GL_MODELVIEW || type_matrix == GL_PROJECTION) {
            this.stack_active = type_matrix;
        } else {
            System.out.println("gluMatrixMode:Error - Invalid parameter");
            System.exit(1);
        }
    }

    private void setStack(MyMatrix m) {
        if (stack_active == GL_MODELVIEW) {
            this.modelview_stack.replaceTop(m);
        } else if (stack_active == GL_PROJECTION) {
            this.projection_stack.replaceTop(m);
        }
    }

    public float[] gluGetMatrix() {
        if (this.stack_active == GL_MODELVIEW) {
            return this.modelview_stack.getTop().getMatrix();
        } else if (stack_active == GL_PROJECTION) {
            return this.projection_stack.getTop().getMatrix();
        } else {
            return (new float[1]);
        }
    }

    public void printStack() {
        if (this.stack_active == GL_MODELVIEW) {
            System.out.println("Modelview Stack");
            this.modelview_stack.printStack();
        } else if (stack_active == GL_PROJECTION) {
            System.out.println("Projection Stack");
            this.projection_stack.printStack();
        }
    }

    public void gluLoadMatrixf(float[] m_in) {
        MyMatrix matrix_aux = new MyMatrix();
        matrix_aux.setMatrix16fv(m_in);
        matrix_aux.transposeMatrix();
        this.setStack(matrix_aux);
    }

    public void gluLoadMatrixd(double[] m_in) {
        MyMatrix matrix_aux = new MyMatrix();
        matrix_aux.setMatrix16dv(m_in);
        matrix_aux.transposeMatrix();
        this.setStack(matrix_aux);
    }

    public void gluLoadIdentity() {
        MyMatrix m = new MyMatrix();
        float[] identity = new float[] { 1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f };

        m.setMatrix9fv(identity);
        this.setStack(m);

    }

    public void gluMultMatrixf(float[] m) {
        MyMatrix m_trans = new MyMatrix();

        m_trans.setMatrix16fv(m);
        m_trans.transposeMatrix();

        m_trans.multiMatrixf(this.gluGetMatrix());
        this.setStack(m_trans);
    }

    public void gluMultMatrixd(double[] m) {
        MyMatrix m_trans = new MyMatrix();
        m_trans.setMatrix16dv(m);
        m_trans.transposeMatrix();

        m_trans.multiMatrixf(this.gluGetMatrix());
        this.setStack(m_trans);
    }

    public void gluFrustum(double left, double right,
            double bottom, double top,
            double near, double far) {

        if (near <= 0.0 || far <= 0.0 || Math.abs(right - left) < 0.00001 || Math.abs(bottom - top) < 0.00001
                || Math.abs(far - near) < 0.00001) {
            System.out.println("gluFrustum:Error - Invalid parameter");
            System.exit(1);
        }

        double[] matrix_aux;

        matrix_aux = new double[] { (2.0 * near / (right - left)), 0.0, ((right + left) / (right - left)), 0.0,
                0.0, (2.0 * near / (top - bottom)), ((top + bottom) / (top - bottom)), 0.0,
                0.0, 0.0, (-(far + near) / (far - near)), (-2.0 * far * near / (far - near)),
                0.0, 0.0, -1.0, 0.0 };

        this.gluMultMatrixd(matrix_aux);
    }

    public void gluOrtho(double left, double right,
            double bottom, double top,
            double near, double far) {

        if (Math.abs(right - left) < 0.00001 || Math.abs(bottom - top) < 0.00001 || Math.abs(far - near) < 0.00001) {
            System.out.println("gluOrtho:Error - Invalid parameter");
            System.exit(1);
        }

        double[] matrix_aux;

        matrix_aux = new double[] { (2.0 / (right - left)), 0.0, 0.0, (-(right + left) / (right - left)),
                0.0, (2.0 / (top - bottom)), 0.0, (-(top + bottom) / (top - bottom)),
                0.0, 0.0, (-2.0 / (far - near)), (-(far + near) / (far - near)),
                0.0, 0.0, 0.0, 1.0 };

        this.gluMultMatrixd(matrix_aux);

    }

    public void gluLookAt(double ex, double ey, double ez,
            double cx, double cy, double cz,
            double ux, double uy, double uz) {

        double[] matrix_aux;
        double[] direction = new double[3];
        double[] normal_1 = new double[3];
        double[] normal_2 = new double[3];
        double direction_norm;
        double normal_1_norm;
        direction[0] = cx - ex;
        direction[1] = cy - ey;
        direction[2] = cz - ez;

        direction_norm = Math.sqrt(Math.pow(direction[0], 2) + Math.pow(direction[1], 2) + Math.pow(direction[2], 2));

        direction[0] = direction[0] / direction_norm;
        direction[1] = direction[1] / direction_norm;
        direction[2] = direction[2] / direction_norm;

        normal_1[0] = direction[1] * uz - direction[2] * uy;
        normal_1[1] = direction[2] * ux - direction[0] * uz;
        normal_1[2] = direction[0] * uy - direction[1] * ux;

        normal_1_norm = Math.sqrt(Math.pow(normal_1[0], 2) + Math.pow(normal_1[1], 2) + Math.pow(normal_1[2], 2));

        normal_1[0] = normal_1[0] / normal_1_norm;
        normal_1[1] = normal_1[1] / normal_1_norm;
        normal_1[2] = normal_1[2] / normal_1_norm;

        normal_2[0] = normal_1[1] * direction[2] - normal_1[2] * direction[1];
        normal_2[1] = normal_1[2] * direction[0] - normal_1[0] * direction[2];
        normal_2[2] = normal_1[0] * direction[1] - normal_1[1] * direction[0];

        matrix_aux = new double[] { normal_1[0], normal_1[1], normal_1[2], 0.0,
                normal_2[0], normal_2[1], normal_2[2], 0.0,
                -direction[0], -direction[1], -direction[2], 0.0,
                0.0, 0.0, 0.0, 1.0 };

        this.gluMultMatrixd(matrix_aux);
        this.gluTranslated(-ex, -ey, -ez);
    }

    public void gluTranslatef(float tx, float ty, float tz) {
        float[] matrix_aux;
        matrix_aux = new float[] { 1.0f, 0.0f, 0.0f, tx,
                0.0f, 1.0f, 0.0f, ty,
                0.0f, 0.0f, 1.0f, tz,
                0.0f, 0.0f, 0.0f, 1.0f };

        this.gluMultMatrixf(matrix_aux);

    }

    public void gluTranslated(double tx, double ty, double tz) {
        double[] matrix_aux;
        matrix_aux = new double[] { 1.0, 0.0, 0.0, tx,
                0.0, 1.0, 0.0, ty,
                0.0, 0.0, 1.0, tz,
                0.0, 0.0, 0.0, 1.0 };

        this.gluMultMatrixd(matrix_aux);
    }

    public void gluRotatef(float angle, float x, float y, float z) {
        float xn, yn, zn, vnorm, cos, sin;
        float[] matrix_aux;

        vnorm = (float) Math.sqrt(x * x + y * y + z * z);
        xn = (x) / (vnorm);
        yn = (y) / (vnorm);
        zn = (z) / (vnorm);
        cos = (float) Math.cos(angle * Math.PI / 180.0f);
        sin = (float) Math.sin(angle * Math.PI / 180.0f);

        matrix_aux = new float[] { xn * xn * (1 - cos) + cos, xn * yn * (1 - cos) - zn * sin,
                xn * zn * (1 - cos) + yn * sin, 0.0f,
                xn * yn * (1 - cos) + zn * sin, yn * yn * (1 - cos) + cos, yn * zn * (1 - cos) - xn * sin, 0.0f,
                xn * zn * (1 - cos) - yn * sin, yn * zn * (1 - cos) + xn * sin, zn * zn * (1 - cos) + cos, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f };

        this.gluMultMatrixf(matrix_aux);

    }

    public void gluRotated(double angle, double x, double y, double z) {
        double xn, yn, zn, vnorm, cos, sin;
        double[] matrix_aux;

        vnorm = Math.sqrt(x * x + y * y + z * z);
        xn = (x) / (vnorm);
        yn = (y) / (vnorm);
        zn = (z) / (vnorm);
        cos = Math.cos(angle * Math.PI / 180.0f);
        sin = Math.sin(angle * Math.PI / 180.0f);

        matrix_aux = new double[] { xn * xn * (1 - cos) + cos, xn * yn * (1 - cos) - zn * sin,
                xn * zn * (1 - cos) + yn * sin, 0.0,
                xn * yn * (1 - cos) + zn * sin, yn * yn * (1 - cos) + cos, yn * zn * (1 - cos) - xn * sin, 0.0,
                xn * zn * (1 - cos) - yn * sin, yn * zn * (1 - cos) + xn * sin, zn * zn * (1 - cos) + cos, 0.0,
                0.0, 0.0, 0.0, 1.0 };

        this.gluMultMatrixd(matrix_aux);

    }

    public void gluScalef(float sx, float sy, float sz) {
        float[] matrix_aux;

        matrix_aux = new float[] { sx, 0.0f, 0.0f, 0.0f,
                0.0f, sy, 0.0f, 0.0f,
                0.0f, 0.0f, sz, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f };

        this.gluMultMatrixf(matrix_aux);
    }

    public void gluScaled(double sx, double sy, double sz) {
        double[] matrix_aux;

        matrix_aux = new double[] { sx, 0.0, 0.0, 0.0,
                0.0, sy, 0.0, 0.0,
                0.0, 0.0, sz, 0.0,
                0.0, 0.0, 0.0, 1.0 };

        this.gluMultMatrixd(matrix_aux);
    }

    public void gluPushMatrix() {
        if (stack_active == GL_MODELVIEW) {
            modelview_stack.push();
        } else if (stack_active == GL_PROJECTION) {
            projection_stack.push();
        }
    }

    public void gluPopMatrix() {
        if (stack_active == GL_MODELVIEW) {
            modelview_stack.pop();
        } else if (stack_active == GL_PROJECTION) {
            projection_stack.pop();
        }
    }
}
