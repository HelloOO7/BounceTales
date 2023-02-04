package bouncetools.rlef.data;

import java.io.DataInput;
import java.io.IOException;
import java.io.PrintStream;
import xstandard.io.util.IndentedPrintStream;

public class EnemyData extends ObjectData {
	
	public byte enemyType; //renamed from: a

	public int movePoint1X; //renamed from: c
	public int movePoint1Y; //renamed from: d
	public int movePoint2X; //renamed from: r
	public int movePoint2Y; //renamed from: s
	
	public EnemyData(DataInput in, int objId) throws IOException {
		super(in, objId);
		movePoint1X = in.readShort();
		movePoint1Y = in.readShort();
		movePoint2X = in.readShort();
		movePoint2Y = in.readShort();
		enemyType = in.readByte();
	}

	@Override
	public void dump(IndentedPrintStream out) {
		out.println("| ENEMY |");
		out.println("  Type: " + enemyType);
		out.println("  Move point 1: (" + movePoint1X + ", " + movePoint1Y + ")");
		out.println("  Move point 2: (" + movePoint2X + ", " + movePoint2Y + ")");
		super.dump(out);
	}
}
