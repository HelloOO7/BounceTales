package bouncetools.rlef.data;

import bouncetales.LP32;
import bouncetales.Matrix;
import java.awt.Color;
import java.awt.Graphics;
import java.io.DataInput;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteOrder;
import java.util.Arrays;
import xstandard.io.base.impl.ext.data.DataIOStream;
import xstandard.io.util.IndentedPrintStream;

public class EventData extends ObjectData {

	public int eventType;
	public int triggerByLeave;
	public int repeatable;
	public int triggerObjId;

	public EventCommand[] commands;

	public EventData(DataInput in, int objId) throws IOException {
		super(in, objId);

		bboxMinX = in.readShort() << 16;
		bboxMaxY = in.readShort() << 16;
		bboxMaxX = in.readShort() << 16;
		bboxMinY = in.readShort() << 16;

		eventType = in.readByte();
		triggerByLeave = in.readByte();
		repeatable = in.readByte();
		triggerObjId = in.readShort();

		int commandCount = in.readByte();
		commands = new EventCommand[commandCount];
		for (int cmdIdx = 0; cmdIdx < commandCount; cmdIdx++) {
			byte evCmdSize = in.readByte();
			byte[] data = new byte[evCmdSize];
			in.readFully(data);

			EventCommand ev = null;
			DataIOStream evin = new DataIOStream(data);
			evin.order(ByteOrder.BIG_ENDIAN);
			evin.skipBytes(1);
			switch (data[0]) {
				case 0:
					ev = new EvCmdFieldMessage(evin);
					break;
				case 1:
					ev = new EvCmdObjAnimate(evin);
					break;
				case 2:
					ev = new EvCmdEventTerminate(evin);
					break;
				case 3:
					ev = new EvCmdEventCancel(evin);
					break;
				case 4:
					ev = new EvCmdEventStart(evin);
					break;
				case 5:
					ev = new EvCmdEventPause(evin);
					break;
				case 6:
					ev = new EvCmdWait(evin);
					break;
				case 7:
					ev = new EvCmdVarSet(evin);
					break;
				case 8:
					ev = new EvCmdVarAdd(evin);
					break;
				case 9:
					ev = new EvCmdVarSub(evin);
					break;
				case 10:
					ev = new EvCmdVarMul(evin);
					break;
				case 11:
					ev = new EvCmdVarDiv(evin);
					break;
				case 12:
					ev = new EvCmdBranchNE(evin);
					break;
				case 13:
					ev = new EvCmdBranchEQ(evin);
					break;
				case 14:
					ev = new EvCmdBranchGEQ(evin);
					break;
				case 15:
					ev = new EvCmdBranchLEQ(evin);
					break;
				case 16:
					ev = new EvCmdObjMove(evin);
					break;
				case 17:
					ev = new EvCmdObjRotate(evin);
					break;
				case 18:
					ev = new EvCmdObjSetPos(evin);
					break;
				case 19:
					ev = new EvCmdObjAttach(evin);
					break;
				case 20:
					ev = new EvCmdObjDetach(evin);
					break;
				case 21:
					ev = new EvCmdBranch(evin);
					break;
				case 22:
					ev = new EvCmdNop(evin);
					break;
				case 23:
					ev = new EvCmdEnd(evin);
					break;
				case 24:
					ev = new EvCmdWaitActorGone(evin);
					break;
				case 25:
					ev = new EvCmdCheckpoint(evin);
					break;
				case 26:
					ev = new EvCmdPhysicsPush(evin);
					break;
				case 27:
					ev = new EvCmdPhysicsGravitate(evin);
					break;
				case 28:
					ev = new EvCmdPhysicsAccelerate(evin);
					break;
				case 29:
					ev = new EvCmdObjSetFlags(evin);
					break;
				case 30:
					ev = new EvCmdCameraTarget(evin);
					break;
				case 31:
					ev = new EvCmdCameraSetParam(evin);
					break;
				case 32:
					ev = new EvCmdCameraSetParamDefault(evin);
					break;
			}
			commands[cmdIdx] = ev;
		}
	}

