package bouncetools.rlef;

import bouncetales.LevelKey;
import bouncetales.Matrix;
import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import bouncetools.rlef.data.BounceObjectData;
import bouncetools.rlef.data.EventData;
import bouncetools.rlef.data.ObjectData;
import bouncetools.rlef.data.SpriteData;
import bouncetools.rlef.data.GeometryData;

public class RLEF {

    public static final int RLEF_SIGNATURE = 0x524C4546;
    public static final int RLEF_VERSION = 0x10000;

    private List<ObjectData> objects = new ArrayList<ObjectData>();
    private int objectCount;
    private int eventCount;

    public RLEF(InputStream in) {
        try {
            DataInputStream dis = new DataInputStream(in);
            int disLen = dis.available();
            if (dis.readInt() != RLEF_SIGNATURE) {
                throw new UnsupportedOperationException("This is not a RovioLevelFile!");
            }
            if (dis.readInt() != RLEF_VERSION) {
                throw new UnsupportedOperationException("Only v 1.0.0 levels are supported.");
            }

            objectCount = dis.readShort();
            dis.skip(2);
            eventCount = dis.readShort(); // ?? maybe ??

            byte command;
            int objId = 0;
            while ((command = dis.readByte()) != LevelKey.END) {
                int dataSize = dis.readShort();
                switch (command) {
                    case LevelKey.GEOMETRY:
                        System.out.println("Reading TriMdl");
                        objects.add(new GeometryData(dis, objId++));
                        break;
                    case 5:
                    case 7:
                    default:
                        System.out.println("Warning: Unknown command: " + command);
                        int readLen = dis.available();
                        objects.add(new ObjectData(dis, readLen));
                        readLen = readLen - dis.available();
                        objId++;
                        System.out.println("skipping " + (dataSize - readLen) + " bytes");
						int remain = dataSize - readLen;
						while (remain > 0) {
							remain -= dis.skipBytes(remain);
						}
                        break;
                    case LevelKey.EVENT:
                        System.out.println("Reading Event");
                        objects.add(new EventData(dis, objId));
                        objId++;
                        break;
                    case LevelKey.PLAYER:
                        System.out.println("Reading Bounce");
                        objects.add(new BounceObjectData(dis, objId));
                        break;
					case LevelKey.SPRITE:
						objects.add(new SpriteData(dis, objId));
						break;
                }
                System.out.println("Reading command at 0x" + Integer.toHexString((disLen - dis.available())));
            }

            for (ObjectData o : objects) {
                if (o != null) {
                    o.establishLinks(objects);
                }
            }

            dis.close();
        } catch (IOException ex) {
            Logger.getLogger(RLEF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void dump(PrintStream out) {
        out.println("---- ROVIO LEVEL FILE v" + Integer.toHexString(RLEF_VERSION) + " ----");
        out.println();
        out.println(" - Scene info: ");
        out.println("    - Object count: " + objectCount);
        out.println("    - Event count: " + eventCount);
        out.println();
        for (ObjectData obj : objects) {
            if (obj != null) {
                obj.dump(out);
            }
        }
    }

    public int[][] getBBox() {
        int[][] bbox = new int[][]{
            new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE},
            new int[]{-Integer.MAX_VALUE, -Integer.MAX_VALUE},};
        for (ObjectData o : objects) {
            if (o instanceof EventData || o instanceof GeometryData) {
                Matrix mtx = o.getTransformMatrix();
                mtx.mulVector(o.bboxMinX, o.bboxMinY);
                bbox[0][0] = Math.min(bbox[0][0], Matrix.vectorMulRslX);
                bbox[0][1] = Math.min(bbox[0][1], Matrix.vectorMulRslY);
                mtx.mulVector(o.bboxMaxX, o.bboxMaxY);
                bbox[1][0] = Math.max(bbox[1][0], Matrix.vectorMulRslX);
                bbox[1][1] = Math.max(bbox[1][1], Matrix.vectorMulRslY);

                /*bbox[0][0] = Math.min(bbox[0][0], tm.bboxMinX);
				bbox[0][1] = Math.min(bbox[0][1], tm.bboxMinY);
				bbox[1][0] = Math.max(bbox[1][0], tm.bboxMaxX);
				bbox[1][1] = Math.max(bbox[1][1], tm.bboxMaxY);*/
            }
        }
        return bbox;
    }

    public void dumpDraw(Graphics g) {
        for (ObjectData obj : objects) {
            if (obj != null) {
                obj.draw(g);
            }
        }
    }
}
