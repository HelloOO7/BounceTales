package bouncetales;

public class EventCommand {

	public static final int MESSAGE = 0;
	public static final int BLOWER = 1;
	public static final int EVENT_TERMINATE = 2;
	public static final int EVENT_CANCEL = 3;
	public static final int EVENT_START = 4;
	public static final int EVENT_PAUSE = 5;
	public static final int WAIT = 6;
	public static final int VAR_SET = 7;
	public static final int VAR_ADD = 8;
	public static final int VAR_SUB = 9;
	public static final int VAR_MUL = 10;
	public static final int VAR_DIV = 11;
	public static final int BRANCH_IF_NE = 12;
	public static final int BRANCH_IF_EQ = 13;
	public static final int BRANCH_IF_GEQ = 14;
	public static final int BRANCH_IF_LEQ = 15;
	public static final int OBJ_MOVE = 16;
	public static final int OBJ_ROTATE = 17;
	public static final int OBJ_SETPOS = 18;
	public static final int OBJ_ATTACH = 19;
	public static final int OBJ_DETACH = 20;
	public static final int BRANCH = 21;
	public static final int NOP = 22;
	public static final int END = 23;
	public static final int WAIT_ACTOR_GONE = 24;
	public static final int CHECKPOINT = 25;
	public static final int PUSH = 26;
	public static final int GRAVITATE = 27;
	public static final int ACCELERATE = 28;
	public static final int CMD_29 = 29;
	public static final int CAMERA_TARGET = 30;
	public static final int CAMERA_SETPARAM = 31;
	public static final int CAMERA_SETPARAM_DEFAULT = 32;
}
