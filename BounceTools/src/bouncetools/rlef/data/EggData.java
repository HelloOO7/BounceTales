package bouncetools.rlef.data;

import java.io.DataInput;
import java.io.IOException;
import java.io.PrintStream;
import xstandard.io.util.IndentedPrintStream;

public class EggData extends ObjectData {
	
	public EggData(DataInput in, int objId) throws IOException {
		super(in, objId);
	}

	@Override
	public void dump(IndentedPrintStream out) {
		out.println("| EGG |");
		super.dump(out);
	}
}
