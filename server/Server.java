/*
    Här startas servern, här finns även alla allmänna funktioner och variabler


 */

package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server {

	static int playersOnline = 0;

	public static ArrayList<Integer> disconnectedPlayers = new ArrayList<>(); // håller nummer över de spelare som har disconnectat
	public static ArrayList<Integer> allPlayers = new ArrayList<>(); // håller nummer över de spelare som är och har varit anslutna
	static ArrayList<Integer> activePlayers = new ArrayList<>(); // över nummer över de spelare som är anslutna just nu

	public static ArrayList<ServerReturn> SRS = new ArrayList<>(); // hålle ServerReturn instanserna på de spelare som är anslutna

	public static ArrayList<Team> teams = new ArrayList<>();

	// public static ArrayList<Socket> ConnectionArray = new ArrayList<>(); // håller socketen på de spelare som är anslutna

	public static ArrayList IPadress = new ArrayList<>(); // ip-adresser

	static Socket SOCK;
	static ServerMap serverMap;

	public static void main(String[] args) throws IOException, IndexOutOfBoundsException, SocketException {
		try {
			final int PORT = 25565;
			ServerSocket SERVER = new ServerSocket(PORT);
			System.out.println("Waiting for clients...");
			new ServerFrame().setVisible(true);

			ServerFrame.appendConsole("Server is now running");

			serverMap = new ServerMap();
			initTeams();

			// server kollar hela tiden om någon ny klient försöker ansluta
			while (true) {
				SOCK = SERVER.accept();
				addUser(SOCK); // lägger till den nya klienten

			}
		} catch (Exception X) {
			System.out.print(X);
		}

	}

	// lägger till en ny klient
	public static void addUser(Socket SOCK) {
		Socket TEMP_SOCK = SOCK;
		int newPlayerNumber = 0;
		try {
			newPlayerNumber = playersOnline; // newPlayerNumber är det spelarnummer som den nya spelaren kommer få
			allPlayers.add(newPlayerNumber); // lägger till spelaren i listan över alla spelare

			playersOnline++;

			// skapar ny instans av Server return
			ServerReturn SR = new ServerReturn(TEMP_SOCK, newPlayerNumber);
			SRS.add(SR);

			Thread X = new Thread(SR);
			X.start();

			updateActivePlayers(); // uppdaterar listan över aktiva spelare

			int teamNumber = playersOnline % 2;

			// APN = Asign player number, skickar spelarens nummer samt vilket lag spelaren ska vara med i
			sendToClient(newPlayerNumber, "APN!" + newPlayerNumber);

			sendPlayerList(); // skickar spelarlistan till alla spelare

			ServerMap.sendWorldInfo(newPlayerNumber);
			
			// lägger till spelaren i ett lag och skickar all information om alla lag till alla spelare för att uppdatera allt
			teams.get(teamNumber).addPlayer(SR);

			

			// sendToClient(newPlayerNumber, "SSPOS!2000&4000"); // sending startposition - SSPOS&startX&startY

			// done sending informaiton
			sendToClient(newPlayerNumber, "DSI"); // done sending information, detta innebär att server har skickat all nödvändig information för att starta spelet så klienten vet när spelet kan startas

			updateTextAreas();

			ServerFrame.appendConsole("Player connected");

		} catch (IndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error 5");
		}

	}

	public static void initTeams() {
		Team team = new Team(0);
		teams.add(team);

		team = new Team(1);
		teams.add(team);
	}

	public static void closeServer() {
		sendToAllClients("CSN");
		System.exit(0);
	}

	// uppdaterar listan av aktiva spelare
	static void updateActivePlayers() {
		activePlayers.clear();
		for (int i = 0; i < allPlayers.size(); i++) {
			if (!disconnectedPlayers.contains(i)) {
				activePlayers.add(allPlayers.get(i));
			}
		}
	}

	// uppdaterar server rutans informations fält om vilka spelare som är anslutna osv
	static void updateTextAreas() {
		ServerFrame.txaPlayersOnline.setText("");
		for (int i = 0; i < activePlayers.size(); i++) {
			ServerFrame.txaPlayersOnline.append(i + ". " + activePlayers.get(i) + "    " + SRS.get(i).getDelay() + "ms \n");
		}

		ServerFrame.txaAllPlayers.setText("");
		for (int i = 0; i < allPlayers.size(); i++) {
			ServerFrame.txaAllPlayers.append(i + ". " + allPlayers.get(i) + "\n");
		}

		ServerFrame.txaDisconnected.setText("");
		for (int i = 0; i < disconnectedPlayers.size(); i++) {
			ServerFrame.txaDisconnected.append(i + ". " + disconnectedPlayers.get(i) + "\n");
		}
	}

	// skickar ett meddelande till en specificerad klient
	public static void sendToClient(int playerNumber, String message) {
		try {
			int index = activePlayers.indexOf(playerNumber);
			Socket TEMP_SOCK = SRS.get(index).getSocket();
			PrintWriter OUT;
			OUT = new PrintWriter(TEMP_SOCK.getOutputStream());
			OUT.println(message);
			OUT.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// skickar ett meddalnde till alla klienter
	public static void sendToAllClients(String message) {
		for (int i = 0; i < SRS.size(); i++) {
			try {
				Socket TEMP_SOCK = (Socket) Server.SRS.get(i).getSocket();
				PrintWriter OUT;
				OUT = new PrintWriter(TEMP_SOCK.getOutputStream());
				OUT.println(message);
				OUT.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static ServerReturn getPlayerByNumber(int playerNumber) {

		ServerReturn player = null;

		for (int i = 0; i < SRS.size(); i++) {
			if (SRS.get(i).getPlayerNumber() == playerNumber) {
				player = SRS.get(i);
			}
		}

		return player;
	}

	// skickar listan över aktiva spelare till alla klienter
	public static void sendPlayerList() {
		sendToAllClients("SPL!" + activePlayers);
		ServerFrame.appendConsole("Sending CRL: " + activePlayers);
	}

	// skickar information om alla lag till alla spelare
	public static void sendTeamsInfo() {
		for (int i = 0; i < teams.size(); i++) {
			teams.get(i).sendTeamInfoToAll();
		}
	}

}
