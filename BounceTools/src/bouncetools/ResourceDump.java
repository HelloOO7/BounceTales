package bouncetools;

import bouncetools.resmap.ResourceTable;
import bouncetales.BounceGame;
import bouncetales.LP32;
import bouncetales.ext.rsc.ResourceBatch;
import bouncetales.ext.rsc.ResourceInfo;
import bouncetales.ext.rsc.ResourceType;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import bouncetools.rlef.RLEF;
import xstandard.io.util.IndentedPrintStream;

public class ResourceDump {

	public static void main(String[] args) {
		String[] LEVEL_FILENAMES = new String[]{
			"bf",
			"bg",
			"bh",
			"bi",
			"br",
			"bj",
			"bk",
			"bl",
			"bm",
			"bs",
			"bn",
			"bo",
			"bp",
			"bq",
			"bt",
			"bu",
		};
		/*	dumpResourceTableToFile(new File("dump/resources.txt"));
		dumpImageTableToFile(new File("dump/imgtbl.txt"));*/
		//System.out.println((char)246);
		//dumpImageMaps(new File("dump/imagemaps"));
		for (int i = 0; i < LEVEL_FILENAMES.length; i++) {
			dumpCommonLevel(new File("dump/level" + (i + 1) + ".txt"), LEVEL_FILENAMES[i]);
		}
		//dumpCommonLevelImg(new File("dump/level2.png"), "bg");
		/*char c = 'f';
		while (c < 'v'){
			dumpCommonLevelImg(new File("dump/level" + ((c - 'f' + 1)) + ".png"), "b" + c);
			c++;
		}*/
		//dumpMessageData(new File("dump/messagedata.txt"), "lang.cs-CZ");
		//dumpScriptMessages(new File("dump/script_messagedata.txt"), "lang.cs-CZ");
	}

