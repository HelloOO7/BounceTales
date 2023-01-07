package bouncetales;

import com.nokia.mid.ui.DirectGraphics;
import javax.microedition.lcdui.Graphics;

/* renamed from: n */
public final class ParticleObject extends GameObject {
	
	public static final byte TYPEID = 11;

	/* renamed from: a */
	private static boolean f369a = false;

	/* renamed from: g */
	private static int[] pointRotationResult = new int[2];

	/* renamed from: a */
	public int particleCount;

	/* renamed from: a */
	private byte[] f372a;

	/* renamed from: a */
	private int[] f373a;

	/* renamed from: a */
	private short[] f374a;

	/* renamed from: b */
	public int f375b;

	/* renamed from: b */
	private byte[] f376b;

	/* renamed from: b */
	private int[] particlePositionsX;

	/* renamed from: c */
	public int f378c;

	/* renamed from: c */
	private int[] particlePositionsY;

	/* renamed from: d */
	private int f380d;

	/* renamed from: d */
	private int[] f381d;

	/* renamed from: e */
	private int[] f382e;

	/* renamed from: f */
	private int[] f383f;

	/* renamed from: r */
	private int f384r;

	/* renamed from: s */
	private int f385s;

	/* renamed from: t */
	private int f386t;

	/* renamed from: u */
	private int f387u;

	/* renamed from: v */
	private int maxParticleCount;

	/* renamed from: w */
	private int f389w;

	/* renamed from: x */
	private int f390x;

	public ParticleObject(int i, int i2, int i3, int i4, int i5, int i6, int i7, int[] spriteIDs, int i8, int i9) {
		if (!f369a) {
			f369a = true;
		}
		this.f373a = new int[i];
		this.particlePositionsX = new int[i];
		this.particlePositionsY = new int[i];
		this.f381d = new int[i];
		this.f382e = new int[i];
		this.f376b = new byte[i];
		if (i7 == 4) {
			this.f374a = new short[i];
			this.f372a = new byte[i];
			for (int i10 = 0; i10 < i; i10++) {
				this.f374a[i10] = 0;
			}
		}
		this.maxParticleCount = i;
		this.f380d = 0;
		this.f384r = i3;
		this.f385s = 0;
		this.f386t = 0;
		this.f387u = i6;
		this.particleCount = -1;
		this.f389w = i7;
		this.objType = TYPEID;
		this.f383f = spriteIDs;
		this.f390x = i8;
		this.zCoord = (byte) i9;
	}

	/* renamed from: a */
	private static void rotatePoint(int angleDeg, int x, int y) {
		short sin = BounceGame.SIN_COS_TABLE[angleDeg % 360];
		short cos = BounceGame.SIN_COS_TABLE[(angleDeg + 90) % 360];
		pointRotationResult[0] = ((x * cos) - (y * sin)) / 360;
		pointRotationResult[1] = ((sin * x) + (cos * y)) / 360;
	}

	/* renamed from: a */
	public final void startEmitter(int count, int posX, int posY, int i4, int i5, int i6, int i7) {
		if (count + 1 + this.particleCount > this.maxParticleCount) {
			count = (this.maxParticleCount - this.particleCount) - 1;
		}
		for (int i8 = 0; i8 < count; i8++) {
			int i9 = (i8 * 360) / count;
			int i10 = i9 + 90 >= 360 ? (i9 + 90) - 360 : i9 + 90;
			this.particleCount++;
			this.f373a[this.particleCount] = i7 != 0 ? (BounceGame.mRNG.nextInt() % i7) + i6 : i6;
			this.particlePositionsX[this.particleCount] = posX;
			this.particlePositionsY[this.particleCount] = posY;
			this.f381d[this.particleCount] = BounceGame.SIN_COS_TABLE[i9] * i4;
			this.f382e[this.particleCount] = BounceGame.SIN_COS_TABLE[i10] * i4;
			if (this.f389w == 3) {
				this.f376b[this.particleCount] = (byte) (i8 % this.f383f.length);
			} else if (this.f389w != 4 || !equals(BounceGame.winParticle)) {
				this.f376b[this.particleCount] = (byte) Math.abs(BounceGame.mRNG.nextInt() % this.f383f.length);
			} else {
				byte abs = (byte) Math.abs(BounceGame.mRNG.nextInt() % this.f383f.length);
				if (abs > 1) {
					abs = (byte) Math.abs(BounceGame.mRNG.nextInt() % this.f383f.length);
				}
				this.f376b[this.particleCount] = abs;
			}
		}
	}

