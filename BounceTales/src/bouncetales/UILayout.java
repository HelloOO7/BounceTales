package bouncetales;

import bouncetales.ext.rsc.ImageMap;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;

/* renamed from: h */
public final class UILayout {

	public static final int SOFTKEY_BAR = 2;
	public static final int ANCHOR_CENTER = 4;
	public static final int TITLE_PADDING_TOP = 6;
	public static final int TITLE_PADDING_BOTTOM = 7;
	public static final int TITLE_PADDING_SIDE = 8;
	public static final int OFFSET_LEFT = 9;
	public static final int OFFSET_TOP = 10;
	public static final int FIXED_WIDTH = 11;
	public static final int FIXED_HEIGHT = 12;
	public static final int FONT = 13;
	public static final int BLOCK_INCREMENT = 14;
	public static final int MARGIN_LEFT = 15;
	public static final int MARGIN_RIGHT = 16;
	public static final int MARGIN_TOP = 17;
	public static final int MARGIN_BOTTOM = 18;
	public static final int VERTICAL_SPACING = 19;
	public static final int SOFTKEY_BAR_COLOR = 21;
	public static final int FONT_TEXT_COLOR = 24;
	public static final int FONT_SHADOW_COLOR = 25;

	/* renamed from: e */
	private static final int[] f118e = new int[4];

	private static final int[] ELEMENT_FLAG_MASKS = {3, 12, 48, 64, 128, 256, 1536, 2048, 4096}; //renamed from: f
	private static final int[] DEFAULT_ELEMENT_ATTRIBUTES = {-1, 7304, -1, -1, 1, 1, 1, 1, 0xFFFFFF, 0, 0, 0x666666, 0, 0, 0x666666, 0xAA7700}; //renamed from: g

	private static final int[] LAYOUT_FLAG_MASKS = {3, 12, 16, 32, 64, 128}; //renamed from: h
	private static final int[] DEFAULT_LAYOUT_ATTRIBUTES = {-1, 8, 2, 2, 2, 0, 0, -1, -1, -1, 20, 12, 12, 2, 6, 1, 0x440000, 0x5E2601, 68, 0xA0A0A0, 0, 0x990000, 0}; //renamed from: i 

	public int uiID = 0; //renamed from: a
	public int[] elemDefaultAttributes = null; //renamed from: a
	public int[] layoutAttributes = null; //renamed from: b

	private final Vector elements = new Vector(); //renamed from: a
	private int selectedElemIdx = -1; //renamed from: b
	private int focusStartY = 0; //renamed from: c

	private TextLabel titleLabel = null; //renamed from: a
	private int titleImageId = -1; //renamed from: d
	private int titleAnimTimer = 0; //renamed from: e
	private int titleXScroll = 0; //renamed from: f
	private int titleScrollDirection = 1; //renamed from: g

	private final String[] softkeyTexts = new String[3]; //renamed from: a
	private final int[] softkeyActions = new int[3]; //renamed from: d
	private final int[] softkeyTypes = new int[3]; //renamed from: d

	/* renamed from: a */
	public static boolean attributeExists(int aid, int[] arr) {
		return (arr != null && (arr[0] & (1 << aid)) != 0);
	}

	/* renamed from: c */
	private int getAttribute(int aid) {
		return readAttribute(aid, this.layoutAttributes, 2);
	}

	/* renamed from: a */
	public final void setElemDefaultAttribute(int aid, int value) {
		this.elemDefaultAttributes = writeAttribute(aid, value, this.elemDefaultAttributes, 1);
	}

	/* renamed from: b */
	public final void setAttribute(int aid, int value) {
		this.layoutAttributes = writeAttribute(aid, value, this.layoutAttributes, 2);
	}

