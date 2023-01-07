package bouncetales;

import com.nokia.mid.ui.DirectGraphics;
import javax.microedition.lcdui.Graphics;

/* renamed from: e */
public final class TrampolineObject extends GameObject {

	public static final byte TYPEID = 8;

	//Parameters
	short imageId; //renamed from: a
	byte basePush; //renamed from: a

	//State
	boolean isJumpFinished; //renamed from: a

	float calcPush; //renamed from: a

	int period; //renamed from: b
	int progress; //renamed from: a
	private int animFrame; //renamed from: c

	BounceObject jumper; ////renamed from: a

	public TrampolineObject() {
		this.objType = TYPEID;
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final int readData(byte[] bArr, int dataPos) {
		dataPos = super.readData(bArr, dataPos);
		this.imageId = readShort(bArr, dataPos);
		dataPos += 2;
		this.basePush = bArr[dataPos++];
		this.progress = 0;
		this.period = 0;
		this.animFrame = 0;
		int levelType = BounceGame.getLevelType(BounceGame.currentLevel);
		switch (levelType) {
			case 0: //green hill zone
				imageId = 497;
				break;
			case 1: //spooky zone
				imageId = 509;
				break;
			case 2: //bonus zone
				imageId = 490;
				break;
		}
		return dataPos;
	}

	// p000.GameObject, p000.GameObject, p000.GameObject, p000.GameObject
	/* renamed from: a */
	//@Override
	public final void initialize() {
		this.bboxMinX = -LP32.FP32ToLP32(70f);
		this.bboxMaxX = LP32.FP32ToLP32(70f);
		this.bboxMinY = 0;
		this.bboxMaxY = LP32.FP32ToLP32(95f);
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final void draw(Graphics graphics, DirectGraphics directGraphics, Matrix rootMatrix) {
		loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
		Matrix.multMatrices(rootMatrix, GameObject.tmpObjMatrix, Matrix.temp);
		GameRuntime.drawAnimatedImageRes(Matrix.temp.translationX >> 16, Matrix.temp.translationY >> 16, this.imageId, this.animFrame);
		debugDraw(graphics, 0xFF00FF, rootMatrix);
	}

	private void releaseJumper() {
		if (jumper != null) {
			jumper.enablePhysics = true;
			jumper.f61j = 0.0f;
			jumper.f63k = 0.0f;
			jumper.reqSkipAccelStretch = true;
			if (jumper.equals(BounceGame.bounceObj)) {
				EventObject.eventVars[1] = BounceGame.CONTROLLER_NORMAL;
			}
		}
	}

	public final void setJumper(BounceObject j) {
		if (jumper != j) {
			releaseJumper();
		}
		jumper = j;
		jumper.enablePhysics = false;
		jumper.curXVelocity = 0.0f;
		jumper.curYVelocity = 0.0f;
	}

	/* renamed from: b */
	public final void onJumperContact() {
		loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
		jumper.localObjectMatrix.translationY = GameObject.tmpObjMatrix.translationY
				- ((LP32.Int32ToLP32((short) GameRuntime.getImageAnimParamEx(GameRuntime.getImageIdAfterAnimation(this.imageId, this.animFrame), 0)) / GameObject.screenSpaceMatrix.m00) << 16);
		if (jumper.equals(BounceGame.bounceObj)) {
			EventObject.eventVars[1] = BounceGame.CONTROLLER_FROZEN;
		}
	}

	// p000.GameObject
	/* renamed from: d */
	//@Override
	public final void updatePhysics() {
		super.updatePhysics();
		if (this.progress < this.period) {
			this.progress += GameRuntime.updateDelta;
			int frameCount = GameRuntime.getImageAnimationFrameCount(this.imageId);
			if (this.progress >= this.period - (this.period / frameCount)) { //jump on last frame
				if (!this.isJumpFinished) {
					this.isJumpFinished = true;
					jumper.curYVelocity = this.calcPush;
					releaseJumper();
				}
				this.animFrame = (frameCount * this.progress) / this.period;
			} else {
				this.animFrame = (frameCount * this.progress) / this.period;
				onJumperContact();
			}
			if (this.progress >= this.period) {
				this.progress = 0;
				this.period = 0;
				this.animFrame = 0;
			}
		}
	}
}
