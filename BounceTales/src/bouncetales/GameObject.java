package bouncetales;

import com.nokia.mid.ui.DirectGraphics;
import javax.microedition.lcdui.Graphics;

/* renamed from: j */
public class GameObject {

	protected static final boolean DEBUG_DRAW_ON = GameRuntime.getAppFlag("ObjectDrawDebug");

	public static final byte TYPEID_DUMMY = 1;

	/* renamed from: a */
	private static int f162a = 0;

	/* renamed from: a */
	public static final Matrix tmpObjMatrix = new Matrix();

	/* renamed from: a */
	public static GameObject cameraTarget;

	/* renamed from: a */
	private static GameObject[] objectsToRender = new GameObject[60];

	/* renamed from: b */
	private static int renderObjCount = 0;

	/* renamed from: b */
	protected static GameObject dummyParent = new GameObject();

	/* renamed from: c */
	private static short gameObjInstanceCount;

	/* renamed from: f */
	public static final Matrix cameraMatrix = new Matrix();

	/* renamed from: g */
	public static final Matrix screenSpaceMatrix = new Matrix();

	/* renamed from: i */
	private static Matrix inverseCameraMatrix = new Matrix();

	/* renamed from: j */
	private static Matrix rootMatrix = new Matrix();

	/* renamed from: k */
	private static Matrix inverseRootMatrix = new Matrix();

	/* renamed from: n */
	public static int f174n;

	/* renamed from: o */
	public static int f175o;

	/* renamed from: p */
	public static int cameraBounceFactor;

	/* renamed from: q */
	public static int cameraStabilizeSpeed;

	/* renamed from: a */
	private short parentIdx;

	/* renamed from: a */
	private boolean bboxIsDirty = true;

	/* renamed from: b */
	public byte zCoord;

	/* renamed from: b */
	public final Matrix renderCalcMatrix;

	/* renamed from: b */
	private short previousIdx;

	/* renamed from: c */
	protected byte objType = TYPEID_DUMMY;

	/* renamed from: c */
	public final Matrix localObjectMatrix;

	/* renamed from: c */
	private GameObject parentNode;

	/* renamed from: d */
	public final Matrix inverseRenderCalcMatrix;

	/* renamed from: d */
	private GameObject firstChildNode;

	/* renamed from: d */
	protected short objectId;

	/* renamed from: e */
	public int bboxMinX;

	/* renamed from: e */
	public final Matrix invAbsoluteObjectMatrix;

	/* renamed from: e */
	private GameObject nextNode;

	/* renamed from: e */
	public boolean renderMatrixIsDirty = true;

	/* renamed from: f */
	public int bboxMinY;

	/* renamed from: f */
	private GameObject previousNode;

	/* renamed from: f */
	public boolean objectMatrixIsDirty = true;

	/* renamed from: g */
	public int bboxMaxX;

	/* renamed from: g */
	public boolean geometryTransformIsDirty = true;

	/* renamed from: h */
	public int bboxMaxY;

	/* renamed from: h */
	private Matrix absoluteObjectMatrix;

	/* renamed from: i */
	public int allBBoxMinX;

	/* renamed from: j */
	public int allBBoxMinY;

	/* renamed from: k */
	public int allBBoxMaxX;

	/* renamed from: l */
	public int allBBoxMaxY;

	/* renamed from: m */
	public int flags;

	static {
		screenSpaceMatrix.m00 = 43266;
		screenSpaceMatrix.m11 = -43266;
		if (GameRuntime.currentWidth < 200) {
			screenSpaceMatrix.m00 = 22306;
			screenSpaceMatrix.m11 = -22306;
		}
	}

	public GameObject() {
		objectId = gameObjInstanceCount++;
		renderCalcMatrix = new Matrix();
		localObjectMatrix = new Matrix();
		absoluteObjectMatrix = new Matrix();
		inverseRenderCalcMatrix = new Matrix();
		invAbsoluteObjectMatrix = new Matrix();
	}

