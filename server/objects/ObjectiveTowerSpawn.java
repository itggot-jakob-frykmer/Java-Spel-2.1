package server.objects;

import server.ServerMap;
import server.Team;

public class ObjectiveTowerSpawn {

	private int x;
	private int y;
	private int width;
	private int height;
	Team belongingTeam;

	private int gainPointDelay = 1000;
	private int gainPointAmount = 1;
	private int gainCounter = 0;

	public ObjectiveTowerSpawn(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.belongingTeam = null;
	}

	public String getInfo() {
		String info = "";

		int teamOwnerNumber;

		// om ingen äger det här tornet är det neutralt och då är teamnumret 2
		if (belongingTeam != null) {
			teamOwnerNumber = belongingTeam.getTeamNumber();
		} else {
			teamOwnerNumber = 2;
		}

		info += x + "@";
		info += y + "@";
		info += width + "@";
		info += height + "@";
		info += teamOwnerNumber;

		return info;
	}

	public void setOwner(Team team) {
		this.belongingTeam = team;
	}

	public void update() {
		// om den tillhör något lag
		if (belongingTeam != null) {
			gainCounter += ServerMap.sleep;

			// lägger till score på ett intervall
			if (gainCounter >= gainPointDelay) {

				belongingTeam.addScore(gainPointAmount);
				gainCounter = 0;
			}
		}

	}
}
