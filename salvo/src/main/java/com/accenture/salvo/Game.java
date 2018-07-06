package com.accenture.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;


@Entity
public class Game {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private Date creationDate;

    @OneToMany(mappedBy = "game",fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game",fetch = FetchType.EAGER)
    private Set<Score> scores;

    public Game() {

        gamePlayers = new HashSet<>();
        scores = new HashSet<>();
        creationDate = new Date();
    }

    //getters

    public long getId() { return id; }
    public Date getCreationDate() { return creationDate; }

    @JsonIgnore
    public Set<Player> getPlayers() { return gamePlayers.stream().map(sub ->
                                        sub.getPlayer()).collect(toSet());}
    @JsonIgnore
    public Set<GamePlayer> getGamePlayers() { return gamePlayers; }

    public Set<Score> getScores() { return scores; }

    public Set<Salvo> getSalvoes() {
        return
            getGamePlayers().stream().map(gamePlayer -> gamePlayer.getSalvoes()).flatMap(list -> list.stream())
                .collect(toSet());

    }

    //setter
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    //checks if the player exists on the game
    public Boolean hasPlayer ( String name ) {

        Boolean hasPlayer = false;

        if ( getPlayers().stream().filter(player -> player.getEmail().equals(name)).count() == 1 ) {
            hasPlayer = true;
        }

        return hasPlayer;
    }

    //connects the game with the player
    public void addGamePlayer(GamePlayer gamePlayer){
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }

    public void addScore(Score score){
        score.setGame(this);
        scores.add(score);
    }
}

