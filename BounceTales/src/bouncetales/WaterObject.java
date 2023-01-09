package bouncetales;

import com.nokia.mid.ui.DirectGraphics;
import javax.microedition.lcdui.Graphics;

/* renamed from: i */
public final class WaterObject extends GameObject {

	public static final byte TYPEID = 6;

	private static final int COLOR_WATER = 0x441111EE;
	private static final int COLOR_AIR_TUNNEL = 0x44000000;

	public static final int REGION_SURFACE = 0;
	public static final int REGION_DEPTHS = 1;

	//Parameters - preset
	private int color; //renamed from: d

	short areaMinX; //renamed from: a
	short areaMaxX; //renamed from: b
	short areaMinY; //renamed from: c
	private short areaMaxY; //renamed from: e

	private byte gravityXLeft; //renamed from: f
	private byte gravityXRight; //renamed from: d
	private byte gravityYTop; //renamed from: a
	private byte gravityYBottom; //renamed from: e

	//Parameters - calculated
	private byte region; //renamed from: g
	int vertexCount = 0; //renamed from: a
	private int maxSplashX; //renamed from: r

	int surfaceY; //renamed from: b
	private short areaWidth; //renamed from: f

	//State - splashes
	private int splashTimer = 0; //renamed from: s
	private byte[] splashYOffsets; //renamed from: a
	private int splashLimit; //renamed from: c

	private int[] splashIntensity; //renamed from: a
	private int[] splashSpread; //renamed from: b
	private byte[] splashDirections; //renamed from: b
	private byte[] splashPermanence; //renamed from: c
	private int[] splashSpeeds; //renamed from: c
	private int[] splashXPos; //renamed from: d

	//State - particles
	private int bounceBubbleTimer; //renamed from: t
	private int ambientParticleTimer; //renamed from: u

