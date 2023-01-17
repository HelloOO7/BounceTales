package bouncetales;

import bouncetales.ext.rsc.ImageMap;
import javax.microedition.lcdui.Graphics;

/* renamed from: b */
public final class TextLabel {

	/* renamed from: a */
	public static final int f0a = (GameRuntime.currentWidth >>> 5);

	/* renamed from: a */
	String rawText;

	/* renamed from: a */
	private boolean f2a;

	/* renamed from: a */
	private String[] lines;

	/* renamed from: b */
	public int textBlockWidth;

	/* renamed from: c */
	public int textBlockHeight;

	/* renamed from: d */
	private int iconImageId;

	/* renamed from: e */
	private int flags;

	/* renamed from: f */
	private int fontId;

	/* renamed from: g */
	private int shadowType;

	/* renamed from: h */
	private int f10h = 0;

	/* renamed from: i */
	private int f11i;

	/* renamed from: j */
	private int lineCount;

	/* renamed from: k */
	private int iconHeight;

	/* renamed from: l */
	private int iconWidth;

	static {
		char[] cArr = {'\n', ' ', '-'};
	}

	public TextLabel(String text, int maxWidth, int fontId, int flags, int iconImageId) {
		this.flags = flags;
		this.rawText = text;
		this.fontId = fontId;
		switch (this.flags & 3) {
			case 0:
				this.shadowType = 1;
				break;
			case 1:
				this.shadowType = 2;
				break;
			case 2:
				this.shadowType = 3;
				break;
		}
		if (iconImageId >= 0) {
			this.iconImageId = iconImageId;
			this.iconWidth = GameRuntime.getImageMapParam(iconImageId, ImageMap.PARAM_WIDTH) + f0a;
			this.iconHeight = GameRuntime.getImageMapParam(iconImageId, ImageMap.PARAM_HEIGHT);
		} else {
			this.iconImageId = -1;
			this.iconWidth = 0;
			this.iconHeight = 0;
		}
		this.f2a = maxWidth > this.iconWidth && (this.flags & 128) != 0 && (this.flags & 48) != 32;
		boolean dynamicWidth = (this.flags & 256) != 0;
		this.rawText = text == null ? "" : text;
		this.lines = GameRuntime.prepareStringLines(this.rawText, maxWidth, this.fontId, this.f2a, this.iconWidth, this.iconHeight, dynamicWidth);
		parseDrawParams(this.lines[0]);
	}

	/* renamed from: a */
	private void parseDrawParams(String str) {
		int[] gapIndices = new int[4];
		if (str != null) {
			int i = 0;
			for (int scanIdx = 0; scanIdx < str.length(); scanIdx++) {
				if (str.charAt(scanIdx) == ' ') {
					gapIndices[i] = scanIdx;
					i++;
				}
			}
			try {
				this.lineCount = Integer.parseInt(str.substring(0, gapIndices[0]));
			} catch (NumberFormatException e) {

			}
			try {
				this.f10h = Integer.parseInt(str.substring(gapIndices[0] + 1, gapIndices[1]));
			} catch (NumberFormatException e) {
				this.f10h = 0;
			}
			try {
				this.f11i = Integer.parseInt(str.substring(gapIndices[1] + 1, gapIndices[2]));
			} catch (NumberFormatException e) {
				this.f11i = 0;
			}
			try {
				this.textBlockWidth = Integer.parseInt(str.substring(gapIndices[2] + 1, gapIndices[3]));
			} catch (NumberFormatException e) {
				this.textBlockWidth = 0;
			}
			try {
				this.textBlockHeight = Integer.parseInt(str.substring(gapIndices[3] + 1, str.length()));
			} catch (NumberFormatException e5) {
				this.textBlockHeight = 0;
			}
		} else {
			this.lineCount = 0;
			this.f10h = 0;
			this.f11i = 0;
			this.textBlockWidth = 0;
			this.textBlockHeight = 0;
		}
	}

	/* renamed from: a */
	public final void draw(int startX, int startY, int textColor, int shadowColor) {
		int iconXOffset;
		int iconAnchor;
		int i7;
		int i8;
		int i9;
		int i10 = this.lineCount;
		GameRuntime.setTextStyle(this.fontId, this.shadowType);
		GameRuntime.setTextColor(0, textColor);
		GameRuntime.setTextColor(1, shadowColor);
		int lineCount = i10 > this.lineCount ? this.lineCount : i10;
		if ((this.flags & 48) == 32) { //icon centered in label
			iconXOffset = this.textBlockWidth / 2;
			iconAnchor = Graphics.HCENTER | Graphics.TOP;
		} else {
			int i12 = (this.flags & 12) == 8 ? (this.textBlockWidth - this.f11i) / 2 : 0;
			if ((this.flags & 48) == 0) {
				iconAnchor = Graphics.TOP | Graphics.LEFT;
				iconXOffset = i12;
			} else {
				iconXOffset = this.textBlockWidth - i12;
				iconAnchor = Graphics.TOP | Graphics.RIGHT;
			}
		}
		boolean z = (this.flags & 48) == 0;
		int i13 = 0;
		int i14 = 0;
		switch (this.flags & 12) {
			case 0:
				i14 = 20;
				i13 = 20;
				if (!z) {
					i7 = 0;
					i8 = 0;
					break;
				} else {
					i7 = 0;
					i8 = this.iconWidth;
					break;
				}
			case 4:
				i14 = 24;
				int i15 = this.textBlockWidth;
				i13 = 24;
				if (!z) {
					i7 = i15;
					i8 = this.textBlockWidth - this.iconWidth;
					break;
				} else {
					i7 = i15;
					i8 = this.textBlockWidth;
					break;
				}
			case 8:
				i14 = 17;
				int i16 = this.textBlockWidth / 2;
				if (!z) {
					i13 = 24;
					i7 = i16;
					i8 = (this.textBlockWidth - ((this.textBlockWidth - this.f11i) / 2)) - this.iconWidth;
					break;
				} else {
					i13 = 20;
					i7 = i16;
					i8 = ((this.textBlockWidth - this.f11i) / 2) + this.iconWidth;
					break;
				}
			default:
				i7 = 0;
				i8 = 0;
				break;
		}
		if (this.iconImageId >= 0) {
			GameRuntime.drawImageResAnchored(startX + iconXOffset, startY, this.iconImageId, iconAnchor);
		}
		if (!this.f2a) {
			startY += this.iconHeight;
		}
		int yoffs = GameRuntime.getFontHeight(this.fontId) * 0;
		int lineIndex = 0;
		while (lineIndex < lineCount) {
			if (this.lines[lineIndex + 1] != null) {
				if (lineIndex < this.f10h) {
					GameRuntime.drawText(this.lines[lineIndex + 1], 0, this.lines[lineIndex + 1].length(), startX + i8, startY + yoffs, i13);
				} else {
					GameRuntime.drawText(this.lines[lineIndex + 1], 0, this.lines[lineIndex + 1].length(), startX + i7, startY + yoffs, i14);
				}
				yoffs += GameRuntime.getFontHeight(this.fontId);
			}
			lineIndex++;
		}
	}
}
