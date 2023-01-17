package bouncetools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 */
public class Debug {

	private static void m139a() {
		int stack = 360 * 57; //360 * 57
		int heap = 0;
		for (int index = 0; index < 360; index++) {
			int result = heap / 57; //heap to radians
			stack -= result; //subtract radians from stack
			heap += stack / 57; //set heap to heap + stack to degrees
			index++;
			System.out.println(result);
		}
	}
	
	private static void sctest() {
		int i2 = 0;
		int i3 = 360 * 57;
		int i4 = 0;
		int[] table = new int[360];
		BufferedImage i = new BufferedImage(360, 722, BufferedImage.TYPE_INT_RGB);
		while (i2 < 360) {
			int i5 = i4 / 57;
			i3 -= i5;
			i4 += i3 / 57;
			i.setRGB(i2, 722 - (i5 + 360), 0xFFFF0000);
			table[i2++] = (short) i5;
		}
		try {
			ImageIO.write(i, "png", new File("C:\\Users\\Čeněk\\eclipse-workspace\\BounceTalesD\\dump\\sincos_table.png"));
		} catch (IOException ex) {
			Logger.getLogger(Debug.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static int m87a(int[] target, int count, int add, byte[] src, int off, int bitFlagIndex) {
		int bitFlag = 1 << (bitFlagIndex - 1);
		int bitMask = (1 << bitFlagIndex) - 1;
		int index = 0;
		byte b = 0;
		int checkBits = 0;
		while (index < count) {
			int i11 = b + 8;
			b += 8;
			checkBits = (checkBits | (src[off] << b)) & ((1 << i11) - 1);
			while (b >= bitFlagIndex) {
				int maskedBits = checkBits & bitMask;
				int result = (maskedBits & bitFlag) > 0 ? maskedBits | ~bitMask : maskedBits;
				if (index < count) {
					target[index] = (result + add) << 16;
					index++;
				}
				checkBits >>>= bitFlagIndex;
				b -= bitFlagIndex;
			}
			off++;
		}
		return off;
	}

	public static int decomposeBytesToInts(final int[] target, final int count, final int base, final byte[] src, int srcPos, int bitsPerInt) {
		int bitBuffer = 0;
		int bufIdx = 0;
		int index = 0;
		int bit = 1 << (bitsPerInt - 1);
		int mask = (1 << bitsPerInt) - 1;
		while (index < count) {
			final int oldBitsWithNewByte = bitBuffer | (src[srcPos++] << bufIdx);
			bufIdx += 8;
			bitBuffer = (oldBitsWithNewByte & ((1 << bufIdx) - 1));
			while (bitsPerInt < bufIdx) {
				int result = (bitBuffer & mask);
				if ((result & bit) > 0) {
					result |= ~mask; //sign extend
				}
				if (index < count) {
					target[index++] = result + base << 16;
				}
				bufIdx -= bitsPerInt;
				bitBuffer >>>= bitsPerInt;
			}
		}
		return srcPos;
	}

	public static void main(String[] args) {
		try {
			m139a();
			sctest();
			if (true) {
				return;
			}
			BufferedImage img = ImageIO.read(new File("as.png"));
			for (int i = 0; i < img.getWidth(); i++) {
				for (int j = 0; j < img.getHeight(); j++) {
					img.setRGB(i, j, c(img.getRGB(i, j) << 8));
				}
			}
			ImageIO.write(img, "png", new File("out.png"));
		} catch (IOException ex) {
			Logger.getLogger(Debug.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static int decomposeBytesToShorts(final short[] target, final int count, final int base, int bitBuffer, final byte[] src, int srcPos, int bitsPerShort) {
		bitBuffer = 0;
		int bufIdx = 0;
		int index = 0;
		final int bit = 1 << bitsPerShort - 1;
		final int mask = (1 << bitsPerShort) - 1;
		while (index < count) {
			bitBuffer |= src[srcPos++] << bufIdx;
			bufIdx += 8;
			bitBuffer &= (1 << bufIdx) - 1;
			while (bitsPerShort < bufIdx) {
				int result = (bitBuffer & mask);
				if ((result & bit) > 0) {
					result |= ~mask; //sign extend
				}
				if (index < count) {
					target[index++] = (short) (result + base);
				}
				bufIdx -= bitsPerShort;
				bitBuffer >>>= bitsPerShort;
			}
		}
		return srcPos;
	}

	public static int mo66c(int color) {
		int b = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int r = color & 0xFF;
		return ((color >>> 24) << 24) + (((g + r) / 2) << 16) + (((r + b) / 2) << 8) + ((b + g) / 2);
	}

	public static int c(int n) {
		final int n2 = n >>> 24;
		final int n3 = n >> 16 & 0xFF;
		final int n4 = n >> 8 & 0xFF;
		n &= 0xFF;
		final int n5 = n3;
		final int n6 = n4;
		n = n;
		final int n7 = n6 + n >> 1;
		final int n8 = n5 + n >> 1;
		n = n5 + n6 >> 1;
		n += (n2 << 24) + (n7 << 16) + (n8 << 8);
		return n;
	}
}
