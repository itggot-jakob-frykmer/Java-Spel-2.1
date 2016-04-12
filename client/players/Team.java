package client.players;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import client.Main;
import client.Screen;
import client.handlers.MapHandler;

public class Team {

	int teamNumber;
	int score;

	Color teamColor;
	String teamName;

	int startX;
	int startY;

	private boolean hasWon = false;

	private ArrayList<Player> members = new ArrayList<>();

	public Team(int teamNumber) {
		this.teamNumber = teamNumber;

		// ger laget en färg
		if (teamNumber == 0) {
			teamColor = new Color(0, 127, 255);
			teamName = "Azure";

			// var medlemmar i detta laget ska spawna
			startX = MapHandler.worldWidth - MapHandler.startX;
			startY = MapHandler.startY;
		} else {

			teamColor = new Color(228, 208, 10);
			teamName = "Citrine";

			// var medlemmar i detta laget ska spawna
			startX = MapHandler.startX;
			startY = MapHandler.startY;
		}
	}

	public void readInfoFromServer(String message) {

		members.clear();

		String[] splitOne = message.split("@");

		String[] splitPlayers = splitOne[0].split("&");

		for (int i = 0; i < splitPlayers.length; i++) {
			int playerNumber = Integer.parseInt(splitPlayers[i]);

			Player player = Main.getPlayerByNumber(playerNumber);
			members.add(player);

			player.setTeam(this);

			// om det är en själv man får information om
			if (player == Main.clientPlayer) {
				// om detta är null så är det första gången man får sitt lag tilldelat till sig
				if (Main.clientPlayer.getWeapon() == null) {
					Main.clientPlayer.pickUpWeapon(); // ger klienten ett vapen
					Main.clientPlayer.getActionBar().initAbilities(this.getTeamNumber(), Main.clientPlayer); // initialierar acitonbaren
				}
			}
		}
	}

	public void paint(Graphics2D g2d) {
		if (hasWon) {
			int x = Screen.panelWidth / 2;
			int y = 300;

			Screen.paintText(x, y, Screen.standardFont, Font.BOLD, 30, teamName + " has won!", teamColor, g2d, 1000, true);
		}
	}

	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}

	public ArrayList<Player> getMembers() {
		return members;
	}

	public int getTeamNumber() {
		return teamNumber;
	}

	public void setScore(int score) {
		this.score = score;
		checkVictory();
	}

	public void checkVictory() {
		if (score >= MapHandler.scoreWinReq) {
			hasWon = true;
		}
	}

	public int getScore() {
		return score;
	}

	public String getName() {
		return teamName;
	}

	public Color getColor() {
		return teamColor;
	}
}
