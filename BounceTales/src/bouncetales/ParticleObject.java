package bouncetales;

import com.nokia.mid.ui.DirectGraphics;
import javax.microedition.lcdui.Graphics;

/* renamed from: n */
public final class ParticleObject extends GameObject {

	public static final int TYPE_SPRITE_4DIR = 0; //up/down/left/right
	public static final int TYPE_BUBBLES = 1;
	public static final int TYPE_SPRITE_RANDOM = 2;
	public static final int TYPE_SPRITE_BY_PARTICLE_NO = 3;
	public static final int TYPE_SHOWER = 4;
	public static final int TYPE_COLLAPSE = 5; //collapses back a little after expanding
	public static final int TYPE_TRAIL = 6;
	public static final int TYPE_SPRITE_2DIR = 7; //vertical/horizontal

	public static final byte TYPEID = 11;

	//Global state
	private static boolean existAnyParticle = false; //renamed from: a

	private static int[] pointRotationResult = new int[2]; //renamed from: g

	//Parameters
	private int[] imageIDs; //renamed from: f

	private int type; //renamed from: w

	private int accelX; //renamed from: d
	private int accelY; //renamed from: r
	private int ambientVelocityX; //renamed from: s
	private int ambientVelocityY; //renamed from: t
	private int falloff; //renamed from: u

	private int animationLifespan; //renamed from: x

	private int maxParticleCount; //renamed from: v

	//State
	public int particleCount; //renamed from: a

	private int[] particleLifespans; //renamed from: a
	private int[] particlePositionsX; //renamed from: b
	private int[] particlePositionsY; //renamed from: c
	private int[] velocityX; //renamed from: d
	private int[] velocityY; //renamed from: e
	private byte[] particleImageIndices; //renamed from: b

	//State - shower
	private byte[] showerDir; //renamed from: a
	private short[] showerTimer; //renamed from: a

	//State - bubble
	public int bubblePopY; //renamed from: b
	public int maxVelocityY; //renamed from: c

