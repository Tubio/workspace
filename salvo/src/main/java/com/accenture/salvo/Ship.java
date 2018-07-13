package com.accenture.salvo;

import javax.persistence.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collector;

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
    //gets an array of locations and adds them to the bottom of the list
    public void addLocations (String[] locationArray) {
        List<String> newListObject = Arrays.asList(locationArray);
        locations.addAll(newListObject);
    }
    public void setType (String type) { this.type = type; }

    public int countTotalLocations() { return locations.size(); }
}