	/* renamed from: a */
	public static int readAttribute(int aid, int[] arr, int type) {
		int[] srcArr;
		int[] flagAttrMasks;
		if (type == 1) {
			srcArr = DEFAULT_ELEMENT_ATTRIBUTES;
			flagAttrMasks = ELEMENT_FLAG_MASKS;
		} else {
			srcArr = DEFAULT_LAYOUT_ATTRIBUTES;
			flagAttrMasks = LAYOUT_FLAG_MASKS;
		}
		int flagCount = flagAttrMasks.length;
		if (attributeExists(aid, arr)) {
			srcArr = arr;
		}
		if (aid >= flagCount) {
			return srcArr[(aid + 2) - flagCount];
		}
		return flagAttrMasks[aid] & srcArr[1];
	}

	/* renamed from: a */
	public static int[] writeAttribute(int aid, int value, int[] arr, int type) {
		int avalArrSize;
		int[] flagAttrMasks;
		if (type == 1) {
			avalArrSize = 16;
			flagAttrMasks = ELEMENT_FLAG_MASKS;
		} else {
			avalArrSize = 23;
			flagAttrMasks = LAYOUT_FLAG_MASKS;
		}
		if (arr == null) {
			arr = new int[avalArrSize];
		} else if (arr.length < avalArrSize) {
			int[] iArr3 = new int[avalArrSize];
			System.arraycopy(arr, 0, iArr3, 0, arr.length);
			arr = iArr3;
		}
		arr[0] = arr[0] | (1 << aid);
		if (aid < flagAttrMasks.length) {
			arr[1] = arr[1] & (~flagAttrMasks[aid]);
			arr[1] = arr[1] | (flagAttrMasks[aid] & value);
		} else {
			arr[(aid + 2) - flagAttrMasks.length] = value;
		}
		return arr;
	}

	/* renamed from: a */
	public final void addElement(UIElement option) {
		int size = this.elements.size();
		this.elements.insertElementAt(option, size);
		option.parentUI = this;
		if (this.selectedElemIdx == -1 && isElemInFocus(size)) {
			setSelectedOption(size);
		}
	}

	/* renamed from: a */
	public final void clear() {
		this.elements.removeAllElements();
		this.focusStartY = 0;
		this.selectedElemIdx = -1;
	}

	/* renamed from: a */
	private UIElement getSelectedElement() {
		UIElement elem = this.selectedElemIdx != -1 ? (UIElement) this.elements.elementAt(this.selectedElemIdx) : null;
		if (elem == null || !elem.isEnabled) {
			return null;
		}
		return elem;
	}

	/* renamed from: a */
	public final int getSelectedOption() {
		return this.selectedElemIdx;
	}

	/* renamed from: a */
	public final void setSelectedOption(int elemIdx) {
		if (elemIdx >= this.elements.size()) {
			elemIdx = this.elements.size() - 1;
		}
		if (elemIdx != this.selectedElemIdx) {
			if (elemIdx == -1 || ((UIElement) this.elements.elementAt(elemIdx)).hasAction()) {
				int lastSelOption = this.selectedElemIdx;
				this.selectedElemIdx = elemIdx;
				if (lastSelOption != -1) {
					this.elements.elementAt(lastSelOption);
				}
				if (elemIdx != -1) {
					this.elements.elementAt(elemIdx);
				}
				if (this.selectedElemIdx != -1) {
					int startY = getElemStartY(this.selectedElemIdx);
					int endY = ((UIElement) this.elements.elementAt(this.selectedElemIdx)).getHeight() + startY;
					int focusHeight = getFocusHeight();
					if (startY < this.focusStartY) {
						this.focusStartY = startY;
					} else if (endY > this.focusStartY + focusHeight) {
						this.focusStartY = endY - focusHeight;
					}
				}
			}
		}
	}

	/* renamed from: a */
	private boolean isElemInFocus(int elemIdx) {
		UIElement elem = (UIElement) this.elements.elementAt(elemIdx);
		int startY = getElemStartY(elemIdx);
		return startY >= this.focusStartY && elem.getHeight() + startY <= this.focusStartY + getFocusHeight();
	}

	/* renamed from: a */
	private int getElemStartY(int elemIdx) {
		int totalHeight = 0;
		for (int i = 0; i < elemIdx; i++) {
			totalHeight += ((UIElement) this.elements.elementAt(i)).getHeight() + getAttribute(VERTICAL_SPACING);
		}
		return totalHeight;
	}

