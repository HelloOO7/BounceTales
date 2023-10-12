package bouncetales;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author vipaol
 */
public class GraphicsUtils {

        public static boolean isDirectGraphicsSupported = false;
        public static boolean enableWaterNoDirectGraphicsWorkaround = true;

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
                int alpha = argbColor & 0xff000000;

                int w = g.getClipWidth();
                int h = g.getClipHeight();

                int x0 = w;
                int y0 = h;
                int xMax = 0;
                int yMax = 0;

                for (int i = xOffset; i < nPoints + xOffset; i++) {
                        x0 = Math.min(x0, xPoints[i]);
                        xMax = Math.max(xMax, xPoints[i]);
                }

                for (int i = yOffset; i < nPoints + yOffset; i++) {
                        y0 = Math.min(y0, yPoints[i]);
                        yMax = Math.max(yMax, yPoints[i]);
                }

                x0 = Math.max(x0, 0);
                y0 = Math.max(y0, 0);
                xMax = Math.min(xMax, w);
                yMax = Math.min(yMax, h);

                w = xMax - x0;
                h = yMax - y0;

                if (w <= 0 || h <= 0) {
                        return;
                }

                Image buffer = Image.createImage(w, h);
                Graphics bufGraphics = buffer.getGraphics();

                int transparencyMarker = bufGraphics.getDisplayColor(0x123456);
                bufGraphics.setColor(transparencyMarker);
                bufGraphics.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

                bufGraphics.setColor(argbColor);
                for (int i = 1; i < nPoints - 1; i++) {
                        int ix = i + xOffset;
                        int iy = i + yOffset;
                        bufGraphics.fillTriangle(
                                        xPoints[xOffset] - x0, yPoints[yOffset] - y0,
                                        xPoints[ix] - x0, yPoints[iy] - y0,
                                        xPoints[ix+1] - x0, yPoints[iy+1] - y0
                        );
                }

                int[] rgbData = new int[w*h];
                buffer.getRGB(rgbData, 0, w, 0, 0, w, h);
                applyAlpha(rgbData, alpha, transparencyMarker);

                g.drawRGB(rgbData, 0, w, x0, y0, w, h, true);
        }

        public static void applyAlpha(int[] rgbData, int alpha, int transparentBgColor) {
                int antialpha = 0xff000000 - alpha;
                for (int i = 0; i < rgbData.length; i++) {
                        if ((rgbData[i] & 0x00ffffff) == transparentBgColor) {
                                rgbData[i] = 0x00000000;
                        } else {
                                rgbData[i] -= antialpha;
                        }
                }
        }
}
