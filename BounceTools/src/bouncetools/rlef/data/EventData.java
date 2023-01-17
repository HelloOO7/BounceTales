package bouncetools.rlef.data;

import bouncetales.LP32;
import bouncetales.Matrix;
import java.awt.Color;
import java.awt.Graphics;
import java.io.DataInput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

public class EventData extends ObjectData {

    public int eventType;
    public int f114d;
    public int f656t;
    public int f108a;

    public byte[][] events;

    public EventData(DataInput in, int objId) throws IOException {
        super(in, objId);

        bboxMinX = in.readShort() << 16;
        bboxMaxY = in.readShort() << 16;
        bboxMaxX = in.readShort() << 16;
        bboxMinY = in.readShort() << 16;

        eventType = in.readByte();
        f114d = in.readByte();
        f656t = in.readByte();
        f108a = in.readShort();

        int eventCount = in.readByte();
        events = new byte[eventCount][];
        for (int eventIdx = 0; eventIdx < eventCount; eventIdx++) {
            byte evCmdSize = in.readByte();
            events[eventIdx] = new byte[evCmdSize];
            in.readFully(events[eventIdx]);
        }
    }

    @Override
    public void draw(Graphics g) {
        Matrix mtx = getTransformMatrix();
        mtx.mulVector(bboxMinX, bboxMinY);
        int x = getLP32CastIntVtx(Matrix.vectorMulRslX);
        int y = getLP32CastIntVtx(Matrix.vectorMulRslY);
        mtx.mulVector(bboxMaxX, bboxMaxY);
        int mx = getLP32CastIntVtx(Matrix.vectorMulRslX);
        int my = getLP32CastIntVtx(Matrix.vectorMulRslY);
        g.setColor(Color.GREEN);
        g.drawRect(x, -my, mx - x, (my - y));
        g.setColor(Color.RED);
        g.fillArc(x - 5, -my - 5, 10, 10, 0, 360);
        g.setColor(Color.CYAN);
        g.drawString("EVENT " + idx, x, -my);
    }

    private int getLP32CastIntVtx(int lp32) {
        return (int) LP32.LP32ToFP32(lp32);
    }

    @Override
    public void dump(PrintStream out) {
        super.dump(out);
        out.println("| EVENT OBJECT |");
        dumpBBox(out);
        out.println(" - f114d: " + f114d);
        out.println(" - f656t: " + f656t);
        out.println(" - f108a: " + f108a);
        out.println();
        out.println(" - | EVENT BINARY DATA |");
        int idx = 0;
        for (byte[] bin : events) {
            out.println("     - EVENT No. " + idx + ": ");
            out.println("         - EvCmd: " + bin[0]);
            out.println("         - EvData: " + Arrays.toString(Arrays.copyOfRange(bin, 1, bin.length)));
            idx++;
        }
        out.println();
    }
}