	/* renamed from: a */
	public final void mo69a(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
		if (i + 1 + this.particleCount > this.maxParticleCount) {
			i = (this.maxParticleCount - this.particleCount) - 1;
		}
		for (int i10 = 0; i10 < i; i10++) {
			int nextInt = i6 + (BounceGame.mRNG.nextInt() % ((i7 / 2) + 1));
			int i11 = nextInt - (nextInt >= 360 ? 360 : 0);
			int i12 = i11 + (i11 < 0 ? 360 : 0);
			int i13 = i12 + 90 >= 360 ? i12 - 270 : i12 + 90;
			int nextInt2 = i5 != 0 ? (BounceGame.mRNG.nextInt() % i5) + i4 : i4;
			this.particleCount++;
			this.f373a[this.particleCount] = i9 != 0 ? (BounceGame.mRNG.nextInt() % i9) + i8 : i8;
			this.particlePositionsX[this.particleCount] = i2;
			this.particlePositionsY[this.particleCount] = i3;
			this.f381d[this.particleCount] = BounceGame.SIN_COS_TABLE[i12] * nextInt2;
			this.f382e[this.particleCount] = nextInt2 * BounceGame.SIN_COS_TABLE[i13];
			if (this.f389w == 0) {
				if (i12 >= 35 && i12 <= 90) {
					this.f376b[this.particleCount] = 2;
				} else if (i12 < 35) {
					this.f376b[this.particleCount] = 3;
				} else if (i12 > 325) {
					this.f376b[this.particleCount] = 1;
				} else {
					this.f376b[this.particleCount] = 0;
				}
			} else if (this.f389w != 7) {
				this.f376b[this.particleCount] = (byte) Math.abs(BounceGame.mRNG.nextInt() % this.f383f.length);
			} else if (i6 == 0 || i6 == 180) {
				this.f376b[this.particleCount] = 0;
			} else {
				this.f376b[this.particleCount] = 1;
			}
		}
	}

	/* renamed from: a */
	public final void mo70a(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
		if (this.particleCount + 11 > this.maxParticleCount) {
			i = (this.maxParticleCount - this.particleCount) - 1;
		}
		for (int i11 = 0; i11 < i; i11++) {
			int nextInt = (BounceGame.mRNG.nextInt() % 200) + 800;
			this.particleCount++;
			this.f373a[this.particleCount] = (BounceGame.mRNG.nextInt() % 200) + 800;
			this.particlePositionsX[this.particleCount] = i2;
			this.particlePositionsY[this.particleCount] = i3;
			this.f381d[this.particleCount] = ((((BounceGame.mRNG.nextInt() % 30) << 10) + i6) * nextInt) >> 8;
			this.f382e[this.particleCount] = (nextInt * (((BounceGame.mRNG.nextInt() % 30) << 10) + i7)) >> 8;
			this.f376b[this.particleCount] = (byte) Math.abs(BounceGame.mRNG.nextInt() % this.f383f.length);
		}
	}

