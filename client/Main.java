package client;

import java.util.ArrayList;
import java.util.Collections;

import client.effects.Effect;
import client.handlers.MapHandler;
import client.objects.WorldObject;
import client.players.OtherPlayer;
import client.players.Player;
import client.players.Team;
import client.players.YourPlayer;
import client.ui.mainMenu.MainMenu;

// Huvudklassen som ansvara för mainloopet och vissa listor.
public class Main {

	static ArrayList<Player> players = new ArrayList<Player>();
	static ArrayList<Integer> activePlayersNumber = new ArrayList<>(); // denna lista används för att hålla ordning på spelarnas globa splarnummer

	public static ArrayList<Effect> effects = new ArrayList<>();

	public static ArrayList<Team> teams = new ArrayList<>();

	public static YourPlayer clientPlayer;

	public static MainMenu mainMenu;

	// olika game states
	public static enum States {
		inMainMenu, inLoadingScreen, inGame;
	}

	public static States state;

	static void addPlayer(Player player) {
		players.add(player);
	}

	static void removePlayer() {

	}

	// Mainloop
	static void update() {

		if (mainMenu != null) {
			mainMenu.update();
		}

		updatePlayers();

		updateWorldObjects();

		updateEffects();

		Screen.updateScreen();

		if (Screen.tick % 100000 == 0) {
			System.gc();
		}
	}

	public static Player getPlayerByNumber(int playerNumber) {

		Player player = new OtherPlayer(-1, 0); // temp

		int index = activePlayersNumber.indexOf(playerNumber);
		if (index != -1) {
			player = players.get(index);
		}

		return player;
	}

	public static void updateEffects() {
		for (int i = 0; i < effects.size(); i++) {
			Effect efc = effects.get(i);
			efc.update();
		}
	}

	public static void updateWorldObjects() {
		for (int i = 0; i < MapHandler.worldObjects.size(); i++) {
			WorldObject obj = MapHandler.worldObjects.get(i);
			if (obj != null) {
				obj.update();
			}
		}
	}

	public static void updatePlayers() {
		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			player.update();
		}
	}

	public static void pushToFront(ArrayList<Object> arrayList, Object obj) {
		Collections.reverse(arrayList);
		arrayList.add(obj);
		Collections.reverse(arrayList);
	}

	public static Team getTeamByNumber(int teamNumber) {

		Team team = null;

		for (int i = 0; i < teams.size(); i++) {
			if (teams.get(i).getTeamNumber() == teamNumber) {
				team = teams.get(i);
			}

		}
		return team;
	}

	// hanterar information om en spelares vapen
	public static void handleWeaponTypeFromClient(String message) {
		int playerNumber = Client.fetchPlayerNumber(message);

		Player player = Main.getPlayerByNumber(playerNumber);

		String info = Client.fetchInfo(message);

		// om none skickas har man inget vapen
		if (info.equals("none")) {
			player.setWeapon(null);
		} else {
			String[] infoSplit = info.split("@");

			int versionType = Integer.parseInt(infoSplit[0]);
			int level = Integer.parseInt(infoSplit[1]);

			player.setWeapon(versionType, level);
		}
	}

	public static void handleTeamScoreFromServer(String message) {
		String[] info = message.split("@"); // delar upp infon

		int teamNumber = Integer.parseInt(info[0]);
		int score = Integer.parseInt(info[1]);

		Team team = getTeamByNumber(teamNumber);
		if (team != null) {
			team.setScore(score);
		}
	}

}