	public static void dumpScriptMessages(File target, String langPath) {
		try {
			PrintStream out = new PrintStream(target);

			String[] msg = getLangFile(langPath);

			for (int i = 0; i < BounceGame.SCRIPT_MESSAGE_IDS.length; i++) {
				out.println(i + ": " + msg[BounceGame.SCRIPT_MESSAGE_IDS[i]]);
			}

			out.close();
		} catch (IOException ex) {
			Logger.getLogger(ResourceDump.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void dumpImageMaps(File targetDir) {
		targetDir.mkdirs();
		ResourceTable tbl = new ResourceTable("/a");
		int batchId = 0;
		List<ImageTable> imageTables = new ArrayList<ImageTable>();
		BufferedImage[] allImages = new BufferedImage[46];
		for (ResourceBatch b : tbl.batches) {
			if (b.resType == ResourceType.IMAGE) {
				try {
					ResourceInfo mainRes = tbl.infos.get(b.mainResId);
					DataInputStream dis = new DataInputStream(ResourceDump.class.getResourceAsStream("/" + mainRes.resourcePath));
					dis.skip(mainRes.skipOffset);
					ImageTable imgtbl = new ImageTable(dis, batchId);
					imageTables.add(imgtbl);
					System.arraycopy(readImages(tbl, b), 0, allImages, imgtbl.baseImageId, b.subResIds.length);
				} catch (IOException ex) {
					Logger.getLogger(ResourceDump.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			batchId++;
		}
		for (ImageTable it : imageTables) {
			it.dump(targetDir, allImages);
		}
	}

	public static BufferedImage[] readImages(ResourceTable rt, ResourceBatch rb) {
		BufferedImage[] out = new BufferedImage[rb.subResIds.length];
		int outIdx = 0;
		for (int imgid : rb.subResIds) {
			ResourceInfo ri = rt.infos.get(imgid);
			try {
				InputStream strm = ResourceDump.class.getResourceAsStream("/" + ri.resourcePath);
				BufferedImage img = ImageIO.read(strm);
				strm.close();
				out[outIdx] = img;
			} catch (IOException ex) {
				Logger.getLogger(ResourceDump.class.getName()).log(Level.SEVERE, null, ex);
			}
			outIdx++;
		}
		return out;
	}

	public static String[] getLangFile(String langPath) throws IOException {
		DataInputStream in = new DataInputStream(ResourceDump.class.getResourceAsStream("/" + langPath));
		in.mark(in.available());

		int[] offsets = new int[91];
		String[] strings = new String[offsets.length];
		for (int i = 0; i < offsets.length; i++) {
			offsets[i] = in.readUnsignedShort();
		}

		for (int i = 0; i < offsets.length; i++) {
			in.reset();
			in.skipBytes(offsets[i]);
			String utf = in.readUTF();
			strings[i] = utf;
		}

		in.close();
		return strings;
	}

	public static void dumpMessageData(File target, String langPath) {
		try {
			PrintStream out = new PrintStream(target);

			int i = 0;
			for (String str : getLangFile(langPath)) {
				out.println(i + ": " + str);
				i++;
			}

			out.close();
		} catch (IOException ex) {
			Logger.getLogger(ResourceDump.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void dumpCommonLevelImg(File target, String levelPath) {
		RLEF rlef = new RLEF(ResourceDump.class.getResourceAsStream("/" + levelPath));

		int[][] bb = rlef.getBBox();
		BufferedImage img = new BufferedImage((int) LP32.LP32ToFP32(bb[1][0] - bb[0][0]), (int) LP32.LP32ToFP32(bb[1][1] - bb[0][1]), BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.translate(-(int) (LP32.LP32ToFP32(bb[0][0])), (int) (LP32.LP32ToFP32(bb[1][1])));
		rlef.dumpDraw(g);

		try {
			ImageIO.write(img, "png", target);
		} catch (IOException ex) {
			Logger.getLogger(ResourceDump.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void dumpCommonLevel(File target, String levelPath) {
		RLEF rlef = new RLEF(ResourceDump.class.getResourceAsStream("/" + levelPath));

		try {
			rlef.dump(new IndentedPrintStream(target));
		} catch (FileNotFoundException ex) {
			Logger.getLogger(ResourceDump.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void dumpImageTableToFile(File target) {
		ResourceTable tbl = new ResourceTable("/a");
		try {
			PrintStream out = new PrintStream(target);
			int btchId = 0;
			for (ResourceBatch b : tbl.batches) {
				if (b.resType == ResourceType.IMAGE) {
					ResourceInfo mainRes = tbl.infos.get(b.mainResId);
					DataInputStream dis = new DataInputStream(ResourceDump.class.getResourceAsStream("/" + mainRes.resourcePath));
					dis.skip(mainRes.skipOffset);
					byte numPngs = dis.readByte();
					out.print("BATCH ");
					out.print(btchId);
					out.print(" | ");
					out.print("numPngs=");
					out.print(numPngs);
					short s0x1 = dis.readShort();
					out.print(", s0x1=");
					out.print(s0x1);
					short count1 = dis.readShort();
					short count2 = dis.readShort();
					short count3 = dis.readShort();
					short[] f411a = new short[500];
					out.println();
					out.print(", f411a: ");
					for (int i = 0; i < count1; i++) {
						short readShort5 = dis.readShort();
						if (i != 0) {
							out.print(", ");
						}
						out.print(readShort5);
						f411a[(readShort5 - 326) << 1] = (short) btchId;
						f411a[((readShort5 - 326) << 1) + 1] = (short) (dis.readShort() + 4);
					}
					out.println();
					out.print(", arr2: ");
					byte[] arr2 = new byte[count2];
					for (int dataIdx = 0; dataIdx < count2; dataIdx++) {
						if (dataIdx != 0) {
							out.print(", ");
						}
						arr2[dataIdx] = dis.readByte();
						out.print(arr2[dataIdx]);
					}
					int baseOfs = 0;
					byte[] f403a = new byte[2282];
					out.println();
					out.print(", f403a: ");
					int lastBaseOfs = -1;
					for (int i = 0; i < (count3 * 8); i++) {
						if (i != 0) {
							out.print(", ");
						}
						if ((i & 7) == 0) {
							baseOfs = dis.readShort() * 7;
							if (baseOfs != lastBaseOfs) {
								out.println();
								lastBaseOfs = baseOfs;
								out.print("IMGRES: " + baseOfs + " | ");
							}
						} else {
							f403a[((i & 7) + baseOfs) - 1] = dis.readByte();
							out.print("[");
							out.print((i & 7) - 1);
							out.print("]");
							out.print("=");
							out.print(f403a[((i & 7) + baseOfs) - 1]);
						}
					}
					out.println();
					out.println();
					dis.close();
				}
				btchId++;
			}
			out.close();
		} catch (IOException ex) {
			Logger.getLogger(ResourceDump.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void dumpResourceTableToFile(File target) {
		try {
			ResourceTable tbl = new ResourceTable("/a");

			PrintStream out = new PrintStream(target);
			int index = 0;
			out.println("---Resources---");
			for (ResourceInfo i : tbl.infos) {
				out.print(index);
				out.print(": ");
				out.print(i.resourcePath);
				out.print(" (SkipIndex ");
				out.print(i.skipOffset);
				out.print(", ReadLen ");
				out.print(i.readLength);
				out.println(")");
				index++;
			}
			out.println();
			out.println("---Batches---");
			index = 0;
			for (ResourceBatch b : tbl.batches) {
				out.print(index);
				out.print(": ");
				out.print(" TYPE: ");
				out.print(b.resType);
				out.print(" / Main Resource: ");
				out.print(tbl.infos.get(b.mainResId).resourcePath);
				out.print("(");
				out.print(b.mainResId);
				out.print(")");
				out.print(", Sub Resources: ");
				for (int i = 0; i < b.subResIds.length; i++) {
					if (i != 0) {
						out.print(", ");
					}
					out.print(tbl.infos.get(b.subResIds[i]).resourcePath);
				}
				out.println();
				index++;
			}
			out.close();
		} catch (IOException ex) {
			Logger.getLogger(ResourceDump.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