	private static Operand readOperand(DataInput in) throws IOException {
		byte dataType = in.readByte();
		switch (dataType) {
			case 1:
				return new VarOperand(in.readByte(), true);
			case 2:
				return new VarOperand(in.readByte(), false);
			case 16:
				return new ConstFloatOperand(in.readFloat());
			case 32:
				return new ConstIntOperand(in.readInt());
			default:
				throw new RuntimeException("Unknown operand type: " + dataType);
		}
	}

	@Override
	public void draw(Graphics g) {
		Matrix mtx = getTransformMatrix();
		mtx.mulVector(bboxMinX, bboxMinY);
		int x = getLP32CastIntVtx(Matrix.vectorMulRslX);
		int y = getLP32CastIntVtx(Matrix.vectorMulRslY);
		mtx.mulVector(bboxMaxX, bboxMaxY);
		int mx = getLP32CastIntVtx(Matrix.vectorMulRslX);
		int my = getLP32CastIntVtx(Matrix.vectorMulRslY);
		g.setColor(Color.GREEN);
		g.drawRect(x, -my, mx - x, (my - y));
		g.setColor(Color.RED);
		g.fillArc(x - 5, -my - 5, 10, 10, 0, 360);
		g.setColor(Color.CYAN);
		g.drawString("EVENT " + idx, x, -my);
	}

	private int getLP32CastIntVtx(int lp32) {
		return (int) LP32.LP32ToFP32(lp32);
	}

	@Override
	public void dump(IndentedPrintStream out) {
		super.dump(out);
		out.println("| EVENT OBJECT |");
		dumpBBox(out);
		out.println(" - Trigger by leave: " + triggerByLeave);
		out.println(" - Repeatable: " + repeatable);
		out.println(" - Trigger object ID: " + triggerObjId);
		out.println();
		out.println(" - | EVENT BINARY DATA |");
		/*int idx = 0;
		for (byte[] bin : events) {
			out.println("     - EVENT No. " + idx + ": ");
			out.println("         - EvCmd: " + bin[0]);
			out.println("         - EvData: " + Arrays.toString(Arrays.copyOfRange(bin, 1, bin.length)));
			idx++;
		}*/
		out.incrementIndentLevel();
		out.incrementIndentLevel();
		int idx = 0;
		for (EventCommand cmd : commands) {
			out.println("Event " + idx + " | " + cmd.getClass().getSimpleName());
			out.incrementIndentLevel();
			cmd.dump(out);
			out.decrementIndentLevel();
			idx++;
		}
		out.decrementIndentLevel();
		out.decrementIndentLevel();
		out.println();
	}

	public static abstract class EventCommand {

		public EventCommand(DataInput in) throws IOException {

		}

		public abstract void dump(PrintStream out);
	}

	public static class EvCmdFieldMessage extends EventCommand {

		public short msgId;

		public EvCmdFieldMessage(DataInput in) throws IOException {
			super(in);
			msgId = in.readShort();
		}

		@Override
		public void dump(PrintStream out) {
			out.println("Message ID: " + msgId);
		}
	}

	public static class EvCmdObjAnimate extends EventCommand {

		public short objId;

		public EvCmdObjAnimate(DataInput in) throws IOException {
			super(in);
			objId = in.readShort();
		}

		@Override
		public void dump(PrintStream out) {
			out.println("Object ID: " + objId);
		}
	}

	public static class EvCmdEventBase extends EventCommand {

		public byte eventId;

		public EvCmdEventBase(DataInput in) throws IOException {
			super(in);
			eventId = in.readByte();
		}

		@Override
		public void dump(PrintStream out) {
			out.println("Event ID: " + eventId);
		}
	}

	public static class EvCmdEventTerminate extends EvCmdEventBase {

		public EvCmdEventTerminate(DataInput in) throws IOException {
			super(in);
		}
	}

	public static class EvCmdEventCancel extends EvCmdEventBase {

		public EvCmdEventCancel(DataInput in) throws IOException {
			super(in);
		}
	}

