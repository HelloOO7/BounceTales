package bouncetales;

import java.io.DataInputStream;
import java.io.IOException;


/* renamed from: p */
 /* loaded from: bounce2_ALL.jar:p.class */
public interface IResourceHandler {

	/* renamed from: a */
	boolean loadResidentData(DataInputStream dis, int type) throws IOException;

	/* renamed from: a */
	Object readResource(DataInputStream dis, int readLength, int type, int resBatchId) throws IOException;

	/* renamed from: a */
	boolean loadResource(DataInputStream dis, String finalRscPath, int readLength, int resType, int batchId, int subResIdx) throws IOException;

	/* renamed from: a */
	boolean unloadResource(int resType, int unloadResId);
}
