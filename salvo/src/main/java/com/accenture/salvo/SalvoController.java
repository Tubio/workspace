package com.accenture.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.stream.Collectors.toList;
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
    @Autowired
    private ShipRepository shipRepository;
    @Autowired
    private SalvoRepository salvoRepository;
    @Autowired
    private ScoreRepository scoreRepository;

    @RequestMapping("/games")
    public Map<String, Object> getAll(Authentication authentication) {

        Map<String, Object> gameMap = new LinkedHashMap<>();

        if (isGuest(authentication))
            gameMap.put("player", "Guest");
        else
            gameMap.put("player", makePlayerDTO(playerRepository.findByUserName(authentication.getName())));

        gameMap.put("games", gameRepository.findAll().stream().map(this::makeGameDTO).collect(toSet()));

        return gameMap;
    }

    //returns info about the gamePlayer Id requested
    @RequestMapping(value = "/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getInfo(@PathVariable long gamePlayerId, Authentication authentication) {

        ResponseEntity<Map<String, Object>> requestResponse;
        Map<String, Object> requestMap;

        GamePlayer requested = gamePlayerRepository.findById(gamePlayerId);

        if ( requested == null) {

            requestResponse = new ResponseEntity<>(makeResponseMap(AppMessage.KEY_ERROR,AppMessage.BODYMSG_BADID),HttpStatus.BAD_REQUEST);
        }

        else if (authentication.getName().equals(requested.getPlayer().getEmail())) {

            requestMap = extractFrom(requested);
            requestResponse = new ResponseEntity<>(requestMap, HttpStatus.ACCEPTED);

        } else {

            requestMap = makeResponseMap(AppMessage.KEY_ERROR,AppMessage.BODYMSG_UNAUTHORIZED);
            requestResponse = new ResponseEntity<>(requestMap, HttpStatus.UNAUTHORIZED);
        }
        return requestResponse;
    }

    @RequestMapping("/leaderBoard")
    public Set<Map<String, Object>> getLeaderboard() {

        List<Player> playerList = playerRepository.findAll();
        return playerList.stream().map(player -> makeLeaderboardDTO(player)).collect(toSet());
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createPlayer(@RequestParam String username, @RequestParam String password) {

        ResponseEntity<Map<String, Object>> responseEntity;

        if (username.isEmpty()) { //checks empty
            responseEntity = new ResponseEntity<>(
                    makeResponseMap(AppMessage.KEY_ERROR, "No name"), HttpStatus.BAD_REQUEST);
        } else { //checks repeated
            Player player = playerRepository.findByUserName(username);
            if (player != null) {
                responseEntity = new ResponseEntity<>(
                        makeResponseMap(AppMessage.KEY_ERROR, "Name in use"), HttpStatus.BAD_REQUEST);
            } else { //saves the new player
                playerRepository.save(new Player(username, password));
                responseEntity = new ResponseEntity<>(
                        makeResponseMap("userName", username), HttpStatus.CREATED);
            }
        }
        return responseEntity;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> createGame (Authentication authentication) {

        ResponseEntity<Map<String,Object>> responseRequest;
        Map <String,Object> responseMap = new HashMap<>();

        if (isGuest(authentication)){ //a guest cannot create games. Response Forbidden

            responseRequest = new ResponseEntity<>(makeResponseMap(AppMessage.KEY_ERROR,AppMessage.BODYMSG_UNAUTHORIZED),HttpStatus.FORBIDDEN);

        }else{ //create game and gamePlayer. Response accepted

            GamePlayer gamePlayer = new GamePlayer();
            gamePlayer.setPlayer(playerRepository.findByUserName(authentication.getName()));

            Game created = new Game();
            created.setCreationDate(new Date());
            created.addGamePlayer(gamePlayer);

            gameRepository.save(created);
            gamePlayerRepository.save(gamePlayer);

            responseMap.put("gpid",gamePlayer.getId());
            responseRequest = new ResponseEntity<>(responseMap,HttpStatus.ACCEPTED);

        }

        return responseRequest;
    }

    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> joinGame( @PathVariable long gameId,
                                                        Authentication authentication ) {

        ResponseEntity<Map<String,Object>> responseRequest;
        Map<String,Object> responseMap = new HashMap<>();

        if (isGuest(authentication)) { // guests cannot join games

            responseMap = makeResponseMap(AppMessage.KEY_ERROR,AppMessage.BODYMSG_UNAUTHORIZED);
            responseRequest = new ResponseEntity<>(responseMap,HttpStatus.UNAUTHORIZED);

        }else { // game validation

            Game requested = gameRepository.findOne(gameId);

            if (requested == null) { //checks if game exists

                responseMap.put(AppMessage.KEY_ERROR, AppMessage.BODYMSG_BADGAME);

                responseRequest = new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);

            }else if (requested.hasPlayer(authentication.getName())) {//checks if the player is already on the game

                responseMap.put(AppMessage.KEY_ERROR,AppMessage.BODYMSG_ALREADYJOINEDGAME);

                responseRequest = new ResponseEntity<>(responseMap, HttpStatus.FORBIDDEN);

            }else if (requested.getPlayers().size() == 2){

                responseMap.put(AppMessage.KEY_ERROR,AppMessage.BODYMSG_FULLGAME);

                responseRequest = new ResponseEntity<>(responseMap, HttpStatus.FORBIDDEN);

            }else {

                GamePlayer gamePlayer = new GamePlayer();
                Game joined = gameRepository.findOne(gameId);

                gamePlayer.setPlayer(playerRepository.findByUserName(authentication.getName()));
                joined.addGamePlayer(gamePlayer);

                gamePlayerRepository.save(gamePlayer);

                responseMap.put("gpid",gamePlayer.getId());
                responseRequest = new ResponseEntity<>(responseMap, HttpStatus.ACCEPTED);

            }
        }
        return responseRequest;
    }

    @RequestMapping(path = "/games/players/{gamePlayerID}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> setShips(@PathVariable Long gamePlayerID,
                                                        @RequestBody List <Ship> ships, Authentication authentication) {

        ResponseEntity<Map<String,Object>> requestResponse;
        GamePlayer gamePlayer = gamePlayerRepository.getOne(gamePlayerID);

        Player logged = playerRepository.findByUserName(authentication.getName());

        if (gamePlayer == null) {

            requestResponse = new ResponseEntity<>(makeResponseMap(AppMessage.KEY_ERROR,AppMessage.BODYMSG_BADID),HttpStatus.BAD_REQUEST);
        }
//        checks if the player is not logged in, is referencing a game that does not exist
//        or is entering a game that he should not.
        else if( isGuest(authentication) || ! gamePlayer.getPlayer().getEmail().equals(logged.getEmail()) ) {

            requestResponse = new ResponseEntity<>(makeResponseMap(AppMessage.KEY_ERROR,AppMessage.BODYMSG_UNAUTHORIZED),HttpStatus.UNAUTHORIZED);

        }else {

            gamePlayer.addShips(ships);
            shipRepository.save(ships);

            requestResponse = new ResponseEntity<>(makeResponseMap(AppMessage.KEY_CREATED,AppMessage.BODYMSG_DATAVERIFIED),HttpStatus.CREATED);

        }
        return requestResponse;
    }

    @RequestMapping(path = "/games/players/{gamePlayerID}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> setSalvoes(@PathVariable Long gamePlayerID,
                                                          @RequestBody Salvo salvo,
                                                          Authentication authentication) {
        //local variables
        ResponseEntity<Map<String, Object>> requestResponse;
        GamePlayer gamePlayer = gamePlayerRepository.getOne(gamePlayerID);
        int turnNumber = gamePlayer.getSalvoes().size() +1;
        Player logged = playerRepository.findByUserName(authentication.getName());

        salvo.setTurnNumber(turnNumber);

        if (gamePlayer == null) {

            requestResponse = new ResponseEntity<>(makeResponseMap(AppMessage.KEY_ERROR,AppMessage.BODYMSG_BADID),HttpStatus.BAD_REQUEST);
        }

//      checks if the player is not logged in, is referencing a game that does not exist
//      or is entering a game that he should not.
        else if(isGuest(authentication) || ! gamePlayer.getPlayer().getEmail().equals(logged.getEmail())) {

            requestResponse = new ResponseEntity<>(makeResponseMap(AppMessage.KEY_ERROR,AppMessage.BODYMSG_UNAUTHORIZED),HttpStatus.UNAUTHORIZED);

        }else {

            gamePlayer.addSalvo(salvo);
            salvoRepository.save(salvo);

            requestResponse = new ResponseEntity<>(makeResponseMap(AppMessage.KEY_CREATED,AppMessage.BODYMSG_DATAVERIFIED),HttpStatus.CREATED);

        }
        return requestResponse;
    }

    /////////////////
    //private methods
    private Map<String, Object> extractFrom(GamePlayer requested) {

        Map<String, Object> gameInfo = new HashMap<>();

        gameInfo.put("id", requested.getGame().getId());
        gameInfo.put("created", requested.getGame().getCreationDate());
        gameInfo.put("gameState", processGameState(requested));
        gameInfo.put("gamePlayers", makeGamePlayerDTO(requested.getGame().getGamePlayers()));
        gameInfo.put("ships", makeShipDTO(requested.getShips()));
        gameInfo.put("salvoes", requested.getGame().getSalvoes().stream().map(this::makeSalvoDTO).collect(toSet()));
        gameInfo.put("hits",makeHitsDTO(requested));

        return gameInfo;
    }

    private Map<String, Object> makeGameDTO(Game game) {

        Map<String, Object> map = new LinkedHashMap<>();

        map.put("id", game.getId());
        map.put("created", game.getCreationDate());
        map.put("gamePlayers", game.getGamePlayers().stream().map(this::makeGamePlayerDTO).collect(toSet()));
        map.put("scores", game.getScores().stream().map(this::makeScoreDTO).collect(toSet()));
        return map;
    }

    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {

        Map<String, Object> gamePlayerMap = new LinkedHashMap<>();
        gamePlayerMap.put("gpid", gamePlayer.getId());
        gamePlayerMap.put("id", gamePlayer.getId());
        gamePlayerMap.put("player", makePlayerDTO(gamePlayer.getPlayer()));

        return gamePlayerMap;
    }

    private Set<Map<String, Object>> makeGamePlayerDTO(Set<GamePlayer> gamePlayerSet) {

        Set<Map<String, Object>> gamePlayerMap = new HashSet<>();
        gamePlayerMap = gamePlayerSet.stream().map(gp -> makeGamePlayerDTO(gp)).collect(toSet());

        return gamePlayerMap;
    }

    private Map<String, Object> makePlayerDTO(Player player) {

        Map<String, Object> playerMap = new LinkedHashMap<>();
        playerMap.put("id", player.getId());
        playerMap.put("email", player.getEmail());

        return playerMap;
    }

    private Map<String, Object> makeShipDTO(Ship ship) {

        Map<String, Object> shipMap = new HashMap<>();
        shipMap.put("type", ship.getType());
        shipMap.put("locations", ship.getLocations());

        return shipMap;
    }

    private Set<Map<String, Object>> makeShipDTO(Set<Ship> shipSet) {

        Set<Map<String, Object>> shipMap = new HashSet<>();
        shipMap = shipSet.stream().map(ship -> makeShipDTO(ship)).collect(toSet());

        return shipMap;
    }

    private Map<String, Object> makeSalvoDTO(Salvo salvo) {

        Map<String, Object> salvoMap = new LinkedHashMap<>();
        salvoMap.put("turn", salvo.getTurnNumber());
        salvoMap.put("player", salvo.getGamePlayer().getPlayer().getId());
        salvoMap.put("locations", salvo.getLocations());

        return salvoMap;
    }

    private Map<String, Object> makeScoreDTO(Score score) {

        Map<String, Object> scoreMap = new LinkedHashMap<>();
        scoreMap.put("playerID", score.getPlayer().getId());
        scoreMap.put("score", score.getScore());
        scoreMap.put("finishDate", score.getFinishDate());

        return scoreMap;
    }

    private Map<String, Object> makeLeaderboardDTO(Player player) {

        Map<String, Object> leaderboardDTO = new LinkedHashMap<>();

        Map<String, Object> scoreDTO = new LinkedHashMap<>();
        scoreDTO.put("total", player.countAllGames());
        scoreDTO.put("won", player.countWonGames());
        scoreDTO.put("lost", player.countLostGames());
        scoreDTO.put("tied", player.countTiedGames());

        leaderboardDTO.put("name", player.getEmail());
        leaderboardDTO.put("score", scoreDTO);

        return leaderboardDTO;
    }

    private Map<String,Object> makeHitsDTO(GamePlayer gamePlayer) {
        /* array positions:
       0: carrier
       1: battleship
       2: submarine
       3: destroyer
       4: patrolboat
       */

        int playerTotalDamage[] = new int[5];
        Set<Ship> playerShips;
        Set<Salvo> playerSalvoes;

        int opponentTotalDamage[] = new int[5];
        Set<Ship> opponentShips;
        Set<Salvo> opponentSalvoes;

        Map<String,Object> hitsMap = new HashMap<>();

        playerShips = gamePlayer.getShips();
        playerSalvoes = gamePlayer.getSalvoes();

        if ( gamePlayer.getGame().getPlayers().size() == 1 ) {

            opponentShips = Collections.emptySet();
            opponentSalvoes = Collections.emptySet();

        }else {

            opponentShips = gamePlayer.findOpponentGamePlayer().getShips();
            opponentSalvoes = gamePlayer.findOpponentGamePlayer().getSalvoes();

        }

        //changed to List because Sets were not getting ordered (bug?)
        List<Salvo> sortedPlayerSalvoes = playerSalvoes.stream().sorted(Comparator.comparing(Salvo::getTurnNumber)).collect(toList());
        List<Salvo> sortedOpponentSalvoes = opponentSalvoes.stream().sorted(Comparator.comparing(Salvo::getTurnNumber)).collect(toList());

        hitsMap.put("self", sortedOpponentSalvoes.stream().map(
                salvo -> makeHitsDamageDTO(salvo,playerShips,playerTotalDamage)).collect(toSet()));
        hitsMap.put("opponent", sortedPlayerSalvoes.stream().map(
                salvo -> makeHitsDamageDTO(salvo,opponentShips,opponentTotalDamage)).collect(toSet()));

        return hitsMap;
    }

    private Map<String,Object> makeHitsDamageDTO(Salvo salvo, Set<Ship> shipSet, int[] totalDamage) {

        List<Ship> shipList = new ArrayList<>(shipSet);

        Map<String,Object> hitsDamageDTO = new HashMap<>();

        int turnDamage[] = new int[5];
        int missed = 0;

        for (int i = 0 ; i < salvo.getLocations().size() ; i++) {
            //for each location

            //linear search
            Ship shipDamaged = findShipDamaged(salvo.getLocations().get(i),shipList);

            if (shipDamaged != null) {

                updateDamageVectors(shipDamaged, turnDamage, totalDamage);
            }
            else missed++;
        }

        hitsDamageDTO.put("turn",salvo.getTurnNumber());
        hitsDamageDTO.put("hitLocations",salvo.getLocations());
        hitsDamageDTO.put("damages",makeShipDamageDTO(turnDamage,totalDamage));
        hitsDamageDTO.put("missed",missed);

        return hitsDamageDTO;
        
    }

    private Map<String,Object> makeShipDamageDTO(int[] turnDamage, int[] totalDamage) {

        Map<String,Object> shipDamageMap = new HashMap<>();

        shipDamageMap.put(Type.CARRIER+"Hits",turnDamage[0]);
        shipDamageMap.put(Type.BATTLESHIP+"Hits",turnDamage[1]);
        shipDamageMap.put(Type.SUBMARINE+"Hits",turnDamage[2]);
        shipDamageMap.put(Type.DESTROYER+"Hits",turnDamage[3]);
        shipDamageMap.put(Type.PATROL_BOAT+"Hits",turnDamage[4]);
        shipDamageMap.put(Type.CARRIER,totalDamage[0]);
        shipDamageMap.put(Type.BATTLESHIP,totalDamage[1]);
        shipDamageMap.put(Type.SUBMARINE,totalDamage[2]);
        shipDamageMap.put(Type.DESTROYER,totalDamage[3]);
        shipDamageMap.put(Type.PATROL_BOAT,totalDamage[4]);

        return shipDamageMap;
    }

    //pre: locationToFind and shipList not null
    //post: returns a ship that contains a location to find in a list of ships, returns
    //      null if it does not find a ship
    private Ship findShipDamaged (String locationToFind , List<Ship> shipList) {

        Ship damaged = null;

        boolean found = false;
        int shipCounter = 0;
        int maxShipCounter = shipList.size();

        int locationCounter = 0;
        int maxLocationCounter;

        while ( !found && (shipCounter < maxShipCounter)) {

            Ship actualShip = shipList.get(shipCounter);
            List<String> actualShipLocations = actualShip.getLocations();

            maxLocationCounter = actualShipLocations.size();

            locationCounter = 0;
            while ( !found && (locationCounter < maxLocationCounter) ){

                String actualLocation = actualShipLocations.get(locationCounter);

                if ( locationToFind.equals(actualLocation) ){

                    damaged = actualShip;
                    found = true;

                }
                else locationCounter ++;
            }

            shipCounter++;

        }

        return damaged;
    }

    private void updateDamageVectors(Ship damaged, int[] turnDamage, int[] totalDamage) {

        String type = damaged.getType();

        if( type.equals(Type.CARRIER) ) {
            turnDamage[0]++;
            totalDamage[0]++;
        }
        else if ( type.equals(Type.BATTLESHIP) ) {
            turnDamage[1]++;
            totalDamage[1]++;
        }
        else if ( type.equals(Type.SUBMARINE) ) {
            turnDamage[2]++;
            totalDamage[2]++;
        }
        else if ( type.equals(Type.DESTROYER) ) {
            turnDamage[3]++;
            totalDamage[3]++;
        }
        else if ( type.equals(Type.PATROL_BOAT) ) {
            turnDamage[4]++;
            totalDamage[4]++;
        }
    }


    //Private Security methods

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private Map<String, Object> makeResponseMap(String key, Object value) {

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put(key, value);

        return responseMap;
    }

    private GameState processGameState (GamePlayer requested) {

        GameState updated = requested.updateGameState();

        if (      (requested.getGameState().equals(GameState.WON)
                || requested.getGameState().equals(GameState.LOST)
                || requested.getGameState().equals(GameState.TIE))) {

            updated = requested.getGameState();
        }
        else if (updated.equals(GameState.WON) || updated.equals(GameState.TIE)
                || updated.equals(GameState.WON)){
            Score finishedGameScore = new Score();

            finishedGameScore.setPlayer(requested.getPlayer());
            finishedGameScore.setGame(requested.getGame());

            if (updated.equals(GameState.WON)){
                finishedGameScore.setScore(1.0);
                requested.setGameState(GameState.WON);
            }
            else if (updated.equals(GameState.TIE)){
                finishedGameScore.setScore(0.5);
                requested.setGameState(GameState.TIE);
            }
            else if (updated.equals(GameState.LOST)){
                finishedGameScore.setScore(0.0); //LOST
                requested.setGameState(GameState.LOST);
            }
            scoreRepository.save(finishedGameScore);
        }
        return updated;
    }
}