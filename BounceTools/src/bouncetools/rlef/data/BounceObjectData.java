package bouncetools.rlef.data;

import java.io.DataInput;
import java.io.IOException;
import java.io.PrintStream;
import xstandard.io.util.IndentedPrintStream;

public class BounceObjectData extends ObjectData {

	public boolean isPlayer;
	
	public BounceObjectData(DataInput in, int objId, boolean isPlayer) throws IOException {
		super(in, objId);
		this.isPlayer = isPlayer;
	}

	@Override
	public void dump(IndentedPrintStream out) {
		out.println("BOUNCE OBJECT:");
		out.println("  Player: " + isPlayer);
		super.dump(out);
	}
}
