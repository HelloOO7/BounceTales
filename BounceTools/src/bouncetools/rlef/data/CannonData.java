package bouncetools.rlef.data;

import java.io.DataInput;
import java.io.IOException;
import java.io.PrintStream;
import xstandard.io.util.IndentedPrintStream;

public class CannonData extends ObjectData {

	public int bounceObjId;
	public byte power;
	
	public CannonData(DataInput in, int objId) throws IOException {
		super(in, objId);
		bounceObjId = in.readShort();
		power = in.readByte();
	}

	@Override
	public void dump(IndentedPrintStream out) {
		out.println("| CANNON |");
		out.println("  Power: " + power);
		out.println("  Target object ID: " + bounceObjId);
		super.dump(out);
	}
}
