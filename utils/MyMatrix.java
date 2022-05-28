package utils;

public class MyMatrix {
    private float[] matrix;

    public MyMatrix() {
        this.matrix = new float[] { 1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f };
    }

    public MyMatrix(float[] m) {
        this.matrix = new float[16];

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.matrix[4 * i + j] = m[4 * i + j];
            }
        }
    }

    public float[] getMatrix() {
        return this.matrix;
    }

    public void transposeMatrix() {
        float[] m_aux = new float[16];

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                m_aux[4 * i + j] = this.matrix[4 * i + j];
            }
        }

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.matrix[4 * i + j] = m_aux[4 * j + i];
            }
        }
    }

    public void setMatrix9fv(float[] m) {
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 3; ++j)
                matrix[4 * i + j] = m[3 * i + j];
    }

    public void setMatrix9dv(double[] m) {
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 3; ++j) {
                Double aux = new Double(m[3 * i + j]);
                matrix[4 * i + j] = aux.floatValue();
            }
    }

    public void setMatrix16fv(float[] m) {
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j) {
                matrix[4 * i + j] = m[4 * i + j];
            }
    }

    public void setMatrix16dv(double[] m) {
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j) {
                Double aux = new Double(m[4 * i + j]);
                matrix[4 * i + j] = aux.floatValue();
            }
    }

    public void printMatrix() {
        for (int i = 0; i < this.matrix.length; ++i) {
            if ((i + 1) % 4 == 0 && i != 0)
                System.out.println(this.matrix[i]);
            else
                System.out.print(this.matrix[i] + " ");
        }
        System.out.println("");
    }

    public void multiMatrixf(float[] m) {
        float[] m_aux = new float[16];

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                m_aux[4 * i + j] = matrix[4 * i + j];
            }
        }

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                float k = 0.0f;
                for (int n = 0; n < 4; ++n) {
                    k += m_aux[4 * i + n] * m[4 * n + j];
                }
                this.matrix[4 * i + j] = k;
            }
        }
    }

    public void multiMatrixd(double[] m) {
        Double[] m_aux = new Double[16];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                Float aux = new Float(matrix[4 * i + j]);
                m_aux[4 * i + j] = aux.doubleValue();
            }
        }

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                Double k = new Double(0.0);
                for (int n = 0; n < 4; ++n) {
                    k += m_aux[4 * i + n] * m[4 * n + j];
                }
                this.matrix[4 * i + j] = k.floatValue();
            }
        }
    }

    public String toString() {
        String str = new String();

        for (int i = 0; i < this.matrix.length; ++i) {
            if ((i + 1) % 4 == 0 && i != 0) {
                str += (Float.toString(this.matrix[i]) + "\n");
            } else {
                str += (Float.toString(this.matrix[i]) + " ");
            }
        }
        return str;
    }

}
