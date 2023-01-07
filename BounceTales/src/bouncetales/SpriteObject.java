package bouncetales;

import bouncetales.ext.rsc.ImageMap;
import com.nokia.mid.ui.DirectGraphics;
import javax.microedition.lcdui.Graphics;

/* renamed from: r */
public final class SpriteObject extends GameObject {
	
	public static final byte TYPEID = 5;

	/* renamed from: a */
	short[] imageIDs;

	/* renamed from: b */
	short[] f485b;

	/* renamed from: c */
	private short[] xCoords;

	/* renamed from: d */
	private short[] yCoords;

	public SpriteObject() {
		this.objType = TYPEID;
	}

	/* renamed from: a */
	private static int getMyAnmFrameCountFromImageID(int i) {
		switch (i) {
			case 474:
			case 480:
			case 485:
				return 150;
			default:
				return 0;
		}
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final int readData(byte[] bArr, int pos) {
		pos = super.readData(bArr, pos);
		int count = bArr[pos++] & 255;
		this.xCoords = new short[count];
		this.yCoords = new short[count];
		this.imageIDs = new short[count];
		this.f485b = new short[count];
		if (count > 0) {
			int bitSize = bArr[pos++] & 255;
			short baseX = readShort(bArr, pos);
			pos += 2;
			short baseY = readShort(bArr, pos);
			pos += 2;
			if (bitSize > 0) {
				pos = decomposeBytesToShorts(this.xCoords, count, baseX, 1, bArr, pos, bitSize);
				pos = decomposeBytesToShorts(this.yCoords, count, baseY, 1, bArr, pos, bitSize);
			} else {
				for (int i = 0; i < count; i++) {
					this.xCoords[i] = (short) baseX;
					this.yCoords[i] = (short) baseY;
				}
			}
			pos = decomposeBytesToShorts(this.imageIDs, count, 0, 1, bArr, pos, 16);
			for (int i = 0; i < count; i++) {
				this.f485b[i] = -1;
			}
		}
		return pos;
	}

	// p000.GameObject, p000.GameObject, p000.GameObject, p000.GameObject
	/* renamed from: a */
	//@Override
	public final void initialize() {
		super.initialize();
		for (int i = 0; i < this.imageIDs.length; i++) {
			int width = (GameRuntime.getImageMapParam((int) this.imageIDs[i], ImageMap.PARAM_WIDTH) << 16) / GameObject.screenSpaceMatrix.m00;
			int height = (GameRuntime.getImageMapParam((int) this.imageIDs[i], ImageMap.PARAM_HEIGHT) << 16) / GameObject.screenSpaceMatrix.m00;
			int a3 = (GameRuntime.getImageMapParam((int) this.imageIDs[i], ImageMap.PARAM_ORIGIN_X) << 16) / GameObject.screenSpaceMatrix.m00;
			int a4 = (GameRuntime.getImageMapParam((int) this.imageIDs[i], ImageMap.PARAM_ORIGIN_Y) << 16) / GameObject.screenSpaceMatrix.m00;
			int x = this.xCoords[i] - a3;
			int endX = width + x;
			int y = this.yCoords[i] - (height - a4);
			int endY = height + y;
			if (x < this.bboxMinX) {
				this.bboxMinX = x;
			}
			if (endX > this.bboxMaxX) {
				this.bboxMaxX = endX;
			}
			if (y < this.bboxMinY) {
				this.bboxMinY = y;
			}
			if (endY > this.bboxMaxY) {
				this.bboxMaxY = endY;
			}
		}
		this.bboxMinX <<= 16;
		this.bboxMaxX <<= 16;
		this.bboxMinY <<= 16;
		this.bboxMaxY <<= 16;
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override 
	public final void draw(Graphics graphics, DirectGraphics directGraphics, Matrix rootMatrix) {
		super.draw(graphics, directGraphics, rootMatrix);
		int i;
		int i2;
		int i3;
		int anmFrame;
		int i4;
		loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
		Matrix.multMatrices(rootMatrix, GameObject.tmpObjMatrix, Matrix.temp);
		for (int resIdx = 0; resIdx < this.imageIDs.length; resIdx++) {
			Matrix.temp.mulVector(this.xCoords[resIdx] << 16, this.yCoords[resIdx] << 16);
			int xpos = Matrix.vectorMulRslX >> 16;
			int ypos = Matrix.vectorMulRslY >> 16;
			if (this.f485b[resIdx] > -1) {
				int i8 = this.imageIDs[resIdx] != 9999 ? this.imageIDs[resIdx] : BounceGame.levelTimer;
				if (this.imageIDs[resIdx] > 0 || this.f485b[resIdx] != 474) {
					i = i8;
					i2 = 0;
				} else {
					i = 1;
					i2 = (((Math.abs((int) this.imageIDs[resIdx]) * 255) / 1500) & 255) << 24;
				}
				int i9 = Matrix.vectorMulRslX >> 16;
				int i10 = Matrix.vectorMulRslY >> 16;
				Graphics a2 = GameRuntime.getGraphicsObj();
				if (i2 != 0) {
					BounceGame.f265b.setColor(255);
					BounceGame.f265b.fillRect(0, 0, BounceGame.f266b.getWidth(), BounceGame.f266b.getHeight());
					int width = BounceGame.f266b.getWidth() >> 1;
					int height = BounceGame.f266b.getHeight();
					GameRuntime.setGraphics(BounceGame.f265b);
					i3 = height;
					xpos = width;
				} else {
					i3 = ypos;
				}
				int b = GameRuntime.getImageAnimationFrameCount((int) this.f485b[resIdx]);
				if (this.imageIDs[resIdx] != 9999) {
					switch (this.f485b[resIdx]) {
						case 474:
							i4 = 750;
							break;
						case 480:
						case 485:
							i4 = 9999;
							break;
						default:
							i4 = 0;
							break;
					}
					anmFrame = ((i4 - i) / getMyAnmFrameCountFromImageID(this.f485b[resIdx])) % b;
				} else {
					anmFrame = (i / getMyAnmFrameCountFromImageID(this.f485b[resIdx])) % b;
				}
				if (anmFrame > b - 1) {
					anmFrame = b - 1;
				}
				GameRuntime.drawAnimatedImageRes(xpos, i3, this.f485b[resIdx], anmFrame);
				if (i2 != 0) {
					GameRuntime.setGraphics(a2);
					BounceGame.f266b.getRGB(BounceGame.f270b, 0, BounceGame.f266b.getWidth(), 0, 0, BounceGame.f266b.getWidth(), BounceGame.f266b.getHeight());
					int i11 = 0;
					for (int i12 = 0; i12 < BounceGame.f266b.getHeight(); i12++) {
						for (int i13 = 0; i13 < BounceGame.f266b.getWidth(); i13++) {
							if (BounceGame.f270b[i11] == -16776961) {
								BounceGame.f270b[i11] = 0;
							} else {
								int[] iArr = BounceGame.f270b;
								iArr[i11] = iArr[i11] - i2;
							}
							i11++;
						}
					}
					GameRuntime.getGraphicsObj().drawRGB(BounceGame.f270b, 0, BounceGame.f266b.getWidth(), i9 - (BounceGame.f266b.getWidth() >> 1), i10 - BounceGame.f266b.getHeight(), BounceGame.f266b.getWidth(), BounceGame.f266b.getHeight(), true);
				}
			} else {
				GameRuntime.drawImageRes(xpos, ypos, this.imageIDs[resIdx]);
			}
		}
		debugDraw(graphics, 0, rootMatrix);
	}

	// p000.GameObject
	/* renamed from: d */
	//@Override 
	public final void updatePhysics() {
		super.updatePhysics();
		for (int i = 0; i < this.imageIDs.length; i++) {
			if (this.f485b[i] > -1) {
				if (this.imageIDs[i] != 9999) {
					this.imageIDs[i] -= GameRuntime.updateDelta;
				}
				if (this.imageIDs[i] > 0) {
				} else if (this.f485b[i] != 474) {
					despawn();
					return;
				} else if (this.imageIDs[i] < -1500) {
					despawn();
					return;
				}
			}
		}
	}
}