	/* renamed from: a */
	private static int getElemXAnchor(UIElement elem, int parentWidth) {
		int anchor = elem.getAttribute(UIElement.ANCHOR_H);
		if (anchor == 512) {
			return parentWidth - elem.getWidth();
		}
		if (anchor == 1024) {
			return (parentWidth - elem.getWidth()) >> 1;
		}
		return 0;
	}

	/* renamed from: b */
	private int getLayoutVDim(int i) {
		switch (i) {
			case 1: //max content Y
				int layoutYMax = GameRuntime.currentHeight;
				if (GameRuntime.isScreenPortrait()) {
					layoutYMax -= GameRuntime.getSoftkeyBarHeight();
				}
				if (getAttribute(3) == 32) {
					return Math.min(getLayoutVDim(2) + getTotalHeight() + getAttribute(MARGIN_TOP) + getAttribute(MARGIN_BOTTOM), layoutYMax);
				} else {
					int c = getAttribute(FIXED_HEIGHT);
					return c <= 0 ? layoutYMax : c;
				}
			case 2: //min content Y
				int titleYEnd = this.titleLabel != null ? this.titleLabel.textBlockHeight : 0;
				int imgHeight = 0;
				if (this.titleImageId != -1) {
					imgHeight = GameRuntime.getImageMapParam(this.titleImageId, ImageMap.PARAM_HEIGHT);
				}
				return Math.max(titleYEnd, imgHeight) + getAttribute(TITLE_PADDING_TOP) + getAttribute(TITLE_PADDING_BOTTOM);
			case 3:
			default:
				return 0;
			case 4: //content max height
				return getLayoutVDim(1) - getLayoutVDim(2);
			case 5:
				return (getLayoutVDim(4) - getAttribute(MARGIN_TOP)) - getAttribute(MARGIN_BOTTOM);
			case 6:
				return GameRuntime.getImageMapParam(151, ImageMap.PARAM_HEIGHT) + 1;
			case 7:
				return GameRuntime.getImageMapParam(150, ImageMap.PARAM_HEIGHT) + 1;
		}
	}

	/* renamed from: c */
	private int getLayoutHDim() {
		int c = getAttribute(FIXED_WIDTH);
		if (c > 0) {
			return c;
		}
		return GameRuntime.getCurrentWidth() - (GameRuntime.isScreenLandscape() ? GameRuntime.getSoftkeyBarWidth() : 0);
	}

	/* renamed from: d */
	private int getTotalHeight() {
		if (this.elements.size() <= 0) {
			return 0;
		}
		return ((UIElement) this.elements.lastElement()).getHeight() + getElemStartY(this.elements.size() - 1);
	}

	/* renamed from: b */
	public final int getFocusWidth() {
		return (getLayoutHDim() - getAttribute(MARGIN_LEFT)) - getAttribute(MARGIN_RIGHT);
	}

	/* renamed from: e */
	private int getFocusHeight() {
		int b = getLayoutVDim(5);
		return getTotalHeight() > b ? (b - getLayoutVDim(TITLE_PADDING_TOP)) - getLayoutVDim(7) : b;
	}

	/* renamed from: a */
	public final void changeSoftkey(int softkey, String text, int type) {
		if ((this.softkeyActions[softkey] & 0xFFFF) == 0xFFFF) {
			this.softkeyTexts[softkey] = text;
			this.softkeyTypes[softkey] = type;
		}
	}

	/* renamed from: a */
	public final void setSoftkey(int softkey, String buttonText, int i2, int action, boolean z) {
		this.softkeyActions[softkey] = 0x110000 | (action & 0xFFFF);
		this.softkeyTexts[softkey] = buttonText;
		this.softkeyTypes[softkey] = 0;
	}

	/* renamed from: b */
	public final void disableSoftkey(int buttonIdx) {
		this.softkeyActions[buttonIdx] = 0;
		this.softkeyTexts[buttonIdx] = null;
		this.softkeyTypes[buttonIdx] = 0;
	}

