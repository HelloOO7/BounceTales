package bouncetales;

import com.nokia.mid.ui.DirectGraphics;
import javax.microedition.lcdui.Graphics;

/* renamed from: p */
public final class GeometryObject extends GameObject {
	
	public static final byte TYPEID = 2;

	//Global temp variables
	public static int[] TEMP_QUAD_XS; //renamed from: a
	public static int[] TEMP_QUAD_YS; //renamed from: b

	//Parameters
	public short event; //renamed from: a

	//Vertex data
	private int rgbColor;

	private short[] indexBuffer;
	public int[] xCoordBuffer;
	public int[] yCoordBuffer;

	//Render state
	private int[] xbuf_transformed;
	private int[] ybuf_transformed;

	public GeometryObject() {
		objType = TYPEID;
	}

	public int getVertexCount() {
		return xCoordBuffer.length;
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final int readData(byte[] src, int dataPos) {
		dataPos = super.readData(src, dataPos);
		int vertexCount = readShort(src, dataPos) + 1;
		int facepointCount = readShort(src, dataPos + 2);
		rgbColor = readInt(src, dataPos + 4);
		xCoordBuffer = new int[vertexCount];
		yCoordBuffer = new int[vertexCount];
		xbuf_transformed = new int[vertexCount];
		ybuf_transformed = new int[vertexCount];
		indexBuffer = new short[facepointCount];
		byte dataBitSize = (byte) src[dataPos + 8];
		short vertexXBase = readShort(src, dataPos + 9);
		dataPos = decomposeBytesToInts(xCoordBuffer, vertexCount - 1, vertexXBase, src, dataPos + 11, dataBitSize);
		short vertexYBase = readShort(src, dataPos);
		dataPos = decomposeBytesToInts(yCoordBuffer, vertexCount - 1, vertexYBase, src, dataPos + 2, dataBitSize);
		dataPos = decomposeBytesToShorts(indexBuffer, facepointCount, 0, 1, src, dataPos + 1, src[dataPos]);
		xCoordBuffer[vertexCount - 1] = xCoordBuffer[0];
		yCoordBuffer[vertexCount - 1] = yCoordBuffer[0];
		event = readShort(src, dataPos);
		dataPos += 2;
		initialize();
		return dataPos;
	}

	// p000.GameObject, p000.GameObject, p000.GameObject, p000.GameObject
	/* renamed from: a */
	//@Override
	public final void initialize() {
		super.initialize();
		for (int i = 0; i < xCoordBuffer.length; i++) {
			if (xCoordBuffer[i] < bboxMinX) {
				bboxMinX = xCoordBuffer[i];
			}
			if (xCoordBuffer[i] > bboxMaxX) {
				bboxMaxX = xCoordBuffer[i];
			}
		}
		for (int i = 0; i < yCoordBuffer.length; i++) {
			if (yCoordBuffer[i] < bboxMinY) {
				bboxMinY = yCoordBuffer[i];
			}
			if (yCoordBuffer[i] > bboxMaxY) {
				bboxMaxY = yCoordBuffer[i];
			}
		}
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final void draw(Graphics graphics, DirectGraphics directGraphics, Matrix rootMatrix) {
		if (geometryTransformIsDirty) {
			loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
			int tx = rootMatrix.translationX;
			int ty = rootMatrix.translationY;
			rootMatrix.translationX = 0;
			rootMatrix.translationY = 0;
			Matrix.multMatrices(rootMatrix, GameObject.tmpObjMatrix, Matrix.temp);
			rootMatrix.translationX = tx;
			rootMatrix.translationY = ty;
			for (int i = 0; i < xCoordBuffer.length; i++) {
				Matrix.temp.mulVector(xCoordBuffer[i], yCoordBuffer[i]);
				xbuf_transformed[i] = Matrix.vectorMulRslX >> 16;
				ybuf_transformed[i] = Matrix.vectorMulRslY >> 16;
			}
			geometryTransformIsDirty = false;
		}

		//Values used for culling if offscreen
		int maxX = GameRuntime.currentWidth - 1;
		int maxY = GameRuntime.currentHeight - 1;

		int tx = rootMatrix.translationX >> 16;
		int ty = rootMatrix.translationY >> 16;
		graphics.setColor(BounceGame.getStolenColorIfApplicable(rgbColor));
		for (int faceIdx = 0; faceIdx < indexBuffer.length; faceIdx += 3) {
			short v1 = indexBuffer[faceIdx];
			short v2 = indexBuffer[faceIdx + 1];
			short v3 = indexBuffer[faceIdx + 2];
			int x1 = xbuf_transformed[v1] + tx;
			int x2 = xbuf_transformed[v2] + tx;
			int x3 = xbuf_transformed[v3] + tx;
			int y1 = ybuf_transformed[v1] + ty;
			int y2 = ybuf_transformed[v2] + ty;
			int y3 = ybuf_transformed[v3] + ty;
			if ((x1 >= 0 || x2 >= 0 || x3 >= 0) && ((y1 >= 0 || y2 >= 0 || y3 >= 0) && ((x1 <= maxX || x2 <= maxX || x3 <= maxX) && (y1 <= maxY || y2 <= maxY || y3 <= maxY)))) {
				graphics.fillTriangle(x1, y1, x2, y2, x3, y3);
			}
		}

		debugDraw(graphics, 0xFF0000, rootMatrix);
	}
}
