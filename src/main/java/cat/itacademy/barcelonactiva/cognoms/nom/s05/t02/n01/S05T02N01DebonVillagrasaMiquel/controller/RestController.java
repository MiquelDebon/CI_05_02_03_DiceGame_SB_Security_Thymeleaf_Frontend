package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.controller;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto.GameDTO;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.services.IPlayerGamerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping
public class RestController {

    private IPlayerGamerService services;

    @Autowired
    public RestController(IPlayerGamerService services) {
        this.services = services;
    }

    @GetMapping("/paginable")
    public List<GameDTO> getPage(
            @RequestParam("id") int id,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("pageSize") int pageSize
    ){
        return services.getGamePagination(id, pageNo, pageSize);
    }



}
