package bouncetales.ext.rsc;

import java.io.DataInput;
import java.io.IOException;

public class ResidentResHeader {

	public short type;
	public short resId;

	public ResidentResHeader(DataInput in) throws IOException {
		type = in.readShort();
		resId = in.readShort();
	}
	
	public ResidentResHeader() {
		
	}
}
