package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.RegisterRequest;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions.customExceptions.DuplicateUserEmailException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.exceptions.customExceptions.DuplicateUserNameException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.GameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.PlayerGameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.entity.PlayerMySQL;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.AuthenticationServiceImpl;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.IAuthenticationService;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.IPlayerGamerService;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.PlayerGamerServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/page")
public class pageController {

    private IPlayerGamerService services;
    private IAuthenticationService authService;

    @Autowired
    public pageController(PlayerGamerServiceImpl services, AuthenticationServiceImpl authService) {
        this.services = services;
        this.authService = authService;
    }

    // http://localhost:9005/page/home
    @GetMapping("/home")
    public String homePage(Model model, Authentication authentication) {
        model.addAttribute("title", "We are IT-Dice Game");
        return "home";
    }


    // http://localhost:9005/page/players
    @GetMapping({"/players", "/getAll"})
    public String table(Model model) {
        List<PlayerGameDTO> players = services.getAllPlayersDTORanking();

        model.addAttribute("title", "User player mode: ");
        model.addAttribute("players", players);
        return "players";
    }

    @GetMapping("/play")
    public String playGame(Authentication authentication, Model model){
        PlayerMySQL player = (PlayerMySQL) authentication.getPrincipal();
//        log.info(((PlayerMySQL) authentication.getPrincipal()).getId().toString());
        GameDTO game = services.saveGame(player.getId());
        model.addAttribute("roll", game);

        List<PlayerGameDTO> players = services.getAllPlayersDTORanking();
        model.addAttribute("title", "User player mode: ");
        model.addAttribute("players", players);

        List<GameDTO> playerGames = services.findGamesByPlayerId(player.getId());
        model.addAttribute("games", playerGames);

        return "players";
    }
    @GetMapping("/play/{id}")
    public String playGameID(Authentication authentication, @PathVariable int id , Model model){
        GameDTO game = services.saveGame(id);
        return "redirect:/page/adminArea";
    }



    // http://localhost:9005/page/login
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login page");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(Model model) {
        model.addAttribute("title", "Login page");
        return "login";
    }



    // http://localhost:9005/page/register
    @GetMapping("/register")
    public String register(Model model) {
        RegisterRequest registerRequest = new RegisterRequest();
        model.addAttribute("registerRequest", registerRequest);
        model.addAttribute("title", "Register page");
        return "register";
    }

    @GetMapping("update/{id}")
    public String updateById(@PathVariable int id, Model model){
        model.addAttribute("title", "EDIT a player");
        PlayerMySQL player = services.getPlayer(id);
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstname(player.getName())
                .lastname(player.getSurname())
                .email(player.getEmail())
                .password("password")
                .build();

        model.addAttribute("title", "Edit a player");
        model.addAttribute("registerRequest", registerRequest);

        return "register";
    }


    @PostMapping("/actionRegister")
    public String actionRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest, BindingResult result, Model model)
    {
        model.addAttribute("title", "Register page new");

        try{
            authService.register(registerRequest);
        }catch (DuplicateUserEmailException e){
            return "redirect:/page/register?duplicatedEmail=true";
        }catch (DuplicateUserNameException e){
            return "redirect:/page/register?duplicatedName=true";
        }
        return "redirect:/page/login?registrado=true";
    }


    @GetMapping("/adminArea")
    public String adminArea(Model model){
        model.addAttribute("title", "Admin area: As an admin you have full control of all users ");

        List<PlayerMySQL> listPlayers = services.getAllPlayerMySQL();

        model.addAttribute("players", listPlayers);

        return "admin/home";
    }



    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable int id){
        if(id>0){
            services.deleteGamesByPlayerId(id);
            log.info("Deleted games from USER id '" + id + "' from the database");

            return "redirect:/page/adminArea";
        }else{
            return "error/404";
        }
    }






}
