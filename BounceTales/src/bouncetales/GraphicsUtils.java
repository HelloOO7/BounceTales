package bouncetales;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author vipaol
 */
public class GraphicsUtils {

        public static boolean isDirectGraphicsSupported = false;

        // Too laggy, so it is disabled by default
        public static boolean enableWaterNoDirectGraphicsWorkaround = false;

        public static void init() {
                System.out.println("Testing nokiaui support...");
                try {
                        Class.forName("com.nokia.mid.ui.DirectGraphics");
                        System.out.println("DirectGraphics is supported");
                        isDirectGraphicsSupported = true;
                } catch (ClassNotFoundException ex) {
                        System.out.println("no DirectGraphics");
                        isDirectGraphicsSupported = false;
                }
        }

        public static void fillPolygonARGB(Graphics g, int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor, boolean force) {
                if (isDirectGraphicsSupported) {
                        com.nokia.mid.ui.DirectUtils.getDirectGraphics(g).fillPolygon(xPoints, xOffset, yPoints, yOffset, nPoints, argbColor);
                } else {
                        if (enableWaterNoDirectGraphicsWorkaround || force) {
                                fillPolygonNoDirectGraphics(g, xPoints, xOffset, yPoints, yOffset, nPoints, argbColor);
                        }
                }
        }

        // A workaround for drawing translucent polygons (pause menu and softkey bar bg, water) on phones that don't support nokiaui
        public static void fillPolygonNoDirectGraphics(Graphics g, int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor) {
                int w = g.getClipWidth();
                int h = g.getClipHeight();
                int rgbColor = argbColor % 0x01000000;
                int alpha = argbColor - rgbColor;
                int prevColor = g.getColor();
                g.setColor(rgbColor);
                Image buffer = Image.createImage(w, h);
                Graphics bufGraphics = buffer.getGraphics();
                bufGraphics.setColor(rgbColor);
                for (int i = 1; i < nPoints - 1; i++) {
                        int ix = i + xOffset;
                        int iy = i + yOffset;
                        bufGraphics.fillTriangle(xPoints[xOffset], yPoints[yOffset], xPoints[ix], yPoints[iy], xPoints[ix+1], yPoints[iy+1]);
                }
                int[] rgbData = new int[w*h];
                buffer.getRGB(rgbData, 0, w, 0, 0, w, h);
                applyAlpha(rgbData, alpha);
                g.setColor(prevColor);
                g.drawRGB(rgbData, 0, w, 0, 0, g.getClipWidth(), g.getClipHeight(), true);
        }

        public static void applyAlpha(int[] rgbData, int alpha) {
                int antialpha = 0xff000000 - alpha;
                for (int i = 0; i < rgbData.length; i++) {
                        if (rgbData[i] == 0xffffffff) {
                                rgbData[i] = 0x00000000;
                        } else {
                                rgbData[i] -= antialpha;
                        }
                }
        }
}
