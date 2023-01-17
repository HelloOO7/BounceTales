package bouncetools.sprites;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import xstandard.formats.yaml.YamlNode;
import xstandard.io.base.iface.DataOutputEx;
import xstandard.util.collections.IntList;

public class CompoundSprite extends AbstractSprite {

	public static final int TYPEID = 1;

	public short unknown1;
	public short unknown2;
	public short unknown3;
	public short unknown4;

	public List<SubSprite> subSprites = new ArrayList<>();
	public List<Extra> extras = new ArrayList<>();

	public CompoundSprite(DataInput in, int flags, int limit) throws IOException {
		boolean is16bit = (flags & FLAG_16BIT) != 0;
		if (is16bit) {
			unknown1 = in.readShort();
			unknown2 = in.readShort();
			unknown3 = in.readShort();
			unknown4 = in.readShort();
		} else {
			unknown1 = in.readByte();
			unknown2 = in.readByte();
			unknown3 = in.readByte();
			unknown4 = in.readByte();
		}

		int entryCount = in.readShort();
		for (int i = 0; i < entryCount; i++) {
			if (is16bit) {
				subSprites.add(SubSprite.read16Bit(in));
			} else {
				subSprites.add(SubSprite.read8Bit(in));
			}
		}

		int pos = (is16bit ? 2 : 1) * (4 + entryCount * 3) + 2 + 1;

		while (pos < limit) {
			extras.add(is16bit ? Extra.read16Bit(in) : Extra.read8Bit(in));
			pos += (is16bit ? 4 : 2);
		}
	}

	public CompoundSprite(YamlNode src) {
		unknown1 = src.getChildShortValue("Unknown1");
		unknown2 = src.getChildShortValue("Unknown2");
		unknown3 = src.getChildShortValue("Unknown3");
		unknown4 = src.getChildShortValue("Unknown4");

		for (YamlNode ssNode : src.getChildByName("SubSprites").children) {
			SubSprite ss = new SubSprite();
			ss.spriteId = ssNode.getChildShortValue("Sprite");
			ss.drawX = ssNode.getChildShortValue("DrawX");
			ss.drawY = ssNode.getChildShortValue("DrawY");
			subSprites.add(ss);
		}
		
		for (YamlNode extrasNode : src.getEnsureChildByName("Extra").children) {
			Extra e = new Extra();
			e.param1 = extrasNode.getChildShortValue("Param1");
			e.param2 = extrasNode.getChildShortValue("Param2");
			extras.add(e);
		}
	}

	@Override
	public void serialize(YamlNode dest, Map<Integer, String> imageResMap) {
		dest.addChild("Type", "Compound");
		dest.addChild("Unknown1", unknown1);
		dest.addChild("Unknown2", unknown2);
		dest.addChild("Unknown3", unknown3);
		dest.addChild("Unknown4", unknown4);

		YamlNode ssListNode = dest.addChildKey("SubSprites");
		for (SubSprite ss : subSprites) {
			YamlNode ssNode = ssListNode.addChildListElem();
			ssNode.addChild("Sprite", ss.spriteId);
			ssNode.addChild("DrawX", ss.drawX);
			ssNode.addChild("DrawY", ss.drawY);
		}

		if (!extras.isEmpty()) {
			YamlNode extrasNode = dest.addChildKey("Extra");
			for (Extra e : extras) {
				YamlNode extraNode = extrasNode.addChildListElem();
				extraNode.addChild("Param1", e.param1);
				extraNode.addChild("Param2", e.param2);
			}
		}
	}

	@Override
	public int[] getUsedImageIDs() {
		IntList l = new IntList(subSprites.size());
		for (SubSprite lnk : subSprites) {
			if (!l.contains(lnk.spriteId)) {
				l.add(lnk.spriteId);
			}
		}
		return l.toArray();
	}

	@Override
	public int getTypeID() {
		return TYPEID;
	}

	@Override
	public boolean needs16BitFormat() {
		if (checkByteOverflow(unknown1, unknown2, unknown3, unknown4)) {
			return true;
		}
		for (SubSprite l : subSprites) {
			if (checkByteOverflow(l.drawX, l.drawY, l.spriteId)) {
				return true;
			}
		}
		for (Extra e : extras) {
			if (checkByteOverflow(e.param1, e.param2)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void writeToBinary(DataOutputEx out) throws IOException {
		boolean is16Bit = needs16BitFormat();

		if (is16Bit) {
			out.writeShorts(unknown1, unknown2, unknown3, unknown4);
		} else {
			out.writeBytes(unknown1, unknown2, unknown3, unknown4);
		}

		out.writeShort(subSprites.size());
		for (SubSprite ss : subSprites) {
			ss.write(out, is16Bit);
		}
		for (Extra e : extras) {
			e.write(out, is16Bit);
		}
	}

	public static class Extra {

		public short param1;
		public short param2;

		public static Extra read8Bit(DataInput in) throws IOException {
			Extra l = new Extra();
			l.param1 = in.readByte();
			l.param2 = in.readByte();
			return l;
		}

		public static Extra read16Bit(DataInput in) throws IOException {
			Extra l = new Extra();
			l.param1 = in.readShort();
			l.param2 = in.readShort();
			return l;
		}

		public void write(DataOutputEx out, boolean is16Bit) throws IOException {
			if (is16Bit) {
				out.writeShorts(param1, param2);
			} else {
				out.writeBytes(param1, param2);
			}
		}
	}

	public static class SubSprite {

		public short drawX;
		public short drawY;

		public short spriteId;

		public static SubSprite read8Bit(DataInput in) throws IOException {
			SubSprite ss = new SubSprite();
			ss.drawX = in.readByte();
			ss.drawY = in.readByte();
			ss.spriteId = in.readByte();
			return ss;
		}

		public static SubSprite read16Bit(DataInput in) throws IOException {
			SubSprite ss = new SubSprite();
			ss.drawX = in.readShort();
			ss.drawY = in.readShort();
			ss.spriteId = in.readShort();
			return ss;
		}

		public void write(DataOutputEx out, boolean is16Bit) throws IOException {
			if (is16Bit) {
				out.writeShorts(drawX, drawY, spriteId);
			} else {
				out.writeBytes(drawX, drawY, spriteId);
			}
		}
	}
}
