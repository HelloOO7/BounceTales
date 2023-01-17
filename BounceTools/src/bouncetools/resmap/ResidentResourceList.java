package bouncetools.resmap;

import bouncetools.ResourceComposer;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import xstandard.formats.yaml.Yaml;
import xstandard.formats.yaml.YamlNode;
import xstandard.fs.FSFile;
import xstandard.io.base.iface.DataInputEx;
import xstandard.io.base.impl.ext.data.DataIOStream;
import xstandard.io.base.impl.ext.data.DataOutStream;
import xstandard.util.collections.IntList;

public class ResidentResourceList {

	public List<IntList> groups;
	
	public ResidentResourceList(byte[] data) throws IOException {
		this(new DataIOStream(data));
	}
	
	public ResidentResourceList(DataInputEx in) throws IOException {
		in.order(ByteOrder.BIG_ENDIAN);
		int groupCount = in.readShort();
		groups = new ArrayList<>(groupCount);
		for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
			int resCount = in.readShort();
			IntList resIds = new IntList(resCount);
			for (int resIdx = 0; resIdx < resCount; resIdx++) {
				resIds.add(in.readShort());
			}
			groups.add(resIds);
		}
		in.close();
	}
	
	public ResidentResourceList(FSFile yamlFile, Map<String, Short> nameToResIDMap) {
		Yaml yml = new Yaml(yamlFile);
		YamlNode groupsNode = yml.getRootNodeKeyNode("Groups");
		groups = new ArrayList<>(groupsNode.children.size());
		for (YamlNode groupNode : groupsNode.children) {
			List<String> resPaths = groupNode.getChildByName("Resources").getChildValuesAsListStr();
			IntList resIDs = new IntList(resPaths.size());
			for (String resPath : resPaths) {
				resIDs.add(ResourceComposer.resolveTagName(resPath, nameToResIDMap));
			}
			groups.add(resIDs);
		}
	}
	
	public void serialize(YamlNode dest, Map<Integer, String> resIDToNameMap) {
		YamlNode groupsNode = dest.addChildKey("Groups");
		for (IntList g : groups) {
			YamlNode gNode = groupsNode.addChildListElem();
			YamlNode resIdsNode = gNode.addChildKey("Resources");
			for (int i = 0; i < g.size(); i++) {
				resIdsNode.addChildListElem().addChildValue(resIDToNameMap.getOrDefault(g.get(i), "#" + g.get(i)));
			}
		}
	}
	
	public void writeToBinary(FSFile dest) throws IOException {
		try (DataOutStream out = dest.getDataOutputStream()) {
			out.order(ByteOrder.BIG_ENDIAN);
			out.writeShort(groups.size());
			for (IntList g : groups) {
				out.writeShort(g.size());
				for (int i = 0; i < g.size(); i++) {
					out.writeShort(g.get(i));
				}
			}
		}
	}
	
	public void writeToYaml(FSFile dest, Map<Integer, String> resIDToNameMap) {
		Yaml yml = new Yaml();
		serialize(yml.root, resIDToNameMap);
		yml.writeToFile(dest);
	}
}
