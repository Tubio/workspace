package com.accenture.salvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository , GameRepository gameRepository ,
									  GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository) {
		return (args) -> {
			//creating player examples
			Player player1 = new Player("playerone@gmail.com");
			Player player2 = new Player("playertwo@gmail.com");
			Player player3 = new Player("playerthree@gmail.com");
			Player player4 = new Player("playerfour@gmail.com");
			Player player5 = new Player("playerfive@gmail.com");

			//creating dates for some game examples
			Date date1 = new Date();
			Date date2 = Date.from(date1.toInstant().plusSeconds(3600));
			Date date3 = Date.from(date1.toInstant().plusSeconds(7200));

			//creating games
			Game game1 = new Game();
			Game game2 = new Game();
			Game game3 = new Game();

			game1.setCreationDate(date1);
			game2.setCreationDate(date2); // +1 hour
			game3.setCreationDate(date3); // +2 hour

			//creating gamePlayers
			GamePlayer gamePlayer1 = new GamePlayer();
			gamePlayer1.setGame(game1);
			gamePlayer1.setPlayer(player1);

			GamePlayer gamePlayer2 = new GamePlayer();
			gamePlayer2.setGame(game1);
			gamePlayer2.setPlayer(player2);

			GamePlayer gamePlayer3 = new GamePlayer();
			gamePlayer3.setGame(game2);
			gamePlayer3.setPlayer(player1);

			GamePlayer gamePlayer4 = new GamePlayer();
			gamePlayer4.setGame(game2);
			gamePlayer4.setPlayer(player2);

			GamePlayer gamePlayer5 = new GamePlayer();
			gamePlayer5.setGame(game3);
			gamePlayer5.setPlayer(player4);

			GamePlayer gamePlayer6 = new GamePlayer();
			gamePlayer6.setGame(game3);
			gamePlayer6.setPlayer(player5);

			//creating ships
			Ship ship1 = new Ship();
			Ship ship2 = new Ship();
			Ship ship3 = new Ship();

			//adding ship stats
			List<String> list1 = Arrays.asList("A1","A2","A3");
			List<String> list2 = Arrays.asList("B1","B2","B3");
			List<String> list3 = Arrays.asList("C1","C2","C3");

			ship1.setLocations(list1);
			ship2.setLocations(list2);
			ship3.setLocations(list3);

			ship1.setType("scout");
			ship2.setType("caravel");
			ship3.setType("submarine");

			//joining gamePlayers and ships

			ship1.setGamePlayer(gamePlayer1);
			ship2.setGamePlayer(gamePlayer2);
			ship3.setGamePlayer(gamePlayer3);

			gamePlayer1.addShip(ship1);
			gamePlayer2.addShip(ship2);
			gamePlayer3.addShip(ship3);

			//saving
			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);
			playerRepository.save(player5);

			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);


			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);
			gamePlayerRepository.save(gamePlayer3);
			gamePlayerRepository.save(gamePlayer4);
			gamePlayerRepository.save(gamePlayer5);
			gamePlayerRepository.save(gamePlayer6);

			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);

		};

	}
}