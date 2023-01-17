package bouncetools.message;

import bouncetools.ResourceComposer;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import xstandard.formats.yaml.Yaml;
import xstandard.fs.FSFile;
import xstandard.io.base.impl.ext.data.DataIOStream;
import xstandard.io.structs.TemporaryOffsetShort;

public class MessageCompiler {

	public static void compileMessageDataDir(FSFile src, FSFile dst) throws IOException {
		dst.mkdir();
		for (FSFile child : src.listFiles()) {
			if (ResourceComposer.isUncompiledResource(child)) {
				compileMessageData(child, dst.getChild(child.getNameWithoutExtension()));
			}
		}
	}

	public static void compileMessageData(FSFile src, FSFile dst) throws IOException {
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
			String str = srcYml.root.children.get(i).getValue();
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
