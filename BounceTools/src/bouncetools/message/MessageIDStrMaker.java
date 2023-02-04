package bouncetools.message;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageIDStrMaker {

	public static String[] getNames(short[] intMapping) {
		Map<Integer, String> names = new HashMap<>();
		for (Field f : MessageID.class.getDeclaredFields()) {
			if (f.getType() == Integer.TYPE) {
				try {
					names.put(f.getInt(null), f.getName());
				} catch (IllegalArgumentException | IllegalAccessException ex) {
					Logger.getLogger(MessageIDStrMaker.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		boolean is2025 = intMapping == MessageMappings.MESSAGE_MAP_2_0_25;
		String[] map = new String[MessageMappings.MESSAGE_MAP_2_0_3.length + (is2025 ? 1 : 0)];
		for (int in = 0, out = 0; out < map.length; in++, out++) {
			if (out == 12 && is2025) {
				map[out++] = "UI_MORE_GAMES";
			}
			map[out] = names.get((int) MessageMappings.MESSAGE_MAP_2_0_3[in]);
		}
		return map;
	}
}