	public void makeIndependent() {
		GameObject objRoot = getObjectRoot();
		if (parentNode != null && parentNode != objRoot) {
			loadObjectMatrixToTarget(tmpObjMatrix);
			localObjectMatrix.setFromMatrix(tmpObjMatrix);
			setParent(objRoot);
		}
	}

	static void allocateRenderPool(int levelObjCount) {
		//high resolution 2D rendering needs this
		objectsToRender = new GameObject[levelObjCount];
		renderObjCount = 0;
	}

	/* renamed from: a */
	public static int decomposeBytesToInts(final int[] target, final int count, final int base, final byte[] src, int srcPos, int bitsPerInt) {
		int bitBuffer = 0;
		int bufIdx = 0;
		int index = 0;
		int bit = 1 << (bitsPerInt - 1);
		int mask = (1 << bitsPerInt) - 1;
		while (index < count) {
			final int oldBitsWithNewByte = bitBuffer | (src[srcPos++] << bufIdx);
			bufIdx += 8;
			bitBuffer = (oldBitsWithNewByte & ((1 << bufIdx) - 1));
			while (bitsPerInt <= bufIdx) {
				int maskedBits = (bitBuffer & mask);
				if ((maskedBits & bit) > 0) {
					maskedBits |= ~mask; //sign extend
				}
				if (index < count) {
					target[index++] = maskedBits + base << 16;
				}
				bufIdx -= bitsPerInt;
				bitBuffer >>>= bitsPerInt;
			}
		}
		return srcPos;
	}

	public static int decomposeBytesToShorts(final short[] target, final int count, final int base, int bitBuffer, final byte[] src, int srcPos, int bitsPerShort) {
		bitBuffer = 0;
		int bufIdx = 0;
		int index = 0;
		final int bit = 1 << bitsPerShort - 1;
		final int mask = (1 << bitsPerShort) - 1;
		while (index < count) {
			bitBuffer |= src[srcPos++] << bufIdx;
			bufIdx += 8;
			bitBuffer &= (1 << bufIdx) - 1;
			while (bitsPerShort <= bufIdx) {
				int result = (bitBuffer & mask);
				if ((result & bit) > 0) {
					result |= ~mask; //sign extend
				}
				if (index < count) {
					target[index++] = (short) (result + base);
				}
				bufIdx -= bitsPerShort;
				bitBuffer >>>= bitsPerShort;
			}
		}
		return srcPos;
	}

	/* renamed from: a */
	public static short readShort(byte[] bArr, int offset) {
		return (short) ((bArr[offset] << 8) | (bArr[offset + 1] & 255));
	}

	public static int readInt(byte[] bArr, int offset) {
		return (int) ((bArr[offset] & 255) << 24) | ((bArr[offset + 1] & 255) << 16) | ((bArr[offset + 2] & 255) << 8) | (bArr[offset + 3] & 255);
	}

	private static int[] tempBounds = new int[4];

	public boolean isInAABB(int[] screenAABB) {
		getBoundsAbs(tempBounds);
		return aabbIntersects(tempBounds[0], tempBounds[1], tempBounds[2], tempBounds[3], screenAABB[0], screenAABB[1], screenAABB[2], screenAABB[3]);
	}

	public static void getWorldMatrix(Matrix dest) {
		cameraMatrix.invert(inverseCameraMatrix);
		Matrix.multMatrices(screenSpaceMatrix, inverseCameraMatrix, dest);
		dest.translationX >>= 16;
		dest.translationX <<= 16;
		dest.translationY >>= 16;
		dest.translationY <<= 16;
	}