	public static class EvCmdEventStart extends EvCmdEventBase {

		public EvCmdEventStart(DataInput in) throws IOException {
			super(in);
		}
	}

	public static class EvCmdEventPause extends EvCmdEventBase {

		public EvCmdEventPause(DataInput in) throws IOException {
			super(in);
		}
	}

	public static class EvCmdWait extends EventCommand {

		public short waitTime;
		public short timeCounter;

		public EvCmdWait(DataInput in) throws IOException {
			super(in);
			waitTime = in.readShort();
			timeCounter = in.readShort();
		}

		@Override
		public void dump(PrintStream out) {
			out.println("Wait time: " + waitTime / 1000f + "s");
			out.println("Time counter: " + timeCounter / 1000f + "s");
		}
	}

	public static abstract class EvCmdVarBase extends EventCommand {

		public byte destType;
		public byte destVar;
		public Operand operand;

		public EvCmdVarBase(DataInput in) throws IOException {
			super(in);
			destType = in.readByte();
			destVar = in.readByte();
			operand = readOperand(in);
		}

		public abstract String getOpStr();

		@Override
		public void dump(PrintStream out) {
			out.println("Operation: " + ((destType == 2) ? "VARi" : "VARf") + "[" + destVar + "] " + getOpStr() + "= " + operand);
		}
	}

	public static class EvCmdVarSet extends EvCmdVarBase {

		public EvCmdVarSet(DataInput in) throws IOException {
			super(in);
		}

		@Override
		public String getOpStr() {
			return "";
		}
	}

	public static class EvCmdVarAdd extends EvCmdVarBase {

		public EvCmdVarAdd(DataInput in) throws IOException {
			super(in);
		}

		@Override
		public String getOpStr() {
			return "+";
		}
	}

	public static class EvCmdVarSub extends EvCmdVarBase {

		public EvCmdVarSub(DataInput in) throws IOException {
			super(in);
		}

		@Override
		public String getOpStr() {
			return "-";
		}
	}

	public static class EvCmdVarMul extends EvCmdVarBase {

		public EvCmdVarMul(DataInput in) throws IOException {
			super(in);
		}

		@Override
		public String getOpStr() {
			return "*";
		}
	}

	public static class EvCmdVarDiv extends EvCmdVarBase {

		public EvCmdVarDiv(DataInput in) throws IOException {
			super(in);
		}

		@Override
		public String getOpStr() {
			return "/";
		}
	}

	public static abstract class EvCmdBranchBase extends EventCommand {

		public byte cmpType;
		public byte cmpVar;
		public Operand operand;
		public byte branchPC;

		public EvCmdBranchBase(DataInput in) throws IOException {
			this(in, false);
		}

		public EvCmdBranchBase(DataInput in, boolean unconditional) throws IOException {
			super(in);
			if (!unconditional) {
				cmpType = in.readByte();
				cmpVar = in.readByte();
				operand = readOperand(in);
			}
			branchPC = in.readByte();
		}

		public abstract String getOpStr();

		@Override
		public void dump(PrintStream out) {
			out.println("Branch to: " + (branchPC - 1));
			if (cmpType > 0) {
				out.println("If: " + ((cmpType == 2) ? "VARi" : "VARf") + "[" + cmpType + "] " + getOpStr() + " " + operand);
			}
		}
	}

	public static class EvCmdBranchNE extends EvCmdBranchBase {

		public EvCmdBranchNE(DataInput in) throws IOException {
			super(in);
		}

		@Override
		public String getOpStr() {
			return "!=";
		}
	}

	public static class EvCmdBranchEQ extends EvCmdBranchBase {

		public EvCmdBranchEQ(DataInput in) throws IOException {
			super(in);
		}

		@Override
		public String getOpStr() {
			return "==";
		}
	}

	public static class EvCmdBranchGEQ extends EvCmdBranchBase {

		public EvCmdBranchGEQ(DataInput in) throws IOException {
			super(in);
		}

		@Override
		public String getOpStr() {
			return ">=";
		}
	}

