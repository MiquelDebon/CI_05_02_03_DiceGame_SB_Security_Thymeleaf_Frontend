package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.RegisterRequest;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions.MessageException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions.customExceptions.EmptyDataBaseException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.GameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.PlayerGameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.entity.GameMySQL;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.entity.PlayerMySQL;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions.customExceptions.UserNotFoundException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.repository.IplayerRepository;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.repository.IGameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlayerGamerServiceImpl implements IPlayerGamerService {


    private IGameRepository gameRepository;
    private IplayerRepository playerRepositorySQL;
    private AuthenticationServiceImpl authenticationMySQLService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public PlayerGamerServiceImpl(
            IGameRepository gameRepository,
            IplayerRepository playerRepositorySQL,
            AuthenticationServiceImpl authenticationMySQLService,
            PasswordEncoder passwordEncoder) {
        this.gameRepository = gameRepository;
        this.playerRepositorySQL = playerRepositorySQL;
        this.authenticationMySQLService = authenticationMySQLService;
        this.passwordEncoder = passwordEncoder;
    }



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
            log.error(MessageException.EMPTY_DATABASE);
            throw new EmptyDataBaseException(MessageException.EMPTY_DATABASE);
        }
    }

    public List<PlayerMySQL> getAllPlayerMySQL(){
        List<PlayerMySQL> playerMySQLList = playerRepositorySQL.findAll();
        if(playerMySQLList.size() > 0){
            return playerMySQLList;
        }else{
            log.error(MessageException.EMPTY_DATABASE);
            throw new EmptyDataBaseException(MessageException.EMPTY_DATABASE);
        }
    }

    public PlayerMySQL getPlayer(int id){
        return playerRepositorySQL.findById(id)
                .orElseThrow(() -> new UserNotFoundException(MessageException.NO_USER_BY_THIS_ID));
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
            log.error(MessageException.EMPTY_DATABASE);
            throw new EmptyDataBaseException(MessageException.EMPTY_DATABASE);
        }
    }

    @Override
    public PlayerGameDTO updatePlayer(RegisterRequest updatedPlayer, int id){


        PlayerMySQL player = playerRepositorySQL.findById(id)
                .orElseThrow(UserNotFoundException::new);

        //Check the new name is not duplicated
        String currentName = player.getName();
        String updatedName = updatedPlayer.getFirstname();
        if(!currentName.equalsIgnoreCase(updatedName)){
            authenticationMySQLService.checkDuplicatedName(updatedPlayer.getFirstname());
            player.setName(updatedPlayer.getFirstname());
            playerRepositorySQL.save(player);
        }

        //Check the new email is not duplicated
        String currentEmail = player.getEmail();
        String updatedEmail = updatedPlayer.getEmail();
        if(!currentEmail.equalsIgnoreCase(updatedEmail)){
            authenticationMySQLService.checkDuplicatedEmail(updatedPlayer.getEmail());
            player.setEmail(updatedPlayer.getEmail());
            playerRepositorySQL.save(player);
            log.warn("Log out and log in again, otherwise the token will fail because the username won't match");
        }

        //Set the new values
        player.setSurname(updatedPlayer.getLastname());
        player.setPassword(passwordEncoder.encode(updatedPlayer.getPassword()));
        playerRepositorySQL.save(player);

        return this.playerDTOfromPlayer(player);
    }

    @Override
    public PlayerGameDTO findPlayerDTOById(int id){
        Optional<PlayerMySQL> player = playerRepositorySQL.findById(id);
        if(player.isPresent()){
            return this.playerDTOfromPlayer(player.get());
        }else{
            log.error(MessageException.NO_USER_BY_THIS_ID);
            throw new UserNotFoundException(MessageException.NO_USER_BY_THIS_ID);
        }
    }

    @Override
    public List<GameDTO> findGamesByPlayerId(int id){
        if(playerRepositorySQL.existsById(id)){
            return gameRepository.findByPlayerId(id).stream()
                    .map(this::gameDTOfromGame)
                    .collect(Collectors.toList());
        }else{
            log.error(MessageException.NO_USER_BY_THIS_ID);
            throw new UserNotFoundException(MessageException.NO_USER_BY_THIS_ID);
        }
    }

    @Override
    public GameDTO saveGame(int playerId){
        int result = LogicGame.PLAY();
        PlayerMySQL player = playerRepositorySQL.findById(playerId).
                orElseThrow(() -> new UserNotFoundException(MessageException.NO_USER_BY_THIS_ID));

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
            log.error(MessageException.NO_USER_BY_THIS_ID);
            throw new UserNotFoundException(MessageException.NO_USER_BY_THIS_ID);
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
                .average()
                .orElseThrow(EmptyDataBaseException::new);
    }

    @Override
    public List<GameDTO> getGamePagination(int id, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<GameMySQL> games = gameRepository.findByPlayerId(id, pageable);
        List<GameMySQL> listOfGames = games.getContent();

        return listOfGames.stream()
                .map(this::gameDTOfromGame)
                .collect(Collectors.toList());
    }

    public void tryit(){

    }



}
