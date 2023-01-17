package bouncetools.rlef.data;

import java.io.DataInput;
import java.io.IOException;
import java.io.PrintStream;

public class BounceObjectData extends ObjectData {

	public BounceObjectData(DataInput in, int objId) throws IOException {
		super(in, objId);
	}

	@Override
	public void dump(PrintStream out) {
		out.println("BOUNCE OBJECT:");
		super.dump(out);
	}
}
