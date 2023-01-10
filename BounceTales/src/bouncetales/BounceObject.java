package bouncetales;

import com.nokia.mid.ui.DirectGraphics;
import javax.microedition.lcdui.Graphics;

/* renamed from: c */
public final class BounceObject extends GameObject {

	public static final byte TYPEID = 4;

	public static final int FORME_BOUNCE = 0;
	public static final int FORME_BUMPY_CRACKS = 1;
	public static final int FORME_WOLLY = 2;

	/*
	Constants
	 */
	//Draw - eyes
	private static final int[] EYE_ANIMATION_IMAGE_IDS = {458, 460, 454, 452, 456}; //renamed from: c

	//Draw - bounce
	private final int BOUNCE_PRIMARY_COLOR = 0xED1C24; //renamed from: u
	private final int BOUNCE_SECONDARY_COLOR = 0xAF1100; //renamed from: w
	private final int BOUNCE_HIGHLIGHT_COLOR = 0xFFFFFF; //renamed from: v

	//Draw - Bumpy Cracks
	private static final short[] BUMPY_CRACKS_ROTATION_SPRITES = {
		211, 212, 223, 234, 237, 238, 239, 240, 241, 242, 213,
		214, 215, 216, 217, 218, 219, 220, 221, 222, 224, 225,
		226, 227, 228, 229, 230, 231, 232, 233, 235, 236
	}; //renamed from: a

	//Draw - Wolly
	private static final int[] WOLLY_SEGMENT_COLORS = {
		0xEDEDED,
		0x0064BC,
		0xEDEDED,
		0xE11900,
		0xEDEDED,
		0xE8DF05
	}; //renamed from: p

	//Collisions
	private static final int MAX_COLLISION_POINTS = 16;
	private static final float LP32_TO_FP32_MULTIPLIER = 1.5258789E-5f; //renamed from: s

	//Dimensions
	public static final int[] BALL_DIMENS = {20, 20, 20}; //renamed from: a
	public static int[] BALL_DIMENS_SCREENSPACE = new int[3]; //renamed from: b
	
	static {
		updateScreenSpaceConstants();
	}

	//Physics
	private static final float BASE_GRAVITY_X = 0.0f; //renamed from: l
	private static final float BASE_GRAVITY_Y = -400.0f; //renamed from: m
	public static final float[] GRAVITY = {1.0f, 1.4f, 0.5f}; //renamed from: a

	private static final float[] f19b = {0.6f, 0.3f, 0.6f}; //renamed from: b
	private static final float[] f22c = {0.1f, 0.1f, 0.1f}; //renamed from: c

	private static final float[] MAX_JUMP_SLOPE_INV = {
		(float) Math.cos(0.9599310755729675d), //cos 55deg
		(float) Math.cos(0.9599310755729675d),
		(float) Math.cos(0.9599310755729675d)
	}; //renamed from: d

	private static final float[] MOVEMENT_TRACTIONS = {500.0f, 350.0f, 312.5f}; //renamed from: e
	private static final float[] MOVEMENT_SPEEDS = {280.0f, 350.0f, 150.0f}; //renamed from: f
	private static final float[] MIDAIR_MOVEMENT_SUPPRESSION = {1.5f, 2.5f, 1.2f}; //renamed from: g

	private static final float[] JUMP_MODIFIERS = {288.0f, 255.0f, 162.5f}; //renamed from: h

	//Global state - AABB collisions
	private static boolean aabbRayResult; //renamed from: i
	private static int aabbRayX; //renamed from: x
	private static int aabbRayY; //renamed from: y
	private static int aabbRayWeight; //renamed from: z

	//Parameters
	private boolean isPlayer = false; //renamed from: h

	//State - eye animation
	//made these non-static to allow for more BounceObjects
	public int idleAnimStartTimer; //renamed from: b
	public int eyeFrame; //renamed from: c
	private int idleAnimTimer; //renamed from: s

	//State - visuals
	public boolean isVisible = true; //renamed from: b
	public int fadeColor = 0; //renamed from: a

	//State - physics
	public boolean enablePhysics = true; //renamed from: a

	public boolean isGrounded = false; //renamed from: d

	private float airTimeCounter; //renamed from: p

	public float curXVelocity; //renamed from: a
	public float curYVelocity; //renamed from: b
	public float curVelocity; //renamed from: c

	public float lastXVelocity; //renamed from: h
	public float lastYVelocity; //renamed from: i

	private float slopeSinAbs; //renamed from: q
	private float slopeCosAbs; //renamed from: r

	private float torqueFalloff = 0.5f; //renamed from: o

	public float torqueX; //renamed from: j
	public float torqueY; //renamed from: k
	private float rotation; //renamed from: n

	public float gravityX; //renamed from: d
	public float gravityY; //renamed from: e

	public float pushX; //renamed from: f
	public float pushY; //renamed from: g

	public int ballForme = 0; //renamed from: d

	//State - stretch
	public boolean reqSkipAccelStretch = false; //renamed from: c

	private int[] reqStretchMagnitudes; //renamed from: d
	private int[] stretchMagnitudes; //renamed from: g

	private int[] stretchResults; //renamed from: e
	private int[] stretchResultsAbs; //renamed from: f

	private int[] stretchDirBalance; //renamed from: h
	private int[] stretchBuffer; //renamed from: i

	//State - collision
	private int collPointCount; //renamed from: t

	private final boolean[] f41a = new boolean[MAX_COLLISION_POINTS]; //renamed from: a

	private final int[] collPointsX = new int[MAX_COLLISION_POINTS]; //renamed from: j
	private final int[] collPointsY = new int[MAX_COLLISION_POINTS]; //renamed from: k

	private final int[] f65l = new int[MAX_COLLISION_POINTS]; //renamed from: l
	private final int[] f66m = new int[MAX_COLLISION_POINTS]; //renamed from: m

	private final int[] f68n = new int[MAX_COLLISION_POINTS]; //renamed from: n
	private final int[] f70o = new int[MAX_COLLISION_POINTS]; //renamed from: o

	//State - Super Bounce
	private int superBounceParticleTimer = 0; //renamed from: r

	public BounceObject(boolean isPlayer) {
		this.objType = TYPEID;
		this.isPlayer = isPlayer;
		if (isPlayer) {
			this.reqStretchMagnitudes = new int[4];
			this.stretchResults = new int[4];
			this.stretchResultsAbs = new int[4];
			this.stretchMagnitudes = new int[4];
			this.stretchDirBalance = new int[4];
			this.stretchBuffer = new int[4];
			resetStretch();
		}
	}

	// p000.GameObject, p000.GameObject, p000.GameObject, p000.GameObject
	/* renamed from: a */
	//@Override
	public final void initialize() {
		this.bboxMinX = (-BALL_DIMENS[this.ballForme]) << 16;
		this.bboxMaxX = BALL_DIMENS[this.ballForme] << 16;
		this.bboxMinY = (-BALL_DIMENS[this.ballForme]) << 16;
		this.bboxMaxY = BALL_DIMENS[this.ballForme] << 16;
	}

