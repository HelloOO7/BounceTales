package bouncetales;

import javax.microedition.lcdui.Graphics;

/* renamed from: k */
public final class CannonObject extends GameObject {

	public static final byte TYPEID = 7;

	private static final int[] CANNON_FRAME_LENGTHS = new int[]{500, 100, 300}; //renamed from: a

	private static final int CANNON_TOTAL_FRAMES; //renamed from: t

	static {
		CANNON_TOTAL_FRAMES = CANNON_FRAME_LENGTHS[0] + CANNON_FRAME_LENGTHS[1] + CANNON_FRAME_LENGTHS[2];
	}

	private static final int FIRING_FRAME_LENGTH = CANNON_FRAME_LENGTHS[2]; //renamed from: u

	//Parameters - preset
	private byte power; //renamed from: a

	//Parameters - calculated	
	private boolean isFacingRight; //renamed from: a

	int loadAABBMinX; //renamed from: a
	int loadAABBMinY; //renamed from: c
	int loadAABBMaxX; //renamed from: b
	int loadAABBMaxY; //renamed from: d

	//State
	private int animCountdown; //renamed from: s

	private short bounceObjId; //renamed from: a

	private int reloadCooldown; //renamed from: r

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
		this.power = data[dataPos++];
		this.reloadCooldown = 0;
		this.isFacingRight = false;
		this.animCountdown = 0;
		if (this.localObjectMatrix.m00 >= 0) {
			this.isFacingRight = true;
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
		this.loadAABBMinX = (-40) << 16;
		this.loadAABBMaxX = 40 << 16;
		this.loadAABBMinY = (-40) << 16;
		this.loadAABBMaxY = 40 << 16;
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final void draw(Graphics graphics, Matrix dVar) {
		super.draw(graphics, dVar);
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
				GeometryObject lerpL = (GeometryObject) BounceGame.cannonModels[(cannonModelFrame * 3) + i];
				GeometryObject morph = (GeometryObject) BounceGame.cannonModels[i + 9];
				GeometryObject lerpR = cannonModelFrame == 2 ? (GeometryObject) BounceGame.cannonModels[i] : (GeometryObject) BounceGame.cannonModels[((cannonModelFrame + 1) * 3) + i];
				for (int vertIdx = 0; vertIdx < morph.xCoordBuffer.length; vertIdx++) {
					morph.xCoordBuffer[vertIdx] = (int) ((((float) lerpL.xCoordBuffer[vertIdx]) * weightInv) + (((float) lerpR.xCoordBuffer[vertIdx]) * weight));
					morph.yCoordBuffer[vertIdx] = (int) ((((float) lerpL.yCoordBuffer[vertIdx]) * weightInv) + (((float) lerpR.yCoordBuffer[vertIdx]) * weight));
				}
			}
		}
		loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
		Matrix.multMatrices(dVar, GameObject.tmpObjMatrix, Matrix.temp);
		int imageX = Matrix.temp.translationX >> 16;
		int imageY = Matrix.temp.translationY >> 16;
		for (int i = 0; i < 3; i++) {
			GameObject model = BounceGame.cannonModels[i + 9];
			model.localObjectMatrix.translationX = this.localObjectMatrix.translationX;
			model.localObjectMatrix.translationY = this.localObjectMatrix.translationY;
			model.localObjectMatrix.m00 = this.localObjectMatrix.m00;
			model.localObjectMatrix.m10 = this.localObjectMatrix.m10;
			model.localObjectMatrix.m01 = this.localObjectMatrix.m01;
			model.localObjectMatrix.m11 = this.localObjectMatrix.m11;
			model.setIsDirtyRecursive();
			model.draw(graphics, dVar);
		}
		GameRuntime.drawImageRes(imageX, imageY, 48);
	}

	/* renamed from: b */
	public final void onPlayerContact() {
		if (this.reloadCooldown == 0 && EventObject.eventVars[1] == BounceGame.CONTROLLER_NORMAL) {
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
			Matrix.temp.setRotation(GameRuntime.updateDelta * 0.001f * 3.0f);
			Matrix.temp.translationX = 0;
			Matrix.temp.translationY = 0;
			this.localObjectMatrix.mul(Matrix.temp);
			if (this.isFacingRight && this.localObjectMatrix.m00 < 0) {
				//set rotation to 90 degrees, scaleX to 1
				this.localObjectMatrix.m00 = 0;
				this.localObjectMatrix.m10 = LP32.ONE;
				this.localObjectMatrix.m01 = -LP32.ONE;
				this.localObjectMatrix.m11 = 0;
			}
			if (!this.isFacingRight && this.localObjectMatrix.m00 > 0) {
				//set rotation to 90 degrees, scaleX to -1
				this.localObjectMatrix.m00 = 0;
				this.localObjectMatrix.m10 = LP32.ONE;
				this.localObjectMatrix.m01 = LP32.ONE;
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
		this.reloadCooldown -= GameRuntime.updateDelta;
		if (this.reloadCooldown < 0) {
			this.reloadCooldown = 0;
		}
		if (this.animCountdown > 0) {
			this.animCountdown -= GameRuntime.updateDelta;
			if (this.animCountdown <= FIRING_FRAME_LENGTH && EventObject.eventVars[1] == BounceGame.CONTROLLER_CANNON) {
				BounceObject bounce = (BounceObject) getObjectRoot().searchByObjId(this.bounceObjId);
				bounce.lastXVelocity = 0.0f;
				bounce.lastYVelocity = 0.0f;
				bounce.curXVelocity = (float) ((this.power * this.localObjectMatrix.m00) >> 12);
				bounce.curYVelocity = (float) ((this.power * this.localObjectMatrix.m10) >> 12);
				bounce.isGrounded = false;
				bounce.reqSkipAccelStretch = true;
				bounce.enablePhysics = true;
				bounce.isVisible = true;
				bounce.torqueX = 0.0f;
				bounce.torqueY = 0.0f;
				EventObject.eventVars[1] = BounceGame.CONTROLLER_NORMAL;
				this.reloadCooldown = 500;
				loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
				GameObject.tmpObjMatrix.mulVector(120 << 16, 0);
				int headX = Matrix.vectorMulRslX;
				int headY = Matrix.vectorMulRslY;
				bounce.localObjectMatrix.translationX = headX;
				bounce.localObjectMatrix.translationY = headY;
				BounceGame.cannonParticle.emitBlast(10, headX, headY, 800, 200, this.localObjectMatrix.m00, this.localObjectMatrix.m10, 30, 800, 200);
			}
			if (this.animCountdown <= 0) {
				this.animCountdown = 0;
				for (int meshIdx = 0; meshIdx < 3; meshIdx++) {
					GeometryObject morph = (GeometryObject) BounceGame.cannonModels[meshIdx + 9];
					GeometryObject baseVerts = (GeometryObject) BounceGame.cannonModels[meshIdx];
					for (int vertIdx = 0; vertIdx < morph.xCoordBuffer.length; vertIdx++) {
						morph.xCoordBuffer[vertIdx] = baseVerts.xCoordBuffer[vertIdx];
						morph.yCoordBuffer[vertIdx] = baseVerts.yCoordBuffer[vertIdx];
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
			if (this.isFacingRight && this.localObjectMatrix.m01 > 0) { //negative sine of angle > 0 -> angle is 180 to 360
				//set rotation to 0 degrees, scaleX to 1
				this.localObjectMatrix.m00 = LP32.ONE;
				this.localObjectMatrix.m10 = 0;
				this.localObjectMatrix.m01 = 0;
				this.localObjectMatrix.m11 = LP32.ONE;
			}
			if (!this.isFacingRight && this.localObjectMatrix.m01 < 0) { //angle is 0 to 180
				//set rotation to 0 degrees, scaleX to -1
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