	public static void getScreenWorldAABB(Matrix invWorldMatrix, int[] dest) {
		int screenMinX;
		int screenMinY;
		invWorldMatrix.mulVector(0, 0);
		int screenBoundXMin = Matrix.vectorMulRslX;
		int screenBoundYMin = Matrix.vectorMulRslY;
		invWorldMatrix.mulVector(GameRuntime.currentWidth << 16, GameRuntime.currentHeight << 16);
		int screenMaxX = Matrix.vectorMulRslX;
		int screenMaxY = Matrix.vectorMulRslY;
		if (screenBoundXMin > screenMaxX) {
			screenMinX = Matrix.vectorMulRslX;
			screenMaxX = screenBoundXMin;
		} else {
			screenMinX = screenBoundXMin;
		}
		if (screenBoundYMin > screenMaxY) {
			screenMinY = Matrix.vectorMulRslY;
			screenMaxY = screenBoundYMin;
		} else {
			screenMinY = screenBoundYMin;
		}
		dest[0] = screenMinX;
		dest[1] = screenMinY;
		dest[2] = screenMaxX;
		dest[3] = screenMaxY;
	}

	protected static int[] screenAABB = new int[4];

	/* renamed from: a */
	public static void drawRootObject(GameObject root, Graphics g, DirectGraphics dg) {
		getWorldMatrix(rootMatrix);
		rootMatrix.invert(inverseRootMatrix);
		getScreenWorldAABB(inverseRootMatrix, screenAABB);
		renderObjCount = 0;
		GameObject currentObj = root;
		while (currentObj != null) {
			if (!(currentObj.objType == ParticleObject.TYPEID) && !currentObj.isInAABB(screenAABB)) {
				currentObj = currentObj.getNextNode(root);
			} else if ((currentObj.flags & 128) != 0) { //nondraw
				currentObj = currentObj.getNextNodeDescendToChildren(root);
			} else if (renderObjCount < objectsToRender.length) {
				objectsToRender[renderObjCount] = currentObj;
				renderObjCount++;
				currentObj = currentObj.getNextNodeDescendToChildren(root);
			} else {
				System.err.println("Rendering engine queue too small!");
				break;
			}
		}
		GameObject[] renderObjs = objectsToRender;
		int i11 = renderObjCount;
		if (renderObjs != null && i11 > 1) {
			if (i11 != 2) {
				m92a(renderObjs, 0, i11 - 1);
			} else if (renderObjs[0].zCoord < renderObjs[1].zCoord) {
				GameObject jVar3 = renderObjs[0];
				renderObjs[0] = renderObjs[1];
				renderObjs[1] = jVar3;
			}
		}
		for (int i = 0; i < renderObjCount; i++) {
			objectsToRender[i].draw(g, dg, rootMatrix);
		}
		renderObjCount = 0;
		for (int i = 0; i < objectsToRender.length; i++) {
			objectsToRender[i] = null;
		}
	}

	public int[] makeBoundsAbsolute(int[] dest, Matrix matrix) {
		tmpObjMatrix.setFromMatrix(matrix);
		tmpObjMatrix.mulVector(dest[0], dest[1]);
		int objMinX = Matrix.vectorMulRslX;
		int objMinY = Matrix.vectorMulRslY;
		int objMaxX = Matrix.vectorMulRslX;
		int objMaxY = Matrix.vectorMulRslY;
		tmpObjMatrix.mulVector(dest[0], dest[3]);
		if (Matrix.vectorMulRslX < objMinX) {
			objMinX = Matrix.vectorMulRslX;
		}
		if (Matrix.vectorMulRslY < objMinY) {
			objMinY = Matrix.vectorMulRslY;
		}
		if (Matrix.vectorMulRslX > objMaxX) {
			objMaxX = Matrix.vectorMulRslX;
		}
		if (Matrix.vectorMulRslY > objMaxY) {
			objMaxY = Matrix.vectorMulRslY;
		}
		tmpObjMatrix.mulVector(dest[2], dest[3]);
		if (Matrix.vectorMulRslX < objMinX) {
			objMinX = Matrix.vectorMulRslX;
		}
		if (Matrix.vectorMulRslY < objMinY) {
			objMinY = Matrix.vectorMulRslY;
		}
		if (Matrix.vectorMulRslX > objMaxX) {
			objMaxX = Matrix.vectorMulRslX;
		}
		if (Matrix.vectorMulRslY > objMaxY) {
			objMaxY = Matrix.vectorMulRslY;
		}
		tmpObjMatrix.mulVector(dest[2], dest[1]);
		if (Matrix.vectorMulRslX < objMinX) {
			objMinX = Matrix.vectorMulRslX;
		}
		if (Matrix.vectorMulRslY < objMinY) {
			objMinY = Matrix.vectorMulRslY;
		}
		if (Matrix.vectorMulRslX > objMaxX) {
			objMaxX = Matrix.vectorMulRslX;
		}
		if (Matrix.vectorMulRslY > objMaxY) {
			objMaxY = Matrix.vectorMulRslY;
		}
		dest[0] = objMinX;
		dest[1] = objMinY;
		dest[2] = objMaxX;
		dest[3] = objMaxY;
		return dest;
	}