	/* renamed from: a */
	public final void setPosXY(int posX, int posY) {
		resetPhysics();
		this.localObjectMatrix.translationX = posX;
		this.localObjectMatrix.translationY = posY;
		this.renderCalcMatrix.translationX = posX;
		this.renderCalcMatrix.translationY = posY;
		this.reqSkipAccelStretch = true;
		if (this.isPlayer) {
			this.lastXVelocity = 0.0f;
			this.lastYVelocity = 0.0f;
			for (int i3 = 0; i3 < 4; i3++) {
				this.reqStretchMagnitudes[i3] = 0;
				this.stretchResults[i3] = 0;
				this.stretchResultsAbs[i3] = 0;
				this.stretchMagnitudes[i3] = 0;
			}
		}
		this.objectMatrixIsDirty = true;
	}

	public static void updateScreenSpaceConstants() {
		BALL_DIMENS_SCREENSPACE[0] = ((BALL_DIMENS[0] * GameObject.screenSpaceMatrix.m00) >> 16) + 1;
		BALL_DIMENS_SCREENSPACE[1] = ((BALL_DIMENS[1] * GameObject.screenSpaceMatrix.m00) >> 16) + 1;
		BALL_DIMENS_SCREENSPACE[2] = ((BALL_DIMENS[2] * GameObject.screenSpaceMatrix.m00) >> 16) + 1;
	}

