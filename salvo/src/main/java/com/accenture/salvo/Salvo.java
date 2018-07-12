package com.accenture.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Salvo {
    //attributes
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    private int turnNumber;

    @ElementCollection
    @Column(name = "salvoLocations")
    private List<String> locations;

    //methods

    //constructor
    public Salvo(){}

    //getters
    public long getId() { return id; }

    @JsonIgnore
    public GamePlayer getGamePlayer() { return gamePlayer; }

    public int getTurnNumber() { return turnNumber; }

    public List<String> getLocations() { return locations; }

    //setters
    public void setId(long id) { this.id = id; }

    public void setGamePlayer(GamePlayer gamePlayer) { this.gamePlayer = gamePlayer; }

    public void setTurnNumber(int turnNumber) { this.turnNumber = turnNumber; }

    public void setLocations(List<String> locations) { this.locations = locations; }

    public void addLocation(String location) { this.locations.add(location); }
}
