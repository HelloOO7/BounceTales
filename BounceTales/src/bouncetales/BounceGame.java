package bouncetales;

import bouncetales.ext.rsc.ImageMap;
import java.util.Random;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/* renamed from: m */
public final class BounceGame {

	public static final int CONTROLLER_NORMAL = 0;
	public static final int CONTROLLER_CANNON = 1;
	public static final int CONTROLLER_DISABLED = 2;
	public static final int CONTROLLER_FROZEN = 3;

	public static final int PLAYER_STATE_PLAY = 0;
	public static final int PLAYER_STATE_LOSE = 1;
	public static final int PLAYER_STATE_WIN = 2;
	public static final int PLAYER_STATE_LOSE_UPDATE = 3;
	public static final int PLAYER_STATE_WIN_UPDATE = 4;

	/*
	Constants
	 */
	public static short[] SIN_COS_TABLE = new short[360]; //renamed from: b

	private static final byte[] CHEAT_COMBO_ALL_UNLOCK = {KeyCode.NUM0, KeyCode.NUM0, KeyCode.NUM0}; //renamed from: a
	private static final byte[] CHEAT_COMBO_ALL_COMPLETE = {KeyCode.NUM1, KeyCode.NUM1, KeyCode.NUM1}; //renamed from: b

	public static final int CANNON_LEVEL_INDEX = 15;

	private static final short[] LEVEL_RESIDS = {
		ResourceID.LEVELS_LEVEL_CAMPAIGN01_RLEF,
		ResourceID.LEVELS_LEVEL_CAMPAIGN02_RLEF,
		ResourceID.LEVELS_LEVEL_CAMPAIGN03_RLEF,
		ResourceID.LEVELS_LEVEL_CAMPAIGN04_RLEF,
		ResourceID.LEVELS_LEVEL_EXTRA01_RLEF,
		ResourceID.LEVELS_LEVEL_CAMPAIGN05_RLEF,
		ResourceID.LEVELS_LEVEL_CAMPAIGN06_RLEF,
		ResourceID.LEVELS_LEVEL_CAMPAIGN07_RLEF,
		ResourceID.LEVELS_LEVEL_CAMPAIGN08_RLEF,
		ResourceID.LEVELS_LEVEL_EXTRA02_RLEF,
		ResourceID.LEVELS_LEVEL_CAMPAIGN09_RLEF,
		ResourceID.LEVELS_LEVEL_CAMPAIGN10_RLEF,
		ResourceID.LEVELS_LEVEL_CAMPAIGN11_RLEF,
		ResourceID.LEVELS_LEVEL_CAMPAIGN12_RLEF,
		ResourceID.LEVELS_LEVEL_EXTRA03_RLEF,
		ResourceID.LEVELS_LEVEL_OBJ01_CANNON_RLEF
	}; //renamed from: v

	private static final short[] LEVEL_NAME_MESSAGE_IDS = {
		MessageID.LEVEL_MISTY_MORNING,
		MessageID.LEVEL_UNFRIENDLY_FRIENDS,
		MessageID.LEVEL_SEEKING_ANSWERS,
		MessageID.LEVEL_BUMPY_CRACKS,
		MessageID.LEVEL_SECRET_STALKWAY,
		MessageID.LEVEL_INTO_THE_MINES,
		MessageID.LEVEL_A_GLOOMY_PATH,
		MessageID.LEVEL_RUMBLING_SOUNDS,
		MessageID.LEVEL_TRAPPED_IN_MACHINE,
		MessageID.LEVEL_TUNNEL_OF_TREASURES,
		MessageID.LEVEL_WICKED_CIRCUS,
		MessageID.LEVEL_HUNTING_COLOURS,
		MessageID.LEVEL_ALMOST_THERE,
		MessageID.LEVEL_FINAL_RIDE,
		MessageID.LEVEL_FANTASTIC_FAIR
	}; //renamed from: h

	private static final short[] LEVEL_COVER_ART_IMAGE_IDS = {
		359,
		363,
		364,
		365,
		328,
		366,
		367,
		368,
		369,
		329,
		370,
		360,
		361,
		362,
		330
	}; //renamed from: g

	private static final short[] LEVEL_EGG_TROPHY_REQUIREMENTS = {
		30, 29, 26,
		30, 28, 26,
		30, 25, 21,
		30, 25, 20,
		30, 30, 30,
		30, 28, 25,
		30, 28, 22,
		30, 27, 20,
		30, 27, 24,
		30, 30, 30,
		30, 29, 27,
		30, 28, 19,
		30, 25, 20,
		30, 25, 20,
		30, 30, 30
	}; //renamed from: d

	private static final short[] LEVEL_TIMER_TROPHY_REQUIREMENTS = {
		30, 40, 50,
		36, 45, 55,
		35, 40, 48,
		32, 42, 50,
		9999, 9999, 9999,
		34, 45, 60,
		75, 85, 95,
		42, 46, 55,
		45, 55, 65,
		9999, 9999, 9999,
		45, 50, 58,
		60, 70, 80,
		48, 54, 60,
		24, 34, 44,
		9999, 9999, 9999
	}; //renamed from: e

	private static final float[] EGG_SCORE_MULTIPLIER_BY_LEVEL = {
		0.937f,
		0.969f,
		0.659f,
		0.937f,
		1.412f,
		0.969f,
		2.121f,
		1.298f,
		1.298f,
		1.011f,
		1.298f,
		1.921f,
		1.298f,
		0.712f,
		1.195f
	}; //renamed from: a

	private static final int[] BONUS_LEVEL_INFO = {
		LevelID.SECRET_STALKWAY, 60,
		LevelID.TUNNEL_OF_TREASURES, 200,
		LevelID.FANTASTIC_FAIR, 400
	}; //renamed from: c

	private static final int[] FORME_UNLOCK_LEVELS = {3, 8}; //renamed from: d

	private static final short[] NUMBER_FONT_IMAGE_IDS = {90, 91, 92, 93, 94, 95, 96, 97, 98, 99}; //renamed from: w

	private static final int PARALLAX_MAX_COUNT = 5;

	private static final short[] ALL_PARALLAX_IMAGE_IDS = {388, 373, 145, 313, 265, 157, 174, 55, 345, 243, 78, 317, 176, 267}; //renamed from: u
	private static short[] f307i = {388}; //renamed from: i
	private static short[] f311j = {251}; //renamed from: j
	private static short[] f314k = {252}; //renamed from: k
	private static short[] f318l = new short[0]; //renamed from: l
	private static short[] f322m = {373}; //renamed from: m
	private static short[] f325n = {161}; //renamed from: n
	private static short[] f328o = {159, 160}; //renamed from: o
	private static short[] f331p = new short[0]; //renamed from: p
	private static short[] f334q = {352}; //renamed from: q
	private static short[] f337r = {113}; //renamed from: r
	private static short[] f340s = {114}; //renamed from: s
	private static short[] f343t = {353}; //renamed from: t

	private static final int[] WIN_PARTICLE_IMAGE_IDS = {420, 426, 402, 408}; //renamed from: j
	private static final int[] SPLASH_PARTICLE_IMAGE_IDS = {526, 516, 531, 521}; //renamed from: f
	private static final int[] BUBBLE_PARTICLE_IMAGE_IDS = {536}; //renamed from: e
	private static final int[] COLOR_MACHINE_DESTROY_PARTICLE_IMAGE_IDS = {390, 414, 396}; //renamed from: l
	private static final int[] SUPER_BOUNCE_PARTICLE_IMAGE_IDS = {420, 426}; //renamed from: k
	private static final int[] CANNON_PARTICLE_IMAGE_IDS = {390, 420}; //renamed from: g
	private static final int[] EGG_COLLECT_PARTICLE_IMAGE_IDS = {420, 426}; //renamed from: h
	private static final int[] ENEMY_DEATH_PARTICLE_IMAGE_IDS = {414, 396, 420}; //renamed from: i
	private static final int[] AIR_PARTICLE_IMAGE_IDS = {437, 432}; //renamed from: m

	private static final short[] TROPHY_IMAGE_IDS = {314, 315, 389}; //renamed from: c

	public static final short[] SCRIPT_MESSAGE_IDS = MessageID.ALL_MESSAGES_SORTED; //renamed from: a

	private static final int[] SPLASH_SCREEN_LAYOUT_RESIDS = {ResourceID.GRAPHICS_SPLASHLOGO_RES}; //renamed from: o
	private static final int[] SPLASH_SCREEN_DURATIONS = {100}; //renamed from: q
	private static final int[] SPLASH_BG_COLORS = {0xFFFFFF}; //renamed from: r
	private static final int[] SPLASH_IMAGE_IDS = {1}; //renamed from: p

	private static final int LAYOUT_MAIN_MENU_TITLE_PADDING = 129; //renamed from: q
	private static final int LAYOUT_DEFAULT_TITLE_PADDING_TOP = 40; //renamed from: r
	private static final int LAYOUT_DEFAULT_TITLE_PADDING_BOTTOM = 5; //renamed from: s
	private static final int LAYOUT_DEFAULT_VERTICAL_MARGIN = 56; //renamed from: t
	private static final int LAYOUT_DEFAULT_HORIZONTAL_MARGIN = 20; //renamed from: u
	private static final int LAYOUT_DEFAULT_HORIZONTAL_MARGIN_INGAME = 40; //renamed from: v

	//State - root
	public static Random mRNG = new Random(System.currentTimeMillis()); //renamed from: a

	private static short[] levelSaveData = new short[60]; //renamed from: f
	public static boolean isSuperBounceUnlocked; //renamed from: d

	private static int totalGameTime; //renamed from: j
	private int gameMainState = 1; //renamed from: i

	private static boolean reqQuit = false; //added in 2.0.25 for more game URL action
	private static boolean reqPlayTitleMusic = false;

	private static final boolean moreGamesStatus;
	private static final String moreGamesURL;

	private static final boolean enableCheats; //renamed from: f
	private static byte cheatComboIndex; //renamed from: a

	private static int renderClipWidth = GameRuntime.currentWidth; //renamed from: x
	private static int renderClipHeight = GameRuntime.currentHeight; //renamed from: y

	//State - text
	private static boolean isTextRightToLeft = StringManager.getMessage(MessageID.IS_TEXT_RIGHT_TO_LEFT_RESERVED).equals("1"); //renamed from: e

	//State - layout core
	private final UILayout ui = new UILayout(); //renamed from: b
	private UILayout drawUI = null; //renamed from: a

	//State - loading
	private int curSplashId; //renamed from: C
	private long splashScreenStartTime; //renamed from: a

	private static boolean hasLoadingProgressBar = false; //renamed from: j
	private int loadingProgressBar = 0; //renamed from: B

	//State - menus
	private static int exitLevelReturnScene = 17; //renamed from: A
	private static int lastMenuOption = 0; //renamed from: o
	private static int selectedLevelId = 0; //renamed from: k

	private static int lastSelectedLevelId = 0; //renamed from: l

	private static int bookAnimationTime = 0; //renamed from: m
	private static int targetBookAnimationTime = 0; //renamed from: n

	//State - softkey bar polygon coordinates
	private static int[] xluSoftkeyBarXs = new int[4]; //renamed from: v
	private static int[] xluSoftkeyBarYs = new int[4]; //renamed from: w

	//State - framebuffers
	public static Graphics ballGraphics; //renamed from: a
	public static Image ballFramebuffer; //renamed from: a
	public static int[] ballFramebufferRGB; //renamed from: a

	public static Graphics spriteOffscreenGraphics; //renamed from: b
	public static Image spriteFB; //renamed from: b
	public static int[] spriteFBRGB; //renamed from: b

	//State - level
	private boolean isLevelActive = false; //renamed from: k

	private static boolean isBlockingEvent; //renamed from: h

	public static boolean reqCameraSnap = false; //renamed from: c

	public static boolean levelPaused; //renamed from: b

	public static int currentLevel; //renamed from: e
	private static int objectCount; //renamed from: D
	private static int eventCount; //renamed from: E

	private static EventObject[] events; //renamed from: a
	private static GameObject[] levelObjects; //renamed from: a
	public static GameObject[] cannonModels; //renamed from: b

	public static GameObject rootLevelObj; //renamed from: a

	public static BounceObject bounceObj; //renamed from: a
	public static CannonObject currentCannon; //renamed from: a

	private static int bonusLevelEggLimit; //renamed from: G

	//State - level progress
	public static int levelTimer; //renamed from: a
	public static int eggCount; //renamed from: f

	public static int checkpointPosX; //renamed from: g
	public static int checkpointPosY; //renamed from: h

	public static boolean waterSingletonFlag; //renamed from: a

	//State - stolen colors
	private static boolean isFlashToOtherColorMode; //renamed from: l
	private static boolean isColorsAreStolen; //renamed from: m
	private static int stolenColorsAnimationCountdown; //renamed from: H
	private static int stolenColorsFlashCountdown; //renamed from: I

	//Particles
	public static ParticleObject winParticle = new ParticleObject(20, 0, 0, 0, 0, 35, 4, WIN_PARTICLE_IMAGE_IDS, 1840, -4); //renamed from: a
	public static ParticleObject waterSplashParticle = new ParticleObject(20, 0, -200, 0, 0, 0, 0, SPLASH_PARTICLE_IMAGE_IDS, 800, 7); //renamed from: b
	public static ParticleObject bubbleParticle = new ParticleObject(150, 0, 80, 0, 0, 0, 1, BUBBLE_PARTICLE_IMAGE_IDS, 4000, 7); //renamed from: c
	public static ParticleObject superBounceParticle = new ParticleObject(10, 0, 0, 0, 0, 0, 6, SUPER_BOUNCE_PARTICLE_IMAGE_IDS, 1000, -5); //renamed from: d
	public static ParticleObject cannonParticle = new ParticleObject(10, 0, 0, 0, 0, 30, 2, CANNON_PARTICLE_IMAGE_IDS, 800, -1); //renamed from: e
	public static ParticleObject eggCollectParticle = new ParticleObject(50, 0, 0, 0, 0, 85, 3, EGG_COLLECT_PARTICLE_IMAGE_IDS, 540, -2); //renamed from: f
	public static ParticleObject enemyDeathParticle = new ParticleObject(50, 0, 0, 0, 0, 35, 4, ENEMY_DEATH_PARTICLE_IMAGE_IDS, 1840, -3); //renamed from: g
	public static ParticleObject colorMachineDestroyParticle = new ParticleObject(24, 0, 0, 0, 0, 35, 4, COLOR_MACHINE_DESTROY_PARTICLE_IMAGE_IDS, 2040, -6); //renamed from: h
	public static ParticleObject airTunnelParticle = new ParticleObject(150, 0, 0, 0, 0, 0, 7, AIR_PARTICLE_IMAGE_IDS, 2000, 15); //renamed from: i

	//Extra entities
	public static EggObject enemyDeadEgg; //renamed from: a

	//State - level exit
	public static int exitWaitTimer; //renamed from: b
	public static int deathBaseY; //renamed from: c

	//State - field message
	private static boolean isFieldMessageShowing; //renamed from: g
	private static String lastFieldMsg; //renamed from: a

	private static int[] fieldMessageQueue = new int[5]; //renamed from: n
	private static int fieldMessagePointer; //renamed from: z
	private static String[] fieldMessageParam = null; //renamed from: a

	private boolean reqQuitLevelAfterFieldMessage = false; //renamed from: p
	private static boolean reqReloadFieldMsg = false; //renamed from: i

	//State - parallax
	private static Image[] parallaxImagesRegColors; //renamed from: a
	private static Image[] parallaxImagesStolenColors; //renamed from: b

	private static int f240F; //renamed from: F

	//array size bugfixed for HD parallaxes
	//another slight optimization: originally the size allocated was 10 (double the parallax count)
	//instead of 6 (parallax count + 1), which was just enough. reducing it changes nothing and descreases memory footprint.
	private static int[] parallaxXOffsets = new int[PARALLAX_MAX_COUNT * ((renderClipWidth + 239) / 240) * ((renderClipHeight + 319) / 320) + 1]; //renamed from: s
	private static int[] parallaxYOffsets = new int[parallaxXOffsets.length]; //renamed from: t
	private static int[] parallaxImageIndices = new int[parallaxYOffsets.length]; //renamed from: u

	//State - after level cleared
	private static int calcScore; //renamed from: p

	private int timerChallengeTrophy = -1; //renamed from: J
	private int collectionChallengeTrophy = -1; //renamed from: K

	private boolean wasFinalLevelJustBeaten = false; //renamed from: n
	private boolean wasSuperBounceJustUnlocked = false; //renamed from: o
	private boolean highScoreBeaten = false; //renamed from: q

	public static final int unused_f279d = 0; //renamed from: d
	private static int unused_f350w = 0; //renamed from: w

	static {
		enableCheats = GameRuntime.getAppFlag("Cheats");
		moreGamesStatus = GameRuntime.getAppFlag("more_games_status");
		moreGamesURL = GameRuntime.mMidLet.getAppProperty("more_games_url");

		generateSinCosTable();
	}

	/* renamed from: a */
	private static void generateSinCosTable() {
		int curve = 0;
		int tangent = 57 * 360;
		for (int angle = 0; angle < 360; angle++) {
			int sin = curve / 57;
			SIN_COS_TABLE[angle] = (short) sin;
			tangent -= sin;
			curve += tangent / 57;
		}
	}

