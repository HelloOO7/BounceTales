package bouncetales;

import com.nokia.mid.ui.DirectGraphics;
import javax.microedition.lcdui.Graphics;

/* renamed from: i */
public final class WaterObject extends GameObject {
	
	public static final byte TYPEID = 6;

	/* renamed from: a */
	private byte f137a;

	/* renamed from: a */
	int vertexCount = 0;

	/* renamed from: a */
	short areaMinX;

	/* renamed from: a */
	private byte[] splashYOffsets;

	/* renamed from: a */
	private int[] splashIntensity;

	/* renamed from: b */
	int surfaceY;

	/* renamed from: b */
	short areaMaxX;

	/* renamed from: b */
	private byte[] splashDirections;

	/* renamed from: b */
	private int[] splashSpread;

	/* renamed from: c */
	private int splashCount;

	/* renamed from: c */
	short areaMinY;

	/* renamed from: c */
	private byte[] splashPermanence;

	/* renamed from: c */
	private int[] splashSpeeds;

	/* renamed from: d */
	private byte f150d;

	/* renamed from: d */
	private int color;

	/* renamed from: d */
	private int[] splashXPos;

	/* renamed from: e */
	private byte f153e;

	/* renamed from: e */
	private short areaMaxY;

	/* renamed from: f */
	private byte f155f;

	/* renamed from: f */
	private short areaWidth;

	/* renamed from: g */
	private byte f157g;

	/* renamed from: r */
	private int f158r;

	/* renamed from: s */
	private int ripplingTimer = 0;

	/* renamed from: t */
	private int f160t;

	/* renamed from: u */
	private int f161u;

	public WaterObject() {
		this.objType = TYPEID;
	}

