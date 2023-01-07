package bouncetales.ext.rsc;

import java.io.DataInputStream;
import java.io.IOException;

public class ImageMap {

	public static final int PARAM_WIDTH = 0;
	public static final int PARAM_HEIGHT = 1;
	public static final int PARAM_ORIGIN_X = 2;
	public static final int PARAM_ORIGIN_Y = 3;
	public static final int PARAM_ATLAS_X = 4;
	public static final int PARAM_ATLAS_Y = 5;
	public static final int PARAM_IMAGE_ID = 6;

	public short width;
	public short height;
	public short originX;
	public short originY;

	public short atlasX;
	public short atlasY;
	public short imageId;

	public ImageMap() {
		clear();
	}

	public final void clear() {
		width = -1;
		height = -1;
		originX = -1;
		originY = -1;
		atlasX = -1;
		atlasY = -1;
		imageId = -1;
	}

	public void read(DataInputStream dis) throws IOException {
		width = (short) dis.readUnsignedByte();
		height = (short) dis.readUnsignedByte();
		originX = dis.readByte();
		originY = dis.readByte();
		atlasX = (short) dis.readUnsignedByte();
		atlasY = (short) dis.readUnsignedByte();
		imageId = dis.readByte();
	}

	public void read16Bit(DataInputStream dis) throws IOException {
		width = dis.readShort();
		height = dis.readShort();
		originX = dis.readShort();
		originY = dis.readShort();
		atlasX = dis.readShort();
		atlasY = dis.readShort();
		imageId = dis.readShort();
	}

	public int getParam(int paramId) {
		switch (paramId) {
			case PARAM_ATLAS_X:
				return atlasX;
			case PARAM_ATLAS_Y:
				return atlasY;
			case PARAM_WIDTH:
				return width;
			case PARAM_HEIGHT:
				return height;
			case PARAM_ORIGIN_X:
				return originX;
			case PARAM_ORIGIN_Y:
				return originY;
			case PARAM_IMAGE_ID:
				return imageId;
		}
		throw new IllegalArgumentException("Param wrong " + paramId);
	}
}
