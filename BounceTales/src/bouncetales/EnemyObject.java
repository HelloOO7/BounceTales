package bouncetales;

import com.nokia.mid.ui.DirectGraphics;
import javax.microedition.lcdui.Graphics;

/* renamed from: l */
public final class EnemyObject extends GameObject {

	public static final byte TYPEID = 10;

	public static final int TYPE_CANDLE = 0;
	public static final int TYPE_BUMPER_UNUSED = 1;
	public static final int TYPE_MOLE = 2;
	public static final int TYPE_STALKER_UNUSED = 3;

	private static final int[] ENEMY_MOTION_SPEEDS = {6000, 4000, 4000, 10000}; //renamed from: a
	private static final int[] ENEMY_WIDTHS = {50, 100, 100, 150}; //renamed from: c
	private static final int[] ENEMY_HEIGHTS = {100, 50, 80, 150}; //renamed from: b

	private static final int STALKER_RECHARGE_TIME = 2000; //renamed from: x

	//Parameters
	byte enemyType; //renamed from: a

	private int movePoint1X; //renamed from: c
	private int movePoint1Y; //renamed from: d
	private int movePoint2X; //renamed from: r
	private int movePoint2Y; //renamed from: s

	//State - common
	private byte state; //renamed from: f

	byte propelType; //renamed from: d

	private byte curMovePoint; //renamed from: e
	private boolean facingLeft; //renamed from: b

	private int rechargeTimer; //renamed from: v

	//State - mole
	private int moleWaitTimer; //renamed from: w

	int molePeekPeriod; //renamed from: b
	int molePeekTimer; //renamed from: a

	boolean moleIsVulnerable; //renamed from: a

	//State - stalker
	private int stalkerInitX; //renamed from: t
	private int stalkerInitY; //renamed from: u

	public EnemyObject() {
		this.objType = TYPEID;
	}

	/* renamed from: f */
	private void killAndDropEgg() {
		loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
		int posX = GameObject.tmpObjMatrix.translationX;
		int posY = GameObject.tmpObjMatrix.translationY;
		BounceGame.enemyDeathParticle.emitCircle(10, posX, ((ENEMY_HEIGHTS[this.enemyType] << 16) >> 1) + posY, 370, 0, 920, 230);
		despawn();
		BounceGame.enemyDeadEgg.localObjectMatrix.translationX = posX;
		BounceGame.enemyDeadEgg.localObjectMatrix.translationY = posY + LP32.Int32ToLP32(30);
		BounceGame.enemyDeadEgg.renderCalcMatrix.setFromMatrix(this.localObjectMatrix);
		BounceGame.enemyDeadEgg.recalcAbsObjectMatrix();
		BounceGame.enemyDeadEgg.renderCalcMatrix.invert(BounceGame.enemyDeadEgg.inverseRenderCalcMatrix);
	}

