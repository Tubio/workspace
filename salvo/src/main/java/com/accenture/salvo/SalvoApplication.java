package com.accenture.salvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository , GameRepository gameRepository ,
									  GamePlayerRepository gamePlayerRepository) {
		return (args) -> {
			//creating player examples
			Player player1 = new Player("playerone@gmail.com");
			Player player2 = new Player("playertwo@gmail.com");
			Player player3 = new Player("playerthree@gmail.com");
			Player player4 = new Player("playerfour@gmail.com");
			Player player5 = new Player("playerfive@gmail.com");

			//Saving some player examples
			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);
			playerRepository.save(player5);

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

			//saving game examples
			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);

			//saving some gamePlayer examples
			GamePlayer gamePlayer1 = new GamePlayer();
			gamePlayer1.setGame(game1);
			gamePlayer1.setPlayer(player1);
			gamePlayerRepository.save(gamePlayer1);

			GamePlayer gamePlayer2 = new GamePlayer();
			gamePlayer2.setGame(game1);
			gamePlayer2.setPlayer(player2);
			gamePlayerRepository.save(gamePlayer2);

			GamePlayer gamePlayer3 = new GamePlayer();
			gamePlayer3.setGame(game2);
			gamePlayer3.setPlayer(player1);
			gamePlayerRepository.save(gamePlayer3);

			GamePlayer gamePlayer4 = new GamePlayer();
			gamePlayer4.setGame(game2);
			gamePlayer4.setPlayer(player2);
			gamePlayerRepository.save(gamePlayer4);

			GamePlayer gamePlayer5 = new GamePlayer();
			gamePlayer5.setGame(game3);
			gamePlayer5.setPlayer(player4);
			gamePlayerRepository.save(gamePlayer5);

			GamePlayer gamePlayer6 = new GamePlayer();
			gamePlayer6.setGame(game3);
			gamePlayer6.setPlayer(player5);
			gamePlayerRepository.save(gamePlayer6);

		};
	}
}