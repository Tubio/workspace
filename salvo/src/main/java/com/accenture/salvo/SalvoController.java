package com.accenture.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;
import java.net.URI;
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
    public Map<String,Object> getAll(Authentication authentication) {

        Map<String,Object> gameMap = new LinkedHashMap<>();

        if ( isGuest(authentication) )
            gameMap.put("player","Guest");
        else
            gameMap.put("player",makePlayerDTO(playerRepository.findByUserName(authentication.getName())));

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
        return playerList.stream().map( player -> makeLeaderboardDTO(player)).collect(toSet());
    }

    @RequestMapping( path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> createPlayer(@RequestParam String username,@RequestParam String password) {

        ResponseEntity<Map<String,Object>> responseEntity;

        if (username.isEmpty()){ //checks empty
            responseEntity = new ResponseEntity<>(
                    makeResponseMap("error","No name"), HttpStatus.FORBIDDEN);
        }
        else { //checks repeated
            Player player = playerRepository.findByUserName(username);
            if (player != null) {
                responseEntity = new ResponseEntity<>(
                        makeResponseMap("error","Name in use"), HttpStatus.FORBIDDEN);
            } else { //saves the new player
                playerRepository.save(new Player(username, password));
                responseEntity = new ResponseEntity<>(
                        makeResponseMap("userName",username), HttpStatus.CREATED);
            }
        }
        return responseEntity;
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
        map.put("created", game.getCreationDate());
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

    //Private Security methods

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private Map<String,Object> makeResponseMap (String key, Object value){

        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put(key,value);

        return responseMap;
    }
}
