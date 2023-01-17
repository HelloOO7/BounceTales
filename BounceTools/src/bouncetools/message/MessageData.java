package bouncetools.message;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import xstandard.formats.yaml.Yaml;
import xstandard.formats.yaml.YamlNode;
import xstandard.fs.FSFile;
import xstandard.io.base.impl.ext.data.DataIOStream;
import xstandard.util.collections.IntList;

public class MessageData {

	public List<String> lines = new ArrayList<>();

	public MessageData(DataIOStream in) throws IOException {
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
			lines.add(new String(in.readBytes(in.readUnsignedShort()), StandardCharsets.UTF_8));
		}
	}
	
	public void serialize(YamlNode dest) {
		int idx = 0;
		for (String line : lines) {
			dest.addChild(String.valueOf(idx), line);
			idx++;
		}
	}
	
	public void writeYmlToFile(FSFile dest) {
		Yaml yml = new Yaml();
		serialize(yml.root);
		yml.writeToFile(dest);
	}
}
