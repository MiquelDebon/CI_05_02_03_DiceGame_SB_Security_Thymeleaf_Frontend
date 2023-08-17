package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.RegisterRequest;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.ExceptionHandler.*;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.GameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.PlayerGameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.PlayerGamerServiceMySQLImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "IT-Academy - MySQL", description = "MySQL Controller methods to deal with the Game")
@RestController
@RequestMapping("players")
public class DiceControllerMySQL {
    //http://localhost:9005/swagger-ui/index.html

    @Autowired
    private PlayerGamerServiceMySQLImpl PGService;



    /**
     *  🟠PUT  Modifica el nom del jugador/a.
     *   🔗http://localhost:9005/players
     */
    @Operation(
            summary = "Update one player | Authorized: Admin or User with ID = ID_Token_User_Assigned",
            description = "Description: This method update a new player in the database",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response",
                            content = @Content(schema = @Schema(implementation = PlayerGameDTO.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request buddy",
                            content = @Content),
                    @ApiResponse(
                            responseCode = "403",
                            description = BaseDescriptionException.E403_DESCRIPTION,
                            content = @Content
                    )
            },
            security = {@SecurityRequirement(name = "Bearer Authentication")}
    )
    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<?> updatePlayer(@PathVariable int id, @RequestBody RegisterRequest request){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String username = userDetails.getUsername();
        log.info(username);

        try{
            PlayerGameDTO updatedDTO = PGService.updatePlayer(request, username);
            return new ResponseEntity<>(updatedDTO, HttpStatus.OK);

        }catch (UserNotFoundException | DuplicateUserNameException | DuplicateUserEmailException e){
            throw e;
        }catch (Exception e){
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     *  🟢POST un jugador/a específic realitza una tirada dels daus.
     *  @see <a href="http://localhost:9005/players/2/games"> 🔗 http://localhost:9005/players/2/games </a>
     */
    @Operation(
            summary = "Play by ID player | Authorized: Admin or User with ID = ID_Token_User_Assigned",
            description = "Description: This method is to play a round",
            parameters = @Parameter(
                    name = "id",
                    description = "PLay the game by PlayerID",
                    required = true),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response",
                            content = @Content(schema = @Schema(implementation = GameDTO.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = "404",
                            description = BaseDescriptionException.NO_USER_BY_THIS_ID,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request buddy",
                            content = @Content),
                    @ApiResponse(
                            responseCode = "403",
                            description = BaseDescriptionException.E403_DESCRIPTION,
                            content = @Content
                    )
            },
            security = {@SecurityRequirement(name = "Bearer Authentication")}
    )
    @PostMapping("/{id}/games")
    @PreAuthorize("#id == authentication.principal.id or hasAuthority('ADMIN')")
    //This Annotation allows only the authorised user to use this method or an Admin user
    public ResponseEntity<?> playGame(@PathVariable int id){
        try{
            GameDTO gameDTO = PGService.saveGame(id);
            return new ResponseEntity<>(gameDTO, HttpStatus.OK);
        }catch (UserNotFoundException e){
            throw e;
        }catch (Exception e){
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     *  🔵GET   Retorna el llistat de tots  els jugadors/es del sistema amb el seu  percentatge mitjà d’èxits.
     *  @see <a href="http://localhost:9005/players"> 🔗http://localhost:9005/players</a>
     */
    @Operation(
            summary = "Get all players and their average mark | Authorized: Admin and all User",
            description = "Description: This method retrieve all the player with their average mark",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response",
                            content = @Content(schema = @Schema(implementation = PlayerGameDTO.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = "204",
                            description = BaseDescriptionException.EMPTY_DATABASE,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "403",
                            description = BaseDescriptionException.E403_DESCRIPTION,
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal error",
                            content = @Content)
            },
            security = {@SecurityRequirement(name = "Bearer Authentication")}
    )
    @GetMapping()
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllPlayers(){
        try{
            List<PlayerGameDTO> returnList = PGService.getAllPlayersDTO();
            log.info("Controller - get All Players");
            return new ResponseEntity<>(returnList, HttpStatus.OK);
        }catch (EmptyDataBaseException e){
            throw e;
        }catch (RuntimeException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     *  🔵GET Retorna el llistat de jugades per un jugador/a.
     *  @see <a href="http://localhost:9005/players/2"> 🔗http://localhost:9005/players/2</a>
     */
    @Operation(
            summary = "All games from player | Authorized: Admin or User with ID = ID_Token_User_Assigned",
            description = "Description: This method retrieve all the games from the database by player ID",
            parameters = @Parameter(
                    name = "id", description = "ID player",
                    required = true, example = "1"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response",
                            content = @Content(schema = @Schema(implementation = GameDTO.class), mediaType = "application/json")),
                    @ApiResponse(
                            responseCode = "404",
                            description = BaseDescriptionException.NO_USER_BY_THIS_ID,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request buddy",
                            content = @Content),
                    @ApiResponse(
                            responseCode = "403",
                            description = BaseDescriptionException.E403_DESCRIPTION,
                            content = @Content
                    )
            },
            security = {@SecurityRequirement(name = "Bearer Authentication")}
    )
    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<?> getGamePlayer(@PathVariable int id){
        try{
            List<GameDTO> returnList =  PGService.findGamesByPlayerId(id);
            return new ResponseEntity<>(returnList, HttpStatus.OK);
        } catch (UserNotFoundException e){
            throw e;
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     *  🔴DELETE Elimina les tirades del jugador/a.
     *  🔗http://localhost:9005/players/2/games
     */
    @Operation(
            summary = "Delete all the games from player by id | Authorized: Admin or User with ID = ID_Token_User_Assigned",
            description = "Description: This method deletes all the games in the database from a player",
            parameters = @Parameter(
                    name = "id", description = "ID player",
                    required = true, example = "1"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response",
                            content = @Content(schema = @Schema(implementation = PlayerGameDTO.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request buddy",
                            content = @Content),
                    @ApiResponse(
                            responseCode = "403",
                            description = BaseDescriptionException.E403_DESCRIPTION,
                            content = @Content
                    )
            },
            security = {@SecurityRequirement(name = "Bearer Authentication")}
    )
    @DeleteMapping("/{id}/games")
    @PreAuthorize("#id == authentication.principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<?> deletePlayerGames(@PathVariable int id){
        try{
            PGService.deleteGamesByPlayerId(id);
            PlayerGameDTO player = PGService.findPlayerDTOById(id);
            return new ResponseEntity<>(player, HttpStatus.OK);
        }catch (UserNotFoundException e){
            throw e;
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     *  🔵 GET Retorna el ranking mig de tots els jugadors/es del sistema. És a dir, el  percentatge mitjà d’èxits.
     *  @see <a href="http://localhost:9005/players/ranking"> 🔗http://localhost:9005/players/ranking</a>
     */

    @Operation(
            summary = "Get the ranking of the players | Authorized: Admin or all Users",
            description = "Description: This method retrieve all the player order by their position ranking",
            parameters = @Parameter(
                    name = "id",
                    description = "ID player",
                    required = true,
                    example = "1"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response",
                            content = @Content(schema = @Schema(implementation = PlayerGameDTO.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = "204",
                            description = BaseDescriptionException.EMPTY_DATABASE,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request buddy",
                            content = @Content),
                    @ApiResponse(
                            responseCode = "403",
                            description = BaseDescriptionException.E403_DESCRIPTION,
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = BaseDescriptionException.E500_INTERNAL_ERROR,
                            content = @Content)
            },
            security = {@SecurityRequirement(name = "Bearer Authentication")}
    )
    @GetMapping("/ranking")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> getRankingPlayers(){
        try{
            List<PlayerGameDTO> returnList = PGService.getAllPlayersDTORanking();
            return new ResponseEntity<>(returnList, HttpStatus.OK);
        }catch (EmptyDataBaseException e){
            throw e;
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     *  🔵 GET Retorna el jugador/a  amb pitjor percentatge d’èxit.
     *  @see <a href="http://localhost:9005/players/ranking/loser"> 🔗http://localhost:9005/players/ranking/loser</a>
     */
    @Operation(
            summary = "The worst player | Authorized: Admin and all Users",
            description = "Description: This method retrieve the worst player by the average mark",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response",
                            content = @Content(schema = @Schema(implementation = PlayerGameDTO.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = "404",
                            description = BaseDescriptionException.EMPTY_DATABASE,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "403",
                            description = BaseDescriptionException.E403_DESCRIPTION,
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = BaseDescriptionException.E500_INTERNAL_ERROR,
                            content = @Content)
            },
            security = {@SecurityRequirement(name = "Bearer Authentication")}
    )
    @GetMapping("/ranking/loser")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> getWorstPlayer(){
        try{
            PlayerGameDTO player = PGService.getWorstPlayer();
            return new ResponseEntity<>(player, HttpStatus.OK);
        }catch (EmptyDataBaseException e){
            throw e;
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     *  🔵 GET Retorna el  jugador amb millor percentatge d’èxit.
     *  @see <a href="http://localhost:9005/players/ranking/winnerr"> 🔗http://localhost:9005/players/ranking/winner</a>
     */
    @Operation(
            summary = "The best player | Authorized: Admin and all Users",
            description = "Description: This method retrieve the best player by the average mark",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response",
                            content = @Content(schema = @Schema(implementation = PlayerGameDTO.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = "404",
                            description = BaseDescriptionException.EMPTY_DATABASE,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "403",
                            description = BaseDescriptionException.E403_DESCRIPTION,
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = BaseDescriptionException.E500_INTERNAL_ERROR,
                            content = @Content)
            },
            security = {@SecurityRequirement(name = "Bearer Authentication")}
    )
    @GetMapping("/ranking/winner")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> getBestPlayer(){
        try{
            PlayerGameDTO player =  PGService.getBestPlayer();
            return new ResponseEntity<>(player, HttpStatus.OK);
        }catch (EmptyDataBaseException e){
            throw e;
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Average success mark from all players | Authorized: Admin",
            description = "Description: This method retrieve the average mark of success from all player",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response",
                            content = @Content(schema = @Schema(implementation = PlayerGameDTO.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = "204",
                            description = BaseDescriptionException.EMPTY_DATABASE,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "403",
                            description = BaseDescriptionException.E403_DESCRIPTION,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "500",
                            description = BaseDescriptionException.E500_INTERNAL_ERROR,
                            content = @Content)
            },
            security = {@SecurityRequirement(name = "Bearer Authentication")}
    )
    @GetMapping("/totalAverageMark")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAverageTotalMark(){
        Double averageMark = PGService.averageTotalMarks();
        try{
            Double result = Math.round(averageMark * 100.00) / 100.00;
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (EmptyDataBaseException e){
            throw e;
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





}