	/* renamed from: b */
	public final void setupSoftkeys() {
		for (int softkeyIdx = 0; softkeyIdx < 3; softkeyIdx++) {
			boolean z = ((short) this.softkeyActions[softkeyIdx]) == -2;
			UIElement selElem = getSelectedElement();
			if ((this.softkeyActions[softkeyIdx] & 0xFFFF) == 0 || (this.softkeyActions[softkeyIdx] & 0x100000) == 0 || (z && selElem == null)) {
				GameRuntime.setSoftkey(softkeyIdx, (String) null, 0);
			} else {
				GameRuntime.setSoftkey(softkeyIdx, this.softkeyTexts[softkeyIdx], this.softkeyTypes[softkeyIdx]);
			}
		}
	}

	/* renamed from: a */
	public final void setTitle(String str, int bgImage, int i2) {
		this.titleLabel = (str == null || str.length() <= 0) ? null : new TextLabel(str, Integer.MAX_VALUE, getAttribute(FONT), getAttribute(0) | 256, -1);
		this.titleImageId = -1;
		this.titleXScroll = 0;
	}

	/* renamed from: c */
	public final void updateTitleScroll() {
		if (this.titleLabel != null) {
			int titleImageWidth = this.titleImageId != -1 ? GameRuntime.getImageMapParam(this.titleImageId, ImageMap.PARAM_WIDTH) : 0;
			int c = getLayoutHDim() - (getAttribute(TITLE_PADDING_SIDE) << 1);
			int i = getAttribute(1) != 8 ? c - titleImageWidth : c;
			if (i < this.titleLabel.textBlockWidth) {
				if (this.titleAnimTimer >= 3000) {
					if (this.titleScrollDirection > 0) {
						this.titleXScroll = ((this.titleAnimTimer - 3000) * 20) / 1000;
						if (this.titleXScroll > this.titleLabel.textBlockWidth - i) {
							this.titleXScroll = this.titleLabel.textBlockWidth - i;
							this.titleAnimTimer = 0;
							this.titleScrollDirection = -1;
						}
					} else {
						this.titleXScroll = (this.titleLabel.textBlockWidth - i) - (((this.titleAnimTimer - 3000) * 20) / 1000);
						if (this.titleXScroll <= 0) {
							this.titleXScroll = 0;
							this.titleAnimTimer = 0;
							this.titleScrollDirection = 1;
						}
					}
				}
				this.titleAnimTimer += GameRuntime.updateDelta;
			}
		}
	}