	// p000.GameObject, p000.GameObject, p000.GameObject
	/* renamed from: a */
	//@Override
	public final void checkCollisions(GameObject startNode) {
		int higherX;
		int lowerX;
		int higherY;
		int lowerY;
		int xmax;
		int xmin;
		int ymax;
		int ymin;
		int i9;
		boolean z;
		for (int i10 = 2; i10 < 10; i10++) {
			this.collPointCount = 0;
			int ballDiameter = BALL_DIMENS[this.ballForme] << 16;
			int ballDiameterSquared = (BALL_DIMENS[this.ballForme] * BALL_DIMENS[this.ballForme]) << 16;
			int xChange = this.localObjectMatrix.translationX - this.renderCalcMatrix.translationX;
			int yChange = this.localObjectMatrix.translationY - this.renderCalcMatrix.translationY;
			GameObject other = startNode;
			while (other != null) {
				other.inverseRenderCalcMatrix.mulVector(this.renderCalcMatrix.translationX, this.renderCalcMatrix.translationY);
				int xRelToOther = Matrix.vectorMulRslX;
				int yRelToOther = Matrix.vectorMulRslY;
				if (other.objectMatrixIsDirty) {
					other.recalcAbsObjectMatrix();
				}
				other.invAbsoluteObjectMatrix.mulVector(this.localObjectMatrix.translationX, this.localObjectMatrix.translationY);
				int newXRelToOther = Matrix.vectorMulRslX;
				int newYRelToOther = Matrix.vectorMulRslY;
				if (xRelToOther > newXRelToOther) {
					higherX = xRelToOther;
					lowerX = newXRelToOther;
				} else {
					higherX = newXRelToOther;
					lowerX = xRelToOther;
				}
				if (yRelToOther > newYRelToOther) {
					higherY = yRelToOther;
					lowerY = newYRelToOther;
				} else {
					higherY = newYRelToOther;
					lowerY = yRelToOther;
				}
				int collAABBMinX = lowerX - ballDiameter;
				int collAABBMinY = lowerY - ballDiameter;
				int collAABBMaxX = higherX + ballDiameter;
				int collAABBMaxY = higherY + ballDiameter;
				if (!aabbIntersects(other.allBBoxMinX, other.allBBoxMinY, other.allBBoxMaxX, other.allBBoxMaxY, collAABBMinX, collAABBMinY, collAABBMaxX, collAABBMaxY)) {
					other = other.getNextNode(startNode);
				} else if ((other.flags & FLAG_NOCOLLIDE) == 0) {
					//System.out.println("checkcoll me " + getObjectId() + " other " + other.getObjectId() + " isplayer " + isPlayer + " mybbox " + collAABBMinX + "/" + collAABBMaxX + "/" + collAABBMinY + "/" + collAABBMaxY);
					switch (other.getObjType()) {
						case GeometryObject.TYPEID:
							GeometryObject geom = (GeometryObject) other;
							boolean z2 = false;
							int i24 = 0;
							int i25 = 0;
							int i26 = 0;
							int i27 = 0;
							int i28 = 0;
							int vertCount = geom.getVertexCount() - 1;
							for (int vertIdx = 0; vertIdx < vertCount; vertIdx++) {
								int x1 = geom.xCoordBuffer[vertIdx];
								int y1 = geom.yCoordBuffer[vertIdx];
								int x2 = geom.xCoordBuffer[vertIdx + 1];
								int y2 = geom.yCoordBuffer[vertIdx + 1];
								if (x1 > x2) {
									xmax = x1;
									xmin = x2;
								} else {
									xmax = x2;
									xmin = x1;
								}
								if (y1 > y2) {
									ymax = y1;
									ymin = y2;
								} else {
									ymax = y2;
									ymin = y1;
								}
								if (aabbIntersects(xmin, ymin, xmax, ymax, collAABBMinX, collAABBMinY, collAABBMaxX, collAABBMaxY)) {
									if (!z2) {
										z = true;
										i24 = newXRelToOther - xRelToOther;
										i25 = newYRelToOther - yRelToOther;
										i26 = (int) Math.sqrt((double) ((((long) i24) * ((long) i24)) + (((long) i25) * ((long) i25))));
										if (i26 != 0) {
											i9 = (int) ((((long) i24) << 16) / ((long) i26));
											i28 = (int) ((((long) i25) << 16) / ((long) i26));
										} else {
											i9 = i27;
										}
									} else {
										i9 = i27;
										z = z2;
									}
									int lineYDim = y1 - y2;
									int lineXDimNeg = -(x1 - x2);
									int sqrt = (int) Math.sqrt((double) ((((long) lineYDim) * ((long) lineYDim)) + (((long) lineXDimNeg) * ((long) lineXDimNeg))));
									int i37 = (int) (((((long) lineYDim) * ((long) BALL_DIMENS[this.ballForme])) << 16) / ((long) sqrt));
									int i38 = (int) (((((long) lineXDimNeg) * ((long) BALL_DIMENS[this.ballForme])) << 16) / ((long) sqrt));
									int i39 = x1 + i37;
									int i40 = y1 + i38;
									int i41 = x2 + i37;
									int i42 = y2 + i38;
									if ((((long) i24) * ((long) lineYDim)) + (((long) i25) * ((long) lineXDimNeg)) < 0) {
										if (aabbIntersectRay(xRelToOther, yRelToOther, i24, i25, i39, i40, i41, i42, ballDiameterSquared)) {
											registCollPoint(geom, aabbRayWeight, xChange, yChange, lineYDim, lineXDimNeg, aabbRayResult);
										}
										if (m9c(xRelToOther, yRelToOther, i9, i28, i26, x1, y1, BALL_DIMENS[this.ballForme])) {
											registCollPoint(geom, aabbRayWeight, xChange, yChange, aabbRayX - x1, aabbRayY - y1, aabbRayResult);
										}
										if (m9c(xRelToOther, yRelToOther, i9, i28, i26, x2, y2, BALL_DIMENS[this.ballForme])) {
											registCollPoint(geom, aabbRayWeight, xChange, yChange, aabbRayX - x2, aabbRayY - y2, aabbRayResult);
										}
									}
								} else {
									i9 = i27;
									z = z2;
								}
								i27 = i9;
								z2 = z;
							}
							other = other.getNextNodeDescendToChildren(startNode);
							break;
						case 3:
						case 5:
						default:
							other = other.getNextNodeDescendToChildren(startNode);
							break;
						case BounceObject.TYPEID:
							other = other.getNextNodeDescendToChildren(startNode);
							break;
						case WaterObject.TYPEID: //water
						{
							WaterObject water = (WaterObject) other;
							int waterMinX = water.areaMinX << 16;
							if (aabbIntersectRay(waterMinX, water.areaMinY << 16, (water.areaMaxX << 16) - waterMinX, 0, xRelToOther, yRelToOther, newXRelToOther, newYRelToOther, 0)) {
								water.onBounceSurfaceContact(aabbRayWeight, this.curYVelocity, BALL_DIMENS[this.ballForme], this);
							}
							if (newYRelToOther - ballDiameter < water.surfaceY) {
								water.updateBounceSwim(newXRelToOther, newYRelToOther - ballDiameter, this);
							}
							other = other.getNextNodeDescendToChildren(startNode);
							break;
						}
						case CannonObject.TYPEID:
							CannonObject cannon = (CannonObject) other;
							if (aabbCheckBoundCross(xRelToOther, yRelToOther, newXRelToOther, newYRelToOther, cannon.loadAABBMinX, cannon.loadAABBMinY, cannon.loadAABBMaxX, cannon.loadAABBMaxY)) {
								cannon.loadBounceToCannon();
							}
							other = other.getNextNodeDescendToChildren(startNode);
							break;
						case TrampolineObject.TYPEID: //jump pad
							TrampolineObject jumpPad = (TrampolineObject) other;
							if (aabbIntersectRay(
									LP32.Int32ToLP32(-70),
									LP32.Int32ToLP32(95),
									LP32.Int32ToLP32(140),
									0,
									xRelToOther,
									yRelToOther,
									newXRelToOther,
									newYRelToOther,
									0
							)) {
								float yvel = this.curYVelocity;
								if (yvel < 0.0f) {
									int yvelLim = -((int) yvel);
									if (yvelLim < 100) {
										yvelLim = 100;
									}
									if (yvelLim > 1000) {
										yvelLim = 1000;
									}
									jumpPad.period = ((1100 - yvelLim) >> 1) + 20;
									jumpPad.progress = jumpPad.period / GameRuntime.getImageAnimationFrameCount(jumpPad.imageId);
									float basePush = ((float) jumpPad.basePush * 2.0f) / 100.0f;
									float maxPush = 400f * basePush;
									jumpPad.calcPush = -yvel * basePush;
									if (jumpPad.calcPush > maxPush) {
										jumpPad.calcPush = maxPush;
									}
									jumpPad.setJumper(this);
									jumpPad.isJumpFinished = false;
									jumpPad.onJumperContact();
								}
							}
							other = other.getNextNodeDescendToChildren(startNode);
							break;
						case EggObject.TYPEID: //collected egg
							EggObject collectEgg = (EggObject) other;
							other = other.getNextNodeDescendToChildren(startNode);
							int i45 = newXRelToOther >> 16;
							int i46 = newYRelToOther >> 16;
							if ((i45 * i45) + (i46 * i46) < 2025) {
								collectEgg.loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
								BounceGame.eggCollectParticle.emitCircle(8, GameObject.tmpObjMatrix.translationX, GameObject.tmpObjMatrix.translationY, 540, 0, 540, 0);
								if (collectEgg.equals(BounceGame.enemyDeadEgg)) {
									BounceGame.eggCount++;
									collectEgg.localObjectMatrix.translationX = Integer.MAX_VALUE;
									collectEgg.localObjectMatrix.translationY = Integer.MAX_VALUE;
									collectEgg.renderCalcMatrix.setFromMatrix(collectEgg.localObjectMatrix);
									collectEgg.renderCalcMatrix.invert(collectEgg.inverseRenderCalcMatrix);
									collectEgg.objectMatrixIsDirty = true;
								} else {
									collectEgg.despawn();
									BounceGame.eggCount++;
								}
							}
							break;
						case EnemyObject.TYPEID: //enemy collision
							EnemyObject enemy = (EnemyObject) other;
							other = other.getNextNodeDescendToChildren(startNode);
							boolean isStomp = false;
							if (enemy.enemyType == EnemyObject.TYPE_CANDLE || (enemy.enemyType == EnemyObject.TYPE_MOLE && (enemy.propelType == 1 || enemy.moleIsVulnerable))) {
								isStomp = aabbCheckBoundCross(xRelToOther, yRelToOther, newXRelToOther, newYRelToOther, enemy.bboxMinX, enemy.bboxMinY + (((enemy.bboxMaxY - enemy.bboxMinY) << 1) / 3), enemy.bboxMaxX, enemy.bboxMaxY);
							}
							if (isStomp) {
								enemy.stomp();
							} else {
								if (aabbCheckBoundCross(xRelToOther, yRelToOther, newXRelToOther, newYRelToOther, enemy.bboxMinX, enemy.bboxMinY, enemy.bboxMaxX, enemy.bboxMaxY)) {
									enemy.onPlayerHit();
								}
								if (enemy.enemyType == EnemyObject.TYPE_MOLE) {
									if (aabbCheckBoundCross(xRelToOther, yRelToOther, newXRelToOther, newYRelToOther, enemy.bboxMinX, enemy.bboxMinY, enemy.bboxMaxX, enemy.bboxMinY + ((((enemy.molePeekTimer * 100) / enemy.molePeekPeriod) * (enemy.bboxMaxY - enemy.bboxMinY)) / 100))) {
										enemy.propelBounceAway();
									}
								}
							}
							break;
					}
				} else {
					other = other.getNextNodeDescendToChildren(startNode);
				}
			}
			if (this.collPointCount != 0) {
				long nearestDistance = Long.MAX_VALUE;
				int nearestCollIdx = -1;
				for (int collIndex = 0; collIndex < this.collPointCount; collIndex++) {
					long distX = (long) (this.collPointsX[collIndex] - this.renderCalcMatrix.translationX);
					long distY = (long) (this.collPointsY[collIndex] - this.renderCalcMatrix.translationY);
					long distance = (distX * distX) + (distY * distY);
					if (this.f41a[collIndex]) {
						distance = -distance;
					}
					if (distance > 0x271000000000L) {
						System.out.println("Sanity check failed! Found collision is too far, distance: " + Math.sqrt((double) distance) / 65536.0d);
					} else if (distance < nearestDistance) {
						nearestCollIdx = collIndex;
						nearestDistance = distance;
					}
				}
				if (nearestCollIdx != -1) {
					float f3 = 1000.0f / ((float) GameRuntime.updateDelta);
					float f4 = ((float) this.collPointsX[nearestCollIdx]) * LP32_TO_FP32_MULTIPLIER;
					float f5 = ((float) this.collPointsY[nearestCollIdx]) * LP32_TO_FP32_MULTIPLIER;
					float f6 = ((float) this.f65l[nearestCollIdx]) * LP32_TO_FP32_MULTIPLIER;
					float f7 = ((float) this.f66m[nearestCollIdx]) * LP32_TO_FP32_MULTIPLIER;
					float sqrt2 = 1.0f / ((float) Math.sqrt((double) ((f6 * f6) + (f7 * f7))));
					float xslope = sqrt2 * f6;
					float yslope = sqrt2 * f7;
					float f10 = ((float) this.f68n[nearestCollIdx]) * LP32_TO_FP32_MULTIPLIER;
					float f11 = ((float) this.f70o[nearestCollIdx]) * LP32_TO_FP32_MULTIPLIER;
					float f12 = (f10 * xslope) + (f11 * yslope);
					float f13 = f12 * xslope;
					float f14 = f12 * yslope;
					if ((xslope * f10) + (yslope * f11) < 0.0f) {
						f13 = -f13;
						f14 = -f14;
					}
					float f15 = f10 * f3;
					float f16 = f11 * f3;
					float f17 = ((float) (this.localObjectMatrix.translationX - this.collPointsX[nearestCollIdx])) * LP32_TO_FP32_MULTIPLIER;
					float f18 = ((float) (this.localObjectMatrix.translationY - this.collPointsY[nearestCollIdx])) * LP32_TO_FP32_MULTIPLIER;
					float f19 = f10 + f4;
					float f20 = f11 + f5;
					float f21 = (f17 * xslope) + (f18 * yslope);
					float f22 = f21 * xslope;
					float f23 = f21 * yslope;
					float f24 = f13 + f4 + ((f17 - f22) - (f22 * f22c[this.ballForme])) + (0.01f * xslope);
					float f25 = f14 + ((f18 - f23) - (f23 * f22c[this.ballForme])) + f5 + (0.01f * yslope);
					float f26 = (this.curXVelocity * xslope) + (this.curYVelocity * yslope);
					float f27 = f26 * xslope;
					float f28 = f26 * yslope;
					float f29 = (this.curXVelocity - f27) - (f27 * f22c[this.ballForme]);
					float f30 = (this.curYVelocity - f28) - (f28 * f22c[this.ballForme]);
					float f31 = (f15 * xslope) + (f16 * yslope);
					this.curXVelocity = f29 + (f31 * xslope);
					this.curYVelocity = f30 + (f31 * yslope);
					float f32 = this.curXVelocity - f15;
					float f33 = this.curYVelocity - f16;
					float sqrt3 = (float) Math.sqrt((double) ((f32 * f32) + (f33 * f33)));
					float f34 = sqrt3 != 0.0f ? f32 / sqrt3 : 0.0f;
					float f35 = sqrt3 != 0.0f ? f33 / sqrt3 : 0.0f;
					float f36 = (-((0.0f * xslope) + (BASE_GRAVITY_Y * yslope))) * f19b[this.ballForme] * GRAVITY[this.ballForme];
					float f37 = f34 * f36;
					float f38 = f35 * f36;
					float f39 = f3 * GRAVITY[this.ballForme];
					float f40 = f32 * f39;
					float f41 = f39 * f33;
					if ((f40 * f40) + (f41 * f41) < (f37 * f37) + (f38 * f38)) {
						this.gravityX -= f40;
						this.gravityY -= f41;
					} else {
						this.gravityX -= f37;
						this.gravityY -= f38;
					}
					this.torqueX = (this.torqueX * (1.0f - this.torqueFalloff)) + (this.torqueFalloff * f32);
					this.torqueY = (this.torqueY * (1.0f - this.torqueFalloff)) + (this.torqueFalloff * f33);
					this.airTimeCounter = 0.0f;
					this.isGrounded = true;
					this.slopeSinAbs = xslope;
					this.slopeCosAbs = yslope;
					this.renderCalcMatrix.translationX = LP32.FP32ToLP32(f19);
					this.renderCalcMatrix.translationY = LP32.FP32ToLP32(f20);
					this.localObjectMatrix.translationX = LP32.FP32ToLP32(f24);
					this.localObjectMatrix.translationY = LP32.FP32ToLP32(f25);
					recalcAbsObjectMatrix();
					this.renderCalcMatrix.invert(this.inverseRenderCalcMatrix);
					this.collPointCount = 0;
				}
			} else {
				break;
			}
		}
		this.airTimeCounter += GameRuntime.updateDelta * 0.001f;
		if (this.airTimeCounter > 0.25f) {
			//since this is done both in coll check and physics update, it's actually 1/8th of a second instead of 1/4th
			this.isGrounded = false;
		}
	}

