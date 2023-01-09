package bouncetales;

import javax.microedition.midlet.MIDlet;

public class RMIDlet extends MIDlet {

	//@Override
	protected void startApp() {
		if (GameRuntime.mMidLet == null) {
			GameRuntime.mMidLet = this;
			GameRuntime.setState(GameState.INIT);
		} else {
			GameRuntime.setState(GameState.RUN);
		}
	}

	//@Override
	protected void destroyApp(boolean z) {
		GameRuntime.quit();
	}

	//@Override
	protected void pauseApp() {
		GameRuntime.setState(GameState.PAUSE);
		notifyPaused();
	}
}
