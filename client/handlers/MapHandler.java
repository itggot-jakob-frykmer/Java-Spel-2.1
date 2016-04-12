package client.handlers;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;

import client.Client;
import client.Main;
import client.Screen;
import client.effects.Bubble;
import client.objects.Building;
import client.objects.CircleAnimatedObject;
import client.objects.CloudObject;
import client.objects.Ladder;
import client.objects.OpenChest;
import client.objects.Platform;
import client.objects.Tower;
import client.objects.WorldObject;
import client.objects.backgroundObjects.BackgroundObject;
import client.objects.backgroundObjects.Sun;
import client.objects.interactableWorldObjects.Chest;
import client.objects.interactableWorldObjects.InteractableWorldObject;
import client.objects.interactableWorldObjects.LadderSpot;
import client.objects.interactableWorldObjects.ObjectiveTowerSpawn;
import client.objects.interactableWorldObjects.PotionShrine;
import client.objects.interactableWorldObjects.Sawmill;
import client.objects.interactableWorldObjects.TreeSpawn;
import client.objects.interactableWorldObjects.UnlockSkill;
import client.objects.interactableWorldObjects.WeaponShrine;
import client.objects.lootableWorldObjects.Mineral;
import client.objects.lootableWorldObjects.Potion;
import client.objects.lootableWorldObjects.Wood;
import client.players.Player;
import client.players.Team;

// Den här klassen ansvara för alla worldobjects. När klienten ansluter till servern skickar servern information till klienten
// om vilka worldobjects det finns som denna klassen hanterar i addObjectFromInfo()
public class MapHandler {

	public static ArrayList<WorldObject> worldObjects = new ArrayList<>();
	public static ArrayList<TreeSpawn> treeSpawns = new ArrayList<>();
	public static ArrayList<ObjectiveTowerSpawn> objectiveTowerSpawns = new ArrayList<>();

	public static int worldWidth = 0;
	public static int worldHeight = 0;
	public static int groundLevel;

	public static int startX;
	public static int startY;

	public static int scoreWinReq;