	public WaterObject() {
		this.objType = TYPEID;
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final int readData(byte[] data, int dataPos) {
		dataPos = super.readData(data, dataPos);
		this.areaMinX = readShort(data, dataPos);
		this.areaMaxY = readShort(data, dataPos + 2);
		this.areaMaxX = readShort(data, dataPos + 4);
		this.areaMinY = readShort(data, dataPos + 6);
		dataPos += 8;
		this.gravityYTop = data[dataPos++];
		this.gravityXRight = data[dataPos++];
		this.gravityYBottom = data[dataPos++];
		this.gravityXLeft = data[dataPos++];
		dataPos++; //skip alpha
		int colorAGB = 0x44000000
				| ((data[dataPos++] & 255) << 16)
				| ((data[dataPos++] & 255) << 8);
		int red = data[dataPos++] & 255;
		this.color = colorAGB | red;
		if (red == 16) {
			this.region = REGION_DEPTHS;
		} else {
			this.region = REGION_SURFACE;
		}
		if (this.color != COLOR_AIR_TUNNEL) {
			this.color = COLOR_WATER;
		}
		this.areaWidth = (short) (this.areaMaxX - this.areaMinX);
		if (isWater() && this.region == REGION_SURFACE) {
			int maxSplashVerts = (this.areaWidth * 50) / 100;
			this.splashYOffsets = new byte[maxSplashVerts];
			this.maxSplashX = maxSplashVerts << 12;
			this.ambientParticleTimer = 0;
			this.vertexCount = maxSplashVerts + 2;
			for (int i = 0; i < maxSplashVerts; i++) {
				this.splashYOffsets[i] = 0;
			}
			int maxSplashX = maxSplashVerts / 20;
			this.splashLimit = (maxSplashX << 1) + 2;
			this.splashIntensity = new int[this.splashLimit];
			this.splashSpread = new int[this.splashLimit];
			this.splashDirections = new byte[this.splashLimit];
			this.splashPermanence = new byte[this.splashLimit];
			this.splashSpeeds = new int[this.splashLimit];
			this.splashXPos = new int[this.splashLimit];
			for (int i = 0; i < this.splashLimit; i++) {
				this.splashIntensity[i] = 0;
			}
			for (int i = 2; i <= maxSplashX - 2; i++) {
				int length = (((this.splashYOffsets.length - 1) << 12) * i) / maxSplashX;
				int intensity1 = (((Math.abs(BounceGame.mRNG.nextInt() % 2) + 24) << 12) << 1) / 9;
				insertSplash(intensity1, intensity1 * 3, -1, ((Math.abs(BounceGame.mRNG.nextInt() % 2) + 10) << 12) / 3, length, 1);
				int intensity2 = (((Math.abs(BounceGame.mRNG.nextInt() % 2) + 24) << 12) << 1) / 9;
				insertSplash(intensity2, intensity2 * 3, 1, ((Math.abs(BounceGame.mRNG.nextInt() % 2) + 10) << 12) / 3, length, 1);
			}
		}
		return dataPos;
	}

	// p000.GameObject, p000.GameObject, p000.GameObject, p000.GameObject
	/* renamed from: a */
	//@Override
	public final void initialize() {
		this.bboxMinX = this.areaMinX << 16;
		this.bboxMaxX = this.areaMaxX << 16;
		this.bboxMinY = (this.areaMaxY << 16) - LP32.Int32ToLP32(30);
		this.bboxMaxY = (this.areaMinY << 16) + LP32.Int32ToLP32(30);
		this.surfaceY = this.areaMinY << 16;
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final void draw(Graphics graphics, DirectGraphics directGraphics, Matrix rootMatrix) {
		int airEmitDir;
		int airYMax;
		int airXMin;
		int airYMin;
		int airXMax;
		int baseSplash;
		int delta = GameRuntime.updateDelta * GameRuntime.getUpdatesPerDraw();
		if (isWater()) {
			if (!BounceGame.levelPaused) {
				this.ambientParticleTimer += delta;
				if (this.ambientParticleTimer > 150) {
					loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
					int swimPosXMin = GameObject.tmpObjMatrix.translationX + (this.areaMinX << 16);
					int swimPosXMax = GameObject.tmpObjMatrix.translationX + (this.areaMaxX << 16);
					int swimPosYMin = GameObject.tmpObjMatrix.translationY + (this.areaMaxY << 16);
					BounceGame.bubbleParticle.bubblePopY = GameObject.tmpObjMatrix.translationY + (this.areaMinY << 16);
					BounceGame.bubbleParticle.maxVelocityY = 60000;
					BounceGame.bubbleParticle.emitIndependentBursts(1, swimPosXMin, swimPosYMin, swimPosXMax, swimPosYMin, 0, 0, 0, 0, 4000, 666);
					this.ambientParticleTimer = 0;
				}
			}
			if (!BounceGame.levelPaused && this.region == REGION_SURFACE) {
				this.splashTimer += delta;
				for (int i = 0; i < this.splashYOffsets.length; i++) {
					this.splashYOffsets[i] = 0;
				}
				for (int splashIdx = 0; splashIdx < this.splashLimit; splashIdx++) {
					if (this.splashIntensity[splashIdx] != 0) {
						if (this.splashPermanence[splashIdx] != 1) {
							this.splashIntensity[splashIdx] -= ((delta * 28) >> 3);
							this.splashSpread[splashIdx] -= ((delta * 12) >> 3);
							this.splashSpeeds[splashIdx] -= ((delta * 4) >> 3);
						}
						this.splashXPos[splashIdx] += (((this.splashDirections[splashIdx] * this.splashSpeeds[splashIdx]) * delta) >> 6);
						if (this.splashSpread[splashIdx] <= 0 || this.splashSpeeds[splashIdx] <= 0) {
							this.splashIntensity[splashIdx] = 0;
						}
						if (this.splashIntensity[splashIdx] <= 0) {
							this.splashIntensity[splashIdx] = 0;
						} else if (this.splashXPos[splashIdx] < 0 || (this.splashXPos[splashIdx] >> 12) >= (this.maxSplashX >> 12)) {
							this.splashDirections[splashIdx] = (byte) (-this.splashDirections[splashIdx]); //splash in the other direction
							if (this.splashXPos[splashIdx] < 0) {
								this.splashXPos[splashIdx] = 0;
							} else if ((this.splashXPos[splashIdx] >> 12) >= (this.maxSplashX >> 12)) {
								this.splashXPos[splashIdx] = ((this.maxSplashX >> 12) - 1) << 12;
							}
							if (this.splashPermanence[splashIdx] != 1) {
								this.splashIntensity[splashIdx] -= (((delta << 1) * 28) >> 3);
								this.splashSpread[splashIdx] -= (((delta << 1) * 12) >> 3);
								this.splashSpeeds[splashIdx] -= (((delta << 1) * 4) >> 3);
							}
						}
						if (this.splashIntensity[splashIdx] > 0 && (baseSplash = this.splashXPos[splashIdx] >> 12) >= 0 && baseSplash < this.splashYOffsets.length) {
							this.splashYOffsets[baseSplash] += this.splashIntensity[splashIdx] >> 12;
							int maxSpread = this.splashSpread[splashIdx] >> 12;
							if (maxSpread == 0) {
								maxSpread = 1;
							}
							int i14 = 180 / maxSpread;
							for (int spread = 1; spread < maxSpread; spread++) {
								int increment = ((this.splashIntensity[splashIdx] * ((BounceGame.SIN_COS_TABLE[(i14 * spread) + 90] + 360) >> 1)) / 360) >> 12;
								if (baseSplash - spread >= 0) {
									this.splashYOffsets[baseSplash - spread] += increment;
								}
								if (baseSplash + spread < this.splashYOffsets.length) {
									this.splashYOffsets[baseSplash + spread] += increment;
								}
							}
						}
					}
				}
				for (int i = 0; i < this.splashYOffsets.length; i++) {
					if (i > 0
							&& this.splashYOffsets[i - 1] < this.splashYOffsets[i]
							&& i < this.splashYOffsets.length - 1
							&& this.splashYOffsets[i + 1] < this.splashYOffsets[i]) {
						this.splashYOffsets[i]--;
					}
				}
			}
			loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
			Matrix.multMatrices(rootMatrix, GameObject.tmpObjMatrix, Matrix.temp);
			Matrix.temp.mulVector(this.areaMinX << 16, this.areaMinY << 16);
			int minx = Matrix.vectorMulRslX >> 16;
			int miny = Matrix.vectorMulRslY >> 16;
			Matrix.temp.mulVector(this.areaMaxX << 16, this.areaMaxY << 16);
			int maxx = Matrix.vectorMulRslX >> 16;
			int maxy = Matrix.vectorMulRslY >> 16;
			int width = maxx - minx;
			if (this.region == REGION_SURFACE) {
				int length = (((width << 8) << 10) / this.splashYOffsets.length) >> 8;
				int[] xPoints = GeometryObject.TEMP_QUAD_XS;
				int[] yPoints = GeometryObject.TEMP_QUAD_YS;
				xPoints[0] = maxx;
				yPoints[0] = maxy;
				xPoints[1] = minx;
				yPoints[1] = maxy;
				int nPoints = 2;
				int vertIdx = 2;
				while (vertIdx < this.splashYOffsets.length + 2) {
					if (vertIdx > 2) {
						byte lastSplashY = this.splashYOffsets[vertIdx - 2];

						for (int scan = vertIdx + 1; scan < this.splashYOffsets.length + 2 && this.splashYOffsets[scan - 2] == lastSplashY; ++scan) {
							vertIdx++;
						}
					}
					if (vertIdx == this.splashYOffsets.length + 1) {
						xPoints[nPoints] = maxx;
						yPoints[nPoints] = miny;
					} else {
						xPoints[nPoints] = minx + (((vertIdx - 2) * length) >> 10);
						yPoints[nPoints] = miny - ((this.splashYOffsets[vertIdx - 2] * length) >> 10);
					}
					nPoints++;
					vertIdx++;
				}
				directGraphics.fillPolygon(xPoints, 0, yPoints, 0, nPoints, BounceGame.getStolenColorIfApplicable(this.color));
			} else {
				int[] polyX = GeometryObject.TEMP_QUAD_XS;
				int[] polyY = GeometryObject.TEMP_QUAD_YS;
				polyX[0] = minx;
				polyY[0] = miny;
				polyX[1] = maxx;
				polyY[1] = miny;
				polyX[2] = maxx;
				polyY[2] = maxy;
				polyX[3] = minx;
				polyY[3] = maxy;
				directGraphics.fillPolygon(polyX, 0, polyY, 0, 4, BounceGame.getStolenColorIfApplicable(this.color));
			}
		} else if (!BounceGame.levelPaused) { //air tunnel
			this.ambientParticleTimer += delta;
			if (this.ambientParticleTimer > 150) {
				loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
				int leftX = GameObject.tmpObjMatrix.translationX + (this.areaMinX << 16);
				int rightX = GameObject.tmpObjMatrix.translationX + (this.areaMaxX << 16);
				int topY = GameObject.tmpObjMatrix.translationY + (this.areaMaxY << 16);
				int bottomY = GameObject.tmpObjMatrix.translationY + (this.areaMinY << 16);
				int horizontalGravity = Math.abs(this.gravityXLeft);
				if (Math.abs(this.gravityXRight) > horizontalGravity) {
					horizontalGravity = Math.abs(this.gravityXRight);
				}
				int verticalGravity = Math.abs(this.gravityYTop);
				if (Math.abs(this.gravityYBottom) > verticalGravity) {
					verticalGravity = Math.abs(this.gravityYBottom);
				}
				int dispBase;
				if (this.gravityXLeft + this.gravityXRight > 0) {
					dispBase = horizontalGravity << 7;
					airEmitDir = 90;
					airYMax = bottomY;
					airXMin = leftX;
					airYMin = topY;
					airXMax = leftX;
				} else if (this.gravityXLeft + this.gravityXRight < 0) {
					dispBase = horizontalGravity << 7;
					airEmitDir = 270;
					airYMax = bottomY;
					airXMin = rightX;
					airYMin = topY;
					airXMax = rightX;
				} else if (this.gravityYTop + this.gravityYBottom > 0) {
					dispBase = verticalGravity << 7;
					airEmitDir = 0;
					airYMax = topY;
					airXMin = leftX;
					airYMin = topY;
					airXMax = rightX;
				} else {
					dispBase = verticalGravity << 7;
					airEmitDir = 180;
					airYMax = bottomY;
					airXMin = leftX;
					airYMin = bottomY;
					airXMax = rightX;
				}
				BounceGame.airTunnelParticle.emitIndependentBursts(1, airXMin, airYMin, airXMax, airYMax, dispBase, dispBase / 8, airEmitDir, 0, 2000, 333);
				this.ambientParticleTimer = 0;
			}
		}
	}

	private boolean isWater() {
		return color != COLOR_AIR_TUNNEL;
	}

	/* renamed from: a */
	private void insertSplash(int intensity, int spread, int direction, int speed, int xPos, int permanence) {
		for (int newIdx = 0; newIdx < this.splashLimit; newIdx++) {
			if (this.splashIntensity[newIdx] == 0) {
				this.splashIntensity[newIdx] = (intensity * 50) / 100;
				this.splashSpread[newIdx] = (spread * 50) / 100;
				this.splashDirections[newIdx] = (byte) direction;
				this.splashPermanence[newIdx] = (byte) permanence;
				this.splashSpeeds[newIdx] = (speed * 50) / 100;
				this.splashXPos[newIdx] = xPos;
				return;
			}
		}
	}

	/* renamed from: a */
	public final void onBounceSurfaceContact(int xposWeight, float splashIntensity, int radius, BounceObject bounce) {
		if (isWater()) {
			this.bounceBubbleTimer = 0;
			if (splashIntensity > 230.0f) {
				splashIntensity = 230.0f;
			} else if (splashIntensity < -230.0f) {
				splashIntensity = -230.0f;
			}
			int intensityAbs = (int) Math.abs(splashIntensity);
			if (this.region == 0) {
				int length = (int) ((((long) ((this.splashYOffsets.length - 1) << 12)) * ((long) xposWeight)) >> 16L);
				int i3 = (intensityAbs * 100 << 12) / 1500;
				int i4 = i3 << 1;
				insertSplash(i3, i4, -1, 20480, length - (radius << 2), 0);
				insertSplash(i3, i4, 1, 20480, length + (radius << 2), 0);
			}
			bounce.torqueX /= 3.0f;
			bounce.torqueY /= 3.0f;
			if (this.region == REGION_SURFACE) {
				loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
				int splashY = GameObject.tmpObjMatrix.translationY + (this.areaMinY << 16);
				int splashRange = (intensityAbs * 500) / 230;
				int splashParticleCount = (intensityAbs * 6) / 230;
				int splashLifespan = (intensityAbs * 800) / 230;
				BounceGame.waterSplashParticle.emitBurst(
						splashParticleCount,
						bounce.localObjectMatrix.translationX, //bugfix replace BounceGame.bounceObj with param bounce
						splashY,
						splashRange,
						splashRange >> 2,
						325,
						30,
						splashLifespan,
						splashLifespan / 6
				);
				BounceGame.waterSplashParticle.emitBurst(
						splashParticleCount,
						bounce.localObjectMatrix.translationX, //bugfix replace BounceGame.bounceObj with param bounce
						splashY,
						splashRange,
						splashRange >> 2,
						35,
						30,
						splashLifespan,
						splashLifespan / 6
				);
			}
		}
	}

	/* renamed from: a */
	public final void updateBounceSwim(int x, int y, BounceObject bounce) {
		this.bounceBubbleTimer += GameRuntime.updateDelta;
		if (!BounceGame.waterSingletonFlag) { //force recalc only one water block per update
			float xWeight = ((float) (x - this.bboxMinX)) / ((float) (this.bboxMaxX - this.bboxMinX));
			float yWeight = ((float) (y - this.bboxMinY)) / ((float) (this.bboxMaxY - this.bboxMinY));
			int antiGravityX;
			int antiGravityY;
			//This lerp may not seem very smart at first glance, but it reduces the number of floating point multiplications, which is actually great
			if (this.gravityXLeft < this.gravityXRight) {
				antiGravityX = ((int) (xWeight * (this.gravityXRight - this.gravityXLeft))) + this.gravityXLeft;
			} else {
				antiGravityX = ((int) ((1.0f - xWeight) * (this.gravityXLeft - this.gravityXRight))) + this.gravityXRight;
			}
			if (this.gravityYBottom < this.gravityYTop) {
				antiGravityY = ((int) (yWeight * (this.gravityYTop - this.gravityYBottom))) + this.gravityYBottom;
			} else {
				antiGravityY = ((int) ((1.0f - yWeight) * (this.gravityYBottom - this.gravityYTop))) + this.gravityYTop;
			}
			bounce.gravityX += (float) (antiGravityX << 5);
			bounce.gravityY += (float) (antiGravityY << 5);
			if (bounce.torqueX > 0.0f) {
				bounce.torqueX -= (GameRuntime.updateDelta * bounce.torqueX) / 400f;
				if (bounce.torqueX < 0.0f) {
					bounce.torqueX = 0.0f;
				}
			} else if (bounce.torqueX < 0.0f) {
				bounce.torqueX -= (GameRuntime.updateDelta * bounce.torqueX) / 400f;
				if (bounce.torqueX > 0.0f) {
					bounce.torqueX = 0.0f;
				}
			}
			if (bounce.torqueY > 0.0f) {
				bounce.torqueY -= (GameRuntime.updateDelta * bounce.torqueY) / 400f;
				if (bounce.torqueY < 0.0f) {
					bounce.torqueY = 0.0f;
				}
			} else if (bounce.torqueY < 0.0f) {
				bounce.torqueY -= (GameRuntime.updateDelta * bounce.torqueY) / 400f;
				if (bounce.torqueY > 0.0f) {
					bounce.torqueY = 0.0f;
				}
			}
			if (isWater()) {
				float invGravity = 1.0f / BounceObject.GRAVITY[bounce.ballForme];
				float slowdown = GameRuntime.updateDelta * 0.0014f;
				if (bounce.curXVelocity > 0.0f) {
					bounce.curXVelocity -= (bounce.curXVelocity * invGravity) * slowdown;
					if (bounce.curXVelocity < 0.0f) {
						bounce.curXVelocity = 0.0f;
					}
				} else {
					bounce.curXVelocity -= (bounce.curXVelocity * invGravity) * slowdown;
					if (bounce.curXVelocity > 0.0f) {
						bounce.curXVelocity = 0.0f;
					}
				}
				if (bounce.curYVelocity > 0.0f) {
					bounce.curYVelocity -= (invGravity * bounce.curYVelocity) * slowdown;
					if (bounce.curYVelocity < 0.0f) {
						bounce.curYVelocity = 0.0f;
					}
				} else {
					bounce.curYVelocity -= (invGravity * bounce.curYVelocity) * slowdown;
					if (bounce.curYVelocity > 0.0f) {
						bounce.curYVelocity = 0.0f;
					}
				}

				if (this.bounceBubbleTimer > 150) {
					loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
					BounceGame.bubbleParticle.bubblePopY = GameObject.tmpObjMatrix.translationY + (this.areaMinY << 16);
					BounceGame.bubbleParticle.maxVelocityY = 60000;
					BounceGame.bubbleParticle.emitTrail(EventObject.eventVars[4] / 60, bounce.localObjectMatrix.translationX, bounce.localObjectMatrix.translationY, BounceObject.BALL_DIMENS[0] << 15, 0, 0, 0, 0, 4000, 666);
					this.bounceBubbleTimer = 0;
				}
				bounce.zCoord = 8;
			}
			BounceGame.waterSingletonFlag = true;
		}
	}
}
