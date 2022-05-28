package robson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.FloatBuffer;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;

import utils.JGLU;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;

public class Sweep implements GLEventListener {
    private GL4 gl;
    private JGLU glu;

    private MySweepSurface sweepsurf;

    private static int g_width = 600;
    private static int g_height = 600;

    private int g_shaderProgram;
    private int g_vertShader;
    private int g_fragShader;

    private int g_projectionMatrixLocation;
    private int g_modelViewMatrixLocation;
    private int g_colorLocation;

    private int[] vao1 = new int[1];
    private int[] vbo1 = new int[2];

    private double aspect = ((double) g_height) / ((double) g_width);

    private int num_vertices_path = 20;
    private int num_vertices_curve = 20;
    private float radius = 5.0f;

    private float step = 0.0f;

    public static void main(String[] args) {

        GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL4));
        GLWindow glWindow = GLWindow.create(caps);

        glWindow.setTitle("Hermite Sweep Surface");
        glWindow.setSize(g_width, g_height);
        glWindow.setUndecorated(false);
        glWindow.setPointerVisible(true);
        glWindow.setVisible(true);

        glWindow.addGLEventListener(new Sweep());
        Animator animator = new Animator();
        animator.add(glWindow);
        animator.start();
    }

    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL4();
        glu = new JGLU();

        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        sweepsurf = new MySweepSurface();
        MyHermiteCurve hermite_path = new MyHermiteCurve();

        float[] control_points = new float[] { -5f, -15f, 0f, 1.0f,
                18f, 10.0f, 0.0f, 1.0f,
                10f, 50f, 0f, 1.0f,
                20f, 80.0f, 0.0f, 1.0f };

        hermite_path.createHermiteCurve(control_points, num_vertices_path);

        FloatBuffer profile_curve = Curves.generateCircumference(new float[] { 0, 0, 0, 1.0f }, radius,
                num_vertices_curve, new float[] { 0.0f, 1.0f, 0.0f });

        sweepsurf.createSweepSurface(hermite_path.getVertices(),
                profile_curve,
                num_vertices_curve,
                num_vertices_path);

        g_vertShader = loadShader(GL4.GL_VERTEX_SHADER, "./res/shader.vert");
        g_fragShader = loadShader(GL4.GL_FRAGMENT_SHADER, "./res/shader.frag");

        g_shaderProgram = gl.glCreateProgram();
        gl.glAttachShader(g_shaderProgram, g_vertShader);
        gl.glAttachShader(g_shaderProgram, g_fragShader);

        gl.glLinkProgram(g_shaderProgram);
        // Check link status.
        int[] linked = new int[1];
        gl.glGetProgramiv(g_shaderProgram, GL4.GL_LINK_STATUS, linked, 0);
        if (linked[0] != 0) {
            System.out.println("Shaders succesfully linked");
        } else {
            int[] logLength = new int[1];
            gl.glGetProgramiv(g_shaderProgram, GL4.GL_INFO_LOG_LENGTH,
                    logLength, 0);

            byte[] log = new byte[logLength[0]];
            gl.glGetProgramInfoLog(g_shaderProgram, logLength[0], (int[]) null,
                    0, log, 0);

            System.err.println("Error linking shaders: " + new String(log));
            System.exit(1);
        }

        gl.glUseProgram(g_shaderProgram);

        g_projectionMatrixLocation = gl.glGetUniformLocation(g_shaderProgram,
                "u_projectionMatrix");
        g_modelViewMatrixLocation = gl.glGetUniformLocation(g_shaderProgram,
                "u_modelViewMatrix");
        g_colorLocation = gl.glGetUniformLocation(g_shaderProgram,
                "u_color");

        int vertexLocation = gl.glGetAttribLocation(g_shaderProgram, "a_vertex");

        gl.glGenVertexArrays(1, vao1, 0);
        gl.glBindVertexArray(vao1[0]);

        gl.glGenBuffers(2, vbo1, 0);

        int verticesVBO1 = vbo1[0];
        int indicesVBO1 = vbo1[1];

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, verticesVBO1);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, 4 * sweepsurf.getNumVertices() * Float.BYTES, sweepsurf.getVertices(),
                GL4.GL_STATIC_DRAW);

        gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, indicesVBO1);
        gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, sweepsurf.getNumIndices() * Integer.BYTES, sweepsurf.getIndices(),
                GL4.GL_STATIC_DRAW);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, verticesVBO1);
        gl.glVertexAttribPointer(vertexLocation, 4, GL4.GL_FLOAT, false, 4 * Float.BYTES, 0);
        gl.glEnableVertexAttribArray(vertexLocation);

        gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, indicesVBO1);

        gl.glBindVertexArray(vao1[0]);

        glu.gluMatrixMode(JGLU.GL_PROJECTION);
        glu.gluLoadIdentity();
        glu.gluFrustum(-1, 1, -aspect, aspect, 1, 100);

        float[] projection_matrix = glu.gluGetMatrix();

        gl.glUniformMatrix4fv(g_projectionMatrixLocation, 1, false, projection_matrix, 0);

        glu.gluMatrixMode(JGLU.GL_MODELVIEW);
        glu.gluLoadIdentity();

        gl.glEnable(GL4.GL_DEPTH_TEST);
        gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_LINE);

    }

    public void display(GLAutoDrawable arg0) {

        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);

        glu.gluLoadIdentity();
        glu.gluLookAt(0, 0, 26, 0, -1, 0, 0, 1, 0);

        glu.gluPushMatrix();
        glu.gluRotated(step, 1, 0, 0);
        glu.gluTranslated(-5, 0, 0);
        float[] model_matrix = glu.gluGetMatrix();
        glu.gluPopMatrix();

        gl.glBindVertexArray(vao1[0]);

        gl.glUniform4f(g_colorLocation, 0.0f, 0.0f, 0.0f, 1.0f);
        gl.glUniformMatrix4fv(g_modelViewMatrixLocation, 1, false, model_matrix, 0);
        gl.glDrawElements(GL4.GL_TRIANGLES, sweepsurf.getNumIndices(), GL4.GL_UNSIGNED_INT, 0);

        gl.glFlush();

        step += 1.0f;

    }

    public void dispose(GLAutoDrawable arg0) {
        System.out.println("Dispose");
        System.out.println("cleanup, remember to release shaders");
        gl.glUseProgram(0);
        gl.glDetachShader(g_shaderProgram, g_vertShader);
        gl.glDeleteShader(g_vertShader);
        gl.glDetachShader(g_shaderProgram, g_fragShader);
        gl.glDeleteShader(g_fragShader);
        gl.glDeleteProgram(g_shaderProgram);
        System.exit(0);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        g_width = w;
        g_height = h;

        aspect = ((double) h) / ((double) w);
        gl.glViewport(0, 0, g_width, g_height);
    }

    public int loadShader(int type, String filename) {
        int shader;

        // Create GPU shader handle
        shader = gl.glCreateShader(type);

        // Read shader file
        String[] vlines = new String[1];
        vlines[0] = "";
        String line;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                vlines[0] += line + "\n"; // insert a newline character after
                                          // each line
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Fail reading shader file");
        }

        gl.glShaderSource(shader, vlines.length, vlines, null);

        // Compile shader
        gl.glCompileShader(shader);

        // Check compile status.
        int[] compiled = new int[1];
        gl.glGetShaderiv(shader, GL4.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] != 0) {
            System.out.println("Shader succesfully compiled");
        } else {
            int[] logLength = new int[1];
            gl.glGetShaderiv(shader, GL4.GL_INFO_LOG_LENGTH, logLength, 0);

            byte[] log = new byte[logLength[0]];
            gl.glGetShaderInfoLog(shader, logLength[0], (int[]) null, 0, log, 0);

            System.err
                    .println("Error compiling the shader: " + new String(log));
            System.exit(1);
        }

        return shader;
    }

}
