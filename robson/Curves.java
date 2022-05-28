package robson;

import java.nio.FloatBuffer;

import utils.MyMatrix;

import com.jogamp.common.nio.Buffers;

public class Curves {
    public static FloatBuffer generateCircumference(float[] center, float radius, int num_vertices, float[] up) {
        if (Math.abs(radius - 0.0) < 0.00001) {
            System.out.println("generateCircumference: Error - radius null");
            System.exit(1);
        }

        float norm = 0.0f;
        float vec[] = new float[4];
        float t = 0.0f;
        float t_step = (2.0f * (float) Math.PI) / ((float) num_vertices);
        float[] points = new float[4];
        FloatBuffer curve = Buffers.newDirectFloatBuffer(4 * num_vertices);
        MyMatrix rotations = new MyMatrix();
        MyMatrix trans = new MyMatrix(new float[] { 1.0f, 0.0f, 0.0f, center[0],
                0.0f, 1.0f, 0.0f, center[1],
                0.0f, 0.0f, 1.0f, center[2],
                0.0f, 0.0f, 0.0f, 1.0f });
        trans.transposeMatrix();

        norm = (float) Math.sqrt(Math.pow(up[0], 2) + Math.pow(up[1], 2) + Math.pow(up[2], 2));
        vec[0] = up[0] / norm;
        vec[1] = up[1] / norm;
        vec[2] = up[2] / norm;
        vec[3] = 1.0f;

        float cos;
        float sin;

        if (vec[2] == 0.0f) {
            cos = 0.0f;
            sin = 1.0f;

            rotations.setMatrix16fv(new float[] { cos, 0.0f, sin, 0.0f,
                    0.0f, 1.0f, 0.0f, 0.0f,
                    -sin, 0.0f, cos, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f });

            rotations.transposeMatrix();

            cos = vec[0] / ((float) Math.sqrt(Math.pow(vec[0], 2) + Math.pow(vec[1], 2)));
            sin = vec[1] / ((float) Math.sqrt(Math.pow(vec[0], 2) + Math.pow(vec[1], 2)));

            rotations.multiMatrixf(new float[] { cos, sin, 0.0f, 0.0f,
                    -sin, cos, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f });
        } else {
            if (vec[0] == 0.0f && vec[2] == 0.0f) {
                cos = 1.0f;
                sin = 0.0f;
            } else {
                cos = vec[0] / ((float) Math.sqrt(Math.pow(vec[0], 2) + Math.pow(vec[2], 2)));
                sin = vec[2] / ((float) Math.sqrt(Math.pow(vec[0], 2) + Math.pow(vec[2], 2)));
            }

            rotations.setMatrix16fv(new float[] { cos, 0.0f, sin, 0.0f,
                    0.0f, 1.0f, 0.0f, 0.0f,
                    -sin, 0.0f, cos, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f });

            rotations.transposeMatrix();

            vec = vec_matrix_product(vec, rotations.getMatrix());

            cos = vec[1] / ((float) Math.sqrt(Math.pow(vec[0], 2) + Math.pow(vec[1], 2)));
            sin = vec[0] / ((float) Math.sqrt(Math.pow(vec[0], 2) + Math.pow(vec[1], 2)));

            rotations.multiMatrixf(new float[] { cos, sin, 0.0f, 0.0f,
                    -sin, cos, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f });

            rotations.multiMatrixf(new float[] { 1.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f });
        }

        rotations.printMatrix();

        for (t = 0.0f; Math.abs(t - 2.0f * Math.PI) > 0.0001; t += t_step) {
            float x = radius * ((float) Math.cos(t));
            float y = radius * ((float) Math.sin(t));

            points = vec_matrix_product(new float[] { x, y, 0, 1 }, rotations.getMatrix());
            points = vec_matrix_product(points, trans.getMatrix());
            curve.put(points);
        }

        curve.rewind();
        return curve;
    }

    private static float[] vec_matrix_product(float[] t, float[] matrix) {
        float[] aux = new float[4];

        for (int i = 0; i < 1; ++i) {
            for (int j = 0; j < 4; ++j) {
                float k = 0.0f;
                for (int n = 0; n < 4; ++n) {
                    k += t[4 * i + n] * matrix[4 * n + j];
                }
                aux[4 * i + j] = k;
            }
        }

        aux[3] = 1.0f;

        return aux;
    }
}
