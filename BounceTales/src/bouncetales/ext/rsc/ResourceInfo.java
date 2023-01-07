package bouncetales.ext.rsc;

import java.io.DataInput;
import java.io.IOException;

public class ResourceInfo {

	public String resourcePath;
	public int skipOffset;
	public int readLength;

	public ResourceInfo(DataInput in) throws IOException {
		this(in, false);
	}

	public ResourceInfo(DataInput in, boolean readPath) throws IOException {
		if (readPath) {
			resourcePath = in.readUTF();
		}
		skipOffset = in.readInt();
		readLength = in.readInt();
	}

	public boolean exists() {
		return readLength != 0;
	}
}