	/* renamed from: a */
	private static void setBGColor(int rgb, Graphics graphics) {
		if ((isColorsAreStolen && !isFlashToOtherColorMode) || (!isColorsAreStolen && isFlashToOtherColorMode)) {
			int red = (rgb >> 16) & 255;
			int green = (rgb >> 8) & 255;
			int blue = rgb & 255;
			rgb = (((green + blue) >> 1) << 16) + (((blue + red) >> 1) << 8) + ((red + green) >> 1);
		}
		graphics.setColor(rgb);
	}

	/* renamed from: a */
	private static int drawStylizedNumber(int x, int y, int value, int anchor, boolean allowSingleDigit) {
		int newDrawnWidth;
		boolean isSingleDigit = value < 10 && allowSingleDigit;
		int drawnWidth = 0;
		int digitIndex = anchor == Graphics.LEFT ? 0 : 1; //EXTREMELY hackily coded, only works for 2 digit integers cause it skips the 1st digit for left alignment
		int remainder = value;
		int xOffset = x;
		while (digitIndex < 2) {
			if (remainder == 0) {
				if (digitIndex == 1) {
					GameRuntime.drawImageResAnchored(xOffset, y, NUMBER_FONT_IMAGE_IDS[0], Graphics.TOP | Graphics.RIGHT);
				}
				newDrawnWidth = drawnWidth + GameRuntime.getImageMapParam(NUMBER_FONT_IMAGE_IDS[0], ImageMap.PARAM_WIDTH);
			} else {
				newDrawnWidth = drawnWidth;
				while (remainder != 0) {
					short imageId = NUMBER_FONT_IMAGE_IDS[remainder % 10];
					if (digitIndex == 1) {
						GameRuntime.drawImageResAnchored(xOffset - newDrawnWidth, y, imageId, Graphics.TOP | Graphics.RIGHT);
					}
					remainder /= 10;
					newDrawnWidth += GameRuntime.getImageMapParam(imageId, ImageMap.PARAM_WIDTH);
				}
			}
			if (digitIndex == 0) {
				xOffset += newDrawnWidth;
				newDrawnWidth = 0;
				remainder = value;
			}
			digitIndex++;
			drawnWidth = newDrawnWidth;
		}
		if (!isSingleDigit) {
			return drawnWidth;
		}
		GameRuntime.drawImageResAnchored(xOffset - drawnWidth, y, NUMBER_FONT_IMAGE_IDS[0], Graphics.TOP | Graphics.RIGHT); //leading zero
		return GameRuntime.getImageMapParam(NUMBER_FONT_IMAGE_IDS[0], ImageMap.PARAM_WIDTH) + drawnWidth;
	}

	/* renamed from: a */
	private static void drawLevelSelectUI(int x, int y, int levelId, int bottomY, int i5) {
		int b = getLevelType(levelId);
		if (b == 0) {
			GameRuntime.drawImageRes(x, y, 8);
		} else if (b == 2) {
			GameRuntime.drawImageRes(x, y, 380);
		}
		GameRuntime.drawImageRes(x, y, LEVEL_COVER_ART_IMAGE_IDS[levelId]);
		String[] printfParams = new String[1];
		GameRuntime.setTextStyle(-3, 1);
		GameRuntime.setTextColor(0, 0);
		if (!isLevelUnlocked(levelId)) {
			//Level not unlocked
			GameRuntime.drawImageRes(x, y, 149);
			if (isBonusLevel(levelId)) {
				//Required eggs for unlock
				int bonusLevelRequirement = 0;
				for (int i = 0; i < BONUS_LEVEL_INFO.length; i += 2) {
					if (levelId == BONUS_LEVEL_INFO[i]) {
						bonusLevelRequirement = BONUS_LEVEL_INFO[i + 1];
					}
				}
				String unlockRequirementMsg = StringManager.getMessage(MessageID.NEED_COLLECT_COUNT, bonusLevelRequirement);
				int a2 = GameRuntime.getStrRenderWidth(-3, unlockRequirementMsg, 0, unlockRequirementMsg.length()) + 23 + 5;
				int i8 = (GameRuntime.currentWidth >> 1) - (a2 >> 1);
				int i9 = a2 + i8;
				if (isTextRightToLeft) {
					GameRuntime.drawImageResAnchored(i9, i5, 102, 24);
					GameRuntime.drawText(unlockRequirementMsg, 0, unlockRequirementMsg.length(), i8, i5, 20);
				} else {
					GameRuntime.drawImageResAnchored(i8, i5, 102, 20);
					GameRuntime.drawText(unlockRequirementMsg, 0, unlockRequirementMsg.length(), i8 + 23 + 5, i5, 20);
				}
			}
		} else {
			//Level stats
			int a3 = GameRuntime.getFontHeight(-3) + 1;
			int a4 = GameRuntime.getFontHeight(-3) + 23 + 3;
			String a5 = StringManager.getMessage(MessageID.SCORE, "9999");
			int a6 = GameRuntime.getStrRenderWidth(-3, a5, 0, a5.length());
			int myScore = getLevelLocalHighScore(levelId);
			if (!(myScore > 0)) {
				myScore = 0;
			}
			int i11 = ((GameRuntime.currentWidth >> 1) - (a6 >> 1)) - 6;
			int a7 = GameRuntime.getStrRenderWidth(-3, "00/00", 0, "00/00".length()) + 23 + 11;
			String a8 = StringManager.getMessage(MessageID.SCORE, myScore);
			if (isTextRightToLeft) {
				GameRuntime.drawText(a8, 0, a8.length(), i11 + a7 + 23, i5, 24);
			} else {
				GameRuntime.drawText(a8, 0, a8.length(), i11, i5, 20);
			}
			if (!isBonusLevel(levelId)) {
				printfParams[0] = getLevelEggCount(levelId) + "/30";
				String str = printfParams[0];
				if (isTextRightToLeft) {
					GameRuntime.drawImageResAnchored(i11 + a7, i5 + a3, 102, 20);
					GameRuntime.drawText(str, 0, str.length(), (i11 - 11) + a7, i5 + a3 + 11, 10);
				} else {
					GameRuntime.drawImageResAnchored(i11, i5 + a3, 102, 20);
					GameRuntime.drawText(str, 0, str.length(), i11 + 23 + 11, i5 + a3 + 11, 6);
				}
			}
			if (wasLevelBeaten(LevelID.GAME_CLEAR_LEVEL) && !isBonusLevel(levelId)) {
				int d = getCollectionChallengeRank(levelId);
				int i12 = d > -1 ? TROPHY_IMAGE_IDS[d] : d;
				int e = getTimerChallengeRank(levelId);
				int i13 = e > -1 ? TROPHY_IMAGE_IDS[e] : e;
				String timer = formatGameTimer(getLevelClearTime(levelId));
				if (isTextRightToLeft) {
					GameRuntime.drawImageResAnchored(i11 + a7, i5 + a4, 103, 20);
					GameRuntime.drawText(timer, 0, timer.length(), (i11 - 11) + a7, i5 + a4 + 11, 10);
				} else {
					GameRuntime.drawImageResAnchored(i11, i5 + a4, 103, 20);
					GameRuntime.drawText(timer, 0, timer.length(), i11 + 23 + 11, i5 + a4 + 11, 6);
				}
				int a9 = GameRuntime.getStrRenderWidth(-3, "00:00", 0, "00:00".length()) + 23 + 11;
				if (isTextRightToLeft) {
					if (i12 > -1) {
						GameRuntime.drawImageResAnchored(i11, i5 + a3, i12, 24);
					}
					if (i13 > -1) {
						GameRuntime.drawImageResAnchored(i11, i5 + a4, i13, 12);
					}
				} else {
					if (i12 > -1) {
						GameRuntime.drawImageResAnchored(i11 + a9 + 11, i5 + a3, i12, 20);
					}
					if (i13 > -1) {
						GameRuntime.drawImageResAnchored(a9 + i11 + 11, i5 + a4, i13, 20);
					}
				}
			}
		}
		GameRuntime.setTextStyle(-2, 3);
		GameRuntime.setTextColor(0, 0xFF7800);
		GameRuntime.setTextColor(1, 0);
		if (isBonusLevel(levelId)) {
			String numStr = StringManager.getMessage(MessageID.UI_CHAPTERNO_BONUS, getLevelChapterNumber(levelId));
			GameRuntime.drawText(numStr, 0, numStr.length(), x, bottomY - GameRuntime.getFontHeight(GameRuntime.getCurrentFont()), 33);
		} else {
			String numStr = StringManager.getMessage(MessageID.UI_CHAPTERNO_STD, getLevelChapterNumber(levelId));
			GameRuntime.drawText(numStr, 0, numStr.length(), x, bottomY - GameRuntime.getFontHeight(GameRuntime.getCurrentFont()), 33);
		}
		String levelName = StringManager.getMessage(LEVEL_NAME_MESSAGE_IDS[levelId]);
		GameRuntime.drawText(levelName, 0, levelName.length(), x, bottomY, Graphics.BOTTOM | Graphics.HCENTER);
	}

	/* renamed from: a */
	private static void drawBookFrame(int xpos, int ypos, Graphics graphics) {
		GameRuntime.drawImageRes(xpos, ypos, 331);
		int yparam = GameRuntime.getCompoundSpriteParamEx(331, 0);
		int xEnd = yparam >> 16;
		short yEnd = (short) (yparam & 0xFFFF);
		int xparam = GameRuntime.getCompoundSpriteParamEx(331, 1);
		int xStart = xparam >> 16;
		short yStart = (short) (xparam & 0xFFFF);
		graphics.setColor(0xFBF7E3);
		graphics.fillRect(xpos + xStart, ypos + yStart, xEnd - xStart, yEnd - yStart);
		graphics.setColor(0);
		graphics.fillRect((xpos + xStart) - 2, ypos + yStart, 4, yEnd - yStart);
	}

	/* renamed from: a */
	private static String getLevelChapterNumber(int levelId) {
		int countOfBonusChaptersBefore = 0;
		int bonusStructIdx = 0;
		while (bonusStructIdx < BONUS_LEVEL_INFO.length && levelId >= BONUS_LEVEL_INFO[bonusStructIdx]) {
			countOfBonusChaptersBefore++;
			bonusStructIdx += 2;
		}
		return isBonusLevel(levelId) ? String.valueOf(countOfBonusChaptersBefore) : String.valueOf((levelId + 1) - countOfBonusChaptersBefore);
	}

	/* renamed from: c */
	private static boolean isBonusLevel(int i) {
		for (int i2 = 0; i2 < BONUS_LEVEL_INFO.length; i2 += 2) {
			if (i == BONUS_LEVEL_INFO[i2]) {
				return true;
			}
		}
		return false;
	}

	/* renamed from: d */
	private static int getLevelMusicID() {
		switch (currentLevel) {
			case LevelID.SECRET_STALKWAY:
			case LevelID.TUNNEL_OF_TREASURES:
			case LevelID.FANTASTIC_FAIR:
				return ResourceID.AUDIO_BGM_LEVEL_BONUS_MID;
			case LevelID.BUMPY_CRACKS:
			case LevelID.TRAPPED_IN_MACHINE:
			case LevelID.FINAL_RIDE:
				return ResourceID.AUDIO_BGM_LEVEL_BOSS_MID;
		}
		switch (getLevelType(currentLevel)) {
			case 0:
				return ResourceID.AUDIO_BGM_LEVEL_ACT01_MID;
			case 1:
				return ResourceID.AUDIO_BGM_LEVEL_ACT02_MID;
			default:
				return ResourceID.AUDIO_BGM_LEVEL_ACT03_MID;
		}
	}

	/* renamed from: a */
	public static final void pushFieldMessage(int msgId) {
		if (fieldMessagePointer < 5) {
			fieldMessageQueue[fieldMessagePointer] = msgId;
			fieldMessagePointer++;
		}
	}

	/* renamed from: c */
	private void popFieldMessage() {
		if (!isFieldMessageShowing && fieldMessagePointer > 0) {
			setUI(34);
			isBlockingEvent = true;
			isFieldMessageShowing = true;
			for (int i = 0; i < 4; i++) {
				fieldMessageQueue[i] = fieldMessageQueue[i + 1];
			}
			fieldMessagePointer--;
		}
	}

	public static final void setPlayerState(int state) {
		EventObject.eventVars[0] = state;
	}

	public static final int getPlayerState() {
		return EventObject.eventVars[0];
	}

	/* renamed from: a */
	private static void updateLevelStats(int levelId, short eggCount, short clearTime, short score) {
		int saveDataOffset = levelId << 2;
		if (eggCount > levelSaveData[saveDataOffset]) {
			levelSaveData[saveDataOffset] = eggCount;
		}
		if (clearTime < levelSaveData[saveDataOffset + 1]) {
			levelSaveData[saveDataOffset + 1] = clearTime;
		}
		if (score > levelSaveData[saveDataOffset + 2]) {
			levelSaveData[saveDataOffset + 2] = score;
		}
		if (score > levelSaveData[saveDataOffset + 3]) {
			levelSaveData[saveDataOffset + 3] = score;
		}
	}

	/* renamed from: a */
	private static void drawTranslucentSoftkeyBar(Graphics grp) {
		int skbHeight = GameRuntime.getSoftkeyBarHeight();
		int width = GameRuntime.currentWidth;
		int height = GameRuntime.currentHeight - skbHeight;
		xluSoftkeyBarXs[0] = 0;
		xluSoftkeyBarYs[0] = height;
		xluSoftkeyBarXs[1] = 0 + width;
		xluSoftkeyBarYs[1] = height;
		xluSoftkeyBarXs[2] = 0 + width;
		xluSoftkeyBarYs[2] = height + skbHeight;
		xluSoftkeyBarXs[3] = 0;
		xluSoftkeyBarYs[3] = skbHeight + height;
		GraphicsUtils.fillPolygonARGB(grp, xluSoftkeyBarXs, 0, xluSoftkeyBarYs, 0, 4, 0x55000000, true);
	}

	/* renamed from: a */
	public static void drawSoftkeyUI(String str, int type, int xpos, int ypos, int flags) {
		if (type == 1) {
			GameRuntime.drawImageResAnchored(xpos, ypos, (flags | Graphics.TOP) == Graphics.TOP ? 151 : 150, flags);
		} else {
			GameRuntime.setTextStyle(-3, 3);
			GameRuntime.setTextColor(0, 0xFF7800);
			GameRuntime.setTextColor(1, 0);
			GameRuntime.drawText(str, 0, str.length(), xpos, ypos, flags);
		}
	}

	/* renamed from: a */
	private static void updateLevelStartSoftkeyByUnlock(UILayout ui) {
		if (isLevelUnlocked(selectedLevelId)) {
			ui.changeSoftkey(GameRuntime.SOFTKEY_CENTER, StringManager.getMessage(MessageID.UI_SELECT), 0);
		} else {
			ui.changeSoftkey(GameRuntime.SOFTKEY_CENTER, null, 0);
		}
	}

	private static void cycleLevelSelectLeft(UILayout layout, boolean updateSoftkeys) {
		if (selectedLevelId != 0) {
                        boolean fast = lastSelectedLevelId > selectedLevelId;
                        lastSelectedLevelId = selectedLevelId;
                        selectedLevelId--;
			if (bookAnimationTime == 650 || fast) {
				bookAnimationTime = 0;
			}
			targetBookAnimationTime = 650;
                        if (updateSoftkeys) {
                                updateLevelStartSoftkeyByUnlock(layout);
                        }
		}
	}

	/* renamed from: a */
	private static void cycleLevelSelectRight(UILayout layout, boolean updateSoftkeys) {
		if (selectedLevelId != 14) {
                        boolean fast = lastSelectedLevelId < selectedLevelId;
                        lastSelectedLevelId = selectedLevelId;
                        selectedLevelId++;
			if (bookAnimationTime == 0 || fast) {
				bookAnimationTime = 650;
			}
			targetBookAnimationTime = 0;
                        if (updateSoftkeys) {
                                updateLevelStartSoftkeyByUnlock(layout);
                        }
		}
	}

	/* renamed from: a */
	private static void clearUIBackground(Graphics graphics) {
		graphics.setColor(0x3F1A01);
		graphics.fillRect(0, 0, GameRuntime.currentWidth >> 1, GameRuntime.currentHeight);
		graphics.setColor(0x5E2601);
		graphics.fillRect(GameRuntime.currentWidth >> 1, 0, GameRuntime.currentWidth >> 1, GameRuntime.currentHeight);
		GameRuntime.drawImageRes(0, 0, 4);
		GameRuntime.drawImageRes(0, 0, 74);
		GameRuntime.drawImageRes(0, 0, 75);
		GameRuntime.drawImageRes(0, GameRuntime.currentHeight, 3);
	}

	private static int[] arraysCopyOf(int[] src, int newSize) {
		int[] newarr = new int[newSize];
		System.arraycopy(src, 0, newarr, 0, src.length);
		return newarr;
	}

	/*
	For high resolution parallaxes.
	 */
	private static void checkReallocParallax(int maxAllocSize) {
		if (parallaxImageIndices.length < maxAllocSize) {
			parallaxImageIndices = arraysCopyOf(parallaxImageIndices, maxAllocSize);
			parallaxXOffsets = arraysCopyOf(parallaxXOffsets, maxAllocSize);
			parallaxYOffsets = arraysCopyOf(parallaxYOffsets, maxAllocSize);
		}
	}