	/* renamed from: a */
	private void registCollPoint(GeometryObject geometry, int t, int x, int y, int x2, int y2, boolean z) {
		if (t > 0) {
			this.collPointsX[this.collPointCount] = this.renderCalcMatrix.translationX + ((int) ((((long) x) * ((long) t)) >> 16));
			this.collPointsY[this.collPointCount] = this.renderCalcMatrix.translationY + ((int) ((((long) y) * ((long) t)) >> 16));
			this.f41a[this.collPointCount] = z;
			geometry.renderCalcMatrix.mulDirection(x2, y2);
			int i6 = Matrix.vectorMulRslX;
			int i7 = Matrix.vectorMulRslY;
			geometry.loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
			GameObject.tmpObjMatrix.mulDirection(x2, y2);
			int i8 = Matrix.vectorMulRslX;
			int i9 = Matrix.vectorMulRslY;
			this.f65l[this.collPointCount] = (int) (((((long) i6) * ((long) (LP32.ONE - t))) + (((long) i8) * ((long) t))) >> 16);
			this.f66m[this.collPointCount] = (int) (((((long) i7) * ((long) (LP32.ONE - t))) + (((long) i9) * ((long) t))) >> 16);
		} else if (t < 0) {
			throw new IllegalStateException("t < 0, t: " + t);
		} else {
			geometry.renderCalcMatrix.mulVector(aabbRayX, aabbRayY);
			this.collPointsX[this.collPointCount] = Matrix.vectorMulRslX;
			this.collPointsY[this.collPointCount] = Matrix.vectorMulRslY;
			this.f41a[this.collPointCount] = z;
			geometry.renderCalcMatrix.mulVector(x2, y2);
			this.f65l[this.collPointCount] = Matrix.vectorMulRslX;
			this.f66m[this.collPointCount] = Matrix.vectorMulRslY;
		}
		geometry.renderCalcMatrix.mulVector(aabbRayX, aabbRayY);
		int i10 = Matrix.vectorMulRslX;
		int i11 = Matrix.vectorMulRslY;
		geometry.loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
		GameObject.tmpObjMatrix.mulVector(aabbRayX, aabbRayY);
		int i12 = (int) ((float) GameRuntime.updateDelta * 6553.6f);
		this.f68n[this.collPointCount] = (Matrix.vectorMulRslX - i10) + (i12 * 0);
		this.f70o[this.collPointCount] = (Matrix.vectorMulRslY - i11) + (i12 * 0);
		this.collPointCount++;
		if (geometry.event > -1) {
			System.out.println("Geometry " + getObjectId() + " started event " + geometry.event);
			((EventObject) getObjectRoot().searchByObjId(geometry.event)).changeEventState(EventObject.STATE_ACTIVE);
		}
	}

