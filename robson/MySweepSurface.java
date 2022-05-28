package robson;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.common.nio.Buffers;

import utils.MyMatrix;

public class MySweepSurface {
    private FloatBuffer vertices;
    private IntBuffer indices;
    private int numVertices;
    private int numIndices;

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

    public void createSweepSurface(FloatBuffer path, FloatBuffer curve, int num_vertices_curve, int num_vertices_path) {
        float[] curve_point = new float[4];

        this.setNumVertices(num_vertices_curve * num_vertices_path);
        this.setNumIndices((num_vertices_path - 1) * num_vertices_curve * 6);

        this.vertices = Buffers.newDirectFloatBuffer(4 * getNumVertices());
        this.indices = Buffers.newDirectIntBuffer(getNumIndices());

        for (int t = 0; t < num_vertices_path; ++t) {
            MyMatrix trans = new MyMatrix();
            trans.setMatrix16fv(new float[] { 1.0f, 0.0f, 0.0f, path.get(),
                    0.0f, 1.0f, 0.0f, path.get(),
                    0.0f, 0.0f, 1.0f, path.get(),
                    0.0f, 0.0f, 0.0f, path.get() });

            trans.transposeMatrix();
            curve.rewind();

            for (int j = 0; j < num_vertices_curve; ++j) {

                float x = curve.get();
                float y = curve.get();
                float z = curve.get();
                float w = curve.get();

                curve_point = vec_matrix_product(new float[] { x, y, z, w }, trans.getMatrix());

                this.vertices.put(curve_point);
            }
        }

        for (int i = 0; i < num_vertices_path - 1; ++i) {
            for (int j = 0; j < num_vertices_curve; ++j) {

                if (j == num_vertices_curve - 1) {
                    indices.put(i * num_vertices_curve + j);
                    indices.put((i + 1) * num_vertices_curve + j);
                    indices.put((i + 1) * num_vertices_curve + j + 1 - num_vertices_curve);

                    indices.put(i * num_vertices_curve + j);
                    indices.put(i * num_vertices_curve + j + 1 - num_vertices_curve);
                    indices.put((i + 1) * num_vertices_curve + j + 1 - num_vertices_curve);
                } else {
                    indices.put(i * num_vertices_curve + j);
                    indices.put((i + 1) * num_vertices_curve + j);
                    indices.put((i + 1) * num_vertices_curve + j + 1);

                    indices.put(i * num_vertices_curve + j);
                    indices.put(i * num_vertices_curve + j + 1);
                    indices.put((i + 1) * num_vertices_curve + j + 1);
                }

            }
        }

        this.vertices.rewind();
        this.indices.rewind();

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
