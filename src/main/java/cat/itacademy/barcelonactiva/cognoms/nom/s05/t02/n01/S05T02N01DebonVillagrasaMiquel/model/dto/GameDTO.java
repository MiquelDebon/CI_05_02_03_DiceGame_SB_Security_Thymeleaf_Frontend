package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Game DTO Information")
public class GameDTO {
    @Schema(defaultValue = "7", description = "Here you have the mark")
    private int mark;
    @Schema(defaultValue = "WIN/LOSE", description = "Here you know if you have won or lost")
    private String message;

    public GameDTO( int mark) {
        this.mark = mark;
        message = mark > 7 ? "WIN" : "LOSE";
    }
}