	public int[] getLocalBoundsAbs(int[] dest, Matrix matrix) {
		dest[0] = bboxMinX;
		dest[1] = bboxMinY;
		dest[2] = bboxMaxX;
		dest[3] = bboxMaxY;
		makeBoundsAbsolute(dest, matrix);
		return dest;
	}

	public int[] getBoundsAbs(int[] dest) {
		dest[0] = allBBoxMinX;
		dest[1] = allBBoxMinY;
		dest[2] = allBBoxMaxX;
		dest[3] = allBBoxMaxY;
		loadObjectMatrixToTarget(tmpObjMatrix);
		makeBoundsAbsolute(dest, tmpObjMatrix);
		return dest;
	}

	/* renamed from: a */
	public static void makeObjectLinks(GameObject[] objects) {
		for (int i = 0; i < objects.length; i++) {
			GameObject obj = objects[i];
			if (obj.parentIdx > -1) {
				GameObject jVar2 = objects[obj.parentIdx];
				obj.parentNode = jVar2;
				if (jVar2.firstChildNode == null) {
					jVar2.firstChildNode = obj;
				}
			}
			if (obj.previousIdx > -1) {
				GameObject prevObj = objects[obj.previousIdx];
				obj.previousNode = prevObj;
				prevObj.nextNode = obj;
			}
		}
		for (int i = 0; i < objects.length; i++) {
			objects[i].renderMatrixIsDirty = true;
			objects[i].bboxIsDirty = true;
			objects[i].objectMatrixIsDirty = true;
			objects[i].recalcAbsObjectMatrix();
			objects[i].loadObjectMatrixToTarget(tmpObjMatrix);
			objects[i].renderCalcMatrix.setFromMatrix(tmpObjMatrix);
			objects[i].renderCalcMatrix.invert(objects[i].inverseRenderCalcMatrix);
		}
	}

	/* renamed from: a */
	private static final void m92a(GameObject[] jVarArr, int i, int i2) {
		if (i < i2) {
			GameObject jVar = jVarArr[i2];
			int i3 = i - 1;
			for (int i4 = i; i4 < i2; i4++) {
				if (jVar.zCoord < jVarArr[i4].zCoord) {
					i3++;
					GameObject jVar2 = jVarArr[i3];
					jVarArr[i3] = jVarArr[i4];
					jVarArr[i4] = jVar2;
				}
			}
			jVarArr[i2] = jVarArr[i3 + 1];
			jVarArr[i3 + 1] = jVar;
			int i5 = i3 + 1;
			m92a(jVarArr, i, i5 - 1);
			m92a(jVarArr, i5 + 1, i2);
		}
	}

	/* renamed from: a */
	public static boolean aabbContainsPoint(int x, int y, int minX, int minY, int maxX, int maxY) {
		return x >= minX && x <= maxX && y >= minY && y <= maxY;
	}

