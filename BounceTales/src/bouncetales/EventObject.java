package bouncetales;

import javax.microedition.lcdui.Graphics;

/* renamed from: g */
public final class EventObject extends GameObject {

	public static final byte TYPEID = 3;

	public static final byte STATE_WAITING = 0;
	public static final byte STATE_TERMINATED = 1;
	public static final byte STATE_ACTIVE = 2;
	public static final byte STATE_PAUSED = 3;

	//Global state
	//public static int finalBossTimer = 0; //renamed from: a //removed in 2.0.25
	public static int[] eventVars; //renamed from: a

	//Global - stream pos after read function return
	private static int readPtr = 0; //renamed from: d

	private static EventObject[] currentEvents; //renamed from: a
	private static GameObject[] triggerCandidates = new GameObject[2]; //renamed from: c

	//Parameters
	private byte[][] events; //renamed from: a
	private byte eventCount = 0; //renamed from: f

	private byte triggerByLeave; //renamed from: d
	private short triggerObjId; //renamed from: a
	private byte repeatable; //renamed from: e

	//State - interaction
	private GameObject[] actorsInAreaQueue = new GameObject[2]; //renamed from: a
	private int queuedAreaActorCount; //renamed from: b
	private GameObject[] lastActorsInArea = new GameObject[2]; //renamed from: b
	private int lastAreaActorCount; //renamed from: c

	private byte eventState = 0; //renamed from: a
	private byte currentEvent = -1; //renamed from: g

