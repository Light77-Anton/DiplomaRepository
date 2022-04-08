package main.model.repositories;
import main.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO post_comments (post_id, user_id, \"time\", text) VALUES (?1, ?2, now(), ?3)", nativeQuery = true)
    void insertComment(int postId, int userId, String text);

}
