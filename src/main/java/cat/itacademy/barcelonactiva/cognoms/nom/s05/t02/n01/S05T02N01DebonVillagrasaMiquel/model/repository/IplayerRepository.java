package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.repository;

import cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.model.entity.PlayerMySQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IplayerRepository extends JpaRepository<PlayerMySQL, Integer> {
    boolean existsByEmail(String email);
    Optional<PlayerMySQL> findByEmail(String email);

}
