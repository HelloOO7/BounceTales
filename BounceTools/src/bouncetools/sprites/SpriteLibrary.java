package bouncetools.sprites;

import bouncetales.ext.rsc.ImageMapEx;
import bouncetales.ext.rsc.ImageMap;
import bouncetools.ResourceComposer;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import xstandard.formats.yaml.Yaml;
import xstandard.formats.yaml.YamlNode;
import xstandard.fs.FSFile;
import xstandard.io.base.iface.DataOutputEx;
import xstandard.io.base.impl.ext.data.DataIOStream;

public class SpriteLibrary {

	public int baseImageId;
	public String[] images;

	public Map<Integer, ImageMap> imageMaps = new LinkedHashMap<>();
	public Map<Integer, AbstractSprite> sprites = new LinkedHashMap<>();

	public SpriteLibrary(byte[] data, String[] subResNames) throws IOException {
		DataIOStream in = new DataIOStream(data);
		in.order(ByteOrder.BIG_ENDIAN);

		int imageCount = in.readByte();
		baseImageId = in.readShort();
		images = Arrays.copyOf(subResNames, imageCount);

		short spriteCount = in.readShort();
		short extraSize = in.readShort();
		short imageMapCount = in.readShort();

		Map<Integer, ImageMapEx> spriteInfo = new LinkedHashMap<>();

		for (int i = 0; i < spriteCount; i++) {
			int id = in.readShort();
			ImageMapEx spr = new ImageMapEx();
			spr.read(-1, in);
			spriteInfo.put(id, spr);
		}

		in.setBaseHere();

		for (Map.Entry<Integer, ImageMapEx> e : spriteInfo.entrySet()) {
			ImageMapEx spr = e.getValue();
			in.seek(spr.offset - 4);

			int flags = in.read();

			AbstractSprite sprobj = null;

			switch (flags & AbstractSprite.TYPEID_MASK) {
				case ExtendedSprite.TYPEID:
					sprobj = new ExtendedSprite(in, flags);
					break;
				case AnimatedSprite.TYPEID:
					sprobj = new AnimatedSprite(in, flags);
					break;
				case CompoundSprite.TYPEID: {
					int endOffset = extraSize + 4;

					for (Map.Entry<Integer, ImageMapEx> e2 : spriteInfo.entrySet()) {
						int o = e2.getValue().offset;
						if (o < endOffset && o > spr.offset) {
							endOffset = o;
						}
					}

					sprobj = new CompoundSprite(in, flags, endOffset - spr.offset);
					break;
				}
			}

			if (sprobj != null) {
				sprites.put(e.getKey(), sprobj);
			} else {
				System.err.println("Invalid typeID!!!");
			}
		}

		in.seek(extraSize);
		in.resetBase();

		for (int i = 0; i < imageMapCount; i++) {
			int id = in.readShort();
			ImageMap m = new ImageMap();
			m.read(in);
			imageMaps.put(id, m);
		}
	}

	public static void parseImageIDs(FSFile yamlFile, Map<String, Short> dest) {
		Yaml yml = new Yaml(yamlFile);
		YamlNode imagesNode = yml.getRootNodeKeyNode("Images");
		int imageId = imagesNode.getChildIntValue("BaseID");
		for (String filename : imagesNode.getChildByName("FileNames").getChildValuesAsListStr()) {
			dest.put(filename, (short) imageId);
			imageId++;
		}
	}

	public SpriteLibrary(FSFile yamlFile, Map<String, Short> imageIDMap) {
		Yaml yml = new Yaml(yamlFile);
		YamlNode imagesNode = yml.getRootNodeKeyNode("Images");
		baseImageId = imagesNode.getChildIntValue("BaseID");
		images = imagesNode.getChildByName("FileNames").getChildValuesAsListStr().toArray(new String[0]);

		for (YamlNode imap : yml.getRootNodeKeyNode("ImageMaps").children) {
			ImageMap map = new ImageMap();
			map.imageId = (byte) ResourceComposer.resolveTagName(imap.getChildValue("SrcImage"), imageIDMap);
			map.width = (byte) imap.getChildIntValue("Width");
			map.height = (byte) imap.getChildIntValue("Height");
			map.atlasX = (byte) imap.getChildIntValue("MapX");
			map.atlasY = (byte) imap.getChildIntValue("MapY");
			map.originX = (byte) imap.getChildIntValue("OriginX");
			map.originY = (byte) imap.getChildIntValue("OriginY");
			imageMaps.put(imap.getChildIntValue("ID"), map);
		}

		for (YamlNode snode : yml.getRootNodeKeyNode("Sprites").children) {
			AbstractSprite spr = null;

			switch (snode.getChildValue("Type")) {
				case "Extended":
					spr = new ExtendedSprite(snode, imageIDMap);
					break;
				case "Compound":
					spr = new CompoundSprite(snode);
					break;
				case "Animated":
					spr = new AnimatedSprite(snode);
					break;
				default:
					System.err.println("Invalid sprite type: " + snode.getChildValue("Type"));
					break;
			}

			if (spr != null) {
				sprites.put(snode.getChildIntValue("ID"), spr);
			}
		}
	}

