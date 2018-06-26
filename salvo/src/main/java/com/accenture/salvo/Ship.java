package com.accenture.salvo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    private String type;

    @ElementCollection
    @Column(name = "locations")
    private List<String> locations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    //Constructor
    public Ship() {
        locations = new ArrayList<>();
    }

    //getters
    public GamePlayer getGamePlayer() { return gamePlayer; }
    public String getType() { return type; }
    public List<String> getLocations() { return locations; }

    //setters
    public void setGamePlayer (GamePlayer gamePlayer) { this.gamePlayer = gamePlayer; }
    public void setLocations (List<String> locations) { this.locations = locations; }
    public void addLocation (String newLocation) { this.locations.add(newLocation); }
    public void setType (String type) { this.type = type; }
}

