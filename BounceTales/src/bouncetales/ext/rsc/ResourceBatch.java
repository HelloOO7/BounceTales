package bouncetales.ext.rsc;

import java.io.DataInput;
import java.io.IOException;

public class ResourceBatch {

	public byte resType;
	public short mainResId;
	public short[] subResIds;

	public ResourceBatch(DataInput in) throws IOException {
		resType = in.readByte();
		int count = in.readByte();
		mainResId = in.readShort();
		subResIds = new short[count];
		for (int i = 0; i < count; i++) {
			subResIds[i] = in.readShort();
		}
	}
	
	public ResourceBatch() {
		
	}
}
