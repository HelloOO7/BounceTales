package bouncetools.resmap;

import bouncetales.ext.rsc.ResidentResHeader;
import bouncetales.ext.rsc.ResourceBatch;
import bouncetales.ext.rsc.ResourceInfo;
import bouncetools.ResourceComposer;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import xstandard.io.base.iface.DataOutputEx;


public class ResourceTable {

	public List<ResourceInfo> infos = new ArrayList<ResourceInfo>();
	public List<ResourceBatch> batches = new ArrayList<ResourceBatch>();
	public List<ResidentResHeader> resident = new ArrayList<ResidentResHeader>();

	public ResourceTable() {

	}

	public ResourceTable(String path) {
		this(new DataInputStream(ResourceTable.class.getResourceAsStream(path)));
	}

	public ResourceTable(byte[] data) {
		this(new DataInputStream(new ByteArrayInputStream(data)));
	}

	public ResourceTable(DataInputStream in) {
		try {
			int resourceCount = in.readShort();
			for (int i = 0; i < resourceCount; i++) {
				infos.add(new ResourceInfo(in, true));
			}
			int batchCount = in.readShort();
			for (int i = 0; i < batchCount; i++) {
				batches.add(new ResourceBatch(in));
			}
			int residentCount = in.readShort();
			for (int i = 0; i < residentCount; i++) {
				resident.add(new ResidentResHeader(in));
			}
			in.close();
		} catch (IOException ex) {
			Logger.getLogger(ResourceTable.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public short addResInfo(ResourceInfo ri) {
		int r = infos.size();
		infos.add(ri);
		return (short) r;
	}
	
	public short addResBatch(ResourceBatch rb) {
		int r = batches.size();
		batches.add(rb);
		return (short) r;
	}

	public int getResidentResId(int type) {
		for (ResidentResHeader h : resident) {
			if (h.type == type) {
				return h.resId;
			}
		}
		return -1;
	}

	public void write(DataOutputEx out) throws IOException {
		out.order(ByteOrder.BIG_ENDIAN);
		out.writeShort(infos.size());
		for (ResourceInfo i : infos) {
			ResourceComposer.writeUTF(out, i.resourcePath);
			out.writeInt(i.skipOffset);
			out.writeInt(i.readLength);
		}
		out.writeShort(batches.size());
		for (ResourceBatch b : batches) {
			out.write(b.resType);
			out.write(b.subResIds.length);
			out.writeShort(b.mainResId);
			for (short s : b.subResIds) {
				out.writeShort(s);
			}
		}
		out.writeShort(resident.size());
		for (ResidentResHeader h : resident) {
			out.writeShort(h.type);
			out.writeShort(h.resId);
		}
	}
}