	/* renamed from: c */
 /*
	 * FIXME. THIS METHOD WAS DECOMPILED REAAAAALLY BADLY!!!
	 */
	public final void handleKeyCode(int keyCode) {
		int i2;
		int i3;
		int resultSoftkey;
		UIElement selElem = getSelectedElement();
		switch (keyCode) {
			case KeyCode.UP:
				if (!this.elements.isEmpty()) {
					if (this.selectedElemIdx != -1) {
						int i5 = this.selectedElemIdx - 1;
						while (true) {
							if (i5 < 0) {
								i5 = -1;
								break;
							} else if (!((UIElement) this.elements.elementAt(i5)).hasAction()) {
								i5--;
							} else {
								break;
							}
						}
						i3 = i5 != -1 ? ((UIElement) this.elements.elementAt(i5)).getHeight() + getElemStartY(i5) < this.focusStartY - getAttribute(BLOCK_INCREMENT) ? -1 : i5 : i5;
					} else {
						i3 = -1;
						for (int j = 0; j < this.elements.size(); ++j) {
							final UIElement f = (UIElement) this.elements.elementAt(j);
							final int a2;
							final int n4 = (a2 = this.getElemStartY(j)) + f.getHeight();
							if (a2 >= this.focusStartY) {
								break;
							}
							if (f.hasAction() && n4 > this.focusStartY - this.getAttribute(BLOCK_INCREMENT)) {
								i3 = j;
							}
						}
					}
					if (i3 != -1) {
						setSelectedOption(i3);
						return;
					} else if (getAttribute(5) != 128 || this.focusStartY > 0) {
						this.focusStartY -= getAttribute(BLOCK_INCREMENT);
						if (this.focusStartY < 0) {
							this.focusStartY = 0;
						}
						if (!(this.selectedElemIdx == -1 || isElemInFocus(this.selectedElemIdx))) {
							setSelectedOption(-1);
							return;
						}
						return;
					} else {
						int d = getTotalHeight();
						this.focusStartY = d - getFocusHeight();
						if (d < getLayoutVDim(5)) {
							this.focusStartY = 0;
						}
						for (int size = this.elements.size() - 1; size > 0; size--) {
							if (!isElemInFocus(size)) {
								setSelectedOption(-1);
								return;
							} else if (((UIElement) this.elements.elementAt(size)).hasAction()) {
								setSelectedOption(size);
								return;
							}
						}
						return;
					}
				} else {
					return;
				}
			case KeyCode.DOWN:
				if (!this.elements.isEmpty()) {
					int d2 = getTotalHeight();
					int e = getFocusHeight();
					if (this.selectedElemIdx != -1) {
						int i8 = this.selectedElemIdx + 1;
						while (true) {
							if (i8 >= this.elements.size()) {
								i2 = -1;
								break;
							} else if (((UIElement) this.elements.elementAt(i8)).hasAction()) {
								i2 = i8;
								break;
							} else {
								i8++;
							}
						}
						if (i2 != -1 && getElemStartY(i2) > this.focusStartY + e + getAttribute(BLOCK_INCREMENT)) {
							i2 = -1;
						}
					} else {
						i2 = -1;
						for (int n8 = this.elements.size() - 1; n8 >= 0; --n8) {
							final UIElement f2 = (UIElement) this.elements.elementAt(n8);
							final int n9;
							if ((n9 = this.getElemStartY(n8) + f2.getHeight()) < this.focusStartY + e) {
								break;
							}
							if (f2.hasAction() && n9 <= this.focusStartY + e + this.getAttribute(BLOCK_INCREMENT)) {
								i2 = n8;
							}
						}
					}
					if (i2 != -1) {
						setSelectedOption(i2);
						return;
					} else if (getAttribute(5) != 128 || this.focusStartY < d2 - e) {
						this.focusStartY += getAttribute(BLOCK_INCREMENT);
						if (d2 <= e) {
							this.focusStartY = 0;
						} else if (this.focusStartY > d2 - e) {
							this.focusStartY = d2 - e;
						}
						if (!(this.selectedElemIdx == -1 || isElemInFocus(this.selectedElemIdx))) {
							setSelectedOption(-1);
							return;
						}
						return;
					} else {
						this.focusStartY = 0;
						for (int j = 0; j < this.elements.size() - 1; j++) {
							UIElement fVar3 = (UIElement) this.elements.elementAt(j);
							if (!isElemInFocus(j)) {
								setSelectedOption(-1);
								return;
							} else if (fVar3.hasAction()) {
								setSelectedOption(j);
								return;
							}
						}
						return;
					}
				} else {
					return;
				}
			case KeyCode.SOFTKEY_RIGHT:
				resultSoftkey = 1;
				break;
			case KeyCode.SOFTKEY_LEFT:
			case 24:
				resultSoftkey = 2;
				break;
			case KeyCode.SOFTKEY_MIDDLE:
				resultSoftkey = 0;
				break;
			default:
				return;
		}
		if ((this.softkeyActions[resultSoftkey] & 0x10000) != 0) {
			short s = (short) this.softkeyActions[resultSoftkey];
			if (s != -2) {
				GameRuntime.mBounceGame.changeScene(s);
			} else if (selElem != null) {
				GameRuntime.mBounceGame.changeScene(selElem.action);
			}
		}
	}

