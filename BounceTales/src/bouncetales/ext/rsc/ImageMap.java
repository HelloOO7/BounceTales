package bouncetales.ext.rsc;

import java.io.DataInput;
import java.io.IOException;

public class ImageMap {

	public static final int PARAM_WIDTH = 0;
	public static final int PARAM_HEIGHT = 1;
	public static final int PARAM_ORIGIN_X = 2;
	public static final int PARAM_ORIGIN_Y = 3;
	public static final int PARAM_ATLAS_X = 4;
	public static final int PARAM_ATLAS_Y = 5;
	public static final int PARAM_IMAGE_ID = 6;

	public byte width;
	public byte height;
	public byte originX;
	public byte originY;

	public byte atlasX;
	public byte atlasY;
	public byte imageId;

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

	public void read(DataInput dis) throws IOException {
		width = dis.readByte();
		height = dis.readByte();
		originX = dis.readByte();
		originY = dis.readByte();
		atlasX = dis.readByte();
		atlasY = dis.readByte();
		imageId = dis.readByte();
	}

	public int getParam(int paramId) {
		switch (paramId) {
			case PARAM_ATLAS_X:
				return atlasX & 0xFF;
			case PARAM_ATLAS_Y:
				return atlasY & 0xFF;
			case PARAM_WIDTH:
				return width & 0xFF;
			case PARAM_HEIGHT:
				return height & 0xFF;
			case PARAM_ORIGIN_X:
				return originX;
			case PARAM_ORIGIN_Y:
				return originY;
			case PARAM_IMAGE_ID:
				return imageId & 0xFF;
		}
		throw new IllegalArgumentException("Param wrong " + paramId);
	}
}
