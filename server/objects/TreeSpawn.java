package server.objects;

import java.util.Random;

import server.Server;
import server.ServerMap;

public class TreeSpawn {

	private int x;
	private int y;
	private int width;
	private int height;
	private int delay;
	private int tickSleep;
	private int spawnChance;
	private boolean hasTree = true;

	private boolean woodSpawned = false;
	private int woodSpawnDelay = 550;
	private int woodSpawnCounter = 0;

	public TreeSpawn(int x, int y, int width, int height, int delay, int spawnChance) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.spawnChance = spawnChance;
		int rem = delay % ServerMap.sleep;
		this.delay = delay - rem;
	}

	public void update() {
		tickSleep += ServerMap.sleep;

		if (tickSleep % delay == 0) {
			Random ra = new Random();
			int randInt = ra.nextInt(100);
			// gör så att det finns en chans att det spawnar
			if (randInt < spawnChance) {
				if (!hasTree) {
					spawnTree();
				}
			}
		}

		// om det inte finns ett träd och wood inte har spawnat ännu
		if (!hasTree && !woodSpawned) {
			woodSpawnCounter += ServerMap.sleep;

			// gör så att trä inte spawnar direkt utan det kommer först när trädet har fallit 
			if (woodSpawnCounter >= woodSpawnDelay) {
				spawnWood();
				woodSpawned = true;
			}
		}

	}

	public void spawnTree() {
		hasTree = true;
		sendState();

	}

	public void despawnTree() {
		hasTree = false;
		// återställer så wood kan spawna igen
		woodSpawned = false;
		woodSpawnCounter = 0;
	}

	// skickar vilket läge objektet är i
	public void sendState() {
		String info = "SNDSTATEOFTREESPAWN!"; // sennding state of treespawn

		int index = ServerMap.treeSpawns.indexOf(this);
		int intHasTree = (hasTree ? 1 : 0);

		info += index + "@" + intHasTree;

		Server.sendToAllClients(info);

	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public boolean hasTree() {
		return hasTree;
	}

	public void setHasTree(boolean hasTree) {
		this.hasTree = hasTree;
	}

	// lägger ut wood vid ett fallet träd
	public void spawnWood() {
		int extraX = 100;
		int xInc = (int) (height * 0.3);

		int woodSize = 40;

		for (int i = 0; i < 3; i++) {
			ServerMap.addObjectLive(x + extraX + xInc * i, y + height - woodSize, woodSize, woodSize, 1, "wood", 0);
		}

	}
}