	/* renamed from: d */
	public final void draw() {
		int lytX;
		int lytY;
		int i;
		Graphics grp = GameRuntime.getGraphicsObj();
		int b = getLayoutVDim(2);
		if (getAttribute(ANCHOR_CENTER) == 64) {
			//anchor to X center of drawable area
			lytX = ((GameRuntime.currentWidth - getLayoutHDim()) >> 1) + (GameRuntime.getScreenOrientationFromSoftkeys() == GameRuntime.SCREEN_ORIENTATION_RLANDSCAPE ? GameRuntime.getSoftkeyBarWidth() : 0);
		} else {
			//anchor relative to left
			lytX = getAttribute(OFFSET_LEFT) + (GameRuntime.getScreenOrientationFromSoftkeys() == GameRuntime.SCREEN_ORIENTATION_RLANDSCAPE ? GameRuntime.getSoftkeyBarWidth() : 0);
		}
		if (getAttribute(ANCHOR_CENTER) == 64) {
			//anchor to Y center of drawable area
			lytY = ((GameRuntime.currentHeight - (GameRuntime.isScreenPortrait() ? GameRuntime.getSoftkeyBarHeight() : 0)) - getLayoutVDim(1)) >> 1;
		} else {
			//anchor relative to top
			lytY = getAttribute(OFFSET_TOP) + (GameRuntime.getScreenOrientationFromSoftkeys() == GameRuntime.SCREEN_ORIENTATION_RPORTRAIT ? GameRuntime.getSoftkeyBarHeight() : 0);
		}
		int lytW = getLayoutHDim();
		int lytH = getLayoutVDim(1);

		if (BounceGame.drawUIGraphics(this, 1, lytX, lytY, lytW, lytH)) {
			grp.setColor(getAttribute(22));
			grp.fillRect(lytX, lytY, lytW, lytH);
			grp.setColor(getAttribute(23));
			grp.drawRect(lytX, lytY, lytW - 1, lytH - 1);
		}
		if (b > 0) {
			grp.setClip(lytX, lytY, lytW, b);
			if (BounceGame.drawUIGraphics(this, 2, lytX, lytY, lytW, b)) {
				System.out.println("raw2");
				grp.setColor(getAttribute(20));
				grp.fillRect(lytX, lytY, lytW, b);
			}
			grp.setClip(lytX, lytY, lytW, b);
			if (BounceGame.drawUIGraphics(this, 3, lytX, lytY, lytW, b)) {
				grp.setClip(lytX, lytY, lytW, b);
				int c4 = getAttribute(8);
				int a2 = this.titleImageId != -1 ? GameRuntime.getImageMapParam(this.titleImageId, 0) : 0;
				int i2 = this.titleLabel != null ? this.titleLabel.textBlockWidth : 0;
				int i3 = lytW - (c4 << 1);
				int i4 = getAttribute(1) != 8 ? i3 - a2 : i3;
				if (this.titleImageId != -1) {
					switch (getAttribute(1)) {
						case 4:
							i = (i3 - a2) + c4;
							break;
						case 8:
							i = ((i3 - a2) >> 1) + c4;
							break;
						default:
							i = c4;
							break;
					}
					GameRuntime.drawImageResAnchored(i + lytX, getAttribute(TITLE_PADDING_TOP) + lytY, this.titleImageId, 20);
				}
				if (this.titleLabel != null) {
					switch (getAttribute(1)) {
						case 0:
							c4 += a2;
							break;
						case 4:
							c4 += Math.max(i4 - i2, 0);
							break;
						case 8:
							c4 += Math.max(i3 - i2, 0) >> 1;
							break;
					}
					int i5 = 0;
					if (lytW < GameRuntime.currentWidth) {
						i5 = ((GameRuntime.currentWidth - lytW) - (GameRuntime.isScreenLandscape() ? GameRuntime.getSoftkeyBarWidth() : 0)) >> 1;
					}
					grp.setClip(i5 + (c4 - 1), lytY - 1, i4 + 2, b + 2);
					this.titleLabel.draw((lytX + c4) - this.titleXScroll,
							getAttribute(TITLE_PADDING_TOP) + lytY,
							getAttribute(FONT_TEXT_COLOR),
							getAttribute(FONT_SHADOW_COLOR)
					);
				}
			}
		}
		if (getAttribute(SOFTKEY_BAR) == 16) {
			//softkey bar
			int softkeyBarHeight = GameRuntime.getSoftkeyBarHeight();
			int softkeyBarWidth = GameRuntime.getSoftkeyBarWidth();
			grp.setClip(0, 0, GameRuntime.currentWidth, GameRuntime.currentHeight);
			grp.setColor(getAttribute(SOFTKEY_BAR_COLOR));
			switch (GameRuntime.getScreenOrientationFromSoftkeys()) {
				case GameRuntime.SCREEN_ORIENTATION_RPORTRAIT:
					grp.fillRect(0, 0, GameRuntime.currentWidth, softkeyBarHeight);
					break;
				case GameRuntime.SCREEN_ORIENTATION_PORTRAIT:
					grp.fillRect(0, GameRuntime.currentHeight - softkeyBarHeight, GameRuntime.currentWidth, softkeyBarHeight);
					break;
				case GameRuntime.SCREEN_ORIENTATION_RLANDSCAPE:
					grp.fillRect(0, 0, softkeyBarWidth, GameRuntime.currentHeight);
					break;
				case GameRuntime.SCREEN_ORIENTATION_LANDSCAPE:
					grp.fillRect(GameRuntime.currentWidth - softkeyBarWidth, 0, GameRuntime.currentWidth, GameRuntime.currentHeight);
					break;
			}
		}
		grp.setClip(lytX, lytY + b, lytW, lytH - b);
		BounceGame.drawUIGraphics(this, 4, lytX, lytY + b, lytW, lytH - b);
		int contentX = lytX + getAttribute(MARGIN_LEFT);
		int contentY = lytY + b + getAttribute(MARGIN_TOP);
		int focusWidth = getFocusWidth();
		int focusHeight = getFocusHeight();
		int d2 = getTotalHeight();
		int b4 = getLayoutVDim(5);
		grp.setClip(contentX, contentY, focusWidth, b4);
		BounceGame.drawUIGraphics(this, 5, contentX, contentY, focusWidth, b4);
		grp.setClip(contentX, contentY, focusWidth, b4);
		if (this.focusStartY > 0) {
			grp.setClip(contentX, contentY, focusWidth, getLayoutVDim(6));
			if (BounceGame.drawUIGraphics(this, 6, contentX, contentY, focusWidth, getLayoutVDim(6))) {
				//up arrow
				GameRuntime.drawImageRes(GameRuntime.getImageMapParam(151, 2) + contentX + ((focusWidth - GameRuntime.getImageMapParam(151, 0)) >> 1), GameRuntime.getImageMapParam(151, 3) + contentY, 151);
			}
		}
		int b5 = focusHeight < d2 ? contentY + getLayoutVDim(6) : contentY;
		if (this.focusStartY < d2 - focusHeight) {
			grp.setClip(contentX, b5 + focusHeight, focusWidth, getLayoutVDim(7));
			if (BounceGame.drawUIGraphics(this, 7, contentX, b5 + focusHeight, focusWidth, getLayoutVDim(7))) {
				//down arrow
				GameRuntime.drawImageRes(GameRuntime.getImageMapParam(150, 2) + contentX + ((focusWidth - GameRuntime.getImageMapParam(150, 0)) >> 1), b5 + focusHeight + 1 + GameRuntime.getImageMapParam(150, 3), 150);
			}
		}
		int selectedIdx = this.selectedElemIdx;
		if (selectedIdx != -1 && ((UIElement) this.elements.elementAt(selectedIdx)).getAttribute(UIElement.SELECTION_ARROWS) == 2048) {
			UIElement selectedElem = (UIElement) this.elements.elementAt(selectedIdx);
			int labelX = contentX + getElemXAnchor(selectedElem, focusWidth);
			int labelY = (getElemStartY(selectedIdx) + b5) - this.focusStartY;
			int labelW = selectedElem.getWidth();
			int labelH = selectedElem.getHeight();
			grp.setClip(labelX, labelY, labelW, labelH);
			if (BounceGame.drawUIGraphics(this, 9, labelX, labelY, labelW, labelH)) {
				grp.setColor(selectedElem.getAttribute(UIElement.COLOR1));
				grp.fillRect(labelX, labelY, labelW, labelH);
				grp.setColor(selectedElem.getAttribute(UIElement.COLOR2));
				grp.drawRect(labelX, labelY, labelW - 1, labelH - 1);
			}
		}
		for (i = 0; i < this.elements.size(); ++i) {
			final UIElement elem = (UIElement) this.elements.elementAt(i);
			final int startY = getElemStartY(i);
			if (startY > this.focusStartY + focusHeight) { //element out of focus
				break;
			}
			final int elemHeight = elem.getHeight();
			if (startY + elemHeight >= this.focusStartY) {
				final int elemX = contentX + getElemXAnchor(elem, focusWidth);
				final int elemY = b5 + startY - this.focusStartY;
				final int elemWidth = elem.getWidth();
				grp.setClip(contentX, b5, focusWidth, focusHeight);
				GameRuntime.setChildClip(elemX, elemY, elemWidth, elemHeight, f118e);
				if (BounceGame.drawUIGraphics(this, 8, elemX, elemY, elemWidth, elemHeight)) {
					if (elem.label != null) {
						elem.label.draw(
								elemX + elem.getAttribute(UIElement.MARGIN_LEFT),
								elemY + elem.getAttribute(UIElement.MARGIN_TOP),
								elem.getColor(i == this.selectedElemIdx),
								elem.getAttribute(UIElement.FONT_SHADOW_COLOR)
						);
					}
				}
			}
		}

		if (selectedIdx != -1 && ((UIElement) this.elements.elementAt(selectedIdx)).getAttribute(UIElement.SELECTION_ARROWS) == 2048) {
			UIElement elem = (UIElement) this.elements.elementAt(selectedIdx);
			int x = contentX + getElemXAnchor(elem, focusWidth);
			int y = b5 + this.getElemStartY(selectedIdx) - this.focusStartY;
			grp.setClip(x, y, elem.getWidth(), elem.getHeight());
			//selected item arrows
			BounceGame.drawUIGraphics(this, 10, x, y, elem.getWidth(), elem.getHeight());
		}
	}

