package com.accenture.salvo;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
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

    //constructor
    public GamePlayer() {

        ships = new HashSet<>();
        creationDate = new Date();
    }

    /*public void GamePlayer(Player player , Game game) { //not working
        this.player = player;
        this.game = game;
    }*/

    //getters
    public Long getId() { return id; }
    public Player getPlayer(){ return player; }
    public Game getGame() { return game;}
    public Date getCreationDate() { return creationDate; }
    public Set<Ship> getShips() {return ships; }

    //setters
    public void setPlayer(Player player) { this.player = player; }
    public void setGame(Game game) { this.game = game; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public void addShip (Ship newShip) {ships.add(newShip); }
}