	/* renamed from: a */
	public static final boolean aabbCheckBoundCross(int i, int i2, int i3, int i4, int minX, int minY, int maxX, int maxY) {
		long dimX = (long) (maxX - minX);
		long dimY = (long) (maxY - minY);
		long dimX2 = (long) (i3 - i);
		long dimY2 = (long) (i4 - i2);
		long j5 = (long) (((i + i3) - minX) - maxX);
		long j6 = (long) (((i2 + i4) - minY) - maxY);
		long dimX2Abs = dimX2 > 0 ? dimX2 : -dimX2;
		if ((j5 > 0 ? j5 : -j5) > dimX + dimX2Abs) {
			return false;
		}
		long dimY2Abs = dimY2 > 0 ? dimY2 : -dimY2;
		if ((j6 > 0 ? j6 : -j6) > dimY + dimY2Abs) {
			return false;
		}
		long j9 = (j5 * dimY2) - (j6 * dimX2);
		if (j9 <= 0) {
			j9 = -j9;
		}
		return j9 <= (dimX * dimY2Abs) + (dimY * dimX2Abs);
	}

	/* renamed from: b */
	public static final int read32(byte[] bArr, int i) {
		return (int) (((((((0 | ((long) bArr[i])) << 8) | ((long) (bArr[i + 1] & 255))) << 8) | ((long) (bArr[i + 2] & 255))) << 8) | ((long) (bArr[i + 3] & 255)));
	}

	/* renamed from: b */
	public static void updateCamera(boolean instant) {
		int i;
		int i2;
		int i3 = 0;
		int delta = GameRuntime.updateDelta;
		cameraTarget.loadObjectMatrixToTarget(tmpObjMatrix);
		if (delta != 0) {
			i2 = (tmpObjMatrix.translationX - cameraTarget.renderCalcMatrix.translationX) / delta;
			i = (tmpObjMatrix.translationY - cameraTarget.renderCalcMatrix.translationY) / delta;
		} else {
			i = 0;
			i2 = 0;
		}
		int i5 = i2 << 7;
		int i6 = (-i) << 7;
		int i7 = tmpObjMatrix.translationX;
		int i8 = tmpObjMatrix.translationY;
		screenSpaceMatrix.invert(Matrix.temp);
		Matrix.temp.mulDirection(0, ((-GameRuntime.currentHeight) / 5) << 16);
		int i9 = Matrix.vectorMulRslX;
		int i10 = Matrix.vectorMulRslY;
		Matrix.temp.mulDirection(i5, i6);
		int cameraTx = Matrix.vectorMulRslX + i5 + i7 + i9;
		int cameraTy = Matrix.vectorMulRslY + i6 + i8 + i10;
		if (instant) {
			cameraMatrix.translationX = cameraTx;
			cameraMatrix.translationY = cameraTy;
		} else {
			int i13 = cameraTx - cameraMatrix.translationX;
			int i14 = cameraTy - cameraMatrix.translationY;
			if (Math.abs(i13) < 327680) {
				i13 = 0;
			}
			if (Math.abs(i14) >= 327680) {
				i3 = i14;
			}
			f162a += delta;
			while (f162a >= 15) {
				cameraMatrix.translationX += f174n * 15;
				cameraMatrix.translationY += f175o * 15;
				f174n += ((cameraBounceFactor * 15) * (i13 >> 6)) >> 14;
				f175o += ((cameraBounceFactor * 15) * (i3 >> 6)) >> 14;
				f174n -= ((cameraStabilizeSpeed * 15) * f174n) >> 14;
				f175o -= ((cameraStabilizeSpeed * 15) * f175o) >> 14;
				f162a -= 15;
			}
		}
	}

	/* renamed from: b */
	public static final boolean aabbIntersects(int minX, int minY, int maxX, int maxY, int checkMinX, int checkMinY, int checkMaxX, int checkMaxY) {
		return maxX >= checkMinX && minX <= checkMaxX && maxY >= checkMinY && minY <= checkMaxY;
	}

	/* renamed from: m */
	public static void snapCameraToTarget() {
		f174n = 0;
		f175o = 0;
		updateCamera(true);
	}

	/* renamed from: a */
	public byte getObjType() {
		return objType;
	}