	/* renamed from: a */
	private static void drawBGParallax(short[] imageIDs, int moveSpeedNum, int moveSpeedDenom, int x, int xRange, int y, int yRange, int count, int stripeFillColor, Graphics graphics) {
		parallaxXOffsets[0] = 0;
		int firstYOffset = 0;
		if (yRange != 0) {
			firstYOffset = mRNG.nextInt() % yRange;
		}
		parallaxYOffsets[0] = firstYOffset + y;
		for (int i = 1; i < count + 1; i++) {
			int stepFromLast = 0;
			if (xRange != 0) {
				stepFromLast = Math.abs(mRNG.nextInt() % xRange);
			}
			parallaxXOffsets[i] = stepFromLast + parallaxXOffsets[i - 1] + x;
			int yOffset = 0;
			if (yRange != 0) {
				yOffset = mRNG.nextInt() % yRange;
			}
			parallaxYOffsets[i] = yOffset + y;
			parallaxImageIndices[i] = Math.abs(mRNG.nextInt() % imageIDs.length);
		}
		int parallaxGroupWidth = parallaxXOffsets[count];
		int baseX = parallaxGroupWidth - (((((((GameObject.cameraMatrix.translationX >> 16) + 33000) * GameObject.screenSpaceMatrix.m00) >> 16) * moveSpeedNum) / moveSpeedDenom) % parallaxGroupWidth);
		int baseY = GameRuntime.currentHeight + ((((((GameObject.cameraMatrix.translationY - f240F) >> 16) * GameObject.screenSpaceMatrix.m00) >> 16) * moveSpeedNum) / moveSpeedDenom);
		for (int i = 0; i < count; i++) {
			if (i < 2) {
				GameRuntime.drawImageRes(parallaxXOffsets[i] + baseX, parallaxYOffsets[i] + baseY, imageIDs[parallaxImageIndices[i]]);
			}
			if (i > 2) {
				GameRuntime.drawImageRes((parallaxXOffsets[i] + baseX) - (parallaxGroupWidth << 1), parallaxYOffsets[i] + baseY, imageIDs[parallaxImageIndices[i]]);
			}
			GameRuntime.drawImageRes((parallaxXOffsets[i] + baseX) - parallaxGroupWidth, parallaxYOffsets[i] + baseY, imageIDs[parallaxImageIndices[i]]);
		}
		if (stripeFillColor != -1) {
			setBGColor(stripeFillColor, graphics);
			graphics.fillRect(0, parallaxYOffsets[0] + baseY, GameRuntime.currentWidth, GameRuntime.currentHeight - (baseY + parallaxYOffsets[0]));
		}
	}

	/* renamed from: a */
	public static boolean drawUIGraphics(UILayout ui, int type, int xpos, int ypos, int width, int height) {
		GameRuntime.setBacklight(true);
		Graphics grp = GameRuntime.getGraphicsObj();
		int delta = GameRuntime.updateDelta * GameRuntime.getUpdatesPerDraw();
		if ((ui.uiID == GameScene.INFO_FIELD_MESSAGE
				|| ui.uiID == GameScene.MENU_PAUSE
				|| ui.uiID == GameScene.CONFIRM_RESTART_LEVEL
				|| ui.uiID == GameScene.CONFIRM_RETURN_LEVEL_SELECT
				|| ui.uiID == GameScene.CONFIRM_EXIT_LEVEL) && type == 1) {
			//Pause menu background
			int[] xpoints = GeometryObject.TEMP_QUAD_XS;
			int[] ypoints = GeometryObject.TEMP_QUAD_YS;
			xpoints[0] = xpos;
			ypoints[0] = ypos;
			xpoints[1] = xpos + width;
			ypoints[1] = ypos;
			xpoints[2] = xpos + width;
			ypoints[2] = ypos + height;
			xpoints[3] = xpos;
			ypoints[3] = ypos + height;
			GraphicsUtils.fillPolygonARGB(grp, xpoints, 0, ypoints, 0, 4, 0x55000000, true);
			GameRuntime.drawImageRes(xpos, ypos, 311);
			GameRuntime.drawImageRes(xpos + width, ypos, 312);
			GameRuntime.drawImageRes(xpos, ypos + height, 309);
			GameRuntime.drawImageRes(xpos + width, ypos + height, 310);
			drawTranslucentSoftkeyBar(grp);
			return false;
		} else if (ui.uiID == GameScene.MENU_LEVEL_SELECT) {
			if (type == 1) {
				clearUIBackground(grp);
				int screenCX = GameRuntime.currentWidth >> 1;
				int screenCY = GameRuntime.currentHeight >> 1;
				int b = ((short) GameRuntime.getCompoundSpriteParamEx(331, 2)) + screenCY;
				int b2 = ((short) GameRuntime.getCompoundSpriteParamEx(331, 3)) + screenCY;

				//since 2.0.25
				GameRuntime.setTextStyle(-3, 3);
				int sanityHeight = 10 + (GameRuntime.getFontHeight(GameRuntime.getCurrentFont()) * 3);
				if (sanityHeight > b) {
					b = sanityHeight + 2;
				}

				drawBookFrame(screenCX, screenCY, grp);
				if (selectedLevelId != 0) {
					GameRuntime.drawImageResAnchored(3, screenCY, 326, 6); //left arrow
				}
				if (selectedLevelId != LevelID.LEVEL_IDX_MAX - 1) {
					GameRuntime.drawImageResAnchored(GameRuntime.currentWidth - 3, screenCY, 2, 10); //right arrow
				}
				int topPageLevel = 0;
				int bottomPageLevel = 0;
				if (bookAnimationTime < targetBookAnimationTime) {
					bookAnimationTime += delta;
					if (bookAnimationTime > targetBookAnimationTime) {
						bookAnimationTime = targetBookAnimationTime;
					}
					topPageLevel = selectedLevelId;
					bottomPageLevel = lastSelectedLevelId;
				} else if (bookAnimationTime > targetBookAnimationTime) {
					bookAnimationTime -= delta;
					if (bookAnimationTime < targetBookAnimationTime) {
						bookAnimationTime = targetBookAnimationTime;
					}
					topPageLevel = lastSelectedLevelId;
					bottomPageLevel = selectedLevelId;
				}
				if (bookAnimationTime > 400 && bookAnimationTime < 650) {
					//grab page end
					drawLevelSelectUI(screenCX, screenCY, topPageLevel, b, b2);
					GameRuntime.drawAnimatedImageRes(screenCX, screenCY, 442, ((bookAnimationTime - 400) << 1) / 250);
				} else if (bookAnimationTime > 400 || bookAnimationTime <= 0) {
					//idle
					drawLevelSelectUI(screenCX, screenCY, selectedLevelId, b, b2);
				} else {
					int i12 = ((screenCX - 119) - 239) + 22;
					int i13 = (screenCX + 120) - 30;
					int i14 = (screenCX - 119) + 22;
					int pageSplitXStart = i12 + ((((i13 - 25) - i12) * bookAnimationTime) / 400);
					int pageSplitXEnd = (((i13 - i14) * bookAnimationTime) / 400) + i14;
					int i17 = (screenCY - 158) - 3;
					grp.setClip(0, 0, pageSplitXStart + 3, GameRuntime.currentHeight);
					drawLevelSelectUI(screenCX, screenCY, topPageLevel, b, b2);
					grp.setClip(pageSplitXEnd - 2, 0, (GameRuntime.currentWidth - pageSplitXEnd) + 2, GameRuntime.currentHeight);
					drawLevelSelectUI(screenCX, screenCY, bottomPageLevel, b, b2);
					grp.setClip(0, 0, GameRuntime.currentWidth, GameRuntime.currentHeight);
					GameRuntime.drawImageRes(pageSplitXStart, i17, 377);
					grp.setColor(0xEAE6CC);
					int i18 = ((pageSplitXEnd - pageSplitXStart) - 12) - 13;
					grp.fillRect(pageSplitXStart + 12, i17, i18, 295);
					grp.setColor(0);
					grp.fillRect(pageSplitXStart + 12, i17, i18, 1);
					grp.fillRect(pageSplitXStart + 12, ((i17 + 307) - 1) - 12, i18, 1);
					GameRuntime.drawImageRes(i18 + pageSplitXStart + 12, i17, 378);
					GameRuntime.drawImageRes(screenCX, screenCY, 376);
				}
				GameRuntime.setTextStyle(-3, 3);
				GameRuntime.setTextColor(0, 0xFF7800);
				GameRuntime.setTextColor(1, 0);
				GameRuntime.drawImageRes(9, 9, 102);
				String stringBuffer = getTotalEggCount() + "/450";
				GameRuntime.drawText(stringBuffer, 0, stringBuffer.length(), 41, 10, 20);
				drawTranslucentSoftkeyBar(grp);
			}
			return false;
                } else if (ui.uiID == GameScene.MENU_PAUSE
				|| ui.uiID == GameScene.CONFIRM_RESTART_LEVEL
				|| ui.uiID == GameScene.CONFIRM_RETURN_LEVEL_SELECT
				|| ui.uiID == GameScene.CONFIRM_EXIT_LEVEL
				|| ui.uiID == 15
				|| ui.uiID == GameScene.INFO_FIELD_MESSAGE
				|| type != 1) {
			if (type == 1) {
				drawTranslucentSoftkeyBar(grp);
			}
			if (type == 4 || type == 2 || type == 9) {
				return false;
			}
			if (ui.uiID == GameScene.MENU_PAUSE
					|| ui.uiID == GameScene.CONFIRM_RESTART_LEVEL
					|| ui.uiID == GameScene.CONFIRM_RETURN_LEVEL_SELECT
					|| ui.uiID == GameScene.CONFIRM_EXIT_LEVEL
					|| ui.uiID == GameScene.INFO_FIELD_MESSAGE
					|| type != 10) {
				return true;
			}
			grp.setClip(0, 0, GameRuntime.currentWidth, GameRuntime.currentHeight);
			int selArrowDisp = (SIN_COS_TABLE[(int) ((System.currentTimeMillis() >> 1) % 360)] * 5) / 360;
			GameRuntime.drawImageResAnchored(
					((xpos - 5) - 4) + selArrowDisp,
					(height >> 1) + ypos,
					2,
					Graphics.RIGHT | Graphics.VCENTER
			); //selection arrow L
			GameRuntime.drawImageResAnchored(
					(((xpos + width) + 5) + 4) - selArrowDisp,
					(height >> 1) + ypos,
					326,
					Graphics.LEFT | Graphics.VCENTER
			); //selection arrow R
			return false;
		} else {
			clearUIBackground(grp);
			int i20 = GameRuntime.currentWidth >> 1;
			int i21 = GameRuntime.currentHeight >> 1;
			if (ui.uiID == GameScene.MENU_TITLE) {
				int b3 = GameRuntime.getCompoundSpriteParamEx(332, 0);
				int i22 = b3 >> 16;
				short s = (short) b3;
				int b4 = GameRuntime.getCompoundSpriteParamEx(332, 1);
				int i23 = b4 >> 16;
				int i24 = i20 - 117;
				int i25 = i21 - 157;
				int i26 = (i20 + i23) - i24;
				grp.setColor(0x644330);
				grp.fillRect(i20 + i22, i21 + s, i23 - i22, ((short) b4) - s);
				GameRuntime.drawImageRes(i20, i21, 332);
				grp.setColor(0x55270F);
				grp.drawRect(i24 + 2, i25 + 2, i26 - 4, 296);
				grp.drawRect(i24 + 3, i25 + 3, i26 - 6, 294);
				grp.setColor(0x371909);
				grp.drawRect(i24, i25, i26, 300);
				grp.drawRect(i24 + 1, i25 + 1, i26 - 2, 298);
				GameRuntime.drawImageResTransformed(((i20 + i26) - 117) + 2, (i21 - 157) - 2, 12, Graphics.TOP | Graphics.RIGHT, Sprite.TRANS_ROT270);
				GameRuntime.drawImageResAnchored(((i20 + i26) - 117) + 2, ((i21 + 300) - 157) + 2, 12, Graphics.BOTTOM | Graphics.RIGHT);
				GameRuntime.drawImageResAnchored(i24 - 5, i21 - 75, 15, Graphics.LEFT | Graphics.VCENTER);
				GameRuntime.drawImageResAnchored(i24 - 5, i21 + 75, 15, Graphics.LEFT | Graphics.VCENTER);
			} else {
				drawBookFrame(i20, i21, grp);
			}
			drawTranslucentSoftkeyBar(grp);
			return false;
		}
	}

	/* renamed from: b */
	public static int getSoftkeyBarSize() {
		return GameRuntime.getFontHeight(-3) + 4;
	}

	/* renamed from: b */
	public static int getLevelType(int levelId) {
		int result = 2;
		if (levelId <= LevelID.TUNNEL_OF_TREASURES) {
			result = 1;
		}
		if (levelId <= LevelID.SECRET_STALKWAY) {
			return 0;
		}
		return result;
	}

	/* renamed from: b */
	private static String formatGameTimer(int seconds) {
		int mm = seconds / 60;
		int ss = seconds % 60;
		return ss < 10 ? mm + ":0" + ss : mm + ":" + ss;
	}

	/* renamed from: b */
	private void setIngameHID() {
		this.drawUI = null;
		GameRuntime.resetSoftkeys();
		GameRuntime.setSoftkey(GameRuntime.SOFTKEY_RIGHT, "", 1);
		GameRuntime.initHID(2);
		GameRuntime.resetHID();
	}

	/* renamed from: b */
	private void drawLoadingBar(Graphics graphics) {
		if (hasLoadingProgressBar) {
			graphics.setColor(0x703005);
			graphics.fillRect(0, 0, GameRuntime.currentWidth, GameRuntime.currentHeight);
			graphics.setColor(0);
			graphics.fillRect((GameRuntime.currentWidth - 60) / 2, (GameRuntime.currentHeight - 10) / 2, 60, 10);
			graphics.setColor(0x471D00);
			graphics.fillRect(((GameRuntime.currentWidth - 60) / 2) + 1, ((GameRuntime.currentHeight - 10) / 2) + 1, 58, 8);
			graphics.setColor(0xAEE13C);
			graphics.fillRect(((GameRuntime.currentWidth - 60) / 2) + 1, ((GameRuntime.currentHeight - 10) / 2) + 1, ((this.loadingProgressBar * 60) / 20) - 2, 8);
			this.loadingProgressBar++;
			if (this.loadingProgressBar > 20) {
				this.loadingProgressBar = 0;
				return;
			}
			return;
		}
		graphics.setColor(0xFFFFFF);
		graphics.fillRect(0, 0, GameRuntime.currentWidth, GameRuntime.currentHeight);
	}

	/* renamed from: a */
	private static boolean checkSuperBounceUnlocked() {
		for (int i = 0; i < 15; i++) {
			if (getLevelEggCount(i) < LEVEL_EGG_TROPHY_REQUIREMENTS[i * 3] || getLevelClearTime(i) > LEVEL_TIMER_TROPHY_REQUIREMENTS[i * 3]) {
				return false;
			}
		}
		return true;
	}

	/* renamed from: a */
	public static boolean wasLevelBeaten(int levelId) {
		int clearTime = getLevelClearTime(levelId);
		return clearTime > 0 && clearTime < 9999;
	}

	/* renamed from: b */
	private static boolean isLevelUnlocked(int levelId) {
		return getLevelClearTime(levelId) > 0;
	}

	/* renamed from: e */
	private static void unlockLevel(int levelId) {
		if (getLevelClearTime(levelId) == 0) {
			setLevelClearTime(levelId, 9999);
		}
	}

	/* renamed from: f */
	private static void debugLevelUnlock(int levelId) {
		int status = getLevelClearTime(levelId);
		if (status == 0 || status == 9999) {
			setLevelClearTime(levelId, 300);
		}
	}

	/* renamed from: c */
	private static int getTotalEggCount() {
		int totalEggs = 0;
		for (int levelIdx = 0; levelIdx < LevelID.LEVEL_IDX_MAX; levelIdx++) {
			totalEggs += getLevelEggCount(levelIdx);
		}
		return totalEggs;
	}

	private static void setLevelEggCount(int levelId, int eggCount) {
		levelSaveData[levelId << 2] = (short) eggCount;
	}

	private static void setLevelClearTime(int levelId, int clearTime) {
		levelSaveData[(levelId << 2) + 1] = (short) clearTime;
	}

	private static int getLevelEggCount(int levelId) {
		return levelSaveData[levelId << 2];
	}

	private static int getLevelClearTime(int levelId) {
		return levelSaveData[(levelId << 2) + 1];
	}

	private static short getLevelLocalHighScore(int levelId) {
		return levelSaveData[(levelId << 2) + 2];
	}

	private static short getLevelGlobalHighScore(int levelId) {
		return levelSaveData[(levelId << 2) + 3];
	}

	/* renamed from: d */
	private static int getCollectionChallengeRank(int levelId) {
		int collectedEggs = getLevelEggCount(levelId);
		if (collectedEggs >= LEVEL_EGG_TROPHY_REQUIREMENTS[levelId * 3]) {
			return 2;
		}
		if (collectedEggs >= LEVEL_EGG_TROPHY_REQUIREMENTS[(levelId * 3) + 1]) {
			return 1;
		}
		if (collectedEggs >= LEVEL_EGG_TROPHY_REQUIREMENTS[(levelId * 3) + 2]) {
			return 0;
		}
		return -1;
	}

