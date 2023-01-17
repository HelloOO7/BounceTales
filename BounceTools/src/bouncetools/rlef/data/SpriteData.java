package bouncetools.rlef.data;

import java.io.DataInput;
import java.io.IOException;
import java.io.PrintStream;

public class SpriteData extends ObjectData {

	public short[] imageIDs;

	public short[] xCoords;
	public short[] yCoords;
	
	public SpriteData(DataInput in, int objId) throws IOException {
		super(in, objId);
		int count = in.readUnsignedByte();
		this.xCoords = new short[count];
		this.yCoords = new short[count];
		this.imageIDs = new short[count];
		if (count > 0) {
			int bitSize = in.readUnsignedByte();
			short baseX = in.readShort();
			short baseY = in.readShort();
			if (bitSize > 0) {
				decomposeBytesToShorts(this.xCoords, count, baseX, in, bitSize);
				decomposeBytesToShorts(this.yCoords, count, baseY, in, bitSize);
			} else {
				for (int i = 0; i < count; i++) {
					this.xCoords[i] = (short) baseX;
					this.yCoords[i] = (short) baseY;
				}
			}
			decomposeBytesToShorts(this.imageIDs, count, 0, in, 16);
		}
	}

	@Override
	public void dump(PrintStream out) {
		out.println("| SPRITE |");
		for (int i = 0; i < xCoords.length; i++) {
			out.println("  Image " + imageIDs[i] + " @ (" + xCoords[i] + ", " + yCoords[i] + ")");
		}
		super.dump(out);
	}
}
