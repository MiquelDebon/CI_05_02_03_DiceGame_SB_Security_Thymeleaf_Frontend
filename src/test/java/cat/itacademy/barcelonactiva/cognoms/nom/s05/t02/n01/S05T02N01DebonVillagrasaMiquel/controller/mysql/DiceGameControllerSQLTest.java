package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.mysql;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.DiceControllerMySQL;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.RegisterRequest;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.ExceptionHandler.ApiExceptionHandler;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.ExceptionHandler.EmptyDataBaseException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.ExceptionHandler.UserNotFoundException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.GameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.PlayerGameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.entity.PlayerMySQL;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.PlayerGamerServiceMySQLImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class DiceGameControllerSQLTest {

    //We need to fake HTTP requests. So we will autowire a MockMvc bean which Spring Boot autoconfigures.
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private DiceControllerMySQL underTestController;
    @Mock
    private PlayerGamerServiceMySQLImpl service;
    @Mock
    private SecurityContextHolder securityContextHolder;
    @Autowired
    private ObjectMapper objectMapper;

    private PlayerMySQL player;
    private List<PlayerGameDTO> listPlayerGameDTO;
    private GameDTO gameDTO;
    private PlayerGameDTO playerGameDTO;
    private List<GameDTO> listGameDTO;
    private RegisterRequest registerRequest;
    private UserDetails userDetails;
    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(underTestController)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        player = PlayerMySQL.builder().id(1).name("Miquel").build();
        playerGameDTO = PlayerGameDTO.builder().id(1).name("Miquel").averageMark(2).build();
        gameDTO = new GameDTO(3);
        listPlayerGameDTO = Arrays.asList(
                new PlayerGameDTO(1, "Miquel", 0,"0"),
                new PlayerGameDTO(2, "Marta", 5, "5"),
                new PlayerGameDTO(3, "Julia", 10, "10"));
        listGameDTO = Arrays.asList(
                new GameDTO(1),
                new GameDTO(2),
                new GameDTO(3));

        registerRequest = new RegisterRequest("Miquel", "Debon", "mdebonbcn@gmail.com", "password");
    }


    @Test
    public void diceController_playGame_returnGameDTO() throws Exception{

        given(service.saveGame(player.getId())).willReturn(gameDTO);
        mockMvc.perform(post("/players/{id}/games", player.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gameDTO)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mark").value(gameDTO.getMark()))
                .andDo(print());

        given(service.saveGame(player.getId())).willThrow(UserNotFoundException.class);
        mockMvc.perform(post("/players/{id}/games", player.getId()))
                .andExpect(status().isNotFound());
    }


    @Test
    public void diceController_getAllPlayers_returnListPlayerGameDTO() throws Exception{
        given(service.getAllPlayersDTO()).willReturn(listPlayerGameDTO);
        mockMvc.perform(get("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(listPlayerGameDTO)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(listPlayerGameDTO.size()))
                .andExpect(jsonPath("$.[0].name").value(listPlayerGameDTO.get(0).getName()))
                .andDo(print());

        given(service.getAllPlayersDTO()).willThrow(EmptyDataBaseException.class);
        mockMvc.perform(get("/players"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }


    @Test
    public void diceController_getGamePlayer_returnListGameDTO() throws Exception{
        given(service.findGamesByPlayerId(player.getId())).willReturn(listGameDTO);
        mockMvc.perform(get("/players/{id}", player.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(listGameDTO)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()" ).value(listGameDTO.size()))
                .andExpect(jsonPath("$.[0].mark").value(listGameDTO.get(0).getMark()))
                .andDo(print());

        given(service.findGamesByPlayerId(player.getId())).willThrow(UserNotFoundException.class);
        mockMvc.perform(get("/players/{id}", player.getId()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }


    @Test
    public void diceController_deleteGameById_returnUpdatedPlayerGameDTO() throws Exception{
        given(service.findPlayerDTOById(player.getId())).willReturn(playerGameDTO);
        mockMvc.perform(delete("/players/{id}/games",player.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(playerGameDTO)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name" ).value(playerGameDTO.getName()))
                .andDo(print());

        given(service.findPlayerDTOById(player.getId())).willThrow(UserNotFoundException.class);
        mockMvc.perform(delete("/players/{id}/games", player.getId()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void diceController_getRankingPlayers_returnListPlayerByRanking() throws Exception{
        given(service.getAllPlayersDTORanking()).willReturn(listPlayerGameDTO );
        mockMvc.perform(get("/players/ranking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(listPlayerGameDTO)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(listPlayerGameDTO.size()))
                .andDo(print());

        given(service.getAllPlayersDTORanking()).willThrow(EmptyDataBaseException.class);
        mockMvc.perform(get("/players/ranking"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }


    @Test
    public void diceController_getWorstPlayer_returnPlayer() throws Exception{
        given(service.getWorstPlayer()).willReturn(playerGameDTO);
        mockMvc.perform(get("/players/ranking/loser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(playerGameDTO)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id" ).value(playerGameDTO.getId()))
                .andExpect(jsonPath("$.name" ).value(playerGameDTO.getName()))
                .andExpect(jsonPath("$.averageMark" ).value(playerGameDTO.getAverageMark()))
                .andDo(print());

        given(service.getWorstPlayer()).willThrow(EmptyDataBaseException.class);
        mockMvc.perform(get("/players/ranking/loser"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void diceController_getBestPlayer_returnPlayer() throws Exception{
        given(service.getBestPlayer()).willReturn(playerGameDTO);
        mockMvc.perform(get("/players/ranking/winner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(playerGameDTO)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id" ).value(playerGameDTO.getId()))
                .andExpect(jsonPath("$.name" ).value(playerGameDTO.getName()))
                .andExpect(jsonPath("$.averageMark" ).value(playerGameDTO.getAverageMark()))
                .andDo(print());

        given(service.getBestPlayer()).willThrow(EmptyDataBaseException.class);
        mockMvc.perform(get("/players/ranking/winner"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }


    @Test
    public void diceController_getAverageTotalMark_returnAverageMark() throws Exception{
        given(service.averageTotalMarks()).willReturn(5d);
        mockMvc.perform(get("/players/totalAverageMark")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(Double.class)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }
    @Test
    public void diceController_getAverageTotalMark_returnEmptyException() throws Exception{
        given(service.averageTotalMarks()).willThrow(EmptyDataBaseException.class);
        mockMvc.perform(get("/players/totalAverageMark"))

                .andExpect(status().isNoContent())
                .andDo(print());
    }


    @Test
    @Disabled
    public void diceController_updatePlayer_returnUpdatedPlayerDTO() throws Exception{
        given(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).willReturn(userDetails);

        given(service.updatePlayer(registerRequest, registerRequest.getEmail())).willReturn(playerGameDTO);
        mockMvc.perform(put("/players/{id}", player.getId(), registerRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(playerGameDTO)))

                .andExpect(status().isOk());
    }


}