	public static class EvCmdBranchLEQ extends EvCmdBranchBase {

		public EvCmdBranchLEQ(DataInput in) throws IOException {
			super(in);
		}

		@Override
		public String getOpStr() {
			return "<=";
		}
	}

	public static class EvCmdObjMove extends EventCommand {

		public short objId;
		public int speedX;
		public int speedY;
		public int timeReset;
		public int timeCounter;

		public EvCmdObjMove(DataInput in) throws IOException {
			super(in);
			objId = in.readShort();
			speedX = in.readInt();
			speedY = in.readInt();
			timeReset = in.readInt();
			timeCounter = in.readInt();
		}

		@Override
		public void dump(PrintStream out) {
			out.println("Object ID: " + objId);
			out.println("Speed X: " + LP32.LP32ToFP32(speedX));
			out.println("Speed Y: " + LP32.LP32ToFP32(speedY));
			out.println("Time: " + timeReset / 1000f + "s");
			out.println("Initial time: " + timeCounter / 1000f + "s");
		}
	}

	public static class EvCmdObjRotate extends EventCommand {

		public short objId;
		public int angle;
		public int duration;
		public int counter;

		public int backupM00;
		public int backupM01;
		public int backupM10;
		public int backupM11;

		public EvCmdObjRotate(DataInput in) throws IOException {
			super(in);
			objId = in.readShort();
			angle = in.readInt();
			duration = in.readInt();
			counter = in.readInt();
			backupM00 = in.readInt();
			backupM01 = in.readInt();
			backupM10 = in.readInt();
			backupM11 = in.readInt();
		}

		@Override
		public void dump(PrintStream out) {
			out.println("Object ID: " + objId);
			out.println("Angle: " + LP32.LP32ToFP32(angle) + "f");
			out.println("Time: " + duration / 1000f + "s");
			out.println("Counter: " + counter / 1000f + "s");
			out.println("Backup matrix: " + LP32.LP32ToFP32(backupM00) + "/" + LP32.LP32ToFP32(backupM01) + "/" + LP32.LP32ToFP32(backupM10) + "/" + LP32.LP32ToFP32(backupM11));
		}
	}

	public static class EvCmdObjSetPos extends EventCommand {

		public short objId;
		public short source;

		public int posX;
		public int posY;

		public EvCmdObjSetPos(DataInput in) throws IOException {
			super(in);
			objId = in.readShort();
			source = in.readShort();
			if (source < 0) {
				posX = in.readInt();
				posY = in.readInt();
			}
		}

		@Override
		public void dump(PrintStream out) {
			out.println("Object ID: " + objId);
			out.println("Source object ID: " + source);
			if (source < 0) {
				out.println("Position: (" + LP32.LP32ToFP32(posX) + ", " + LP32.LP32ToFP32(posY) + ")");
			}
		}
	}

	public static class EvCmdObjAttach extends EventCommand {

		public short child;
		public short parent;

		public EvCmdObjAttach(DataInput in) throws IOException {
			super(in);
			child = in.readShort();
			parent = in.readShort();
		}

		@Override
		public void dump(PrintStream out) {
			out.println("Object ID: " + child);
			out.println("Attach to object ID: " + parent);
		}
	}

	public static class EvCmdObjDetach extends EventCommand {

		public short objId;

		public EvCmdObjDetach(DataInput in) throws IOException {
			super(in);
			objId = in.readShort();
		}

		@Override
		public void dump(PrintStream out) {
			out.println("Object ID: " + objId);
		}
	}

	public static class EvCmdBranch extends EvCmdBranchBase {

		public EvCmdBranch(DataInput in) throws IOException {
			super(in, true);
		}

		@Override
		public String getOpStr() {
			return null;
		}
	}

	public static class EvCmdNop extends EventCommand {

		public EvCmdNop(DataInput in) throws IOException {
			super(in);
		}

		@Override
		public void dump(PrintStream out) {

		}
	}

	public static class EvCmdEnd extends EventCommand {

		public EvCmdEnd(DataInput in) throws IOException {
			super(in);
		}

