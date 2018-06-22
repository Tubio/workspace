package com.accenture.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    GameRepository gameRepository;

    @RequestMapping("/games")
    public List<Object> getAll() {
        return
                // gameRepository.findAll().stream().map(game -> game.getId()).collect(toList());
                gameRepository.findAll().stream().map(game -> makeGameDTO(game))
                        .collect(Collectors.toList());
    }
    
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
}