package bouncetools;

import bouncetales.ext.rsc.ImageMapEx;
import bouncetales.ext.rsc.ImageMap;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class ImageTable {

	public int baseImageId;
	public int imageCount;

	public Map<Integer, ImageMapEx> map2 = new LinkedHashMap<Integer, ImageMapEx>();
	public byte[] extra;
	public Map<Integer, ImageMap> map = new HashMap<Integer, ImageMap>();

	public ImageTable(DataInputStream in, int resBatchId) throws IOException {
		imageCount = in.readByte();
		baseImageId = in.readShort();
		short count1 = in.readShort();
		short extraSize = in.readShort();
		short imageMapCount = in.readShort();
		for (int i = 0; i < count1; i++) {
			int id = in.readShort();
			ImageMapEx m2 = new ImageMapEx();
			m2.read(resBatchId, in);
			map2.put(id, m2);
		}
		extra = new byte[extraSize + 4]; //simulate first 4 bytes of image header
		in.read(extra, 4, extra.length - 4);
		for (int i = 0; i < imageMapCount; i++) {
			int id = in.readShort();
			ImageMap m = new ImageMap();
			m.read(in);
			map.put(id, m);
		}
	}

	public ImageMap getMap2AsMap1(ImageMapEx m2) {
		byte flags = extra[m2.offset];
		if ((flags & 3) != 0) {
			return null;
		}
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(extra, m2.offset + 1, extra.length);
			ImageMap m = new ImageMap();
			if ((flags & 4) != 0) {
				//m.read16Bit(new DataInputStream(in));
			} else {
				m.read(new DataInputStream(in));
			}
			in.close();
			return m;
		} catch (IOException ex) {
			Logger.getLogger(ImageTable.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	private void dumpMap1(ImageMap m, File dest, BufferedImage[] images) {
		BufferedImage srcImg = images[m.imageId];
		try {
			ImageIO.write(srcImg.getSubimage(m.atlasX, m.atlasY, m.width, m.height), "png", dest);
		} catch (IOException ex) {
			Logger.getLogger(ImageTable.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void dumpMap2(ImageMapEx m2, File dest, BufferedImage[] images) {
		try {
			byte flags = extra[m2.offset];
			ByteArrayInputStream in = new ByteArrayInputStream(extra, m2.offset + 1, extra.length);
			int stride = ((flags & 4) != 0) ? 2 : 1;
			DataInputStream dis = new DataInputStream(in);
			dis.skip(stride * 4);
			int count = dis.readShort();

			if (count == 0) {
				System.err.println("Count is 0 ..?");
				return;
			}

			int[][] params = new int[count][];

			for (int i = 0; i < count; i++) {
				params[i] = stride == 2 ? new int[]{dis.readShort(), dis.readShort(), dis.readShort()}
						: new int[]{dis.readByte(), dis.readByte(), dis.readByte()};
			}

			int minX = Integer.MAX_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxW = 0;
			int maxH = 0;

			int actcnt = 0;
			for (int[] p : params) {
				if (map.containsKey(p[2])) {
					ImageMap subm = map.get(p[2]);
					p[0] -= subm.originX;
					p[1] -= subm.originY;
					minX = Math.min(p[0], minX);
					minY = Math.min(p[1], minY);
					maxW = Math.max(subm.width + p[0], maxW);
					maxH = Math.max(subm.height + p[1], maxH);
					actcnt++;
				}
				else {
					System.out.println("Submap " + p[2] + " is compound, unsupported.");
				}
			}
			if (actcnt == 0) {
				System.out.println("Some images were external, skipping...");
				return;
			}

			BufferedImage outImg = new BufferedImage(maxW - minX, maxH - minY, BufferedImage.TYPE_INT_ARGB);
			Graphics g = outImg.getGraphics();
			g.translate(-minX, -minY);
			for (int[] p : params) {
				//System.out.println("p " + Arrays.toString(p));
				ImageMap subm = map.get(p[2]);
				if (subm == null) {
					continue;
				}
				g.drawImage(images[subm.imageId],
						p[0],
						p[1],
						p[0] + subm.width,
						p[1] + subm.height,
						subm.atlasX,
						subm.atlasY,
						subm.atlasX + subm.width,
						subm.atlasY + subm.height,
						null
				);
			}

			ImageIO.write(outImg, "png", dest);

			in.close();
		} catch (IOException ex) {
			Logger.getLogger(ImageTable.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void dump(File outDir, BufferedImage[] images) {
		System.out.println("Dumping table with base imageid " + baseImageId + " count " + imageCount);
		for (Map.Entry<Integer, ImageMap> e : map.entrySet()) {
			ImageMap m = e.getValue();

			File dest = new File(outDir, "map_" + e.getKey() + ".png");
			dumpMap1(m, dest, images);
		}
		for (Map.Entry<Integer, ImageMapEx> e : map2.entrySet()) {
			ImageMapEx m2 = e.getValue();

			int type = (extra[m2.offset] & 3);

			if (type == 0) {
				System.out.println("Dumping simple map " + e.getKey());
				ImageMap m = getMap2AsMap1(m2);

				if (m != null) {
					File dest = new File(outDir, "map_" + e.getKey() + ".png");
					dumpMap1(m, dest, images);
				}
			} else if (type == 1) {
				System.out.println("Dumping compound map " + e.getKey());
				File dest = new File(outDir, "map_" + e.getKey() + ".png");
				dumpMap2(m2, dest, images);
			} else if (type == 2) {
				try {
					ByteArrayInputStream in = new ByteArrayInputStream(extra, m2.offset + 1, extra.length);
					DataInputStream dis = new DataInputStream(in);
					int count = dis.readShort();

					for (int i = 0; i < count; i++) {
						int mapId = dis.readShort();
						File dest = new File(outDir, "map_" + e.getKey() + "_frame" + i + ".png");
						System.out.println("Dumping animated map " + mapId + " frame " + i);
						if (map.containsKey(mapId)) {
							dumpMap1(map.get(mapId), dest, images);
						} else if (map2.containsKey(mapId)) {
							dumpMap2(map2.get(mapId), dest, images);
						} else {
							System.err.println("Could not find submap");
						}
					}
				} catch (IOException ex) {
					Logger.getLogger(ImageTable.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}
}
