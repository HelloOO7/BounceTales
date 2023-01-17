package bouncetools;

import bouncetales.ext.rsc.ResourceBatch;
import bouncetales.ext.rsc.ResourceInfo;
import bouncetales.ext.rsc.ResourceType;
import bouncetools.layout.LayoutPreset;
import bouncetools.message.MessageData;
import bouncetools.resmap.ResidentResourceList;
import bouncetools.resmap.ResourceTable;
import bouncetools.sprites.SpriteLibrary;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import xstandard.formats.yaml.YamlReflectUtil;
import xstandard.fs.FSFile;
import xstandard.fs.FSUtil;
import xstandard.fs.accessors.DiskFile;
import xstandard.io.base.iface.ReadableStream;
import xstandard.io.base.impl.ext.data.DataIOStream;
import xstandard.util.collections.IntList;

public class ResourceDecomposer {

	public static final String RESOURCE_INDEX_PATH = "/a";

	public static final String[] LANG_FILENAMES = new String[]{
		"lang.bs-BA",
		"lang.cs-CZ",
		"lang.de",
		"lang.hr-HR",
		"lang.hu-HU",
		"lang.mk-MK",
		"lang.sk-SK",
		"lang.sl-SI",
		"lang.sq",
		"lang.sr-YU",
		"lang.xx"
	};

	public static final Map<String, String> AUDIO_FILENAME_MAP = makeHashMap(
			"aw.mid", "bgm_title.mid",
			"ax.mid", "bgm_level_act01.mid",
			"ay.mid", "bgm_level_act02.mid",
			"az.mid", "bgm_level_act03.mid",
			"ba.mid", "bgm_level_boss.mid",
			"bb.mid", "me_lose.mid",
			"bc.mid", "me_win.mid",
			"bd.mid", "bgm_level_bonus.mid"
	);

	public static final Map<Integer, String> LAYOUT_FILENAME_MAP = makeHashMap(
			36, "InfoLayout.res",
			37, "MenuLayoutA.res",
			38, "MenuLayoutB.res"
	);

	public static final Map<String, String> LEVEL_FILENAME_MAP = makeHashMap(
			"bf", "level_campaign01.rlef",
			"bg", "level_campaign02.rlef",
			"bh", "level_campaign03.rlef",
			"bi", "level_campaign04.rlef",
			"bj", "level_campaign05.rlef",
			"bk", "level_campaign06.rlef",
			"bl", "level_campaign07.rlef",
			"bm", "level_campaign08.rlef",
			"bn", "level_campaign09.rlef",
			"bo", "level_campaign10.rlef",
			"bp", "level_campaign11.rlef",
			"bq", "level_campaign12.rlef",
			"br", "level_extra01.rlef",
			"bs", "level_extra02.rlef",
			"bt", "level_extra03.rlef",
			"bu", "level_obj01_cannon.rlef"
	);

	public static final Map<String, String> IMAGE_FILENAME_MAP = makeHashMap(
			"c.png", "ObjFriend.png",
			"d.png", "BallParts.png",
			"e.png", "BallHighlight.png",
			"f.png", "ObjStoneWall.png",
			"g.png", "ObjCannon.png",
			"h.png", "ObjLever.png",
			"i.png", "ObjDoor.png",
			"j.png", "SplashLogo.png",
			"k.png", "LevelAct03Exit.png",
			"l.png", "LevelAct03Parallax.png",
			"m.png", "LevelAct03Blower.png",
			"n.png", "LevelAct03Weeds.png",
			"o.png", "LevelAct03Trampoline.png",
			"p.png", "LevelAct03Terrain.png",
			"q.png", "LevelAct03Parts.png",
			"r.png", "LevelAct03Wall.png",
			"s.png", "UIArrows.png",
			"t.png", "UIMainMenu.png",
			"u.png", "LevelAct02Exit.png",
			"v.png", "LevelAct02Parallax.png",
			"w.png", "LevelAct02Parts.png",
			"x.png", "LevelAct02Terrain.png",
			"y.png", "LevelAct02Trampoline.png",
			"z.png", "LevelAct02Wall.png",
			"aa.png", "LevelAct01Exit.png",
			"ab.png", "LevelAct01BeanPod.png",
			"ac.png", "LevelAct01Trampoline.png",
			"ad.png", "LevelAct01Wall.png",
			"ae.png", "LevelAct01Parallax.png",
			"af.png", "LevelAct01Parts.png",
			"ag.png", "LevelAct01Terrain.png",
			"ah.png", "UILevelSelect.png",
			"ai.png", "UILevelStats.png",
			"aj.png", "UINumberFont.png",
			"ak.png", "ObjHypnotoid.png",
			"al.png", "ObjColorMachine.png",
			"am.png", "ObjColorMachineBroken.png",
			"an.png", "Enemy02Mole.png",
			"ao.png", "Enemy00Candle.png",
			"ap.png", "ParticleCommon.png",
			"aq.png", "ObjEgg.png",
			"ar.png", "BallBumpyCracks.png",
			"as.png", "ObjSignboard.png",
			"at.png", "ObjSpike.png",
			"au.png", "UIPauseMenu.png",
			"av.png", "ParticleSplash.png"
	);

