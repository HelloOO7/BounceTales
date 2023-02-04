package bouncetools.message;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import xstandard.formats.yaml.Yaml;
import xstandard.formats.yaml.YamlNode;
import xstandard.fs.FSFile;
import xstandard.io.base.impl.ext.data.DataIOStream;
import xstandard.util.collections.IntList;

public class MessageData {

	public Map<String, String> lines = new LinkedHashMap<>();

	public MessageData(DataIOStream in, short[] mapping) throws IOException {
		String[] names = MessageIDStrMaker.getNames(mapping);
		List<String> linesUnsorted = new ArrayList<>();
		in.order(ByteOrder.BIG_ENDIAN);
		int limit = in.getLength() - Short.BYTES;
		IntList offsets = new IntList();
		int minoffs = Integer.MAX_VALUE;
		int pos = 0;
		while ((pos = in.getPosition()) <= limit) {
			if (pos == minoffs) {
				//end of offsets
				break;
			}
			int offs = in.readUnsignedShort();
			if (offs < minoffs) {
				minoffs = offs;
			}
			offsets.add(offs);
		}
		for (int i = 0; i < offsets.size(); i++) {
			in.seek(offsets.get(i));
			linesUnsorted.add(new String(in.readBytes(in.readUnsignedShort()), StandardCharsets.UTF_8));
		}
		int index = 0;
		for (short s : mapping) {
			lines.put(names[index++], linesUnsorted.get(s));
		}
	}
	
	public void serialize(YamlNode dest) {
		for (Map.Entry<String, String> e : lines.entrySet()) {
			dest.addChild(e.getKey(), e.getValue());
		}
	}
	
	public void writeYmlToFile(FSFile dest) {
		Yaml yml = new Yaml();
		serialize(yml.root);
		yml.writeToFile(dest);
	}
}