	public EventObject() {
		this.objType = TYPEID;
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final int readData(byte[] b, int dataPos) {
		dataPos = super.readData(b, dataPos);
		this.bboxMinX = readShort(b, dataPos + 0) << 16;
		this.bboxMaxY = readShort(b, dataPos + 2) << 16;
		this.bboxMaxX = readShort(b, dataPos + 4) << 16;
		this.bboxMinY = readShort(b, dataPos + 6) << 16;
		dataPos += 8;
		this.eventState = b[dataPos++];
		if (this.eventState == STATE_ACTIVE) {
			System.out.println("Event " + getObjectId() + " autostart!");
			this.currentEvent = 0;
		}
		this.triggerByLeave = b[dataPos++];
		this.repeatable = b[dataPos++];
		this.triggerObjId = readShort(b, dataPos);
		dataPos += 2;
		this.eventCount = b[dataPos++];
		this.events = new byte[this.eventCount][];
		for (int eventIdx = 0; eventIdx < this.eventCount; eventIdx++) {
			byte evCmdCount = b[dataPos++];
			this.events[eventIdx] = new byte[evCmdCount];
			for (int i = 0; i < evCmdCount; i++) {
				this.events[eventIdx][i] = b[dataPos++];
			}
		}
		if (!DEBUG_DRAW_ON) {
			this.flags |= FLAG_NODRAW;
		}
		return dataPos;
	}

	// p000.GameObject
	/* renamed from: a */
	//@Override
	public final void draw(Graphics graphics, Matrix rootMatrix) {
		if (DEBUG_DRAW_ON) {
			int[] colors = new int[]{0x0000FF, 0xFF0000, 0x00FF00, 0x00CC00};
			debugDraw(graphics, colors[eventState], rootMatrix);
		}
	}

	/* access modifiers changed from: protected */
 /* renamed from: a */
	public final void changeEventState(int newState) {
		switch (this.eventState) {
			case STATE_WAITING:
				if (newState == STATE_ACTIVE) {
					this.eventState = STATE_ACTIVE;
					this.currentEvent = 0;
				}
				if (newState == STATE_TERMINATED) {
					this.eventState = STATE_TERMINATED;
				}
				break;
			case STATE_TERMINATED:
				if (newState == STATE_WAITING) {
					this.eventState = STATE_WAITING;
					resetTransformEvents();
				}
				break;
			case STATE_ACTIVE:
				if (newState == STATE_TERMINATED || newState == STATE_PAUSED) {
					this.eventState = (byte) newState;
				}
				break;
			case STATE_PAUSED:
				if (newState == STATE_ACTIVE) {
					this.eventState = STATE_ACTIVE;
				}
				break;
		}
	}

	/* renamed from: a */
	private static float getFloatEventVar(byte b) {
		return Float.intBitsToFloat(eventVars[b]);
	}

	/* renamed from: a */
	private static void setFloatEventVar(byte b, float f) {
		eventVars[b] = Float.floatToIntBits(f);
	}

	/* renamed from: a */
	private static float readFloat(byte[] arr, int offset) {
		byte dataType = arr[offset];
		readPtr = offset + 1;
		switch (dataType) {
			case 1:
				readPtr++;
				return getFloatEventVar(arr[offset + 1]);
			case 2:
				readPtr++;
				return (float) eventVars[arr[offset + 1]];
			case 16:
				readPtr += 4;
				return Float.intBitsToFloat(GameObject.readInt(arr, offset + 1));
			case 32:
				readPtr += 4;
				return (float) GameObject.readInt(arr, offset + 1);
			default:
				return 0.0f;
		}
	}

	/* renamed from: c */
	private static int readInteger(byte[] data, int offset) {
		byte size = data[offset];
		readPtr = 4;
		switch (size) {
			case 2:
				readPtr++;
				return eventVars[data[offset + 1]];
			case 32:
				readPtr += 4;
				return GameObject.readInt(data, offset + 1);
			default:
				return 0;
		}
	}

	/* renamed from: a  reason: collision with other method in class */
	private static void write16(byte[] bArr, int offset, int value) { //since 2.0.25, offset parameter assumed to be obfuscated
		bArr[offset] = (byte) (value >> 8);
		bArr[offset + 1] = (byte) value;
	}

	/* renamed from: a */
	private static void write32(byte[] bArr, int offset, int value) {
		bArr[offset] = (byte) (value >>> 24);
		bArr[offset + 1] = (byte) (value >> 16);
		bArr[offset + 2] = (byte) (value >> 8);
		bArr[offset + 3] = (byte) value;
	}

	private static void eventLog(String str) {
		System.out.println(str);
	}

	private boolean executeEvent(byte[] evCmd) { //since 2.0.25
		switch (evCmd[0]) {
			case EventCommand.MESSAGE: //display message
				short msg = BounceGame.SCRIPT_MESSAGE_IDS[GameObject.readShort(evCmd, 1)];
				if (!BounceGame.wasLevelBeaten(LevelID.GAME_CLEAR_LEVEL)) {
					BounceGame.pushFieldMessage(msg);
				}
				return true;
			case EventCommand.OBJ_ANIMATE: //start hit animation
				GameObject spriteGO = getObjectRoot().searchByObjId(GameObject.readShort(evCmd, 1));
				if (spriteGO != null) {
					eventLog("Start sprite animation @ " + spriteGO.getObjectId());
					SpriteObject sprite = (SpriteObject) spriteGO;
					sprite.onPlayerContact();
				}
				return true;
			case EventCommand.EVENT_TERMINATE: //terminate event definitely
				currentEvents[evCmd[1]].changeEventState(STATE_TERMINATED);
				return true;
			case EventCommand.EVENT_CANCEL: //end event, but allow repeated execution
				currentEvents[evCmd[1]].changeEventState(STATE_WAITING);
				return true;
			case EventCommand.EVENT_START: //activate event
				eventLog("Activate event " + currentEvents[evCmd[1]].getObjectId() + " (evId: " + evCmd[1] + ")");
				currentEvents[evCmd[1]].changeEventState(STATE_ACTIVE);
				return true;
			case EventCommand.EVENT_PAUSE:
				currentEvents[evCmd[1]].changeEventState(STATE_PAUSED);
				return true;
			case EventCommand.WAIT: //wait
				short waitTime = GameObject.readShort(evCmd, 3);
				if (evCmd[3] == evCmd[1] && evCmd[4] == evCmd[2]) {
					eventLog("Waiting for " + waitTime);
				}
				if (waitTime < 0) {
					evCmd[3] = evCmd[1];
					evCmd[4] = evCmd[2];
					return true;
				} else {
					write16(evCmd, 3, waitTime - GameRuntime.updateDelta);
					return false;
				}
			case EventCommand.VAR_SET: //set event variable
				if (evCmd[1] == 1) {
					setFloatEventVar(evCmd[2], readFloat(evCmd, 3));
				}
				if (evCmd[1] == 2) {
					eventVars[evCmd[2]] = readInteger(evCmd, 3);
				}
				return true;
			case EventCommand.VAR_ADD: //event var +
				if (evCmd[1] == 1) {
					setFloatEventVar(evCmd[2], getFloatEventVar(evCmd[2]) + readFloat(evCmd, 3));
				}
				if (evCmd[1] == 2) {
					eventVars[evCmd[2]] += readInteger(evCmd, 3);
				}
				return true;
			case EventCommand.VAR_SUB: //event var -
				if (evCmd[1] == 1) {
					setFloatEventVar(evCmd[2], getFloatEventVar(evCmd[2]) - readFloat(evCmd, 3));
				}
				if (evCmd[1] == 2) {
					eventVars[evCmd[2]] -= readInteger(evCmd, 3);
				}
				return true;
			case EventCommand.VAR_MUL: //event var *
				if (evCmd[1] == 1) {
					setFloatEventVar(evCmd[2], getFloatEventVar(evCmd[2]) * readFloat(evCmd, 3));
				}
				if (evCmd[1] == 2) {
					eventVars[evCmd[2]] *= readInteger(evCmd, 3);
				}
				return true;
			case EventCommand.VAR_DIV: //event var /
				if (evCmd[1] == 1) {
					setFloatEventVar(evCmd[2], getFloatEventVar(evCmd[2]) / readFloat(evCmd, 3));
				}
				if (evCmd[1] == 2) {
					eventVars[evCmd[2]] /= readInteger(evCmd, 3);
				}
				return true;
			case EventCommand.BRANCH_IF_NE: //event var CMPEQ
				if (evCmd[1] == 2) {
					if (eventVars[evCmd[2]] == readInteger(evCmd, 3)) {
						return true;
					}
				} else if (evCmd[1] == 1 && getFloatEventVar(evCmd[2]) == readFloat(evCmd, 3)) {
					return true;
				}
				currentEvent = (byte) (evCmd[readPtr] - 2);
				return true;
			case EventCommand.BRANCH_IF_EQ: //event var CMPNE
				if (evCmd[1] == 2) {
					if (eventVars[evCmd[2]] != readInteger(evCmd, 3)) {
						return true;
					}
				} else if (evCmd[1] == 1 && getFloatEventVar(evCmd[2]) != readFloat(evCmd, 3)) {
					return true;
				}
				currentEvent = (byte) (evCmd[readPtr] - 2);
				return true;
			case EventCommand.BRANCH_IF_GEQ: //event var CMPLESS
				if (evCmd[1] == 2) {
					if (eventVars[evCmd[2]] < readInteger(evCmd, 3)) {
						return true;
					}
				} else if (evCmd[1] == 1 && getFloatEventVar(evCmd[2]) < readFloat(evCmd, 3)) {
					return true;
				}
				currentEvent = (byte) (evCmd[readPtr] - 2);
				return true;
			case EventCommand.BRANCH_IF_LEQ: //event var CMPGRTR
				if (evCmd[1] == 2) {
					if (eventVars[evCmd[2]] > readInteger(evCmd, 3)) {
						return true;
					}
				} else if (evCmd[1] == 1 && getFloatEventVar(evCmd[2]) > readFloat(evCmd, 3)) {
					return true;
				}
				currentEvent = (byte) (evCmd[readPtr] - 2);
				return true;
			case EventCommand.OBJ_MOVE: //move object to position
				GameObject moveobj = getObjectRoot().searchByObjId(GameObject.readShort(evCmd, 1));
				if (moveobj == null) {
					return true;
				} else {
					int counter = GameObject.readInt(evCmd, 15);
					int delta = GameRuntime.updateDelta;
					if (delta >= counter) {
						evCmd[15] = evCmd[11];
						evCmd[16] = evCmd[12];
						evCmd[17] = evCmd[13];
						evCmd[18] = evCmd[14];
						delta = counter;
					}
					moveobj.localObjectMatrix.translationX += GameObject.readInt(evCmd, 3) * delta;
					moveobj.localObjectMatrix.translationY += GameObject.readInt(evCmd, 7) * delta;
					moveobj.setIsDirtyRecursive();
					moveobj.setBBoxIsDirty();
					int newCounter = counter - delta;
					if (newCounter <= 0) {
						return true;
					} else {
						write32(evCmd, 15, newCounter);
						return false;
					}
				}
			case EventCommand.OBJ_ROTATE: //rotate object
				GameObject rotObj = getObjectRoot().searchByObjId(GameObject.readShort(evCmd, 1));
				if (rotObj == null) {
					return true;
				} else {
					int counter = GameObject.readInt(evCmd, 11);
					int duration = GameObject.readInt(evCmd, 7);
					int delta = GameRuntime.updateDelta;
					if (counter == duration) {
						write32(evCmd, 15, rotObj.localObjectMatrix.m00); //save initial rotation
						write32(evCmd, 19, rotObj.localObjectMatrix.m01);
						write32(evCmd, 23, rotObj.localObjectMatrix.m10);
						write32(evCmd, 27, rotObj.localObjectMatrix.m11);
					}
					if (counter - delta <= 0) {
						evCmd[11] = evCmd[7];
						evCmd[12] = evCmd[8];
						evCmd[13] = evCmd[9];
						evCmd[14] = evCmd[10];
						counter = 0;
					}
					float rotation = LP32.LP32ToFP32((int) ((((long) GameObject.readInt(evCmd, 3)) * ((long) (duration - counter))) / ((long) duration)));
					Matrix.temp.setRotation(rotation);
					Matrix.temp.translationX = 0;
					Matrix.temp.translationY = 0;
					rotObj.localObjectMatrix.m00 = GameObject.readInt(evCmd, 15);
					rotObj.localObjectMatrix.m01 = GameObject.readInt(evCmd, 19);
					rotObj.localObjectMatrix.m10 = GameObject.readInt(evCmd, 23);
					rotObj.localObjectMatrix.m11 = GameObject.readInt(evCmd, 27);
					rotObj.localObjectMatrix.mul(Matrix.temp);
					rotObj.setIsDirtyRecursive();
					rotObj.setBBoxIsDirty();
					int newCounter = counter - delta;
					if (newCounter <= 0) {
						return true;
					} else {
						write32(evCmd, 11, newCounter);
						return false;
					}
				}
			case EventCommand.OBJ_SETPOS: //copy translation from other object or eventcmd
			{
				GameObject destObj = getObjectRoot().searchByObjId(GameObject.readShort(evCmd, 1));
				if (destObj != null) {
					short srcObjId = GameObject.readShort(evCmd, 3);
					if (srcObjId < 0) {
						destObj.localObjectMatrix.translationX = GameObject.readInt(evCmd, 5);
						destObj.localObjectMatrix.translationY = GameObject.readInt(evCmd, 9);
						eventLog("Set translation of object " + destObj.getObjectId() + " to (" + (destObj.localObjectMatrix.translationX >> 16) + ", " + (destObj.localObjectMatrix.translationY >> 16) + ")");
					} else {
						eventLog("Set translation of object " + destObj.getObjectId() + " from " + srcObjId);
						GameObject srcObj = getObjectRoot().searchByObjId(srcObjId);
						if (srcObj != null) {
							srcObj.loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
							int srcTxAbs = GameObject.tmpObjMatrix.translationX;
							int srcTyAbs = GameObject.tmpObjMatrix.translationY;
							destObj.localObjectMatrix.translationX = 0;
							destObj.localObjectMatrix.translationY = 0;
							destObj.objectMatrixIsDirty = true;
							destObj.loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
							GameObject.tmpObjMatrix.invert(Matrix.temp); //load inverse rotation matrix
							Matrix.temp.mulVector(srcTxAbs, srcTyAbs);
							destObj.localObjectMatrix.translationX = Matrix.vectorMulRslX;
							destObj.localObjectMatrix.translationY = Matrix.vectorMulRslY;
						}
					}
					destObj.setIsDirtyRecursive();
					destObj.setBBoxIsDirty();
					destObj.loadObjectMatrixToTarget(destObj.renderCalcMatrix);
				}
				return true;
			}
			case EventCommand.OBJ_ATTACH: //reset parent to level root
			{
				short parentWhoId = GameObject.readShort(evCmd, 1);
				short parentToId = GameObject.readShort(evCmd, 3);
				GameObject parentWho = GameObject.dummyParent.searchByObjId(parentWhoId);
				if (parentWho != null) {
					GameObject parentTo = getObjectRoot().searchByObjId(parentToId);
					if (parentTo != null) {
						parentWho.setParent(parentTo);
						parentWho.setBBoxIsDirty();
					}
				}
				return true;
			}
			case EventCommand.OBJ_DETACH: //parent to dummy
			{
				GameObject parentWho = getObjectRoot().searchByObjId(GameObject.readShort(evCmd, 1));
				if (parentWho != null) {
					parentWho.setBBoxIsDirty();
					parentWho.setParent(GameObject.dummyParent);
				}
				return true;
			}
			case EventCommand.BRANCH: //branch to event unconditionally
				currentEvent = (byte) (evCmd[1] - 2);
				eventLog("BranchEvent " + currentEvent);
				return true;
			case EventCommand.NOP: //NOP
				return true;
			case EventCommand.END: //end event
				if (repeatable == 1) {
					eventState = STATE_WAITING;
				} else {
					eventState = STATE_TERMINATED;
				}
				return true;
			case EventCommand.WAIT_ACTOR_GONE:
				return !arrayContains(lastActorsInArea, lastAreaActorCount, getObjectRoot().searchByObjId(GameObject.readShort(evCmd, 1)));
			case EventCommand.CHECKPOINT: //checkpoint reached
				BounceGame.checkpointPosX = renderCalcMatrix.translationX;
				BounceGame.checkpointPosY = renderCalcMatrix.translationY;
				eventLog("Checkpoint reached: " + BounceGame.checkpointPosX + ", " + BounceGame.checkpointPosY);
				return true;
			case EventCommand.PUSH: //force gravity push
				GameObject pushTarget = getObjectRoot().searchByObjId(GameObject.readShort(evCmd, 1));
				if (pushTarget.objType == BounceObject.TYPEID) {
					BounceObject bounce = (BounceObject) pushTarget;
					bounce.pushX += (float) GameObject.readShort(evCmd, 3);
					bounce.pushY += (float) GameObject.readShort(evCmd, 5);
				}
				return true;
			case EventCommand.GRAVITATE:
				GameObject gravityTarget = getObjectRoot().searchByObjId(GameObject.readShort(evCmd, 1));
				if (gravityTarget.objType == BounceObject.TYPEID) {
					BounceObject bounce = (BounceObject) gravityTarget;
					bounce.gravityX += (float) GameObject.readShort(evCmd, 3);
					bounce.gravityY += (float) GameObject.readShort(evCmd, 5);
				}
				return true;
			case EventCommand.ACCELERATE:
				GameObject accelTarget = getObjectRoot().searchByObjId(GameObject.readShort(evCmd, 1));
				if (accelTarget.objType == BounceObject.TYPEID) {
					BounceObject bounce = (BounceObject) accelTarget;
					bounce.curXVelocity += (float) GameObject.readShort(evCmd, 3);
					bounce.curYVelocity += (float) GameObject.readShort(evCmd, 5);
				}
				return true;
			case EventCommand.OBJ_SET_FLAGS: {
				GameObject obj = getObjectRoot().searchByObjId(GameObject.readShort(evCmd, 1));
				if (obj != null) {
					eventLog("evcmd 29 on object " + obj.getObjectId());
					int flagExistMask = GameObject.readInt(evCmd, 3);
					int flagValues = GameObject.readInt(evCmd, 7);
					if ((flagExistMask & 1) != 0) {
						obj.flags &= ~FLAG_Z_COORD_MASK;
						obj.flags |= flagValues & FLAG_Z_COORD_MASK;
						obj.zCoord = (byte) ((obj.flags & FLAG_Z_COORD_MASK) - 16);
					}
					if ((flagExistMask & FLAG_NOCOLLIDE) != 0) {
						obj.flags &= ~FLAG_NOCOLLIDE;
						obj.flags |= flagValues & FLAG_NOCOLLIDE;
					}
					if ((flagExistMask & FLAG_NODRAW) != 0) {
						obj.flags &= ~FLAG_NODRAW;
						obj.flags |= flagValues & FLAG_NODRAW;
						if (obj.getObjType() == SpriteObject.TYPEID) {
							SpriteObject sprite = (SpriteObject) obj;
							if (sprite.imageIDs[0] == 358) { //evil machine
								sprite.loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
								BounceGame.colorMachineDestroyParticle.emitIndependentBursts(
										24,
										sprite.bboxMinX + (60 << 16) + GameObject.tmpObjMatrix.translationX,
										sprite.bboxMinY + (60 << 16) + GameObject.tmpObjMatrix.translationY,
										(sprite.bboxMaxX - (60 << 16)) + GameObject.tmpObjMatrix.translationX,
										(sprite.bboxMaxY - (60 << 16)) + GameObject.tmpObjMatrix.translationY,
										840,
										0,
										0,
										360,
										2040,
										510
								);
							}
						}
					}
					if ((flagExistMask & 256) > 0) {
						obj.flags &= ~256;
						obj.flags |= flagValues & 0x100;
					}
				}
				return true;
			}
			case EventCommand.CAMERA_TARGET: //change camera target
				GameObject.cameraTarget = getObjectRoot().searchByObjId(GameObject.readShort(evCmd, 1));
				eventLog("New camera target: " + GameObject.cameraTarget);
				return true;
			case EventCommand.CAMERA_SETPARAM:
				BounceGame.reqCameraSnap = evCmd[1] == 1;
				GameObject.cameraBounceFactor = GameObject.readShort(evCmd, 2);
				GameObject.cameraStabilizeSpeed = GameObject.readShort(evCmd, 4);
				eventLog("Camera return: snap " + BounceGame.reqCameraSnap + " / a " + GameObject.cameraBounceFactor + " / b " + GameObject.cameraStabilizeSpeed);
				return true;
			case EventCommand.CAMERA_SETPARAM_DEFAULT:
				BounceGame.reqCameraSnap = false;
				GameObject.cameraBounceFactor = 90;
				GameObject.cameraStabilizeSpeed = 140;
				return true;
			default:
				return true;
		}
	}

	/* renamed from: a */
	public static void updateEvents(EventObject[] events) {
		currentEvents = events;
		for (int eventIdx = 0; eventIdx < events.length; eventIdx++) {
			EventObject event = events[eventIdx];
			if (event.triggerByLeave == 0) {
				for (int i = 0; i < event.queuedAreaActorCount; i++) {
					GameObject queueActor = event.actorsInAreaQueue[i];
					if (!arrayContains(event.lastActorsInArea, event.lastAreaActorCount, queueActor)) {
						triggerCandidates[i] = queueActor;
					}
				}
			} else {
				for (int i = 0; i < event.lastAreaActorCount; i++) {
					GameObject curActor = event.lastActorsInArea[i];
					if (!arrayContains(event.actorsInAreaQueue, event.queuedAreaActorCount, curActor)) {
						triggerCandidates[i] = curActor;
					}
				}
			}
			for (int i = 0; i < triggerCandidates.length; i++) {
				if (triggerCandidates[i] != null && (event.triggerObjId <= -1 || triggerCandidates[i].getObjectId() == event.triggerObjId)) {
					System.out.println("Actor " + triggerCandidates[i].getObjectId() + " triggered event " + event.getObjectId());
					event.changeEventState(STATE_ACTIVE);
					break;
				}
			}
			for (int i = 0; i < event.actorsInAreaQueue.length; i++) {
				event.lastActorsInArea[i] = event.actorsInAreaQueue[i];
				event.actorsInAreaQueue[i] = null;
				triggerCandidates[i] = null;
			}
			event.lastAreaActorCount = event.queuedAreaActorCount;
			event.queuedAreaActorCount = 0;
		}
		for (int i = 0; i < events.length; i++) {
			EventObject event = events[i];
			EventLoop:
			while (event.isChildOf(BounceGame.rootLevelObj) && event.eventState == STATE_ACTIVE && event.executeEvent(event.events[event.currentEvent])) {
				/*if (BounceGame.currentLevel == LevelID.FINAL_RIDE) {
					finalBossTimer += GameRuntime.updateDelta;
					//kinda bug, kinda snack. this probably shouldn't be getting updated with each event.
					if (event.objectId == 15) {
						if (finalBossTimer <= 20000) {
							event.eventState = STATE_WAITING;
						}
					}
				}*/ //removed in 2.0.25
				
				if (event.currentEvent < -1) {
					event.currentEvent = -1;
				}
				event.currentEvent++;
				if (event.currentEvent >= event.eventCount) {
					if (event.repeatable == 1) {
						event.eventState = STATE_WAITING;
						event.resetTransformEvents();
					} else {
						event.eventState = STATE_TERMINATED;
					}
				}
			}
		}
	}

	/* renamed from: b */
	private void resetTransformEvents() {
		this.currentEvent = -1;
		for (int i = 0; i < this.events.length; i++) {
			byte[] evt = this.events[i];
			switch (evt[0]) {
				case 6:
					evt[3] = evt[1];
					evt[4] = evt[2];
					break;
				case 16:
					evt[15] = evt[11];
					evt[16] = evt[12];
					evt[17] = evt[13];
					evt[18] = evt[14];
					break;
				case 17:
					evt[11] = evt[7];
					evt[12] = evt[8];
					evt[13] = evt[9];
					evt[14] = evt[10];
					break;
			}
		}
	}

	/* renamed from: a */
	public static void checkBounceEventTrigger(EventObject[] events, BounceObject bounce) {
		int bboxW = bounce.bboxMaxX - bounce.bboxMinX;
		int bboxH = bounce.bboxMaxY - bounce.bboxMinY;
		int maxBBoxDimHalf = bboxW < bboxH ? bboxH >> 1 : bboxW >> 1;
		for (int eventIdx = 0; eventIdx < events.length; eventIdx++) {
			EventObject event = events[eventIdx];
			if (event.eventState != STATE_TERMINATED) {
				event.loadObjectMatrixToTarget(GameObject.tmpObjMatrix);
				GameObject.tmpObjMatrix.invert(Matrix.temp);
				Matrix.temp.mulVector(bounce.renderCalcMatrix.translationX, bounce.renderCalcMatrix.translationY);
				int bounceOldX = Matrix.vectorMulRslX;
				int bounceOldY = Matrix.vectorMulRslY;
				Matrix.temp.mulVector(bounce.localObjectMatrix.translationX, bounce.localObjectMatrix.translationY);
				int bounceX = Matrix.vectorMulRslX;
				int bounceY = Matrix.vectorMulRslY;
				int aabbMinX = event.bboxMinX - maxBBoxDimHalf;
				int aabbMinY = event.bboxMinY - maxBBoxDimHalf;
				int aabbMaxX = event.bboxMaxX + maxBBoxDimHalf;
				int aabbMaxY = event.bboxMaxY + maxBBoxDimHalf;
				if ((GameObject.aabbCheckBoundCross(bounceOldX, bounceOldY, bounceX, bounceY, aabbMinX, aabbMinY, aabbMaxX, aabbMaxY)
						|| GameObject.aabbContainsPoint(bounceX, bounceY, aabbMinX, aabbMinY, aabbMaxX, aabbMaxY))
						&& event.queuedAreaActorCount < 2) {
					event.actorsInAreaQueue[event.queuedAreaActorCount] = bounce;
					event.queuedAreaActorCount++;
				}
			}
		}
	}

	/* renamed from: a */
	private static boolean arrayContains(Object[] array, int count, Object obj) {
		for (int i = 0; i < count; i++) {
			if (array[i] == obj) {
				return true;
			}
		}
		return false;
	}
}
