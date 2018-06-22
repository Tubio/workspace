package com.accenture.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String userName;

    @OneToMany(mappedBy = "player",fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    public Player() { }

    public Player(String userName) {
        this.userName = userName;
    }

    //getters

    public long getId() { return id; }
    public String getEmail() {
        return userName;
    }

    @JsonIgnore
    public Set<Game> getGames() { return gamePlayers.stream().map(sub ->
            sub.getGame()).collect(Collectors.toSet());}

    @JsonIgnore
    public Set<GamePlayer> getGamePlayers() {return gamePlayers; }
    //setter
    public void setEmail(String userName) { this.userName = userName; }

    //connects the game with the player
    public void addGamePlayer(GamePlayer gamePlayer){
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

}


