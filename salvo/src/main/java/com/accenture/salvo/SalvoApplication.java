package com.accenture.salvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import java.util.*;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository , GameRepository gameRepository ,
									  GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository,
									  SalvoRepository salvoRepository) {
		return (args) -> {

			List<Player> playerList = createPlayers();
			List<Game> gameList = createGames();
			List<GamePlayer> gamePlayerList = createGamePlayers(playerList,gameList);
			List<Ship> shipList = createShips();
			List<Salvo> salvoList = createSalvoes();

			connectGamePlayersAndShips(gamePlayerList,shipList);
			connectGamePlayersAndSalvoes(gamePlayerList,salvoList);

			playerList.forEach(playerRepository::save);
			gameList.forEach(gameRepository::save);
			gamePlayerList.forEach(gamePlayerRepository::save);
			shipList.forEach(shipRepository::save);
			salvoList.forEach(salvoRepository::save);

			};

	}

	//private methods

	//this methods are used to create examples for every repository
	private List<Player> createPlayers() {

		List<Player> playerList = new ArrayList<>();

		Player player1 = new Player("playerone@gmail.com");
		Player player2 = new Player("playertwo@gmail.com");
		Player player3 = new Player("playerthree@gmail.com");
		Player player4 = new Player("playerfour@gmail.com");
		Player player5 = new Player("playerfive@gmail.com");

		playerList.add(player1);
		playerList.add(player2);
		playerList.add(player3);
		playerList.add(player4);
		playerList.add(player5);

		return playerList;
	}

	private List<Game> createGames() {

		List<Game> gameList = new ArrayList<>();

		Game game1 = new Game();
		Game game2 = new Game();
		Game game3 = new Game();

		gameList.add(game1);
		gameList.add(game2);
		gameList.add(game3);

		createDates(gameList);

		return gameList;
	}

	private void createDates(List<Game> gameList) {

		Date date1 = new Date();
		Date date2 = Date.from(date1.toInstant().plusSeconds(3600));
		Date date3 = Date.from(date1.toInstant().plusSeconds(7200));

		gameList.get(0).setCreationDate(date1);
		gameList.get(1).setCreationDate(date2);
		gameList.get(2).setCreationDate(date3);
	}

	private List<GamePlayer> createGamePlayers(List<Player> playerList, List<Game> gameList){

		List<GamePlayer> gamePlayerList = new ArrayList<>();

		GamePlayer gamePlayer1 = new GamePlayer();
		gamePlayer1.setGame(gameList.get(0));
		gamePlayer1.setPlayer(playerList.get(0));

		GamePlayer gamePlayer2 = new GamePlayer();
		gamePlayer2.setGame(gameList.get(0));
		gamePlayer2.setPlayer(playerList.get(1));

		GamePlayer gamePlayer3 = new GamePlayer();
		gamePlayer3.setGame(gameList.get(1));
		gamePlayer3.setPlayer(playerList.get(0));

		GamePlayer gamePlayer4 = new GamePlayer();
		gamePlayer4.setGame(gameList.get(1));
		gamePlayer4.setPlayer(playerList.get(1));

		GamePlayer gamePlayer5 = new GamePlayer();
		gamePlayer5.setGame(gameList.get(2));
		gamePlayer5.setPlayer(playerList.get(3));

		GamePlayer gamePlayer6 = new GamePlayer();
		gamePlayer6.setGame(gameList.get(2));
		gamePlayer6.setPlayer(playerList.get(4));

		gamePlayerList.add(gamePlayer1);
		gamePlayerList.add(gamePlayer2);
		gamePlayerList.add(gamePlayer3);
		gamePlayerList.add(gamePlayer4);
		gamePlayerList.add(gamePlayer5);
		gamePlayerList.add(gamePlayer6);

		return gamePlayerList;
	}

	private List<Ship> createShips() {

		List<Ship> shipList = new ArrayList<>();

		//creating ships
		Ship ship1 = new Ship();
		Ship ship2 = new Ship();
		Ship ship3 = new Ship();

		//setting ship stats
		List<String> list1 = Arrays.asList("A1","A2","A3");
		List<String> list2 = Arrays.asList("B1","B2","B3");
		List<String> list3 = Arrays.asList("C1","C2","C3");

		ship1.setLocations(list1);
		ship2.setLocations(list2);
		ship3.setLocations(list3);

		ship1.setType("scout");
		ship2.setType("caravel");
		ship3.setType("submarine");

		shipList.add(ship1);
		shipList.add(ship2);
		shipList.add(ship3);

		return shipList;
	}

	private List<Salvo> createSalvoes() {

		List<Salvo> salvoList = new ArrayList<>();

		//creating salvoes
		Salvo salvo1 = new Salvo();
		Salvo salvo2 = new Salvo();
		Salvo salvo3 = new Salvo();
		Salvo salvo4 = new Salvo();
		Salvo salvo5 = new Salvo();

		//adding salvoes stats
		salvo1.setLocations(new ArrayList<> (Arrays.asList("A1","A2","D4","D5","E6","C1")));
		salvo1.setTurnNumber(1);
		salvo2.setLocations(new ArrayList<> (Arrays.asList("C1","A2","B4","A5","B6","D1")));
		salvo2.setTurnNumber(2);
		salvo3.setLocations(new ArrayList<> (Arrays.asList("D1","E2","C4","F5","E6","E1")));
		salvo3.setTurnNumber(1);
		salvo4.setLocations(new ArrayList<> (Arrays.asList("E1","E2","D4","D5","F6","F1")));
		salvo4.setTurnNumber(2);
		salvo5.setLocations(new ArrayList<> (Arrays.asList("F1","F2","E4","E5","E6","C1")));
		salvo5.setTurnNumber(3);

		salvoList.add(salvo1);
		salvoList.add(salvo2);
		salvoList.add(salvo3);
		salvoList.add(salvo4);
		salvoList.add(salvo5);

		return salvoList;
	}

	private void connectGamePlayersAndShips(List<GamePlayer> gamePlayerList, List<Ship> shipList) {

		shipList.get(0).setGamePlayer(gamePlayerList.get(0));
		shipList.get(1).setGamePlayer(gamePlayerList.get(1));
		shipList.get(2).setGamePlayer(gamePlayerList.get(2));

		gamePlayerList.get(0).addShip(shipList.get(0));
		gamePlayerList.get(1).addShip(shipList.get(1));
		gamePlayerList.get(2).addShip(shipList.get(2));
	}

	private void connectGamePlayersAndSalvoes(List<GamePlayer> gamePlayerList, List<Salvo> salvoList) {

		salvoList.get(0).setGamePlayer(gamePlayerList.get(0));
		salvoList.get(1).setGamePlayer(gamePlayerList.get(1));
		salvoList.get(2).setGamePlayer(gamePlayerList.get(2));

		gamePlayerList.get(0).addSalvo(salvoList.get(0));
		gamePlayerList.get(1).addSalvo(salvoList.get(1));
		gamePlayerList.get(2).addSalvo(salvoList.get(2));
	}

}