	private static boolean aabbIntersectRay(int minX, int minY, int width, int height, int rayx1, int rayy1, int rayx2, int rayy2, int epsilon) {
		long l2 = (long) rayx1 * (long) height >> 16;
		long l3 = (long) rayy1 * (long) width >> 16;
		long l4 = (long) rayx2 * (long) height >> 16;
		long l5 = (long) rayy2 * (long) width >> 16;
		long l6 = l2 - l3 - l4 + l5;
		if (l6 == 0L) {
			return false;
		}
		long l7 = (l2 - l3 + ((long) width * (long) minY >> 16) - ((long) height * (long) minX >> 16) << 16) / l6;
		if (l7 < 0L || l7 > LP32.ONE) {
			return false;
		}
		long weight = ((long) rayx1 * (long) (rayy2 - minY) + (long) rayy1 * (long) (minX - rayx2) + (long) rayx2 * (long) minY - (long) rayy2 * (long) minX) / l6;
		if (weight >= 0L && weight <= LP32.ONE) {
			aabbRayWeight = (int) weight;
			aabbRayX = (int) ((long) minX + (weight * (long) width >> 16));
			aabbRayY = (int) ((long) minY + (weight * (long) height >> 16));
			aabbRayResult = false;
			return true;
		}
		if (weight < 0L) {
			long l9 = minX - rayx1;
			long l10 = rayx2 - rayx1;
			long l11 = minY - rayy1;
			long l12 = rayy2 - rayy1;
			long l13 = l9 * l10 + l11 * l12 >> 16;
			if (l13 <= 0L) {
				return false;
			}
			long l14 = l10 * l10 + l12 * l12 >> 16;
			if (l13 >= l14) {
				return false;
			}
			long l15 = (long) rayx1 + ((l13 = (l13 << 16) / l14) * l10 >> 16);
			long l16 = l15 - (long) minX;
			long l17 = (long) rayy1 + (l13 * l12 >> 16);
			long l18 = l17 - (long) minY;
			long l19 = l16 * l16 + l18 * l18 >> 16;
			if (l19 > (long) epsilon) {
				return false;
			}
			aabbRayWeight = 0;
			aabbRayX = (int) l15;
			aabbRayY = (int) l17;
			aabbRayResult = true;
			return true;
		}
		return false;
	}

