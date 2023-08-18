package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller.auth.RegisterRequest;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.PlayerGameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.AuthenticationMySQLService;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.PlayerGamerServiceMySQLImpl;
import jakarta.websocket.server.PathParam;
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
        if(authentication != null){
            log.info(authentication.toString());
        }
        model.addAttribute("title", "We are IT-Branch");
        return "home";
    }


    // http://localhost:9005/page/players
    @GetMapping({"/players", "/getAll"})
    public String table(Model model) {
        List<PlayerGameDTO> players = services.getAllPlayersDTORanking();

        model.addAttribute("title", "User player mode: ");
        model.addAttribute("players", players);
        log.info("player.html page");

        return "players";
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
    public String actionRegister(@ModelAttribute RegisterRequest registerRequest, BindingResult result, Model model)
    {
        model.addAttribute("title", "Register page new");
        authService.register(registerRequest);
        return "redirect:/page/register?exito";
    }




}
