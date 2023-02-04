package bouncetools.message;

import bouncetools.ResourceComposer;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import xstandard.formats.yaml.Yaml;
import xstandard.formats.yaml.YamlNode;
import xstandard.fs.FSFile;
import xstandard.io.base.impl.ext.data.DataIOStream;
import xstandard.io.structs.TemporaryOffsetShort;

public class MessageCompiler {

	public static LinkedHashMap<String, Short> compileMessageDataDir(FSFile src, FSFile dst) throws IOException {
		LinkedHashMap<String, Short> mapout = new LinkedHashMap<>();
		dst.mkdir();
		for (FSFile child : src.listFiles()) {
			if (ResourceComposer.isUncompiledResource(child)) {
				compileMessageData(child, dst.getChild(child.getNameWithoutExtension()), child.getName().equals("lang.xx.res") ? mapout : null);
			}
		}
		return mapout;
	}

	public static void compileMessageData(FSFile src, FSFile dst, Map<String, Short> mapout) throws IOException {
		Yaml srcYml = new Yaml(src);
		DataIOStream out = dst.getDataIOStream();
		out.setLength(0);
		out.order(ByteOrder.BIG_ENDIAN);

		int count = srcYml.root.children.size();

		TemporaryOffsetShort[] offsets = new TemporaryOffsetShort[count];

		for (int i = 0; i < count; i++) {
			offsets[i] = new TemporaryOffsetShort(out);
		}

		Map<String, Integer> existOffsets = new HashMap<>();

		for (int i = 0; i < count; i++) {
			YamlNode node = srcYml.root.children.get(i);
			String key = node.getKey();
			String str = node.getValue();
			if (mapout != null) {
				mapout.put(key, (short) i);
			}
			boolean write = true;
			int offs;
			if (existOffsets.containsKey(str)) {
				offs = existOffsets.get(str);
				write = false;
			} else {
				offs = out.getPosition();
			}
			offsets[i].set(offs);
			if (write) {
				ResourceComposer.writeUTF(out, str);
				existOffsets.put(str, offs);
			}
		}

		out.close();
	}
}
