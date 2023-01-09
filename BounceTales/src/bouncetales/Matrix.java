package bouncetales;

/* renamed from: d */
public final class Matrix {

	public static Matrix temp = new Matrix(); //renamed from: a
	public static int vectorMulRslX; //renamed from: a
	public static int vectorMulRslY; //renamed from: b

	public int m00 = LP32.ONE; //renamed from: a
	public int m01; //renamed from: b
	public int translationX; //renamed from: c
	public int m10; //renamed from: d
	public int m11 = LP32.ONE; //renamed from: e
	public int translationY; //renamed from: f

	/* renamed from: a */
	public static void multMatrices(Matrix a, Matrix b, Matrix dest) {
		int _m01 = (int) (((((long) a.m00) * ((long) b.m01)) + (((long) a.m01) * ((long) b.m11))) >> 16);
		int _m10 = (int) (((((long) a.m10) * ((long) b.m00)) + (((long) a.m11) * ((long) b.m10))) >> 16);
		int _m00 = (int) (((((long) a.m00) * ((long) b.m00)) + (((long) a.m01) * ((long) b.m10))) >> 16);
		int _m11 = (int) (((((long) a.m10) * ((long) b.m01)) + (((long) a.m11) * ((long) b.m11))) >> 16);
		int _tx = (int) ((((((long) a.m00) * ((long) b.translationX)) + (((long) a.m01) * ((long) b.translationY))) >> 16) + ((long) a.translationX));
		int _ty = (int) ((((((long) a.m10) * ((long) b.translationX)) + (((long) a.m11) * ((long) b.translationY))) >> 16) + ((long) a.translationY));
		dest.m00 = _m00;
		dest.m01 = _m01;
		dest.translationX = _tx;
		dest.m10 = _m10;
		dest.m11 = _m11;
		dest.translationY = _ty;
	}

	/* renamed from: a */
	public final void setFromMatrix(Matrix src) {
		m00 = src.m00;
		m01 = src.m01;
		translationX = src.translationX;
		m10 = src.m10;
		m11 = src.m11;
		translationY = src.translationY;
	}

	/* renamed from: a */
	public final void setRotation(float f) {
		int cos = LP32.FP64ToLP32(Math.cos(f));
		int sin = LP32.FP64ToLP32(Math.sin(f));
		m00 = cos;
		m01 = -sin;
		m10 = sin;
		m11 = cos;
	}

	public final void setScale(int sx, int sy) {
		m00 = sx;
		m11 = sy;
	}

	public float getScaleX() {
		return LP32.LP32ToFP32(m00);
	}

	public float getScaleY() {
		return LP32.LP32ToFP32(m11);
	}

	/* renamed from: a */
	public final void mulVector(int x, int y) {
		vectorMulRslX = ((int) (((((long) m00) * ((long) x)) + (((long) m01) * ((long) y))) >> 16)) + translationX;
		vectorMulRslY = ((int) (((((long) m10) * ((long) x)) + (((long) m11) * ((long) y))) >> 16)) + translationY;
	}

	/* renamed from: b */
	public final void mulDirection(int x, int y) {
		vectorMulRslX = (int) (((((long) m00) * ((long) x)) + (((long) m01) * ((long) y))) >> 16);
		vectorMulRslY = (int) (((((long) m10) * ((long) x)) + (((long) m11) * ((long) y))) >> 16);
	}

	/* renamed from: b */
	public final void mul(Matrix other) {
		int _m01 = (int) (((((long) m00) * ((long) other.m01)) + (((long) m01) * ((long) other.m11))) >> 16);
		int _m10 = (int) (((((long) m10) * ((long) other.m00)) + (((long) m11) * ((long) other.m10))) >> 16);
		int _m00 = (int) (((((long) m00) * ((long) other.m00)) + (((long) m01) * ((long) other.m10))) >> 16);
		int _m11 = (int) (((((long) m10) * ((long) other.m01)) + (((long) m11) * ((long) other.m11))) >> 16);
		int _tx = (int) ((((((long) m00) * ((long) other.translationX)) + (((long) m01) * ((long) other.translationY))) >> 16) + ((long) translationX));
		int _ty = (int) ((((((long) m10) * ((long) other.translationX)) + (((long) m11) * ((long) other.translationY))) >> 16) + ((long) translationY));
		m01 = _m01;
		m10 = _m10;
		m00 = _m00;
		m11 = _m11;
		translationX = _tx;
		translationY = _ty;
	}

	/* renamed from: c */
	public final void invert(Matrix dest) {
		long j = ((((long) m00) * ((long) m11)) - (((long) m01) * ((long) m10))) >> 16;
		if (j != 0) {
			dest.m00 = (int) ((((long) m11) << 16) / j);
			dest.m01 = (int) ((((long) m10) << 16) / j);
			dest.m10 = (int) ((((long) m01) << 16) / j);
			dest.m11 = (int) ((((long) m00) << 16) / j);
			dest.translationX = -((int) (((((long) translationX) * ((long) dest.m00)) + (((long) translationY) * ((long) dest.m01))) >> 16));
			dest.translationY = -((int) (((((long) translationX) * ((long) dest.m10)) + (((long) translationY) * ((long) dest.m11))) >> 16));
		} else {
			throw new ArithmeticException("Non-invertible matrix.");
		}
	}

	public String toString() {
		return m00 + " " + m01 + " " + translationX + "\n" + m10 + " " + m11 + " " + translationY;
	}
}
