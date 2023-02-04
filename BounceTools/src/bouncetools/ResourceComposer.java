package bouncetools;

import bouncetales.ext.rsc.ResidentResHeader;
import bouncetales.ext.rsc.ResourceBatch;
import bouncetales.ext.rsc.ResourceInfo;
import bouncetales.ext.rsc.ResourceType;
import bouncetools.layout.LayoutPreset;
import bouncetools.message.MessageCompiler;
import bouncetools.message.MessageIncludeGenerator;
import bouncetools.resmap.ResidentResourceList;
import bouncetools.resmap.ResourceTable;
import bouncetools.sprites.SpriteLibrary;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import xstandard.formats.yaml.Yaml;
import xstandard.formats.yaml.YamlReflectUtil;
import xstandard.fs.FSFile;
import xstandard.fs.FSUtil;
import xstandard.fs.accessors.DiskFile;
import xstandard.io.base.impl.ext.data.DataOutStream;

public class ResourceComposer {

	public static final String VERSION = "1.0.0";

	public static final String CONFIG_FILENAME = "composer.yml";
	public static final String VERSION_FILENAME = "composer_version.txt";

	public static final String UNCOMPILED_RESOURCE_EXT = ".res";

	public static final String MESSAGEDATA_DIR = "Localized";
	public static final String GRAPHICS_DIR = "Graphics";
	public static final String AUDIO_DIR = "Audio";
	public static final String LAYOUT_DIR = "Layout";
	public static final String LEVELS_DIR = "Levels";

	public static final String ICON_FILENAME = "icon.png";
	public static final String GRAPHICS_LIST_FILENAME = "master.res";
	public static final String AUDIO_LIST_FILENAME = "sound_list.res";
	public static final String RESIDENT_FILENAME = "resident.res";

	public static boolean isUncompiledResource(FSFile f) {
		return f.getName().endsWith(UNCOMPILED_RESOURCE_EXT);
	}

