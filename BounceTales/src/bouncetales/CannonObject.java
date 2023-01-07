package bouncetales;

import com.nokia.mid.ui.DirectGraphics;
import javax.microedition.lcdui.Graphics;

/* renamed from: k */
public final class CannonObject extends GameObject {
	
	public static final byte TYPEID = 7;

	/* renamed from: a */
	private static int[] CANNON_FRAME_LENGTHS = new int[]{500, 100, 300};

	/* renamed from: t */
	private static int CANNON_TOTAL_FRAMES;

	/* renamed from: u */
	private static int f207u = CANNON_FRAME_LENGTHS[2];

	/* renamed from: a */
	private byte f208a;

	/* renamed from: a */
	int f209a;

	/* renamed from: a */
	private short bounceObjId;

	/* renamed from: a */
	private boolean f211a;

	/* renamed from: b */
	int f212b;

	/* renamed from: c */
	int f213c;

	/* renamed from: d */
	int f214d;

	/* renamed from: r */
	private int f215r;

	/* renamed from: s */
	private int animCountdown;

	static {
		CANNON_TOTAL_FRAMES = CANNON_FRAME_LENGTHS[0] + CANNON_FRAME_LENGTHS[1] + CANNON_FRAME_LENGTHS[2];
	}

	public CannonObject() {
		this.objType = TYPEID;
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final int readData(byte[] data, int dataPos) {
		dataPos = super.readData(data, dataPos);
		this.bounceObjId = readShort(data, dataPos);
		dataPos += 2;
		this.f208a = data[dataPos++];
		this.f215r = 0;
		this.f211a = false;
		this.animCountdown = 0;
		if (this.localObjectMatrix.m00 >= 0) {
			this.f211a = true;
		}
		return dataPos;
	}

	// p000.GameObject, p000.GameObject, p000.GameObject, p000.GameObject
	/* renamed from: a */
	//@Override
	public final void initialize() {
		this.bboxMinX = (-120) << 16;
		this.bboxMaxX = 120 << 16;
		this.bboxMinY = (-120) << 16;
		this.bboxMaxY = 120 << 16;
		this.f209a = (-40) << 16;
		this.f212b = 40 << 16;
		this.f213c = (-40) << 16;
		this.f214d = 40 << 16;
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final void draw(Graphics graphics, DirectGraphics directGraphics, Matrix dVar) {
		super.draw(graphics, directGraphics, dVar);
		if (this.animCountdown > 0) {
			int currentCannonFrame = CANNON_TOTAL_FRAMES - this.animCountdown;
			int frameStartFrames = 0;
			int cannonModelFrame = 0;
			for (int cannonFrmIdx = 0; cannonFrmIdx < 3 && currentCannonFrame >= CANNON_FRAME_LENGTHS[cannonFrmIdx] + frameStartFrames; cannonFrmIdx++) {
				frameStartFrames += CANNON_FRAME_LENGTHS[cannonFrmIdx];
				cannonModelFrame++;
			}
			float weight = ((float) (currentCannonFrame - frameStartFrames)) / ((float) CANNON_FRAME_LENGTHS[cannonModelFrame]);
			float weightInv = 1.0f - weight;
			for (int i = 0; i < 3; i++) {
				GeometryObject pVar = (GeometryObject) BounceGame.cannonModels[(cannonModelFrame * 3) + i];
				GeometryObject frameLeft = (GeometryObject) BounceGame.cannonModels[i + 9];
				GeometryObject frameRight = cannonModelFrame == 2 ? (GeometryObject) BounceGame.cannonModels[i] : (GeometryObject) BounceGame.cannonModels[((cannonModelFrame + 1) * 3) + i];
				for (int i6 = 0; i6 < frameLeft.xCoordBuffer.length; i6++) {
					frameLeft.xCoordBuffer[i6] = (int) ((((float) pVar.xCoordBuffer[i6]) * weightInv) + (((float) frameRight.xCoordBuffer[i6]) * weight));
					frameLeft.yCoordBuffer[i6] = (int) ((((float) pVar.yCoordBuffer[i6]) * weightInv) + (((float) frameRight.yCoordBuffer[i6]) * weight));
				}
			}
		}
		loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
		Matrix.multMatrices(dVar, GameObject.tmpObjMatrix, Matrix.temp);
		int i7 = Matrix.temp.translationX >> 16;
		int i8 = Matrix.temp.translationY >> 16;
		for (int i9 = 0; i9 < 3; i9++) {
			GameObject model = BounceGame.cannonModels[i9 + 9];
			model.localObjectMatrix.translationX = this.localObjectMatrix.translationX;
			model.localObjectMatrix.translationY = this.localObjectMatrix.translationY;
			model.localObjectMatrix.m00 = this.localObjectMatrix.m00;
			model.localObjectMatrix.m10 = this.localObjectMatrix.m10;
			model.localObjectMatrix.m01 = this.localObjectMatrix.m01;
			model.localObjectMatrix.m11 = this.localObjectMatrix.m11;
			model.setIsDirtyRecursive();
			model.draw(graphics, directGraphics, dVar);
		}
		GameRuntime.drawImageRes(i7, i8, 48);
	}

	/* renamed from: b */
	public final void loadBounceToCannon() {
		if (this.f215r == 0 && EventObject.eventVars[1] == BounceGame.CONTROLLER_NORMAL) {
			EventObject.eventVars[1] = BounceGame.CONTROLLER_CANNON;
			BounceGame.currentCannon = this;
			BounceObject bounce = (BounceObject) getObjectRoot().searchByObjId(this.bounceObjId);
			loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
			bounce.setPosXY(GameObject.tmpObjMatrix.translationX, GameObject.tmpObjMatrix.translationY + 2293760);
			bounce.enablePhysics = false;
			bounce.isVisible = false;
		}
	}

	/* renamed from: c */
	public final void rotateUp() {
		if (this.animCountdown == 0) {
			Matrix.temp.setRotation(((float) GameRuntime.updateDelta) * 0.001f * 3.0f);
			Matrix.temp.translationX = 0;
			Matrix.temp.translationY = 0;
			this.localObjectMatrix.mul(Matrix.temp);
			if (this.f211a && this.localObjectMatrix.m00 < 0) {
				this.localObjectMatrix.m00 = 0;
				this.localObjectMatrix.m10 = LP32.FP32ToLP32(1f);
				this.localObjectMatrix.m01 = LP32.FP32ToLP32(-1f);
				this.localObjectMatrix.m11 = 0;
			}
			if (!this.f211a && this.localObjectMatrix.m00 > 0) {
				this.localObjectMatrix.m00 = 0;
				this.localObjectMatrix.m10 = LP32.FP32ToLP32(1f);
				this.localObjectMatrix.m01 = LP32.FP32ToLP32(1f);
				this.localObjectMatrix.m11 = 0;
			}
			setIsDirtyRecursive();
		}
	}

	// p000.GameObject
	/* renamed from: d */
	//@Override
	public final void updatePhysics() {
		super.updatePhysics();
		this.f215r -= GameRuntime.updateDelta;
		if (this.f215r < 0) {
			this.f215r = 0;
		}
		if (this.animCountdown > 0) {
			this.animCountdown -= GameRuntime.updateDelta;
			if (this.animCountdown <= f207u && EventObject.eventVars[1] == BounceGame.CONTROLLER_CANNON) {
				BounceObject bounce = (BounceObject) getObjectRoot().searchByObjId(this.bounceObjId);
				bounce.lastXVelocity = 0.0f;
				bounce.lastYVelocity = 0.0f;
				bounce.curXVelocity = (float) ((this.f208a * this.localObjectMatrix.m00) >> 12);
				bounce.curYVelocity = (float) ((this.f208a * this.localObjectMatrix.m10) >> 12);
				bounce.isGrounded = false;
				bounce.reqSkipAccelStretch = true;
				bounce.enablePhysics = true;
				bounce.isVisible = true;
				bounce.f61j = 0.0f;
				bounce.f63k = 0.0f;
				EventObject.eventVars[1] = BounceGame.CONTROLLER_NORMAL;
				this.f215r = 500;
				loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
				GameObject.tmpObjMatrix.mulVector(7864320, 0);
				int i = Matrix.vectorMulRslX;
				int i2 = Matrix.vectorMulRslY;
				bounce.localObjectMatrix.translationX = i;
				bounce.localObjectMatrix.translationY = i2;
				BounceGame.f285e.mo70a(10, i, i2, 800, 200, this.localObjectMatrix.m00, this.localObjectMatrix.m10, 30, 800, 200);
			}
			if (this.animCountdown <= 0) {
				this.animCountdown = 0;
				for (int i3 = 0; i3 < 3; i3++) {
					GeometryObject pVar = (GeometryObject) BounceGame.cannonModels[i3 + 9];
					GeometryObject pVar2 = (GeometryObject) BounceGame.cannonModels[i3];
					for (int i4 = 0; i4 < pVar.xCoordBuffer.length; i4++) {
						pVar.xCoordBuffer[i4] = pVar2.xCoordBuffer[i4];
						pVar.yCoordBuffer[i4] = pVar2.yCoordBuffer[i4];
					}
				}
			}
		}
	}

	/* renamed from: e */
	public final void rotateDown() {
		if (this.animCountdown == 0) {
			Matrix.temp.setRotation(((float) GameRuntime.updateDelta) * 0.001f * -3.0f);
			Matrix.temp.translationX = 0;
			Matrix.temp.translationY = 0;
			this.localObjectMatrix.mul(Matrix.temp);
			if (this.f211a && this.localObjectMatrix.m01 > 0) {
				this.localObjectMatrix.m00 = LP32.ONE;
				this.localObjectMatrix.m10 = 0;
				this.localObjectMatrix.m01 = 0;
				this.localObjectMatrix.m11 = LP32.ONE;
			}
			if (!this.f211a && this.localObjectMatrix.m01 < 0) {
				this.localObjectMatrix.m00 = -LP32.ONE;
				this.localObjectMatrix.m10 = 0;
				this.localObjectMatrix.m01 = 0;
				this.localObjectMatrix.m11 = LP32.ONE;
			}
			setIsDirtyRecursive();
		}
	}

	/* renamed from: f */
	public final void fire() {
		if (this.animCountdown == 0) {
			this.animCountdown = CANNON_TOTAL_FRAMES;
		}
	}
}
