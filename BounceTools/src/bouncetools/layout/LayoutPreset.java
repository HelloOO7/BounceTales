package bouncetools.layout;

import java.io.IOException;
import java.nio.ByteOrder;
import xstandard.formats.yaml.Yaml;
import xstandard.fs.FSFile;
import xstandard.io.base.iface.DataInputEx;
import xstandard.io.base.iface.DataOutputEx;

public class LayoutPreset {
	
	public final LayoutValues layout;
	public final LayoutValues elements;
	
	public LayoutPreset(DataInputEx in) throws IOException {
		in.order(ByteOrder.BIG_ENDIAN);
		elements = new LayoutValues(in, LayoutBinarySchema.ELEMENT);
		layout = new LayoutValues(in, LayoutBinarySchema.LAYOUT);
	}
	
	public LayoutPreset(FSFile yamlFile) {
		Yaml yml = new Yaml(yamlFile);
		elements = new LayoutValues(yml.getRootNodeKeyNode("ElementConfig"), LayoutBinarySchema.ELEMENT);
		layout = new LayoutValues(yml.getRootNodeKeyNode("LayoutConfig"), LayoutBinarySchema.LAYOUT);
	}
	
	public void writeToBinary(DataOutputEx out) throws IOException {
		out.order(ByteOrder.BIG_ENDIAN);
		elements.writeToBinary(out);
		layout.writeToBinary(out);
	}
	
	public void writeToYaml(FSFile out) {
		Yaml yml = new Yaml();
		elements.serialize(yml.getEnsureRootNodeKeyNode("ElementConfig"));
		layout.serialize(yml.getEnsureRootNodeKeyNode("LayoutConfig"));
		yml.writeToFile(out);
	}
}
