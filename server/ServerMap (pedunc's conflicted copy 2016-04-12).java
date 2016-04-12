package server;

import java.util.ArrayList;
import java.util.Random;

import server.objects.ChestSpawn;
import server.objects.ObjectiveTowerSpawn;
import server.objects.TreeSpawn;
import server.objects.WorldObject;

public class ServerMap implements Runnable {

	public static ArrayList<WorldObject> worldObjects = new ArrayList<>();
	public static ArrayList<TreeSpawn> treeSpawns = new ArrayList<>();
	public static ArrayList<ObjectiveTowerSpawn> objectiveTowerSpawns = new ArrayList<>();
	public static ArrayList<ChestSpawn> chestSpawns = new ArrayList<>();

	public static int worldWidth = (int) (8000 * 1.3);
	public static int worldHeight = (int) (4500 * 1.3);
	public static int groundLevel = worldHeight - 1300;

	public static int startX = 2000;
	public static int startY = groundLevel - 200;

	public static int sleep = 40;

	public static int idCounter = 0;

	public static int woodPerStack = 1;
	public static int mineralsPerStack = 1;

	public static int scoreWinReq = 1000;
	public static boolean gameWon = false;

	public int tick = 0;

	public ServerMap() {
		loadLevel(0);
		Thread thread = new Thread(this);
		thread.start();
	}

	public static void sendWorldObjects(int playerNumber) {
		String worldInfo = "SNDWORLDOBJECTS!"; // SENDING MAP INFO
		for (int i = 0; i < worldObjects.size(); i++) {
			WorldObject obj = worldObjects.get(i);

			worldInfo += getInfoFromWorldObject(obj) + "=";
		}

		// System.out.println(worldInfo);
		Server.sendToClient(playerNumber, worldInfo);
	}

	public static void sendObjectiveTowerSpawns(int playerNumber) {

		String worldInfo = "SNDOBJECTIVETOWERSPAWNS!"; // SENDING TREE SPAWNS

		for (int i = 0; i < objectiveTowerSpawns.size(); i++) {
			ObjectiveTowerSpawn OTS = objectiveTowerSpawns.get(i);

			worldInfo += OTS.getInfo() + "=";
		}

		Server.sendToClient(playerNumber, worldInfo);
	}

	public static void sendTreeSpawns(int playerNumber) {

		String worldInfo = "SNDTREESPAWNS!"; // SENDING TREE SPAWNS
		for (int i = 0; i < treeSpawns.size(); i++) {
			TreeSpawn TS = treeSpawns.get(i);

			worldInfo += getInfoFromTreeSpawn(TS) + "=";
		}
		Server.sendToClient(playerNumber, worldInfo);
	}

	public static void sendWorldInfo(int playerNumber) {
		String worldInfo = "SNDMAPINFO!" + worldWidth + "@" + worldHeight + "@" + groundLevel + "@" + startX + "@" + startY + "@" + scoreWinReq;
		Server.sendToClient(playerNumber, worldInfo);

		ServerMap.sendWorldObjects(playerNumber);
		sendTreeSpawns(playerNumber);
		sendObjectiveTowerSpawns(playerNumber);
	}

	static void loadLevel(int level) {
		if (level == 0) {
			WorldObject obj;

			fillBackground();
			fillGround();

			double scale = 0.50;
			int smallPlatformWidth = (int) (791 * scale);
			int smallPlatformHeight = (int) (122 * scale);

			// storlek för objective tower
			scale = 1.5;
			int OTSHeight = (int) (372 * scale);
			int OTSWidth = (int) (148 * scale);

			int centerX = worldWidth / 2;

			// storlek på stora platformar
			scale = 0.49;
			int platformWidth = (int) (2917 * scale);
			int platformHeight = (int) (215 * scale);

			int platformCenterXOffset = platformWidth / 2;

			// storlek på stegar
			scale = 0.9;
			int ladderWidth = (int) (98 * scale);
			int ladderHeight = 375;
			int ladderCenterXOffset = ladderWidth / 2;

			int ladderHeightAlmost = ladderHeight - 10;

			int chestSpawnChance = 10;
			int chestSpawnInterval = 30000;

			// --
			// ---- PLATFORMEN LÄNGST NER, ANDRA NIVÅN ---
			// --

			int platformX = centerX - platformCenterXOffset;
			int platformY = groundLevel - ladderHeightAlmost;

			// Platformen längst ner
			obj = new WorldObject(platformX, platformY, platformWidth, platformHeight, 1, "platform", 0);
			worldObjects.add(obj);

			// stegen i mitten
			obj = new WorldObject(centerX - ladderCenterXOffset, groundLevel - ladderHeight, ladderWidth, ladderHeight, 1, "ladderSpot", 0);
			worldObjects.add(obj);

			// kistspawnen på andra nivån
			scale = 0.2;
			int chestWidth = (int) (422 * scale);
			int chestHeight = (int) (455 * scale);

			int chestX = centerX - 350;
			int chestY = platformY - chestHeight;

			ChestSpawn chestSpawn = new ChestSpawn(chestX, chestY, chestWidth, chestHeight, 100, 5000, true);
			chestSpawns.add(chestSpawn);

			int mirrorX = mirrorXCoord(chestX, chestWidth);

			chestSpawn = new ChestSpawn(mirrorX, chestY, chestWidth, chestHeight, 100, 2500, true);
			chestSpawns.add(chestSpawn);

			// koordinater för de två stegarna som sitter på platformen näst längst ner
			int ladderX = (int) (platformX + platformWidth * 0.15); // placerar stegen en bit in på platformen
			int ladderXMirror = mirrorXCoord(ladderX, ladderWidth);

			int ladderY = platformY - ladderHeight;

			// --
			// --------------- PLATFORMARNA NÄST LÄNGST NER, TREDJE NIVÅN -----------------
			// --

			// koordianter för platformarna näst längst ner
			platformX = (int) (centerX - (platformWidth * 0.7) - platformCenterXOffset);
			int platformXMirror = mirrorXCoord(platformX, platformWidth);

			platformY = platformY - ladderHeightAlmost;

			// platformen näst längst ner till vänster
			obj = new WorldObject(platformX, platformY, platformWidth, platformHeight, 1, "platform", 0);
			worldObjects.add(obj);

			// platformen näst längst ner till höger
			obj = new WorldObject(platformXMirror, platformY, platformWidth, platformHeight, 1, "platform", 0);
			worldObjects.add(obj);

			// stegen till vänster på mittenplatformen
			obj = new WorldObject(ladderX, ladderY, ladderWidth, ladderHeight, 1, "ladderSpot", 0);
			worldObjects.add(obj);

			// stegen till höger på mittenplatformen
			obj = new WorldObject(ladderXMirror, ladderY, ladderWidth, ladderHeight, 1, "ladderSpot", 0);
			worldObjects.add(obj);

			// koordinaterna som ska sitta på platformarna på tredje nivån
			ladderX = (int) (platformX + platformWidth * 0.15); // placerar stegen en bit in på platformen
			ladderXMirror = mirrorXCoord(ladderX, ladderWidth);

			ladderY = platformY - ladderHeight;

			// --
			// ---------- PLATFORMARNA TREJDE LÄNGST NER, fjärde nivån------------
			// --

			// koordinater för platformnarna på fjärde nivån
			platformX = (int) (centerX - (platformWidth * 0.7 * 2) - platformCenterXOffset);
			platformXMirror = mirrorXCoord(platformX, platformWidth);

			platformY = platformY - ladderHeightAlmost;

			// mitten platformen på fjärde nivån
			obj = new WorldObject(centerX - platformCenterXOffset, platformY, platformWidth, platformHeight, 1, "platform", 0);
			worldObjects.add(obj);

			// SAWMILL ------------------------------
			scale = 0.6;
			int sawmillWidth = (int) (542 * scale);
			int sawmillHeight = (int) (455 * scale);
			int sawmillX = centerX - sawmillWidth / 2;

			obj = new WorldObject(sawmillX, platformY - sawmillHeight, sawmillWidth, sawmillHeight, 1, "sawmill", 0);
			worldObjects.add(obj);

			chestX = sawmillX - 200;
			chestY = platformY - chestHeight;

			ServerMap.spawnMirrodChests(chestX, chestY, chestSpawnChance, chestSpawnInterval, false);

			// Objetive tornen på fjärde nivån

			// platformar

			// till vänster
			obj = new WorldObject(platformX, platformY, platformWidth, platformHeight, 1, "platform", 0);
			worldObjects.add(obj);

			// till höger
			obj = new WorldObject(platformXMirror, platformY, platformWidth, platformHeight, 1, "platform", 0);
			worldObjects.add(obj);

			// stegen till vänster
			obj = new WorldObject(ladderX, ladderY, ladderWidth, ladderHeight, 1, "ladderSpot", 0);
			worldObjects.add(obj);

			// stegen till höger
			obj = new WorldObject(ladderXMirror, ladderY, ladderWidth, ladderHeight, 1, "ladderSpot", 0);
			worldObjects.add(obj);

			ladderX = (int) (platformX + platformWidth * 0.15); // placerar stegen en bit in på platformen
			ladderXMirror = mirrorXCoord(ladderX, ladderWidth);

			ladderY = platformY - ladderHeight;

			// potion shrines

			scale = 0.6;
			int potionShrineWidth = (int) (81 * scale);
			int potionShrineHeight = (int) (265 * scale);

			int shrineX = platformX + platformWidth / 2 - potionShrineWidth / 2;
			int shrineY = platformY - potionShrineHeight;

			obj = new WorldObject(shrineX, shrineY, potionShrineWidth, potionShrineHeight, 1, "potionShrine", 0);
			worldObjects.add(obj);

			mirrorX = mirrorXCoord(shrineX, potionShrineWidth);

			obj = new WorldObject(mirrorX, shrineY, potionShrineWidth, potionShrineHeight, 1, "potionShrine", 1);
			worldObjects.add(obj);

			// --
			// ---------- PLATFORMARNA FJÄRDE LÄNGST NER, femte nivån------------ MEN MED SMÅ PLATFORMAR
			// --

			// små platformarna näst längst ut
			platformX = (int) (ladderX - smallPlatformWidth / 2);
			platformXMirror = mirrorXCoord(platformX, smallPlatformWidth);

			platformY = platformY - ladderHeightAlmost;

			ServerMap.addSmallPlatforms(platformX, platformY, smallPlatformWidth, smallPlatformHeight, true); // små platformar med kistor

			// till vänster
			obj = new WorldObject(ladderX, ladderY, ladderWidth, ladderHeight, 1, "ladderSpot", 0);
			worldObjects.add(obj);

			// till höger
			obj = new WorldObject(ladderXMirror, ladderY, ladderWidth, ladderHeight, 1, "ladderSpot", 0);
			worldObjects.add(obj);

			// --
			// ---------- PLATFORMARNA FEMTE LÄNGST NER, sjätte nivån------------
			// --

			platformY = platformY - ladderHeightAlmost;

			// mitten platformen på sjätte nivån
			obj = new WorldObject(centerX - platformCenterXOffset, platformY, platformWidth, platformHeight, 1, "platform", 0);
			worldObjects.add(obj);

			// weaponShrine

			scale = 0.7;
			int weaponShrineWidth = (int) (82 * scale);
			int weaponShrineHeight = (int) (136 * scale);

			int weaponShrineX = centerX - weaponShrineWidth / 2;
			int weaponShrineY = platformY - weaponShrineHeight;

			obj = new WorldObject(weaponShrineX, weaponShrineY, weaponShrineWidth, weaponShrineHeight, 1, "weaponShrine", 0);
			worldObjects.add(obj);

			// --
			// ---------- PLATFORMARNA SJÄTTE LÄNGST NER, sjunde nivån------------
			// --

			platformX = (int) (centerX - (platformWidth * 0.7 * 3) - platformCenterXOffset);
			platformXMirror = mirrorXCoord(platformX, platformWidth);
			platformY = platformY - ladderHeightAlmost;

			// till vänster
			obj = new WorldObject(platformX, platformY, platformWidth, platformHeight, 1, "platform", 0);
			worldObjects.add(obj);

			// till höger
			obj = new WorldObject(platformXMirror, platformY, platformWidth, platformHeight, 1, "platform", 0);
			worldObjects.add(obj);

			// OBJETIVE TOWERS ------------------------------------------

			int x = platformX + platformWidth / 2 - OTSWidth / 2;
			int y = platformY - OTSHeight;

			ObjectiveTowerSpawn towerSpawn = new ObjectiveTowerSpawn(x, y, OTSWidth, OTSHeight);
			objectiveTowerSpawns.add(towerSpawn);

			int towerMirrorX = mirrorXCoord(x, OTSWidth);

			towerSpawn = new ObjectiveTowerSpawn(towerMirrorX, y, OTSWidth, OTSHeight);
			objectiveTowerSpawns.add(towerSpawn);

			// --
			// ---------- PLATFORMARNA SJUNDE LÄNGST NER, åttånde nivån------------
			// --

			platformY = platformY - ladderHeightAlmost;

			// mitten platformen på åttånde
			obj = new WorldObject(centerX - platformCenterXOffset, platformY, platformWidth, platformHeight, 1, "platform", 0);
			worldObjects.add(obj);

			scale = 1;
			int observerTowerWidth = (int) (148 * scale);
			int observerTowerHeight = (int) (372 * scale);

			int observerTowerX = centerX - observerTowerWidth / 2;
			int observerTowerY = platformY - observerTowerHeight;

			// mittentornet
			obj = new WorldObject(observerTowerX, observerTowerY, observerTowerWidth, observerTowerHeight, 1, "tower", 0);
			worldObjects.add(obj);

			// END PILLARS SOM MARKERAR VAR BANAN SLUTAR
			scale = 1;
			int endPillarWidth = (int) (60 * scale);
			int endPillarHeight = (int) (290);

			obj = new WorldObject(0 - endPillarWidth, groundLevel - endPillarHeight, endPillarWidth, endPillarHeight, 1, "building", 0);
			worldObjects.add(obj);

			obj = new WorldObject(worldWidth, groundLevel - endPillarHeight, endPillarWidth, endPillarHeight, 1, "building", 0);
			worldObjects.add(obj);

			/// TREES ------------------------------------

			x = 50;
			scale = 1.1;
			int treeHeight = (int) (350 * scale);
			int treeWidth = (int) (150 * scale);

			// spawnar in massa träd längs marken
			for (int i = 0; i < 20; i++) {
				TreeSpawn spawner = new TreeSpawn(x, groundLevel - treeHeight, treeWidth, treeHeight, 10000, 100);
				treeSpawns.add(spawner);
				x += 1000;
			}

			// small platforms ------------------------------

			// små platformarna längst ner i mitten

			addSmallPlatforms(centerX - 2000, groundLevel - 1600, smallPlatformWidth, smallPlatformHeight, true);
			// små platformarna näst längst ner i mitten

			addSmallPlatforms(centerX - 1350, groundLevel - 1700, smallPlatformWidth, smallPlatformHeight, true);

			// små platformarna näst högst upp i mitten

			addSmallPlatforms(centerX - 1350, groundLevel - 1990, smallPlatformWidth, smallPlatformHeight, true);

			// små platformarna högst upp i mitten --------------------------------------

			addSmallPlatforms(centerX - 2000, groundLevel - 2100, smallPlatformWidth, smallPlatformHeight, true);

			// små plattformarna längst åt sidan med DJ -----------------------------------------

			int smallPlatformX = centerX - 3350;
			int smallPlatformY = groundLevel - 1600;

			addSmallPlatforms(smallPlatformX, smallPlatformY, smallPlatformWidth, smallPlatformHeight, false);

			// UNLOCK SKILLS SPOTS ---------------------------

			scale = 0.6;
			int unlockWidth = (int) (200 * scale);
			int unlockHeight = (int) (200 * scale);

			int unlockX = smallPlatformX + smallPlatformWidth / 2 - unlockWidth / 2;
			int unlockY = smallPlatformY - unlockHeight;

			obj = new WorldObject(unlockX, unlockY, unlockWidth, unlockHeight, 1, "unlockSkill", 0);
			worldObjects.add(obj);

			mirrorX = mirrorXCoord(unlockX, unlockWidth);

			obj = new WorldObject(mirrorX, unlockY, unlockWidth, unlockHeight, 1, "unlockSkill", 1);
			worldObjects.add(obj);

		}
	}

	public static void spawnMirrodChests(int x, int y, int chestSpawnChance, int chestSpawnInterval, boolean spawnWithChest) {
		double scale = 0.2;
		int chestWidth = (int) (422 * scale);
		int chestHeight = (int) (455 * scale);

		ChestSpawn chestSpawn = new ChestSpawn(x, y, chestWidth, chestHeight, chestSpawnChance, chestSpawnInterval, spawnWithChest);
		chestSpawns.add(chestSpawn);

		int mirrorX = mirrorXCoord(x, chestWidth);

		chestSpawn = new ChestSpawn(mirrorX, y, chestWidth, chestHeight, chestSpawnChance, chestSpawnInterval, spawnWithChest);
		chestSpawns.add(chestSpawn);

	}

	// lägger till två styckten små platformar speglade
	public static void addSmallPlatforms(int x, int y, int platformWidth, int platformHeight, boolean addChests) {
		int smallPlatformX = x;
		int smallPlatformY = y;

		int chestSpawnInterval = 30000;
		int chestSpawnChance = 10;

		double scale = 0.2;
		int chestWidth = (int) (422 * scale);
		int chestHeight = (int) (455 * scale);

		int chestY = smallPlatformY - chestHeight;
		int chestX = smallPlatformX + platformWidth / 2 - chestWidth / 2;

		WorldObject obj = new WorldObject(smallPlatformX, smallPlatformY, platformWidth, platformHeight, 1, "platform", 2);
		worldObjects.add(obj);

		if (addChests) {
			ChestSpawn chestSpawn = new ChestSpawn(chestX, chestY, chestWidth, chestHeight, chestSpawnChance, chestSpawnInterval, false);
			chestSpawns.add(chestSpawn);
		}

		int mirrorX = mirrorXCoord(smallPlatformX, platformWidth);
		int chestMirrorX = mirrorXCoord(chestX, chestWidth);

		obj = new WorldObject(mirrorX, smallPlatformY, platformWidth, platformHeight, 1, "platform", 2);
		worldObjects.add(obj);

		if (addChests) {
			ChestSpawn chestSpawn = new ChestSpawn(chestMirrorX, chestY, chestWidth, chestHeight, chestSpawnChance, chestSpawnInterval, false);
			chestSpawns.add(chestSpawn);
		}
	}

	static void fillBackground() {

		WorldObject obj;

		// himlen
		obj = new WorldObject(-5000, 0, worldWidth + 10000, worldHeight, 1, "backgroundObject", 4);
		worldObjects.add(obj);

		// solen
		obj = new WorldObject(worldWidth / 2 - 400, groundLevel - 2500, 800, 800, 1, "sun", 0);
		worldObjects.add(obj);

		initClouds();

		int height = 1200;
		// mörka bakgrunden över marken bakom trädet
		obj = new WorldObject(-5000, groundLevel - height, worldWidth + 10000, height, 1, "backgroundObject", 0);
		worldObjects.add(obj);

		int originalTreeHeight = 1518;
		int y = 0;
		int x = 0;

		// sätter ut alla träd
		for (int i = 4; i >= 0; i--) {

			int width = (int) (1920 - 1920 * 0.13 * i);
			height = (int) (width * 0.79);

			y = (int) (groundLevel - (originalTreeHeight * (1.69 + 0.17 * i)));

			double paralax = 0.7 - 0.1 * i;

			int version = 5 + i;

			int numRepeats = (int) (worldWidth / width * 1.0);

			int extraReps = 3;
			numRepeats += extraReps; // gör så den körs några extra gånger så allt täcks ordentligt
			x = 150 * i - width * extraReps; // 300*i gör så att varje bakgrund förskjuts. Width * extraReps gör så allt förskjuts till vänster så det är lika tätt över allt

			for (int n = 0; n < numRepeats; n++) {
				obj = new WorldObject(x, y, width, height, paralax, "backgroundObject", version);
				worldObjects.add(obj);
				x += width;
			}

		}

	}

	static void initClouds() {

		int num = 20;

		double scale = 1.3;
		int cloudWidth = (int) (439 * scale);
		int cloudHeight = (int) (166 * scale);

		for (int i = 0; i < num; i++) {
			Random ra = new Random();

			// spawnar random över en del av mapen
			int randX = ra.nextInt((int) (worldWidth * 0.25)) - 2000;
			int randY = ra.nextInt((int) (worldHeight * 0.5)) - 500;

			int version = ra.nextInt(3);

			WorldObject obj = new WorldObject(randX, randY, cloudWidth, cloudHeight, 0.1, "cloud", version);
			worldObjects.add(obj);
		}

	}

	// fyller med platformar längs hela banan
	static void fillGround() {
		WorldObject obj;

		int extra = 10;
		int y = groundLevel;
		int groundWidth = 1920;
		int x = -groundWidth;
		int groundHeight = 215;

		int numGrounds = (worldWidth / groundWidth) + 1;

		for (int i = -extra; i < numGrounds + 10; i++) {

			obj = new WorldObject(x, y, groundWidth, groundHeight, 1, "platform", 1);
			worldObjects.add(obj);

			x += groundWidth;
		}

	}

	public static WorldObject addObjectLive(int x, int y, int width, int height, double paralax, String itemType, int versionType) {
		WorldObject obj = new WorldObject(x, y, width, height, paralax, itemType, versionType);
		worldObjects.add(obj);
		sendAddObject(obj);

		return obj;
	}

	@Override
	public void run() {
		while (true) {

			if (tick % 20000 == 0) {
				System.gc();
			}

			for (int i = 0; i < chestSpawns.size(); i++) {
				ChestSpawn cs = chestSpawns.get(i);
				cs.update();
			}

			// uppdaterar alla treespawns
			for (int i = 0; i < treeSpawns.size(); i++) {
				TreeSpawn TS = treeSpawns.get(i);
				TS.update();
			}

			for (int i = 0; i < objectiveTowerSpawns.size(); i++) {
				ObjectiveTowerSpawn OTS = objectiveTowerSpawns.get(i);
				OTS.update();
			}

			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String getInfoFromTreeSpawn(TreeSpawn TS) {
		String info = "";

		int x = TS.getX();
		int y = TS.getY();
		int width = TS.getWidth();
		int height = TS.getHeight();
		int hasTree = (TS.hasTree()) ? 1 : 0;

		info += x + "@" + y + "@" + width + "@" + height + "@" + hasTree;

		return info;

	}

	public static String getInfoFromWorldObject(WorldObject obj) {
		String info = "";

		int x = obj.getX();
		int y = obj.getY();
		int width = obj.getWidth();
		int height = obj.getHeight();
		double paralax = obj.getParalax();
		String itemType = obj.getItemType();
		int versionType = obj.getVersionType();
		int objectId = obj.getObjectId();

		info += x + "@" + y + "@" + width + "@" + height + "@" + paralax + "@" + itemType + "@" + versionType + "@" + objectId;

		return info;
	}

	public static void sendRemoveObject(WorldObject obj) {
		String info = "SENDREMOVEOBJECTBYID" + obj.getObjectId();
		Server.sendToAllClients(info);
	}

	public static void sendAddObject(WorldObject obj) {
		String info = "SNDADDOBJECT!" + getInfoFromWorldObject(obj);
		Server.sendToAllClients(info);
	}

	// hanterar information från en klient om att byta ägare på ett torn
	static void changeObjectiveOwnerFromClient(String message) {
		String[] split1 = message.split("£"); // tar bort tidstämpeln
		String[] split2 = split1[0].split("!"); // tar bort prefixet
		String[] info = split2[1].split("&");

		int senderNumber = Integer.parseInt(info[0]);

		ServerReturn sender = Server.getPlayerByNumber(senderNumber);

		Team team = sender.getTeam();

		int OTSIndex = Integer.parseInt(info[1]);

		ObjectiveTowerSpawn OTS = objectiveTowerSpawns.get(OTSIndex);

		OTS.setOwner(team);
	}

	static void cutTreeFromClient(String message) {
		String[] split1 = message.split("£");
		String[] info = split1[0].split("&");

		int treeSpawnIndex = Integer.parseInt(info[1]);
		TreeSpawn TS = treeSpawns.get(treeSpawnIndex);
		TS.despawnTree();
	}

	// tar bort ett object med information från en klient
	static void removeObjectFromClient(String message) {
		String[] split1 = message.split("£");
		String[] info = split1[0].split("&");

		int id = Integer.parseInt(info[1]);
		WorldObject obj = getWorldObjectById(id);
		worldObjects.remove(obj);
	}

	static WorldObject getWorldObjectById(int id) {
		WorldObject returnObj = null;
		for (int i = 0; i < worldObjects.size(); i++) {
			WorldObject obj = worldObjects.get(i);
			if (obj.getObjectId() == id) {
				returnObj = obj;
			}
		}

		return returnObj;
	}

	static int mirrorXCoord(int x, int width) {
		int newX = 0;

		int oldX = x + width;

		int distFromCenter = Math.abs(worldWidth / 2 - oldX);

		newX = worldWidth / 2 + distFromCenter;

		return newX;
	}

	// När en klient öppnar en kista körs detta
	static void spawnOpenChestFromClient(String message) {
		String[] split1 = message.split("£"); // tar bort tidsstämpeln
		String[] info = split1[0].split("&");

		int id = Integer.parseInt(info[1]);// vilket id som objeketet som ska tas bort
		WorldObject oldObject = getWorldObjectById(id);

		addObjectLive(oldObject.getX(), oldObject.getY(), oldObject.getWidth(), oldObject.getHeight(), 1, "openChest", 0);

		updateChestSpawnById(id, false);
	}

	// kollar igenom alla chestspawns och uppdaterar den som har ID:t som skickas med. Körs när man får info från servern om att en kista har öppnats
	public static void updateChestSpawnById(int id, boolean hasChest) {
		for (int i = 0; i < chestSpawns.size(); i++) {
			ChestSpawn cs = chestSpawns.get(i);

			if (cs.getCurrentChestId() == id) {
				cs.setHasChest(hasChest);
			}
		}
	}

	// när en spelare dör ska alla items spelaren hade på sig droppas på marken. Klienten säger till server som kör denna metoden som spawner items på marken och skickar det till alla klienter
	static void dropAllItemsFromPlayer(String message) {
		String[] timeSplit = message.split("£"); // tar bort tidsstämpeln
		String[] prefixSplit = timeSplit[0].split("!"); // tar delar upp med prefixet

		String[] playerNumberSplit = prefixSplit[1].split("&"); // delar upp mellan informationen och spelar numret

		String[] infoSplit = playerNumberSplit[1].split("@");

		// hämtar informationen om hur många items av varje sak spelaren hade
		int x = Integer.parseInt(infoSplit[0]);
		int y = Integer.parseInt(infoSplit[1]);
		int numWood = Integer.parseInt(infoSplit[2]);
		int numMinerals = Integer.parseInt(infoSplit[3]);
		int numHealthPotions = Integer.parseInt(infoSplit[4]);
		int numEnergyPotions = Integer.parseInt(infoSplit[5]);

		// bara hälften av det man har på sig när man dör ska droppas. Anrundas neråt efersom det inte kan ligga ett halv item på marken
		numWood = (int) Math.floor(numWood / 2);
		numMinerals = (int) Math.floor(numMinerals / 2);
		numHealthPotions = (int) Math.floor(numHealthPotions / 2);
		numEnergyPotions = (int) Math.floor(numEnergyPotions / 2);

		// gör så att alla items som droppar sprids ut lite på marken
		int spread = 100;
		int startPlaceX = x - spread / 4;
		int totalNumItems = numWood + numMinerals + numHealthPotions + numEnergyPotions;

		double xJump = 0;
		if (totalNumItems != 0) {
			xJump = spread / (totalNumItems * 1.0);
		}

		int xStepCount = 0;

		// räknar ut hur många stacks av items det blir
		int numWoodStacks = numWood / woodPerStack;
		int numMineralStacks = numMinerals / mineralsPerStack;

		for (int i = 0; i < numWoodStacks; i++) {
			// sprider ut
			int xOffset = (int) (xJump * xStepCount);
			xStepCount++;

			addObjectLive(startPlaceX + xOffset, y, 30, 30, 1, "wood", 0);
		}

		for (int i = 0; i < numMineralStacks; i++) {
			// sprider ut
			int xOffset = (int) (xJump * xStepCount);
			xStepCount++;

			addObjectLive(startPlaceX + xOffset, y, 30, 30, 1, "mineral", 0);
		}

		for (int i = 0; i < numHealthPotions; i++) {
			// sprider ut
			int xOffset = (int) (xJump * xStepCount);
			xStepCount++;

			addObjectLive(startPlaceX + xOffset, y, 19, 30, 1, "potion", 0);
		}

		for (int i = 0; i < numEnergyPotions; i++) {
			// sprider ut
			int xOffset = (int) (xJump * xStepCount);
			xStepCount++;

			addObjectLive(startPlaceX + xOffset, y, 19, 30, 1, "potion", 1);
		}

	}

	// När en klient har byggt en stege skickas den informationen till servern som sen skickar det till alla andra
	static void spawnLadderFromClient(String message) {
		String[] split1 = message.split("£"); // tar bort tidsstämpeln
		String[] info = split1[0].split("&"); // delar upp efter prefixet

		int id = Integer.parseInt(info[1]); // vilket id som objeketet som ska hanteras
		WorldObject oldObject = getWorldObjectById(id);

		addObjectLive(oldObject.getX(), oldObject.getY(), oldObject.getWidth(), oldObject.getHeight(), 1, "ladder", 0);
	}

}
