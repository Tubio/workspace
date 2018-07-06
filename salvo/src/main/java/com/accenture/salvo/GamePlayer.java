package com.accenture.salvo;

import javax.persistence.*;
import javax.validation.constraints.Null;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private Date creationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer",fetch = FetchType.EAGER)
    private Set<Ship> ships;

    @OneToMany(mappedBy = "gamePlayer",fetch = FetchType.EAGER)
    private Set<Salvo> salvoes;

    //constructor
    public GamePlayer() {

        ships = new HashSet<>();
        salvoes = new HashSet<>();
        creationDate = new Date();
    }

    public GamePlayer(Game game,Player player) {

        ships = new HashSet<>();
        salvoes = new HashSet<>();
        creationDate = new Date();
        this.game = game;
        this.player = player;
    }

    public GamePlayer(Game game){ //DO NOT USE, PROGRAM CRASHES, TODO: FIX

        ships = new HashSet<>();
        salvoes = new HashSet<>();
        creationDate = new Date();
        this.game = game;

        //creating a player called N/A for when the class recives only a game parameter
        player = new Player("N/A");

    }

    /*public void GamePlayer(Player player , Game game) { //not working
        this.player = player;
        this.game = game;
    }*/

    //getters
    public Long getId() { return id; }
    public Player getPlayer(){ return player; }
    public Game getGame() { return game; }
    public Date getCreationDate() { return creationDate; }
    public Set<Ship> getShips() { return ships; }
    public Set<Salvo> getSalvoes() { return salvoes; }
    public Score getScore() { return getPlayer().getScore(getGame()); }

    //setters
    public void setPlayer(Player player) {
        this.player = player;
        player.addGamePlayer(this);
    }
    public void setGame(Game game) { this.game = game; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }
    public void setSalvoes(Set<Salvo> salvoes) { this.salvoes = salvoes; }

    //add
    public void addShip ( Ship newShip ) {
        ships.add(newShip);
        newShip.setGamePlayer(this);
    }

    public void addShips ( List<Ship> ships ) {
        ships.forEach(this::addShip);
    }

    public void addSalvo ( Salvo newSalvo ) {
        salvoes.add(newSalvo);
        newSalvo.setGamePlayer(this);
    }

    public void addSalvoes ( List<Salvo> salvoes ) {
        salvoes.forEach(this::addSalvo);
    }

}