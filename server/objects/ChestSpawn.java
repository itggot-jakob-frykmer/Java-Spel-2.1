package server.objects;

import java.util.Random;

import server.ServerMap;

// Ett ChestSpawn är en plats där kistor kan spawna. Servern ansvarar för att spawna in kistor på olika bestäma platser som bestäms där man laddar in kartan i ServerMap
// När en klient öppnar en kista skickar hen denna information till servern så detta spawn vet att dens kista har blivit öppnad och kan spawna en ny efter en stund. 
public class ChestSpawn {

	private int x;
	private int y;
	private int width;
	private int height;

	private int spawnChance;
	private int interval;

	private boolean hasChest = false; // om det finns en kista vid detta spawnet nu

	private int intervalCounter = 0;

	private int currentChestId = 0; // vilket ID den kistan som finns vid spawnet just nu har. Behövs för när en klient skickar info om att en kista har öppnats för att veta vilket spawn kistan tillhör

	Random ra = new Random();

	public ChestSpawn(int x, int y, int width, int height, int spawnChance, int interval, boolean spawnWithChest) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.spawnChance = spawnChance;
		this.interval = interval;
		
		//om det ska finnas en kista från början
		if (spawnWithChest) {
			spawnChest();
		}

	}

	public void update() {

		// om det inte finns någon kista vid dett spawn
		if (!hasChest) {
			intervalCounter += ServerMap.sleep;
			if (intervalCounter >= interval) {
				intervalCounter = 0;
				int randInt = ra.nextInt(100);

				if (randInt <= spawnChance) {
					spawnChest();
				}

			}
		}

	}

	public int getCurrentChestId() {
		return currentChestId;
	}

	public void setHasChest(boolean hasChest) {
		this.hasChest = hasChest;
	}

	public void spawnChest() {
		setHasChest(true);
		WorldObject obj = ServerMap.addObjectLive(x, y, width, height, 1, "chest", 0);
		currentChestId = obj.getObjectId();
	}
}
