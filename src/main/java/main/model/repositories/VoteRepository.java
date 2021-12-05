package main.model.repositories;
import main.model.Vote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends CrudRepository<Vote, Integer> {

//List<Vote> findAllByValue(int value);
}
