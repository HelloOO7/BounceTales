package bouncetools.rlef.data;

import java.io.DataInput;
import java.io.IOException;
import java.io.PrintStream;
import xstandard.io.util.IndentedPrintStream;

public class TrampolineData extends ObjectData {
	
	public short imageId;
	public byte basePush;
	
	public TrampolineData(DataInput in, int objId) throws IOException {
		super(in, objId);
		imageId = in.readShort();
		basePush = in.readByte();
	}

	@Override
	public void dump(IndentedPrintStream out) {
		out.println("| TRAMPOLINE |");
		out.println("  Power: " + basePush);
		out.println("  Image ID: " + imageId);
		super.dump(out);
	}
}
