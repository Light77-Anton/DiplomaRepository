package main.model.repositories;
import main.model.TagToPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TagToPostRepository extends JpaRepository<TagToPost, Integer> {

@Transactional
@Modifying
@Query(value = "INSERT INTO tag2post AS ttp (post_id, tag_id) VALUES (?1, ?2)", nativeQuery = true)
int insertTagToPost(int postId, int tagId);
}