	public static void main(String[] args) {
		try {
			compose(new DiskFile("res_decomposed"));
		} catch (IOException ex) {
			Logger.getLogger(ResourceComposer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void cleanDirectory(FSFile dir, String... exclude) {
		if (dir.exists()) {
			Set<String> excludeSet = new HashSet<>();
			for (String s : exclude) {
				excludeSet.add(s);
			}
			for (FSFile child : dir.listFiles()) {
				if (!excludeSet.contains(child.getName())) {
					child.delete();
				}
			}
		}
	}

	public static void compose(FSFile srcDir) throws IOException {
		Config cfg = YamlReflectUtil.deserialize(new Yaml(srcDir.getChild(CONFIG_FILENAME)).root, Config.class);
		FSFile dstDir = srcDir.getChild(cfg.destDir);
		cleanDirectory(dstDir, ".gitignore");
		dstDir.mkdirs();

		srcDir.getChild(ResourceComposer.ICON_FILENAME).copyTo(dstDir.getChild(ResourceComposer.ICON_FILENAME));

		IFilenameSupplier fn = cfg.obfuscate ? OBFUSCATE_FILENAME : KEEP_FILENAME;

		FSFile msgRoot = srcDir.getChild(ResourceComposer.MESSAGEDATA_DIR);
		FSFile audioRoot = srcDir.getChild(ResourceComposer.AUDIO_DIR);
		FSFile layoutRoot = srcDir.getChild(ResourceComposer.LAYOUT_DIR);
		FSFile graphicsRoot = srcDir.getChild(ResourceComposer.GRAPHICS_DIR);
		FSFile levelRoot = srcDir.getChild(ResourceComposer.LEVELS_DIR);

		LinkedHashMap<String, Short> msgMap = MessageCompiler.compileMessageDataDir(msgRoot, dstDir);

		String resTableFn = fn.getFilename("index.bin");
		FSFile resTableFile = dstDir.getChild(resTableFn);
		String graphicsMasterFn = fn.getFilename(GRAPHICS_DIR + "/master.bin");
		FSFile graphicsMasterFile = dstDir.getChild(graphicsMasterFn);
		graphicsMasterFile.getParent().mkdirs();
		DataOutStream graphicsMasterOut = graphicsMasterFile.getDataOutputStream();

		ResourceTable restbl = new ResourceTable();
		Map<String, Short> resNameMap = new HashMap<>();

		//Compile graphics
		ResourceMasterList sml = new ResourceMasterList(graphicsRoot.getChild(GRAPHICS_LIST_FILENAME), "Sprites");
		Map<String, Short> imageResIDMap = new HashMap<>();
		for (String splFilename : sml.filenames) {
			SpriteLibrary.parseImageIDs(graphicsRoot.getChild(splFilename), imageResIDMap);
		}
		for (String splFilename : sml.filenames) {
			SpriteLibrary slib = new SpriteLibrary(graphicsRoot.getChild(splFilename), imageResIDMap);
			int outOffs = graphicsMasterOut.getPosition();
			slib.writeToBinary(graphicsMasterOut);
			short mainResId = restbl.addResInfo(createResInfo(graphicsMasterFn, outOffs, graphicsMasterOut.getPosition() - outOffs));
			short[] subResIDs = new short[slib.images.length];
			for (int i = 0; i < subResIDs.length; i++) {
				String srcImageFn = GRAPHICS_DIR + "/" + slib.images[i];
				String dstImageFn = fn.getFilename(srcImageFn, true);
				subResIDs[i] = restbl.addResInfo(createResInfo(dstImageFn));
				copyFile(graphicsRoot.getChild(slib.images[i]), dstDir.getChild(dstImageFn));
			}
			resNameMap.put(GRAPHICS_DIR + "/" + splFilename, restbl.addResBatch(createResBatch(ResourceType.IMAGE, mainResId, subResIDs)));
		}

		graphicsMasterOut.close();

		//Compile audio
		ResourceMasterList aml = new ResourceMasterList(audioRoot.getChild(AUDIO_LIST_FILENAME), "Sounds");
		for (String audioFileName : aml.filenames) {
			String srcFilename = AUDIO_DIR + "/" + audioFileName;
			String dstFilename = fn.getFilename(srcFilename, true);
			short mainIdx = restbl.addResInfo(createDummyResInfo());
			short subIdx = restbl.addResInfo(createResInfo(dstFilename));
			resNameMap.put(srcFilename, restbl.addResBatch(createResBatch(ResourceType.MIDI, mainIdx, subIdx)));
			copyFile(audioRoot.getChild(audioFileName), dstDir.getChild(dstFilename));
		}

		//Compile layouts
		String layoutMasterFn = fn.getFilename(LAYOUT_DIR + "/layout_params.bin");
		FSFile layoutMasterFile = dstDir.getChild(layoutMasterFn);
		layoutMasterFile.getParent().mkdirs();
		DataOutStream layoutStream = layoutMasterFile.getDataOutputStream();
		for (FSFile layoutFile : layoutRoot.listFiles()) {
			String srcFilename = LAYOUT_DIR + "/" + layoutFile.getName();
			int outOffs = layoutStream.getPosition();
			new LayoutPreset(layoutFile).writeToBinary(layoutStream);
			short mainIdx = restbl.addResInfo(createResInfo(layoutMasterFn, outOffs, layoutStream.getPosition() - outOffs));
			resNameMap.put(srcFilename, restbl.addResBatch(createResBatch(ResourceType.LAYOUT, mainIdx)));
		}
		layoutStream.close();

		//Compile levels
		for (FSFile levelFile : levelRoot.listFiles()) {
			if (levelFile.getName().endsWith(".rlef")) {
				String srcFilename = LEVELS_DIR + "/" + levelFile.getName();
				String dstFilename = fn.getFilename(srcFilename);
				short mainIdx = restbl.addResInfo(createResInfo(dstFilename, 0, levelFile.length()));
				resNameMap.put(srcFilename, restbl.addResBatch(createResBatch(ResourceType.LEVEL, mainIdx)));
				copyFile(levelFile, dstDir.getChild(dstFilename));
			}
		}

		//Compile resident resource map
		String residentListFn = fn.getFilename("resident.bin");
		FSFile residentListFile = dstDir.getChild(residentListFn);
		ResidentResourceList residentList = new ResidentResourceList(srcDir.getChild(RESIDENT_FILENAME), resNameMap);
		residentList.writeToBinary(residentListFile);
		restbl.resident.add(createResidentHeader(6,
				restbl.addResInfo(createResInfo(residentListFn, 0, residentListFile.length()))
		));

		try (DataOutStream out = resTableFile.getDataOutputStream()) {
			restbl.write(out);
		}

		if (cfg.includeRoot != null) {
			if (cfg.resMapIncludeClass != null) {
				String[] tgt = getIncludeTarget(cfg.resMapIncludeClass);
				ResourceIncludeGenerator.generate(srcDir.getChild(cfg.includeRoot).getChild(tgt[0]), tgt[1], resTableFn, resNameMap);
			}
			if (cfg.msgMapIncludeClass != null) {
				if (!msgMap.containsKey("UI_MORE_GAMES")) {
					msgMap.put("UI_MORE_GAMES", (short) -1); //fix compile errors when loading pre-2.0.25 resources
				}
				String[] tgt = getIncludeTarget(cfg.msgMapIncludeClass);
				MessageIncludeGenerator.generate(srcDir.getChild(cfg.includeRoot).getChild(tgt[0]), tgt[1], msgMap);
			}
		}
	}

	private static String[] getIncludeTarget(String cfgValue) {
		String className = cfgValue.substring(cfgValue.lastIndexOf('.') + 1); //if not found, -1 + 1 = 0 = entire string
		String packageName = cfgValue.substring(0, cfgValue.length() - className.length());
		if (packageName.endsWith(".")) {
			packageName = packageName.substring(0, packageName.length() - 1);
		}
		String basepath = packageName.replace('.', '/') + "/";
		return new String[]{basepath + className + ".java", packageName};
	}

	public static short resolveTagName(String name, Map<String, Short> nameToResIDMap) {
		if (name.startsWith("#")) {
			return Short.parseShort(name.substring(1));
		}
		return nameToResIDMap.get(name);
	}

	private static void copyFile(FSFile src, FSFile dst) {
		dst.getParent().mkdirs();
		src.copyTo(dst);
	}

	private static ResidentResHeader createResidentHeader(int type, short resId) {
		ResidentResHeader h = new ResidentResHeader();
		h.type = (short) type;
		h.resId = resId;
		return h;
	}

	private static ResourceBatch createResBatch(byte resType, short mainId, short... subIds) {
		ResourceBatch rb = new ResourceBatch();
		rb.resType = resType;
		rb.mainResId = mainId;
		rb.subResIds = subIds;
		return rb;
	}

	private static ResourceInfo createDummyResInfo() {
		return createResInfo("", 0, 0);
	}

	private static ResourceInfo createResInfo(String path) {
		return createResInfo(path, -1, -1);
	}

	private static ResourceInfo createResInfo(String path, int offset, int size) {
		ResourceInfo ri = new ResourceInfo();
		ri.resourcePath = path;
		ri.readLength = size;
		ri.skipOffset = offset;
		return ri;
	}

	public static void writeUTF(DataOutput out, String str) throws IOException {
		byte[] strdata = str.getBytes(StandardCharsets.UTF_8);
		out.writeShort(strdata.length);
		out.write(strdata);
	}

	public static interface IFilenameSupplier {

		public String getFilename(String originalFilename, boolean mustKeepExtension);

		public default String getFilename(String originalFilename) {
			return getFilename(originalFilename, false);
		}
	}

	public static IFilenameSupplier KEEP_FILENAME = (String originalFilename, boolean mustKeepExtension) -> originalFilename;

	public static IFilenameSupplier OBFUSCATE_FILENAME = new IFilenameSupplier() {

		private final Map<String, String> mappedNames = new HashMap<>();
		private int curFileIndex = 1;

		private String getNameFromIndex() {
			StringBuilder sb = new StringBuilder(3);
			int v = curFileIndex;
			if (v == 0) {
				return "a";
			}
			while (v != 0) {
				v--;
				int rem = (v % 26);
				v /= 26;
				sb.append((char) (rem + 'a'));
			}
			sb.reverse();
			return sb.toString();
		}

		@Override
		public String getFilename(String originalFilename, boolean mustKeepExtension) {
			String newName = mappedNames.get(originalFilename);
			if (newName == null) {
				newName = getNameFromIndex();
				curFileIndex++;
				if (mustKeepExtension) {
					newName += FSUtil.getFileExtensionWithDot(originalFilename);
				}
			}
			mappedNames.put(originalFilename, newName);
			return newName;
		}
	};

	public static class Config {

		public boolean obfuscate;
		public String destDir;
		public String includeRoot;
		public String resMapIncludeClass;
		public String msgMapIncludeClass;
	}
}
