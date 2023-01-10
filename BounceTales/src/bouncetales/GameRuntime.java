package bouncetales;

import bouncetales.ext.rsc.ImageMapEx;
import bouncetales.ext.rsc.ImageMap;
import bouncetales.ext.rsc.ResidentResHeader;
import bouncetales.ext.rsc.ResourceBatch;
import bouncetales.ext.rsc.ResourceInfo;
import bouncetales.ext.rsc.ResourceType;
import com.nokia.mid.ui.DeviceControl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/* renamed from: o */
public final class GameRuntime extends GameCanvas implements Runnable, CommandListener {

	/*
	Constants
	 */
	public static final int SCREEN_ORIENTATION_UNKNOWN = 0;
	public static final int SCREEN_ORIENTATION_RPORTRAIT = 1;
	public static final int SCREEN_ORIENTATION_PORTRAIT = 2;
	public static final int SCREEN_ORIENTATION_RLANDSCAPE = 3;
	public static final int SCREEN_ORIENTATION_LANDSCAPE = 4;

	public static final int SOFTKEY_CENTER = 0;
	public static final int SOFTKEY_RIGHT = 1;
	public static final int SOFTKEY_LEFT = 2;

	private static final int KEYEVENT_FLAG_JMEKEYCODE = 8;
	private static final int KEYEVENT_FLAG_PRESS = 1;
	private static final int KEYEVENT_FLAG_RELEASE = 2;

	public static final int CONTROL_MODE_RAW = 1;
	public static final int CONTROL_MODE_GAME = 2;
	public static final int CONTROL_MODE_STRINPUT = 3;

	public static final int SYSTEM_EVENT_START = 1;
	public static final int SYSTEM_EVENT_RESIZE = 2;
	public static final int SYSTEM_EVENT_PAUSE = 3;

	private static boolean DEBUG_OVERLAY_ON = false;

	private static final int LOADING_WAIT_TIMEOUT = 500; //renamed from: d

