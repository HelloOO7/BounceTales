package bouncewin32;

import jademula.Jademula;
import javax.microedition.lcdui.Canvas;

public class BounceWin32 {

	public static void main(String[] args) {
		Canvas.LIMIT_FPS = false; //uncapped framerate
		Jademula.main(args);
		Jademula.loadMyself();
	}
}
