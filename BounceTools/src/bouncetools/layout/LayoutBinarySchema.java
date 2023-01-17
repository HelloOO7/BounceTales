package bouncetools.layout;

import java.util.Objects;
import xstandard.text.FormattingUtils;
import xstandard.util.ParsingUtils;

public class LayoutBinarySchema {

	public static final LayoutBinarySchema ELEMENT = new LayoutBinarySchema(
			new Parameter("FontShadowType", ParameterType.SHADOW),
			new Parameter("TextAlignment", ParameterType.ALIGNMENT),
			new Parameter("IconAlignment", ParameterType.ALIGNMENT),
			new Parameter("Flag3", ParameterType.FLAG),
			new Parameter("Flag4", ParameterType.FLAG),
			new Parameter("AutoWidth", ParameterType.FLAG),
			new Parameter("HorizontalAnchor", ParameterType.ALIGNMENT),
			new Parameter("ShowSelectionArrows", ParameterType.FLAG),
			new Parameter("InheritWidth", ParameterType.FLAG),
			new Parameter("Font", ParameterType.INT),
			new Parameter("Width", ParameterType.INT),
			new Parameter("MarginTop", ParameterType.INT),
			new Parameter("MarginBottom", ParameterType.INT),
			new Parameter("MarginLeft", ParameterType.INT),
			new Parameter("MarginRight", ParameterType.INT),
			new Parameter("FontTextColor", ParameterType.COLOR),
			new Parameter("FontShadowColor", ParameterType.COLOR),
			new Parameter("Color17", ParameterType.COLOR),
			new Parameter("FontTextColorSelected", ParameterType.COLOR),
			new Parameter("FontTextColorDisabled", ParameterType.COLOR),
			new Parameter("FontTextColorSelectedDisabled", ParameterType.COLOR),
			new Parameter("SelectionBackgroundColor", ParameterType.COLOR),
			new Parameter("SelectionBorderColor", ParameterType.COLOR)
	);

	public static final LayoutBinarySchema LAYOUT = new LayoutBinarySchema(
			new Parameter("ElementAlignment", ParameterType.ALIGNMENT),
			new Parameter("TitleAlignment", ParameterType.ALIGNMENT),
			new Parameter("EnableSoftkeyBar", ParameterType.FLAG),
			new Parameter("PackHeight", ParameterType.FLAG),
			new Parameter("AnchorCenter", ParameterType.FLAG),
			new Parameter("ScrollWraparound", ParameterType.FLAG),
			new Parameter("TittlePaddingTop", ParameterType.INT),
			new Parameter("TittlePaddingBottom", ParameterType.INT),
			new Parameter("TittlePaddingSide", ParameterType.INT),
			new Parameter("OffsetX", ParameterType.INT),
			new Parameter("OffsetY", ParameterType.INT),
			new Parameter("Width", ParameterType.INT),
			new Parameter("Height", ParameterType.INT),
			new Parameter("Font", ParameterType.INT),
			new Parameter("BlockIncrement", ParameterType.INT),
			new Parameter("MarginLeft", ParameterType.INT),
			new Parameter("MarginRight", ParameterType.INT),
			new Parameter("MarginTop", ParameterType.INT),
			new Parameter("MarginBottom", ParameterType.INT),
			new Parameter("VerticalSpacing", ParameterType.INT),
			new Parameter("ContentPaneColor", ParameterType.COLOR),
			new Parameter("SoftkeyBarColor", ParameterType.COLOR),
			new Parameter("BackgroundColor", ParameterType.COLOR),
			new Parameter("BorderColor", ParameterType.COLOR),
			new Parameter("FontTextColor", ParameterType.COLOR),
			new Parameter("FontShadowColor", ParameterType.COLOR),
			new Parameter("Color26", ParameterType.COLOR)
	);

	public final Parameter[] params;

	private LayoutBinarySchema(Parameter... params) {
		this.params = params;
	}

	public Parameter getParameter(String name) {
		for (Parameter p : params) {
			if (Objects.equals(name, p.name)) {
				return p;
			}
		}
		return null;
	}

	public int countByType(ParameterType t) {
		int count = 0;
		for (Parameter p : params) {
			if (p.type == t) {
				count++;
			}
		}
		return count;
	}

	public static class Parameter {

		public final String name;
		public final ParameterType type;

		public Parameter(String name, ParameterType type) {
			this.name = name;
			this.type = type;
		}

		public int valueFromString(String strValue) {
			switch (type) {
				case ALIGNMENT:
					return AlignmentEnum.valueOf(strValue).ordinal();
				case SHADOW:
					return ShadowEnum.valueOf(strValue).ordinal();
				case FLAG:
					return Objects.equals(strValue, "true") ? 1 : 0;
				case INT:
					return ParsingUtils.parseBasedInt(strValue);
				case COLOR:
					return Integer.parseInt(strValue.substring(1), 16);
			}
			return 0;
		}

		public String valueToString(int rawValue) {
			switch (type) {
				case ALIGNMENT:
					return LayoutBinarySchema.AlignmentEnum.values()[rawValue].name();
				case SHADOW:
					return LayoutBinarySchema.ShadowEnum.values()[rawValue].name();
				case FLAG:
					return (rawValue != 0) ? "true" : "false";
				case INT:
					return Integer.toString(rawValue);
				case COLOR:
					return "#" + FormattingUtils.getStrWithLeadingZeros(6, Integer.toHexString(rawValue));
			}
			return "";
		}
	}

	public static enum ParameterType {
		FLAG,
		ALIGNMENT,
		SHADOW,
		INT,
		COLOR
	}

	public static enum AlignmentEnum {
		LEFT,
		RIGHT,
		CENTER
	}

	public static enum ShadowEnum {
		NONE,
		CAST,
		DROP
	}
}
