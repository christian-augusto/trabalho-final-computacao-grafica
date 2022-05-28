package robson;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.common.nio.Buffers;

import utils.MyMatrix;

public class MyHermiteCurve {
    private int numVertices;
    private int numIndices;
    private FloatBuffer vertices;
    private IntBuffer indices;
    private float[] tvector;

    private MyMatrix HermiteMatrix = new MyMatrix();

    public MyHermiteCurve() {
        this.HermiteMatrix.setMatrix16fv(new float[] { 2.0f, -2.0f, 1.0f, 1.0f,
                -3.0f, 3.0f, -2.0f, -1.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 0.0f });
    }

    public void setNumVertices(int numvertices) {
        this.numVertices = numvertices;
    }

    public int getNumVertices() {
        return this.numVertices;
    }

    public void setNumIndices(int numindices) {
        this.numIndices = numindices;
    }

    public int getNumIndices() {
        return this.numIndices;
    }

    public FloatBuffer getVertices() {
        return this.vertices;
    }

    public IntBuffer getIndices() {
        return this.indices;
    }

    public void createHermiteCurve(float[] controlpoints, int num_vertices) {
        if (num_vertices <= 1) {
            System.out.println("createBezierSurface: Erro - Invalid parameters");
            System.exit(1);
        }

        float t = 0.0f;
        float t_step = 1.0f / ((float) num_vertices - 1);

        MyMatrix geo_vector = new MyMatrix();
        geo_vector.setMatrix16fv(new float[] { controlpoints[0], controlpoints[1], controlpoints[2], controlpoints[3],
                controlpoints[4], controlpoints[5], controlpoints[6], controlpoints[7],
                controlpoints[8] - controlpoints[0], controlpoints[9] - controlpoints[1],
                controlpoints[10] - controlpoints[2], controlpoints[11] - controlpoints[3],
                controlpoints[12] - controlpoints[4], controlpoints[13] - controlpoints[5],
                controlpoints[14] - controlpoints[6], controlpoints[15] - controlpoints[7] });

        setNumVertices(num_vertices);
        setNumIndices(2 * (num_vertices - 1));

        vertices = Buffers.newDirectFloatBuffer(4 * getNumVertices());
        indices = Buffers.newDirectIntBuffer(getNumIndices());

        for (t = 0; t <= 1.0f; t += t_step) {
            tvector = new float[] { (float) Math.pow(t, 3.0f), (float) Math.pow(t, 2.0f), (float) Math.pow(t, 1.0f),
                    1.0f };

            MyMatrix aux = new MyMatrix();
            aux.setMatrix16fv(geo_vector.getMatrix());
            aux.transposeMatrix();
            MyMatrix HermiteTranspose = new MyMatrix(HermiteMatrix.getMatrix());
            HermiteTranspose.transposeMatrix();
            aux.multiMatrixf(HermiteTranspose.getMatrix());
            aux.transposeMatrix();

            vertices.put(vec_matrix_product(tvector, aux.getMatrix()));
        }

        for (int i = 0; i < num_vertices - 1; ++i) {
            indices.put(i);
            indices.put(i + 1);
        }

        vertices.flip();
        indices.flip();

    }

    private float[] vec_matrix_product(float[] t, float[] matrix) {
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
