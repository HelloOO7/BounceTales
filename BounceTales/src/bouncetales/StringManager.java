package bouncetales;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/* renamed from: q */
public final class StringManager {

	private static DataInputStream textReader = null; //renamed from: a

	private static String localeProperty = null; //renamed from: a

	private static StringManager mInstance = new StringManager(); //renamed from: a

	static {
		System.out.println("LocalizedData init...");
		boolean matchedPlatform = false;
		String platformProperty = System.getProperty("microedition.platform");
		StringBuffer sb = new StringBuffer();
		InputStream manifestStrm = StringManager.class.getResourceAsStream("/META-INF/MANIFEST.MF");
		if (manifestStrm != null) {
			while (true) {
				try {
					int read = manifestStrm.read();
					if (read < 0) {
						break;
					} else if (((char) read) == '\r') {
						continue;
					} else if (((char) read) != '\n') {
						sb.append((char) read);
					} else if (sb.toString().trim().startsWith("Nokia-Platform:")) {
						sb.append(readPropertyValue(manifestStrm));
						String nokiaPlatform = sb.toString().trim().substring("Nokia-Platform:".length());
						System.out.println("Check Nokia-Platform " + nokiaPlatform + " against " + platformProperty);
						Vector nokiaPlatforms = new Vector();
						while (true) {
							int indexOf = nokiaPlatform.indexOf("@");
							if (indexOf == -1) {
								break;
							}
							nokiaPlatforms.addElement(nokiaPlatform.substring(0, indexOf));
							nokiaPlatform = nokiaPlatform.substring(indexOf + 1, nokiaPlatform.length());
						}
						nokiaPlatforms.addElement(nokiaPlatform);
						for (int i = 0; i < nokiaPlatforms.size(); i++) {
							String plaf = (String) nokiaPlatforms.elementAt(i);
							if (checkNokiaPlatform(platformProperty, plaf.trim(), 0, 0)) {
								matchedPlatform = true;
								break;
							}
						}
						break;
					} else {
						sb.delete(0, sb.length());
					}
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}
		if (!matchedPlatform) {
			System.out.println("LocalizedData init failure!");
			System.exit(0);
		} else {
			System.out.println("LocalizedData init success.");
		}
	}

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
			textReader.skipBytes((textReader.readUnsignedShort() - (msgId * 2)) - 2);
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
			return "E";
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

	/* renamed from: a */
	private static StringBuffer readPropertyValue(final InputStream inputStream) {
		final StringBuffer sb = new StringBuffer();
		try {
			if ((char) inputStream.read() != ' ') {
				return sb;
			}
			char character;
			while ((character = (char) inputStream.read()) != -1) {
				if (character != '\r') {
					if (character == '\n') {
						sb.append((Object) readPropertyValue(inputStream));
						break;
					}
					sb.append(character);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return sb;
	}

	/* renamed from: a */
	private static boolean checkNokiaPlatform(String platform, String filter, int offsetPlatform, int offsetFilter) {
		while (true) {
			if (offsetPlatform == platform.length() && offsetFilter == filter.length()) {
				return true;
			}
			if (offsetPlatform != platform.length() && offsetFilter != filter.length()) {
				switch (filter.charAt(offsetFilter)) {
					case '*':
						if (offsetFilter != filter.length() - 1 && !checkNokiaPlatform(platform, filter, offsetPlatform, offsetFilter + 1)) {
							offsetPlatform++;
						} else {
							return true;
						}
						break;
					case '?':
						offsetPlatform++;
						offsetFilter++;
						break;
					default:
						if (platform.charAt(offsetPlatform) == filter.charAt(offsetFilter)) {
							offsetPlatform++;
							offsetFilter++;
						} else {
							return false;
						}
						break;
				}
			}
		}
	}
}
