package bouncetools.layout;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import xstandard.formats.yaml.YamlNode;
import xstandard.io.base.iface.DataOutputEx;

public class LayoutValues {

	private final LayoutBinarySchema schema;

	public List<Value> values = new ArrayList<>();

	public LayoutValues(DataInput dis, LayoutBinarySchema schema) throws IOException {
		this.schema = schema;
		int paramMask = dis.readInt();
		int flagBits = 0;

		int bitfieldSize
				= schema.countByType(LayoutBinarySchema.ParameterType.FLAG)
				+ ((schema.countByType(LayoutBinarySchema.ParameterType.ALIGNMENT) + schema.countByType(LayoutBinarySchema.ParameterType.SHADOW)) * 2);
		int bitfieldMask = (1 << bitfieldSize) - 1;
		if (bitfieldSize != 0 && (paramMask & bitfieldMask) != 0) {
			flagBits = dis.readInt();
		}

		int flagIndex = 0;
		for (LayoutBinarySchema.Parameter p : schema.params) {
			int value = 0;
			switch (p.type) {
				case FLAG:
					value = ((flagBits & (1 << flagIndex)) != 0) ? 1 : 0;
					flagIndex++;
					break;
				case ALIGNMENT:
				case SHADOW:
					value = ((flagBits >> flagIndex) & 0b11);
					flagIndex += 2;
					break;
				case INT:
					value = dis.readShort();
					break;
				case COLOR:
					int colorRgb555 = dis.readShort();
					value = ((colorRgb555 & 0x7C00) << 9) | ((colorRgb555 & 0x3E0) << 6) | ((colorRgb555 & 31) << 3);
					break;
			}
			values.add(new Value(p, value));
		}
	}
	
	public LayoutValues(YamlNode src, LayoutBinarySchema schema) {
		this.schema = schema;
		for (YamlNode node : src.children) {
			LayoutBinarySchema.Parameter p = schema.getParameter(node.getChildValue("Name"));
			if (p != null) {
				YamlNode valNode = node.getChildByName("Value");
				int rawValue;
				switch (p.type) {
					case COLOR:
						rawValue = (valNode.getChildIntValue("R") << 16) | (valNode.getChildIntValue("G") << 8) | (valNode.getChildIntValue("B") << 0);
						break;
					default:
						rawValue = p.valueFromString(valNode.getValue());
						break;
				}
				values.add(new Value(p, rawValue));
			}
		}
	}

	public void serialize(YamlNode dest) {
		for (Value v : values) {
			YamlNode ch = dest.addChildListElem();
			ch.addChild("Name", v.param.name);
			switch (v.param.type) {
				case COLOR:
					YamlNode colorValue = ch.addChildKey("Value");
					colorValue.addChild("R", (v.rawValue >> 16) & 0xFF);
					colorValue.addChild("G", (v.rawValue >> 8) & 0xFF);
					colorValue.addChild("B", (v.rawValue >> 0) & 0xFF);
					break;
				default:
					ch.addChild("Value", v.valueToString());
					break;
			}
		}
	}

	public void writeToBinary(DataOutputEx out) throws IOException {
		int existMask = 0;

		int paramBit = 1;
		for (LayoutBinarySchema.Parameter p : schema.params) {
			if (valueExists(p.name)) {
				existMask |= paramBit;
			}
			paramBit <<= 1;
		}

		out.writeInt(existMask);

		int bitfield = 0;
		int bitfieldIndex = 0;
		boolean existAnyBitfield = false;
		for (LayoutBinarySchema.Parameter p : schema.params) {
			if (valueExists(p.name)) {
				existAnyBitfield = true;
				int raw = getValue(p.name).rawValue;
				if (p.type == LayoutBinarySchema.ParameterType.FLAG) {
					bitfield |= (raw & 1) << bitfieldIndex;
				} else if (p.type == LayoutBinarySchema.ParameterType.ALIGNMENT || p.type == LayoutBinarySchema.ParameterType.SHADOW) {
					bitfield |= (raw & 3) << bitfieldIndex;
				}
			}
			if (p.type == LayoutBinarySchema.ParameterType.FLAG) {
				bitfieldIndex++;
			} else if (p.type == LayoutBinarySchema.ParameterType.ALIGNMENT || p.type == LayoutBinarySchema.ParameterType.SHADOW) {
				bitfieldIndex += 2;
			}
		}
		if (existAnyBitfield) {
			out.writeInt(bitfield);
		}

		for (LayoutBinarySchema.Parameter p : schema.params) {
			if (valueExists(p.name)) {
				int raw = getValue(p.name).rawValue;
				if (p.type == LayoutBinarySchema.ParameterType.INT) {
					out.writeShort(raw);
				} else if (p.type == LayoutBinarySchema.ParameterType.COLOR) {
					out.writeShort( //RGB888 to RGB555
							(raw & 0xF80000) >> 9
							| ((raw & 0xF800) >> 6)
							| ((raw & 0xF8) >> 3)
					);
				}
			}
		}
	}

	public boolean valueExists(String name) {
		return getValue(name) != null;
	}

	public Value getValue(String name) {
		for (Value v : values) {
			if (Objects.equals(name, v.param.name)) {
				return v;
			}
		}
		return null;
	}

	public static class Value {

		public final LayoutBinarySchema.Parameter param;
		public int rawValue;

		public Value(LayoutBinarySchema.Parameter schemaParam, int value) {
			this.param = schemaParam;
			this.rawValue = value;
		}

		public String valueToString() {
			return param.valueToString(rawValue);
		}
	}

	private static int[] readAttributesFromStream(DataInput dis, int[] flagMasks, int regularAttrEnd, int colorAttrEnd) throws IOException {
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
