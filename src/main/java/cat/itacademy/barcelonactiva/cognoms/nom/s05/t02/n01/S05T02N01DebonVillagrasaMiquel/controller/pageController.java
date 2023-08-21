package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.RegisterRequest;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.ExceptionHandler.DuplicateUserEmailException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.ExceptionHandler.DuplicateUserNameException;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.PlayerGameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.entity.PlayerMySQL;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.AuthenticationMySQLService;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.PlayerGamerServiceMySQLImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/page")
public class pageController {

    @Autowired
    private PlayerGamerServiceMySQLImpl services;
    @Autowired
    private AuthenticationMySQLService authService;



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
    public String playGame(Authentication authentication){
        PlayerMySQL player = (PlayerMySQL) authentication.getPrincipal();
//        log.info(((PlayerMySQL) authentication.getPrincipal()).getId().toString());
        services.saveGame(player.getId());
        return "redirect:/page/players";
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

//        if(result.hasErrors()){
//            return "redirect:/page/register";
//        }
        return "redirect:/page/login?registrado=true";
    }


    @GetMapping("/adminArea")
    public String adminArea(Model model){
        model.addAttribute("title", "Admin area");
        return "admin/home";
    }




}
