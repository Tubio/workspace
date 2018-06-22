package com.accenture.salvo;

import javax.persistence.*;
import java.util.Date;

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

    //constructor
    public GamePlayer() {
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

    //setters
    public void setPlayer(Player player) { this.player = player; }
    public void setGame(Game game) { this.game = game; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }
}
