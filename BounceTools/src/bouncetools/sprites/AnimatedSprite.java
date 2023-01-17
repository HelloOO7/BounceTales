package bouncetools.sprites;

import java.io.DataInput;
import java.io.IOException;
import java.util.Map;
import xstandard.formats.yaml.YamlNode;
import xstandard.io.base.iface.DataOutputEx;
import xstandard.util.collections.IntList;

public class AnimatedSprite extends AbstractSprite {

	public static final int TYPEID = 2;

	public IntList imageIDs;

	public AnimatedSprite(DataInput in, int flags) throws IOException {
		int frameCount = in.readUnsignedShort();
		imageIDs = new IntList(frameCount);
		for (int i = 0; i < frameCount; i++) {
			imageIDs.add(in.readUnsignedShort());
		}
	}
	
	public AnimatedSprite(YamlNode src) {
		YamlNode frames = src.getChildByName("FrameImageIDs");
		imageIDs = new IntList(frames.children.size());
		for (YamlNode ch : frames.children) {
			imageIDs.add(ch.getValueInt());
		}
	}

	@Override
	public int[] getUsedImageIDs() {
		return imageIDs.toArray();
	}

	@Override
	public void serialize(YamlNode dest, Map<Integer, String> imageResMap) {
		dest.addChild("Type", "Animated");
		YamlNode frameImageIDs = dest.addChildKey("FrameImageIDs");
		for (int i = 0; i < imageIDs.size(); i++) {
			frameImageIDs.addChildListElem().addChildValue(String.valueOf(imageIDs.get(i)));
		}
	}

	@Override
	public int getTypeID() {
		return TYPEID;
	}

	@Override
	public boolean needs16BitFormat() {
		return false;
	}

	@Override
	public void writeToBinary(DataOutputEx out) throws IOException {
		out.writeShort(imageIDs.size());
		for (int i = 0; i < imageIDs.size(); i++) {
			out.writeShort(imageIDs.get(i));
		}
	}
}