	/* renamed from: c */
	private static boolean m9c(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
		long j = (long) (i - i6);
		long j2 = (long) (i2 - i7);
		long j3 = ((((long) i3) * j) + (((long) i4) * j2)) >> 16;
		if (j3 >= 0) {
			return false;
		}
		long j4 = (((j * j) + (j2 * j2)) >> 16) - ((long) ((i8 * i8) << 16));
		if (j4 <= 0) {
			aabbRayWeight = 0;
			int sqrt = (int) Math.sqrt((double) ((j * j) + (j2 * j2)));
			int i9 = 0;
			int i10 = 0;
			if (sqrt != 0) {
				i9 = (int) (((((long) i8) * j) << 16) / ((long) sqrt));
				i10 = (int) (((((long) i8) * j2) << 16) / ((long) sqrt));
			}
			aabbRayX = i9 + i6;
			aabbRayY = i10 + i7;
			aabbRayResult = true;
			return true;
		}
		long j5 = ((j3 * j3) >> 16) - j4;
		if (j5 < 0) {
			return false;
		}
		long sqrt2 = (-j3) - ((long) ((int) Math.sqrt((double) (j5 << 16))));
		if (sqrt2 > ((long) i5)) {
			return false;
		}
		aabbRayWeight = (int) ((sqrt2 << 16) / ((long) i5));
		aabbRayX = (int) (((long) i) + ((((long) i3) * sqrt2) >> 16));
		aabbRayY = (int) (((sqrt2 * ((long) i4)) >> 16) + ((long) i2));
		aabbRayResult = false;
		return true;
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final void draw(Graphics graphics, DirectGraphics directGraphics, Matrix rootMatrix) {
		super.draw(graphics, directGraphics, rootMatrix);
		int fbBallCY;
		int fbBallCX;
		Graphics graphics2;
		if (this.isVisible) {
			loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
			Matrix.multMatrices(rootMatrix, GameObject.tmpObjMatrix, Matrix.temp);
			Matrix.temp.mulVector(this.localObjectMatrix.translationX, this.localObjectMatrix.translationY);
			int ballX = Matrix.temp.translationX >> 16;
			int ballY = Matrix.temp.translationY >> 16;
			Matrix.temp.mulVector(-(BALL_DIMENS[this.ballForme] << 16), BALL_DIMENS[this.ballForme] << 16);
			int ballTLX = Matrix.vectorMulRslX >> 16;
			int ballTLY = Matrix.vectorMulRslY >> 16;
			int ballHalfWidthX = ballX - ballTLX;
			int ballHalfWidthY = ballY - ballTLY;
			if (this.isPlayer) {
				switch (this.ballForme) {
					case FORME_BOUNCE: {
						Graphics orgGraphics = GameRuntime.getGraphicsObj();
						if (this.fadeColor != 0) {
							BounceGame.ballGraphics.setColor(0x0000FF);
							BounceGame.ballGraphics.fillRect(0, 0, BounceGame.ballFramebuffer.getWidth(), BounceGame.ballFramebuffer.getHeight());
							fbBallCX = BounceGame.ballFramebuffer.getWidth() >> 1;
							fbBallCY = BounceGame.ballFramebuffer.getHeight() >> 1;
							graphics2 = BounceGame.ballGraphics;
							GameRuntime.setGraphics(BounceGame.ballGraphics);
						} else {
							fbBallCY = ballY;
							fbBallCX = ballX;
							graphics2 = graphics;
						}
						for (int axisA = 0; axisA < 4; axisA++) {
							this.stretchDirBalance[axisA] = 0;
							for (int axisB = 0; axisB < 4; axisB++) {
								if (axisA != axisB) {
									this.stretchDirBalance[axisA] -= this.stretchResultsAbs[axisB] >> 1;
								}
							}
						}
						for (int axis = 0; axis < 4; axis++) {
							this.stretchBuffer[axis] = 999;
						}
						fillStretchedCircle(fbBallCX, fbBallCY, BALL_DIMENS_SCREENSPACE[this.ballForme] + 2, BALL_DIMENS_SCREENSPACE[this.ballForme] + 2, 0xFF000000, graphics2, false, false);
						fillStretchedCircle(fbBallCX, fbBallCY, BALL_DIMENS_SCREENSPACE[this.ballForme], BALL_DIMENS_SCREENSPACE[this.ballForme], this.BOUNCE_PRIMARY_COLOR, graphics2, true, false);
						int innerRadius = (BALL_DIMENS_SCREENSPACE[this.ballForme] * 90) / 100;
						fillStretchedCircle(fbBallCX, fbBallCY + 2, innerRadius, BALL_DIMENS_SCREENSPACE[this.ballForme], this.BOUNCE_SECONDARY_COLOR, graphics2, false, false);
						fillStretchedCircle(fbBallCX + 1, fbBallCY - 1, innerRadius, BALL_DIMENS_SCREENSPACE[this.ballForme], this.BOUNCE_PRIMARY_COLOR, graphics2, false, true);
						Matrix.temp.setRotation(5.3f);
						int i16 = this.stretchBuffer[1] >> 1;
						int i17 = (this.stretchBuffer[1] - i16) + 1;
						int highlightX = fbBallCX + ((Matrix.temp.m00 * i17) >> 16);
						int highlightY = fbBallCY + ((i17 * Matrix.temp.m10) >> 16);
						for (int i20 = 0; i20 < 12; i20++) {
							int abs = Math.abs(i20 & 1) + 31;
							int i21 = 100 - (i20 << 2);
							Matrix.temp.setRotation(this.rotation + (((float) i20) * 0.8f));
							int posBase = ((this.stretchResultsAbs[1] + this.stretchDirBalance[1]) >> 10);
							for (int i22 = 0; i22 < 4; i22++) {
								int i23 = ((this.stretchBuffer[i22] - ((this.stretchBuffer[i22] >> 1) >> 1)) * i21) / 100;
								int i24 = ((Matrix.temp.m00 * i23) >> 16) + fbBallCX;
								int i25 = ((i23 * Matrix.temp.m10) >> 16) + fbBallCY;
								if (i22 == 0) {
									if (i24 >= fbBallCX && i25 <= fbBallCY) {
										GameRuntime.drawImageRes(i24, +i25, abs);
										break;
									}
								} else if (i22 == 1) {
									if (i24 <= fbBallCX && i25 <= fbBallCY) {
										GameRuntime.drawImageRes(i24, posBase + i25, abs);
										break;
									}
								} else if (i22 == 2) {
									if (i24 <= fbBallCX && i25 >= fbBallCY) {
										GameRuntime.drawImageRes(i24, posBase + i25, abs);
										break;
									}
								} else if (i24 >= fbBallCX && i25 >= fbBallCY) {
									GameRuntime.drawImageRes(i24, posBase + i25, abs);
									break;
								}
								i22++;
							}
						}
						GameRuntime.drawImageRes(fbBallCX, fbBallCY, 17);
						fillStretchedCircle(highlightX, highlightY, i16 >> 1, BALL_DIMENS_SCREENSPACE[this.ballForme], this.BOUNCE_HIGHLIGHT_COLOR, graphics2, false, false);
						GameRuntime.drawImageRes(fbBallCX, fbBallCY, 28);
						if (this.fadeColor != 0) {
							GameRuntime.setGraphics(orgGraphics);
							BounceGame.ballFramebuffer.getRGB(BounceGame.ballFramebufferRGB, 0, BounceGame.ballFramebuffer.getWidth(), 0, 0, BounceGame.ballFramebuffer.getWidth(), BounceGame.ballFramebuffer.getHeight());
							int rgbIdx = 0;
							for (int y = 0; y < BounceGame.ballFramebuffer.getHeight(); y++) {
								for (int x = 0; x < BounceGame.ballFramebuffer.getWidth(); x++) {
									if (BounceGame.ballFramebufferRGB[rgbIdx] == 0xFF0000FF) {
										BounceGame.ballFramebufferRGB[rgbIdx] = 0;
									} else {
										BounceGame.ballFramebufferRGB[rgbIdx] -= this.fadeColor;
									}
									rgbIdx++;
								}
							}
							GameRuntime.getGraphicsObj().drawRGB(BounceGame.ballFramebufferRGB,
									0,
									BounceGame.ballFramebuffer.getWidth(),
									ballX - (BounceGame.ballFramebuffer.getWidth() >> 1),
									ballY - (BounceGame.ballFramebuffer.getHeight() >> 1),
									BounceGame.ballFramebuffer.getWidth(),
									BounceGame.ballFramebuffer.getHeight(),
									true
							);
						}
						if (BounceGame.isSuperBounceUnlocked && !BounceGame.levelPaused) {
							this.superBounceParticleTimer += GameRuntime.updateDelta * GameRuntime.getUpdatesPerDraw();
							if (this.superBounceParticleTimer > 150) {
								BounceGame.superBounceParticle.emitTrail(EventObject.eventVars[4] / 120, BounceGame.bounceObj.localObjectMatrix.translationX, BounceGame.bounceObj.localObjectMatrix.translationY, BALL_DIMENS[0] << 15, 0, 0, 0, 0, 1000, 166);
								this.superBounceParticleTimer = 0;
							}
						}
						ballY = fbBallCY;
						ballX = fbBallCX;
						break;
					}
					case FORME_BUMPY_CRACKS: {
						//optimized method
						int degrees = ((int) Math.toDegrees((double) this.rotation)) % 360;
						if (degrees < 0) {
							degrees += 360;
						}
						/*
						while (degrees < 0) { //this could get real slow real quick
							degrees += 360;
						}
						while (degrees > 359) {
							degrees -= 360;
						}*/
						GameRuntime.drawImageRes(ballX, ballY, BUMPY_CRACKS_ROTATION_SPRITES[31 - ((int) (((float) degrees) / 11.25f))]);
						break;
					}
					case FORME_WOLLY: {
						int wollyCenterWidth = ballHalfWidthX >> 2;
						int centerCircleX = ballX - wollyCenterWidth;
						int centerCircleY = ballY - wollyCenterWidth;
						int ballWidth = BALL_DIMENS_SCREENSPACE[this.ballForme] << 1;
						graphics.setColor(0x000000); //outline
						graphics.fillArc(ballTLX - 2, ballTLY - 2, ballWidth + 4, ballWidth + 4, 0, 360);
						for (int wollySegment = 0; wollySegment < 6; wollySegment++) {
							graphics.setColor(WOLLY_SEGMENT_COLORS[wollySegment]);
							graphics.fillArc(ballTLX, ballTLY, ballWidth, ballWidth, (wollySegment * 60) - ((int) Math.toDegrees(this.rotation)), 60);
						}
						graphics.setColor(WOLLY_SEGMENT_COLORS[0]);
						graphics.fillArc(centerCircleX, centerCircleY, wollyCenterWidth << 1, wollyCenterWidth << 1, 0, 360);
						GameRuntime.drawImageRes(ballX, ballY, 5);
						break;
					}
					default:
						break;
				}
				if (eyeFrame == 1 || BounceGame.getPlayerState() == BounceGame.PLAYER_STATE_LOSE_UPDATE) {
					GameRuntime.drawImageRes(ballX, ballY, 20); //owowowowow
				} else if (eyeFrame == 2 || eyeFrame == 3) {
					int eyeImageId = 462;
					int frameCount = 2;
					if (eyeFrame == 3) {
						eyeImageId = 447;
						frameCount = 4;
					}
					int frameInvIndex = (idleAnimTimer * frameCount) / 600;
					if (frameInvIndex > frameCount - 1) {
						frameInvIndex = frameCount - 1;
					}
					GameRuntime.drawAnimatedImageRes(ballX, ballY, eyeImageId, (frameCount - 1) - frameInvIndex);
				} else if (eyeFrame >= 4 && eyeFrame <= 8) {
					GameRuntime.drawAnimatedImageRes(ballX, ballY, EYE_ANIMATION_IMAGE_IDS[eyeFrame - 4], 0);
				} else if (BounceGame.getPlayerState() == BounceGame.PLAYER_STATE_WIN_UPDATE) {
					GameRuntime.drawAnimatedImageRes(ballX, ballY, 465, 0);
				}
			} else {
				graphics.setColor(this.BOUNCE_PRIMARY_COLOR);
				graphics.fillArc(ballTLX, ballTLY, ballHalfWidthX << 1, ballHalfWidthY << 1, 0, 360);
			}
		}
		debugDraw(graphics, 0xFFCC00, rootMatrix);
	}

	/* renamed from: b */
	private void stretchInDirection(int dir, int magnitude) {
		if (magnitude > 0) {
			if (magnitude > 70) {
				magnitude = 70;
			}
		} else if (magnitude < 0 && magnitude < -70) {
			magnitude = -70;
		}
		this.reqStretchMagnitudes[dir] += magnitude;
	}

	/* renamed from: n */
	private void resetStretch() {
		for (int i = 0; i < 4; i++) {
			this.reqStretchMagnitudes[i] = 0;
			this.stretchResults[i] = 0;
			this.stretchResultsAbs[i] = 0;
			this.stretchMagnitudes[i] = 0;
		}
	}

	/* renamed from: a */
	private void fillStretchedCircle(int cx, int cy, int radius, int stretchRadius, int color, Graphics graphics, boolean writeStretchBuffer, boolean z2) {
		graphics.setColor(color);
		int i6 = 2;
		int axis = 0;
		while (axis < 4) {
			int width = radius - (((this.stretchResultsAbs[i6] + this.stretchDirBalance[i6]) >> 10) * radius / stretchRadius);
			int height = radius - (((this.stretchResultsAbs[axis >> 1] + this.stretchDirBalance[axis >> 1]) >> 10) * radius / stretchRadius);
			if (z2) {
				width++;
			}
			if (writeStretchBuffer) {
				this.stretchBuffer[axis] = width;
				if (height < width) {
					this.stretchBuffer[axis] = height;
				}
			}
			graphics.fillArc(cx - width, (cy - height) + ((this.stretchResultsAbs[1] + this.stretchDirBalance[1]) >> 10), width << 1, height << 1, axis * 90, 90);
			int i10 = axis == 0 ? i6 + 1 : i6;
			if (axis == 2) {
				i10--;
			}
			axis++;
			i6 = i10;
		}
	}

	/* renamed from: g */
	public final void resetPhysics() {
		this.curXVelocity = 0.0f;
		this.curYVelocity = 0.0f;
		this.pushX = 0.0f;
		this.pushY = 0.0f;
		this.gravityX = 0.0f;
		this.gravityY = 0.0f;
		this.torqueX = 0.0f;
		this.torqueY = 0.0f;
	}

	// p000.GameObject
	/* renamed from: d */
	//@Override
	public final void updatePhysics() {
		float f;
		float f2;
		setIsDirtyRecursive();
		super.updatePhysics();
		this.objectMatrixIsDirty = true;
		if (this.enablePhysics) {
			this.gravityX += BASE_GRAVITY_X * GRAVITY[this.ballForme];
			this.gravityY += BASE_GRAVITY_Y * GRAVITY[this.ballForme];
			if (this.isPlayer) {
				if (!this.reqSkipAccelStretch) {
					float xaccel = this.curXVelocity - this.lastXVelocity;
					float yaccel = this.curYVelocity - this.lastYVelocity;
					if (xaccel > 0.0f) {
						stretchInDirection(3, ((((int) xaccel) >> 2) << 1) / 3);
					} else {
						stretchInDirection(2, (((-((int) xaccel)) >> 2) << 1) / 3);
					}
					if (yaccel > 0.0f) {
						stretchInDirection(1, ((((int) yaccel) >> 2) << 1) / 3);
					} else {
						stretchInDirection(0, (((-((int) yaccel)) >> 2) << 1) / 3);
					}
				}
				this.reqSkipAccelStretch = false;
			}
			float motionDelta = ((float) GameRuntime.updateDelta) * 0.001f;
			if (this.isPlayer) {
				this.lastXVelocity = this.curXVelocity;
				this.lastYVelocity = this.curYVelocity;
			}
			float invGravity = 1.0f / GRAVITY[this.ballForme];
			this.curXVelocity += this.gravityX * invGravity * motionDelta;
			this.curYVelocity += this.gravityY * invGravity * motionDelta;
			this.curXVelocity += this.pushX * invGravity;
			this.curYVelocity += this.pushY * invGravity;
			this.localObjectMatrix.translationX += LP32.FP32ToLP32(this.curXVelocity * motionDelta);
			this.localObjectMatrix.translationY += LP32.FP32ToLP32(this.curYVelocity * motionDelta);
			this.gravityX = 0.0f;
			this.gravityY = 0.0f;
			this.pushX = 0.0f;
			this.pushY = 0.0f;
			float f7 = (this.torqueX * this.slopeSinAbs) + (this.torqueY * this.slopeCosAbs);
			float f8 = this.slopeSinAbs * f7;
			float f9 = this.slopeCosAbs * f7;
			if ((this.slopeSinAbs * this.torqueX) + (this.slopeCosAbs * this.torqueY) >= 0.0f) {
				f = this.torqueX - f8;
				f2 = this.torqueY - f9;
			} else {
				f = this.torqueX + f8;
				f2 = this.torqueY + f9;
			}
			float sqrt = motionDelta * (((float) Math.sqrt((f * f) + (f2 * f2))) / ((float) BALL_DIMENS[this.ballForme]));
			this.rotation += ((f2 * this.slopeSinAbs) - (f * this.slopeCosAbs) > 0.0f ? -sqrt : sqrt);
			this.airTimeCounter += GameRuntime.updateDelta * 0.001f;
			if (this.airTimeCounter > 0.25f) {
				this.isGrounded = false;
			}
			this.curVelocity = (float) Math.sqrt((double) ((this.curXVelocity * this.curXVelocity) + (this.curYVelocity * this.curYVelocity)));
			if (this.curVelocity > 999.0f) { //terminal velocity
				float invVelocity = 999.0f / this.curVelocity;
				this.curXVelocity *= invVelocity;
				this.curYVelocity *= invVelocity;
				this.curVelocity = 999.0f;
			}
			if (this.isPlayer) {
				for (int axis = 0; axis < 4; axis++) {
					//this part of the code was reworked to be 16.16 fixed point in order
					//for stretching to work properly on very high framerates
					//since part of the formula is to divide by 65536 (shr 16), a lot of precision
					//was lost at low deltas, which made stretching very weird on 1000FPS
					//the long multiplication loses a bit of performance in exchange for making it work properly
					this.stretchMagnitudes[axis] += this.reqStretchMagnitudes[axis] << 16;
					this.reqStretchMagnitudes[axis] = 0;
					if (this.stretchMagnitudes[axis] > 70 << 16) {
						this.stretchMagnitudes[axis] = 70 << 16;
					} else if (this.stretchMagnitudes[axis] < -70 << 16) {
						this.stretchMagnitudes[axis] = -70 << 16;
					}
					this.stretchResults[axis] += (this.stretchMagnitudes[axis] * GameRuntime.updateDelta);
					this.stretchResultsAbs[axis] = ((this.stretchResults[axis] >> 16) * GameObject.screenSpaceMatrix.m00) >> 16;
					this.stretchMagnitudes[axis] += ((long) (GameRuntime.updateDelta * 7L) * (long) -this.stretchResults[axis]) >> 16L;
					this.stretchMagnitudes[axis] -= ((long) (GameRuntime.updateDelta * 120L) * (long) this.stretchMagnitudes[axis]) >> 16L;
				}
			}
			if (this.isPlayer) {
				if (idleAnimTimer > 0) {
					idleAnimTimer -= GameRuntime.updateDelta;
					idleAnimStartTimer = 3000;
					if (idleAnimTimer <= 0) {
						if (eyeFrame < 2 || eyeFrame > 8) {
							idleAnimTimer = 0;
							eyeFrame = 0;
						} else {
							int abs = Math.abs(BounceGame.mRNG.nextInt() % 3) == 0 ? 3 : Math.abs(BounceGame.mRNG.nextInt() % 6) + 3;
							idleAnimTimer = 1500;
							eyeFrame = abs;
							if (abs == 3) {
								idleAnimTimer = 600;
							}
						}
					}
				} else if (BounceGame.getPlayerState() == BounceGame.PLAYER_STATE_PLAY && EventObject.eventVars[1] == BounceGame.CONTROLLER_NORMAL && Math.abs(this.curXVelocity) < 40.0f && Math.abs(this.curYVelocity) < 40.0f) {
					idleAnimStartTimer -= GameRuntime.updateDelta;
					if (idleAnimStartTimer <= 0) {
						idleAnimTimer = 600;
						eyeFrame = 2;
						idleAnimStartTimer = 3000;
					}
				}
				if (Math.abs(this.curXVelocity) >= 40.0f || Math.abs(this.curYVelocity) >= 40.0f) {
					if (eyeFrame != 1) {
						eyeFrame = 0;
					}
					idleAnimStartTimer = 3000;
				}
			}
			if (this.fadeColor != 0) {
				int fadeAlpha = this.fadeColor >>> 24;
				int alphaDecrement = GameRuntime.updateDelta / 2;
				if (alphaDecrement < 1) {
					alphaDecrement = 1;
				}
				int newFadeAlpha = fadeAlpha - alphaDecrement;
				if (newFadeAlpha < 0) {
					newFadeAlpha = 0;
				}
				this.fadeColor = newFadeAlpha << 24;
			}
		}
	}

	/* renamed from: e */
	public final void updateDeathAnimation() {
		setIsDirtyRecursive();
		super.updatePhysics();
		resetStretch();
		int i = 3000 - BounceGame.exitWaitTimer;
		if (i <= 1000) {
			this.localObjectMatrix.translationY = (BounceGame.SIN_COS_TABLE[(i / 5) % 360] << 14) + BounceGame.deathBaseY;
		} else {
			this.localObjectMatrix.translationY = (BounceGame.deathBaseY + (BounceGame.SIN_COS_TABLE[200] << 14)) - ((i - 1000) * 22500);
		}
	}

	/* renamed from: a */
	public final void jump(boolean small) {
		if (this.slopeCosAbs > MAX_JUMP_SLOPE_INV[this.ballForme] && this.isGrounded) {
			if (small) {
				this.pushY += JUMP_MODIFIERS[this.ballForme] / 2.0f;
			} else {
				this.pushY += JUMP_MODIFIERS[this.ballForme];
			}
			this.isGrounded = false;
			if (this.isPlayer) {
				if (small) {
					stretchInDirection(0, -27);
					stretchInDirection(1, -27);
				} else {
					stretchInDirection(0, -53);
					stretchInDirection(1, -53);
				}
			}
		}
	}

	/* renamed from: b */
	public final void moveLeft() {
		float speedBoost = 0.0f;
		if (BounceGame.isSuperBounceUnlocked && this.ballForme == BounceObject.FORME_BOUNCE) {
			speedBoost = 50.0f;
		}
		if (this.isGrounded) {
			if (this.curXVelocity > (-MOVEMENT_SPEEDS[this.ballForme]) - speedBoost) {
				this.gravityX -= speedBoost + MOVEMENT_TRACTIONS[this.ballForme];
			}
		} else if (this.curXVelocity > (-MOVEMENT_SPEEDS[this.ballForme]) - speedBoost) {
			this.gravityX -= (speedBoost + MOVEMENT_TRACTIONS[this.ballForme]) / MIDAIR_MOVEMENT_SUPPRESSION[this.ballForme];
		}
	}

	/* renamed from: c */
	public final void moveRight() {
		float speedBoost = 0.0f;
		if (BounceGame.isSuperBounceUnlocked && this.ballForme == BounceObject.FORME_BOUNCE) {
			speedBoost = 50.0f;
		}
		if (this.isGrounded) {
			if (this.curXVelocity < MOVEMENT_SPEEDS[this.ballForme] + speedBoost) {
				this.gravityX += speedBoost + MOVEMENT_TRACTIONS[this.ballForme];
			}
		} else if (this.curXVelocity < MOVEMENT_SPEEDS[this.ballForme] + speedBoost) {
			this.gravityX += ((speedBoost + MOVEMENT_TRACTIONS[this.ballForme]) / MIDAIR_MOVEMENT_SUPPRESSION[this.ballForme]);
		}
	}

	/* renamed from: f */
	public final void cycleForme() {
		this.ballForme++;
		if (this.ballForme > 2) {
			this.ballForme = 0;
		}
		if (this.ballForme > BounceGame.getUnlockedFormeCount()) {
			this.ballForme = 0;
		}
		initialize();
	}
}
