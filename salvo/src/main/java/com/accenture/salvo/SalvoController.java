package com.accenture.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @RequestMapping("/games")
    public Map<String,Object> getAll() {

        Map<String,Object> gameMap = new LinkedHashMap<>();
        gameMap.put("player","Guest");
        gameMap.put("games",gameRepository.findAll().stream().map(this::makeGameDTO).collect(toSet()));

        return gameMap;
    }

    //returns info about the gamePlayer Id requested
    @RequestMapping( value = "/game_view/{gamePlayerId}" , method = RequestMethod.GET)
    public Map<String,Object> getInfo(@PathVariable long gamePlayerId){

       return extractFrom(gamePlayerRepository.getOne(gamePlayerId));
    }

    @RequestMapping("/leaderBoard")
    public Set<Map<String,Object>> getLeaderboard() {

        List<Player> playerList = playerRepository.findAll();
        return playerList.stream().filter( player -> player.getEmail() != "N/A").map( player -> makeLeaderboardDTO(player)).collect(toSet());
    }

    /////////////////
    //private methods
    private Map<String,Object> extractFrom(GamePlayer requested){

        Map<String,Object> gameInfo = new HashMap<>();

        gameInfo.put("id",requested.getGame().getId());
        gameInfo.put("created",requested.getGame().getCreationDate());
        gameInfo.put("gamePlayers",makeGamePlayerDTO(requested.getGame().getGamePlayers()));
        gameInfo.put("ships",makeShipDTO(requested.getShips()));

        //me falta modularizar la funcion de abajo
        gameInfo.put("salvoes",requested.getGame().getGamePlayers().stream().map
                ( gp -> gp.getSalvoes()).flatMap( list -> list.stream()).collect(toSet()));

        return gameInfo;
    }

    private Map<String,Object> makeGameDTO(Game game) {

        Map<String, Object> map = new LinkedHashMap<>();

        map.put("id", game.getId());
        map.put("creationDate", game.getCreationDate());
        map.put("gamePlayers", game.getGamePlayers().stream().map(gp -> makeGamePlayerDTO(gp)).collect(toSet()));
        map.put("scores",game.getScores().stream().map( score -> makeScoreDTO(score )).collect(toSet()));
        return map;
    }
    private Map<String,Object> makeGamePlayerDTO(GamePlayer gamePlayer){

        Map<String,Object> gamePlayerMap = new LinkedHashMap<>();
        gamePlayerMap.put("id",gamePlayer.getId());
        gamePlayerMap.put("player", makePlayerDTO(gamePlayer.getPlayer()));

        return gamePlayerMap;
    }

    private Set<Map<String,Object>> makeGamePlayerDTO(Set<GamePlayer> gamePlayerSet){

        Set<Map<String,Object>> gamePlayerMap = new HashSet<>();
        gamePlayerMap = gamePlayerSet.stream().map( gp -> makeGamePlayerDTO(gp) ).collect(toSet());

        return gamePlayerMap;
    }

    private Map<String,Object> makePlayerDTO(Player player){

        Map<String,Object> playerMap = new LinkedHashMap<>();
        playerMap.put("id",player.getId());
        playerMap.put("email",player.getEmail());

        return playerMap;
    }

    private Map<String,Object> makeShipDTO(Ship ship){

        Map<String,Object> shipMap = new HashMap<>();
        shipMap.put("type",ship.getType());
        shipMap.put("locations",ship.getLocations());

        return shipMap;
    }

    private Set<Map<String,Object>> makeShipDTO(Set<Ship> shipSet){

        Set<Map<String,Object>> shipMap = new HashSet<>();
        shipMap = shipSet.stream().map( ship -> makeShipDTO(ship) ).collect(toSet());

        return shipMap;
    }

     private Map<String,Object> makeSalvoDTO(Salvo salvo) {

        Map<String,Object> salvoMap = new LinkedHashMap<>();
        salvoMap.put("turn",salvo.getTurnNumber());
        salvoMap.put("player",salvo.getGamePlayer().getPlayer().getId());
        salvoMap.put("locations",salvo.getLocations());

        return salvoMap;
    }

    private Map<String,Object> makeScoreDTO(Score score){

        Map<String,Object> scoreMap = new LinkedHashMap<>();
        scoreMap.put("playerID",score.getPlayer().getId());
        scoreMap.put("score",score.getScore());
        scoreMap.put("finishDate",score.getFinishDate());

        return scoreMap;
    }

    private Map<String,Object> makeLeaderboardDTO(Player player) {

        Map<String,Object> leaderboardDTO = new LinkedHashMap<>();

        Map<String,Object> scoreDTO = new LinkedHashMap<>();
        scoreDTO.put("total",player.countAllGames());
        scoreDTO.put("won",player.countWonGames());
        scoreDTO.put("lost",player.countLostGames());
        scoreDTO.put("tied",player.countTiedGames());

        leaderboardDTO.put("name",player.getEmail());
        leaderboardDTO.put("score",scoreDTO);

        return leaderboardDTO;
    }

//    Map<String,Object> generateSalvoMap()
}