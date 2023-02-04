package bouncetools.rlef.data;

import java.awt.Color;
import java.io.DataInput;
import java.io.IOException;
import java.io.PrintStream;
import xstandard.io.util.IndentedPrintStream;

public class WaterData extends ObjectData {
	
	public int color;

	public short areaMinX;
	public short areaMaxX;
	public short areaMinY;
	public short areaMaxY;

	public byte gravityXLeft;
	public byte gravityXRight;
	public byte gravityYTop;
	public byte gravityYBottom;
	
	public WaterData(DataInput in, int objId) throws IOException {
		super(in, objId);
		this.areaMinX = in.readShort();
		this.areaMaxY = in.readShort();
		this.areaMaxX = in.readShort();
		this.areaMinY = in.readShort();
		this.gravityYTop = in.readByte();
		this.gravityXRight = in.readByte();
		this.gravityYBottom = in.readByte();
		this.gravityXLeft = in.readByte();
		this.color = in.readInt();
	}

	@Override
	public void dump(IndentedPrintStream out) {
		out.println("| WATER |");
		out.println("  Color: " + new Color(color));
		out.println("  Area:");
		out.println("    Left: " + areaMinX);
		out.println("    Right: " + areaMaxX);
		out.println("    Top: " + areaMinY);
		out.println("    Bottom: " + areaMaxY);
		out.println("  Gravity:");
		out.println("    Left: " + gravityXLeft);
		out.println("    Right: " + gravityXRight);
		out.println("    Top: " + gravityYTop);
		out.println("    Bottom: " + gravityYBottom);
		super.dump(out);
	}
}
