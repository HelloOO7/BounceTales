package bouncetools.sprites;

import java.io.IOException;
import java.util.Map;
import xstandard.formats.yaml.YamlNode;
import xstandard.io.base.iface.DataOutputEx;

public abstract class AbstractSprite {
	
	public static final int TYPEID_MASK = 0b11;
	public static final int FLAG_16BIT = (1 << 2);
	
	public abstract int getTypeID();
	public abstract boolean needs16BitFormat();
	
	public abstract int[] getUsedImageIDs();
	
	public abstract void writeToBinary(DataOutputEx out) throws IOException;
	public abstract void serialize(YamlNode dest, Map<Integer, String> imageResMap);
	
	protected static boolean checkByteOverflow(short... fields) {
		for (short s : fields) {
			if (s > Byte.MAX_VALUE || s < Byte.MIN_VALUE) {
				return true;
			}
		}
		return false;
	}
}
