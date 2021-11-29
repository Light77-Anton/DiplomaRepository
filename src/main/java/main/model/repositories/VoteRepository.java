package main.model.repositories;
import main.model.Vote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends CrudRepository<Vote, Integer> {

}
