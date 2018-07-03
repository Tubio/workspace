package com.accenture.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String userName;

    @Ignore
    private String password;

    @OneToMany(mappedBy = "player",fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player",fetch = FetchType.EAGER)
    Set<Score> scores;

    public Player() {

        gamePlayers = new HashSet<>();
        scores = new HashSet<>();
    }

    public Player(String userName) {

        this.userName = userName;
        this.gamePlayers = new HashSet<>();
        this.scores = new HashSet<>();
    }

    public Player(String userName, String password) {

        this.userName = userName;
        this.password = password;
        this.gamePlayers = new HashSet<>();
        this.scores = new HashSet<>();
    }

    //getters

    public long getId() { return id; }

    public String getEmail() {
        return userName;
    }

    public String getPassword() { return password; }

    @JsonIgnore
    public Set<Game> getGames() { return gamePlayers.stream().map(sub ->
            sub.getGame()).collect(Collectors.toSet());}

    @JsonIgnore
    public Set<GamePlayer> getGamePlayers() { return gamePlayers; }

    public Set<Score> getScores() { return scores; }

    public Score getScore(Game game) {
       return getScores().stream().filter(
               score -> score.getId() == game.getId() ).findFirst().orElse(null);
    }

    //setters
    public void setEmail(String userName) { this.userName = userName; }

    public void setPassword(String password) {this.password = password; }

    //connects the game with the player
    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

    public void addScore(Score score) {
        score.setPlayer(this);
        scores.add(score);
    }

    //GAME SCORE COUNTERS
    public double countAllGames() {
       return scores.size();
    }

    public double countWonGames() {
        return scores.stream().filter( score -> score.getScore() == 1.0d).count();
    }

    public double countLostGames() {
        return scores.stream().filter( score -> score.getScore() == 0.0d).count();
    }

    public double countTiedGames() {
        return scores.stream().filter( score -> score.getScore() == 0.5d).count();
    }


}


