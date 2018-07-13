package com.accenture.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.swing.text.StyledEditorKit;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;


@Entity
public class Game {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private Date creationDate;

    @OneToMany(mappedBy = "game",fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game",fetch = FetchType.EAGER)
    private Set<Score> scores;

    public Game() {

        gamePlayers = new HashSet<>();
        scores = new HashSet<>();
        creationDate = new Date();
    }

    //getters

    public long getId() { return id; }
    public Date getCreationDate() { return creationDate; }

    @JsonIgnore
    public Set<Player> getPlayers() { return gamePlayers.stream().map(sub ->
                                        sub.getPlayer()).collect(toSet());}
    @JsonIgnore
    public Set<GamePlayer> getGamePlayers() { return gamePlayers; }

    public Set<Score> getScores() { return scores; }

    public Set<Salvo> getSalvoes() {
        return
            getGamePlayers().stream().map(gamePlayer -> gamePlayer.getSalvoes()).flatMap(list -> list.stream())
                .collect(toSet());

    }

    //setter
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    //checks if the player exists on the game
    public Boolean hasPlayer ( String name ) {

        Boolean hasPlayer = false;

        if ( getPlayers().stream().filter(player -> player.getEmail().equals(name)).count() == 1 ) {
            hasPlayer = true;
        }

        return hasPlayer;
    }

    //connects the game with the player
    public void addGamePlayer(GamePlayer gamePlayer){
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }

    public void addScore(Score score){
        score.setGame(this);
        scores.add(score);
    }

    public void updateGameStates() {

        List<GamePlayer> gamePlayers = new ArrayList<>(this.gamePlayers);
        Boolean fullGame = false;

        GamePlayer gp1 = gamePlayers.get(0);

        if (gp1.hasOpponent()) {
            fullGame = true;
        }

        if (!fullGame) {

            if(gp1.getShips().isEmpty())
                gp1.setGameState(GameState.PLACESHIPS);
            else gp1.setGameState(GameState.WAITINGFOROPP);

        }else{

            GamePlayer gp2 = gamePlayers.get(1);

            if(gp2.getShips().isEmpty()){
                gp1.setGameState(GameState.WAITINGFOROPP);
                gp2.setGameState(GameState.PLACESHIPS);
            }
            else {
                if (gp1.getSalvoes().size() < gp2.getSalvoes().size()) {

                    gp1.setGameState(GameState.PLAY);
                    gp2.setGameState(GameState.WAIT);

                }
                if (gp1.getSalvoes().size() == gp2.getSalvoes().size()) {

                    List<String> playerOneHitsLocations = gp1.getHits();
                    List<String> playerTwoHitsLocations = gp2.getHits();

                    Boolean playerOneSunked = false;
                    Boolean playerTwoSunked = false;

                    if (playerOneHitsLocations.size() == gp2.countTotalShipLocations())
                        playerTwoSunked = true;
                    if (playerTwoHitsLocations.size() == gp1.countTotalShipLocations())
                        playerOneSunked = true;

                    if (playerOneSunked && playerTwoSunked) {
                        gp1.setGameState(GameState.TIE);
                        gp2.setGameState(GameState.TIE);
                    } else if (playerOneSunked) {
                        gp1.setGameState(GameState.LOST);
                        gp2.setGameState(GameState.WON);
                    } else if (playerTwoSunked) {
                        gp1.setGameState(GameState.WON);
                        gp2.setGameState(GameState.LOST);
                    } else {
                        gp1.setGameState(GameState.PLAY);
                        gp2.setGameState(GameState.PLAY);
                    }
                }
                else {
                    gp1.setGameState(GameState.WAIT);
                    gp2.setGameState(GameState.PLAY);
                }
            }
        }



    }
}