	/* renamed from: e */
	private static int getTimerChallengeRank(int levelId) {
		int clearTime = getLevelClearTime(levelId);
		if (clearTime <= LEVEL_TIMER_TROPHY_REQUIREMENTS[levelId * 3]) {
			return 2;
		}
		if (clearTime <= LEVEL_TIMER_TROPHY_REQUIREMENTS[(levelId * 3) + 1]) {
			return 1;
		}
		if (clearTime <= LEVEL_TIMER_TROPHY_REQUIREMENTS[(levelId * 3) + 2]) {
			return 0;
		}
		return -1;
	}

	/* renamed from: a */
	public static int getUnlockedFormeCount() {
		int i = 0;
		if (wasLevelBeaten(FORME_UNLOCK_LEVELS[0]) || (currentLevel == FORME_UNLOCK_LEVELS[0] && EventObject.eventVars[2] > 0)) {
			i = 1;
		}
		if (wasLevelBeaten(FORME_UNLOCK_LEVELS[1]) || (currentLevel == FORME_UNLOCK_LEVELS[1] && EventObject.eventVars[2] > 0)) {
			return 2;
		}
		return i;
	}

	private static void deserializeSaveData(byte[] save) {
		for (int saveDataInIdx = 0, saveDataOutIdx = 0; saveDataOutIdx < levelSaveData.length; saveDataOutIdx++, saveDataInIdx += 2) {
			levelSaveData[saveDataOutIdx] = GameObject.readShort(save, saveDataInIdx);
		}
	}

	private static void clearSaveData() {
		for (int i = 0; i < levelSaveData.length; i++) {
			levelSaveData[i] = 0;
		}
	}

	/* renamed from: d */
	private static void serializeSaveData() {
		byte[] savedata = new byte[(levelSaveData.length << 1)];
		for (int i = 0; i < levelSaveData.length; i++) {
			savedata[i << 1] = (byte) (levelSaveData[i] >> 8);
			savedata[(i << 1) + 1] = (byte) levelSaveData[i];
		}
		GameRuntime.saveToRecordStore("game", savedata);
	}

	/* renamed from: g */
	private static void initStolenColorData() { //inlined in 2.0.25
		stolenColorsAnimationCountdown = 0;
		stolenColorsFlashCountdown = 0;
		isFlashToOtherColorMode = false;
		isColorsAreStolen = false;
		try {
			parallaxImagesRegColors = new Image[ALL_PARALLAX_IMAGE_IDS.length];
			parallaxImagesStolenColors = new Image[ALL_PARALLAX_IMAGE_IDS.length];
			for (int i = 0; i < ALL_PARALLAX_IMAGE_IDS.length; i++) {
				if (GameRuntime.getImageResource(ALL_PARALLAX_IMAGE_IDS[i]) != null) {
					parallaxImagesRegColors[i] = GameRuntime.getImageResource(ALL_PARALLAX_IMAGE_IDS[i]);
					parallaxImagesStolenColors[i] = Image.createImage(parallaxImagesRegColors[i]);
					int[] stolenRGB = new int[(parallaxImagesStolenColors[i].getWidth() * parallaxImagesStolenColors[i].getHeight())];
					parallaxImagesStolenColors[i].getRGB(
							stolenRGB,
							0,
							parallaxImagesStolenColors[i].getWidth(),
							0,
							0,
							parallaxImagesStolenColors[i].getWidth(),
							parallaxImagesStolenColors[i].getHeight()
					);
					for (int rgbIdx = 0; rgbIdx < stolenRGB.length; rgbIdx++) {
						int r = (stolenRGB[rgbIdx] >> 16) & 255;
						int g = (stolenRGB[rgbIdx] >> 8) & 255;
						int b = stolenRGB[rgbIdx] & 255;
						stolenRGB[rgbIdx] = ((stolenRGB[rgbIdx] >>> 24) << 24) + (((g + b) >> 1) << 16) + (((b + r) >> 1) << 8) + ((r + g) >> 1);
					}
					parallaxImagesStolenColors[i] = Image.createRGBImage(stolenRGB, parallaxImagesStolenColors[i].getWidth(), parallaxImagesStolenColors[i].getHeight(), true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* renamed from: h */
	private static void resetParallaxStolenColors() {
		for (int i = 0; i < ALL_PARALLAX_IMAGE_IDS.length; i++) {
			if (!(GameRuntime.getImageResource(ALL_PARALLAX_IMAGE_IDS[i]) == null || parallaxImagesRegColors[i] == null)) {
				GameRuntime.replaceImageResource(ALL_PARALLAX_IMAGE_IDS[i], parallaxImagesRegColors[i]);
			}
		}
	}

	/* renamed from: c */
	public static int getStolenColorIfApplicable(int argb) {
		//10 - false
		//01 - false
		//11 - true
		//00 - true
		if ((isColorsAreStolen && isFlashToOtherColorMode) || (!isColorsAreStolen && !isFlashToOtherColorMode)) {
			return argb;
		}
		int i2 = (argb >> 16) & 255;
		int i3 = (argb >> 8) & 255;
		int i4 = argb & 255;
		return ((argb >>> 24) << 24) + (((i3 + i4) >> 1) << 16) + (((i4 + i2) >> 1) << 8) + ((i2 + i3) >> 1);
	}

	/* renamed from: e */
	private void updateLoadingScreen() {
		if (this.curSplashId + 1 >= SPLASH_SCREEN_LAYOUT_RESIDS.length) {
			GameRuntime.startLoadScene(GameScene.CALL_TITLE_MENU);
		} else if (GameRuntime.isResourceLoadDone(SPLASH_SCREEN_LAYOUT_RESIDS[this.curSplashId + 1])) {
			hasLoadingProgressBar = true;
			this.curSplashId++;
			this.splashScreenStartTime = System.currentTimeMillis();
			if (this.curSplashId - 1 > -1) {
				GameRuntime.unloadResource(SPLASH_SCREEN_LAYOUT_RESIDS[this.curSplashId - 1]);
			}
		}
	}

	/* renamed from: j */
	private void levelEnded() {
		resetParallaxStolenColors();
		this.isLevelActive = false;
		GameRuntime.startLoadScene(GameScene.EXIT_LEVEL);
		exitLevelReturnScene = GameScene.INFO_CHAPTER_COMPLETE;
		setPlayerState(PLAYER_STATE_PLAY);
	}

	/* renamed from: f */
	private static void unloadLevel() {
		ballFramebuffer = null;
		ballGraphics = null;
		ballFramebufferRGB = null;
		spriteFB = null;
		spriteOffscreenGraphics = null;
		spriteFBRGB = null;
		GeometryObject.TEMP_QUAD_XS = null;
		GeometryObject.TEMP_QUAD_YS = null;
		rootLevelObj = null;
		GameObject.cameraTarget = null;
		bounceObj = null;
		events = null;
		currentCannon = null;
		parallaxImagesRegColors = null;
		parallaxImagesStolenColors = null;
		GameRuntime.unloadResource(ResourceID.GRAPHICS_BALLHIGHLIGHT_RES);
		GameRuntime.unloadResource(ResourceID.GRAPHICS_BALLPARTS_RES);
		GameRuntime.unloadResource(ResourceID.GRAPHICS_OBJDOOR_RES);
		GameRuntime.unloadResource(ResourceID.GRAPHICS_OBJLEVER_RES);
		GameRuntime.unloadResource(ResourceID.GRAPHICS_OBJSIGNBOARD_RES);
		GameRuntime.unloadResource(ResourceID.GRAPHICS_OBJFRIEND_RES);
		GameRuntime.unloadResource(ResourceID.GRAPHICS_OBJSTONEWALL_RES);
		GameRuntime.unloadResource(ResourceID.GRAPHICS_UIPAUSEMENU_RES);
		GameRuntime.unloadResource(ResourceID.GRAPHICS_ENEMY00CANDLE_RES);
		GameRuntime.unloadResource(ResourceID.GRAPHICS_ENEMY02MOLE_RES);
		GameRuntime.unloadResource(ResourceID.GRAPHICS_OBJHYPNOTOID_RES);
		GameRuntime.unloadResource(ResourceID.GRAPHICS_OBJSPIKE_RES);
		GameRuntime.unloadResource(ResourceID.GRAPHICS_OBJEGG_RES);
		GameRuntime.unloadResource(ResourceID.GRAPHICS_PARTICLESPLASH_RES);
		GameRuntime.unloadResource(ResourceID.GRAPHICS_PARTICLECOMMON_RES);
		GameRuntime.unloadResource(ResourceID.GRAPHICS_BALLBUMPYCRACKS_RES);
		switch (getLevelType(currentLevel)) {
			case 0:
				GameRuntime.unloadResource(ResourceID.GRAPHICS_LEVELACT01_RES);
				GameRuntime.unloadResource(ResourceID.GRAPHICS_OBJCOLORMACHINE_RES);
				GameRuntime.unloadResource(ResourceID.GRAPHICS_OBJCOLORMACHINEBROKEN_RES);
				break;
			case 1:
				GameRuntime.unloadResource(ResourceID.GRAPHICS_LEVELACT02_RES);
				GameRuntime.unloadResource(ResourceID.GRAPHICS_OBJCOLORMACHINE_RES);
				GameRuntime.unloadResource(ResourceID.GRAPHICS_OBJCOLORMACHINEBROKEN_RES);
				break;
			case 2:
				GameRuntime.unloadResource(ResourceID.GRAPHICS_LEVELACT03_RES);
				GameRuntime.unloadResource(ResourceID.GRAPHICS_OBJCANNON_RES);
				//bugfix: this resource is not unloaded in the original game, causing a resource leak
				GameRuntime.unloadResource(ResourceID.GRAPHICS_OBJCOLORMACHINE_RES);
				break;
			default:
				break;
		}
		GameRuntime.loadResource(ResourceID.GRAPHICS_UIMAINMENU_RES);
		GameRuntime.loadResource(ResourceID.GRAPHICS_UILEVELSELECT_RES);
	}

	/* renamed from: g */
	private void setUI(int uiID) {
		System.out.println("Bounce setUI " + uiID);
		this.ui.clear();
		this.ui.disableSoftkey(0);
		this.ui.disableSoftkey(1);
		this.ui.layoutAttributes = null;
		this.ui.elemDefaultAttributes = null;
		this.ui.uiID = uiID;
		this.ui.setElemDefaultAttribute(UIElement.FONT, -2);
		this.ui.setAttribute(UILayout.FONT, -2);
		switch (uiID) {
			case GameScene.MENU_HIGH_SCORES: //high scores list
				this.ui.loadFromResource(36);
				this.ui.setElemDefaultAttribute(UIElement.FONT, -3);
				this.ui.setAttribute(UILayout.FONT, -2);
				this.ui.setAttribute(UILayout.MARGIN_LEFT, 2);
				this.ui.setAttribute(UILayout.MARGIN_RIGHT, 2);
				this.ui.setAttribute(UILayout.TITLE_PADDING_TOP, LAYOUT_DEFAULT_TITLE_PADDING_TOP);
				this.ui.setAttribute(UILayout.TITLE_PADDING_BOTTOM, LAYOUT_DEFAULT_TITLE_PADDING_BOTTOM);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, GameRuntime.currentHeight - LAYOUT_DEFAULT_VERTICAL_MARGIN);
				this.ui.setAttribute(UILayout.FIXED_WIDTH, (GameRuntime.currentWidth - (LAYOUT_DEFAULT_HORIZONTAL_MARGIN << 1)) + 4);
				this.ui.setAttribute(UILayout.OFFSET_LEFT, LAYOUT_DEFAULT_HORIZONTAL_MARGIN - 2);

				//HD
				this.ui.setAttribute(UILayout.TITLE_PADDING_TOP, 4);
				this.ui.setAttribute(UILayout.FIXED_WIDTH, (240 - (LAYOUT_DEFAULT_HORIZONTAL_MARGIN << 1)) + 4);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, 320 - LAYOUT_DEFAULT_VERTICAL_MARGIN - 36);
				this.ui.setAttribute(UILayout.ANCHOR_CENTER, UILayout.ANCHOR_CENTER_BIT);

				this.ui.setElemDefaultAttribute(3, 0);
				this.ui.setElemDefaultAttribute(2, 32);
				this.ui.setAttribute(UILayout.BLOCK_INCREMENT, GameRuntime.getFontHeight(-3) << 1);
				this.ui.setTitle(StringManager.getMessage(MessageID.UI_HIGH_SCORES), -1, 1);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_RIGHT, StringManager.getMessage(MessageID.UI_BACK), 0, GameScene.MENU_TITLE, true);
				boolean hasAnyHighScore = false;
				for (int levelIdx = 0; levelIdx < LevelID.LEVEL_IDX_MAX; levelIdx++) {
					if (getLevelGlobalHighScore(levelIdx) > 0) {
						hasAnyHighScore = true;
					}
				}
				if (hasAnyHighScore) {
					for (int levelIdx = 0; levelIdx < LevelID.LEVEL_IDX_MAX; levelIdx++) {
						int highScore = Math.max(0, getLevelGlobalHighScore(levelIdx));
						String chapterNoStr;
						if (isBonusLevel(levelIdx)) {
							chapterNoStr = StringManager.getMessage(MessageID.UI_CHAPTERNO_BONUS, getLevelChapterNumber(levelIdx));
						} else {
							chapterNoStr = StringManager.getMessage(MessageID.UI_CHAPTERNO_STD, getLevelChapterNumber(levelIdx));
						}
						int separatorImageId = 89;
						if (levelIdx == 0) {
							separatorImageId = -1;
						}
						this.ui.addElement(new UIElement(chapterNoStr + "\n" + highScore, separatorImageId, this.ui, -1));
					}
					break;
				} else {
					this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.EMPTY), -1, this.ui, -1));
					break;
				}
			case GameScene.MENU_TITLE: //main menu
				this.ui.loadFromResource(37);
				this.ui.setElemDefaultAttribute(UIElement.FONT, -2); //font
				this.ui.setAttribute(UILayout.FONT, -2);
				this.ui.setElemDefaultAttribute(UIElement.AUTO_WIDTH, UIElement.AUTO_WIDTH_BIT);
				this.ui.setAttribute(UILayout.TITLE_PADDING_TOP, LAYOUT_MAIN_MENU_TITLE_PADDING);
				this.ui.setAttribute(UILayout.SCROLL_WRAPAROUND, UILayout.SCROLL_WRAPAROUND_BIT);

				//HD
				this.ui.setAttribute(UILayout.MARGIN_TOP, 14);
				this.ui.setAttribute(UILayout.ANCHOR_CENTER, UILayout.ANCHOR_CENTER_BIT);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, 320);

