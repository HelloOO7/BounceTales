package bouncetales;

/* renamed from: f */
public final class UIElement {

	public static final int FONT_SHADOW_TYPE = 0;
	public static final int TEXT_ALIGNMENT = 1;
	public static final int ICON_ALIGNMENT = 2;
	public static final int FLAG_3 = 3;
	public static final int FLAG_4 = 4;
	public static final int AUTO_WIDTH = 5;
	public static final int ANCHOR_H = 6;
	public static final int SELECTION_ARROWS = 7;
	public static final int INHERIT_WIDTH = 8;
	public static final int FONT = 9;
	public static final int FIXED_WIDTH = 10;
	public static final int MARGIN_TOP = 11;
	public static final int MARGIN_BOTTOM = 12;
	public static final int MARGIN_LEFT = 13;
	public static final int MARGIN_RIGHT = 14;
	public static final int FONT_TEXT_COLOR = 15;
	public static final int FONT_SHADOW_COLOR = 16;
	public static final int COLOR_17 = 17;
	public static final int FONT_TEXT_COLOR_SELECTED = 18;
	public static final int FONT_TEXT_COLOR_DISABLED = 19;
	public static final int FONT_TEXT_COLOR_SELECTED_DISABLED = 20;
	public static final int SELECTION_BACKGROUND_COLOR = 21;
	public static final int SELECTION_BORDER_COLOR = 22;
	
	public static final int AUTO_WIDTH_BIT = 256;
	public static final int ANCHOR_H_BIT = 512;
	public static final int SELECTION_ARROWS_BIT = 1024;
	public static final int INHERIT_WIDTH_BIT = 2048;

	public UILayout parentUI = null; //renamed from: a

	public boolean isEnabled = true; //renamed from: a

	public TextLabel label; //renamed from: a
	public int action = -1; //renamed from: a
	private int[] attributes; //renamed from: a

	public String unused_f99a = null; //renamed from: a

	public UIElement() {
	}

	public UIElement(String str, int iconImageId, UILayout parentUI, int action) {
		this.parentUI = parentUI;
		this.action = action;
		setText(str, iconImageId);
	}

	/* renamed from: a */
	public final int getAttribute(int aid) {
		return (UILayout.attributeExists(aid, this.attributes) || this.parentUI == null)
				? UILayout.readAttribute(aid, this.attributes, 1)
				: UILayout.readAttribute(aid, this.parentUI.elemDefaultAttributes, 1);
	}

	/* renamed from: a */
	public final void setAttribute(int aid, int value) {
		this.attributes = UILayout.writeAttribute(aid, value, this.attributes, 1);
	}

	public final int getColor(boolean selected) {
		return getAttribute(
				isEnabled ? (selected ? FONT_TEXT_COLOR_SELECTED : FONT_TEXT_COLOR) : (selected ? FONT_TEXT_COLOR_SELECTED_DISABLED : FONT_TEXT_COLOR_DISABLED)
		);
	}

	/* renamed from: a */
	public final void setText(String str, int iconImageId) {
		String sanitizedStr = str == null ? "" : str;
		TextLabel lastLabel = this.label;
		this.label = null;
		this.label = lastLabel;
		int availWidth = getWidth() - (getAttribute(MARGIN_LEFT) + getAttribute(MARGIN_RIGHT));
		int flags = 0;
		for (int flagIdx = 0; flagIdx <= INHERIT_WIDTH; flagIdx++) {
			flags |= getAttribute(flagIdx);
		}
		this.label = new TextLabel(sanitizedStr, availWidth, getAttribute(FONT), flags, iconImageId);
	}

	/* renamed from: a */
	public final boolean hasAction() {
		return this.action != -1;
	}

	/* renamed from: a */
	public final int getWidth() {
		if (getAttribute(AUTO_WIDTH) == 256) {
			if (label != null) {
				return ((label.rawText == null || label.rawText.length() < 1)
						? (label.textBlockWidth - TextLabel.f0a)
						: label.textBlockWidth)
						+ getAttribute(MARGIN_LEFT) + getAttribute(MARGIN_RIGHT);
			}
			if (this.parentUI != null) {
				return this.parentUI.getFocusWidth();
			}
		} else if (getAttribute(INHERIT_WIDTH) != 4096) {
			return getAttribute(FIXED_WIDTH);
		} else {
			if (this.parentUI != null) {
				return this.parentUI.getFocusWidth();
			}
		}
		return 0;
	}

	/* renamed from: b */
	public final int getHeight() {
		return (label != null ? label.textBlockHeight : 0) + getAttribute(MARGIN_TOP) + getAttribute(MARGIN_BOTTOM);
	}
}
