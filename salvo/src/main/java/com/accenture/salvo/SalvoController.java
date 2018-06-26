package com.accenture.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @RequestMapping("/games")
    public List<Object> getAll() {
        return
                // gameRepository.findAll().stream().map(game -> game.getId()).collect(toList());
                gameRepository.findAll().stream().map(game -> makeGameDTO(game))
                        .collect(Collectors.toList());
    }

    //returns info about the gamePlayer Id requested
    @RequestMapping( value = "/game_view/{gamePlayerId}" , method = RequestMethod.GET)
    public Map<String,Object> getGamePlayerInfo(@PathVariable long gamePlayerId){

        Map<String,Object> gameInfo = new HashMap<>();
        GamePlayer requested = gamePlayerRepository.getOne(gamePlayerId);

        gameInfo.put("id",requested.getGame().getId());
        gameInfo.put("created",requested.getGame().getCreationDate());
        gameInfo.put("gamePlayers",requested.getGame().getGamePlayers().stream().map( gp -> makeGamePlayerDTO(gp))
                                                                                    .collect(toSet()));
        gameInfo.put("ships",requested.getShips().stream().map( ship -> makeShipDTO(ship)).collect(toSet()));

        return gameInfo;
    }

    /////////////////
    //private methods
    private Map<String,Object> makeGameDTO(Game game) {

        Map<String, Object> map = new LinkedHashMap<>();

        map.put("id", game.getId());
        map.put("creationDate", game.getCreationDate());
        map.put("gamePlayers", game.getGamePlayers().stream().map(gp -> makeGamePlayerDTO(gp)).collect(toSet()));

        return map;
    }
    private Map<String,Object> makeGamePlayerDTO(GamePlayer gamePlayers){

        Map<String,Object> gamePlayerMap = new LinkedHashMap<>();
        gamePlayerMap.put("id",gamePlayers.getId());
        gamePlayerMap.put("player", makePlayerDTO(gamePlayers.getPlayer()));

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

}