package com.accenture.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;

import static java.util.stream.Collectors.toList;


@Entity
public class Game {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private Date creationDate;

    @OneToMany(mappedBy = "game",fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    public Game() {

        gamePlayers = new HashSet<>();
        creationDate = new Date();
    }

    //getters

    public long getId() { return id; }
    public Date getCreationDate() { return creationDate; }

    @JsonIgnore
    public Set<Player> getPlayers() { return gamePlayers.stream().map(sub ->
                                        sub.getPlayer()).collect(Collectors.toSet());}
    @JsonIgnore
    public Set<GamePlayer> getGamePlayers() { return gamePlayers; }

    //setter
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    //connects the game with the player
    public void addGamePlayer(GamePlayer gamePlayer){
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }
}

