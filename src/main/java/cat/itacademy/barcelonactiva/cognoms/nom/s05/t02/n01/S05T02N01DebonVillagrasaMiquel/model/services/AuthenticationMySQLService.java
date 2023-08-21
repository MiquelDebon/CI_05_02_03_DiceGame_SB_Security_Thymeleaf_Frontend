package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.LoginRequest;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.AuthenticationResponse;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.RegisterRequest;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.ExceptionHandler.DuplicateUserEmailException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.ExceptionHandler.DuplicateUserNameException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.entity.PlayerMySQL;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.entity.Role;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.repository.IplayerRepositoryMySQL;
//import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.security.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class AuthenticationMySQLService {

    private final IplayerRepositoryMySQL repository;
    private final PasswordEncoder passwordEncoder;
    private final IplayerRepositoryMySQL playerRepository;
//    private final JwtService jwtService;
//    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request){
        try{
            checkDuplicatedEmail(request.getEmail());
            checkDuplicatedName(request.getFirstname());

            PlayerMySQL user;
            if(!request.getEmail().contains("@admin.com")){
                user = buildPlayer(request, Role.USER);
                repository.save(user);
            }else{
                user = buildPlayer(request, Role.ADMIN);
            }
            repository.save(user);

//            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token("jwtToken")
//                    .token(jwtToken)
                    .build();
        }catch (DuplicateUserEmailException e){
            throw new DuplicateUserEmailException("Error duplicated email");
        }catch (DuplicateUserNameException e){
            throw new DuplicateUserNameException("Error duplicated name");
        }
    }

    public AuthenticationResponse authenticate (LoginRequest request){
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(), request.getPassword()
//                )
//        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();

//        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token("jwtToken")
//                    .token(jwtToken)
                .build();
    }



    public void checkDuplicatedEmail(String email){
        if(playerRepository.findAll()
                .stream().map(PlayerMySQL::getEmail)
                .anyMatch((n)-> n.equalsIgnoreCase(email))
        ){
            throw new DuplicateUserEmailException("Duplicated name");
        }
    }

    public void checkDuplicatedName(String name){
        if (
            !name.equalsIgnoreCase("ANONYMOUS")
            &&
            playerRepository.findAll()
                    .stream().map(PlayerMySQL::getName)
                    .anyMatch((n)-> n.equalsIgnoreCase(name))
        ){
            throw new DuplicateUserNameException("Duplicated name");
        }
    }

    public PlayerMySQL buildPlayer(RegisterRequest request, Role role){
        return PlayerMySQL.builder()
                .name(request.getFirstname())
                .surname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .registerDate(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
                        .format(new java.util.Date()))
                .build();
    }



}
