package bouncetools.rlef.data;

import bouncetales.LP32;
import bouncetales.Matrix;
import java.awt.Color;
import java.awt.Graphics;
import java.io.DataInput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import xstandard.io.util.IndentedPrintStream;

public class GeometryData extends ObjectData {

    public int[][] vertices;
    public short[] facepoints;
    public Color color;
    public int eventId;

    public GeometryData(DataInput in, int objId) throws IOException {
        super(in, objId);

        int vcount = in.readShort();
        vertices = new int[vcount + 1][2];

        int fcount = in.readShort();
        facepoints = new short[fcount];
        color = new Color(in.readInt());

        int coordBitSize = in.readByte();
        int base = in.readShort();
        int[] vb = new int[vcount];
        decomposeBytesToLP32s(vb, vcount, base, in, coordBitSize);
        interlace(vertices, 0, vb);
        base = in.readShort();
        decomposeBytesToLP32s(vb, vcount, base, in, coordBitSize);
        interlace(vertices, 1, vb);
        vertices[vertices.length - 1] = Arrays.copyOf(vertices[0], 2);

        int fpBitSize = in.readByte();
        decomposeBytesToShorts(facepoints, fcount, 0, in, fpBitSize);

        eventId = in.readShort();

        makeBBox();
    }

    public final void makeBBox() {
        resetBBox();
        for (int[] v : vertices) {
            bboxMinX = Math.min(v[0], bboxMinX);
            bboxMaxX = Math.max(v[0], bboxMaxX);
            bboxMinY = Math.min(v[1], bboxMinY);
            bboxMaxY = Math.max(v[1], bboxMaxY);
        }
    }

    @Override
    public void dump(IndentedPrintStream out) {
        super.dump(out);

        out.println("| TRIANGLE MODEL DATA |");
        dumpBBox(out);
        out.println(" - Fill color: " + color.toString());
        out.println(" - Event: " + eventId);
        out.println();
        out.println(" - | DE-INDEXED FACES |");
        for (int i = 0; i < facepoints.length; i += 3) {
            out.print("        ");
            out.print(getVtxStr(vertices[facepoints[i + 0]]));
            out.print(getVtxStr(vertices[facepoints[i + 1]]));
            out.print(getVtxStr(vertices[facepoints[i + 2]]));
            out.println();
        }
        out.println();
    }

    @Override
    public void draw(Graphics g) {

        int[][] transformedVertices = getTransformedVertexArr();
        for (int i = 0; i < facepoints.length; i += 3) {
            g.setColor(color);
            int[] x = new int[3];
            int[] y = new int[3];
            for (int j = 0; j < 3; j++) {
                x[j] = getLP32CastIntVtx(transformedVertices[facepoints[i + j]][0]);
                y[j] = getLP32CastIntVtx(transformedVertices[facepoints[i + j]][1]);
            }
            g.fillPolygon(x, y, 3);
            g.setColor(Color.YELLOW);
            g.drawLine(x[0], y[0], x[1], y[1]);
            g.drawLine(x[1], y[1], x[2], y[2]);
            g.drawLine(x[0], y[0], x[2], y[2]);
        }
        super.draw(g);
    }

    private int getLP32CastIntVtx(int lp32) {
        return (int) LP32.LP32ToFP32(lp32);
    }

    public int[][] getTransformedVertexArr() {
        int[][] tv = new int[vertices.length][2];
        Matrix mtx = getTransformMatrix();
        for (int i = 0; i < tv.length; i++) {
            mtx.mulVector(vertices[i][0], vertices[i][1]);
            tv[i][0] = Matrix.vectorMulRslX;
            tv[i][1] = -Matrix.vectorMulRslY;
        }
        return tv;
    }

    private String getVtxStr(int[] vert) {
        return "(" + getLP32Str(vert[0]) + ", " + getLP32Str(vert[1]) + ")";
    }

    private static void interlace(int[][] tgt, int tgtPos, int[] src) {
        int len = Math.min(src.length, tgt.length);
        for (int i = 0; i < len; i++) {
            tgt[i][tgtPos] = src[i];
        }
    }
}
