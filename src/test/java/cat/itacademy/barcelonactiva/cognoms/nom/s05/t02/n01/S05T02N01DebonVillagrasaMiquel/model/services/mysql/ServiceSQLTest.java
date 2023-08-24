package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.mysql;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.RegisterRequest;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions.customExceptions.EmptyDataBaseException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions.customExceptions.UserNotFoundException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.GameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.PlayerGameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.entity.GameMySQL;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.entity.PlayerMySQL;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.repository.IGameRepository;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.repository.IplayerRepository;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.AuthenticationServiceImpl;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.PlayerGamerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;

@Slf4j
@DisplayName("Service test")
@ExtendWith(MockitoExtension.class)
public class ServiceSQLTest {

    @InjectMocks
    private PlayerGamerServiceImpl underTestService;
    @Mock private IplayerRepository playerRepositorySQL;
    @Mock private IGameRepository gameRepositorySQL;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock
    AuthenticationServiceImpl authenticationMySQLService;


    private PlayerMySQL player1;
    private GameMySQL game1;
    private GameMySQL game2;
    private List<GameMySQL> games ;
    private List<PlayerMySQL> listPlayerMySQL;
    private RegisterRequest registerRequest;

    @BeforeEach
    public void setUp(){
        player1 = new PlayerMySQL(1, "Miquel");

        game1 = new GameMySQL(1, player1);
        game2 = new GameMySQL(2, player1);
        games = Arrays.asList(game1, game2);

        listPlayerMySQL = Arrays.asList(
                new PlayerMySQL(1, "Miquel"),
                new PlayerMySQL(2, "Marta"),
                new PlayerMySQL(3, "Jorge"));

        registerRequest = new RegisterRequest("Miquel", "Debon",
                "mdebonbcn@gmail.com", "password");
    }


    @Test
    public void playerSQLService_findByID_ReturnPlayerDTO(){
        Mockito.when(playerRepositorySQL.findById(1)).thenReturn(Optional.of(player1));

        PlayerGameDTO playerReturned = underTestService.findPlayerDTOById(1);

        Assertions.assertThat(player1).isNotNull();
        Assertions.assertThat(player1.getId()).isEqualTo(playerReturned.getId());
    }

