package com.accenture.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
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
        gameInfo.put("gamePlayers",makeGamePlayerDTO(requested.getGame().getGamePlayers()));
        gameInfo.put("ships",makeShipDTO(requested.getShips()));

        //me falta modularizar la funcion de abajo
        gameInfo.put("salvoes",requested.getGame().getGamePlayers().stream().map( gp -> gp.getSalvoes()).flatMap( list -> list.stream()).collect(toSet()));
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

     Map<String,Object> makeSalvoDTO(Salvo salvo) {

        Map<String,Object> salvoMap = new HashMap<>();
        salvoMap.put("turn",salvo.getTurnNumber());
        salvoMap.put("player",salvo.getGamePlayer().getPlayer().getId());
        salvoMap.put("locations",salvo.getLocations());

        return salvoMap;
    }

//    Map<String,Object> generateSalvoMap()
}