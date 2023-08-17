package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.RegisterRequest;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.ExceptionHandler.BaseDescriptionException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.ExceptionHandler.EmptyDataBaseException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.GameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.PlayerGameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.entity.GameMySQL;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.entity.PlayerMySQL;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.ExceptionHandler.UserNotFoundException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.repository.IplayerRepositoryMySQL;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.repository.IGameRepositoryMySQL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlayerGamerServiceMySQLImpl implements IPlayerGamerServiceMySQL {


    @Autowired
    private IGameRepositoryMySQL gameRepository;
    @Autowired
    private IplayerRepositoryMySQL playerRepositorySQL;
    @Autowired
    private AuthenticationMySQLService authenticationMySQLService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     *
     * 🔁 ----------  MAPPERS -----------
     *
     */

    public PlayerGameDTO playerDTOfromPlayer(PlayerMySQL player){
        return new PlayerGameDTO(player.getId(), player.getName(), player.getAverageMark(), (player.getSuccessRate() + " %"));
    }
    public GameDTO gameDTOfromGame(GameMySQL game){
        return new GameDTO(game.getMark());
    }


    /**
     *
     *  ℹ️    ------- METHODS ----------------
     *
     */


    @Override
    public List<PlayerGameDTO> getAllPlayersDTO(){

        List<PlayerMySQL> playerMySQLList = playerRepositorySQL.findAll();
        if(playerMySQLList.size() > 0){
            return playerMySQLList.stream()
                    .map(p -> this.playerDTOfromPlayer(p))
                    .collect(Collectors.toList());
        }else{
            log.error(BaseDescriptionException.EMPTY_DATABASE);
            throw new EmptyDataBaseException(BaseDescriptionException.EMPTY_DATABASE);
        }

    }

    @Override
    public List<PlayerGameDTO> getAllPlayersDTORanking(){
        List<PlayerMySQL> playerMySQLList = playerRepositorySQL.findAll();
        if(playerMySQLList.size() > 0){
            return playerMySQLList.stream()
                    .sorted(Comparator.comparing(PlayerMySQL::getSuccessRate).reversed())
                    .map(p -> this.playerDTOfromPlayer(p))
                    .collect(Collectors.toList());
        }else{
            log.error(BaseDescriptionException.EMPTY_DATABASE);
            throw new EmptyDataBaseException(BaseDescriptionException.EMPTY_DATABASE);
        }
    }

    @Override
    public PlayerGameDTO updatePlayer(RegisterRequest updatedPlayer, String currentEmail){

        PlayerMySQL newPlayer = playerRepositorySQL.findByEmail(currentEmail).get();
        authenticationMySQLService.checkDuplicatedName(updatedPlayer.getFirstname());

        newPlayer.setName(updatedPlayer.getFirstname());
        newPlayer.setSurname(updatedPlayer.getLastname());
        newPlayer.setPassword(passwordEncoder.encode(updatedPlayer.getPassword()));

        if(!currentEmail.equalsIgnoreCase(updatedPlayer.getEmail())){
            authenticationMySQLService.checkDuplicatedEmail(updatedPlayer.getEmail());
            newPlayer.setEmail(updatedPlayer.getEmail());
            log.warn("Log out and log in again, otherwise the token will fail because the username won't match");
        }

        playerRepositorySQL.save(newPlayer);
        return this.playerDTOfromPlayer(newPlayer);

    }

    @Override
    public PlayerGameDTO findPlayerDTOById(int id){
        Optional<PlayerMySQL> player = playerRepositorySQL.findById(id);
        if(player.isPresent()){
            return this.playerDTOfromPlayer(player.get());
        }else{
            log.error(BaseDescriptionException.NO_USER_BY_THIS_ID);
            throw new UserNotFoundException(BaseDescriptionException.NO_USER_BY_THIS_ID);
        }
    }

    @Override
    public List<GameDTO> findGamesByPlayerId(int id){
        if(playerRepositorySQL.existsById(id)){
            return gameRepository.findByPlayerId(id).stream()
                    .map(this::gameDTOfromGame)
                    .collect(Collectors.toList());
        }else{
            log.error(BaseDescriptionException.NO_USER_BY_THIS_ID);
            throw new UserNotFoundException(BaseDescriptionException.NO_USER_BY_THIS_ID);
        }
    }

    @Override
    public GameDTO saveGame(int playerId){
        int result = LogicGame.PLAY();
        PlayerMySQL player = playerRepositorySQL.findById(playerId).
                orElseThrow(() -> new UserNotFoundException(BaseDescriptionException.NO_USER_BY_THIS_ID));

        GameMySQL savedGame = gameRepository.save(new GameMySQL(result, player));
        playerRepositorySQL.save(player.autoSetNewGamesRates(result));
        return gameDTOfromGame(savedGame);
    }

    @Override
    public List<GameDTO> deleteGamesByPlayerId(int id){
        Optional<PlayerMySQL> player = playerRepositorySQL.findById(id);
        if(player.isPresent()){
            player.get().resetAllGamesRate();
            return gameRepository.deleteByPlayerId(id).stream()
                    .map(this::gameDTOfromGame)
                    .collect(Collectors.toList());
        }else{
            log.error(BaseDescriptionException.NO_USER_BY_THIS_ID);
            throw new UserNotFoundException(BaseDescriptionException.NO_USER_BY_THIS_ID);
        }
    }

    @Override
    public PlayerGameDTO getWorstPlayer(){
        List<PlayerGameDTO> playersList = this.getAllPlayersDTORanking();
        return playersList.get(playersList.size()-1);
    }

    @Override
    public PlayerGameDTO getBestPlayer(){
        List<PlayerGameDTO> playersList = this.getAllPlayersDTORanking();
        return playersList.get(0);
    }


    @Override
    public Double averageTotalMarks(){
        return this.getAllPlayersDTO().stream()
                .mapToDouble(PlayerGameDTO::getAverageMark)
                .average().getAsDouble();
    }



}