	public void serialize(YamlNode dest, Map<Integer, String> imageResMap) {
		YamlNode imagesNode = dest.addChildKey("Images");
		imagesNode.addChild("BaseID", baseImageId);
		YamlNode imageFilenames = imagesNode.addChildKey("FileNames");
		for (int i = 0; i < images.length; i++) {
			imageFilenames.addChildListElem().addChildValue(imageResMap.getOrDefault((int) (baseImageId + i), images[i])); //remap to new filenames if changed in resmap
		}

		YamlNode imageMapsNode = dest.addChildKey("ImageMaps");
		for (Map.Entry<Integer, ImageMap> e : imageMaps.entrySet()) {
			YamlNode mapNode = imageMapsNode.addChildListElem();
			ImageMap map = e.getValue();

			mapNode.addChild("ID", e.getKey());
			mapNode.addChild("SrcImage", imageResMap.getOrDefault((int) map.imageId, "#" + map.imageId));
			mapNode.addChild("MapX", map.atlasX & 0xFF);
			mapNode.addChild("MapY", map.atlasY & 0xFF);
			mapNode.addChild("Width", map.width & 0xFF);
			mapNode.addChild("Height", map.height & 0xFF);
			mapNode.addChild("OriginX", map.originX);
			mapNode.addChild("OriginY", map.originY);
		}

		YamlNode spritesNode = dest.addChildKey("Sprites");
		for (Map.Entry<Integer, AbstractSprite> e : sprites.entrySet()) {
			YamlNode sprNode = spritesNode.addChildListElem();
			AbstractSprite spr = e.getValue();

			sprNode.addChild("ID", e.getKey());
			spr.serialize(sprNode, imageResMap);
		}
	}

	public void writeToBinary(DataOutputEx out) throws IOException {
		out.order(ByteOrder.BIG_ENDIAN);
		out.write(images.length);
		out.writeShort(baseImageId);

		Map<Integer, Integer> spriteDataOffsets = new HashMap<>();
		byte[] spriteBytes;
		try (DataIOStream spriteBinary = new DataIOStream()) {
			spriteBinary.order(ByteOrder.BIG_ENDIAN);
			for (Map.Entry<Integer, AbstractSprite> e : sprites.entrySet()) {
				spriteDataOffsets.put(e.getKey(), spriteBinary.getPosition());
				AbstractSprite s = e.getValue();

				int header = s.getTypeID() | (s.needs16BitFormat() ? AbstractSprite.FLAG_16BIT : 0);
				spriteBinary.write(header);
				s.writeToBinary(spriteBinary);
			}
			spriteBytes = spriteBinary.toByteArray();
		}

		out.writeShort(sprites.size());
		out.writeShort(spriteBytes.length);
		out.writeShort(imageMaps.size());

		for (Map.Entry<Integer, AbstractSprite> e : sprites.entrySet()) {
			out.writeShort(e.getKey());
			out.writeShort(spriteDataOffsets.get(e.getKey()));
		}

		out.write(spriteBytes);

		for (Map.Entry<Integer, ImageMap> e : imageMaps.entrySet()) {
			out.writeShort(e.getKey());
			ImageMap im = e.getValue();
			out.writeBytes(im.width, im.height, im.originX, im.originY, im.atlasX, im.atlasY, im.imageId);
		}
	}

	public void writeToYaml(FSFile dest, Map<Integer, String> imageResMap) {
		Yaml yml = new Yaml();
		serialize(yml.root, imageResMap);
		yml.writeToFile(dest);
	}
}
