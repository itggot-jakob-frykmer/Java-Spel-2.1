package server;

import java.util.ArrayList;

public class Team {

	private int teamNumber;
	private int score = 0;

	
	
	private ArrayList<ServerReturn> members = new ArrayList<>();

	public Team(int teamNumber) {
		this.teamNumber = teamNumber;
	}

	public void addPlayer(ServerReturn player) {
		members.add(player);
		player.setTeam(this);

		Server.sendTeamsInfo();
		sendScore();
	}

	public void removePlayer(ServerReturn player) {
		members.remove(player);

		Server.sendTeamsInfo();
	}

	public String getTeamInfo() {
		String message = "SENDINGTEAMINFO!";

		message += teamNumber + "@";

		for (int i = 0; i < members.size(); i++) {
			message += members.get(i).getPlayerNumber() + "&";
		}
		return message;
	}

	public void sendTeamInfoToClient(int playerNumber) {
		String message = getTeamInfo();
		Server.sendToClient(playerNumber, message);
	}

	public void sendTeamInfoToAll() {
		String message = getTeamInfo();
		Server.sendToAllClients(message);
	}

	public void addScore(int amount) {
		score += amount;
		// om laget får så mycket score man behöver för att vinna
		if (score >= ServerMap.scoreWinReq) {
			
		}
		sendScore();
	}

	public void sendScore() {
		String info = "SENDTEAMSCORE!";

		info += teamNumber + "@";
		info += score + "@";

		Server.sendToAllClients(info);
	}

	public int getTeamNumber() {
		return teamNumber;
	}

}