	/* renamed from: a */
	public int readData(byte[] bArr, int i) {
		int pos = i;
		parentIdx = readShort(bArr, pos);
		pos += 2;
		previousIdx = readShort(bArr, pos);
		pos += 2;
		byte transformFlags = bArr[pos++];
		if ((transformFlags & 7) == 7) {
			localObjectMatrix.m00 = readInt(bArr, pos);
			pos += 4;
			localObjectMatrix.m01 = readInt(bArr, pos);
			pos += 4;
			localObjectMatrix.translationX = readInt(bArr, pos);
			pos += 4;
			localObjectMatrix.m10 = readInt(bArr, pos);
			pos += 4;
			localObjectMatrix.m11 = readInt(bArr, pos);
			pos += 4;
			localObjectMatrix.translationY = readInt(bArr, pos);
			pos += 4;
		} else {
			if ((transformFlags & 1) > 0) {
				localObjectMatrix.translationX = readShort(bArr, pos) << 16;
				pos += 2;
				localObjectMatrix.translationY = readShort(bArr, pos) << 16;
				pos += 2;
			}
			if ((transformFlags & 2) > 0) {
				localObjectMatrix.setRotation(LP32.LP32ToFP32(readInt(bArr, pos)));
				pos += 4;
			}
			if ((transformFlags & 4) > 0) {
				localObjectMatrix.setScale(readInt(bArr, pos), readInt(bArr, pos + 4));
				pos += 8;
			}
		}
		this.flags = readInt(bArr, pos);
		pos += 4;
		zCoord = (byte) ((this.flags & 31) - 16);
		renderCalcMatrix.setFromMatrix(localObjectMatrix);
		return pos;
	}

	/* renamed from: a */
	public final GameObject getObjectRoot() {
		GameObject rsl = this;
		while (rsl.parentNode != null) {
			rsl = rsl.parentNode;
		}
		return rsl;
	}

	/* renamed from: a */
	public final GameObject getNextNodeDescendToChildren(GameObject root) {
		if (root == null) {
			return null;
		}
		if (firstChildNode != null) {
			return firstChildNode;
		}
		if (this == root) {
			return null;
		}
		if (nextNode != null) {
			return nextNode;
		}
		GameObject parent = parentNode;
		while (parent != null && parent != root) {
			if (parent.nextNode != null) {
				return parent.nextNode;
			}
			parent = parent.parentNode;
		}
		return null;
	}

	/* renamed from: a */
	public final GameObject searchByObjId(short searchId) {
		GameObject result = this;
		while (result != null && result.objectId != searchId) {
			result = result.getNextNodeDescendToChildren(this);
		}
		return result;
	}

	/* renamed from: a */
	public final short getObjectId() {
		return objectId;
	}

	/* renamed from: a */
	public void initialize() {
		bboxMinX = Integer.MAX_VALUE;
		bboxMinY = Integer.MAX_VALUE;
		bboxMaxX = Integer.MIN_VALUE;
		bboxMaxY = Integer.MIN_VALUE;
	}

	/* renamed from: a */
	public final void loadObjectMatrixToTarget(Matrix dest) {
		if (objectMatrixIsDirty) {
			recalcAbsObjectMatrix();
		}
		dest.setFromMatrix(absoluteObjectMatrix);
	}

	/* renamed from: a */
	public void checkCollisions(GameObject jVar) {
	}

	/* renamed from: a */
	public void draw(Graphics graphics, DirectGraphics directGraphics, Matrix rootMatrix) {

	}

	/* renamed from: a */
	public final void setObjectId(short s) {
		objectId = s;
	}

	/* renamed from: a */
	public final boolean isChildOf(GameObject other) {
		for (GameObject parent = parentNode; parent != null; parent = parent.parentNode) {
			if (other == parent) {
				return true;
			}
		}
		return false;
	}