		@Override
		public void dump(PrintStream out) {

		}
	}

	public static class EvCmdWaitActorGone extends EventCommand {

		public short objId;

		public EvCmdWaitActorGone(DataInput in) throws IOException {
			super(in);
			objId = in.readShort();
		}

		@Override
		public void dump(PrintStream out) {
			out.println("Object ID: " + objId);
		}
	}

	public static class EvCmdCheckpoint extends EventCommand {

		public EvCmdCheckpoint(DataInput in) throws IOException {
			super(in);
		}

		@Override
		public void dump(PrintStream out) {

		}
	}

	public static class EvCmdPhysicsBase extends EventCommand {

		public int xParam;
		public int yParam;

		public EvCmdPhysicsBase(DataInput in) throws IOException {
			super(in);
			xParam = in.readInt();
			yParam = in.readInt();
		}

		@Override
		public void dump(PrintStream out) {
			out.println("Modifier: (" + LP32.LP32ToFP32(xParam) + ", " + LP32.LP32ToFP32(yParam) + ")");
		}
	}

	public static class EvCmdPhysicsPush extends EvCmdPhysicsBase {

		public EvCmdPhysicsPush(DataInput in) throws IOException {
			super(in);
		}
	}

	public static class EvCmdPhysicsGravitate extends EvCmdPhysicsBase {

		public EvCmdPhysicsGravitate(DataInput in) throws IOException {
			super(in);
		}
	}

	public static class EvCmdPhysicsAccelerate extends EvCmdPhysicsBase {

		public EvCmdPhysicsAccelerate(DataInput in) throws IOException {
			super(in);
		}
	}

	public static class EvCmdObjSetFlags extends EventCommand {

		public short objId;
		public int mask;
		public int flags;

		public EvCmdObjSetFlags(DataInput in) throws IOException {
			super(in);
			objId = in.readShort();
			mask = in.readInt();
			flags = in.readInt();
		}

		@Override
		public void dump(PrintStream out) {
			out.println("Object ID: " + objId);
			if ((mask & 1) != 0) {
				out.println("Z-Index: " + (flags & 31));
			}
			if ((mask & 32) != 0) {
				out.println("Enable collisions: " + ((flags & 32) == 0));
			}
			if ((mask & 128) != 0) {
				out.println("Visible: " + ((flags & 128) == 0));
			}
		}
	}

	public static class EvCmdCameraTarget extends EventCommand {

		public short objId;

		public EvCmdCameraTarget(DataInput in) throws IOException {
			super(in);
			objId = in.readShort();
		}

		@Override
		public void dump(PrintStream out) {
			out.println("Object ID: " + objId);
		}
	}

	public static class EvCmdCameraSetParam extends EventCommand {

		public short bounceFactor;
		public short stabilizeSpeed;

		public EvCmdCameraSetParam(DataInput in) throws IOException {
			super(in);
			bounceFactor = in.readShort();
			stabilizeSpeed = in.readShort();
		}

		@Override
		public void dump(PrintStream out) {
			out.println("Bounce factor: " + bounceFactor);
			out.println("Stabilize speed: " + stabilizeSpeed);
		}
	}

	public static class EvCmdCameraSetParamDefault extends EventCommand {

		public EvCmdCameraSetParamDefault(DataInput in) throws IOException {
			super(in);
		}

		@Override
		public void dump(PrintStream out) {
		}
	}

	public static interface Operand {

	}

	public static class VarOperand implements Operand {

		public boolean isFloat;
		public int varId;

		public VarOperand(int id, boolean isFloat) {
			this.varId = id;
			this.isFloat = isFloat;
		}

		@Override
		public String toString() {
			return (isFloat ? "VARf" : "VARi") + "[" + varId + "]";
		}
	}

	public static class ConstIntOperand implements Operand {

		public int value;

		public ConstIntOperand(int value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return Integer.toString(value);
		}
	}

	public static class ConstFloatOperand implements Operand {

		public float value;

		public ConstFloatOperand(float value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return Float.toString(value) + "f";
		}
	}
}
