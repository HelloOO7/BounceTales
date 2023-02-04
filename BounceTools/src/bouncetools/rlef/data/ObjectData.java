package bouncetools.rlef.data;

import bouncetales.LP32;
import bouncetales.Matrix;
import java.awt.Color;
import java.awt.Graphics;
import java.io.DataInput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import xstandard.io.util.IndentedPrintStream;

public class ObjectData {

    public int idx;
    public int parentIdx;
    public int prevIdx;

    public ObjectData parent;

    public Matrix objectMatrix = new Matrix();

    public int bboxMinX = Integer.MAX_VALUE;
    public int bboxMaxX = -Integer.MAX_VALUE;
    public int bboxMinY = Integer.MAX_VALUE;
    public int bboxMaxY = -Integer.MAX_VALUE;

    public int flags;

    public ObjectData() {

    }

    public ObjectData(DataInput in, int objId) throws IOException {
        idx = objId;
        parentIdx = in.readShort();
        prevIdx = in.readShort();
        byte transformFlags = in.readByte();
        if ((transformFlags & 7) == 7) {
            objectMatrix.m00 = in.readInt();
            objectMatrix.m01 = in.readInt();
            objectMatrix.translationX = in.readInt();
            objectMatrix.m10 = in.readInt();
            objectMatrix.m11 = in.readInt();
            objectMatrix.translationY = in.readInt();
        } else {
            if ((transformFlags & 1) > 0) {
                objectMatrix.translationX = in.readShort() << 16;
                objectMatrix.translationY = in.readShort() << 16;
            }
            if ((transformFlags & 2) > 0) {
                objectMatrix.setRotation((float) in.readInt() / 65536.0f);
            }
            if ((transformFlags & 4) > 0) {
                objectMatrix.setScale(in.readInt(), in.readInt());
            }
        }
        this.flags = in.readInt();
    }

    protected void resetBBox() {
        bboxMinX = Integer.MAX_VALUE;
        bboxMaxX = -Integer.MAX_VALUE;
        bboxMinY = Integer.MAX_VALUE;
        bboxMaxY = -Integer.MAX_VALUE;
    }

    public void establishLinks(List<ObjectData> objects) {
        if (parentIdx != -1) {
            parent = objects.get(parentIdx);
        }
    }

    public Matrix getTransformMatrix() {
        Matrix mtx;
        if (parent != null) {
            mtx = parent.getTransformMatrix();
        } else {
            mtx = new Matrix();
        }
        mtx.mul(objectMatrix);
        mtx.invert(new Matrix());
        return mtx;
    }

    public void dump(IndentedPrintStream out) {
        out.println("-- OBJECT " + idx + " --");
        out.println(" - Relations:");
        out.println("     - Previous: " + prevIdx);
        out.println("     - Parent: " + parentIdx);
        out.println(" - Transform Matrix: ");
        out.println("    " + getLP32Str(objectMatrix.m00) + "  " + getLP32Str(objectMatrix.m10));
        out.println("    " + getLP32Str(objectMatrix.m01) + "  " + getLP32Str(objectMatrix.m11));
        out.println("    " + getLP32Str(objectMatrix.translationX) + "  " + getLP32Str(objectMatrix.translationY));
        out.println(" - Flags: " + flags);
        out.println();
    }

    protected void dumpBBox(PrintStream out) {
        out.println(" - Bounding Box:");
        out.println("     - MinX: " + getLP32Str(bboxMinX) + " / MaxX: " + getLP32Str(bboxMaxX));
        out.println("     - MinY: " + getLP32Str(bboxMinY) + " / MaxY: " + getLP32Str(bboxMaxY));
        out.println();
    }

    public void draw(Graphics g) {
        Matrix mtx = getTransformMatrix();
        mtx.mulVector(0, 0);
        int x = Matrix.vectorMulRslX;
        int y = Matrix.vectorMulRslY;
        g.setColor(Color.RED);
        g.fillArc(x, y, (int) (mtx.getScaleX() * 20), (int) (mtx.getScaleY() * 20), 0, 360);
        g.setColor(Color.WHITE);
        g.drawString("Object " + idx + " (" + getClass().getSimpleName() + ")", x, y);
    }

    protected static String getLP32Str(int lp32) {
        return get8CharFloatStr(LP32.LP32ToFP32(lp32));
    }

    protected static String get8CharFloatStr(float f) {
        String str = String.valueOf(f);
        if (str.length() > 8) {
            str = str.substring(0, 8);
        } else if (str.length() < 8) {
            str = str + "00000000".substring(str.length());
        }
        return str;
    }

    public static void decomposeBytesToLP32s(final int[] target, final int count, final int base, DataInput src, int bitsPerInt) throws IOException {
        int bitBuffer = 0;
        int bufIdx = 0;
        int index = 0;
        int bit = 1 << (bitsPerInt - 1);
        int mask = (1 << bitsPerInt) - 1;
        while (index < count) {
            final int oldBitsWithNewByte = bitBuffer | (src.readByte() << bufIdx);
            bufIdx += 8;
            bitBuffer = (oldBitsWithNewByte & ((1 << bufIdx) - 1));
            while (bitsPerInt <= bufIdx) {
                int maskedBits = (bitBuffer & mask);
                if ((maskedBits & bit) > 0) {
                    maskedBits |= ~mask; //sign extend
                }
                if (index < count) {
                    target[index++] = maskedBits + base << 16;
                }
                bufIdx -= bitsPerInt;
                bitBuffer >>>= bitsPerInt;
            }
        }
    }

    public static void decomposeBytesToShorts(final short[] target, final int count, final int base, DataInput src, int bitsPerShort) throws IOException {
        int bitBuffer = 0;
        int bufIdx = 0;
        int index = 0;
        final int bit = 1 << bitsPerShort - 1;
        final int mask = (1 << bitsPerShort) - 1;
        while (index < count) {
            bitBuffer |= src.readByte() << bufIdx;
            bufIdx += 8;
            bitBuffer &= (1 << bufIdx) - 1;
            while (bitsPerShort <= bufIdx) {
                int result = (bitBuffer & mask);
                if ((result & bit) > 0) {
                    result |= ~mask; //sign extend
                }
                if (index < count) {
                    target[index++] = (short) (result + base);
                }
                bufIdx -= bitsPerShort;
                bitBuffer >>>= bitsPerShort;
            }
        }
    }
}