	public ParticleObject(int maxParticles, int accelX, int accelY, int ambientVelocityX, int ambientVelocityY, int falloff, int type, int[] spriteIDs, int animationLifespan, int zCoord) {
		if (!existAnyParticle) {
			existAnyParticle = true;
		}
		this.particleLifespans = new int[maxParticles];
		this.particlePositionsX = new int[maxParticles];
		this.particlePositionsY = new int[maxParticles];
		this.velocityX = new int[maxParticles];
		this.velocityY = new int[maxParticles];
		this.particleImageIndices = new byte[maxParticles];
		if (type == TYPE_SHOWER) {
			this.showerTimer = new short[maxParticles];
			this.showerDir = new byte[maxParticles];
			for (int i = 0; i < maxParticles; i++) {
				this.showerTimer[i] = 0;
			}
		}
		this.maxParticleCount = maxParticles;
		this.accelX = accelX;
		this.accelY = accelY;
		this.ambientVelocityX = ambientVelocityX;
		this.ambientVelocityY = ambientVelocityY;
		this.falloff = falloff;
		this.particleCount = -1;
		this.type = type;
		this.objType = TYPEID;
		this.imageIDs = spriteIDs;
		this.animationLifespan = animationLifespan;
		this.zCoord = (byte) zCoord;
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final void draw(Graphics graphics, DirectGraphics directGraphics, Matrix rootMatrix) {
		boolean lifespanEnd;
		int i;
		int delta = GameRuntime.updateDelta * GameRuntime.getUpdatesPerDraw();
		int particleIndex = 0;
		while (particleIndex < this.particleCount + 1) {
			particleLifespans[particleIndex] = particleLifespans[particleIndex] - delta;
			if (this.particleLifespans[particleIndex] > 0) {
				if (this.falloff != 0) {
					this.velocityX[particleIndex] -= (((this.velocityX[particleIndex] * this.falloff) * delta) >> 14);
					this.velocityY[particleIndex] -= (((this.velocityY[particleIndex] * this.falloff) * delta) >> 14);
				}
				this.velocityX[particleIndex] += (this.accelX * delta);
				this.velocityY[particleIndex] += (this.accelY * delta);
				if (this.type == TYPE_BUBBLES && this.velocityY[particleIndex] > this.maxVelocityY) {
					this.velocityY[particleIndex] = this.maxVelocityY;
				} else if (this.type == TYPE_SHOWER) {
					this.showerTimer[particleIndex] -= delta;
					if (this.showerTimer[particleIndex] <= 0) {
						this.showerTimer[particleIndex] = (short) (Math.abs(BounceGame.mRNG.nextInt() % 200) + 300);
						this.showerDir[particleIndex] = (byte) (Math.abs(BounceGame.mRNG.nextInt()) & 3);
					}
					int angle = delta / 2;
					if (this.showerDir[particleIndex] == 1) {
						rotatePoint(angle, this.velocityX[particleIndex], this.velocityY[particleIndex]);
						this.velocityX[particleIndex] = pointRotationResult[0];
						this.velocityY[particleIndex] = pointRotationResult[1];
					} else if (this.showerDir[particleIndex] == 2) {
						rotatePoint(359 - angle, this.velocityX[particleIndex], this.velocityY[particleIndex]);
						this.velocityX[particleIndex] = pointRotationResult[0];
						this.velocityY[particleIndex] = pointRotationResult[1];
					}
				} else if (this.type == TYPE_COLLAPSE) {
					//FIXME: delta/5 will break on high framerates
					rotatePoint(delta / 5, this.velocityX[particleIndex], this.velocityY[particleIndex]);
					this.velocityX[particleIndex] = pointRotationResult[0];
					this.velocityY[particleIndex] = pointRotationResult[1];
				}
				particlePositionsX[particleIndex] += (((this.velocityX[particleIndex] + this.ambientVelocityX) * delta) >> 4);
				particlePositionsY[particleIndex] += (((this.velocityY[particleIndex] + this.ambientVelocityY) * delta) >> 4);
				lifespanEnd = false;
			} else {
				lifespanEnd = true;
			}
			if (this.type == TYPE_BUBBLES && this.particlePositionsY[particleIndex] >= this.bubblePopY) {
				lifespanEnd = true;
			}
			if (!lifespanEnd) {
				int frameCount = GameRuntime.getImageAnimationFrameCount(this.imageIDs[this.particleImageIndices[particleIndex]]);
				int frame = (this.particleLifespans[particleIndex] * frameCount) / this.animationLifespan;
				if (frame > frameCount - 1) {
					frame = frameCount - 1;
				}
				rootMatrix.mulVector(this.particlePositionsX[particleIndex], this.particlePositionsY[particleIndex]);
				GameRuntime.drawAnimatedImageRes(Matrix.vectorMulRslX >> 16, Matrix.vectorMulRslY >> 16, this.imageIDs[this.particleImageIndices[particleIndex]], (frameCount - 1) - frame);
				i = particleIndex;
			} else if (particleIndex == this.particleCount) {
				this.particleCount--;
				i = particleIndex;
			} else {
				this.particleLifespans[particleIndex] = this.particleLifespans[this.particleCount];
				this.particlePositionsX[particleIndex] = this.particlePositionsX[this.particleCount];
				this.particlePositionsY[particleIndex] = this.particlePositionsY[this.particleCount];
				this.velocityX[particleIndex] = this.velocityX[this.particleCount];
				this.velocityY[particleIndex] = this.velocityY[this.particleCount];
				this.particleCount--;
				i = particleIndex - 1;
			}
			particleIndex = i + 1;
		}
	}

	/* renamed from: a */
	private static void rotatePoint(int angleDeg, int x, int y) {
		short sin = BounceGame.SIN_COS_TABLE[angleDeg % 360];
		short cos = BounceGame.SIN_COS_TABLE[(angleDeg + 90) % 360];
		pointRotationResult[0] = ((x * cos) - (y * sin)) / 360;
		pointRotationResult[1] = ((sin * x) + (cos * y)) / 360;
	}

	/* renamed from: c */
	public final void attachToObject(GameObject obj) {
		setParent(obj);
		this.localObjectMatrix.m00 = LP32.ONE;
		this.localObjectMatrix.m01 = 0;
		this.localObjectMatrix.translationX = 0;
		this.localObjectMatrix.m10 = 0;
		this.localObjectMatrix.m11 = LP32.ONE;
		this.localObjectMatrix.translationY = 0;
		this.renderCalcMatrix.setFromMatrix(this.localObjectMatrix);
		initialize();
	}

	/* renamed from: a */
	public final void emitCircle(int count, int posX, int posY, int dirScaleX, int dirScaleY, int lifespanBase, int lifespanRange) {
		if (count + 1 + this.particleCount > this.maxParticleCount) {
			count = (this.maxParticleCount - this.particleCount) - 1;
		}
		for (int i = 0; i < count; i++) {
			int sinIdx = (i * 360) / count;
			int cosIdx = sinIdx + 90 >= 360 ? (sinIdx + 90) - 360 : sinIdx + 90;
			this.particleCount++;
			this.particleLifespans[this.particleCount] = lifespanRange != 0 ? (BounceGame.mRNG.nextInt() % lifespanRange) + lifespanBase : lifespanBase;
			this.particlePositionsX[this.particleCount] = posX;
			this.particlePositionsY[this.particleCount] = posY;
			this.velocityX[this.particleCount] = BounceGame.SIN_COS_TABLE[sinIdx] * dirScaleX;
			this.velocityY[this.particleCount] = BounceGame.SIN_COS_TABLE[cosIdx] * dirScaleX;
			if (this.type == TYPE_SPRITE_BY_PARTICLE_NO) {
				this.particleImageIndices[this.particleCount] = (byte) (i % this.imageIDs.length);
			} else if (this.type == TYPE_SHOWER && equals(BounceGame.winParticle)) {
				byte abs = (byte) Math.abs(BounceGame.mRNG.nextInt() % this.imageIDs.length);
				if (abs > 1) {
					abs = (byte) Math.abs(BounceGame.mRNG.nextInt() % this.imageIDs.length);
				}
				this.particleImageIndices[this.particleCount] = abs;
			} else {
				this.particleImageIndices[this.particleCount] = (byte) Math.abs(BounceGame.mRNG.nextInt() % this.imageIDs.length);
			}
		}
	}

	/* renamed from: a */
	public final void emitBlast(
			int count,
			int posX,
			int posY,
			int directionScaleBase,
			int directionScaleRange,
			int directionX,
			int directionY,
			int directionBias,
			int lifespanBase,
			int lifespanRange
	) {
		if (this.particleCount + count + 1 > this.maxParticleCount) {
			count = (this.maxParticleCount - this.particleCount) - 1;
		}
		for (int i = 0; i < count; i++) {
			int velocityScale = directionScaleRange != 0 ? (BounceGame.mRNG.nextInt() % directionScaleRange) + directionScaleBase : directionScaleBase;
			this.particleCount++;
			this.particleLifespans[this.particleCount] = lifespanRange != 0 ? (BounceGame.mRNG.nextInt() % lifespanRange) + lifespanBase : lifespanBase;
			this.particlePositionsX[this.particleCount] = posX;
			this.particlePositionsY[this.particleCount] = posY;
			this.velocityX[this.particleCount] = ((((BounceGame.mRNG.nextInt() % directionBias) << 10) + directionX) * velocityScale) >> 8;
			this.velocityY[this.particleCount] = ((((BounceGame.mRNG.nextInt() % directionBias) << 10) + directionY) * velocityScale) >> 8;
			this.particleImageIndices[this.particleCount] = (byte) Math.abs(BounceGame.mRNG.nextInt() % this.imageIDs.length);
		}
	}

	/* renamed from: a */
	public final void emitBurst(int count, int posX, int posY, int dispBase, int dispRange, int dirAngleBase, int dirAngleRange, int lifespanBase, int lifespanRange) {
		if (count + 1 + this.particleCount > this.maxParticleCount) {
			count = (this.maxParticleCount - this.particleCount) - 1;
		}
		for (int i = 0; i < count; i++) {
			int dirAngleRng = dirAngleBase + (BounceGame.mRNG.nextInt() % ((dirAngleRange / 2) + 1)); //divide by two in order to make negative/positive add up to range
			int dirAngleNormTemp = dirAngleRng - (dirAngleRng >= 360 ? 360 : 0);
			int dirAngle = dirAngleNormTemp + (dirAngleNormTemp < 0 ? 360 : 0);
			int dirAngleCosIdx = dirAngle + 90 >= 360 ? dirAngle - 270 : dirAngle + 90;
			int rndDirScale = dispRange != 0 ? (BounceGame.mRNG.nextInt() % dispRange) + dispBase : dispBase;
			this.particleCount++;
			this.particleLifespans[this.particleCount] = lifespanRange != 0 ? (BounceGame.mRNG.nextInt() % lifespanRange) + lifespanBase : lifespanBase;
			this.particlePositionsX[this.particleCount] = posX;
			this.particlePositionsY[this.particleCount] = posY;
			this.velocityX[this.particleCount] = BounceGame.SIN_COS_TABLE[dirAngle] * rndDirScale;
			this.velocityY[this.particleCount] = BounceGame.SIN_COS_TABLE[dirAngleCosIdx] * rndDirScale;
			if (this.type == TYPE_SPRITE_4DIR) {
				if (dirAngle >= 35 && dirAngle <= 90) {
					this.particleImageIndices[this.particleCount] = 2;
				} else if (dirAngle < 35) {
					this.particleImageIndices[this.particleCount] = 3;
				} else if (dirAngle > 325) {
					this.particleImageIndices[this.particleCount] = 1;
				} else {
					this.particleImageIndices[this.particleCount] = 0;
				}
			} else if (this.type == TYPE_SPRITE_2DIR) {
				if (dirAngleBase == 0 || dirAngleBase == 180) {
					this.particleImageIndices[this.particleCount] = 0;
				} else {
					this.particleImageIndices[this.particleCount] = 1;
				}
			} else {
				this.particleImageIndices[this.particleCount] = (byte) Math.abs(BounceGame.mRNG.nextInt() % this.imageIDs.length);
			}
		}
	}

	/* renamed from: a */
	public final void emitIndependentBursts(int count, int posXMin, int posYMin, int posXMax, int posYMax, int dispBase, int dispRange, int dirAngleBase, int dirAngleRange, int lifespanBase, int lifespanRange) {
		for (int i = 0; i < count; i++) {
			emitBurst(1, posXMin + Math.abs(BounceGame.mRNG.nextInt() % ((posXMax - posXMin) + 1)), posYMin + Math.abs(BounceGame.mRNG.nextInt() % ((posYMax - posYMin) + 1)), dispBase, dispRange, dirAngleBase, dirAngleRange, lifespanBase, lifespanRange);
		}
	}

	/* renamed from: b */
	public final void emitTrail(int count, int posX, int posY, int dim, int dispBase, int dispRange, int dirAngleBase, int dirAngleRange, int lifespanBase, int lifespanRange) {
		int dispX;
		int dispY;
		for (int i = 0; i < count; i++) {
			do {
				dispX = BounceGame.mRNG.nextInt() % ((dim * 2) + 1);
				dispY = BounceGame.mRNG.nextInt() % ((dim * 2) + 1);
			} while ((dispX * dispX) + (dispY * dispY) > dim * dim);
			emitBurst(1, posX + dispX, posY + dispY, dispBase, dispRange, dirAngleBase, dirAngleRange, lifespanBase, lifespanRange);
		}
	}
}
