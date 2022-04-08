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
@Query(value = "UPDATE post_votes SET value = ?3 WHERE user_id = ?1 AND post_id = ?2", nativeQuery = true)
int changeVote(int userId, int postId, int value);

    @Query(value = "SELECT COUNT(pv.id) FROM post_votes AS pv WHERE pv.user_id = ?1 AND pv.value = 1", nativeQuery = true)
    int findLikesCountByUserId(int userId);

    @Query(value = "SELECT COUNT(pv.id) FROM post_votes AS pv WHERE pv.user_id = ?1 AND pv.value = 0", nativeQuery = true)
    int findDislikesCountByUserId(int userId);

    @Query(value = "SELECT COUNT(pv.id) FROM post_votes AS pv WHERE pv.value = 1", nativeQuery = true)
    int findLikesCount();
}
