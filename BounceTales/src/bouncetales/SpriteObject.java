package bouncetales;

import bouncetales.ext.rsc.ImageMap;
import com.nokia.mid.ui.DirectGraphics;
import javax.microedition.lcdui.Graphics;

/* renamed from: r */
public final class SpriteObject extends GameObject {

	public static final byte TYPEID = 5;

	//Parameters
	short[] imageIDs; //renamed from: a

	private short[] xCoords; //renamed from: c
	private short[] yCoords; //renamed from: d

	//State
	short[] actionImageIDs; //renamed from: b

	public SpriteObject() {
		this.objType = TYPEID;
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
		this.actionImageIDs = new short[count];
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
				this.actionImageIDs[i] = -1;
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
		int anmTime;
		int fadeColor;
		int actYPos;
		int anmFrame;
		int anmLength;
		loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
		Matrix.multMatrices(rootMatrix, GameObject.tmpObjMatrix, Matrix.temp);
		for (int componentIdx = 0; componentIdx < this.imageIDs.length; componentIdx++) {
			Matrix.temp.mulVector(this.xCoords[componentIdx] << 16, this.yCoords[componentIdx] << 16);
			int xpos = Matrix.vectorMulRslX >> 16;
			int ypos = Matrix.vectorMulRslY >> 16;
			if (this.actionImageIDs[componentIdx] > -1) {
				int anmProgress = imageIDs[componentIdx]; //field repurposed
				int normalAnmTime = anmProgress != 9999 ? anmProgress : BounceGame.levelTimer;
				if (anmProgress > 0 || this.actionImageIDs[componentIdx] != 474) {
					anmTime = normalAnmTime;
					fadeColor = 0;
				} else {
					anmTime = 1;
					fadeColor = (((Math.abs(anmProgress) * 255) / 1500) & 0xFF) << 24;
				}
				int xposAnim = Matrix.vectorMulRslX >> 16;
				int yposAnim = Matrix.vectorMulRslY >> 16;
				Graphics orgGraphics = GameRuntime.getGraphicsObj();
				if (fadeColor != 0) {
					BounceGame.spriteOffscreenGraphics.setColor(0x0000FF); //red - transparency key color
					BounceGame.spriteOffscreenGraphics.fillRect(0, 0, BounceGame.spriteFB.getWidth(), BounceGame.spriteFB.getHeight());
					int halfFBWidth = BounceGame.spriteFB.getWidth() >> 1;
					int height = BounceGame.spriteFB.getHeight();
					GameRuntime.setGraphics(BounceGame.spriteOffscreenGraphics);
					actYPos = height;
					xpos = halfFBWidth;
				} else {
					actYPos = ypos;
				}
				int anmFrameCount = GameRuntime.getImageAnimationFrameCount(this.actionImageIDs[componentIdx]);
				if (anmProgress != 9999) {
					switch (this.actionImageIDs[componentIdx]) {
						case 474:
							anmLength = 750;
							break;
						case 480:
						case 485:
							anmLength = 9999;
							break;
						default:
							anmLength = 0;
							break;
					}
					anmFrame = ((anmLength - anmTime) / getAnmMsPerFrame(this.actionImageIDs[componentIdx])) % anmFrameCount;
				} else {
					anmFrame = (anmTime / getAnmMsPerFrame(this.actionImageIDs[componentIdx])) % anmFrameCount;
				}
				if (anmFrame > anmFrameCount - 1) {
					anmFrame = anmFrameCount - 1;
				}
				GameRuntime.drawAnimatedImageRes(xpos, actYPos, this.actionImageIDs[componentIdx], anmFrame);
				if (fadeColor != 0) {
					GameRuntime.setGraphics(orgGraphics);
					BounceGame.spriteFB.getRGB(BounceGame.spriteFBRGB, 0, BounceGame.spriteFB.getWidth(), 0, 0, BounceGame.spriteFB.getWidth(), BounceGame.spriteFB.getHeight());
					int rgbIndex = 0;
					for (int y = 0; y < BounceGame.spriteFB.getHeight(); y++) {
						for (int x = 0; x < BounceGame.spriteFB.getWidth(); x++) {
							if (BounceGame.spriteFBRGB[rgbIndex] == 0xFF0000FF) {
								BounceGame.spriteFBRGB[rgbIndex] = 0;
							} else {
								BounceGame.spriteFBRGB[rgbIndex] -= fadeColor;
							}
							rgbIndex++;
						}
					}
					GameRuntime.getGraphicsObj().drawRGB(BounceGame.spriteFBRGB, 0, BounceGame.spriteFB.getWidth(), xposAnim - (BounceGame.spriteFB.getWidth() >> 1), yposAnim - BounceGame.spriteFB.getHeight(), BounceGame.spriteFB.getWidth(), BounceGame.spriteFB.getHeight(), true);
				}
			} else {
				GameRuntime.drawImageRes(xpos, ypos, this.imageIDs[componentIdx]);
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
			if (this.actionImageIDs[i] > -1) {
				if (this.imageIDs[i] != 9999) {
					this.imageIDs[i] -= GameRuntime.updateDelta;
				}
				if (this.imageIDs[i] > 0) {
				} else if (this.actionImageIDs[i] != 474) {
					despawn();
					return;
				} else if (this.imageIDs[i] < -1500) {
					despawn();
					return;
				}
			}
		}
	}

	//@Override
	public final void onPlayerContact() { //since 2.0.25
		for (int componentIdx = 0; componentIdx < imageIDs.length; componentIdx++) {
			short anmImage = -1;
			short anmLength = 0; //9999 = loop forever
			switch (imageIDs[componentIdx]) {
				case 118: //propeller flower
					anmImage = 485;
					anmLength = 9999;
					break;
				case 334: //bumpy cracks stone wall
					anmImage = 474;
					anmLength = 750;
					break;
				case 342: //color machine ray
					anmImage = 480;
					anmLength = 9999;
					break;
			}
			if (anmImage > -1) {
				actionImageIDs[componentIdx] = anmImage;
				imageIDs[componentIdx] = anmLength;
			}
		}
	}

	/* renamed from: a */
	private static int getAnmMsPerFrame(int i) {
		switch (i) {
			case 474:
			case 480:
			case 485:
				return 150;
			default:
				return 0;
		}
	}
}
