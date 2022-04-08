package main.model.repositories;
import main.model.TagToPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface TagToPostRepository extends JpaRepository<TagToPost, Integer> {

@Transactional
@Modifying
@Query(value = "INSERT INTO tag2post (post_id, tag_id) VALUES (?1, ?2)", nativeQuery = true)
int insertTagToPost(int postId, int tagId);

@Query(value = "SELECT * FROM tag2post AS ttp WHERE ttp.post_id = ?1", nativeQuery = true)
List<TagToPost> findAllByPostId(int postId);
}
