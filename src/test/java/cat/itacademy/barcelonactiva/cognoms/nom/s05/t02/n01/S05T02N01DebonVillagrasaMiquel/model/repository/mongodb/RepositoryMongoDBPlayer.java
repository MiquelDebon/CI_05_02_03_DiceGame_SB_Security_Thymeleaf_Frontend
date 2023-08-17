package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.repository.mongodb;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.entity.mongodb.PlayerMongoDB;
import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.repository.mongodb.IplayerRepositoryMongoDB;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

@Slf4j
@DataMongoTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RepositoryMongoDBPlayer {

    @Autowired
    private IplayerRepositoryMongoDB playerMongoDBRepository;


    @Test
    public void playerMongoDBRepo_Save_ReturnSavedPlayer(){
        //Arrange
        PlayerMongoDB playerMongoDB = new PlayerMongoDB("Miquel");

        //Act
        PlayerMongoDB savedPlayer = playerMongoDBRepository.save(playerMongoDB);

        //Assert
        Assertions.assertThat(savedPlayer).isNotNull();
        log.info(savedPlayer.toString());
    }

    @Test
    public void playerMongoDBRepo_FindById_ReturnOne(){
        PlayerMongoDB player = new PlayerMongoDB("Miquela");
        playerMongoDBRepository.save(player);
        String id = player.getId();

        PlayerMongoDB savedPlayer = playerMongoDBRepository.findById(id).get();

        Assertions.assertThat(savedPlayer).isNotNull();
        Assertions.assertThat(savedPlayer.getId()).isEqualTo(id);
    }

    @Test
    public void playerMongoDBRepo_UpdatePlayer_ReturnUpdatedPlayer(){
        PlayerMongoDB player = new PlayerMongoDB("Miquel");
        playerMongoDBRepository.save(player);
        String id = player.getId();

        PlayerMongoDB savedPlayer = playerMongoDBRepository.findById(id).get();
        String updatedName = "Manolo";
        savedPlayer.setName(updatedName);

        PlayerMongoDB updatedPlayer = playerMongoDBRepository.save(savedPlayer);

        Assertions.assertThat(updatedPlayer).isNotNull();
        Assertions.assertThat(updatedPlayer.getName()).isEqualTo(updatedName);
    }


    @Test
    public void playerMongoDBRepo_DeleteById_ReturnIsEmpty(){
        PlayerMongoDB player = new PlayerMongoDB("Miquel");
        playerMongoDBRepository.save(player);
        List<PlayerMongoDB> list = playerMongoDBRepository.findAll();

        playerMongoDBRepository.deleteById(player.getId());

        List<PlayerMongoDB> listEmpty = playerMongoDBRepository.findAll();
        Optional<PlayerMongoDB> optional = playerMongoDBRepository.findById(player.getId());

        Assertions.assertThat(list).isNotEmpty();
        Assertions.assertThat(listEmpty).isEmpty();
        Assertions.assertThat(optional).isEmpty();
    }




}
