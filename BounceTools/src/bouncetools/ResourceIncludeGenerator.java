package bouncetools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import xstandard.fs.FSFile;
import xstandard.io.util.IndentedPrintStream;
import xstandard.text.FormattingUtils;

public class ResourceIncludeGenerator {

	public static void generate(FSFile dest, String packageName, String resMapName, Map<String, Short> resIDMap) throws IOException {
		dest.getParent().mkdirs();
		IndentedPrintStream out = new IndentedPrintStream(dest.getNativeOutputStream());

		if (packageName != null && !packageName.isEmpty()) {
			out.println("package " + packageName + ";");
		}
		out.println();
		out.println("public class " + dest.getNameWithoutExtension() + "{");
		out.println();
		out.incrementIndentLevel();

		out.println("public static final String RESMAP_FILENAME = \"/" + resMapName + "\";");
		out.println();

		List<Map.Entry<String, Short>> entriesSorted = new ArrayList<>(resIDMap.entrySet());
		entriesSorted.sort(Map.Entry.comparingByValue());

		for (Map.Entry<String, Short> e : entriesSorted) {
			out.println("public static final short " + FormattingUtils.getEnumlyString(e.getKey()) + " = " + e.getValue() + ";");
		}

		out.decrementIndentLevel();
		out.println("}");

		out.close();
	}
}
