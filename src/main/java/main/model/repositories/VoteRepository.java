package main.model.repositories;
import main.model.Vote;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface VoteRepository extends CrudRepository<Vote, Integer> {

@Query(value = "SELECT * FROM post_votes AS pv WHERE pv.user_id = ?1 AND pv.post_id = ?2", nativeQuery = true)
Optional<Vote> findByUserAndPostId(int userId, int postId);

@Transactional
@Modifying
@Query(value = "INSERT INTO post_votes (user_id, post_id, \"time\", value) VALUES (?1, ?2, now(), ?3)", nativeQuery = true)
void insertVote(int userId, int postId, int value);

@Transactional
@Modifying
@Query(value = "UPDATE post_votes SET value = ?3 WHERE user_id = ?1 AND post_id = ?2", nativeQuery = true)
int changeVote(int userId, int postId, int value);
}
