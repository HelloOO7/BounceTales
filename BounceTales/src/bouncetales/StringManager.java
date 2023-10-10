package bouncetales;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/* renamed from: q */
public final class StringManager {

	private static StringManager mInstance = new StringManager(); //renamed from: a
	private static String localeProperty = null; //renamed from: a
	
	private static DataInputStream textReader = null; //renamed from: a

	private StringManager() {
	}

	/* renamed from: a */
	public static String getMessage(int msgId) {
		return getMessage(msgId, null);
	}

	public static String getMessage(int msgId, int iparam) {
		return getMessage(msgId, new Integer(iparam));
	}

	private static final String[] tempParam = new String[1];

	public static String getMessage(int msgId, Object param) {
		synchronized (tempParam) {
			tempParam[0] = String.valueOf(param);
			return getMessage(msgId, tempParam);
		}
	}

	/* renamed from: a */
	public static synchronized String getMessage(int msgId, String[] variables) {
		if (localeProperty == null) {
			localeProperty = System.getProperty("microedition.locale");
		}
		int offset = 0;
		try {
			if (mInstance == null) {
				mInstance = new StringManager();
			}
			if (textReader == null) {
				InputStream langRscStrm = mInstance.getClass().getResourceAsStream("/lang." + localeProperty);
				if (langRscStrm == null) {
					langRscStrm = mInstance.getClass().getResourceAsStream("/lang.xx");
				}
				if (langRscStrm == null) {
					return "X";
				} else {
					DataInputStream dis = new DataInputStream(langRscStrm);
					textReader = dis;
					dis.mark(512);
				}
			}
			textReader.skipBytes(msgId * 2);
			//skip to actual message offset
			offset = textReader.readUnsignedShort();
			textReader.skipBytes((offset - (msgId * 2)) - 2);
			String message = textReader.readUTF();
			if (!textReader.markSupported()) {
				textReader.close();
				textReader = null;
			} else {
				try {
					textReader.reset();
				} catch (IOException e) {
					e.printStackTrace();
					textReader.close();
					textReader = null;
				}
			}
			if (variables != null) {
				if (variables.length == 1) {
					message = findAndReplace(message, "%U", variables[0]);
				} else {
					for (int i = 0; i < variables.length; i++) {
						message = findAndReplace(message, "%" + i + "U", variables[i]);
					}
				}
			}
			return message;
		} catch (IOException e2) {
			e2.printStackTrace();
			textReader = null;
			//return "E"; //in 2.0.3
			return "E:" + offset; //since 2.0.25
		}
	}

	/* renamed from: a */
	private static String findAndReplace(String str, String toFind, String str3) {
		int indexOf = str.indexOf(toFind);
		while (indexOf >= 0) {
			str = str.substring(0, indexOf) + str3 + str.substring(toFind.length() + indexOf);
			indexOf = str.indexOf(toFind);
		}
		return str;
	}
}
