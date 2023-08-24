package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.RegisterRequest;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.entity.PlayerMySQL;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions.customExceptions.UserNotFoundException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.GameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.PlayerGameDTO;

import java.util.List;

public interface IPlayerGamerService {

    List<PlayerGameDTO> getAllPlayersDTO();
    List<PlayerGameDTO> getAllPlayersDTORanking();
    PlayerGameDTO updatePlayer(RegisterRequest updatedPlayer, String currentEmail);
    GameDTO saveGame(int id);
    List<GameDTO> deleteGamesByPlayerId(int id);
    PlayerGameDTO findPlayerDTOById(int id);
    List<GameDTO> findGamesByPlayerId(int id) throws UserNotFoundException;
    PlayerGameDTO getWorstPlayer();
    PlayerGameDTO getBestPlayer();
    Double averageTotalMarks();
    List<PlayerMySQL> getAllPlayerMySQL();
    PlayerMySQL getPlayer(int id);
    List<GameDTO> getGamePagination(int id, int pageNo, int pageSize);

}
