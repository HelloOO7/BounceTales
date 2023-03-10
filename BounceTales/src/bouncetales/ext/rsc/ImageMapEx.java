package bouncetales.ext.rsc;

import java.io.DataInput;
import java.io.IOException;

public class ImageMapEx {

	public short resBatchId;
	public short offset;

	public ImageMapEx() {
		clear();
	}

	public final void clear() {
		resBatchId = -1;
		offset = -1;
	}

	public void read(int resBatchId, DataInput dis) throws IOException {
		this.resBatchId = (short) resBatchId;
		this.offset = (short) (dis.readShort() + 4);
	}
}
