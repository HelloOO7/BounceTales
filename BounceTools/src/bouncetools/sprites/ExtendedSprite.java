package bouncetools.sprites;

import bouncetools.ResourceComposer;
import java.io.DataInput;
import java.io.IOException;
import java.util.Map;
import xstandard.formats.yaml.YamlNode;
import xstandard.io.base.iface.DataOutputEx;

public class ExtendedSprite extends AbstractSprite {

	public static final int TYPEID = 0;

	public short width;
	public short height;
	public short originX;
	public short originY;

	public short atlasX;
	public short atlasY;
	public short imageId;

	public ExtendedSprite(DataInput dis, int flags) throws IOException {
		if ((flags & FLAG_16BIT) != 0) {
			width = dis.readShort();
			height = dis.readShort();
			originX = dis.readShort();
			originY = dis.readShort();
			atlasX = dis.readShort();
			atlasY = dis.readShort();
			imageId = dis.readShort();
		} else {
			width = (short) dis.readUnsignedByte();
			height = (short) dis.readUnsignedByte();
			originX = dis.readByte();
			originY = dis.readByte();
			atlasX = (short) dis.readUnsignedByte();
			atlasY = (short) dis.readUnsignedByte();
			imageId = dis.readByte(); //yup. not unsigned
		}
	}

	public ExtendedSprite(YamlNode src, Map<String, Short> imageResMap) {
		imageId = ResourceComposer.resolveTagName(src.getChildValue("SrcImage"), imageResMap);
		atlasX = src.getChildShortValue("MapX");
		atlasY = src.getChildShortValue("MapY");
		originX = src.getChildShortValue("OriginX");
		originY = src.getChildShortValue("OriginY");
		width = src.getChildShortValue("Width");
		height = src.getChildShortValue("Height");
	}

	@Override
	public int[] getUsedImageIDs() {
		return new int[]{imageId};
	}

	@Override
	public void serialize(YamlNode dest, Map<Integer, String> imageResMap) {
		dest.addChild("Type", "Extended");
		dest.addChild("SrcImage", imageResMap.getOrDefault((int) imageId, "#" + imageId));
		dest.addChild("MapX", atlasX);
		dest.addChild("MapY", atlasY);
		dest.addChild("Width", width);
		dest.addChild("Height", height);
		dest.addChild("OriginX", originX);
		dest.addChild("OriginY", originY);
	}

	@Override
	public int getTypeID() {
		return TYPEID;
	}

	@Override
	public boolean needs16BitFormat() {
		return checkByteOverflow(imageId, originX, originY) || (atlasX > 255 || atlasY > 255 || width > 255 || height > 255);
	}

	@Override
	public void writeToBinary(DataOutputEx out) throws IOException {
		if (needs16BitFormat()) {
			out.writeShorts(width, height, originX, originY, atlasX, atlasY, imageId);
		} else {
			out.writeBytes(width, height, originX, originY, atlasX, atlasY, imageId);
		}
	}
}
