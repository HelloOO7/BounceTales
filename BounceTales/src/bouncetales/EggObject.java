package bouncetales;

import com.nokia.mid.ui.DirectGraphics;
import javax.microedition.lcdui.Graphics;

/* renamed from: a */
public final class EggObject extends GameObject {
	
	public static final byte TYPEID = 9;

	public EggObject() {
		this.objType = TYPEID;
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override 
	public final int readData(byte[] input, int offset) {
		return super.readData(input, offset);
	}

	// p000.GameObject, p000.GameObject, p000.GameObject, p000.GameObject
	/* renamed from: a */
	//@Override
	public final void initialize() {
		this.bboxMinX = LP32.FP32ToLP32(-45f);
		this.bboxMaxX = LP32.FP32ToLP32(45f);
		this.bboxMinY = LP32.FP32ToLP32(-45f);
		this.bboxMaxY = LP32.FP32ToLP32(45f);
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final void draw(Graphics graphics, DirectGraphics directGraphics, Matrix dVar) {
		loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
		Matrix.multMatrices(dVar, GameObject.tmpObjMatrix, Matrix.temp);
		GameRuntime.drawImageRes(Matrix.temp.translationX >> 16, Matrix.temp.translationY >> 16, 208);
		debugDraw(graphics, 0xFFBF00, dVar);
	}
}
