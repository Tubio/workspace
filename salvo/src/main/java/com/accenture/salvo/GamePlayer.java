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
        gameState = GameState.UNDEFINED;
    }

    public GamePlayer(Game game,Player player) {

        ships = new HashSet<>();
        salvoes = new HashSet<>();
        creationDate = new Date();
        this.game = game;
        this.player = player;
        gameState = GameState.UNDEFINED;
    }

    //getters
    public Long getId() { return id; }
    public Player getPlayer(){ return player; }
    public Game getGame() { return game; }
    public Date getCreationDate() { return creationDate; }
    public Set<Ship> getShips() { return ships; }
    public Set<Salvo> getSalvoes() { return salvoes; }
    public Score getScore() { return getPlayer().getScore(getGame()); }
    public GameState getGameState() { return gameState; }
    public GameState getUpdatedGameState() {
        return updateGameState();
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

            if ( gamePlayers.get(0).getId().equals(this.getId()) ) {
                opponentGP = gamePlayers.get(1);
            }
            else opponentGP = gamePlayers.get(0);
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

    public GameState updateGameState() {

        GameState updated;

        if (hasOpponent()){
            updated = updateForTwoPlayers();
        }
        else{
            updated = updateForOnePlayer();
        }
        return updated;
    }

    private GameState updateForOnePlayer() {

        GameState updated;

        if (ships.isEmpty()) {
            updated = GameState.PLACESHIPS;
        }
        else {
            updated = GameState.WAITINGFOROPP;
        }

        return updated;
    }

    private GameState updateForTwoPlayers() {

        GameState updated;
        GamePlayer opponentGP = findOpponentGamePlayer();

        //game being setup
        if (ships.isEmpty()) {
            updated = GameState.PLACESHIPS;
        }
        else if (opponentGP.getShips().isEmpty()) {
            updated = GameState.WAITINGFOROPP;
        }
        else {
            updated = updateForFullGame(opponentGP);
        }

        return updated;
    }

    private GameState updateForFullGame(GamePlayer opponentGP) {

        GameState updated;

        if (salvoes.size() < opponentGP.salvoes.size()) {
            updated = GameState.PLAY;
        }
        else if (salvoes.size() > opponentGP.salvoes.size()) {
            updated = GameState.WAIT;
        }
        else {
            updated = updateTurn(opponentGP); //gets called every turn to validate win conditions
        }

        return updated;
    }

    private GameState updateTurn(GamePlayer opponentGP) {

        GameState updated;
        List<String> hits = getHits();
        List <String> opponentHits = getHitsReceived();
        int totalShipLocations = countTotalShipLocations();

        if (hits.size() < totalShipLocations && opponentHits.size() < totalShipLocations)
            updated = GameState.PLAY;
        else {
            boolean shipsDestroyed = (opponentHits.size() == totalShipLocations);
            boolean opponentShipsDestroyed = (hits.size() == totalShipLocations);

            Score finishedGame = new Score();

            if (shipsDestroyed && opponentShipsDestroyed) {
                updated = GameState.TIE;
            }
            else if (shipsDestroyed) {
                updated = GameState.LOST;
            }
            else if (opponentShipsDestroyed) {
                updated = GameState.WON;
            }
            else updated = GameState.PLAY;
        }

        return updated;
    }

}