	/* renamed from: d */
	public final void loadFromResource(int resId) {
		byte[] layoutData = (byte[]) GameRuntime.getLoadedResData(resId);
		if (layoutData != null) {
			try {
				DataInputStream dis = new DataInputStream(new ByteArrayInputStream(layoutData));
				this.elemDefaultAttributes = readAttributesFromStream(dis, ELEMENT_FLAG_MASKS, 15, 22);
				this.layoutAttributes = readAttributesFromStream(dis, LAYOUT_FLAG_MASKS, 20, 26);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/* renamed from: a */
	private static int[] readAttributesFromStream(DataInputStream dis, int[] flagMasks, int regularAttrEnd, int colorAttrEnd) throws IOException {
		int existingParamMask = dis.readInt();
		int flagCount = flagMasks.length;
		if (existingParamMask == 0) {
			return null;
		}
		int clzIndex = flagCount;
		int paramCount = 0;
		while ((existingParamMask >>> (clzIndex - 1)) != 0) {
			paramCount = clzIndex - flagCount;
			clzIndex++;
		}
		int[] result = new int[(paramCount + 2)];
		result[0] = existingParamMask;
		result[1] = 0;
		if (((0xFFFFFFFF >>> (32 - flagCount)) & existingParamMask) != 0) {
			result[1] = dis.readInt();
		}
		for (int paramIdx = 0; paramIdx < paramCount; paramIdx++) {
			if ((result[0] & (1 << (paramIdx + flagCount))) != 0) {
				result[paramIdx + 2] = dis.readShort();
			}
		}

		for (int colorIdx = (regularAttrEnd + 2) - flagCount; colorIdx <= (colorAttrEnd + 2) - flagCount && colorIdx < result.length; colorIdx++) {
			int colorRgb555 = result[colorIdx];
			result[colorIdx] = ((colorRgb555 & 0x7C00) << 9) | 0 | ((colorRgb555 & 0x3E0) << 6) | ((colorRgb555 & 31) << 3);
		}
		return result;
	}
}