	/* renamed from: g */
	private static void bounceAwayPlayerOnStomp() {
		if (BounceGame.bounceObj.curYVelocity < 0.0f) {
			BounceGame.bounceObj.curYVelocity = -BounceGame.bounceObj.curYVelocity * 0.5f;
			BounceGame.bounceObj.curXVelocity *= 0.7f;
		} else {
			BounceGame.bounceObj.curXVelocity = -BounceGame.bounceObj.curXVelocity * 0.5f;
			BounceGame.bounceObj.curYVelocity *= 0.7f;
		}
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final int readData(byte[] data, int dataPos) {
		int a = super.readData(data, dataPos);
		this.movePoint1X = (readShort(data, a) << 16) + this.localObjectMatrix.translationX;
		this.movePoint1Y = (readShort(data, a + 2) << 16) + this.localObjectMatrix.translationY;
		this.movePoint2X = (readShort(data, a + 4) << 16) + this.localObjectMatrix.translationX;
		this.movePoint2Y = (readShort(data, a + 6) << 16) + this.localObjectMatrix.translationY;
		a += 8;
		this.enemyType = data[a++];
		this.curMovePoint = 0;
		this.rechargeTimer = 0;
		this.molePeekTimer = 0;
		this.facingLeft = false;
		if (this.movePoint1X < this.movePoint2X) {
			this.facingLeft = true;
		}
		this.propelType = 1;
		this.moleWaitTimer = 2000;
		this.state = 1;
		this.molePeekPeriod = 800;
		this.moleIsVulnerable = false;
		if (this.enemyType == TYPE_STALKER_UNUSED) {
			this.rechargeTimer = STALKER_RECHARGE_TIME;
			this.stalkerInitX = this.localObjectMatrix.translationX;
			this.stalkerInitY = this.localObjectMatrix.translationY;
			this.movePoint1X = this.stalkerInitX;
			this.movePoint1Y = this.stalkerInitY;
			this.movePoint2X = this.stalkerInitX;
			this.movePoint2Y = this.stalkerInitY;
		}
		return a;
	}

	// p000.GameObject, p000.GameObject, p000.GameObject, p000.GameObject
	/* renamed from: a */
	//@Override
	public final void initialize() {
		this.bboxMinX = (-ENEMY_WIDTHS[this.enemyType]) << 15;
		this.bboxMaxX = ENEMY_WIDTHS[this.enemyType] << 15;
		this.bboxMinY = 0;
		this.bboxMaxY = ENEMY_HEIGHTS[this.enemyType] << 16;
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final void draw(Graphics graphics, DirectGraphics directGraphics, Matrix rootMatrix) {
		loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
		Matrix.multMatrices(rootMatrix, GameObject.tmpObjMatrix, Matrix.temp);
		int posX = Matrix.temp.translationX >> 16;
		int posY = Matrix.temp.translationY >> 16;
		switch (this.enemyType) {
			case TYPE_CANDLE: //candle
				GameRuntime.drawAnimatedImageRes(posX, posY, 467, (BounceGame.levelTimer / 50) % 6);
				break;
			case TYPE_BUMPER_UNUSED:
				if (this.facingLeft) {
					GameRuntime.drawImageRes(posX, posY, -41);
				} else {
					GameRuntime.drawImageRes(posX, posY, -42);
				}
				break;
			case TYPE_MOLE: //mole
				if (this.state != 0) {
					int clipX = graphics.getClipX();
					int clipY = graphics.getClipY();
					int clipWidth = graphics.getClipWidth();
					int clipHeight = graphics.getClipHeight();
					int i3 = posY - clipY;
					if (i3 > clipHeight) {
						i3 = clipHeight;
					}
					graphics.setClip(clipX, clipY, clipWidth, i3);
					int molePeekY = (this.molePeekTimer * 61) / this.molePeekPeriod;
					if (this.facingLeft) {
						GameRuntime.drawImageRes(posX, (posY + 61) - molePeekY, 189);
					} else {
						GameRuntime.drawImageRes(posX, (posY + 61) - molePeekY, 194);
					}
					graphics.setClip(clipX, clipY, clipWidth, clipHeight);
				}
				GameRuntime.drawAnimatedImageRes(posX, posY, 504, this.state == 0 ? (BounceGame.levelTimer / 150) % 4 : 0);
				break;
			case TYPE_STALKER_UNUSED:
				GameRuntime.drawImageRes(posX, posY, -28);
				break;
			default:
				break;
		}
		debugDraw(graphics, 0xFF0000, rootMatrix);
	}

	/* renamed from: b */
	public final void propelBounceAway() {
		loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
		int myX = GameObject.tmpObjMatrix.translationX;
		int bounceX = BounceGame.bounceObj.localObjectMatrix.translationX;
		if (this.rechargeTimer <= 0 && this.propelType == 0 && this.state == 1) {
			this.rechargeTimer = 500;
			if (myX < bounceX) {
				BounceGame.bounceObj.pushX += 200.0f;
				BounceGame.bounceObj.pushY += 400.0f;
			} else {
				BounceGame.bounceObj.pushX -= 200.0f;
				BounceGame.bounceObj.pushY += 400.0f;
			}
			BounceGame.bounceObj.curXVelocity = 0.0f;
			BounceGame.bounceObj.curYVelocity = 0.0f;
		} else if (this.propelType == 1) {
			if (myX < bounceX) {
				BounceGame.bounceObj.pushX += 100.0f;
			} else {
				BounceGame.bounceObj.pushX -= 100.0f;
			}
			BounceGame.bounceObj.curXVelocity = 0.0f;
			BounceGame.bounceObj.curYVelocity = 0.0f;
		}
	}

	/* renamed from: c */
	public final void onPlayerHit() {
		switch (this.enemyType) {
			case TYPE_CANDLE:
				if (this.rechargeTimer <= 0) {
					loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
					if (GameObject.tmpObjMatrix.translationX < BounceGame.bounceObj.localObjectMatrix.translationX) {
						BounceGame.bounceObj.pushX += 500.0f;
					} else {
						BounceGame.bounceObj.pushX -= 500.0f;
					}
					BounceGame.bounceObj.curXVelocity = 0.0f;
					BounceGame.bounceObj.curYVelocity = 0.0f;
					this.rechargeTimer = 500;
				}
				break;
			case TYPE_BUMPER_UNUSED:
				if (BounceGame.bounceObj.ballForme == BounceObject.FORME_BUMPY_CRACKS) {
					killAndDropEgg();
				} else {
					BounceGame.setPlayerState(1);
				}
				break;
			case TYPE_MOLE:
				if (this.rechargeTimer <= 0 && this.propelType == 0 && this.state == 0) {
					this.molePeekTimer = 0;
					this.state = 1;
					this.molePeekPeriod = 200;
				}
				break;
			case TYPE_STALKER_UNUSED:
				BounceGame.setPlayerState(1);
				this.localObjectMatrix.translationX = this.stalkerInitX;
				this.localObjectMatrix.translationY = this.stalkerInitY;
				this.movePoint1X = this.stalkerInitX;
				this.movePoint1Y = this.stalkerInitY;
				this.movePoint2X = this.stalkerInitX;
				this.movePoint2Y = this.stalkerInitY;
				break;
		}
	}

	// p000.GameObject
	/* renamed from: d */
	//@Override
	public final void updatePhysics() {
		int targetTX;
		int targetTY;
		int otherMovePointX;
		setIsDirtyRecursive();
		super.updatePhysics();
		int tx = this.localObjectMatrix.translationX;
		int ty = this.localObjectMatrix.translationY;
		if (this.rechargeTimer > 0) {
			this.rechargeTimer -= GameRuntime.updateDelta;
			if (this.rechargeTimer <= 0) {
				this.rechargeTimer = 0;
				if (this.enemyType == TYPE_STALKER_UNUSED) {
					this.rechargeTimer = STALKER_RECHARGE_TIME;
					this.movePoint2X = this.movePoint1X;
					this.movePoint2Y = this.movePoint1Y;
					int i7 = this.movePoint2X - tx;
					int i8 = this.movePoint2Y - ty;
					float f = (float) (i7 >> 16);
					float f2 = (float) (i8 >> 16);
					int sqrt = LP32.FP64ToLP32(Math.sqrt((f * f) + (f2 * f2)));
					int i9 = (STALKER_RECHARGE_TIME << 1) * ENEMY_MOTION_SPEEDS[this.enemyType];
					if (i9 > sqrt) {
						double d = ((double) i9) / ((double) sqrt);
						this.movePoint2X = ((int) (((double) i7) * (d - 1.0d))) + this.movePoint2X;
						this.movePoint2Y += (int) ((d - 1.0d) * ((double) i8));
					}
					this.movePoint1X = BounceGame.bounceObj.localObjectMatrix.translationX;
					this.movePoint1Y = BounceGame.bounceObj.localObjectMatrix.translationY;
				}
			}
		}
		if (this.enemyType == TYPE_STALKER_UNUSED) {
			int i10 = this.movePoint2X - tx;
			int i11 = this.movePoint2Y - ty;
			float f3 = (float) (i10 >> 16);
			float f4 = (float) (i11 >> 16);
			int sqrt2 = LP32.FP64ToLP32(Math.sqrt((f3 * f3) + (f4 * f4)));
			if (sqrt2 != 0) {
				double d2 = ((double) (ENEMY_MOTION_SPEEDS[this.enemyType] * GameRuntime.updateDelta)) / ((double) sqrt2);
				tx += (int) (((double) i10) * d2);
				ty += (int) (((double) i11) * d2);
			}
		} else {
			if (this.enemyType == TYPE_MOLE) {
				if (this.moleWaitTimer > 0) {
					this.moleWaitTimer -= GameRuntime.updateDelta;
					if (this.moleWaitTimer <= 0) {
						if (this.propelType == 0) {
							this.propelType = 1;
							this.moleWaitTimer = 2000;
							this.state = 1;
							this.molePeekPeriod = 800;
						} else {
							this.propelType = 0;
							this.moleWaitTimer = 5000;
							this.state = 2;
							this.molePeekPeriod = 800;
							this.moleIsVulnerable = true;
						}
					}
				}
				if (this.state == 1) {
					this.molePeekTimer += GameRuntime.updateDelta;
					if (this.molePeekTimer >= this.molePeekPeriod) {
						if (this.propelType == 0) {
							this.state = 2;
							this.molePeekPeriod = 200;
						}
						this.molePeekTimer = this.molePeekPeriod;
					}
				} else if (this.state == 2) {
					this.molePeekTimer -= GameRuntime.updateDelta;
					if (this.molePeekTimer <= 0) {
						this.state = 0;
						this.molePeekTimer = 0;
						this.moleIsVulnerable = false;
					}
				}
			}
			if (this.curMovePoint == 0) {
				targetTX = this.movePoint1X;
				targetTY = this.movePoint1Y;
				otherMovePointX = this.movePoint2X;
			} else {
				targetTX = this.movePoint2X;
				targetTY = this.movePoint2Y;
				otherMovePointX = this.movePoint1X;
			}
			if (this.enemyType == TYPE_CANDLE && Math.abs(tx - targetTX) > LP32.Int32ToLP32(70) && Math.abs(tx - otherMovePointX) > LP32.Int32ToLP32(70)) {
				loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
				int myAbsX = GameObject.tmpObjMatrix.translationX;
				int bounceX = BounceGame.bounceObj.localObjectMatrix.translationX;
				if (Math.abs(bounceX - myAbsX) < LP32.Int32ToLP32(120) && ((myAbsX < bounceX && targetTX < tx) || (myAbsX > bounceX && targetTX > tx))) {
					if (this.curMovePoint == 0) {
						targetTX = this.movePoint2X;
						targetTY = this.movePoint2Y;
						this.curMovePoint = 1;
					} else {
						targetTX = this.movePoint1X;
						targetTY = this.movePoint1Y;
						this.curMovePoint = 0;
					}
				}
			}
			int xdiff = targetTX - tx;
			int ydiff = targetTY - ty;
			float f5 = (float) (xdiff >> 16);
			float f6 = (float) (ydiff >> 16);
			int distToTarget = LP32.FP64ToLP32(Math.sqrt((f5 * f5) + (f6 * f6)));
			boolean xDone = false;
			boolean yDone = false;
			if (distToTarget != 0) {
				double d3 = ((double) (GameRuntime.updateDelta * ENEMY_MOTION_SPEEDS[this.enemyType])) / ((double) distToTarget);
				int i16 = (int) (xdiff * d3);
				int i17 = (int) (ydiff * d3);
				tx += i16;
				ty += i17;
				if ((i16 >= 0 && tx >= targetTX) || (i16 <= 0 && tx <= targetTX)) {
					xDone = true;
					tx = targetTX;
				}
				if ((i17 >= 0 && ty >= targetTY) || (i17 <= 0 && ty <= targetTY)) {
					yDone = true;
					ty = targetTY;
				}
			}

			if ((xDone && yDone) || distToTarget == 0) {
				tx = targetTX;
				ty = targetTY;
				if (this.curMovePoint == 0) {
					this.curMovePoint = 1;
				} else {
					this.curMovePoint = 0;
				}
			}
		}
		if (this.enemyType != TYPE_MOLE || (this.enemyType == TYPE_MOLE && this.propelType == 0 && this.state == 0)) {
			this.facingLeft = this.localObjectMatrix.translationX < tx;
			this.localObjectMatrix.translationX = tx;
			this.localObjectMatrix.translationY = ty;
			this.renderMatrixIsDirty = true;
			this.objectMatrixIsDirty = true;
		}
	}

	/* renamed from: e */
	public final void stomp() {
		switch (this.enemyType) {
			case TYPE_CANDLE:
				bounceAwayPlayerOnStomp();
				killAndDropEgg();
				break;
			case TYPE_BUMPER_UNUSED:
			case TYPE_STALKER_UNUSED:
			default:
				break;
			case TYPE_MOLE:
				bounceAwayPlayerOnStomp();
				killAndDropEgg();
				break;
		}
	}
}