	/* renamed from: a */
	private void insertSplash(int intensity, int spread, int direction, int speed, int xPos, int permanence) {
		for (int newIdx = 0; newIdx < this.splashCount; newIdx++) {
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

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final int readData(byte[] bArr, int dataPos) {
		dataPos = super.readData(bArr, dataPos);
		this.areaMinX = readShort(bArr, dataPos);
		this.areaMaxY = readShort(bArr, dataPos + 2);
		this.areaMaxX = readShort(bArr, dataPos + 4);
		this.areaMinY = readShort(bArr, dataPos + 6);
		dataPos += 8;
		this.f137a = bArr[dataPos++];
		this.f150d = bArr[dataPos++];
		this.f153e = bArr[dataPos++];
		this.f155f = bArr[dataPos++];
		dataPos++; //skip alpha
		int i16 = 0x44000000
				| ((bArr[dataPos++] & 255) << 16)
				| ((bArr[dataPos++] & 255) << 8);
		int red = bArr[dataPos++] & 255;
		this.color = i16 | red;
		if (red == 16) {
			this.f157g = 1;
		} else {
			this.f157g = 0;
		}
		if (this.color != 0x44000000) {
			this.color = 0x441111EE;
		}
		this.areaWidth = (short) (this.areaMaxX - this.areaMinX);
		if (this.color != 0x44000000 && this.f157g == 0) {
			int maxSplashVerts = (this.areaWidth * 50) / 100;
			this.splashYOffsets = new byte[maxSplashVerts];
			this.f158r = maxSplashVerts << 12;
			this.f161u = 0;
			this.vertexCount = maxSplashVerts + 2;
			for (int i20 = 0; i20 < maxSplashVerts; i20++) {
				this.splashYOffsets[i20] = 0;
			}
			int i22 = maxSplashVerts / 20;
			this.splashCount = (i22 << 1) + 2;
			this.splashIntensity = new int[this.splashCount];
			this.splashSpread = new int[this.splashCount];
			this.splashDirections = new byte[this.splashCount];
			this.splashPermanence = new byte[this.splashCount];
			this.splashSpeeds = new int[this.splashCount];
			this.splashXPos = new int[this.splashCount];
			for (int i = 0; i < this.splashCount; i++) {
				this.splashIntensity[i] = 0;
			}
			for (int i = 2; i <= i22 - 2; i++) {
				int length = (((this.splashYOffsets.length - 1) << 12) * i) / i22;
				int abs = (((Math.abs(BounceGame.mRNG.nextInt() % 2) + 24) << 12) << 1) / 9;
				insertSplash(abs, abs * 3, -1, ((Math.abs(BounceGame.mRNG.nextInt() % 2) + 10) << 12) / 3, length, 1);
				int abs2 = (((Math.abs(BounceGame.mRNG.nextInt() % 2) + 24) << 12) << 1) / 9;
				insertSplash(abs2, abs2 * 3, 1, ((Math.abs(BounceGame.mRNG.nextInt() % 2) + 10) << 12) / 3, length, 1);
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

	/* renamed from: a */
	public final void onBounceSurfaceContact(int xposWeight, float splashIntensity, int i2, BounceObject bounce) {
		if (this.color != 0x44000000) {
			this.f160t = 0;
			if (splashIntensity > 230.0f) {
				splashIntensity = 230.0f;
			} else if (splashIntensity < -230.0f) {
				splashIntensity = -230.0f;
			}
			int intensityAbs = (int) Math.abs(splashIntensity);
			if (this.f157g == 0) {
				int length = (int) ((((long) ((this.splashYOffsets.length - 1) << 12)) * ((long) xposWeight)) >> 16L);
				int i3 = (intensityAbs * 100 << 12) / 1500;
				int i4 = i3 << 1;
				insertSplash(i3, i4, -1, 20480, length - (i2 << 2), 0);
				insertSplash(i3, i4, 1, 20480, length + (i2 << 2), 0);
			}
			bounce.f61j /= 3.0f;
			bounce.f63k /= 3.0f;
			if (this.f157g == 0) {
				loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
				int i5 = GameObject.tmpObjMatrix.translationY + (this.areaMinY << 16);
				int i6 = (intensityAbs * 500) / 230;
				int i7 = (intensityAbs * 6) / 230;
				int i8 = (intensityAbs * 800) / 230;
				BounceGame.f267b.mo69a(i7, BounceGame.bounceObj.localObjectMatrix.translationX, i5, i6, i6 >> 2, 325, 30, i8, i8 / 6);
				BounceGame.f267b.mo69a(i7, BounceGame.bounceObj.localObjectMatrix.translationX, i5, i6, i6 >> 2, 35, 30, i8, i8 / 6);
			}
		}
	}

	/* renamed from: a */
	public final void updateBounceSwim(int x, int y, BounceObject bounce) {
		int i3;
		int i4;
		this.f160t += GameRuntime.updateDelta;
		if (!BounceGame.f255a) {
			float xWeight = ((float) (x - this.bboxMinX)) / ((float) (this.bboxMaxX - this.bboxMinX));
			float yWeight = ((float) (y - this.bboxMinY)) / ((float) (this.bboxMaxY - this.bboxMinY));
			if (this.f155f < this.f150d) {
				i3 = ((int) (xWeight * ((float) (this.f150d - this.f155f)))) + this.f155f;
			} else {
				i3 = ((int) ((1.0f - xWeight) * ((float) (this.f155f - this.f150d)))) + this.f150d;
			}
			if (this.f153e < this.f137a) {
				i4 = ((int) (yWeight * ((float) (this.f137a - this.f153e)))) + this.f153e;
			} else {
				i4 = ((int) ((1.0f - yWeight) * ((float) (this.f153e - this.f137a)))) + this.f137a;
			}
			bounce.gravityX += (float) (i3 << 5);
			bounce.gravityY += (float) (i4 << 5);
			if (bounce.f61j > 0.0f) {
				bounce.f61j -= (((float) GameRuntime.updateDelta) * bounce.f61j) / 400f;
				if (bounce.f61j < 0.0f) {
					bounce.f61j = 0.0f;
				}
			} else if (bounce.f61j < 0.0f) {
				bounce.f61j -= (((float) GameRuntime.updateDelta) * bounce.f61j) / 400f;
				if (bounce.f61j > 0.0f) {
					bounce.f61j = 0.0f;
				}
			}
			if (bounce.f63k > 0.0f) {
				bounce.f63k -= (((float) GameRuntime.updateDelta) * bounce.f63k) / 400f;
				if (bounce.f63k < 0.0f) {
					bounce.f63k = 0.0f;
				}
			} else if (bounce.f63k < 0.0f) {
				bounce.f63k -= (((float) GameRuntime.updateDelta) * bounce.f63k) / 400f;
				if (bounce.f63k > 0.0f) {
					bounce.f63k = 0.0f;
				}
			}
			if (this.color != 0x44000000) {
				float f3 = 1.0f / BounceObject.GRAVITY[BounceGame.bounceObj.ballForme];
				float f4 = ((float) GameRuntime.updateDelta) * 0.0014f;
				if (BounceGame.bounceObj.curXVelocity > 0.0f) {
					BounceGame.bounceObj.curXVelocity -= (BounceGame.bounceObj.curXVelocity * f3) * f4;
					if (BounceGame.bounceObj.curXVelocity < 0.0f) {
						BounceGame.bounceObj.curXVelocity = 0.0f;
					}
				} else {
					BounceGame.bounceObj.curXVelocity -= (BounceGame.bounceObj.curXVelocity * f3) * f4;
					if (BounceGame.bounceObj.curXVelocity > 0.0f) {
						BounceGame.bounceObj.curXVelocity = 0.0f;
					}
				}
				if (BounceGame.bounceObj.curYVelocity > 0.0f) {
					BounceGame.bounceObj.curYVelocity -= (f3 * BounceGame.bounceObj.curYVelocity) * f4;
					if (BounceGame.bounceObj.curYVelocity < 0.0f) {
						BounceGame.bounceObj.curYVelocity = 0.0f;
					}
				} else {
					BounceGame.bounceObj.curYVelocity -= (f3 * BounceGame.bounceObj.curYVelocity) * f4;
					if (BounceGame.bounceObj.curYVelocity > 0.0f) {
						BounceGame.bounceObj.curYVelocity = 0.0f;
					}
				}
			}
			if (this.f160t > 150 && this.color != 0x44000000) {
				loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
				BounceGame.swimParticle.f375b = GameObject.tmpObjMatrix.translationY + (this.areaMinY << 16);
				BounceGame.swimParticle.f378c = 60000;
				BounceGame.swimParticle.mo72b(EventObject.eventVars[4] / 60, BounceGame.bounceObj.localObjectMatrix.translationX, BounceGame.bounceObj.localObjectMatrix.translationY, BounceObject.BALL_DIMENS[0] << 15, 0, 0, 0, 0, 4000, 666);
				this.f160t = 0;
			}
			if (this.color != 0x44000000) {
				BounceGame.bounceObj.zCoord = 8;
			}
			BounceGame.f255a = true;
		}
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final void draw(Graphics graphics, DirectGraphics directGraphics, Matrix dVar) {
		int i2;
		int i3;
		int i4;
		int i5;
		int i6;
		int baseSplash;
		int delta = GameRuntime.updateDelta * GameRuntime.getUpdatesPerDraw();
		if (this.color != 0x44000000) {
			if (!BounceGame.levelPaused) {
				this.f161u += delta;
				if (this.f161u > 150) {
					loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
					int i8 = GameObject.tmpObjMatrix.translationX + (this.areaMinX << 16);
					int i9 = GameObject.tmpObjMatrix.translationX + (this.areaMaxX << 16);
					int i10 = GameObject.tmpObjMatrix.translationY + (this.areaMaxY << 16);
					BounceGame.swimParticle.f375b = GameObject.tmpObjMatrix.translationY + (this.areaMinY << 16);
					BounceGame.swimParticle.f378c = 60000;
					BounceGame.swimParticle.mo71a(1, i8, i10, i9, i10, 0, 0, 0, 0, 4000, 666);
					this.f161u = 0;
				}
			}
			if (!BounceGame.levelPaused && this.f157g == 0) {
				this.ripplingTimer += delta;
				for (int i = 0; i < this.splashYOffsets.length; i++) {
					this.splashYOffsets[i] = 0;
				}
				for (int splashIdx = 0; splashIdx < this.splashCount; splashIdx++) {
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
						} else if (this.splashXPos[splashIdx] < 0 || (this.splashXPos[splashIdx] >> 12) >= (this.f158r >> 12)) {
							this.splashDirections[splashIdx] = (byte) (-this.splashDirections[splashIdx]); //splash in the other direction
							if (this.splashXPos[splashIdx] < 0) {
								this.splashXPos[splashIdx] = 0;
							} else if ((this.splashXPos[splashIdx] >> 12) >= (this.f158r >> 12)) {
								this.splashXPos[splashIdx] = ((this.f158r >> 12) - 1) << 12;
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
			Matrix.multMatrices(dVar, GameObject.tmpObjMatrix, Matrix.temp);
			Matrix.temp.mulVector(this.areaMinX << 16, this.areaMinY << 16);
			int minx = Matrix.vectorMulRslX >> 16;
			int miny = Matrix.vectorMulRslY >> 16;
			Matrix.temp.mulVector(this.areaMaxX << 16, this.areaMaxY << 16);
			int maxx = Matrix.vectorMulRslX >> 16;
			int maxy = Matrix.vectorMulRslY >> 16;
			int width = maxx - minx;
			if (this.f157g == 0) {
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
				int convColor = BounceGame.getStolenColorIfApplicable(this.color);
				int[] iArr10 = GeometryObject.TEMP_QUAD_XS;
				int[] iArr11 = GeometryObject.TEMP_QUAD_YS;
				iArr10[0] = minx;
				iArr11[0] = miny;
				iArr10[1] = maxx;
				iArr11[1] = miny;
				iArr10[2] = maxx;
				iArr11[2] = maxy;
				iArr10[3] = minx;
				iArr11[3] = maxy;
				directGraphics.fillPolygon(iArr10, 0, iArr11, 0, 4, convColor);
			}
		} else if (!BounceGame.levelPaused) {
			this.f161u += delta;
			if (this.f161u > 150) {
				loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
				int i29 = GameObject.tmpObjMatrix.translationX + (this.areaMinX << 16);
				int i30 = GameObject.tmpObjMatrix.translationX + (this.areaMaxX << 16);
				int i31 = GameObject.tmpObjMatrix.translationY + (this.areaMaxY << 16);
				int i32 = GameObject.tmpObjMatrix.translationY + (this.areaMinY << 16);
				int abs = Math.abs((int) this.f155f);
				if (Math.abs((int) this.f150d) > abs) {
					abs = Math.abs((int) this.f150d);
				}
				int abs2 = Math.abs((int) this.f137a);
				if (Math.abs((int) this.f153e) > abs2) {
					abs2 = Math.abs((int) this.f153e);
				}
				int i;
				if (this.f155f + this.f150d > 0) {
					i = abs << 7;
					i2 = 90;
					i3 = i32;
					i4 = i29;
					i5 = i31;
					i6 = i29;
				} else if (this.f155f + this.f150d < 0) {
					i = abs << 7;
					i2 = 270;
					i3 = i32;
					i4 = i30;
					i5 = i31;
					i6 = i30;
				} else if (this.f137a + this.f153e > 0) {
					i = abs2 << 7;
					i2 = 0;
					i3 = i31;
					i4 = i29;
					i5 = i31;
					i6 = i30;
				} else {
					i = abs2 << 7;
					i2 = 180;
					i3 = i32;
					i4 = i29;
					i5 = i32;
					i6 = i30;
				}
				BounceGame.waterParticle.mo71a(1, i4, i5, i6, i3, i, i / 8, i2, 0, 2000, 333);
				this.f161u = 0;
			}
		}
	}
}
