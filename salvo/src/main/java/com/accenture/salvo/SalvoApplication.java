package com.accenture.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository , GameRepository gameRepository ,
									  GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository,
									  SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {

			List<Player> playerList = createPlayers();
			List<Game> gameList = createGames();
			List<GamePlayer> gamePlayerList = createGamePlayers(playerList,gameList);
			List<Ship> shipList = createShips();
			List<Salvo> salvoList = createSalvoes();
			List<Score> scoreList = createScores();

			connectGamePlayersAndShips(gamePlayerList,shipList);
			connectGamePlayersAndSalvoes(gamePlayerList,salvoList);

			playerList.forEach(playerRepository::save);
			gameList.forEach(gameRepository::save);
			gamePlayerList.forEach(gamePlayerRepository::save);
			shipList.forEach(shipRepository::save);
			salvoList.forEach(salvoRepository::save);

			//solo para scores
			gameList.get(0).addScore(scoreList.get(0));
			gameList.get(0).addScore(scoreList.get(1));
			playerList.get(0).addScore(scoreList.get(0));
			playerList.get(0).addScore(scoreList.get(1));
			scoreRepository.save(scoreList.get(0));
			scoreRepository.save(scoreList.get(1));

			};

	}

	//private methods

	//this methods are used to create examples for every repository
	private List<Player> createPlayers() {

		List<Player> playerList = new ArrayList<>();

		Player player1 = new Player("j.bauer@ctu.gov");
		Player player2 = new Player("c.obrian@ctu.gov");
		Player player3 = new Player("kim_bauer@gmail.com");
		Player player4 = new Player("t.almeida@ctu.gov");

		//adding passwords to players
		player1.setPassword("24");
		player2.setPassword("42");
		player3.setPassword("kb");
		player4.setPassword("mole");

		playerList.add(player1);
		playerList.add(player2);
		playerList.add(player3);
		playerList.add(player4);

		return playerList;
	}

	private List<Game> createGames() {

		List<Game> gameList = new ArrayList<>();

		Game game1 = new Game();
		Game game2 = new Game();
		Game game3 = new Game();
		Game game4 = new Game();
		Game game5 = new Game();
		Game game6 = new Game();
		Game game7 = new Game();
		Game game8 = new Game();

		gameList.add(game1);
		gameList.add(game2);
		gameList.add(game3);
		gameList.add(game4);
		gameList.add(game5);
		gameList.add(game6);
		gameList.add(game7);
		gameList.add(game8);

		createDates(gameList); // game1 has the actual date as creationDate,
							// gameN has 'N' hours after game1 as creationDate

		return gameList;
	}

	private void createDates(List<Game> gameList) {

		Date date1 = new Date();
		Date date2 = Date.from(date1.toInstant().plusSeconds(3600));
		Date date3 = Date.from(date1.toInstant().plusSeconds(3600*2));
		Date date4 = Date.from(date1.toInstant().plusSeconds(3600*3));
		Date date5 = Date.from(date1.toInstant().plusSeconds(3600*4));
		Date date6 = Date.from(date1.toInstant().plusSeconds(3600*5));
		Date date7 = Date.from(date1.toInstant().plusSeconds(3600*6));
		Date date8 = Date.from(date1.toInstant().plusSeconds(3600*7));

		gameList.get(0).setCreationDate(date1);
		gameList.get(1).setCreationDate(date2);
		gameList.get(2).setCreationDate(date3);
		gameList.get(3).setCreationDate(date4);
		gameList.get(4).setCreationDate(date5);
		gameList.get(5).setCreationDate(date6);
		gameList.get(6).setCreationDate(date7);
		gameList.get(7).setCreationDate(date8);
	}

	private List<GamePlayer> createGamePlayers(List<Player> playerList, List<Game> gameList){

		List<GamePlayer> gamePlayerList = new ArrayList<>();

		gamePlayerList.add( new GamePlayer(gameList.get(0), playerList.get(0)));
		gamePlayerList.add( new GamePlayer(gameList.get(0), playerList.get(1)));
		gamePlayerList.add( new GamePlayer(gameList.get(1), playerList.get(0)));
		gamePlayerList.add( new GamePlayer(gameList.get(1), playerList.get(1)));
		gamePlayerList.add( new GamePlayer(gameList.get(2), playerList.get(1)));
		gamePlayerList.add( new GamePlayer(gameList.get(2), playerList.get(3)));
		gamePlayerList.add( new GamePlayer(gameList.get(3), playerList.get(1)));
		gamePlayerList.add( new GamePlayer(gameList.get(3), playerList.get(0)));
		gamePlayerList.add( new GamePlayer(gameList.get(4), playerList.get(3)));
		gamePlayerList.add( new GamePlayer(gameList.get(4), playerList.get(0)));
		gamePlayerList.add( new GamePlayer(gameList.get(5), playerList.get(2)));
		gamePlayerList.add( new GamePlayer(gameList.get(6), playerList.get(3)));
		gamePlayerList.add( new GamePlayer(gameList.get(7), playerList.get(2)));
		gamePlayerList.add( new GamePlayer(gameList.get(7), playerList.get(3)));

		return gamePlayerList;
	}

	private List<Ship> createShips() {

		List<Ship> shipList = new ArrayList<>();

		//creating ships
		Ship ship1 = new Ship();
		Ship ship2 = new Ship();
		Ship ship3 = new Ship();
		Ship ship4 = new Ship();
		Ship ship5 = new Ship();
		Ship ship6 = new Ship();
		Ship ship7 = new Ship();
		Ship ship8 = new Ship();
		Ship ship9 = new Ship();
		Ship ship10 = new Ship();
		Ship ship11 = new Ship();
		Ship ship12 = new Ship();
		Ship ship13 = new Ship();
		Ship ship14 = new Ship();
		Ship ship15 = new Ship();
		Ship ship16 = new Ship();
		Ship ship17 = new Ship();
		Ship ship18 = new Ship();
		Ship ship19 = new Ship();
		Ship ship20 = new Ship();
		Ship ship21 = new Ship();
		Ship ship22 = new Ship();
		Ship ship23 = new Ship();
		Ship ship24 = new Ship();
		Ship ship25 = new Ship();
		Ship ship26 = new Ship();
		Ship ship27 = new Ship();

		//setting ship stats
		String[] locations1 = {"H2","H3","H4"};
		ship1.setType("Destroyer");
		ship1.addLocations(locations1);

		String[] locations2 = {"E1","F1","G1"};
		ship2.setType("Submarine");
		ship2.addLocations(locations2);

		String[] locations3 = {"B4","B5"};
		ship3.setType("Patrol Boat");
		ship3.addLocations(locations3);

		String[] locations4 = {"B5","C5","D5"};
		ship4.setType("Destroyer");
		ship4.addLocations(locations4);

		String[] locations5 = {"F1","F2"};
		ship5.setType("Patrol Boat");
		ship5.addLocations(locations5);

		String[] locations6 = {"B5","C5","D5"};
		ship6.setType("Destoyer");
		ship6.addLocations(locations6);

		String[] locations7 = {"C6","C7"};
		ship7.setType("Patrol Boat");
		ship7.addLocations(locations7);

		String[] locations8 = {"A2","A3","A4"};
		ship8.setType("Submarine");
		ship8.addLocations(locations8);

		String[] locations9 = {"G6","H6"};
		ship9.setType("Patrol Boat");
		ship9.addLocations(locations9);

		String[] locations10 = {"B5","C5","D5"};
		ship10.setType("Destroyer");
		ship10.addLocations(locations10);

		String[] locations11 = {"C6","C7"};
		ship11.setType("Patrol Boat");
		ship11.addLocations(locations11);

		String[] locations12 = {"A2","A3","A4"};
		ship12.setType("Submarine");
		ship12.addLocations(locations12);

		String[] locations13 = {"G6","H6"};
		ship13.setType("Patrol Boat");
		ship13.addLocations(locations13);

		String[] locations14 = {"B5","C5","D5"};
		ship14.setType("Destroyer");
		ship14.addLocations(locations14);

		String[] locations15 = {"C6","C7"};
		ship15.setType("Patrol Boat");
		ship15.addLocations(locations15);

		String[] locations16 = {"A2","A3","A4"};
		ship16.setType("Submarine");
		ship16.addLocations(locations16);

		String[] locations17 = {"G6","H6"};
		ship17.setType("Patrol Boat");
		ship17.addLocations(locations17);

		String[] locations18 = {"B5","C5","D5"};
		ship18.setType("Destroyer");
		ship18.addLocations(locations18);

		String[] locations19 = {"C6","C7"};
		ship19.setType("Patrol Boat");
		ship19.addLocations(locations19);

		String[] locations20 = {"A2","A3","A4"};
		ship20.setType("Submarine");
		ship20.addLocations(locations20);

		String[] locations21 = {"G6","H6"};
		ship21.setType("Patrol Boat");
		ship21.addLocations(locations21);

		String[] locations22 = {"B5","C5","D5"};
		ship22.setType("Destroyer");
		ship22.addLocations(locations22);

		String[] locations23 = {"C6","C7"};
		ship23.setType("Patrol Boat");
		ship23.addLocations(locations23);

		String[] locations24 = {"B5","C5","D5"};
		ship24.setType("Destoyer");
		ship24.addLocations(locations24);

		String[] locations25 = {"C6","C7"};
		ship25.setType("Patrol Boat");
		ship25.addLocations(locations25);

		String[] locations26 = {"A2","A3","A4"};
		ship26.setType("Submarine");
		ship26.addLocations(locations26);

		String[] locations27 = {"G6","H6"};
		ship27.setType("Patrol Boat");
		ship27.addLocations(locations27);

		shipList.add(ship1);
		shipList.add(ship2);
		shipList.add(ship3);
		shipList.add(ship4);
		shipList.add(ship5);
		shipList.add(ship6);
		shipList.add(ship7);
		shipList.add(ship8);
		shipList.add(ship9);
		shipList.add(ship10);
		shipList.add(ship11);
		shipList.add(ship12);
		shipList.add(ship13);
		shipList.add(ship14);
		shipList.add(ship15);
		shipList.add(ship16);
		shipList.add(ship17);
		shipList.add(ship18);
		shipList.add(ship19);
		shipList.add(ship20);
		shipList.add(ship21);
		shipList.add(ship22);
		shipList.add(ship23);
		shipList.add(ship24);
		shipList.add(ship25);
		shipList.add(ship26);
		shipList.add(ship27);

		return shipList;
	}

	private List<Salvo> createSalvoes() {

		List<Salvo> salvoList = new ArrayList<>();

		//creating salvoes and adding attributes
		Salvo salvo1 = new Salvo();
		salvo1.setTurnNumber(1);
		salvo1.setLocations(new ArrayList<>(Arrays.asList("B5", "C5", "F1")));

		Salvo salvo2 = new Salvo();
		salvo2.setTurnNumber(1);
		salvo2.setLocations(new ArrayList<>(Arrays.asList("B4", "B5", "B6")));

		Salvo salvo3 = new Salvo();
		salvo3.setTurnNumber(2);
		salvo3.setLocations(new ArrayList<>(Arrays.asList("F2", "D5")));

		Salvo salvo4 = new Salvo();
		salvo4.setTurnNumber(2);
		salvo4.setLocations(new ArrayList<>(Arrays.asList("E1", "H3", "A2")));

		Salvo salvo5 = new Salvo();
		salvo5.setTurnNumber(1);
		salvo5.setLocations(new ArrayList<>(Arrays.asList("A2", "A4", "G6")));

		Salvo salvo6 = new Salvo();
		salvo6.setTurnNumber(1);
		salvo6.setLocations(new ArrayList<>(Arrays.asList("B5", "D5", "C7")));

		Salvo salvo7 = new Salvo();
		salvo7.setTurnNumber(2);
		salvo7.setLocations(new ArrayList<>(Arrays.asList("B5", "C5", "F1")));

		Salvo salvo8 = new Salvo();
		salvo8.setTurnNumber(2);
		salvo8.setLocations(new ArrayList<>(Arrays.asList("A3", "H6")));

		Salvo salvo9 = new Salvo();
		salvo9.setTurnNumber(1);
		salvo9.setLocations(new ArrayList<>(Arrays.asList("G6", "H6", "A4")));

		Salvo salvo10 = new Salvo();
		salvo10.setTurnNumber(1);
		salvo10.setLocations(new ArrayList<>(Arrays.asList("H1", "H2", "H3")));

		Salvo salvo11 = new Salvo();
		salvo11.setTurnNumber(2);
		salvo11.setLocations(new ArrayList<>(Arrays.asList("A2", "A3", "D8")));

		Salvo salvo12 = new Salvo();
		salvo12.setTurnNumber(2);
		salvo12.setLocations(new ArrayList<>(Arrays.asList("E1", "F2", "G3")));

		Salvo salvo13 = new Salvo();
		salvo13.setTurnNumber(1);
		salvo13.setLocations(new ArrayList<>(Arrays.asList("A3", "A4", "F7")));

		Salvo salvo14 = new Salvo();
		salvo14.setTurnNumber(2);
		salvo14.setLocations(new ArrayList<>(Arrays.asList("B5", "C6", "H1")));

		Salvo salvo15 = new Salvo();
		salvo15.setTurnNumber(2);
		salvo15.setLocations(new ArrayList<>(Arrays.asList("A2", "G6", "H6")));

		Salvo salvo16 = new Salvo();
		salvo16.setTurnNumber(2);
		salvo16.setLocations(new ArrayList<>(Arrays.asList("C5", "C7", "D5")));

		Salvo salvo17 = new Salvo();
		salvo17.setTurnNumber(1);
		salvo17.setLocations(new ArrayList<>(Arrays.asList("A1", "A2", "A3")));

		Salvo salvo18 = new Salvo();
		salvo18.setTurnNumber(1);
		salvo18.setLocations(new ArrayList<>(Arrays.asList("B5", "B6", "C7")));

		Salvo salvo19 = new Salvo();
		salvo19.setTurnNumber(2);
		salvo19.setLocations(new ArrayList<>(Arrays.asList("G6", "G7", "G8")));

		Salvo salvo20 = new Salvo();
		salvo20.setTurnNumber(2);
		salvo20.setLocations(new ArrayList<>(Arrays.asList("C6", "D6", "E6")));

		Salvo salvo21 = new Salvo();
		salvo21.setTurnNumber(3);
		salvo21.setLocations(new ArrayList<>(Arrays.asList("H1", "H8")));

		salvoList.add(salvo1);
		salvoList.add(salvo2);
		salvoList.add(salvo3);
		salvoList.add(salvo4);
		salvoList.add(salvo5);
		salvoList.add(salvo6);
		salvoList.add(salvo7);
		salvoList.add(salvo8);
		salvoList.add(salvo9);
		salvoList.add(salvo10);
		salvoList.add(salvo11);
		salvoList.add(salvo12);
		salvoList.add(salvo13);
		salvoList.add(salvo14);
		salvoList.add(salvo15);
		salvoList.add(salvo16);
		salvoList.add(salvo17);
		salvoList.add(salvo18);
		salvoList.add(salvo19);
		salvoList.add(salvo20);
		salvoList.add(salvo21);

		return salvoList;
	}

	private List<Score> createScores(){

		List<Score> scoreList = new ArrayList<>();

		Score score1 = new Score();
		score1.setScore(1d);

		Score score2 = new Score();
		score2.setScore(0d);

		scoreList.add(score1);
		scoreList.add(score2);

		return scoreList;
	}

	private void connectGamePlayersAndShips(List<GamePlayer> gamePlayerList, List<Ship> shipList) {

		shipList.get(0).setGamePlayer(gamePlayerList.get(0));
		shipList.get(1).setGamePlayer(gamePlayerList.get(0));
		shipList.get(2).setGamePlayer(gamePlayerList.get(0));
		shipList.get(3).setGamePlayer(gamePlayerList.get(1));
		shipList.get(4).setGamePlayer(gamePlayerList.get(1));
		shipList.get(5).setGamePlayer(gamePlayerList.get(2));
		shipList.get(6).setGamePlayer(gamePlayerList.get(2));
		shipList.get(7).setGamePlayer(gamePlayerList.get(3));
		shipList.get(8).setGamePlayer(gamePlayerList.get(3));
		shipList.get(9).setGamePlayer(gamePlayerList.get(4));
		shipList.get(10).setGamePlayer(gamePlayerList.get(4));
		shipList.get(11).setGamePlayer(gamePlayerList.get(5));
		shipList.get(12).setGamePlayer(gamePlayerList.get(5));
		shipList.get(13).setGamePlayer(gamePlayerList.get(6));
		shipList.get(14).setGamePlayer(gamePlayerList.get(6));
		shipList.get(15).setGamePlayer(gamePlayerList.get(7));
		shipList.get(16).setGamePlayer(gamePlayerList.get(7));
		shipList.get(17).setGamePlayer(gamePlayerList.get(8));
		shipList.get(18).setGamePlayer(gamePlayerList.get(8));
		shipList.get(19).setGamePlayer(gamePlayerList.get(9));
		shipList.get(20).setGamePlayer(gamePlayerList.get(9));
		shipList.get(21).setGamePlayer(gamePlayerList.get(10));
		shipList.get(22).setGamePlayer(gamePlayerList.get(10));
		shipList.get(23).setGamePlayer(gamePlayerList.get(12));
		shipList.get(24).setGamePlayer(gamePlayerList.get(12));
		shipList.get(25).setGamePlayer(gamePlayerList.get(13));
		shipList.get(26).setGamePlayer(gamePlayerList.get(13));

	}

	private void connectGamePlayersAndSalvoes(List<GamePlayer> gamePlayerList, List<Salvo> salvoList) {

		salvoList.get(0).setGamePlayer(gamePlayerList.get(0));
		salvoList.get(1).setGamePlayer(gamePlayerList.get(1));
		salvoList.get(2).setGamePlayer(gamePlayerList.get(0));
		salvoList.get(3).setGamePlayer(gamePlayerList.get(1));

		salvoList.get(4).setGamePlayer(gamePlayerList.get(2));
		salvoList.get(5).setGamePlayer(gamePlayerList.get(3));
		salvoList.get(6).setGamePlayer(gamePlayerList.get(2));
		salvoList.get(7).setGamePlayer(gamePlayerList.get(3));

		salvoList.get(8).setGamePlayer(gamePlayerList.get(4));
		salvoList.get(9).setGamePlayer(gamePlayerList.get(5));
		salvoList.get(10).setGamePlayer(gamePlayerList.get(4));
		salvoList.get(11).setGamePlayer(gamePlayerList.get(5));

		salvoList.get(12).setGamePlayer(gamePlayerList.get(6));
		salvoList.get(13).setGamePlayer(gamePlayerList.get(7));
		salvoList.get(14).setGamePlayer(gamePlayerList.get(6));
		salvoList.get(15).setGamePlayer(gamePlayerList.get(7));

		salvoList.get(16).setGamePlayer(gamePlayerList.get(8));
		salvoList.get(17).setGamePlayer(gamePlayerList.get(9));
		salvoList.get(18).setGamePlayer(gamePlayerList.get(8));
		salvoList.get(19).setGamePlayer(gamePlayerList.get(9));
		salvoList.get(20).setGamePlayer(gamePlayerList.get(8));

	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputUserName -> {
			Player player = playerRepository.findByUserName(inputUserName);
			if(player != null) {
				return new User(player.getEmail(), player.getPassword(), AuthorityUtils.createAuthorityList("USER"));
			} else
				throw new UsernameNotFoundException("Unknown user: "+ inputUserName);
		});
	}

	@Configuration
	@EnableWebSecurity
	class webSecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()
					.antMatchers("/rest/**").hasAuthority("ADMIN")
					.antMatchers("/web/js/game.js").hasAuthority("USER")
					.antMatchers("/web/game.html").hasAuthority("USER")
					.antMatchers("/web/**").permitAll()
					.antMatchers("/api/game_view/**").hasAuthority("USER")
					.antMatchers("/api/**").permitAll()
					.anyRequest().authenticated();

			http.formLogin()
					.usernameParameter("username")
					.passwordParameter("password")
					.loginPage("/api/login");

			http.logout().logoutUrl("/api/logout");

			// turn off checking for CSRF tokens
			http.csrf().disable();

			// if user is not authenticated, just send an authentication failure response
			http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

			// if login is successful, just clear the flags asking for authentication
			http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

			// if login fails, just send an authentication failure response
			http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

			// if logout is successful, just send a success response
			http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

		}

		private void clearAuthenticationAttributes(HttpServletRequest request) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
			}
		}

	}
}

