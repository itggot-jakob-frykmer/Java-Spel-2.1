package client.objects.interactableWorldObjects;

import java.awt.Graphics2D;
import java.awt.Image;

import client.Client;
import client.Main;
import client.handlers.Images;
import client.handlers.MapHandler;
import client.players.Team;

public class ObjectiveTowerSpawn {

	private int x;
	private int y;
	private int width;
	private int height;
	private Team owner;
	private ObjectiveTower tower;

	public int iconX;
	public int iconY = 130;
	public static int iconPanelWidth = 400;
	public static int iconWidth = 55;
	public static int iconHeight = 100;

	private Image icon;

	public ObjectiveTowerSpawn(int x, int y, int width, int height, int belonging) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		Team team = Main.getTeamByNumber(belonging);
		this.owner = team;

		this.tower = new ObjectiveTower(x, y, width, height, 1, "towers/objectiveTower" + belonging + ".png", -1, belonging, this);
		MapHandler.addWorldObjectToFront(tower);

		updateIcon(belonging);

	}

	// byter ägare på tornet
	public void changeOwner(Team team) {
		owner = team;
		MapHandler.removeObject(tower); // tar bort det gamla tornet

		int teamNumber = team.getTeamNumber();

		// spawnar in ett nytt torn 
		this.tower = new ObjectiveTower(x, y, width, height, 1, "towers/objectiveTower" + teamNumber + ".png", -1, teamNumber, this);
		MapHandler.addWorldObjectToFront(tower);

		updateIcon(teamNumber);
	}



	// uppdaterar ikonen
	public void updateIcon(int belonging) {
		icon = Images.readImageFromPath("ui/towerIcons/tower" + belonging + ".png");
	}

	public void paint(Graphics2D g2d) {
		g2d.drawImage(icon, iconX, iconY, iconWidth, iconHeight, null);
	}

	// säger till alla andra klienter att denna klienten nu äger detta torn
	public void sendOwner() {
		int index = MapHandler.objectiveTowerSpawns.indexOf(this);

		Client.sendData("#SENDCHANGEOBJECTIVETOWEROWNER", index + "");
	}

	public Team getOwner() {
		return owner;
	}

	public void setIconX(int x) {
		this.iconX = x;
	}

}