				this.ui.setElemDefaultAttribute(UIElement.FONT_TEXT_COLOR_SELECTED, 0xFF7800);
				this.ui.setTitle("", -1, 1);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_CENTER, StringManager.getMessage(MessageID.UI_SELECT), 0, -2, true);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_RIGHT, StringManager.getMessage(MessageID.UI_LEAVE), 0, GameScene.QUIT_GAME, false);
				if (isLevelUnlocked(1)) {
					this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.UI_CONTINUE), -1, this.ui, GameScene.MENU_LEVEL_SELECT));
					this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.UI_NEW_GAME), -1, this.ui, GameScene.MENU_NEW_GAME));
				} else {
					this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.UI_NEW_GAME), -1, this.ui, GameScene.MENU_LEVEL_SELECT));
					selectedLevelId = 0;
				}
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.UI_HIGH_SCORES), -1, this.ui, GameScene.MENU_HIGH_SCORES));
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.UI_GUIDE), -1, this.ui, GameScene.MENU_GUIDE));
				if (moreGamesStatus) { //since 2.0.25
					if (MessageID.UI_MORE_GAMES > 0) { //for BounceWin32 manifest
						this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.UI_MORE_GAMES), -1, this.ui, GameScene.OPEN_MORE_GAMES_URL));
					}
				}
				this.ui.setSelectedOption(lastMenuOption);
				break;
			case GameScene.MENU_LEVEL_SELECT: //level select
				this.ui.setSoftkey(GameRuntime.SOFTKEY_CENTER, StringManager.getMessage(MessageID.UI_SELECT), 0, GameScene.ENTER_LEVEL, true);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_RIGHT, StringManager.getMessage(MessageID.UI_BACK), 0, GameScene.MENU_TITLE, true);
				updateLevelStartSoftkeyByUnlock(this.ui);
				break;
			case GameScene.MENU_NEW_GAME: //start new game
				this.ui.loadFromResource(37);
				this.ui.setElemDefaultAttribute(UIElement.FONT, -3);
				this.ui.setAttribute(UILayout.FONT, -2);
				/*this.ui.setAttribute(UILayout.FIXED_WIDTH, (GameRuntime.currentWidth - (LAYOUT_DEFAULT_HORIZONTAL_MARGIN << 1)) + 4);
				this.ui.setAttribute(UILayout.OFFSET_LEFT, 18);*/ //added in 2.0.25, removed for HD
				this.ui.setAttribute(UILayout.TITLE_PADDING_TOP, LAYOUT_DEFAULT_TITLE_PADDING_TOP);
				this.ui.setAttribute(UILayout.TITLE_PADDING_BOTTOM, LAYOUT_DEFAULT_TITLE_PADDING_BOTTOM);

				//HD
				this.ui.setAttribute(UILayout.TITLE_PADDING_TOP, 0);
				this.ui.setAttribute(UILayout.MARGIN_BOTTOM, 180);
				this.ui.setAttribute(UILayout.ANCHOR_CENTER, UILayout.ANCHOR_CENTER_BIT);
				this.ui.setAttribute(UILayout.PACKED_HEIGHT, UILayout.PACKED_HEIGHT_BIT);

				this.ui.setTitle(StringManager.getMessage(MessageID.DIALOG_NEW_GAME), -1, 1);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_CENTER, StringManager.getMessage(MessageID.UI_YES), 0, GameScene.START_NEW_GAME, false);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_RIGHT, StringManager.getMessage(MessageID.UI_NO), 0, GameScene.MENU_TITLE, true);
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.GAME_PROGRESS_WILL_BE_LOST), -1, this.ui, -1));
				break;
			case GameScene.MENU_GUIDE: //guide
				this.ui.loadFromResource(36);
				this.ui.setElemDefaultAttribute(UIElement.FONT, -3);
				this.ui.setAttribute(UILayout.FONT, -2);
				this.ui.setAttribute(UILayout.MARGIN_LEFT, 2);
				this.ui.setAttribute(UILayout.MARGIN_RIGHT, 2);
				this.ui.setAttribute(UILayout.TITLE_PADDING_TOP, LAYOUT_DEFAULT_TITLE_PADDING_TOP);
				this.ui.setAttribute(UILayout.TITLE_PADDING_BOTTOM, LAYOUT_DEFAULT_TITLE_PADDING_BOTTOM);
				this.ui.setAttribute(UILayout.FIXED_WIDTH, (GameRuntime.currentWidth - (LAYOUT_DEFAULT_HORIZONTAL_MARGIN << 1)) + 4);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, GameRuntime.currentHeight - LAYOUT_DEFAULT_VERTICAL_MARGIN);

				//HD
				this.ui.setAttribute(UILayout.TITLE_PADDING_TOP, 4);
				this.ui.setAttribute(UILayout.FIXED_WIDTH, (240 - (LAYOUT_DEFAULT_HORIZONTAL_MARGIN << 1)) + 4);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, 320 - LAYOUT_DEFAULT_VERTICAL_MARGIN - 36);
				this.ui.setAttribute(UILayout.ANCHOR_CENTER, UILayout.ANCHOR_CENTER_BIT);

				this.ui.setAttribute(UILayout.OFFSET_LEFT, LAYOUT_DEFAULT_HORIZONTAL_MARGIN - 2);
				this.ui.setElemDefaultAttribute(3, 0);
				this.ui.setElemDefaultAttribute(2, 32);
				this.ui.setAttribute(UILayout.BLOCK_INCREMENT, GameRuntime.getFontHeight(-3) << 1);
				this.ui.setTitle(StringManager.getMessage(MessageID.UI_GUIDE), -1, 1);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_RIGHT, StringManager.getMessage(MessageID.UI_BACK), 0, GameScene.MENU_TITLE, true);
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.GUIDE_TEXT_1), -1, this.ui, -1));
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.GUIDE_TEXT_2), 102, this.ui, -1));
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.GUIDE_TEXT_3), -1, this.ui, -1));
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.GUIDE_TEXT_4), 371, this.ui, -1));
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.GUIDE_TEXT_5), 372, this.ui, -1));
				break;
			case GameScene.CONFIRM_QUIT_GAME: //quit game
				this.ui.setTitle(StringManager.getMessage(MessageID.DIALOG_QUIT_GAME), -1, 1);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_CENTER, StringManager.getMessage(MessageID.UI_YES), 0, GameScene.QUIT_GAME, false);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_RIGHT, StringManager.getMessage(MessageID.UI_NO), 0, GameScene.MENU_TITLE, true);
				break;
			case GameScene.MENU_PAUSE: //pause menu
				this.ui.loadFromResource(37);
				this.ui.setElemDefaultAttribute(UIElement.FONT, -3);
				this.ui.setAttribute(UILayout.FONT, -2);
				this.ui.setElemDefaultAttribute(UIElement.AUTO_WIDTH, 256);
				this.ui.setAttribute(UILayout.MARGIN_LEFT, 2);
				this.ui.setAttribute(UILayout.MARGIN_RIGHT, 2);
				this.ui.setAttribute(UILayout.FIXED_WIDTH, (GameRuntime.currentWidth - LAYOUT_DEFAULT_HORIZONTAL_MARGIN_INGAME) + 4);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, GameRuntime.currentWidth - LAYOUT_DEFAULT_HORIZONTAL_MARGIN_INGAME);
				this.ui.setAttribute(UILayout.ANCHOR_CENTER, UILayout.ANCHOR_CENTER_BIT);
				this.ui.setAttribute(UILayout.PACKED_HEIGHT, UILayout.PACKED_HEIGHT_BIT);
				this.ui.setAttribute(UILayout.SCROLL_WRAPAROUND, UILayout.SCROLL_WRAPAROUND_BIT);
				this.ui.setElemDefaultAttribute(UIElement.FONT_TEXT_COLOR_SELECTED, 0xFF7800);
				this.ui.setAttribute(UILayout.SOFTKEY_BAR, 0);
				this.ui.setTitle(StringManager.getMessage(MessageID.DIALOG_PAUSE_MENU), -1, 1);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_CENTER, StringManager.getMessage(MessageID.UI_SELECT), 0, -2, true);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_RIGHT, StringManager.getMessage(MessageID.UI_QUIT), 0, GameScene.CONFIRM_EXIT_LEVEL, true);
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.UI_CONTINUE_LEVEL), -1, this.ui, GameScene.UNPAUSE_LEVEL));
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.UI_RESTART_LEVEL), -1, this.ui, GameScene.CONFIRM_RESTART_LEVEL));
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.UI_RETURN_LEVEL_SELECT), -1, this.ui, GameScene.CONFIRM_RETURN_LEVEL_SELECT));
				this.ui.setSelectedOption(lastMenuOption);
				break;
			case GameScene.CONFIRM_RESTART_LEVEL: //confirm restart level
				this.ui.loadFromResource(37);
				this.ui.setElemDefaultAttribute(UIElement.FONT, -3);
				this.ui.setAttribute(UILayout.FONT, -2);
				this.ui.setElemDefaultAttribute(UIElement.AUTO_WIDTH, 256);
				this.ui.setAttribute(UILayout.MARGIN_LEFT, 2);
				this.ui.setAttribute(UILayout.MARGIN_RIGHT, 2);
				this.ui.setAttribute(UILayout.FIXED_WIDTH, (GameRuntime.currentWidth - LAYOUT_DEFAULT_HORIZONTAL_MARGIN_INGAME) + 4);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, GameRuntime.currentHeight - LAYOUT_DEFAULT_HORIZONTAL_MARGIN_INGAME);
				this.ui.setAttribute(UILayout.ANCHOR_CENTER, UILayout.ANCHOR_CENTER_BIT);
				this.ui.setAttribute(UILayout.PACKED_HEIGHT, UILayout.PACKED_HEIGHT_BIT);
				this.ui.setAttribute(UILayout.SOFTKEY_BAR, 0);
				this.ui.setTitle(StringManager.getMessage(MessageID.DIALOG_RESTART_LEVEL), -1, 1);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_CENTER, StringManager.getMessage(MessageID.UI_YES), 0, GameScene.RESTART_LEVEL, false);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_RIGHT, StringManager.getMessage(MessageID.UI_NO), 0, GameScene.MENU_PAUSE, true);
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.LEVEL_PROGRESS_WILL_BE_LOST), -1, this.ui, -1));
				break;
			case GameScene.CONFIRM_RETURN_LEVEL_SELECT: //confirm return to level select
				this.ui.loadFromResource(37);
				this.ui.setElemDefaultAttribute(UIElement.FONT, -3);
				this.ui.setAttribute(UILayout.FONT, -2);
				exitLevelReturnScene = GameScene.MENU_LEVEL_SELECT;
				this.ui.setElemDefaultAttribute(UIElement.AUTO_WIDTH, UIElement.AUTO_WIDTH_BIT);
				this.ui.setAttribute(UILayout.MARGIN_LEFT, 2);
				this.ui.setAttribute(UILayout.MARGIN_RIGHT, 2);
				this.ui.setAttribute(UILayout.FIXED_WIDTH, (GameRuntime.currentWidth - LAYOUT_DEFAULT_HORIZONTAL_MARGIN_INGAME) + 4);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, GameRuntime.currentHeight - LAYOUT_DEFAULT_HORIZONTAL_MARGIN_INGAME);
				this.ui.setAttribute(UILayout.ANCHOR_CENTER, UILayout.ANCHOR_CENTER_BIT);
				this.ui.setAttribute(UILayout.PACKED_HEIGHT, UILayout.PACKED_HEIGHT_BIT);
				this.ui.setAttribute(UILayout.SOFTKEY_BAR, 0);
				this.ui.setTitle(StringManager.getMessage(MessageID.DIALOG_RETURN_LEVEL_SELECT), -1, 1);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_CENTER, StringManager.getMessage(MessageID.UI_YES), 0, GameScene.EXIT_LEVEL, false);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_RIGHT, StringManager.getMessage(MessageID.UI_NO), 0, GameScene.MENU_PAUSE, true);
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.LEVEL_PROGRESS_WILL_BE_LOST), -1, this.ui, -1));
				break;
			case GameScene.CONFIRM_EXIT_LEVEL: //confirm quit level
				this.ui.loadFromResource(37);
				this.ui.setElemDefaultAttribute(UIElement.FONT, -3);
				this.ui.setAttribute(UILayout.FONT, -2);
				exitLevelReturnScene = GameScene.MENU_TITLE;
				this.ui.setElemDefaultAttribute(UIElement.AUTO_WIDTH, UIElement.AUTO_WIDTH_BIT);
				this.ui.setAttribute(UILayout.MARGIN_LEFT, 2);
				this.ui.setAttribute(UILayout.MARGIN_RIGHT, 2);
				this.ui.setAttribute(UILayout.FIXED_WIDTH, (GameRuntime.currentWidth - LAYOUT_DEFAULT_HORIZONTAL_MARGIN_INGAME) + 4);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, GameRuntime.currentHeight - LAYOUT_DEFAULT_HORIZONTAL_MARGIN_INGAME);
				this.ui.setAttribute(UILayout.ANCHOR_CENTER, UILayout.ANCHOR_CENTER_BIT);
				this.ui.setAttribute(UILayout.PACKED_HEIGHT, UILayout.PACKED_HEIGHT_BIT);
				this.ui.setAttribute(UILayout.SOFTKEY_BAR, 0);
				this.ui.setTitle(StringManager.getMessage(MessageID.DIALOG_QUIT_GAME), -1, 1);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_CENTER, StringManager.getMessage(MessageID.UI_YES), 0, GameScene.EXIT_LEVEL, false);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_RIGHT, StringManager.getMessage(MessageID.UI_NO), 0, GameScene.MENU_PAUSE, true);
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.LEVEL_PROGRESS_WILL_BE_LOST), -1, this.ui, -1));
				break;
			case GameScene.INFO_CHAPTER_COMPLETE: //chapter complete
				this.ui.loadFromResource(36);
				this.ui.setElemDefaultAttribute(UIElement.FONT, -3);
				this.ui.setAttribute(UILayout.FONT, -2);
				this.ui.setAttribute(UILayout.TITLE_PADDING_TOP, LAYOUT_DEFAULT_TITLE_PADDING_TOP);
				this.ui.setAttribute(UILayout.TITLE_PADDING_BOTTOM, LAYOUT_DEFAULT_TITLE_PADDING_BOTTOM);
				this.ui.setAttribute(UILayout.MARGIN_LEFT, 2);
				this.ui.setAttribute(UILayout.MARGIN_RIGHT, 2);
				this.ui.setAttribute(UILayout.FIXED_WIDTH, (GameRuntime.currentWidth - (LAYOUT_DEFAULT_HORIZONTAL_MARGIN << 1)) + 4);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, GameRuntime.currentHeight - LAYOUT_DEFAULT_VERTICAL_MARGIN);
				this.ui.setAttribute(UILayout.OFFSET_LEFT, LAYOUT_DEFAULT_HORIZONTAL_MARGIN - 2);
				this.ui.setTitle(StringManager.getMessage(MessageID.CHAPTER_COMPLETE), -1, 1);

				//HD
				this.ui.setAttribute(UILayout.TITLE_PADDING_TOP, 4);
				this.ui.setAttribute(UILayout.FIXED_WIDTH, (240 - (LAYOUT_DEFAULT_HORIZONTAL_MARGIN << 1)) + 4);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, 320 - LAYOUT_DEFAULT_VERTICAL_MARGIN - 36);
				this.ui.setAttribute(UILayout.ANCHOR_CENTER, UILayout.ANCHOR_CENTER_BIT);

				if (this.wasSuperBounceJustUnlocked) {
					this.ui.setSoftkey(GameRuntime.SOFTKEY_CENTER, StringManager.getMessage(MessageID.UI_OK), 0, GameScene.INFO_GAME_COMPLETED, true);
				} else if (this.wasFinalLevelJustBeaten) {
					this.ui.setSoftkey(GameRuntime.SOFTKEY_CENTER, StringManager.getMessage(MessageID.UI_OK), 0, GameScene.INFO_GAME_BEATEN, true);
				} else {
					this.ui.setSoftkey(GameRuntime.SOFTKEY_CENTER, StringManager.getMessage(MessageID.UI_OK), 0, GameScene.MENU_LEVEL_SELECT, true);
				}
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.SCORE, calcScore), -1, this.ui, -1));
				String eggsCollectedStr = eggCount + "/" + bonusLevelEggLimit;
				UIElement eggsCollectedUI = new UIElement(eggsCollectedStr, 102, this.ui, -1);
				if (isTextRightToLeft) {
					eggsCollectedUI.setAttribute(UIElement.ICON_ALIGNMENT, 16);
					eggsCollectedUI.setText(eggsCollectedStr, 102);
				}
				this.ui.addElement(eggsCollectedUI);
				String timerStr = formatGameTimer(levelTimer / 1000);
				UIElement timerUI = new UIElement(timerStr, 103, this.ui, -1);
				if (isTextRightToLeft) {
					timerUI.setAttribute(UIElement.ICON_ALIGNMENT, 16);
					timerUI.setText(timerStr, 103);
				}
				this.ui.addElement(timerUI);
				if (this.highScoreBeaten) {
					UIElement newHighScoreText = new UIElement(StringManager.getMessage(MessageID.NEW_HIGH_SCORE), -1, this.ui, -1);
					newHighScoreText.setAttribute(UIElement.TEXT_ALIGNMENT, 8);
					newHighScoreText.setText(StringManager.getMessage(MessageID.NEW_HIGH_SCORE), -1);
					this.ui.addElement(newHighScoreText);
				}
				if (wasLevelBeaten(LevelID.GAME_CLEAR_LEVEL) && !this.wasFinalLevelJustBeaten && !isBonusLevel(currentLevel)) {
					boolean anyMedalsWon = false;
					if (this.timerChallengeTrophy >= 0) {
						short timerTrophyImageId = TROPHY_IMAGE_IDS[this.timerChallengeTrophy];
						anyMedalsWon = true;
						UIElement timerChallengeText = new UIElement(StringManager.getMessage(MessageID.TIMER_CHALLENGE), timerTrophyImageId, this.ui, -1);
						timerChallengeText.setAttribute(UIElement.ICON_ALIGNMENT, isTextRightToLeft ? 0 : 16); //since 2.0.25
						timerChallengeText.setAttribute(UIElement.FLAG_3, 64);
						timerChallengeText.setAttribute(UIElement.TEXT_ALIGNMENT, 8);
						timerChallengeText.setText(StringManager.getMessage(MessageID.TIMER_CHALLENGE), timerTrophyImageId);
						this.ui.addElement(timerChallengeText);
					}
					if (this.collectionChallengeTrophy >= 0) {
						short collectionTrophyImageId = TROPHY_IMAGE_IDS[this.collectionChallengeTrophy];
						anyMedalsWon = true;
						UIElement collectionChallengeText = new UIElement(StringManager.getMessage(MessageID.COLLECTION_CHALLENGE), collectionTrophyImageId, this.ui, -1);
						collectionChallengeText.setAttribute(UIElement.ICON_ALIGNMENT, isTextRightToLeft ? 0 : 16); //since 2.0.25
						collectionChallengeText.setAttribute(UIElement.FLAG_3, 64);
						collectionChallengeText.setAttribute(UIElement.TEXT_ALIGNMENT, 8);
						collectionChallengeText.setText(StringManager.getMessage(MessageID.COLLECTION_CHALLENGE), collectionTrophyImageId);
						this.ui.addElement(collectionChallengeText);
					}
					if (!anyMedalsWon) {
						UIElement noMedalsWonText = new UIElement(StringManager.getMessage(MessageID.NO_MEDALS_WON), -1, this.ui, -1);
						noMedalsWonText.setAttribute(UIElement.TEXT_ALIGNMENT, 8);
						noMedalsWonText.setText(StringManager.getMessage(MessageID.NO_MEDALS_WON), -1);
						this.ui.addElement(noMedalsWonText);
					}
				}
				this.wasFinalLevelJustBeaten = false;
				this.wasSuperBounceJustUnlocked = false;
				this.timerChallengeTrophy = -1;
				this.collectionChallengeTrophy = -1;
				this.highScoreBeaten = false;
				cycleLevelSelectRight(this.ui, false);
				break;
			case GameScene.INFO_GAME_BEATEN: //all levels beaten
				this.ui.loadFromResource(36);
				this.ui.setElemDefaultAttribute(UIElement.FONT, -3);
				this.ui.setAttribute(UILayout.FONT, -2);
				this.ui.setAttribute(UILayout.TITLE_PADDING_TOP, LAYOUT_DEFAULT_TITLE_PADDING_TOP);
				this.ui.setAttribute(UILayout.TITLE_PADDING_BOTTOM, LAYOUT_DEFAULT_TITLE_PADDING_BOTTOM);
				this.ui.setAttribute(UILayout.MARGIN_LEFT, 2);
				this.ui.setAttribute(UILayout.MARGIN_RIGHT, 2);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, GameRuntime.currentHeight - LAYOUT_DEFAULT_VERTICAL_MARGIN);
				this.ui.setAttribute(UILayout.FIXED_WIDTH, (GameRuntime.currentWidth - (LAYOUT_DEFAULT_HORIZONTAL_MARGIN << 1)) + 4);
				this.ui.setAttribute(UILayout.OFFSET_LEFT, LAYOUT_DEFAULT_HORIZONTAL_MARGIN - 2);

				//HD
				this.ui.setAttribute(UILayout.TITLE_PADDING_TOP, 4);
				this.ui.setAttribute(UILayout.FIXED_WIDTH, (240 - (LAYOUT_DEFAULT_HORIZONTAL_MARGIN << 1)) + 4);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, 320 - LAYOUT_DEFAULT_VERTICAL_MARGIN - 36);
				this.ui.setAttribute(UILayout.ANCHOR_CENTER, UILayout.ANCHOR_CENTER_BIT);

				this.ui.setTitle(StringManager.getMessage(MessageID.DIALOG_GAME_BEATEN), -1, 1);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_CENTER, StringManager.getMessage(MessageID.UI_OK), 0, GameScene.MENU_LEVEL_SELECT, true);
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.ALL_LEVELS_BEATEN), -1, this.ui, -1));
				break;
			case GameScene.INFO_GAME_COMPLETED: //all levels completed
				this.ui.loadFromResource(36);
				this.ui.setElemDefaultAttribute(UIElement.FONT, -3);
				this.ui.setAttribute(UILayout.FONT, -2);
				this.ui.setAttribute(UILayout.TITLE_PADDING_TOP, LAYOUT_DEFAULT_TITLE_PADDING_TOP);
				this.ui.setAttribute(UILayout.TITLE_PADDING_BOTTOM, LAYOUT_DEFAULT_TITLE_PADDING_BOTTOM);
				this.ui.setAttribute(UILayout.MARGIN_LEFT, 2);
				this.ui.setAttribute(UILayout.MARGIN_RIGHT, 2);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, GameRuntime.currentHeight - LAYOUT_DEFAULT_VERTICAL_MARGIN);
				this.ui.setAttribute(UILayout.FIXED_WIDTH, (GameRuntime.currentWidth - (LAYOUT_DEFAULT_HORIZONTAL_MARGIN << 1)) + 4);
				this.ui.setAttribute(UILayout.OFFSET_LEFT, LAYOUT_DEFAULT_HORIZONTAL_MARGIN - 2);

				//HD
				this.ui.setAttribute(UILayout.TITLE_PADDING_TOP, 4);
				this.ui.setAttribute(UILayout.FIXED_WIDTH, (240 - (LAYOUT_DEFAULT_HORIZONTAL_MARGIN << 1)) + 4);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, 320 - LAYOUT_DEFAULT_VERTICAL_MARGIN - 36);
				this.ui.setAttribute(UILayout.ANCHOR_CENTER, UILayout.ANCHOR_CENTER_BIT);

				this.ui.setTitle(StringManager.getMessage(MessageID.DIALOG_GAME_COMPLETED), -1, 1);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_CENTER, StringManager.getMessage(MessageID.UI_OK), 0, GameScene.MENU_LEVEL_SELECT, true);
				this.ui.addElement(new UIElement(StringManager.getMessage(MessageID.ALL_LEVELS_COMPLETED), -1, this.ui, -1));
				break;
			case GameScene.INFO_FIELD_MESSAGE: //field message
				this.ui.setElemDefaultAttribute(UIElement.FONT, -1);
				this.ui.setAttribute(UILayout.FONT, -1);
				this.ui.setAttribute(UILayout.MARGIN_LEFT, 2);
				this.ui.setAttribute(UILayout.MARGIN_RIGHT, 2);
				this.ui.setAttribute(UILayout.FIXED_WIDTH, (GameRuntime.currentWidth - LAYOUT_DEFAULT_HORIZONTAL_MARGIN_INGAME) + 4);
				this.ui.setAttribute(UILayout.FIXED_HEIGHT, GameRuntime.currentHeight - LAYOUT_DEFAULT_HORIZONTAL_MARGIN_INGAME);
				this.ui.setAttribute(UILayout.ANCHOR_CENTER, UILayout.ANCHOR_CENTER_BIT);
				this.ui.setAttribute(UILayout.PACKED_HEIGHT, UILayout.PACKED_HEIGHT_BIT);
				this.ui.setAttribute(UILayout.SOFTKEY_BAR, 0);
				this.ui.setTitle("", -1, 1);
				this.ui.setSoftkey(GameRuntime.SOFTKEY_CENTER, StringManager.getMessage(MessageID.UI_OK), 0, GameScene.CLOSE_FIELD_MESSAGE, true);
				if (reqReloadFieldMsg) {
					this.ui.addElement(new UIElement(lastFieldMsg, -1, this.ui, -1));
					reqReloadFieldMsg = false;
				} else {
					String fieldMsg;
					if (fieldMessageParam != null) {
						fieldMsg = StringManager.getMessage(fieldMessageQueue[0], fieldMessageParam);
						fieldMessageParam = null;
					} else {
						fieldMsg = StringManager.getMessage(fieldMessageQueue[0]);
					}
					if (fieldMsg.endsWith("\n\n")) {
						fieldMsg = fieldMsg.substring(0, fieldMsg.length() - 2);
					} else if (fieldMsg.startsWith("\n\n")) {
						fieldMsg = fieldMsg.substring(2, fieldMsg.length());
					}
					lastFieldMsg = fieldMsg;
					this.ui.addElement(new UIElement(fieldMsg, -1, this.ui, -1));
				}
				break;
		}
		this.drawUI = this.ui;
		this.ui.setupSoftkeys();
		GameRuntime.initHID(2);
		GameRuntime.resetHID();
	}

	/* renamed from: i */
	private static void updateEvents() {
		if (isBlockingEvent) {
			EventObject.updateEvents(events);
			isBlockingEvent = false;
		} else {
			rootLevelObj.updateBBox();
			for (GameObject obj = rootLevelObj; obj != null; obj = obj.getNextNodeDescendToChildren(rootLevelObj)) {
				obj.updatePhysics();
			}
			EventObject.checkBounceEventTrigger(events, bounceObj);
			int stolenColorsBeforeScript = EventObject.eventVars[7];
			EventObject.updateEvents(events);
			if (EventObject.eventVars[7] != stolenColorsBeforeScript) {
				isFlashToOtherColorMode = false;
				stolenColorsAnimationCountdown = 2000;
				stolenColorsFlashCountdown = 0;
			}
			for (GameObject collider = rootLevelObj; collider != null; collider = collider.getNextNodeDescendToChildren(rootLevelObj)) {
				collider.checkCollisions(rootLevelObj);
			}
			if (reqCameraSnap) {
				GameObject.cameraVelocityX = 0;
				GameObject.cameraVelocityY = 0;
				GameObject.updateCamera(true);
				reqCameraSnap = false;
			} else {
				GameObject.updateCamera(false);
			}
		}
	}

	/* renamed from: a */
	public final int update(int lastUpdateRes) {
		if (reqQuit) {
			GameRuntime.quit();
		}
		if (reqPlayTitleMusic) {
			GameRuntime.playMusic(ResourceID.AUDIO_BGM_TITLE_MID, -1);
			reqPlayTitleMusic = false;
		}
		boolean bounceMoving;
		if (this.drawUI != null) {
			this.drawUI.setupSoftkeys();
			this.drawUI.updateTitleScroll();
		} else if (this.gameMainState == 4) {
			if (levelPaused) {
				setUI(GameScene.MENU_PAUSE);
				GameRuntime.stopMusic();
			} else if (reqReloadFieldMsg) {
				setUI(GameScene.INFO_FIELD_MESSAGE);
				isBlockingEvent = true;
				isFieldMessageShowing = true;
			} else {
				totalGameTime += GameRuntime.updateDelta;
				waterSingletonFlag = false;
				switch (getPlayerState()) {
					case PLAYER_STATE_PLAY:
						bounceObj.zCoord = 0;
						if (EventObject.eventVars[1] != CONTROLLER_FROZEN) {
							levelTimer += GameRuntime.updateDelta;
						}
						if (EventObject.eventVars[1] == CONTROLLER_NORMAL) {
							if (GameRuntime.checkButton(KeyCode.LEFT)) {
								bounceObj.moveLeft();
								bounceMoving = true;
							} else {
								bounceMoving = false;
							}
							if (GameRuntime.checkButton(KeyCode.RIGHT)) {
								bounceObj.moveRight();
								bounceMoving = true;
							}
							if (GameRuntime.checkButton(KeyCode.NUM5)
									|| GameRuntime.checkButton(KeyCode.NUM2)
									|| GameRuntime.checkButton(KeyCode.UP)
									|| GameRuntime.checkButton(KeyCode.SOFTKEY_MIDDLE)) {
								bounceObj.jump(false);
								bounceMoving = true;
							}
							if (bounceMoving) {
								if (bounceObj.eyeFrame != 1) {
									bounceObj.eyeFrame = 0;
								}
								bounceObj.idleAnimStartTimer = 3000;
							}
						} else if (EventObject.eventVars[1] == CONTROLLER_CANNON) {
							if (GameRuntime.checkButton(KeyCode.UP)) {
								currentCannon.rotateUp();
							}
							if (GameRuntime.checkButton(KeyCode.DOWN)) {
								currentCannon.rotateDown();
							}
							if (GameRuntime.checkButton(KeyCode.NUM5) || GameRuntime.checkButton(KeyCode.SOFTKEY_MIDDLE)) {
								currentCannon.fire();
							}
						}
						int i2 = EventObject.eventVars[4];
						updateEvents();
						if (EventObject.eventVars[4] == 0 && i2 != 0) {
							bounceObj.resetPhysics();
						}
						if (bounceObj.localObjectMatrix.translationY < rootLevelObj.allBBoxMinY && getPlayerState() == PLAYER_STATE_WIN) {
							//sanity death boundary
							setPlayerState(PLAYER_STATE_LOSE);
						}
						if (eggCount == bonusLevelEggLimit && isBonusLevel(currentLevel)) {
							setPlayerState(PLAYER_STATE_WIN);
						}
						EventObject.eventVars[3] = bounceObj.ballForme;
						EventObject.eventVars[4] = (int) bounceObj.curVelocity;
						EventObject.eventVars[5] = (int) bounceObj.curXVelocity;
						EventObject.eventVars[6] = (int) bounceObj.curYVelocity;
						if (stolenColorsAnimationCountdown > 0) {
							stolenColorsAnimationCountdown -= GameRuntime.updateDelta;
							stolenColorsFlashCountdown -= GameRuntime.updateDelta;
							if (stolenColorsFlashCountdown <= 0) {
								stolenColorsFlashCountdown = Math.abs((mRNG.nextInt() % 200) + 300);
								isFlashToOtherColorMode = true;
							}
							if (stolenColorsAnimationCountdown <= 0) {
								stolenColorsAnimationCountdown = 0;
								stolenColorsFlashCountdown = 0;
								isFlashToOtherColorMode = false;
								isColorsAreStolen = !isColorsAreStolen;
							}
							for (int i = 0; i < ALL_PARALLAX_IMAGE_IDS.length; i++) {
								if (GameRuntime.getImageResource(ALL_PARALLAX_IMAGE_IDS[i]) != null) {
									if ((!isColorsAreStolen || isFlashToOtherColorMode) && (isColorsAreStolen || !isFlashToOtherColorMode)) {
										if (parallaxImagesRegColors[i] != null) {
											GameRuntime.replaceImageResource(ALL_PARALLAX_IMAGE_IDS[i], parallaxImagesRegColors[i]);
										}
									} else if (parallaxImagesStolenColors[i] != null) {
										GameRuntime.replaceImageResource(ALL_PARALLAX_IMAGE_IDS[i], parallaxImagesStolenColors[i]);
									}
								}
							}
						}
						break;
					case PLAYER_STATE_LOSE:
						GameRuntime.playMusic(ResourceID.AUDIO_ME_LOSE_MID, 1);
						setPlayerState(PLAYER_STATE_LOSE_UPDATE);
						exitWaitTimer = 3000;
						deathBaseY = bounceObj.localObjectMatrix.translationY;
						/*if (currentLevel == LevelID.FINAL_RIDE) { //removed in 2.0.25
							EventObject.finalBossTimer = 0;
						}*/
						break;
					case PLAYER_STATE_WIN:
						GameRuntime.playMusic(ResourceID.AUDIO_ME_WIN_MID, 1);
						setPlayerState(PLAYER_STATE_WIN_UPDATE);
						exitWaitTimer = 3000;
						bounceObj.resetPhysics();
						if (isBonusLevel(currentLevel)) {
							bounceObj.enablePhysics = false;
						}
						winParticle.emitCircle(20, bounceObj.localObjectMatrix.translationX, bounceObj.localObjectMatrix.translationY, 740, 0, 1840, 230);
						break;
					case PLAYER_STATE_LOSE_UPDATE: //dying -> return to checkpoint
						exitWaitTimer -= GameRuntime.updateDelta;
						bounceObj.updateDeathAnimation();
						if (exitWaitTimer <= 0) {
							bounceObj.setPosXY(checkpointPosX, checkpointPosY);
							reqCameraSnap = true;
							setPlayerState(PLAYER_STATE_PLAY);
							bounceObj.fadeColor = 0xFF000000;
							GameRuntime.playMusic(getLevelMusicID(), -1);
						}
						break;
					case PLAYER_STATE_WIN_UPDATE:
						//exit level and update stats
						exitWaitTimer -= GameRuntime.updateDelta;
						bounceObj.jump(true);
						updateEvents();
						if (exitWaitTimer <= 0) {
							int e = getTimerChallengeRank(currentLevel);
							int d = getCollectionChallengeRank(currentLevel);
							short highScoreBefore = getLevelGlobalHighScore(currentLevel);
							boolean finalRideBeatenBefore = wasLevelBeaten(13);
							boolean superBounceUnlockedBefore = checkSuperBounceUnlocked();
							short levelClearTimeSeconds = (short) (levelTimer / 1000);
							if (levelClearTimeSeconds < 1) {
								levelClearTimeSeconds = 1;
							}
							int finalScore = (int) ((((float) (eggCount * 10000)) * EGG_SCORE_MULTIPLIER_BY_LEVEL[currentLevel]) / ((float) levelClearTimeSeconds));
							if (finalScore > 32767) {
								finalScore = 32767;
							}
							calcScore = (short) finalScore;
							updateLevelStats(currentLevel, (short) eggCount, levelClearTimeSeconds, (short) calcScore);
							if (!isBonusLevel(currentLevel)) {
								int nextLevel = currentLevel + 1;
								if (isBonusLevel(nextLevel)) {
									nextLevel++;
								}
								if (nextLevel <= LevelID.FANTASTIC_FAIR && !isLevelUnlocked(nextLevel)) {
									unlockLevel(nextLevel);
								}
							}
							int bonusChapterNo = 0;
							for (int bonusLevelIdx = 0; bonusLevelIdx < BONUS_LEVEL_INFO.length; bonusLevelIdx += 2) {
								if (getTotalEggCount() >= BONUS_LEVEL_INFO[bonusLevelIdx + 1]) { //required egg count for unlock
									int bonusLevelLevelId = BONUS_LEVEL_INFO[bonusLevelIdx];
									if (!isLevelUnlocked(bonusLevelLevelId)) {
										unlockLevel(bonusLevelLevelId);
										bonusChapterNo = (bonusLevelIdx >> 1) + 1;
									}
								}
							}
							if (!finalRideBeatenBefore && wasLevelBeaten(LevelID.GAME_CLEAR_LEVEL)) {
								this.wasFinalLevelJustBeaten = true;
							}
							if (!superBounceUnlockedBefore && checkSuperBounceUnlocked()) {
								this.wasSuperBounceJustUnlocked = true;
							}
							int e2 = getTimerChallengeRank(currentLevel);
							if (e2 > e) {
								this.timerChallengeTrophy = e2;
							}
							int d2 = getCollectionChallengeRank(currentLevel);
							if (d2 > d) {
								this.collectionChallengeTrophy = d2;
							}
							if (getLevelGlobalHighScore(currentLevel) > highScoreBefore) {
								this.highScoreBeaten = true;
							}
							serializeSaveData();
							if (bonusChapterNo > 0) {
								this.reqQuitLevelAfterFieldMessage = true;
								fieldMessageParam = new String[]{String.valueOf(bonusChapterNo)};
								pushFieldMessage(MessageID.BONUS_CHAPTER_UNLOCKED);
							} else {
								levelEnded();
							}
						}
						break;
					default:
						break;
				}
				popFieldMessage();
			}
		}
		return 0;
	}

	/* renamed from: a */
	public final int paint(int lastPaintResult, int paintMode) {
		short[] sArr;
		short[] sArr2;
		short[] sArr3;
		Graphics grp = GameRuntime.getGraphicsObj();
		if (this.gameMainState == 2) { //splash screen/loading
			if (this.curSplashId < 0) {
				drawLoadingBar(grp);
				updateLoadingScreen();
			} else {
				grp.setColor(SPLASH_BG_COLORS[this.curSplashId]);
				grp.fillRect(0, 0, GameRuntime.currentWidth, GameRuntime.currentHeight);
				int splashImageId = SPLASH_IMAGE_IDS[this.curSplashId];
				GameRuntime.drawImageRes(
						((GameRuntime.currentWidth - GameRuntime.getImageMapParam(splashImageId, 0)) / 2) + GameRuntime.getImageMapParam(splashImageId, 2),
						((GameRuntime.currentHeight - GameRuntime.getImageMapParam(splashImageId, 1)) / 2) + GameRuntime.getImageMapParam(splashImageId, 3),
						splashImageId
				);
				if (System.currentTimeMillis() - this.splashScreenStartTime > ((long) SPLASH_SCREEN_DURATIONS[this.curSplashId])) {
					updateLoadingScreen();
				}
			}
			return 0;
		}
		if (paintMode == 1) {
			if (this.gameMainState == 4) { //in-game
				GameRuntime.setBacklight(true);
				Graphics graphics = GameRuntime.getGraphicsObj();
				graphics.setClip(0, 0, renderClipWidth, renderClipHeight);
				int levelType = getLevelType(currentLevel);
				short[] sArr4 = f307i;
				short[] sArr5 = f311j;
				short[] sArr6 = f314k;
				short[] fixedPosParallaxes = f318l;
				int i4 = 5413606;
				int i5 = 7460351;
				if (levelType == 1) {
					short[] sArr8 = f322m;
					short[] sArr9 = f325n;
					short[] sArr10 = f328o;
					fixedPosParallaxes = f331p;
					i4 = 7263689;
					i5 = 10485759;
					sArr = sArr10;
					sArr2 = sArr9;
					sArr3 = sArr8;
				} else if (levelType == 2) {
					short[] sArr11 = f334q;
					short[] sArr12 = f337r;
					short[] sArr13 = f340s;
					fixedPosParallaxes = f343t;
					i4 = 7737588;
					i5 = 131610;
					sArr = sArr13;
					sArr2 = sArr12;
					sArr3 = sArr11;
				} else {
					sArr = sArr6;
					sArr2 = sArr5;
					sArr3 = sArr4;
				}
				if (isBonusLevel(currentLevel)) {
					i4 = 0xB400BD;
					i5 = 0xFD8C13;
				}
				int bgStripeW = GameRuntime.currentWidth;
				int bgHeight = GameRuntime.currentHeight;
				int bgStripeH = GameRuntime.currentHeight / 40;
				int i9 = (i4 >> 16) & 255;
				int i10 = (i4 >> 8) & 255;
				int i11 = i4 & 255;
				int i12 = 65536 / bgHeight;
				int i13 = (((i5 >> 16) & 255) - i9) * i12 * bgStripeH;
				int i14 = (((i5 >> 8) & 255) - i10) * i12 * bgStripeH;
				int i15 = ((i5 & 255) - i11) * i12 * bgStripeH;
				int i16 = i9 << 16;
				int i17 = i10 << 16;
				int i18 = i11 << 16;
				for (int bgY = 0; bgY < bgHeight; bgY += bgStripeH) {
					setBGColor(((i16 >> 16) << 16) + ((i17 >> 16) << 8) + (i18 >> 16), graphics);
					graphics.fillRect(0, bgY + 0, bgStripeW, bgStripeH);
					i16 += i13;
					i17 += i14;
					i18 += i15;
				}
				//bugfix: on high resolutions, not enough parallaxes were generated, resulting in early culling
				//we compensate this by generating more parallaxes for larger screen sizes
				int pscale = ((renderClipWidth + 239) / 240) * ((renderClipHeight + 319) / 320);
				checkReallocParallax((pscale * PARALLAX_MAX_COUNT) + 1);
				mRNG.setSeed(currentLevel + 1);
				if (levelType == 2) {
					drawBGParallax(fixedPosParallaxes, 5, 100, 600, 100, -80, 50, 2 * pscale, -1, graphics);
					drawBGParallax(sArr, 20, 100, GameRuntime.getImageMapParam(sArr[0], ImageMap.PARAM_WIDTH), 0, -50, 0, 3 * pscale, 0x15113C, graphics);
					drawBGParallax(sArr2, 50, 100, GameRuntime.getImageMapParam(sArr2[0], ImageMap.PARAM_WIDTH), 0, -30, 0, 3 * pscale, 0x02021A, graphics);
				} else {
					drawBGParallax(sArr, 20, 100, 200, 140, -200, 200, PARALLAX_MAX_COUNT * pscale, -1, graphics);
					drawBGParallax(sArr2, 50, 100, 100, 70, -70, 300, PARALLAX_MAX_COUNT * pscale, -1, graphics);
					drawBGParallax(sArr3, 80, 100, 150, 100, -70, 300, PARALLAX_MAX_COUNT * pscale, -1, graphics);
				}
				mRNG.setSeed(System.currentTimeMillis());
				GameObject.drawSceneTree(rootLevelObj, graphics);
				graphics.setClip(0, 0, GameRuntime.currentWidth, GameRuntime.currentHeight);
				GameRuntime.setTextStyle(-3, 1);
				GameRuntime.setTextColor(0, 0);
				GameRuntime.drawImageRes(0, 0, 102);
				int eggLimitX = drawStylizedNumber(32, 7, eggCount, Graphics.LEFT, false) + 32;
				GameRuntime.drawImageRes(eggLimitX, 7, 101); //divider slash (0/30)
				drawStylizedNumber(eggLimitX + 9, 7, bonusLevelEggLimit, Graphics.LEFT, false);
				GameRuntime.drawImageRes(GameRuntime.currentWidth, 0, 103);
				int levelSecondTimer = levelTimer / 1000;
				int levelTimeMinutes = levelSecondTimer / 60;
				int levelTimeSeconds = levelSecondTimer % 60;
				int timerSecondsX = ((GameRuntime.currentWidth - 25) - 6) - 1;
				int timerMinutesX = timerSecondsX - drawStylizedNumber(timerSecondsX, 7, levelTimeSeconds, Graphics.RIGHT, true);
				GameRuntime.drawImageResAnchored(timerMinutesX, 7, 100, Graphics.TOP | Graphics.RIGHT); //divider colon (00:00)
				drawStylizedNumber(timerMinutesX - 5, 7, levelTimeMinutes, Graphics.RIGHT, false);
				isFlashToOtherColorMode = false;
			}
			if (this.drawUI != null) {
				this.drawUI.draw();
			}
		} else if (paintMode == 2) { //loading
			drawLoadingBar(grp);
		}
		return 0;
	}

	/* renamed from: b */
	public final int loadScene(int sceneId, int sceneResult) {
		if (!(sceneId == GameScene.MENU_TITLE || sceneId == GameScene.MENU_PAUSE)) {
			lastMenuOption = 0;
		}
		switch (sceneId) {
			case GameScene.ENTRYPOINT:
				GameRuntime.startLoadScene(GameScene.INIT);
				return 0;
			case GameScene.LOAD_SAVE_DATA:
				if (sceneResult >= 0 && sceneResult < SPLASH_SCREEN_LAYOUT_RESIDS.length) {
					if (sceneResult == 0) {
						this.curSplashId = -1;
						this.gameMainState = 2;

						this.drawUI = null;
						GameRuntime.resetSoftkeys(); //not present in the original game, disables softkeys on soft-reset
					}
					GameRuntime.loadResource(SPLASH_SCREEN_LAYOUT_RESIDS[sceneResult]);
					return sceneResult + 1;
				} else if (sceneResult != SPLASH_SCREEN_LAYOUT_RESIDS.length) {
					return 0;
				} else {
					System.out.println("Loading saved data...");
					unloadLevel();
					if (GameRuntime.isMusicEnabled()) {
						GameRuntime.loadResidentResSet(0);
					}
					byte[] savedData = GameRuntime.loadFromRecordStore("game");
					if (savedData != null) {
						deserializeSaveData(savedData);
						if (!wasLevelBeaten(LevelID.GAME_CLEAR_LEVEL)) {
							for (int levelId = LevelID.LEVEL_IDX_MAX - 1; levelId >= 0; levelId--) {
								if (!isBonusLevel(levelId) && isLevelUnlocked(levelId)) {
									selectedLevelId = levelId;
									break;
								}
							}
						}
					} else {
						clearSaveData();
						unlockLevel(0);
					}
					System.out.println("Saved data loaded");
					return 0;
				}
			case GameScene.INIT:
				if (sceneResult == 0) {
					return 1;
				}
				if (sceneResult == 1) {
					return 2;
				}
				if (sceneResult != 2) {
					return 0;
				}
				if (this.drawUI == null || this.drawUI.uiID == 14) {
					GameRuntime.setMusicEnabled(true);
					GameRuntime.startLoadScene(GameScene.LOAD_SAVE_DATA);
				} else {
					setUI(GameScene.MENU_SOFTLOCK); //forbid soft-resetting
				}
				return 0;
			case GameScene.EXIT_LEVEL: //exit level
				if (sceneResult == 0) {
					GameRuntime.stopMusic();
					unloadLevel();
					return 1;
				} else if (sceneResult != 1) {
					return 0;
				} else {
					this.gameMainState = 3;
					setUI(exitLevelReturnScene);
					GameRuntime.playMusic(ResourceID.AUDIO_BGM_TITLE_MID, -1);
					return 0;
				}
			case GameScene.LOAD_LEVEL:
			case 15: //load level
				if (sceneResult == 0) {
					GameRuntime.stopMusic();
					GameRuntime.unloadResource(ResourceID.GRAPHICS_UIMAINMENU_RES);
					GameRuntime.unloadResource(ResourceID.GRAPHICS_UILEVELSELECT_RES);
					GameRuntime.loadResource(LEVEL_RESIDS[currentLevel]);
					GameRuntime.loadResource(LEVEL_RESIDS[CANNON_LEVEL_INDEX]);
					GameRuntime.loadResource(ResourceID.GRAPHICS_BALLHIGHLIGHT_RES);
					GameRuntime.loadResource(ResourceID.GRAPHICS_BALLPARTS_RES);
					GameRuntime.loadResource(ResourceID.GRAPHICS_OBJDOOR_RES);
					GameRuntime.loadResource(ResourceID.GRAPHICS_OBJLEVER_RES);
					GameRuntime.loadResource(ResourceID.GRAPHICS_OBJSIGNBOARD_RES);
					GameRuntime.loadResource(ResourceID.GRAPHICS_OBJFRIEND_RES);
					GameRuntime.loadResource(ResourceID.GRAPHICS_OBJSTONEWALL_RES);
					GameRuntime.loadResource(ResourceID.GRAPHICS_UIPAUSEMENU_RES);
					GameRuntime.loadResource(ResourceID.GRAPHICS_ENEMY00CANDLE_RES);
					GameRuntime.loadResource(ResourceID.GRAPHICS_ENEMY02MOLE_RES);
					GameRuntime.loadResource(ResourceID.GRAPHICS_OBJHYPNOTOID_RES);
					GameRuntime.loadResource(ResourceID.GRAPHICS_OBJSPIKE_RES);
					GameRuntime.loadResource(ResourceID.GRAPHICS_PARTICLESPLASH_RES);
					GameRuntime.loadResource(ResourceID.GRAPHICS_PARTICLECOMMON_RES);
					GameRuntime.loadResource(ResourceID.GRAPHICS_BALLBUMPYCRACKS_RES);
					GameRuntime.loadResource(ResourceID.GRAPHICS_OBJEGG_RES);
					switch (getLevelType(currentLevel)) {
						case 0:
							GameRuntime.loadResource(ResourceID.GRAPHICS_LEVELACT01_RES);
							GameRuntime.loadResource(ResourceID.GRAPHICS_OBJCOLORMACHINE_RES);
							GameRuntime.loadResource(ResourceID.GRAPHICS_OBJCOLORMACHINEBROKEN_RES);
							break;
						case 1:
							GameRuntime.loadResource(ResourceID.GRAPHICS_LEVELACT02_RES);
							GameRuntime.loadResource(ResourceID.GRAPHICS_OBJCOLORMACHINE_RES);
							GameRuntime.loadResource(ResourceID.GRAPHICS_OBJCOLORMACHINEBROKEN_RES);
							break;
						case 2:
							GameRuntime.loadResource(ResourceID.GRAPHICS_LEVELACT03_RES);
							GameRuntime.loadResource(ResourceID.GRAPHICS_OBJCANNON_RES);
							//This resource is used for the hypnotoid beam. However, it isn't properly unloaded when the level ends in the original game.
							GameRuntime.loadResource(ResourceID.GRAPHICS_OBJCOLORMACHINE_RES);
							break;
						default:
							break;
					}
					ballFramebuffer = Image.createImage(BounceObject.BALL_DIMENS_SCREENSPACE[0] * 3, BounceObject.BALL_DIMENS_SCREENSPACE[0] * 3);
					ballGraphics = ballFramebuffer.getGraphics();
					ballFramebufferRGB = new int[(ballFramebuffer.getWidth() * ballFramebuffer.getHeight())];
					Image createImage2 = Image.createImage(112, 26);
					spriteFB = createImage2;
					spriteOffscreenGraphics = createImage2.getGraphics();
					spriteFBRGB = new int[(spriteFB.getWidth() * spriteFB.getHeight())];
					return 1;
				} else if (sceneResult != 1) {
					return 0;
				} else {
					if (!this.isLevelActive) {
						this.isLevelActive = true;
						levelPaused = false;
						loadLevel();
					}
					this.gameMainState = 4;
					setIngameHID();
					if (!levelPaused) {
						GameRuntime.playMusic(getLevelMusicID(), -1);
					}
					return 0;
				}
			case GameScene.START_NEW_GAME: //start NG+
				for (int saveIndex = 0; saveIndex < levelSaveData.length; saveIndex++) {
					if ((saveIndex + 1) % 4 != 0) {
						levelSaveData[saveIndex] = 0;
					}
				}
				unlockLevel(0);
				selectedLevelId = 0;
				serializeSaveData();
				this.isLevelActive = false;
				setUI(GameScene.MENU_LEVEL_SELECT);
				return 0;
			case 21:
			case 22:
				lastMenuOption = this.ui.getSelectedOption();
				setUI(sceneId);
				return 0;
			case 27:
				return 0;
			case GameScene.CALL_TITLE_MENU: //go to main menu
				if (sceneResult == 0) {
					GameRuntime.unloadResource(SPLASH_SCREEN_LAYOUT_RESIDS[SPLASH_SCREEN_LAYOUT_RESIDS.length - 1]);
					return 1;
				} else if (sceneResult != 1) {
					return 0;
				} else {
					this.gameMainState = 3;
					GameRuntime.playMusic(ResourceID.AUDIO_BGM_TITLE_MID, -1);
					setUI(GameScene.MENU_TITLE);
					return 0;
				}
			default:
				return 0;
		}
	}

	private static void loadLevel() {
		GameRuntime.setUpdatesPerDraw(2);
		GameRuntime.setMaxUpdateDelta(150 / GameRuntime.getUpdatesPerDraw());

		//since 2.0.25
		for (int i = 0; i < 5; i++) {
			fieldMessageQueue[i] = -1;
		}
		fieldMessagePointer = 0;
		isFieldMessageShowing = false;
		reqReloadFieldMsg = false;

		isSuperBounceUnlocked = checkSuperBounceUnlocked();
		GameObject.cameraBounceFactor = 90;
		GameObject.cameraStabilizeSpeed = 140;
		levelTimer = 0;
		totalGameTime = 0;
		eggCount = 0;
		calcScore = 0;
		isFieldMessageShowing = false;
		waterSingletonFlag = false;
		isBlockingEvent = false;
		GameObject.setScreenSpaceMatrixByWindow(GameRuntime.currentWidth, GameRuntime.currentHeight);
		BounceObject.updateScreenSpaceConstants();
		byte[] cannonLevel = (byte[]) GameRuntime.getLoadedResData((int) LEVEL_RESIDS[CANNON_LEVEL_INDEX]);
		cannonModels = new GameObject[GameObject.readShort(cannonLevel, 8)];
		byte cmnKey;
		short cmnObjectId = 0;
		int maxVerticesPerObj = 0;
		int pos = 14;
		while ((cmnKey = cannonLevel[pos++]) != LevelKey.END) {
			short dataSize = GameObject.readShort(cannonLevel, pos);
			pos += 2;
			switch (cmnKey) {
				case LevelKey.GEOMETRY:
					GeometryObject geometry = new GeometryObject();
					geometry.setObjectId(cmnObjectId);
					geometry.readData(cannonLevel, pos);
					if (geometry.getVertexCount() > maxVerticesPerObj) {
						maxVerticesPerObj = geometry.getVertexCount();
					}
					cannonModels[cmnObjectId++] = geometry;
					break;
				default:
					break;
			}
			pos += dataSize;
		}
		byte[] levelData = (byte[]) GameRuntime.getLoadedResData((int) LEVEL_RESIDS[currentLevel]);
		if (levelData == null) {
			return;
		}
		objectCount = GameObject.readShort(levelData, 8);
		levelObjects = new GameObject[objectCount];
		eventCount = GameObject.readShort(levelData, 12);
		events = new EventObject[eventCount];
		int currentBytePos = 8 + 6 + 1;
		byte key = levelData[8 + 6];
		short objID = 0;
		int eventId = 0;
		bonusLevelEggLimit = 0;
		while (key != LevelKey.END) {
			short dataLength = GameObject.readShort(levelData, currentBytePos);
			int afterHeaderPos = currentBytePos + 2;
			switch (key) {
				case LevelKey.GEOMETRY: {
					GeometryObject geometry = new GeometryObject();
					geometry.setObjectId(objID);
					geometry.readData(levelData, afterHeaderPos);
					if (geometry.getVertexCount() > maxVerticesPerObj) {
						maxVerticesPerObj = geometry.getVertexCount();
					}
					levelObjects[objID] = geometry;
					break;
				}
				case 5:
				case 7:
				default:
					levelObjects[objID] = new GameObject();
					levelObjects[objID].setObjectId(objID);
					levelObjects[objID].readData(levelData, afterHeaderPos);
					break;
				case LevelKey.EVENT:
					EventObject objEv = new EventObject();
					objEv.setObjectId(objID);
					objEv.readData(levelData, afterHeaderPos);
					levelObjects[objID] = objEv;
					events[eventId++] = objEv;
					break;
				case LevelKey.PLAYER:
					BounceObject player = new BounceObject(true);
					bounceObj = player;
					player.setObjectId(objID);
					player.readData(levelData, afterHeaderPos);
					player.initialize();
					levelObjects[objID] = player;
					checkpointPosX = player.localObjectMatrix.translationX;
					checkpointPosY = player.localObjectMatrix.translationY;
					break;
				case LevelKey.SPRITE:
					levelObjects[objID] = new SpriteObject();
					levelObjects[objID].setObjectId(objID);
					levelObjects[objID].readData(levelData, afterHeaderPos);
					levelObjects[objID].initialize();
					break;
				case LevelKey.WATER:
					WaterObject water = new WaterObject();
					water.setObjectId(objID);
					water.readData(levelData, afterHeaderPos);
					water.initialize();
					if (water.vertexCount > maxVerticesPerObj) {
						maxVerticesPerObj = water.vertexCount;
					}
					levelObjects[objID] = water;
					break;
				case LevelKey.CANNON:
					CannonObject cannon = new CannonObject();
					cannon.setObjectId(objID);
					cannon.readData(levelData, afterHeaderPos);
					cannon.initialize();
					levelObjects[objID] = cannon;
					break;
				case LevelKey.TRAMPOLINE:
					TrampolineObject jumpPad = new TrampolineObject();
					jumpPad.setObjectId(objID);
					jumpPad.readData(levelData, afterHeaderPos);
					jumpPad.initialize();
					levelObjects[objID] = jumpPad;
					break;
				case LevelKey.EGG:
					EggObject egg = new EggObject();
					egg.setObjectId(objID);
					egg.readData(levelData, afterHeaderPos);
					egg.initialize();
					levelObjects[objID] = egg;
					bonusLevelEggLimit++;
					break;
				case LevelKey.FRIEND: {
					BounceObject friend = new BounceObject(false);
					friend.setObjectId(objID);
					friend.readData(levelData, afterHeaderPos);
					friend.initialize();
					levelObjects[objID] = friend;
					break;
				}
				case LevelKey.ENEMY: {
					EnemyObject enemy = new EnemyObject();
					enemy.setObjectId(objID);
					enemy.readData(levelData, afterHeaderPos);
					enemy.initialize();
					levelObjects[objID] = enemy;
					bonusLevelEggLimit++;
					break;
				}
			}
			objID++;
			int nextKeyIndex = afterHeaderPos + dataLength;
			currentBytePos = nextKeyIndex + 1;
			key = levelData[nextKeyIndex];
		}
		for (int i = 0; i < levelObjects.length; i++) {
			if (levelObjects[i] == null) {
				System.err.println("Object with ID " + i + " is ABSENT!!");
			}
		}
		GameObject.makeObjectLinks(levelObjects);
		//BUGFIX: BounceObject physics only work if the object isn't parented to anything
		for (int i = 0; i < levelObjects.length; i++) {
			if (levelObjects[i].getObjType() == BounceObject.TYPEID) {
				levelObjects[i].makeIndependent();
			}
		}
		rootLevelObj = levelObjects[0];
		levelObjects = null;
		enemyDeadEgg = new EggObject();
		enemyDeadEgg.setObjectId(objID++);
		enemyDeadEgg.setParent(rootLevelObj);
		enemyDeadEgg.localObjectMatrix.m00 = LP32.ONE;
		enemyDeadEgg.localObjectMatrix.m01 = 0;
		enemyDeadEgg.localObjectMatrix.translationX = Integer.MAX_VALUE;
		enemyDeadEgg.localObjectMatrix.m10 = 0;
		enemyDeadEgg.localObjectMatrix.m11 = LP32.ONE;
		enemyDeadEgg.localObjectMatrix.translationY = Integer.MAX_VALUE;
		enemyDeadEgg.renderCalcMatrix.setFromMatrix(enemyDeadEgg.localObjectMatrix);
		enemyDeadEgg.initialize();
		bubbleParticle.setObjectId(objID++);
		bubbleParticle.attachToObject(rootLevelObj);
		waterSplashParticle.setObjectId(objID++);
		waterSplashParticle.attachToObject(rootLevelObj);
		cannonParticle.setObjectId(objID++);
		cannonParticle.attachToObject(rootLevelObj);
		eggCollectParticle.setObjectId(objID++);
		eggCollectParticle.attachToObject(rootLevelObj);
		enemyDeathParticle.setObjectId(objID++);
		enemyDeathParticle.attachToObject(rootLevelObj);
		winParticle.setObjectId(objID++);
		winParticle.attachToObject(rootLevelObj);
		superBounceParticle.setObjectId(objID++);
		superBounceParticle.attachToObject(rootLevelObj);
		colorMachineDestroyParticle.setObjectId(objID++);
		colorMachineDestroyParticle.attachToObject(rootLevelObj);
		airTunnelParticle.setObjectId(objID++);
		airTunnelParticle.attachToObject(rootLevelObj);
		GameObject.allocateRenderPool(objID); //okay to be a bit much, it's just a pointer array
		GeometryObject.TEMP_QUAD_XS = new int[maxVerticesPerObj];
		GeometryObject.TEMP_QUAD_YS = new int[maxVerticesPerObj];
		EventObject.eventVars = new int[72];
		setPlayerState(PLAYER_STATE_PLAY);
		EventObject.eventVars[1] = 0;
		EventObject.eventVars[2] = 0;
		EventObject.eventVars[7] = 0;
		GameRuntime.unloadResource(LEVEL_RESIDS[currentLevel]);
		GameRuntime.unloadResource(LEVEL_RESIDS[CANNON_LEVEL_INDEX]);
		GameObject.cameraTarget = bounceObj;
		GameObject.snapCameraToTarget();
		f240F = GameObject.cameraMatrix.translationY;
		initStolenColorData(); //inlined in 2.0.25
		waterSplashParticle.particleCount = -1;
		bubbleParticle.particleCount = -1;
		cannonParticle.particleCount = -1;
		eggCollectParticle.particleCount = -1;
		enemyDeathParticle.particleCount = -1;
		winParticle.particleCount = -1;
		superBounceParticle.particleCount = -1;
		colorMachineDestroyParticle.particleCount = -1;
		airTunnelParticle.particleCount = -1;
		bounceObj.fadeColor = 0xFF000000;
		boolean noBonusLevelsBeaten = true;
		for (int bonusLevelIdx = 0; bonusLevelIdx < BONUS_LEVEL_INFO.length; bonusLevelIdx += 2) {
			if (wasLevelBeaten(BONUS_LEVEL_INFO[bonusLevelIdx])) {
				noBonusLevelsBeaten = false;
			}
		}
		if (noBonusLevelsBeaten && isBonusLevel(currentLevel)) {
			pushFieldMessage(MessageID.GUIDE_TEXT_3); //bonus chapter guide
		}
		bounceObj.eyeFrame = 0;
		bounceObj.idleAnimStartTimer = 3000;
	}

	private static final int[] CHEAT_COMBO_DEBUG_SCENE_CALL = new int[]{KeyCode.NUM3, KeyCode.NUM1, KeyCode.NUM3};

	private int debugSceneCallIdx = -1;
	private int debugSceneCallBuffer = 0;

	/* renamed from: b */
	public final void handleKeyPress(int keyCode) {
		if (this.drawUI != null) {
			UILayout ui = this.drawUI;
			if (ui.uiID == GameScene.MENU_LEVEL_SELECT) {
				if (debugSceneCallIdx != -1) {
					int num = keyCode - KeyCode.NUM0;
					if (num >= 0 && num <= 9) {
						debugSceneCallBuffer *= 10;
						debugSceneCallBuffer += num;
					} else if (keyCode == KeyCode.SOFTKEY_MIDDLE) {
						debugSceneCallIdx = -1;
						System.out.println("Calling debug scene " + debugSceneCallBuffer);
						GameRuntime.initHID(GameRuntime.CONTROL_MODE_GAME);
						changeScene(debugSceneCallBuffer);
					} else {
						GameRuntime.initHID(GameRuntime.CONTROL_MODE_GAME);
						debugSceneCallIdx = -1;
					}
					return;
				}

				if (enableCheats) {
					if (CHEAT_COMBO_ALL_UNLOCK[cheatComboIndex] == keyCode) {
						if (++cheatComboIndex == CHEAT_COMBO_ALL_UNLOCK.length) {
							System.out.println("Cheat activated: unlock all levels");
							for (int levelId = 0; levelId < LevelID.LEVEL_IDX_MAX; levelId++) {
								debugLevelUnlock(levelId);
							}
							cheatComboIndex = 0;
							updateLevelStartSoftkeyByUnlock(ui);
							serializeSaveData();
						}
					} else if (CHEAT_COMBO_ALL_COMPLETE[cheatComboIndex] == keyCode) {
						if (++cheatComboIndex == CHEAT_COMBO_ALL_COMPLETE.length) {
							System.out.println("Cheat activated: complete all levels");
							for (int levelId = 0; levelId < LevelID.LEVEL_IDX_MAX; levelId++) {
								debugLevelUnlock(levelId);
								updateLevelStats(levelId, (short) 30, (short) 1, (short) 9999);
							}
							cheatComboIndex = 0;
							updateLevelStartSoftkeyByUnlock(ui);
							serializeSaveData();
						}
					} else if (CHEAT_COMBO_DEBUG_SCENE_CALL[cheatComboIndex] == keyCode) {
						if (++cheatComboIndex == CHEAT_COMBO_DEBUG_SCENE_CALL.length) {
							System.out.println("Cheat activated: debug scene call");
							debugSceneCallIdx = 0;
							debugSceneCallBuffer = 0;
							cheatComboIndex = 0;
							GameRuntime.initHID(GameRuntime.CONTROL_MODE_RAW);
						}
					} else {
						cheatComboIndex = 0;
					}
				}

				switch (keyCode) {
					case KeyCode.LEFT:
						cycleLevelSelectLeft(ui, true);
						break;
					case KeyCode.RIGHT:
						cycleLevelSelectRight(ui, true);
						break;
				}
                        }
			ui.handleKeyCode(keyCode);
		} else if (this.gameMainState == 2) {
			updateLoadingScreen();
		} else if (this.gameMainState == 4) {
			if (enableCheats) {
				//automatically win the level
				if (CHEAT_COMBO_ALL_UNLOCK[cheatComboIndex] == keyCode && (EventObject.eventVars[1] == CONTROLLER_NORMAL || EventObject.eventVars[1] == CONTROLLER_CANNON)) {
					if (++cheatComboIndex == CHEAT_COMBO_ALL_UNLOCK.length) {
						setPlayerState(PLAYER_STATE_WIN);
						cheatComboIndex = 0;
						eggCount = 30;
					}
				} else {
					cheatComboIndex = 0;
				}
			}
			switch (keyCode) {
				case KeyCode.SOFTKEY_RIGHT:
					levelPaused = true;
					break;
				case KeyCode.STAR:
					if (EventObject.eventVars[1] == CONTROLLER_NORMAL && getPlayerState() != PLAYER_STATE_LOSE_UPDATE) {
						bounceObj.cycleForme();
					}
					break;
			}
		}
	}

	/* renamed from: c */
	public final void onSystemEvent(int eventId) {
		if (eventId == GameRuntime.SYSTEM_EVENT_START) {
			GameRuntime.loadResource(ResourceID.GRAPHICS_UIARROWS_RES); //game UI
			GameRuntime.loadResource(ResourceID.GRAPHICS_UILEVELSTATS_RES);
			GameRuntime.loadResource(ResourceID.GRAPHICS_UINUMBERFONT_RES);
			GameRuntime.loadResource(ResourceID.LAYOUT_MENULAYOUTA_RES);
			GameRuntime.loadResource(ResourceID.LAYOUT_INFOLAYOUT_RES);
			GameRuntime.loadResource(-1);
			GameRuntime.loadResource(-2);
			GameRuntime.loadResource(-3);
			GameRuntime.startLoadScene(GameScene.ENTRYPOINT);
		} else if (eventId == GameRuntime.SYSTEM_EVENT_PAUSE && this.gameMainState == 4) {
			levelPaused = true;
			if (isFieldMessageShowing) {
				reqReloadFieldMsg = true;
				isFieldMessageShowing = false;
				setIngameHID();
			} else if (!reqReloadFieldMsg) {
				lastFieldMsg = null;
			}
		} else if (eventId == GameRuntime.SYSTEM_EVENT_RESIZE) { //added for resizing support
			if (GameRuntime.currentWidth > 0 && GameRuntime.currentHeight > 0) {
				renderClipWidth = GameRuntime.currentWidth;
				renderClipHeight = GameRuntime.currentHeight;
				GameObject.setScreenSpaceMatrixByWindow(GameRuntime.currentWidth, GameRuntime.currentHeight);
				BounceObject.updateScreenSpaceConstants();
			}
		}
	}

	/* renamed from: d */
	public final void changeScene(final int sceneId) {
		if (sceneId != GameScene.MENU_TITLE && sceneId != GameScene.MENU_PAUSE) {
			lastMenuOption = 0;
		}
		switch (sceneId) {
			case GameScene.ENTRYPOINT: //not present in original game, added for debug
			case GameScene.INIT:
			case GameScene.EXIT_LEVEL:
			case GameScene.LOAD_LEVEL:
			case GameScene.START_NEW_GAME:
			case 15:
			case 21:
			case GameScene.MENU_GUIDE:
			case 27: {
				GameRuntime.startLoadScene(sceneId);
				break;
			}
			case GameScene.MENU_HIGH_SCORES:
			case GameScene.MENU_NEW_GAME:
			case GameScene.CONFIRM_RESTART_LEVEL:
			case GameScene.CONFIRM_RETURN_LEVEL_SELECT:
			case GameScene.CONFIRM_EXIT_LEVEL: {
				lastMenuOption = this.ui.getSelectedOption();
			}
			case GameScene.MENU_TITLE:
			case GameScene.MENU_LEVEL_SELECT:
			case 19:
			case 23:
			case 24:
			case GameScene.MENU_PAUSE:
			case 26:
			case GameScene.INFO_CHAPTER_COMPLETE:
			case GameScene.INFO_GAME_BEATEN:
			case GameScene.INFO_GAME_COMPLETED: {
				this.setUI(sceneId);
				break;
			}
			case GameScene.QUIT_GAME: {
				GameRuntime.quit();
				break;
			}
			case GameScene.CLOSE_FIELD_MESSAGE: { //field message advance
				isFieldMessageShowing = false;
				this.setIngameHID();
				if (reqQuitLevelAfterFieldMessage) {
					reqQuitLevelAfterFieldMessage = false;
					levelEnded();
				}
				break;
			}
			case 13: {
				break;
			}
			case GameScene.RESTART_LEVEL: { //restart level
				resetParallaxStolenColors();
				//fall through
			}
			case GameScene.ENTER_LEVEL: {
				if (isLevelUnlocked(selectedLevelId)) { //enter level
					bookAnimationTime = targetBookAnimationTime;
					this.isLevelActive = false;
					currentLevel = selectedLevelId;
					GameRuntime.startLoadScene(6); //load level
				}
				break;
			}
			case GameScene.UNPAUSE_LEVEL: { //unpause
				levelPaused = false;
				this.setIngameHID();
				if (!levelPaused) {
					GameRuntime.playMusic(getLevelMusicID(), -1);
					break;
				}
				break;
			}
			case GameScene.OPEN_MORE_GAMES_URL: //since 2.0.25
				reqPlayTitleMusic = true;
				GameRuntime.stopMusic();
				try {
					if (GameRuntime.mMidLet.platformRequest(moreGamesURL)) {
						reqQuit = true;
					}
				} catch (ConnectionNotFoundException ex) {

				}
				break;
		}
	}
}