	/* renamed from: b */
	public final GameObject getNextNode(GameObject root) {
		if (root == null || this == root) {
			return null;
		}
		if (nextNode != null) {
			return nextNode;
		}
		GameObject parent = parentNode;
		while (parent != null && parent != root) {
			if (parent.nextNode != null) {
				return parent.nextNode;
			}
			parent = parent.parentNode;
		}
		return null;
	}

	/* renamed from: b */
	public final void setParent(GameObject parent) {
		if (parent.isChildOf(this)) {
			throw new IllegalArgumentException();
		} else if (parent == this) {
			throw new IllegalArgumentException();
		} else {
			despawn();
			nextNode = parent.firstChildNode;
			if (nextNode != null) {
				nextNode.previousNode = this;
			}
			parentNode = parent;
			parentNode.firstChildNode = this;
		}
	}

	/* renamed from: d */
	public void updatePhysics() {
		if (renderMatrixIsDirty) {
			if (parentNode != null) {
				Matrix.multMatrices(parentNode.renderCalcMatrix, localObjectMatrix, renderCalcMatrix);
			} else {
				renderCalcMatrix.setFromMatrix(localObjectMatrix);
			}
			renderCalcMatrix.invert(inverseRenderCalcMatrix);
			renderMatrixIsDirty = false;
		}
	}

	/* renamed from: h */
	public final void setIsDirtyRecursive() {
		GameObject obj = this;
		while (obj != null) {
			obj.renderMatrixIsDirty = true;
			obj.objectMatrixIsDirty = true;
			obj.geometryTransformIsDirty = true;
			obj = obj.getNextNodeDescendToChildren(this);
		}
	}

	/* renamed from: i */
	public final void setBBoxIsDirty() {
		GameObject obj = this;
		while (obj != null) {
			obj.bboxIsDirty = true;
			obj = obj.parentNode;
		}
	}

	/* renamed from: j */
	public final void despawn() {
		if (parentNode != null) {
			if (parentNode.firstChildNode == this) {
				parentNode.firstChildNode = nextNode;
				if (nextNode != null) {
					nextNode.previousNode = null;
				}
			} else {
				if (previousNode != null) {
					previousNode.nextNode = nextNode;
				}
				if (nextNode != null) {
					nextNode.previousNode = previousNode;
				}
			}
			parentNode = null;
			nextNode = null;
			previousNode = null;
		}
	}

	/* renamed from: k */
	public final void recalcAbsObjectMatrix() {
		absoluteObjectMatrix.setFromMatrix(localObjectMatrix);
		for (GameObject parent = parentNode; parent != null; parent = parent.parentNode) {
			Matrix.multMatrices(parent.localObjectMatrix, absoluteObjectMatrix, absoluteObjectMatrix);
		}
		absoluteObjectMatrix.invert(invAbsoluteObjectMatrix);
		objectMatrixIsDirty = false;
	}

	protected int[] get2DBoundsAbs(Matrix rootMatrix) {
		loadObjectMatrixToTarget(tmpObjMatrix);
		Matrix.multMatrices(rootMatrix, tmpObjMatrix, Matrix.temp);
		getLocalBoundsAbs(tempBounds, Matrix.temp);
		for (int i = 0; i < 4; i++) {
			tempBounds[i] >>= 16;
		}
		return new int[]{tempBounds[0], tempBounds[1], tempBounds[2] - tempBounds[0], tempBounds[3] - tempBounds[1]};
	}

	protected void drawBBox(Graphics graphics, Matrix rootMatrix) {
		int[] bounds = get2DBoundsAbs(rootMatrix);
		graphics.drawRect(bounds[0], bounds[1], bounds[2], bounds[3]);
	}

	protected void debugDraw(Graphics graphics, int color, Matrix rootMatrix) {
		if (DEBUG_DRAW_ON) {
			graphics.setColor(color);
			GameRuntime.setTextStyle(-3, 1);
			drawBBox(graphics, rootMatrix);
			int[] bounds = get2DBoundsAbs(rootMatrix);
			GameRuntime.drawText(toString(), 0, toString().length(), bounds[0] + 2, bounds[1] + 2, Graphics.TOP | Graphics.LEFT);
		}
	}

