package main.model.repositories;
import main.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    /*
@Query(value = "SELECT * FROM post_comments AS c WHERE c.post_id = ?1", nativeQuery = true)
Optional<Comment> findByPostId(int postId);

     */

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO post_comments (post_id, user_id, \"time\", text) VALUES (?1, ?2, now(), ?3)", nativeQuery = true)
    void insertComment(int postId, int userId, String text);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO post_comments (parent_id, post_id, user_id, \"time\", text) VALUES (?1, ?2, ?3, now(), ?4)", nativeQuery = true)
    void insertQuoteToComment(int parentId, int postId, int userId, String text);

@Query(value = "SELECT MAX(c.id) FROM post_comments AS c WHERE c.user_id = ?1", nativeQuery = true)
Integer findLastCommentByUserId(int userId);
}