    @Test
    public void gameSQLService_finPlayerByPlayerId_ReturnException(){
        Mockito.when(playerRepositorySQL.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            underTestService.findPlayerDTOById(anyInt());
        });
    }

    @Test
    public void playerSQLService_getAllPlayerDTO_ReturnListPlayersDTO(){
        Mockito.when(playerRepositorySQL.findAll()).thenReturn(listPlayerMySQL);

        List<PlayerGameDTO> actualList = underTestService.getAllPlayersDTO();

        Assertions.assertThat(actualList).isNotEmpty();
        Assertions.assertThat(actualList.size()).isEqualTo(3);
    }


    @Test
    public void playerSQLService_getAllPlayerDTO_ReturnException(){
        Mockito.when(playerRepositorySQL.findAll()).thenReturn(new ArrayList<PlayerMySQL>());
        assertThrows(EmptyDataBaseException.class, () -> {
            underTestService.getAllPlayersDTO();
        });
    }


    @Test
    public void gamesSQLService_findGamesByPlayerId_ReturnGameList(){
        Mockito.when(playerRepositorySQL.existsById(1)).thenReturn(true);
        Mockito.when(gameRepositorySQL.findByPlayerId(1)).thenReturn(games);

        underTestService.findGamesByPlayerId(1);
        Assertions.assertThat(games.size()).isEqualTo(2);
        Assertions.assertThat(games).isNotEmpty();
    }

    @Test
    public void gamesSQLService_findGamesByPlayerId_returnException(){
        Mockito.when(playerRepositorySQL.existsById(anyInt())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () ->
                underTestService.findGamesByPlayerId(anyInt()));
    }


    @Test
    public void gameSQLService_saveGame_returnSavedGame(){
        Mockito.when(playerRepositorySQL.findById(1)).thenReturn(Optional.of(player1));
        Mockito.when(gameRepositorySQL.save(any(GameMySQL.class))).thenReturn(game1);

        GameDTO actualGameDTO = underTestService.saveGame(1);

        Assertions.assertThat(actualGameDTO).isNotNull();
    }

    @Test
    public void gameSQLService_saveGame_returnUserNotFoundException(){
        Mockito.when(playerRepositorySQL.findById(1))
                .thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> {
            underTestService.saveGame(1);
        });

        Mockito.verify(gameRepositorySQL, never()).save(any());
    }

    @Test
    public void playerSQLService_getAllPlayerDTORanking_ReturnListPlayersDTO(){
        Mockito.when(playerRepositorySQL.findAll()).thenReturn(listPlayerMySQL);

        List<PlayerGameDTO> actualList = underTestService.getAllPlayersDTORanking();

        Assertions.assertThat(actualList).isNotEmpty();
        Assertions.assertThat(actualList.size()).isEqualTo(3);
    }
    @Test
    public void playerSQLService_getAllPlayerDTORanking_ReturnException(){
        Mockito.when(playerRepositorySQL.findAll()).thenReturn(new ArrayList<PlayerMySQL>());

        assertThrows(EmptyDataBaseException.class, () -> {
            underTestService.getAllPlayersDTORanking();
        });
    }


    @Test
    public void gameSQLService_deleteGameByPlayerId_ReturnListGameDTO(){
        Mockito.when(playerRepositorySQL.findById(1)).thenReturn(Optional.of(player1));
        Mockito.when(gameRepositorySQL.deleteByPlayerId(1)).thenReturn(games);

        // Delete games
        List<GameDTO> deletedGames = underTestService.deleteGamesByPlayerId(1);

        // Verify
        Assertions.assertThat(deletedGames.size()).isEqualTo(2);
        Mockito.verify(gameRepositorySQL, Mockito.times(1)).deleteByPlayerId(1);
    }
    @Test
    public void gameSQLService_deleteGamesByPlayerId_returnException(){
        Mockito.when(playerRepositorySQL.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                ()-> underTestService.deleteGamesByPlayerId(anyInt()));
    }


    @Test
    public void playerSQLService_GetWorstPlayer_ReturnPlayerGameDTO(){
        Mockito.when(playerRepositorySQL.findAll()).thenReturn(listPlayerMySQL);

        PlayerGameDTO player = underTestService.getWorstPlayer();

        Assertions.assertThat(player).isNotNull();
        Assertions.assertThat(player.getId()).isEqualTo(3);
        Assertions.assertThat(player.getClass()).isEqualTo(PlayerGameDTO.class);
    }

    @Test
    public void playerSQLService_GetBestPlayer_ReturnPlayerGameDTO(){
        Mockito.when(playerRepositorySQL.findAll()).thenReturn(listPlayerMySQL);

        PlayerGameDTO player = underTestService.getBestPlayer();

        Assertions.assertThat(player).isNotNull();
        Assertions.assertThat(player.getId()).isEqualTo(1);
        Assertions.assertThat(player.getClass()).isEqualTo(PlayerGameDTO.class);
    }

    @Test
    public void playerSQL_getAverageTotalMarks_returnDouble(){
        Mockito.when(playerRepositorySQL.findAll()).thenReturn(listPlayerMySQL);

        Double doubleValue = underTestService.averageTotalMarks();

        Assertions.assertThat(doubleValue).isNotNull();
    }

    @Test
    public void playerSQL_getAverageTotalMarks_returnEmptyDBException(){
        Mockito.when(playerRepositorySQL.findAll())
                .thenThrow(EmptyDataBaseException.class);

        assertThrows(EmptyDataBaseException.class,
                ()->underTestService.averageTotalMarks());
    }


    @Test
    public void playerSQLService_updatePlayer_ReturnUpdatedPlayer(){
        Mockito.when(playerRepositorySQL.findByEmail(anyString())).thenReturn(Optional.of(player1));
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn(anyString());

        PlayerGameDTO updatedPlayer = underTestService.updatePlayer(registerRequest, "email");

        Assertions.assertThat(updatedPlayer.getName()).isEqualTo("Miquel");
    }


}