	/* renamed from: l */
	public final void updateBBox() {
		if (bboxIsDirty) {
			allBBoxMinX = bboxMinX;
			allBBoxMinY = bboxMinY;
			allBBoxMaxX = bboxMaxX;
			allBBoxMaxY = bboxMaxY;
			for (GameObject obj = firstChildNode; obj != null; obj = obj.nextNode) {
				obj.updateBBox();
				obj.localObjectMatrix.mulVector(obj.allBBoxMinX, obj.allBBoxMinY);
				if (Matrix.vectorMulRslX < allBBoxMinX) {
					allBBoxMinX = Matrix.vectorMulRslX;
				}
				if (Matrix.vectorMulRslY < allBBoxMinY) {
					allBBoxMinY = Matrix.vectorMulRslY;
				}
				if (Matrix.vectorMulRslX > allBBoxMaxX) {
					allBBoxMaxX = Matrix.vectorMulRslX;
				}
				if (Matrix.vectorMulRslY > allBBoxMaxY) {
					allBBoxMaxY = Matrix.vectorMulRslY;
				}
				obj.localObjectMatrix.mulVector(obj.allBBoxMinX, obj.allBBoxMaxY);
				if (Matrix.vectorMulRslX < allBBoxMinX) {
					allBBoxMinX = Matrix.vectorMulRslX;
				}
				if (Matrix.vectorMulRslY < allBBoxMinY) {
					allBBoxMinY = Matrix.vectorMulRslY;
				}
				if (Matrix.vectorMulRslX > allBBoxMaxX) {
					allBBoxMaxX = Matrix.vectorMulRslX;
				}
				if (Matrix.vectorMulRslY > allBBoxMaxY) {
					allBBoxMaxY = Matrix.vectorMulRslY;
				}
				obj.localObjectMatrix.mulVector(obj.allBBoxMaxX, obj.allBBoxMaxY);
				if (Matrix.vectorMulRslX < allBBoxMinX) {
					allBBoxMinX = Matrix.vectorMulRslX;
				}
				if (Matrix.vectorMulRslY < allBBoxMinY) {
					allBBoxMinY = Matrix.vectorMulRslY;
				}
				if (Matrix.vectorMulRslX > allBBoxMaxX) {
					allBBoxMaxX = Matrix.vectorMulRslX;
				}
				if (Matrix.vectorMulRslY > allBBoxMaxY) {
					allBBoxMaxY = Matrix.vectorMulRslY;
				}
				obj.localObjectMatrix.mulVector(obj.allBBoxMaxX, obj.allBBoxMinY);
				if (Matrix.vectorMulRslX < allBBoxMinX) {
					allBBoxMinX = Matrix.vectorMulRslX;
				}
				if (Matrix.vectorMulRslY < allBBoxMinY) {
					allBBoxMinY = Matrix.vectorMulRslY;
				}
				if (Matrix.vectorMulRslX > allBBoxMaxX) {
					allBBoxMaxX = Matrix.vectorMulRslX;
				}
				if (Matrix.vectorMulRslY > allBBoxMaxY) {
					allBBoxMaxY = Matrix.vectorMulRslY;
				}
			}
			if (allBBoxMaxX < allBBoxMinX | allBBoxMaxY < allBBoxMinY && allBBoxMinX != Integer.MAX_VALUE) {
				throw new RuntimeException("bbox misaligned: min " + allBBoxMinX + "x" + allBBoxMinY + " max " + allBBoxMaxX + "x" + allBBoxMaxY);
			}
			bboxIsDirty = false;
		}
	}

	//@Override
	public String toString() {
		String classname = getClass().getName();
		int idx = classname.lastIndexOf('.');
		if (idx != -1) {
			classname = classname.substring(idx + 1);
		}
		return classname + "|ID:" + getObjectId();
	}
}
