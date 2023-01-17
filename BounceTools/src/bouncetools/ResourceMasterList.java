package bouncetools;

import java.util.ArrayList;
import java.util.List;
import xstandard.formats.yaml.Yaml;
import xstandard.formats.yaml.YamlNode;
import xstandard.fs.FSFile;

public class ResourceMasterList {

	public List<String> filenames;
	
	public ResourceMasterList() {
		filenames = new ArrayList<>();
	}

	public ResourceMasterList(FSFile fsf, String rootKey) {
		Yaml yml = new Yaml(fsf);
		filenames = yml.getRootNodeKeyNode(rootKey).getChildValuesAsListStr();
	}

	public void writeToYaml(FSFile fsf, String rootKey) {
		Yaml yml = new Yaml();
		YamlNode root = yml.root.addChildKey(rootKey);
		for (String spl : filenames) {
			root.addChildListElem().addChildValue(spl);
		}
		yml.writeToFile(fsf);
	}
}