	/* renamed from: a */
	public final void mo71a(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11) {
		for (int i12 = 0; i12 < i; i12++) {
			mo69a(1, i2 + Math.abs(BounceGame.mRNG.nextInt() % ((i4 - i2) + 1)), i3 + Math.abs(BounceGame.mRNG.nextInt() % ((i5 - i3) + 1)), i6, i7, i8, i9, i10, i11);
		}
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final void draw(Graphics graphics, DirectGraphics directGraphics, Matrix rootMatrix) {
		boolean z;
		int i;
		int a = GameRuntime.updateDelta * GameRuntime.getUpdatesPerDraw();
		int particleIndex = 0;
		while (particleIndex < this.particleCount + 1) {
			f373a[particleIndex] = f373a[particleIndex] - a;
			if (this.f373a[particleIndex] > 0) {
				if (this.f387u != 0) {
					int[] iArr2 = this.f381d;
					iArr2[particleIndex] = iArr2[particleIndex] - (((this.f381d[particleIndex] * this.f387u) * a) >> 14);
					int[] iArr3 = this.f382e;
					iArr3[particleIndex] = iArr3[particleIndex] - (((this.f382e[particleIndex] * this.f387u) * a) >> 14);
				}
				int[] iArr4 = this.f381d;
				iArr4[particleIndex] = iArr4[particleIndex] + (this.f380d * a);
				int[] iArr5 = this.f382e;
				iArr5[particleIndex] = iArr5[particleIndex] + (this.f384r * a);
				if (this.f389w == 1 && this.f382e[particleIndex] > this.f378c) {
					this.f382e[particleIndex] = this.f378c;
				} else if (this.f389w == 4) {
					short[] sArr = this.f374a;
					sArr[particleIndex] = (short) (sArr[particleIndex] - a);
					if (this.f374a[particleIndex] <= 0) {
						this.f374a[particleIndex] = (short) (Math.abs(BounceGame.mRNG.nextInt() % 200) + 300);
						this.f372a[particleIndex] = (byte) (Math.abs(BounceGame.mRNG.nextInt()) & 3);
					}
					int i3 = a / 2;
					if (this.f372a[particleIndex] == 1) {
						rotatePoint(i3, this.f381d[particleIndex], this.f382e[particleIndex]);
						this.f381d[particleIndex] = pointRotationResult[0];
						this.f382e[particleIndex] = pointRotationResult[1];
					} else if (this.f372a[particleIndex] == 2) {
						rotatePoint(359 - i3, this.f381d[particleIndex], this.f382e[particleIndex]);
						this.f381d[particleIndex] = pointRotationResult[0];
						this.f382e[particleIndex] = pointRotationResult[1];
					}
				} else if (this.f389w == 5) {
					rotatePoint(a / 5, this.f381d[particleIndex], this.f382e[particleIndex]);
					this.f381d[particleIndex] = pointRotationResult[0];
					this.f382e[particleIndex] = pointRotationResult[1];
				}
				int[] iArr6 = this.particlePositionsX;
				iArr6[particleIndex] = iArr6[particleIndex] + (((this.f381d[particleIndex] + this.f385s) * a) >> 4);
				int[] iArr7 = this.particlePositionsY;
				iArr7[particleIndex] = iArr7[particleIndex] + (((this.f382e[particleIndex] + this.f386t) * a) >> 4);
				z = false;
			} else {
				z = true;
			}
			if (this.f389w == 1 && this.particlePositionsY[particleIndex] >= this.f375b) {
				z = true;
			}
			if (!z) {
				int b = GameRuntime.getImageAnimationFrameCount(this.f383f[this.f376b[particleIndex]]);
				int i4 = (this.f373a[particleIndex] * b) / this.f390x;
				if (i4 > b - 1) {
					i4 = b - 1;
				}
				rootMatrix.mulVector(this.particlePositionsX[particleIndex], this.particlePositionsY[particleIndex]);
				GameRuntime.drawAnimatedImageRes(Matrix.vectorMulRslX >> 16, Matrix.vectorMulRslY >> 16, this.f383f[this.f376b[particleIndex]], (b - 1) - i4);
				i = particleIndex;
			} else if (particleIndex == this.particleCount) {
				this.particleCount--;
				i = particleIndex;
			} else {
				this.f373a[particleIndex] = this.f373a[this.particleCount];
				this.particlePositionsX[particleIndex] = this.particlePositionsX[this.particleCount];
				this.particlePositionsY[particleIndex] = this.particlePositionsY[this.particleCount];
				this.f381d[particleIndex] = this.f381d[this.particleCount];
				this.f382e[particleIndex] = this.f382e[this.particleCount];
				this.particleCount--;
				i = particleIndex - 1;
			}
			particleIndex = i + 1;
		}
	}

	/* renamed from: b */
	public final void mo72b(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
		int nextInt;
		int nextInt2;
		for (int i11 = 0; i11 < i; i11++) {
			do {
				nextInt = BounceGame.mRNG.nextInt() % ((i4 * 2) + 1);
				nextInt2 = BounceGame.mRNG.nextInt() % ((i4 * 2) + 1);
			} while ((nextInt * nextInt) + (nextInt2 * nextInt2) > i4 * i4);
			mo69a(1, i2 + nextInt, i3 + nextInt2, 0, 0, 0, 0, i9, i10);
		}
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
}