	// när man ansluter från man information om hur världen ser ut, i denna metod behandlas denna info
	public static void initObjectsFromServer(String message) {

		try {
			String[] objects = message.split("=");
			for (int i = 0; i < objects.length; i++) {
				addObjectFromInfo(objects[i]);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}

	// lägger till ett object med information från servern
	public static void addObjectFromInfo(String objectInfo) {

		String[] info = objectInfo.split("@");

		int x = Integer.parseInt(info[0]);
		int y = Integer.parseInt(info[1]);
		int width = Integer.parseInt(info[2]);
		int height = Integer.parseInt(info[3]);
		double paralax = Double.parseDouble(info[4]);
		String itemType = info[5];
		int versionType = Integer.parseInt(info[6]);
		int objectId = Integer.parseInt(info[7]);

		if (itemType.equals("platform")) {
			WorldObject object = new Platform(x, y, width, height, paralax, "platforms/platform" + versionType + ".png", objectId, versionType);
			worldObjects.add(object);
		}

		else if (itemType.equals("building")) {
			WorldObject object = new Building(x, y, width, height, paralax, "buildings/building" + versionType + ".png", objectId, versionType);
			worldObjects.add(object);
		}

		else if (itemType.equals("backgroundObject")) {
			WorldObject object = new BackgroundObject(x, y, width, height, paralax, "backgrounds/background" + versionType + ".png", objectId, versionType);
			worldObjects.add(object);
		}

		else if (itemType.equals("wood")) {
			WorldObject object = new Wood(x, y, width, height, paralax, "objects/wood.png", objectId, versionType);
			worldObjects.add(object);
		}

		else if (itemType.equals("sun")) {
			WorldObject object = new Sun(x, y, width, height, paralax, "objects/sun.png", objectId, versionType);
			worldObjects.add(object);
		}

		else if (itemType.equals("potion")) {
			WorldObject object = new Potion(x, y, width, height, paralax, "potions/potion" + versionType + ".png", objectId, versionType);
			worldObjects.add(object);
		}

		else if (itemType.equals("ladder")) {
			WorldObject object = new Ladder(x, y, width, height, paralax, "objects/ladder.png", objectId, versionType);
			worldObjects.add(object);
		}

		else if (itemType.equals("ladderSpot")) {
			WorldObject object = new LadderSpot(x, y, width, height, paralax, "objects/ladder.png", objectId, versionType);
			worldObjects.add(object);
		}

		else if (itemType.equals("mineral")) {
			WorldObject object = new Mineral(x, y, width, height, paralax, "ui/icons/mineral.png", objectId, versionType);
			worldObjects.add(object);
		}

		else if (itemType.equals("potionShrine")) {
			WorldObject object = new PotionShrine(x, y, width, height, paralax, "potionShrines/potionShrine" + versionType + ".png", objectId, versionType);
			worldObjects.add(object);
		}

		else if (itemType.equals("tower")) {
			WorldObject object;
			if (versionType == 0) {

				int offsetX = (int) (width * 0.5);
				int offsetY = (int) (height * 0.14);

				int arcWidth = 143;
				int arcHeight = 141;
				object = new CircleAnimatedObject(x + offsetX - arcWidth / 2, y + offsetY - arcHeight / 2, arcWidth, arcHeight, 1, "objects/bigArc.png", -1, 0, 95, 0.005);
				MapHandler.worldObjects.add(object);

				arcWidth = 80;
				arcHeight = 68;

				object = new CircleAnimatedObject(x + offsetX - arcWidth / 2, y + offsetY - arcHeight / 2, arcWidth, arcHeight, 1, "objects/mediumArc.png", -1, 0, 75, 0.01);
				MapHandler.worldObjects.add(object);

				arcWidth = 43;
				arcHeight = 38;

				object = new CircleAnimatedObject(x + offsetX - arcWidth / 2, y + offsetY - arcHeight / 2, arcWidth, arcHeight, 1, "objects/smallArc.png", -1, 0, 55, 0.008);
				MapHandler.worldObjects.add(object);
			}

			object = new Tower(x, y, width, height, paralax, "towers/tower" + versionType + ".png", objectId, versionType);
			worldObjects.add(object);
		} else if (itemType.equals("chest")) {
			WorldObject object = new Chest(x, y, width, height, paralax, "chests/chest" + versionType + ".png", objectId, versionType);
			worldObjects.add(object);
		} else if (itemType.equals("openChest")) {
			WorldObject object = new OpenChest(x, y, width, height, paralax, "chests/openChest0.png", objectId, versionType);
			worldObjects.add(object);
		} else if (itemType.equals("weaponShrine")) {
			WorldObject object = new WeaponShrine(x, y, width, height, paralax, "buildings/weaponShrine.png", objectId, versionType);
			worldObjects.add(object);
		} else if (itemType.equals("sawmill")) {
			WorldObject object;

			double scale = 0.18;
			int sawWidth = (int) (224 * scale);
			int sawHeight = (int) (224 * scale);

			// spawnar också in sågen i sawmillen
			int sawX = (int) (x + width * 0.17) - sawWidth / 2;
			int sawY = (int) (y + height * 0.81) - sawHeight / 2;

			object = new CircleAnimatedObject(sawX, sawY, sawWidth, sawHeight, 1, "objects/sawmillSaw.png", -1, 0, 0, 0.009);
			MapHandler.worldObjects.add(object);

			object = new Sawmill(x, y, width, height, paralax, "buildings/sawmill.png", objectId, versionType);
			worldObjects.add(object);

		} else if (itemType.equals("unlockSkill")) {
			WorldObject object = new UnlockSkill(x, y, width, height, paralax, "ui/icons/skill_" + versionType + "_ready.png", objectId, versionType);
			worldObjects.add(object);
		} else if (itemType.equals("cloud")) {
			WorldObject object = new CloudObject(x, y, width, height, paralax, "objects/clouds/cloud" + versionType + ".png", objectId, versionType);
			worldObjects.add(object);
		}

	}

	// uppdaterar ägaren av ett torn vid ett tornspawn
	public static void updateOwnerOfObjectiveTower(String message) {
		String[] split = message.split("&");

		int senderNumber = Integer.parseInt(split[0]);

		Player sender = Main.getPlayerByNumber(senderNumber);
		Team team = sender.getTeam();

		int OTSIndex = Integer.parseInt(split[1]);
		ObjectiveTowerSpawn OTS = objectiveTowerSpawns.get(OTSIndex);

		OTS.changeOwner(team);
		// OTS.updateIcon();
	}

	// uppdaterar en tree spawner med ett meddelande från servern
	public static void updateStateOfTreeSpawn(String message) {
		String[] info = message.split("@");

		int index = Integer.parseInt(info[0]);
		boolean hasTree = info[1].equals("1");

		treeSpawns.get(index).updateState(hasTree);
	}

	// initialiserar alla objectiveTowerSpawn
	public static void initObjectiveTowerSpawns(String message) {

		String[] objects = message.split("=");
		for (int i = 0; i < objects.length; i++) {

			String[] info = objects[i].split("@");

			int x = Integer.parseInt(info[0]);
			int y = Integer.parseInt(info[1]);
			int width = Integer.parseInt(info[2]);
			int height = Integer.parseInt(info[3]);
			int belonging = Integer.parseInt(info[4]);

			ObjectiveTowerSpawn OTS = new ObjectiveTowerSpawn(x, y, width, height, belonging);
			objectiveTowerSpawns.add(OTS);
		}

	}

	// initialiserar alla tree spawners med meddelande från servern
	public static void initTreeSpawns(String message) {
		String[] objects = message.split("=");
		for (int i = 0; i < objects.length; i++) {

			String[] info = objects[i].split("@");

			int x = Integer.parseInt(info[0]);
			int y = Integer.parseInt(info[1]);
			int width = Integer.parseInt(info[2]);
			int height = Integer.parseInt(info[3]);
			boolean hasTree = (info[4].equals("1"));

			TreeSpawn TS = new TreeSpawn(x, y, width, height, hasTree);
			treeSpawns.add(TS);
		}
	}

	public static void cutTreeFromServer(String message) {
		String[] info = message.split("&");

		int treeSpawnIndex = Integer.parseInt(info[1]);

		TreeSpawn TS = treeSpawns.get(treeSpawnIndex);
		TS.despawnTree();
	}

	// tar emot information om mappen från servern
	public static void initMapInfo(String message) {
		String[] info = message.split("@");
		worldWidth = Integer.parseInt(info[0]);
		worldHeight = Integer.parseInt(info[1]);
		groundLevel = Integer.parseInt(info[2]);
		startX = Integer.parseInt(info[3]);
		startY = Integer.parseInt(info[4]);
		scoreWinReq = Integer.parseInt(info[5]);

		Toolkit tk = Toolkit.getDefaultToolkit();

		// hämtar datorns skärm storlek
		int screenWidth = (int) (tk.getScreenSize().getWidth());
		int screenHeight = (int) (tk.getScreenSize().getHeight());

		Screen.scaleWidthZoom = screenWidth / (worldWidth * 1.0);
		Screen.scaleHeightZoom = screenHeight / (worldHeight * 1.0);
	}

	public static void removeObject(WorldObject obj) {
		obj.onRemove();
		worldObjects.remove(obj);
	}

	public static void sendRemoveObject(WorldObject obj) {

		int id = obj.getObjectId();

		String message = id + "";

		Client.sendData("#SNDCLIENTREMOVEWORLDOBJECT", message);

	}

	// tar bort ett object med information från servern
	public static void removeObjectFromServer(String message) {
		String[] split = message.split("&");

		int id = Integer.parseInt(split[1]);
		removeWorldObjectById(id);
	}

	static void removeWorldObjectById(int id) {
		WorldObject obj = getWorldObjectById(id);
		if (obj != null) {
			removeObject(obj);
		}
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

	public static void addWorldObjectToFront(WorldObject obj) {
		Collections.reverse(MapHandler.worldObjects);
		worldObjects.add(obj);
		Collections.reverse(MapHandler.worldObjects);
	}

	public static void sendSpawnOpenChest(int objectId) {
		Client.sendData("SENDSPAWNOPENCHEST", objectId + "");
	}

	public static void sendSpawnLadder(int objectId) {
		Client.sendData("SENDSPAWNLADDER", objectId + "");
	}

	// hämtar det worldObject som ligger på en given plats. Funkar bara om man vet att det inte kommer ligga två stycken på samma plats
	public static WorldObject getWorldObjectByPositionAndSize(int x, int y, int width, int height) {

		WorldObject object = null;

		for (int i = 0; i < worldObjects.size(); i++) {
			WorldObject tmpObj = worldObjects.get(i);

			if (tmpObj.getX() == x && tmpObj.getY() == y && tmpObj.getWidth() == width && tmpObj.getHeight() == height) {
				object = tmpObj;
			}

		}

		return object;
	}

	// spawnar bubblor
	public static void causeBubbleEffect(int x, int y, int width, int height, int num, int versionType, int delay) {

		for (int i = 0; i < num; i++) {
			Bubble bubble = new Bubble(x, y, width, height, versionType, delay * i);
			Main.effects.add(bubble);
		}

	}

	// hanterar information från servern om var det ska spawna en bubbleseffect
	public static void handleBubblesEffect(String message) {
		message = Client.fetchInfo(message);
		String[] info = message.split("@");

		int startX = Integer.parseInt(info[0]);
		int startY = Integer.parseInt(info[1]);
		int width = Integer.parseInt(info[2]);
		int height = Integer.parseInt(info[3]);
		int num = Integer.parseInt(info[4]);
		int versionType = Integer.parseInt(info[5]);
		int delay = Integer.parseInt(info[6]);

		MapHandler.causeBubbleEffect(startX, startY, width, height, num, versionType, delay);
	}

	public static ArrayList<Platform> getAllPlatforms() {
		ArrayList<Platform> platforms = new ArrayList<>();

		for (int i = 0; i < worldObjects.size(); i++) {

			WorldObject obj = worldObjects.get(i);

			if (obj instanceof Platform) {
				platforms.add((Platform) obj);
			}
		}

		return platforms;

	}

	public static void handleInteractionSound(String message) {
		message = Client.fetchInfo(message);

		String[] info = message.split("@");

		int x = Integer.parseInt(info[0]);
		int y = Integer.parseInt(info[1]);
		int width = Integer.parseInt(info[2]);
		int height = Integer.parseInt(info[3]);

		WorldObject obj = getWorldObjectByPositionAndSize(x, y, width, height);

		// kollar så det faktiskt är ett interactableworldobject
		if (obj instanceof InteractableWorldObject) {
			InteractableWorldObject intObj = (InteractableWorldObject) obj;
			intObj.playInteractionSound();
		}
	}

	// init som körs efter att all information om världen och spelare har skicats från servern
	public static void finalInit() {
		updateObjectiveTowerIcons();
	}

	// uppdaterar hur ikonerna för objetive towers ska sitta
	public static void updateObjectiveTowerIcons() {

		int num = MapHandler.objectiveTowerSpawns.size();

		if (num > 0) {
			int totalWidth = ObjectiveTowerSpawn.iconPanelWidth;

			int distX = totalWidth / (num + 1);
			int startX = Screen.panelWidth / 2 - totalWidth / 2;

			int centerOffset = totalWidth / num / 2;

			for (int i = 0; i < MapHandler.objectiveTowerSpawns.size(); i++) {
				ObjectiveTowerSpawn OTS = MapHandler.objectiveTowerSpawns.get(i);

				OTS.setIconX(startX + distX * i + centerOffset);
			}
		}
	}

}
