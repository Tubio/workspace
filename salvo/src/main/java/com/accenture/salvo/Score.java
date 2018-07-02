package com.accenture.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    private Double score;

    private Date finishDate;

    //CONSTRUCTOR
    public Score(){

        score = 0d;
        finishDate = new Date();
    }

    //GETTERS
    public Long getId() { return id; }

    @JsonIgnore
    public Player getPlayer() { return player; }

    @JsonIgnore
    public Game getGame() { return game; }

    public Double getScore() { return score; }

    public Date getFinishDate() { return finishDate; }

    //SETTERS
    public void setId(Long id) { this.id = id; }

    public void setPlayer(Player player) { this.player = player; }

    public void setGame(Game game) { this.game = game; }

    public void setScore(Double score) { this.score = score; }

    public void setFinishDate(Date finishDate) { this.finishDate = finishDate; }
}
