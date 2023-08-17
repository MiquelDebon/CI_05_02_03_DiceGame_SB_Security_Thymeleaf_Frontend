package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.LoginRequest;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.AuthenticationResponse;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.RegisterRequest;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.ExceptionHandler.BaseDescriptionException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.AuthenticationMySQLService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "IT-Academy - Authentication MySQL")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/mysql/auth")
public class AuthenticationMySQLController {
    //http://localhost:9005/swagger-ui/index.html


    private final AuthenticationMySQLService service;

    @Operation(
            summary = "Register endpoint",
            description = "Description: This method is to register a new player",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = BaseDescriptionException.EMPTY_DATABASE,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "500",
                            description = BaseDescriptionException.E500_INTERNAL_ERROR,
                            content = @Content)
            }
    )
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        try{
            return ResponseEntity.ok(service.register(request));
        }catch (Exception e){
            throw e;
        }
    }


    @Operation(
            summary = "Authentication endpoint",
            description = "Description: This method is to authenticate a new player",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = BaseDescriptionException.EMPTY_DATABASE,
                            content = @Content),
                    @ApiResponse(
                            responseCode = "500",
                            description = BaseDescriptionException.E500_INTERNAL_ERROR,
                            content = @Content)
            }
    )
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody LoginRequest request
    ){
        return ResponseEntity.ok(service.authenticate(request));
    }



}
