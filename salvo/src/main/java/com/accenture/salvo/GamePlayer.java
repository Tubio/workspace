package com.accenture.salvo;

import javax.persistence.*;
import javax.validation.constraints.Null;
import java.util.*;
import java.util.stream.Collectors;

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

    private GameState gameState;

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
    public GameState getGameState() {
        game.updateGameStates();
        return gameState;
    }
    //setters
    public void setPlayer(Player player) {
        this.player = player;
        player.addGamePlayer(this);
    }
    public void setGame(Game game) { this.game = game; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }
    public void setSalvoes(Set<Salvo> salvoes) { this.salvoes = salvoes; }
    public void setGameState(GameState gameState) { this.gameState = gameState; }

    //add
    public void addShip ( Ship newShip ) {
        newShip.setGamePlayer(this);
        ships.add(newShip);
    }

    public void addShips ( List<Ship> ships ) {
        ships.forEach(this::addShip);
    }

    public void addSalvo ( Salvo newSalvo ) {
        newSalvo.setGamePlayer(this);
        salvoes.add(newSalvo);
    }

    public void addSalvoes ( List<Salvo> salvoes ) {
        salvoes.forEach(this::addSalvo);
    }

    public Boolean hasOpponent() {

        Boolean hasOpponent = false;

        if ( getGame().getPlayers().size() == 2)
            hasOpponent = true;

        return hasOpponent;
    }
    public GamePlayer findOpponentGamePlayer() {

        GamePlayer opponentGP = null;

        if (hasOpponent()) {
            List<GamePlayer> gamePlayers = new ArrayList<>(game.getGamePlayers());

            if ( gamePlayers.get(0).getId() != this.getPlayer().getId() ) {
                opponentGP = gamePlayers.get(0);
            }
            else opponentGP = gamePlayers.get(1);
        }

        return opponentGP;
    }

    public int countTotalShipLocations() {

        int totalShipLocationCounter = 0;
        for ( Ship ship : ships ){
            totalShipLocationCounter = totalShipLocationCounter +ship.countTotalLocations();
        }

        return totalShipLocationCounter;
    }

    public int countTotalSalvoLocations() {

        int totalSalvoLocationCounter = 0;
        for ( Salvo salvo : salvoes ){
            totalSalvoLocationCounter = totalSalvoLocationCounter + salvo.countTotalLocations();
        }

        return totalSalvoLocationCounter;
    }

    public List<String> getHitsReceived() {

        List<String> hitsReceivedLocations;

        List<String> opponentSalvoLocations = new ArrayList<>();
        List<String> shipLocations = new ArrayList<>();

        for (Salvo salvo : findOpponentGamePlayer().getSalvoes())
            opponentSalvoLocations.addAll(salvo.getLocations());

        for (Ship ship : ships)
            shipLocations.addAll(ship.getLocations());

        hitsReceivedLocations = shipLocations.stream().filter(
                opponentSalvoLocations::contains).collect(Collectors.toList());

        return hitsReceivedLocations;

    }

    public List<String> getHits() {

        List<String> hitLocations;

        List<String> opponentShipLocations = new ArrayList<>();
        List<String> salvoLocations = new ArrayList<>();

        for (Salvo salvo : getSalvoes())
            salvoLocations.addAll(salvo.getLocations());

        for (Ship ship : findOpponentGamePlayer().getShips())
            opponentShipLocations.addAll(ship.getLocations());

        hitLocations = opponentShipLocations.stream().filter(
                salvoLocations::contains).collect(Collectors.toList());

        return hitLocations;

    }

}