	public static final Map<Integer, String> SPRITE_LIBRARY_FILENAME_MAP = makeHashMap(
			0, "ObjFriend.res",
			1, "BallParts.res",
			2, "BallHighlight.res",
			3, "ObjStoneWall.res",
			4, "ObjCannon.res",
			5, "ObjLever.res",
			6, "ObjDoor.res",
			7, "SplashLogo.res",
			8, "LevelAct03.res",
			9, "UIArrows.res",
			10, "UIMainMenu.res",
			11, "LevelAct02.res",
			12, "LevelAct01.res",
			13, "UILevelSelect.res",
			14, "UILevelStats.res",
			15, "UINumberFont.res",
			16, "ObjHypnotoid.res",
			17, "ObjColorMachine.res",
			18, "ObjColorMachineBroken.res",
			19, "Enemy02Mole.res",
			20, "Enemy00Candle.res",
			21, "ParticleCommon.res",
			22, "ObjEgg.res",
			23, "BallBumpyCracks.res",
			24, "ObjSignboard.res",
			25, "ObjSpike.res",
			26, "UIPauseMenu.res",
			27, "ParticleSplash.res"
	);

	private static <K, V> Map<K, V> makeHashMap(Object... src) {
		Map<K, V> map = new HashMap<>();
		for (int i = 0; i < src.length; i += 2) {
			map.put((K) src[i], (V) src[i + 1]);
		}
		return map;
	}

	private static byte[] getResourceData(FSFile root, ResourceTable tbl, int resId) throws IOException {
		ResourceInfo i = tbl.infos.get(resId);
		return getResourceData(root, i.resourcePath, i.skipOffset, i.readLength);
	}

	private static byte[] getResourceData(FSFile root, String path) throws IOException {
		return getResourceData(root, path, -1, -1);
	}

	private static byte[] getResourceData(FSFile root, String path, int offset, int length) throws IOException {
		if (length == 0) {
			return null;
		}
		if (offset == -1) {
			offset = 0;
		}
		InputStream in;
		if (root == null) {
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			in = ResourceDecomposer.class.getResourceAsStream(path);
		} else {
			FSFile f = root.getChild(path);
			ReadableStream rs = f.getInputStream();
			if (rs == null) {
				throw new FileNotFoundException("Could not find resource " + f + "!");
			}
			in = rs.getInputStream();
		}
		if (in == null) {
			return null;
		}
		byte[] ret = null;
		in.skip(offset);
		if (length != -1) {
			ret = new byte[length];
			in.read(ret);
		} else {
			ret = FSUtil.readStreamToBytes(in);
		}
		in.close();
		return ret;
	}