	private static final Font[] FONTS = {
		Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_ITALIC, 0),
		Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, 0),
		Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, 0)
	}; //renamed from: a

	private static final char[] SPLITTABLE_CHARACTERS = {'\n', ' ', '-'}; //renamed from: a 

	private static final char[][] KEY_TO_CHAR_MAP = {
		new char[]{' ', '0'}, //0 key
		new char[]{'.', ',', '?', '!', '\'', '-', '(', ')', '@', '/', ':', '_', '1'}, //1/voicemail key
		new char[]{'a', 'b', 'c', 'ä', 'å', '2'}, //2/abc key
		new char[]{'d', 'e', 'f', '3'}, //3/def key
		new char[]{'g', 'h', 'i', '4'}, //4/ghi key
		new char[]{'j', 'k', 'l', '5'}, //5/jkl key
		new char[]{'m', 'n', 'o', 'ö', '6'}, //6/mno key
		new char[]{'p', 'q', 'r', 's', '7'}, //7/pqrs key
		new char[]{'t', 'u', 'v', '8'}, //8/tuv key
		new char[]{'w', 'x', 'y', 'z', '9'}, //9/wxyz key
		new char[0], //star key
		new char[0] //pound key
	}; //renamed from: a 

	/*
	Global state
	 */
	//ENGINE GLOBAL STATE
	public static MIDlet mMidLet; //renamed from: a 

	private static boolean midletIsPaused = false; //renamed from: d

	private static GameRuntime[] gameRuntimes; //renamed from: a 
	private static GameRuntime mInstance = null; //renamed from: a 

	private static final Object gameMutex = new Object(); //renamed from: b 

	private static boolean gameThreadStarted = false; //renamed from: a
	private static boolean reqSystemGamePause = false; //renamed from: e
	private static boolean gameIsLoading = false; //renamed from: f
	private static boolean reqClose = false; //renamed from: g

	//GAME UPDATE TIMING
	private static int maxUpdateDelta = 500; //renamed from: i
	private static int updatesPerDraw = 1; //renamed from: j

	public static int updateDelta; //renamed from: a 
	private static long currentTime;//renamed from: a
	private static long lastUpdateTimestamp; //renamed from: b 

	//RENDERING
	public static int currentWidth = 240; //renamed from: b
	public static int currentHeight = 320; //renamed from: c

	private static Graphics mGraphics = null; //renamed from: b 
	private static Graphics lastGraphics = null; //renamed from: a

	private static int paintMode = 0; //renamed from: k
	private static int textDrawMode; //renamed from: m
	private static int currentFont = -1; //renamed from: n
	private static int nextDrawTransform = -1; //renamed from: o
	private static int[] textColors = new int[2]; //renamed from: d

	private static boolean isGamePaintEnabled = true; //renamed from: b

	private static String[] reqSoftkeyTexts; //renamed from: a 
	private static String[] softkeyTexts; //renamed from: b 
	private static int[] softkeyUITypes; //renamed from: c 

	//RESOURCE MAP
	private static ResourceInfo[] resourceInfo; //renamed from: e 
	private static String[] resourcePaths; //renamed from: c
	private static ResourceBatch[] resourceBatchInfo; //renamed from: a 

	//RESOURCE LOADER
	private final static Object loadingMutex = new Object(); //renamed from: a 

	private static Vector resLoadQueue; //renamed from: a 
	private static Vector resUnloadQueue; //renamed from: b 

	private static boolean[] isResourceLoaded; //renamed from: a
	private static Object[] loadedResources; //renamed from: a

	private static short[][] residentResMap; //renamed from: b 

	private static int sceneLoadQueueSize; //renamed from: f
	private static int[] sceneLoaderQueue; //renamed from: b 

	//IMAGE RESOURCES
	private static Image[] imageResources; //renamed from: a 
	private static ImageMap[] imageMaps; //renamed from: a 
	private static ImageMapEx[] imageMaps2; //renamed from: a 
	private static short[] tempImageDrawParams = new short[6]; //renamed from: b 

	//RESIDENT STRINGS (unused)
	private static int residentStringFieldCount; //renamed from: p
	private static String[] residentStrings; //renamed from: d

	//SOUND
	private static Player mMusicPlayer = null; //renamed from: a 
	private static Player mMusicPlayerUnused = null; //renamed from: b 

	private static boolean music_IsEnabled = false; //renamed from: i
	private static int music_IdCurrent = -1; //renamed from: s
	private static int music_IdQueuedAfterSysUnpause = -1; //renamed from: g
	private static int music_IdBeforeSysPause = -1; //renamed from: h
	private static int music_MasterVolume = 60; //renamed from: r

	private static int[] systemEventQueue; //renamed from: a
	private static int systemEventQueueSize; //renamed from: e

	//INPUT / HID
	private static boolean disableHID; //renamed from: k

	private static int controlMode = 1; //renamed from: t

	//for control mode 1
	private static int buttonsDown = 0; //renamed from: u
	private static int buttonsHit = 0; //renamed from: v
	private static int buttonsHeld = 0; //renamed from: w
	private static int keyQueueSize; //renamed from: x
	private static int[] keyQueue; //renamed from: f

	//for control mode 3
	private static char curTypedChar; //renamed from: a	
	private static int typeSeqLastKey = 0; //renamed from: y
	private static long typeSeqLastTime = 0; //renamed from: c 
	private static int typeSeqKeyRepeatNo = 0; //renamed from: z
	private static boolean typingKeyIsHeld = false; //renamed from: l
	private static long typingKeyHoldStartTime = 0; //renamed from: d
	private static int typingKeyHeldId; //renamed from: A

	//BOUNCE
	public static BounceGame mBounceGame; //renamed from: a 

	//UNUSED FIELDS (may have been constants optimized out by compiler)
	private static char[][] unused_f425b = null; //renamed from: b 
	private static boolean unused_f429c = false; //renamed from: c 
	private static boolean unused_f446h = false; //renamed from: h
	private static boolean unused_f450j = false; //renamed from: j
	private static int unused_f453l = 0; //renamed from: l
	private static boolean unused_f456m = false; //renamed from: m
	private static int unused_f460q = 0; //renamed from: q

	public GameRuntime() {
		super(false);
	}

	public static void resetGlobalState() {
		typingKeyIsHeld = false;
		textDrawMode = 0;
		currentFont = -1;
		nextDrawTransform = -1;
		residentStringFieldCount = 0;
		resetHID();
		typeSeqLastKey = 0;
		typeSeqKeyRepeatNo = 0;
		gameThreadStarted = false;
		reqClose = false;
	}

	public static boolean getAppFlag(String name) {
		String property = GameRuntime.mMidLet.getAppProperty(name);
		return property != null && property.equals("On");
	}

	/* renamed from: a */
	public static void quit() {
		reqClose = true;
	}

	/* renamed from: e */
	public static int getCurrentWidth() {
		return currentWidth;
	}

	/* renamed from: a */
	public static int getUpdatesPerDraw() {
		return updatesPerDraw;
	}

	/* renamed from: b */
	public static void setUpdatesPerDraw(int updatesPerDraw) {
		GameRuntime.updatesPerDraw = updatesPerDraw;
	}

	/* renamed from: c */
	public static void setMaxUpdateDelta(int i) {
		maxUpdateDelta = i;
	}

	/* renamed from: a */
	public static Graphics getGraphicsObj() {
		return mGraphics;
	}

	/* renamed from: a */
	public static void setGraphics(Graphics graphics) {
		if (graphics == null) {
			graphics = lastGraphics;
		}
		mGraphics = graphics;
	}

	/* renamed from: a */
	public static boolean setChildClip(int x, int y, int width, int height, int[] oldClipDest) {
		int newWidth;
		int newHeight;
		int clipX = mGraphics.getClipX();
		int clipY = mGraphics.getClipY();
		int clipWidth = mGraphics.getClipWidth();
		int clipHeight = mGraphics.getClipHeight();
		oldClipDest[0] = clipX;
		oldClipDest[1] = clipY;
		oldClipDest[2] = clipWidth;
		oldClipDest[3] = clipHeight;
		if (x < clipX) {
			newWidth = width - (clipX - x);
			x = clipX;
		} else {
			newWidth = width;
		}
		if (y < clipY) {
			newHeight = height - (clipY - y);
			y = clipY;
		} else {
			newHeight = height;
		}
		if (x + newWidth > clipX + clipWidth) {
			newWidth = (clipX + clipWidth) - x;
		}
		if (y + newHeight > clipY + clipHeight) {
			newHeight = (clipY + clipHeight) - y;
		}
		if (newWidth <= 0 || newHeight <= 0) {
			return false;
		}
		mGraphics.setClip(x, y, newWidth, newHeight);
		return true;
	}

	/* renamed from: f */
	public static int getCurrentFont() {
		return currentFont;
	}

	/* renamed from: a */
	public static int getFontHeight(int fontId) {
		char fontIndex;
		if (fontId == -1) {
			fontIndex = 0;
		} else if (fontId == -2) {
			fontIndex = 1;
		} else if (fontId != -3) {
			return -1;
		} else {
			fontIndex = 2;
		}
		return FONTS[fontIndex].getHeight();
	}

	/* renamed from: a */
	public static void setTextStyle(int font, int textDrawMode) {
		currentFont = font;
		GameRuntime.textDrawMode = textDrawMode;
		textColors[0] = 0;
	}

	/* renamed from: b */
	public static void setTextColor(int colorType, int colorValue) {
		textColors[colorType] = colorValue;
	}

	/* renamed from: a */
	public static int getStrRenderWidth(int fontId, String str, int off, int len) {
		char c;
		if (fontId == -1) {
			c = 0;
		} else if (fontId == -2) {
			c = 1;
		} else if (fontId != -3) {
			return -1;
		} else {
			c = 2;
		}
		return FONTS[c].substringWidth(str, off, len);
	}

	/* renamed from: a */
	public static String[] prepareStringLines(String str, int maxWidth, int fontId, boolean b, int n3, int n4, boolean dynamicWidth) {
		String[] array = new String[5];
		int lineCount = 0;
		int n6 = 0;
		int max = n3;
		int n7 = 0;
		final int length = str.length();
		int strPos = 0;
		while (strPos < length) {
			final boolean b3 = b && getFontHeight(fontId) * lineCount < n4;
			int n8;
			if (b3) {
				n8 = maxWidth - n3;
				++n6;
			} else {
				n8 = maxWidth;
			}
			int n9 = 0;
			final int n10 = strPos;
			int n11 = length;
			while (strPos < length) {
				int n12 = length;
				int j = strPos;
				while (j < length) {
					int n13;
					for (n13 = 0; n13 < SPLITTABLE_CHARACTERS.length && str.charAt(j) != SPLITTABLE_CHARACTERS[n13]; ++n13) {
					}
					if (n13 < SPLITTABLE_CHARACTERS.length) {
						n12 = j;
						if (str.charAt(n12) == '-') {
							++n12;
							break;
						}
						break;
					} else {
						++j;
					}
				}
				if (getStrRenderWidth(fontId, str, n10, n12 - n10) < n8) {
					n11 = n12;
					strPos = n12;
					++n9;
					if (n11 < length && str.charAt(n11) == '\n') {
						break;
					}
					while (strPos < length) {
						int n14;
						for (n14 = 0; n14 < SPLITTABLE_CHARACTERS.length && str.charAt(strPos) != SPLITTABLE_CHARACTERS[n14]; ++n14) {
						}
						if (n14 >= SPLITTABLE_CHARACTERS.length || str.charAt(strPos) == '-' || str.charAt(strPos) == '\n') {
							break;
						}
						++strPos;
					}
				} else {
					if (n9 == 0) {
						int n15;
						for (n15 = n10; getStrRenderWidth(fontId, str, n10, n15 - n10 + 1) < n8; ++n15) {
						}
						n11 = n15;
						strPos = n15;
						break;
					}
					break;
				}
			}
			final int a = getStrRenderWidth(fontId, str, n10, n11 - n10);
			if (b3) {
				max = Math.max(max, a + n3);
				n7 = Math.max(n7, a + n3);
			} else {
				n7 = Math.max(n7, a);
			}
			final String[] array2 = array;
			final int n16 = lineCount + 1;
			final String substring = str.substring(n10, n11);
			final int n17 = n16;
			String[] array3 = array2;
			if (array2.length <= n17) {
				final String[] array4 = new String[array3.length + 5];
				System.arraycopy(array3, 0, array4, 0, array3.length);
				array3 = array4;
			}
			array3[n17] = substring;
			array = array3;
			++lineCount;
			if (strPos < length && str.charAt(strPos) == '\n') {
				++strPos;
			}
			try {
				Thread.sleep(1L);
			} catch (InterruptedException ex) {
			}
		}
		int blockWidth;
		if (dynamicWidth) {
			blockWidth = n7;
		} else {
			blockWidth = maxWidth;
		}
		int blockHeight;
		if (b) {
			blockHeight = Math.max(lineCount * getFontHeight(fontId), n4);
		} else {
			blockHeight = lineCount * getFontHeight(fontId) + n4;
		}
		array[0] = lineCount + " " + n6 + " " + max + " " + blockWidth + " " + blockHeight;
		return array;
	}

	/* renamed from: a */
	public static void drawText(String str, int offset, int length, int xpos, int ypos, int flags) {
		char fontIndex;
		int anchor;
		switch (currentFont) {
			case -1:
				fontIndex = 0;
				break;
			case -2:
				fontIndex = 1;
				break;
			case -3:
				fontIndex = 2;
				break;
			default:
				return;
		}
		if ((flags & 2) == 2) {
			anchor = (flags & -3) | 16;
			ypos -= getFontHeight(currentFont) / 2;
		} else {
			anchor = flags;
		}
		mGraphics.setFont(FONTS[fontIndex]);
		if (textDrawMode != 1) {
			mGraphics.setColor(textColors[1]);
			switch (textDrawMode) {
				case 2:
					mGraphics.drawSubstring(str, 0, length, xpos + 1, ypos + 1, anchor);
					break;
				case 3:
					int i7 = 0;
					while (i7 < 4) {
						int i8 = i7 + 1;
						mGraphics.drawSubstring(str, 0, length, xpos + ((i7 & 1) * (i7 - 2)), ypos + ((i8 & 1) * (i8 - 2)), anchor);
						i7 = i8;
					}
					break;
			}
		}
		mGraphics.setColor(textColors[0]);
		mGraphics.drawSubstring(str, 0, length, xpos, ypos, anchor);
	}

	/* renamed from: d */
	private static int getImgResIdx(int imgMapIdx) {
		int baseOffset;
		int mapType;
		byte[] bArr;
		boolean is16bit;
		short imageId;
		if (imgMapIdx == -1 || imgMapIdx < -1) {
			return -1;
		}
		if (imgMapIdx >= imageMaps.length) {
			int map2Index = imgMapIdx - imageMaps.length;
			if (map2Index >= imageMaps2.length) {
				return -1;
			}
			short s2 = imageMaps2[map2Index].offset;
			short s3 = imageMaps2[map2Index].resBatchId;
			if (s3 < 0) {
				return -1;
			}
			byte[] bArr2 = (byte[]) loadedResources[s3];
			if (s2 == -1 || bArr2 == null) {
				return -1;
			}
			baseOffset = s2 + 1;
			byte b = bArr2[s2];
			mapType = b & 3;
			bArr = bArr2;
			is16bit = (b & 4) != 0;
		} else if (imageMaps[imgMapIdx].imageId == -1) {
			return -1;
		} else {
			bArr = null;
			baseOffset = 0;
			is16bit = false;
			mapType = -99;
		}
		if (mapType != 0 && mapType != -99) {
			imageId = -1;
		} else if (mapType == 0) {
			int i5 = is16bit ? baseOffset + 12 : baseOffset + 6;
			imageId = getShortFromByteArray(bArr, i5);
		} else {
			imageId = imageMaps[imgMapIdx].imageId;
		}
		return imageId;
	}

	/* renamed from: a */
	public static Image getImageResource(int i) {
		int d = getImgResIdx(i);
		if (d >= 0) {
			return imageResources[d];
		}
		return null;
	}

	/* renamed from: a */
	public static int getImageMapParam(int mapId, int paramId) {
		if (mapId < imageMaps.length) {
			return imageMaps[mapId].getParam(paramId);
		}
		ImageMapEx m2 = imageMaps2[mapId - imageMaps.length];
		short offset = m2.offset;
		byte[] b = (byte[]) loadedResources[m2.resBatchId];
		int dataOffs = offset + 1;
		if ((b[offset] & 4) != 0) { //16bit data
			int offset16 = (paramId << 1) + dataOffs;
			return getShortFromByteArray(b, offset16);
		} else {
			return b[dataOffs + paramId];
		}
	}

	/* renamed from: a */
	private static short getShortFromByteArray(byte[] arr, int offset) {
		return (short) ((arr[offset] << 8) | (arr[offset + 1] & 255));
	}

	private static int getIntFromByteArray(byte[] arr, int offset) {
		return (int) ((arr[offset] & 255) << 24) | ((arr[offset + 1] & 255) << 16) | ((arr[offset + 2] & 255) << 8) | (arr[offset + 3] & 255);
	}

	/* renamed from: b */
	public static void drawAnimatedImageRes(int xpos, int ypos, int imageId, int anmFrame) {
		drawImageRes(xpos, ypos, getImageIdAfterAnimation(imageId, anmFrame));
	}

	/* renamed from: c */
	public static int getImageIdAfterAnimation(int imageId, int i2) {
		//for images of type 2
		ImageMapEx m2 = imageMaps2[imageId - imageMaps.length];
		short s = m2.offset;
		byte[] bArr = (byte[]) loadedResources[m2.resBatchId];
		int i3 = s + 3 + (i2 * 2);
		return getShortFromByteArray(bArr, i3);
	}

	/* renamed from: b */
	public static int getImageAnimParamEx(int imgMapId, int paramIndex) {
		ImageMapEx m2 = imageMaps2[imgMapId - imageMaps.length];
		short offset = m2.offset;
		byte[] bArr = (byte[]) loadedResources[m2.resBatchId];
		int baseOffset = offset + 1;
		byte flags = bArr[offset];
		int mapType = flags & 3;
		int paramStride = (flags & 4) != 0 ? 2 : 1;
		if (mapType != 1) {
			return 0;
		}
		int i6 = baseOffset + (paramStride << 2);
		int dataOffs = i6 + 2 + (getShortFromByteArray(bArr, i6) * paramStride * 3) + ((paramStride << 1) * paramIndex);
		if (paramStride == 1) {
			return (bArr[dataOffs + 1] & 0xFFFF) | ((bArr[dataOffs] << 16) & 0xFFFF0000);
		} else {
			return getIntFromByteArray(bArr, dataOffs);
		}
	}

	/* renamed from: b */
	public static int getImageAnimationFrameCount(int imageId) {
		ImageMapEx m2 = imageMaps2[imageId - imageMaps.length];
		return getShortFromByteArray(
				(byte[]) loadedResources[m2.resBatchId],
				m2.offset + 1
		);
	}

	/* renamed from: a */
	public static void drawImageRes(int xpos, int ypos, int mapId) {
		if (mapId < 0) {
			ypos -= 20;
			mGraphics.setColor(0xFFFFFF);
			mGraphics.fillRect(xpos, ypos, 20, 20);
			mGraphics.setColor(0xFF00FF);
			mGraphics.fillRect(xpos, ypos, 10, 10);
			mGraphics.fillRect(xpos + 10, ypos + 10, 10, 10);
			return; //invalid resource
		}
		int i4;
		int mapType;
		int dataType;
		byte[] bArr;
		Image image;
		int i7;
		int resIndex = 0;
		if (mapId < imageMaps.length) {
			mapType = -99;
			bArr = null;
			i4 = 0;
			dataType = 0;
		} else {
			ImageMapEx m2 = imageMaps2[mapId - imageMaps.length];
			short s = m2.offset;
			byte[] bArr2 = (byte[]) loadedResources[m2.resBatchId];
			i4 = s + 1;
			byte b = bArr2[s];
			mapType = b & 3;
			dataType = (b & 4) != 0 ? 2 : 1;
			bArr = bArr2;
		}
		if (mapType == 0 || mapType == -99) {
			if (mapType == 0) {
				if (dataType == 2) {
					i7 = i4;
					for (int i9 = 0; i9 < 6; i9++) {
						tempImageDrawParams[i9] = (short) ((bArr[i7] << 8) | (bArr[i7 + 1] & 255));
						i7 += 2;
					}
				} else {
					i7 = i4;
					for (int i10 = 0; i10 < 6; i10++) {
						tempImageDrawParams[i10] = (short) bArr[i7];
						i7++;
					}
				}
				image = imageResources[(short) ((bArr[i7 + 1] & 255) | (bArr[i7] << 8))];
			} else {
				tempImageDrawParams[0] = imageMaps[mapId].width;
				tempImageDrawParams[1] = imageMaps[mapId].height;
				tempImageDrawParams[2] = imageMaps[mapId].originX;
				tempImageDrawParams[3] = imageMaps[mapId].originY;
				tempImageDrawParams[4] = imageMaps[mapId].atlasX;
				tempImageDrawParams[5] = imageMaps[mapId].atlasY;
				image = imageResources[imageMaps[mapId].imageId];
			}
			int drawX = xpos - tempImageDrawParams[2];
			int drawY = ypos - tempImageDrawParams[3];
			short drawW = tempImageDrawParams[0];
			short drawH = tempImageDrawParams[1];
			if (drawW > 0 && drawH > 0) {
				if (nextDrawTransform != -1) {
					mGraphics.drawRegion(image, tempImageDrawParams[4], tempImageDrawParams[5], drawW, drawH, nextDrawTransform, drawX, drawY, Graphics.TOP | Graphics.LEFT);
					nextDrawTransform = -1;
				} else {
					mGraphics.drawRegion(image, tempImageDrawParams[4], tempImageDrawParams[5], drawW, drawH, Sprite.TRANS_NONE, drawX, drawY, Graphics.TOP | Graphics.LEFT);
				}
			}
		} else if (mapType == 1) {
			int i14 = (dataType << 2) + i4;
			short resCount = getShortFromByteArray(bArr, i14);
			int streamPos = i14 + 2;
			if (dataType == 2) {
				while (resIndex < resCount) {
					drawImageRes(
							getShortFromByteArray(bArr, streamPos) + xpos,
							getShortFromByteArray(bArr, streamPos + 2) + ypos,
							getShortFromByteArray(bArr, streamPos + 4)
					);
					streamPos += 6;
					resIndex++;
				}
			} else {
				while (resIndex < resCount) {
					drawImageRes(xpos + bArr[streamPos], ypos + bArr[streamPos + 1], bArr[streamPos + 2]);
					streamPos += 3;
					resIndex++;
				}
			}
		}
	}

	/* renamed from: a */
	public static void drawImageResAnchored(int x, int y, int mapId, int anchor) {
		int adjustX = x + getImageMapParam(mapId, ImageMap.PARAM_ORIGIN_X);
		int adjustY = getImageMapParam(mapId, ImageMap.PARAM_ORIGIN_Y) + y;
		if ((anchor & Graphics.HCENTER) != 0) {
			adjustX -= getImageMapParam(mapId, ImageMap.PARAM_WIDTH) / 2;
		}
		if ((anchor & Graphics.RIGHT) != 0) {
			adjustX -= getImageMapParam(mapId, ImageMap.PARAM_WIDTH);
		}
		if ((anchor & Graphics.VCENTER) != 0) {
			adjustY -= getImageMapParam(mapId, ImageMap.PARAM_HEIGHT) / 2;
		}
		if ((anchor & Graphics.BOTTOM) != 0) {
			adjustY -= getImageMapParam(mapId, ImageMap.PARAM_HEIGHT);
		}
		drawImageRes(adjustX, adjustY, mapId);
	}

	/* renamed from: a */
	public static void drawImageResTransformed(int i, int i2, int i3, int anchor, int transform) {
		nextDrawTransform = transform;
		drawImageResAnchored(i, i2, 12, 24);
	}

	/* renamed from: a */
	public static void replaceImageResource(int imageId, Image image) {
		int d = getImgResIdx(imageId);
		if (d >= 0) {
			imageResources[d] = image;
		}
	}

	public static byte[] loadFromRecordStore(String tag) {
		RecordStore openRecordStore = null;
		try {
			if ((openRecordStore = RecordStore.openRecordStore(tag, false)) == null || openRecordStore.getNumRecords() < 1) {
				return null;
			}
			final byte[] record;
			if ((record = openRecordStore.getRecord(1)) == null || record.length < 2 || record[0] != 1 || record[1] != 2) {
				return null;
			}
			final byte[] o = new byte[record.length - 2];
			System.arraycopy(record, 2, o, 0, o.length);
			return o;
		} catch (RecordStoreException ex) {
			return null;
		} catch (Exception ex2) {
			return null;
		} finally {
			try {
				if (openRecordStore != null) {
					openRecordStore.closeRecordStore();
				}
			} catch (Exception ex3) {
			}
		}
	}

	/* renamed from: a */
	public static void saveToRecordStore(String tag, byte[] rawData) {
		RecordStore recordStore = null;
		try {
			recordStore = RecordStore.openRecordStore(tag, true);
			if (recordStore.getNumRecords() < 1) {
				recordStore.addRecord((byte[]) null, 0, 0);
			}
			byte[] taggedData = new byte[(rawData.length + 2)];
			taggedData[0] = 1;
			taggedData[1] = 2;
			System.arraycopy(rawData, 0, taggedData, 2, rawData.length);
			recordStore.setRecord(1, taggedData, 0, taggedData.length);
			if (recordStore != null) {
				try {
					recordStore.closeRecordStore();
				} catch (Exception e) {
				}
			}
		} catch (RecordStoreException e2) {
			try {
				recordStore.closeRecordStore();
			} catch (Exception e3) {
				
			}
		} catch (Exception e4) {
			try {
				recordStore.closeRecordStore();
			} catch (Exception e5) {
			}
		} catch (Throwable th) {
			try {
				recordStore.closeRecordStore();
			} catch (Exception e6) {
			}
		}
	}

	/* renamed from: a */
	public static void startLoadScene(int sceneId) {
		if (sceneLoadQueueSize == 40) {
			throw new RuntimeException();
		}
		sceneLoaderQueue[sceneLoadQueueSize] = sceneId;
		sceneLoadQueueSize++;
	}

	/* renamed from: a */
	public static boolean isScreenLandscape() {
		return getScreenOrientationFromSoftkeys() == SCREEN_ORIENTATION_RLANDSCAPE || getScreenOrientationFromSoftkeys() == SCREEN_ORIENTATION_LANDSCAPE;
	}

	/* renamed from: a */
	public static void setSoftkey(int softkey, String text, int type) {
		reqSoftkeyTexts[softkey] = text;
		softkeyUITypes[softkey] = type;
	}

	/* renamed from: b */
	public static void resetSoftkeys() {
		for (int i = 0; i < 3; i++) {
			setSoftkey(i, (String) null, -1);
		}
	}

	private float debugFps = 0f;

	private void paintDebugOverlay(Graphics g) {
		if (debugFps != 0f) {
			GameRuntime.setTextStyle(-3, 1);
			g.setColor(0xFF0000);
			String fpsStr = String.valueOf(debugFps);
			int len = Math.min(fpsStr.length(), 4);
			fpsStr = "FPS: " + fpsStr.substring(0, len) + "0000".substring(len);
			g.drawString(fpsStr, currentWidth >> 1, 2, Graphics.HCENTER | Graphics.TOP);
		}
	}

	/* renamed from: b */
	private void gamePaint(Graphics graphics) {
		if (paintMode != 0 && graphics != null) {
			try {
				lastGraphics = graphics;
				mGraphics = graphics;
				graphics.setClip(0, 0, currentWidth, currentHeight);
				if (mBounceGame == null) {
					//idle blank screen
					graphics.setColor(0);
					graphics.fillRect(0, 0, currentWidth, currentHeight);
				} else if (isGamePaintEnabled) {
					//game
					for (int paintResult = mBounceGame.paint(0, paintMode); paintResult != 0; paintResult = mBounceGame.paint(paintResult, paintMode)) {
					}
				} else {
					return;
				}

				//draw softkey bar
				graphics.setClip(0, 0, currentWidth, currentHeight);
				for (int i = 0; i < 3; i++) {
					String str = softkeyTexts[i];
					if (str != null) {
						int anchor = getSoftkeyScreenAnchor(i);
						int i2 = (anchor & Graphics.LEFT) != 0 ? 2 : 0;
						if ((anchor & Graphics.RIGHT) != 0) {
							i2 = currentWidth - 2;
						}
						int xpos = (anchor & Graphics.HCENTER) != 0 ? currentWidth >> 1 : i2;
						int ypos = (anchor & Graphics.TOP) != 0 ? 2 : 0;
						if ((anchor & Graphics.BOTTOM) != 0) {
							ypos = currentHeight - 2;
						}
						if ((anchor & Graphics.VCENTER) != 0) {
							ypos = currentHeight >> 1;
						}
						BounceGame.drawSoftkeyUI(str, softkeyUITypes[i], xpos, ypos, anchor);
					}
				}

				if (DEBUG_OVERLAY_ON) {
					paintDebugOverlay(graphics);
				}
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	/* renamed from: j */
	private void callGamePaint(int mode) {
		paintMode = mode;
		gamePaint(mInstance.getGraphics());
		mInstance.flushGraphics();
		paintMode = 0;
	}

	/* renamed from: b */
	public static void setBacklight(boolean isOn) {
		DeviceControl.setLights(0, isOn ? 100 : 0);
	}

	/* renamed from: b */
	public static boolean isScreenPortrait() {
		return getScreenOrientationFromSoftkeys() == SCREEN_ORIENTATION_RPORTRAIT || getScreenOrientationFromSoftkeys() == SCREEN_ORIENTATION_PORTRAIT;
	}

	/* renamed from: d */
	public static int getSoftkeyBarWidth() {
		return getScreenOrientationFromSoftkeys() != SCREEN_ORIENTATION_UNKNOWN ? -1 : 0;
	}

	/* renamed from: c */
	public static int getSoftkeyBarHeight() {
		if (getScreenOrientationFromSoftkeys() != SCREEN_ORIENTATION_UNKNOWN) {
			return BounceGame.getSoftkeyBarSize();
		}
		return 0;
	}

	/* renamed from: c */
	private static int getSoftkeyScreenAnchor(int softkey) {
		//portrait mode
		switch (softkey) {
			case SOFTKEY_CENTER:
				return Graphics.BOTTOM | Graphics.HCENTER;
			case SOFTKEY_RIGHT:
				return Graphics.BOTTOM | Graphics.RIGHT;
			case SOFTKEY_LEFT:
				return Graphics.BOTTOM | Graphics.LEFT;
			default:
				return -1;
		}
		//landscape mode
		/*switch (i) {
			case SOFTKEY_CENTER:
				return Graphics.LEFT | Graphics.VCENTER;
			case SOFTKEY_RIGHT:
				return Graphics.LEFT | Graphics.BOTTOM;
			case SOFTKEY_LEFT:
				return Graphics.LEFT | Graphics.TOP;
			default:
				return -1;
		}*/
	}

	/* renamed from: b */
	public static int getScreenOrientationFromSoftkeys() {
		final int VERTICAL_ANCHOR_MASK = Graphics.BASELINE | Graphics.TOP | Graphics.BOTTOM | Graphics.VCENTER;
		final int HORIZONTAL_ANCHOR_MASK = Graphics.LEFT | Graphics.RIGHT | Graphics.HCENTER;

		if ((getSoftkeyScreenAnchor(SOFTKEY_CENTER) & VERTICAL_ANCHOR_MASK) == (getSoftkeyScreenAnchor(SOFTKEY_RIGHT) & VERTICAL_ANCHOR_MASK)) {
			return (getSoftkeyScreenAnchor(SOFTKEY_CENTER) & Graphics.BOTTOM) != 0 ? SCREEN_ORIENTATION_PORTRAIT : SCREEN_ORIENTATION_RPORTRAIT;
		}
		if ((getSoftkeyScreenAnchor(SOFTKEY_CENTER) & HORIZONTAL_ANCHOR_MASK) == (getSoftkeyScreenAnchor(SOFTKEY_RIGHT) & HORIZONTAL_ANCHOR_MASK)) {
			return (getSoftkeyScreenAnchor(SOFTKEY_CENTER) & Graphics.LEFT) != 0 ? SCREEN_ORIENTATION_RLANDSCAPE : SCREEN_ORIENTATION_LANDSCAPE;
		}
		return SCREEN_ORIENTATION_UNKNOWN;
	}

	/* renamed from: c */
	public static void playMusic(int id, int loopCount) {
		synchronized (gameMutex) {
			if (reqSystemGamePause) {
				if (loopCount == -1) {
					music_IdQueuedAfterSysUnpause = id;
				} else {
					music_IdQueuedAfterSysUnpause = -1;
				}
				return;
			}
			music_IdQueuedAfterSysUnpause = -1;
			byte[] bytes = (byte[]) getLoadedResData(id);
			if (bytes != null) {
				stopMusic();
				if (music_IsEnabled) {
					try {
						if (loopCount == -1) {
							try {
								music_IdCurrent = id;
								loopCount = -1;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						Player player = Manager.createPlayer(new ByteArrayInputStream(bytes), "audio/midi");
						mMusicPlayer = player;
						player.prefetch();
						VolumeControl control = (VolumeControl) mMusicPlayer.getControl("VolumeControl");
						if (control != null) {
							control.setLevel(music_MasterVolume);
						}
						mMusicPlayer.setLoopCount(loopCount);
						mMusicPlayer.start();
					} catch (IOException ex) {
						ex.printStackTrace();
					} catch (MediaException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	/* renamed from: d */
	public static void stopMusic() {
		synchronized (gameMutex) {
			music_IdQueuedAfterSysUnpause = -1;
			if (music_IsEnabled && mMusicPlayer != null) {
				try {
					if (mMusicPlayer.getState() != Player.CLOSED) {
						mMusicPlayer.stop();
						mMusicPlayer.deallocate();
						mMusicPlayer.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				music_IdCurrent = -1;
				mMusicPlayer = null;
			}
		}
	}

	/* renamed from: c */
	public static boolean isMusicEnabled() {
		return music_IsEnabled;
	}

	/* renamed from: a */
	public static void setMusicEnabled(boolean enabled) {
		music_IsEnabled = enabled;
	}

	/* renamed from: d */
	public static void initHID(int controlMode) {
		buttonsDown = 0;
		GameRuntime.controlMode = controlMode;
	}

	/* renamed from: c */
	public static void resetHID() {
		buttonsDown = 0;
		buttonsHeld = 0;
		buttonsHit = 0;
		keyQueueSize = 0;
		disableHID = true;
	}

	/* renamed from: e */
	private static int convertKeyCode(int jmeKeyCode) {
		final int SOFT_KEY_MIDDLE_NOKIA = -5;
		final int SOFT_KEY_LEFT_NOKIA = -6;
		final int SOFT_KEY_RIGHT_NOKIA = -7;

		if (jmeKeyCode == SOFT_KEY_RIGHT_NOKIA) {
			return KeyCode.SOFTKEY_RIGHT;
		}
		if (jmeKeyCode == SOFT_KEY_LEFT_NOKIA) {
			return KeyCode.SOFTKEY_LEFT;
		}
		if (jmeKeyCode == SOFT_KEY_MIDDLE_NOKIA) {
			return KeyCode.SOFTKEY_MIDDLE;
		}
		switch (jmeKeyCode) {
			case Canvas.KEY_POUND:
				return KeyCode.POUND;
			case 36:
			case 37:
			case 38:
			case 39:
			case 40:
			case 41:
			case 43:
			case 44:
			case 45:
			case 46:
			case 47:
			default: {
				try {
					switch (mInstance.getGameAction(jmeKeyCode)) {
						case Canvas.UP:
							return KeyCode.UP;
						case Canvas.LEFT:
							return KeyCode.LEFT;
						case 3:
						case 4:
						default:
							return -1;
						case Canvas.RIGHT:
							return KeyCode.RIGHT;
						case Canvas.DOWN:
							return KeyCode.DOWN;
					}
				} catch (IllegalArgumentException e) {
					return -1;
				}
			}
			case Canvas.KEY_STAR:
				return KeyCode.STAR;
			case Canvas.KEY_NUM0:
				return KeyCode.NUM0;
			case Canvas.KEY_NUM1:
				return KeyCode.NUM1;
			case Canvas.KEY_NUM2:
				return KeyCode.NUM2;
			case Canvas.KEY_NUM3:
				return KeyCode.NUM3;
			case Canvas.KEY_NUM4:
				return KeyCode.NUM4;
			case Canvas.KEY_NUM5:
				return KeyCode.NUM5;
			case Canvas.KEY_NUM6:
				return KeyCode.NUM6;
			case Canvas.KEY_NUM7:
				return KeyCode.NUM7;
			case Canvas.KEY_NUM8:
				return KeyCode.NUM8;
			case Canvas.KEY_NUM9:
				return KeyCode.NUM9;
		}
	}


	/* renamed from: d */
	private static void onKeyEvent(int keyCode, int flags) {
		int keyId;
		if (!disableHID && isGamePaintEnabled) {
			if ((flags & KEYEVENT_FLAG_JMEKEYCODE) == KEYEVENT_FLAG_JMEKEYCODE) {
				try {
					keyId = convertKeyCode(keyCode);
				} catch (Throwable th) {
					return;
				}
			} else {
				keyId = keyCode;
			}
			if (controlMode == CONTROL_MODE_GAME) { //convert numbers to directional keys
				switch (keyId) {
					case KeyCode.NUM5:
						keyId = KeyCode.SOFTKEY_MIDDLE;
						break;
					case KeyCode.NUM2:
						keyId = KeyCode.UP;
						break;
					case KeyCode.NUM8:
						keyId = KeyCode.DOWN;
						break;
					case KeyCode.NUM4:
						keyId = KeyCode.LEFT;
						break;
					case KeyCode.NUM6:
						keyId = KeyCode.RIGHT;
						break;
				}
			}
			if (controlMode != CONTROL_MODE_STRINPUT || ((keyId < KeyCode.NUM0 || keyId > KeyCode.POUND) && keyId != KeyCode.POUND)) {
				if ((flags & KEYEVENT_FLAG_PRESS) == KEYEVENT_FLAG_PRESS) {
					if (keyQueueSize < 20) {
						keyQueue[keyQueueSize++] = keyId;
					}
					if (keyId != -1) {
						buttonsDown |= 1 << keyId;
						buttonsHit |= (1 << keyId);
					}
				} else if ((flags & KEYEVENT_FLAG_RELEASE) == KEYEVENT_FLAG_RELEASE && keyId != -1) {
					buttonsDown = (~(1 << keyId)) & buttonsDown;
				}
			} else {
				if ((flags & KEYEVENT_FLAG_PRESS) != 0) {
					if (keyId != KeyCode.POUND) {
						long currentTimeMillis = System.currentTimeMillis();
						if (currentTimeMillis - typeSeqLastTime >= 700 || keyId != typeSeqLastKey) {
							typeSeqKeyRepeatNo = 0;
						} else {
							typeSeqKeyRepeatNo++;
						}
						int keyNumber = keyId - KeyCode.NUM0;
						if (KEY_TO_CHAR_MAP[keyNumber].length != 0 && keyNumber >= 0) {
							typeSeqLastTime = currentTimeMillis;
							typeSeqLastKey = keyId;
							char c = KEY_TO_CHAR_MAP[keyNumber][typeSeqKeyRepeatNo % KEY_TO_CHAR_MAP[keyNumber].length];
							if ((keyId != typingKeyHeldId || c != curTypedChar) && !typingKeyIsHeld) {
								typingKeyHeldId = keyId;
								curTypedChar = c;
								typingKeyHoldStartTime = System.currentTimeMillis();
								typingKeyIsHeld = true;
							}
						}
					}
				} else if ((flags & KEYEVENT_FLAG_RELEASE) != 0) {
					if (keyId >= KeyCode.NUM0 && keyId <= KeyCode.NUM9 && System.currentTimeMillis() - typeSeqLastTime > 1200) {
						typeSeqKeyRepeatNo = 0;
						typeSeqLastTime = 0;
					}
					endTypingKeyHold(keyId);
				}
			}
		}
	}

	/* renamed from: a */
	public static boolean checkButton(int buttonBit) {
		return (buttonsHeld & (1 << buttonBit)) != 0;
	}

	/* renamed from: k */
	private static void endTypingKeyHold(int keyCode) {
		if (keyCode == typingKeyHeldId && typingKeyIsHeld) {
			typingKeyIsHeld = false;
			typingKeyHeldId = 999;
			curTypedChar = ' ';
		}
	}

	/* renamed from: d */
	private static boolean isGameLoading() {
		return resLoadQueue.size() > 0 || resUnloadQueue.size() > 0 || sceneLoadQueueSize > 0;
	}

	/* renamed from: e */
	private static void readResourceTable() {
		if (resourcePaths == null) {
			try {
				DataInputStream dis = new DataInputStream(mMidLet.getClass().getResourceAsStream("/a"));
				int rscCount = dis.readShort();
				resourcePaths = new String[rscCount];
				resourceInfo = new ResourceInfo[rscCount];
				for (int i = 0; i < rscCount; i++) {
					resourcePaths[i] = dis.readUTF();
					resourceInfo[i] = new ResourceInfo(dis);
				}
				int batchCount = dis.readShort();
				resourceBatchInfo = new ResourceBatch[batchCount];
				loadedResources = new Object[batchCount];
				isResourceLoaded = new boolean[batchCount];
				for (int batchIdx = 0; batchIdx < batchCount; batchIdx++) {
					resourceBatchInfo[batchIdx] = new ResourceBatch(dis);
				}

				int residentCount = dis.readShort();
				ResidentResHeader[] resident = new ResidentResHeader[residentCount];
				for (int i4 = 0; i4 < residentCount; i4++) {
					resident[i4] = new ResidentResHeader(dis);
				}
				dis.close();

				for (int i = 0; i < resident.length; i++) {
					ResidentResHeader h = resident[i];
					DataInputStream strm = getStreamForRscId(h.resId);
					if (strm != null) {
						for (int gameRtIdx = 0; gameRtIdx < gameRuntimes.length; gameRtIdx++) {
							if (gameRuntimes[gameRtIdx].loadResidentData(strm, h.type)) {
								break;
							}
						}
						strm.close();
					}
				}

				gameRuntimes[0].loadResidentData((DataInputStream) null, -1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* renamed from: a */
	private static void forceSkipBytes(DataInputStream dis, int amount) throws IOException {
		for (int bytesSkipped = 0; bytesSkipped < amount; bytesSkipped++) {
			bytesSkipped = dis.skipBytes(amount - bytesSkipped);
		}
	}

	/* renamed from: a */
	private static DataInputStream getStreamForRscId(int rscId) throws IOException {
		if (!resourceInfo[rscId].exists()) {
			return null;
		}
		DataInputStream dis = new DataInputStream(mMidLet.getClass().getResourceAsStream("/" + resourcePaths[rscId]));
		if (resourceInfo[rscId].skipOffset == -1) {
			return dis;
		}
		forceSkipBytes(dis, resourceInfo[rscId].skipOffset);
		return dis;
	}

	/* renamed from: a */
	public static byte[] getLoadedResData(int i) {
		if (i >= 0 && loadedResources != null) {
			return (byte[]) loadedResources[i];
		}
		return null;
	}

	/* renamed from: e */
	public static void loadResource(int resId) {
		if (resId >= 0) {
			Integer integer = new Integer(resId);
			if (!resLoadQueue.contains(integer)) {
				resLoadQueue.addElement(integer);
			}
			resUnloadQueue.removeElement(integer);
		}
	}

	/* renamed from: f */
	private static void processResourceLoad() {
		int newCurStreamPos;
		String newLastRscPath;
		Vector vector = new Vector();
		for (int i = 0; i < resLoadQueue.size(); i++) {
			int batchId = ((Integer) resLoadQueue.elementAt(i)).intValue();
			if (!isResourceLoaded[batchId]) {
				int firstAvailInsertIdx = 0;
				int subResIdx = -1;
				while (subResIdx < resourceBatchInfo[batchId].subResIds.length) {
					int rscId = subResIdx == -1 ? resourceBatchInfo[batchId].mainResId : resourceBatchInfo[batchId].subResIds[subResIdx];
					//System.out.println("req load " + batchId + " / " + subResIdx + " / " + rscId + " first insidx " + firstAvailInsertIdx + " mypath " + resourcePaths[rscId]);
					int size = vector.size();
					int insertIdx = firstAvailInsertIdx;
					while (true) {
						if (insertIdx >= vector.size()) {
							insertIdx = size;
							break;
						}
						int[] other = (int[]) vector.elementAt(insertIdx);
						boolean pathsMatch = resourcePaths[rscId].equals(resourcePaths[other[2]]);
						boolean z2 = (insertIdx == vector.size() - 1) || !resourcePaths[rscId].equals(resourcePaths[((int[]) vector.elementAt(insertIdx + 1))[2]]);
						if (pathsMatch && resourceInfo[rscId].skipOffset < resourceInfo[other[2]].skipOffset) {
							break;
						}
						if (pathsMatch && z2) {
							insertIdx++;
							break;
						}
						insertIdx++;
					}
					//System.out.println("act insidx " + insertIdx);
					firstAvailInsertIdx = subResIdx == -1 ? insertIdx + 1 : firstAvailInsertIdx;
					vector.insertElementAt(new int[]{batchId, subResIdx, rscId}, insertIdx);
					subResIdx++;
				}
			}
		}

		DataInputStream lastStream = null;
		String lastRscPath = null;
		int curStreamPos = 0;

		for (int index = 0; index < vector.size(); index++) {
			try {
				int[] resLoadInfo = (int[]) vector.elementAt(index);
				int batchId = resLoadInfo[0];
				int subResIdx = resLoadInfo[1];
				int rscId = resLoadInfo[2];
				String rscPath = resourcePaths[rscId];
				int skipOffset = resourceInfo[rscId].skipOffset;
				int readLength = resourceInfo[rscId].readLength;
				if (lastStream == null || !rscPath.equals(lastRscPath) || curStreamPos > skipOffset) {
					if (lastStream != null) {
						lastStream.close();
						lastStream = null;
					}
					if (!(skipOffset == -1 || readLength == 0)) {
						lastStream = getStreamForRscId(rscId);
					}
				} else {
					forceSkipBytes(lastStream, skipOffset - curStreamPos);
				}
				boolean subResLoadSuccess = false;

				if (subResIdx == -1) { //main resource
					DataInputStream rscStream = skipOffset == -1 ? new DataInputStream(mMidLet.getClass().getResourceAsStream("/" + rscPath)) : lastStream;
					if (rscStream == null && rscPath.trim().length() > 0) {
						throw new IOException("Could not open stream for resource " + rscPath + " (ID " + rscId + ")");
					}
					for (int rtIdx = 0; rtIdx < gameRuntimes.length; rtIdx++) {
						try {
							Object friendlyArray = gameRuntimes[rtIdx].readResource(
									readLength != 0 ? rscStream : null,
									readLength,
									resourceBatchInfo[batchId].resType,
									batchId
							);
							if (friendlyArray != null) {
								loadedResources[batchId] = friendlyArray;
								subResLoadSuccess = true;
								lastStream = rscStream;
							} else {
								System.err.println("Failed to load resource batch " + batchId);
								rtIdx++;
							}
						} catch (IOException ex) {
							ex.printStackTrace();
							throw new IOException("Failed to read resource " + rscPath + " (ID " + rscId + " skip " + skipOffset + " batch " + batchId + " is cachestream " + (rscStream == lastStream) + ")");
						}
					}
					lastStream = rscStream;
				} else if (readLength != 0) {
					for (int i = 0; i < gameRuntimes.length; i++) {
						String finalRscPath = readLength == -1 ? rscPath : null;
						short resType = resourceBatchInfo[batchId].resType;

						switch (resType) {
							case ResourceType.IMAGE:
								int imgResNo = getShortFromByteArray(getLoadedResData(batchId), 0) + subResIdx;
								if (lastStream != null) {
									byte[] bytes = readInputStreamToBytes((InputStream) lastStream, readLength);
									imageResources[imgResNo] = Image.createImage(bytes, 0, bytes.length);
								} else {
									imageResources[imgResNo] = Image.createImage("/" + finalRscPath);
								}
								subResLoadSuccess = true;
								break;
							case ResourceType.MIDI:
							case ResourceType.LEVEL:
								if (lastStream != null) {
									preloadResourceImpl(readInputStreamToBytes(lastStream, readLength), batchId, null);
								} else {
									preloadResourceImpl(null, batchId, "/" + finalRscPath);
								}
								subResLoadSuccess = true;
								break;
							default:
								subResLoadSuccess = false;
								break;
						}
					}
				}
				if (!subResLoadSuccess && skipOffset != -1) {
					forceSkipBytes(lastStream, readLength);
				}
				if (lastStream != null) {
					newCurStreamPos = skipOffset + readLength;
					newLastRscPath = rscPath;
				} else {
					newCurStreamPos = curStreamPos;
					newLastRscPath = lastRscPath;
				}
				curStreamPos = newCurStreamPos;
				lastRscPath = newLastRscPath;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (lastStream != null) {
			try {
				lastStream.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		for (int batchIndex = 0; batchIndex < resLoadQueue.size(); batchIndex++) {
			isResourceLoaded[((Integer) resLoadQueue.elementAt(batchIndex)).intValue()] = true;
		}
		resLoadQueue.removeAllElements();
	}

	/* renamed from: a */
	private static void preloadResourceImpl(byte[] bytes, int id, String resourcePath) {
		if (bytes == null) {
			try {
				bytes = readInputStreamToBytes(mMidLet.getClass().getResourceAsStream(resourcePath), -1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (id >= 0) {
			loadedResources[id] = bytes;
			isResourceLoaded[id] = bytes != null;
		}
	}

	/* renamed from: a */
	private static byte[] readInputStreamToBytes(InputStream inputStream, int len) throws IOException {
		int i = 0;
		if (len != -1) {
			byte[] bytes = new byte[len];
			while (i < bytes.length) {
				i += inputStream.read(bytes, i, bytes.length - i);
			}
			return bytes;
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] bytes = new byte[1024];
		while (true) {
			int read = inputStream.read(bytes);
			if (read == -1) {
				break;
			}
			byteArrayOutputStream.write(bytes, 0, read);
		}
		return byteArrayOutputStream.toByteArray();
	}

	/* renamed from: f */
	public static void unloadResource(int resId) {
		if (resId >= 0) {
			Integer num = new Integer(resId);
			if (!resUnloadQueue.contains(num)) {
				resUnloadQueue.addElement(num);
			}
			resLoadQueue.removeElement(num);
		}
	}

	/* renamed from: g */
	private static void processResourceUnload() {
		for (int i = 0; i < resUnloadQueue.size(); i++) {
			int unloadResId = ((Integer) resUnloadQueue.elementAt(i)).intValue();
			if (isResourceLoaded[unloadResId]) {
				short resType = resourceBatchInfo[unloadResId].resType;
				boolean unloadSuccess = false;
				for (int j = 0; j < gameRuntimes.length; j++) {
					switch (resType) {
						case ResourceType.IMAGE:
							byte[] texAtlasInfo = (byte[]) loadedResources[unloadResId];
							for (int k = 0; k < imageMaps2.length; k++) {
								ImageMapEx m2 = imageMaps2[k];
								if (m2.resBatchId == unloadResId) {
									m2.clear();
								}
							}
							short startImageId = getShortFromByteArray(texAtlasInfo, 0);
							short endImageId = getShortFromByteArray(texAtlasInfo, 2);
							for (int k = 0; k < imageMaps.length; k++) {
								ImageMap m = imageMaps[k];
								if (m.imageId >= startImageId && m.imageId < endImageId) {
									m.clear();
								}
							}
							for (int imgResIdx = startImageId; imgResIdx < endImageId; imgResIdx++) {
								imageResources[imgResIdx] = null;
							}
							unloadSuccess = true;
							break;
						case ResourceType.MIDI:
							unloadSuccess = true;
							break;
						case ResourceType.STRINGS:
							short[] stringDesc = (short[]) loadedResources[unloadResId];
							int stringCount = stringDesc[0] + stringDesc[1];
							for (int s2 = stringDesc[0]; s2 < stringCount; s2++) {
								residentStrings[s2 == 1 ? 1 : 0] = null;
								s2 = (s2 == 1 ? 1 : 0) + 1;
							}
							unloadSuccess = true;
							break;
					}
					if (unloadSuccess) {
						loadedResources[unloadResId] = null;
						isResourceLoaded[unloadResId] = false;
					}
				}
				loadedResources[unloadResId] = null;
				isResourceLoaded[unloadResId] = false;
			}
		}
		resUnloadQueue.removeAllElements();
	}

	/* renamed from: g */
	public static void loadResidentResSet(int setId) {
		short[] arr = residentResMap[setId];
		for (int i = 0; i < arr.length; i++) {
			loadResource(arr[i]);
		}
	}

	/* renamed from: i */
	private void updateGameLoad() {
		gameIsLoading = true;
		new Thread(mInstance, "LoadingThread").start();
		readResourceTable();
		int eventResult = 0;
		int sceneLoadIdx = 0;
		while (true) {
			if (sceneLoadIdx < sceneLoadQueueSize || !resLoadQueue.isEmpty() || !resUnloadQueue.isEmpty()) {
				//System.out.println("Begin loading resources: batch remaining " + batchResourceIds.size() + " single remaining " + singleResourceIds.size() + " scenes " + sceneLoaderIdx + "/" + sceneLoaderQueueSize);
				if (!resUnloadQueue.isEmpty()) {
					processResourceUnload();
					System.gc();
				}
				if (!resLoadQueue.isEmpty()) {
					processResourceLoad();
					System.gc();
				}
				if (sceneLoadIdx < sceneLoadQueueSize && (eventResult = mBounceGame.loadScene(sceneLoaderQueue[sceneLoadIdx], eventResult)) == 0) {
					sceneLoadIdx++;
				}
				//System.out.println("Resource load done!");
			} else {
				synchronized (loadingMutex) {
					gameIsLoading = false;
					loadingMutex.notify();
				}
				sceneLoadQueueSize = 0;
				resumeRuntime();
				return;
			}
		}
	}

	/* renamed from: b */
	public static boolean isResourceLoadDone(int resId) {
		return resId >= 0 && isResourceLoaded != null && isResourceLoaded[resId];
	}

	/* renamed from: a */
	public final Object readResource(DataInputStream dis, int readLength, int type, int resBatchId) throws IOException {
		switch (type) {
			case ResourceType.IMAGE:
				//Some sort of image, but not PNG...
				byte imageCount = dis.readByte();
				short baseImageID = dis.readShort();
				short count1 = dis.readShort();
				short count2 = dis.readShort();
				short imageMapCount = dis.readShort();
				for (int i = 0; i < count1; i++) {
					imageMaps2[dis.readShort() - imageMaps.length].read(resBatchId, dis);
				}
				ByteArrayOutputStream baos = new ByteArrayOutputStream(count2 + 4);
				DataOutputStream dos = new DataOutputStream(baos);
				dos.writeShort(baseImageID);
				dos.writeShort(baseImageID + imageCount);
				for (int dataIdx = 0; dataIdx < count2; dataIdx++) {
					dos.writeByte(dis.readByte());
				}
				for (int i = 0; i < imageMapCount; i++) {
					imageMaps[dis.readShort()].read(dis);
				}
				return baos.toByteArray();
			case ResourceType.MIDI:
				return new Object();
			case ResourceType.STRINGS:
				short stringCount = dis.readShort();
				short firstStrId = dis.readShort();
				int headerFieldId = 0;
				int sectionEnd = 0;
				int stringSectionSize = -999999;
				int skipToStrStart = -999999;
				while (true) {
					if (headerFieldId < residentStringFieldCount + 1) {
						if (headerFieldId == 0) {
							skipToStrStart = dis.readInt();
						} else {
							sectionEnd = dis.readInt();
							if (headerFieldId == 1) { //first section
								stringSectionSize = sectionEnd - skipToStrStart;
							}
						}
						headerFieldId++;
					} else {
						forceSkipBytes(dis, skipToStrStart);
						for (int i = 0; i < stringCount; i++) {
							if (residentStrings[firstStrId + i] == null) {
								residentStrings[firstStrId + i] = dis.readUTF();
							} else {
								dis.readUTF();
							}
						}
						forceSkipBytes(dis, sectionEnd - (skipToStrStart + stringSectionSize));
						return new short[]{(short) firstStrId, (short) stringCount};
					}
				}
			default:
				return readInputStreamToBytes((InputStream) dis, readLength);
		}
	}

	/* renamed from: a */
	public final boolean loadResidentData(DataInputStream dis, int type) throws IOException {
		switch (type) {
			case -1:
				imageMaps2 = new ImageMapEx[214];
				imageMaps = new ImageMap[326];
				imageResources = new Image[46];
				for (int mapIdx = 0; mapIdx < imageMaps2.length; mapIdx++) {
					imageMaps2[mapIdx] = new ImageMapEx();
				}
				for (int mapIdx = 0; mapIdx < imageMaps.length; mapIdx++) {
					imageMaps[mapIdx] = new ImageMap();
				}
				return false;
			case 0:
			case 1:
			case 2:
			case 3:
			case 5:
			default:
				return false;
			case 4:
				residentStringFieldCount = dis.readByte();
				residentStrings = new String[dis.readShort()];
				return false;
			case 6: //contains BGM and splash screen
				int resMapGroupCount = dis.readShort();
				residentResMap = new short[resMapGroupCount][];
				for (int groupIndex = 0; groupIndex < resMapGroupCount; groupIndex++) {
					int resBatchCount = dis.readShort();
					residentResMap[groupIndex] = new short[resBatchCount];
					for (int resBatchIndex = 0; resBatchIndex < resBatchCount; resBatchIndex++) {
						residentResMap[groupIndex][resBatchIndex] = dis.readShort();
					}
				}
				return true;
			case 7:
				return true;
		}
	}

	public final void commandAction(Command command, Displayable displayable) {
	}

	//@Override
	protected final void showNotify() {
		setState(GameState.SHOWN);
	}

	//@Override
	protected final void hideNotify() {
		setState(GameState.HIDDEN);
	}

	//@Override
	protected final void keyPressed(int keyCode) {
		onKeyEvent(keyCode, KEYEVENT_FLAG_JMEKEYCODE | KEYEVENT_FLAG_PRESS);
	}

	//@Override
	protected final void keyReleased(int keyCode) {
		onKeyEvent(keyCode, KEYEVENT_FLAG_JMEKEYCODE | KEYEVENT_FLAG_RELEASE);
	}

	//@Override
	public final void paint(Graphics graphics) {
		gamePaint(graphics);
	}

	//@Override
	public final void run() {
		System.out.println("Starting BounceThread");
		try {
			if (gameThreadStarted) {
				synchronized (loadingMutex) {
					try {
						loadingMutex.wait(LOADING_WAIT_TIMEOUT);
						while (gameIsLoading) {
							updateViewport();
							callGamePaint(2);
							loadingMutex.wait(LOADING_WAIT_TIMEOUT);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("Loading screen thread ended");
				return;
			}
			System.out.println("Initializing game");
			gameThreadStarted = true;
			DEBUG_OVERLAY_ON = getAppFlag("DebugOverlay");
			systemEventQueue = new int[20];
			systemEventQueueSize = 0;
			sceneLoaderQueue = new int[40];
			sceneLoadQueueSize = 0;
			keyQueue = new int[20];
			keyQueueSize = 0;
			reqSoftkeyTexts = new String[3];
			softkeyTexts = new String[3];
			softkeyUITypes = new int[3];
			Display.getDisplay(mMidLet).setCurrent(this);
			GameRuntime.super.setFullScreenMode(true);
			updateViewport();
			resLoadQueue = new Vector();
			resUnloadQueue = new Vector();
			gameRuntimes = new GameRuntime[]{mInstance};
			mBounceGame = new BounceGame();
			notifySystemEvent(SYSTEM_EVENT_START);
			resumeRuntime();
			long[] framets = new long[20];
			int frameIndex = 0;
			while (!reqClose) {
				while (true) {
					try {
						currentTime = System.currentTimeMillis();
						if (!isGamePaintEnabled) {
							reqSystemGamePause = true;
						}
						updateViewport();
						if (systemEventQueueSize > 0) {
							synchronized (systemEventQueue) {
								for (int i = 0; i < systemEventQueueSize; i++) {
									mBounceGame.onSystemEvent(systemEventQueue[i]);
									systemEventQueue[i] = 0;
								}
								systemEventQueueSize = 0;
							}
							synchronized (gameMutex) {
								if (music_IdQueuedAfterSysUnpause != -1) {
									//this is bugged as the menu music will be muted on incoming call
									//however, the straightforward way to fix it wouldn't work, as sounds are not allowed when this part of the code runs
									//not sure if I want to rewrite this or preserve the original behavior
									if (music_IdBeforeSysPause != music_IdQueuedAfterSysUnpause) {
										playMusic(music_IdQueuedAfterSysUnpause, -1);
									}
									music_IdQueuedAfterSysUnpause = -1;
								}
							}
						}
						if (isGameLoading()) {
							for (int softkeyIdx = 0; softkeyIdx < reqSoftkeyTexts.length; softkeyIdx++) {
								softkeyTexts[softkeyIdx] = null;
							}
							updateGameLoad();
							currentTime = System.currentTimeMillis();
						}
						if (DEBUG_OVERLAY_ON) {
							framets[frameIndex++] = currentTime;
							if (frameIndex == framets.length) {
								frameIndex = 0;
							}
							if (framets[frameIndex] != 0f) {
								debugFps = (float) framets.length / ((currentTime - framets[frameIndex]) / 1000f);
							} else {
								debugFps = 0f;
							}
						}
						if (!reqSystemGamePause) {
							for (int softkeyIdx = 0; softkeyIdx < reqSoftkeyTexts.length; softkeyIdx++) {
								if (!objectsEquals(softkeyTexts[softkeyIdx], reqSoftkeyTexts[softkeyIdx])) {
									if (softkeyTexts[softkeyIdx] == null || !softkeyTexts[softkeyIdx].equals(reqSoftkeyTexts[softkeyIdx])) {
										softkeyTexts[softkeyIdx] = reqSoftkeyTexts[softkeyIdx];
									} else {
										reqSoftkeyTexts[softkeyIdx] = softkeyTexts[softkeyIdx];
									}
								}
							}
							callGamePaint(1);
							if (!reqSystemGamePause) {
								gameUpdate();
								if (reqClose) {
									System.out.println("Game is about to close.");
									break;
								}
								if (typingKeyIsHeld && typingKeyHeldId >= 10 && typingKeyHeldId <= 19 && System.currentTimeMillis() - typingKeyHoldStartTime > 500) {
									endTypingKeyHold(typingKeyHeldId);
								}
								if (reqSystemGamePause) {
									waitPausedRuntime();
								} else {
									Thread.sleep(1);
								}
							} else {
								waitPausedRuntime();
							}
						} else { //paused
							waitPausedRuntime();
						}
					} catch (Throwable th) {
						th.printStackTrace();
					}
				} //end game loop
			}
			mBounceGame = null;
			stopMusic();
			for (int unloadIdx = 0; unloadIdx < loadedResources.length; unloadIdx++) { //unload all resources
				resUnloadQueue.addElement(new Integer(unloadIdx));
			}
			processResourceUnload();
			MIDlet m = mMidLet;
			mMidLet = null;
			mInstance = null;
			resetGlobalState();
			m.notifyDestroyed();
		} catch (Throwable th2) {
			th2.printStackTrace();
		}
		System.out.println("BounceThread died");
	}

	/* renamed from: l */
	private static void waitPausedRuntime() {
		synchronized (gameMutex) {
			if (!isGameLoading()) {
				reqSystemGamePause = false;
				notifySystemEvent(SYSTEM_EVENT_PAUSE);
			}
			if (midletIsPaused) {
				try {
					gameMutex.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		resumeRuntime();
	}

	/* renamed from: h */
	private static void resumeRuntime() {
		lastUpdateTimestamp = 0;
		updateDelta = 0;
		resetHID();
	}

	/* renamed from: h */
	public static void setState(int state) {
		synchronized (gameMutex) {
			System.out.println("New game state " + state);
			buttonsDown = 0;
			buttonsHeld = 0;
			buttonsHit = 0;
			if (state == GameState.INIT) {
				mInstance = new GameRuntime();
				new Thread(mInstance, "BounceThread").start();
			} else if (mInstance != null) {
				if (state == GameState.PAUSE || state == GameState.HIDDEN) {
					if (!midletIsPaused) {
						midletIsPaused = true;
						reqSystemGamePause = true;
						int returnMusic = music_IdCurrent;
						music_IdQueuedAfterSysUnpause = returnMusic;
						music_IdBeforeSysPause = returnMusic;
					}
				} else if ((state == GameState.RUN || state == GameState.SHOWN) && midletIsPaused) {
					System.out.println("Midlet shown, releasing mutex...");
					midletIsPaused = false;
					gameMutex.notifyAll();
				}
			} else {
				System.out.println("State change requested, but instance not available!");
			}
		}
	}

	/* renamed from: i */
	private static void notifySystemEvent(int eventId) {
		if (systemEventQueueSize != 20) {
			systemEventQueue[systemEventQueueSize] = eventId;
			systemEventQueueSize++;
		}
	}

	/* renamed from: j */
	private static void updateViewport() {
		int width = mInstance.getWidth();
		int height = mInstance.getHeight();
		if (currentWidth != width || currentHeight != height) {
			currentWidth = width;
			currentHeight = height;
			if (mBounceGame != null) {
				notifySystemEvent(SYSTEM_EVENT_RESIZE);
			}
		}
	}

	/* renamed from: k */
	private static void gameUpdate() {
		long currentTimeMillis = System.currentTimeMillis();
		if (lastUpdateTimestamp != 0) {
			int delta = (int) (currentTimeMillis - lastUpdateTimestamp);
			updateDelta = delta;
			int deltaPerUpdate = delta / updatesPerDraw;
			updateDelta = deltaPerUpdate;
			if (deltaPerUpdate > maxUpdateDelta) {
				updateDelta = maxUpdateDelta;
			}
			if (updateDelta == 0) {
				//when the game is too fast (such as after out jademula drawRegion fix)
				//the update delta can sometimes be so small that we can't even notice it
				//if this happens too much, the imprecision will actually slow the game down
				//because of too many subsequent zero deltas. thus, we will skip the update altogether.
				return;
			}
		} else {
			updateDelta = 0;
		}
		lastUpdateTimestamp = currentTimeMillis;
		disableHID = false;
		for (int updateIdx = 0; updateIdx < updatesPerDraw; updateIdx++) {
			for (int keyComboIndex = 0; keyComboIndex < keyQueueSize; keyComboIndex++) {
				mBounceGame.handleKeyPress(keyQueue[keyComboIndex]);
				keyQueue[keyComboIndex] = -1;
			}
			keyQueueSize = 0;
			buttonsHeld = buttonsDown | buttonsHit;
			buttonsHit = 0;
			for (int updateRes = mBounceGame.update(0); updateRes != 0; updateRes = mBounceGame.update(updateRes)) {
			}
			if (isGameLoading()) {
				return;
			}
		}
	}

	private static boolean objectsEquals(Object a, Object b) {
		if (a == null) {
			return b == null;
		} else if (b == null) {
			return false;
		}
		return a.equals(b);
	}
}