	public static void decompose(FSFile src, FSFile dst) throws IOException {
		ResourceComposer.cleanDirectory(dst, "composer.yml");
		dst.mkdirs();

		FSFile msgRoot = dst.getChild(ResourceComposer.MESSAGEDATA_DIR);
		FSFile audioRoot = dst.getChild(ResourceComposer.AUDIO_DIR);
		FSFile layoutRoot = dst.getChild(ResourceComposer.LAYOUT_DIR);
		FSFile graphicsRoot = dst.getChild(ResourceComposer.GRAPHICS_DIR);
		FSFile levelRoot = dst.getChild(ResourceComposer.LEVELS_DIR);

		msgRoot.mkdir();
		audioRoot.mkdir();
		layoutRoot.mkdir();
		graphicsRoot.mkdir();
		levelRoot.mkdir();

		for (String langfile : LANG_FILENAMES) {
			byte[] data = getResourceData(src, langfile);
			if (data != null) {
				try (DataIOStream in = new DataIOStream(data)) {
					new MessageData(in).writeYmlToFile(msgRoot.getChild(langfile + ResourceComposer.UNCOMPILED_RESOURCE_EXT));
				}
			}
		}
		dst.getChild(ResourceComposer.ICON_FILENAME).setBytes(getResourceData(src, ResourceComposer.ICON_FILENAME));
		ResourceTable restbl = new ResourceTable(getResourceData(src, RESOURCE_INDEX_PATH));

		Map<Integer, SpriteLibrary> spriteLibs = new HashMap<>();
		IntList spriteLibOrder = new IntList();

		IntList soundOrder = new IntList();

		Map<Integer, String> globalResIdToNameMap = new HashMap<>();

		int resId = 0;
		for (ResourceBatch b : restbl.batches) {
			String filename = null;

			switch (b.resType) {
				case ResourceType.MIDI:
					ResourceInfo subres = restbl.infos.get(b.subResIds[0]);
					byte[] resdata = getResourceData(src, restbl, b.subResIds[0]);
					if (resdata == null) {
						throw new IOException("Could not read resource: " + subres.resourcePath);
					}
					filename = ResourceComposer.AUDIO_DIR + "/" + AUDIO_FILENAME_MAP.getOrDefault(subres.resourcePath, subres.resourcePath);
					dst.getChild(filename).setBytes(resdata);
					soundOrder.add(resId);
					break;
				case ResourceType.LEVEL:
					ResourceInfo lvlRes = restbl.infos.get(b.mainResId);
					filename = ResourceComposer.LEVELS_DIR + "/" + LEVEL_FILENAME_MAP.getOrDefault(lvlRes.resourcePath, lvlRes.resourcePath + ".rlef");
					dst.getChild(filename).setBytes(getResourceData(src, restbl, b.mainResId));
					break;
				case ResourceType.LAYOUT: {
					try (DataIOStream in = new DataIOStream(getResourceData(src, restbl, b.mainResId))) {
						new LayoutPreset(in).writeToYaml(dst.getChild(
								filename = ResourceComposer.LAYOUT_DIR + "/" + LAYOUT_FILENAME_MAP.getOrDefault(resId, "Layout" + resId + ResourceComposer.UNCOMPILED_RESOURCE_EXT))
						);
					}
					break;
				}
				case ResourceType.IMAGE:
					String[] imageNames = new String[b.subResIds.length];
					for (int i = 0; i < imageNames.length; i++) {
						imageNames[i] = restbl.infos.get(b.subResIds[i]).resourcePath;
						String imageFilename = ResourceComposer.GRAPHICS_DIR + "/" + IMAGE_FILENAME_MAP.getOrDefault(imageNames[i], imageNames[i]);
						dst.getChild(imageFilename).setBytes(getResourceData(src, restbl, b.subResIds[i]));
						globalResIdToNameMap.put((int) b.subResIds[i], imageFilename);
					}
					SpriteLibrary lib = new SpriteLibrary(getResourceData(src, restbl, b.mainResId), imageNames);
					spriteLibs.put(resId, lib);
					spriteLibOrder.add(resId);
					break;
			}

			if (filename != null) {
				globalResIdToNameMap.put(resId, filename);
			}

			resId++;
		}

		Map<Integer, String> imageNameMap = new HashMap<>();

		for (SpriteLibrary sl : spriteLibs.values()) {
			for (int i = 0; i < sl.images.length; i++) {
				imageNameMap.put(i + sl.baseImageId, IMAGE_FILENAME_MAP.getOrDefault(sl.images[i], sl.images[i]));
			}
		}

		for (Map.Entry<Integer, SpriteLibrary> sl : spriteLibs.entrySet()) {
			String filename = ResourceComposer.GRAPHICS_DIR + "/" + SPRITE_LIBRARY_FILENAME_MAP.getOrDefault(sl.getKey(), "Library" + sl.getKey() + ".res");
			sl.getValue().writeToYaml(dst.getChild(filename), imageNameMap);
			globalResIdToNameMap.put(sl.getKey(), filename);
		}

		{
			//Sprite master list
			ResourceMasterList sml = new ResourceMasterList();
			for (int i = 0; i < spriteLibOrder.size(); i++) {
				int sln = spriteLibOrder.get(i);
				sml.filenames.add(SPRITE_LIBRARY_FILENAME_MAP.getOrDefault(sln, "Library" + sln + ".res"));
			}
			sml.writeToYaml(graphicsRoot.getChild(ResourceComposer.GRAPHICS_LIST_FILENAME), "Sprites");
		}

		{
			//Audio master list
			ResourceMasterList aml = new ResourceMasterList();
			for (int i = 0; i < soundOrder.size(); i++) {
				String name = restbl.infos.get(restbl.batches.get(soundOrder.get(i)).subResIds[0]).resourcePath;
				aml.filenames.add(AUDIO_FILENAME_MAP.getOrDefault(name, name));
			}
			aml.writeToYaml(audioRoot.getChild(ResourceComposer.AUDIO_LIST_FILENAME), "Sounds");
		}

		{
			//Resident resource table
			int residentResId = restbl.getResidentResId(6);
			if (residentResId != -1) {
				ResidentResourceList list = new ResidentResourceList(getResourceData(src, restbl, residentResId));
				list.writeToYaml(dst.getChild(ResourceComposer.RESIDENT_FILENAME), globalResIdToNameMap);
			}
		}

		{
			//composer.yml
			FSFile composerFile = dst.getChild(ResourceComposer.CONFIG_FILENAME);
			if (!composerFile.exists()) {
				ResourceComposer.Config cfg = new ResourceComposer.Config();
				cfg.obfuscate = true;
				cfg.destDir = src != null ? src.getPathRelativeTo(dst) : "compiled";
				YamlReflectUtil.serializeObjectAsYml(cfg).writeToFile(composerFile);
			}
		}

		{
			//composer_version.txt
			FSUtil.writeStringToFile(dst.getChild(ResourceComposer.VERSION_FILENAME), ResourceComposer.VERSION);
		}
	}

	public static void main(String[] args) {
		try {
			decompose(null, new DiskFile("res_decomposed"));
		} catch (IOException ex) {
			Logger.getLogger(ResourceDecomposer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
