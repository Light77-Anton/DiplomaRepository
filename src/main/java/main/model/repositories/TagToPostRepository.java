package main.model.repositories;
import main.model.TagToPost;
import main.model.TagToPostId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TagToPostRepository extends JpaRepository<TagToPost, TagToPostId> {

    //List<TagToPost> findByIdPost(int postId);

    //List<TagToPost> findByIdTag(int tagId);

    @Query(value = "SELECT * FROM tag2post AS ttp WHERE ttp.post_id = ?1", nativeQuery = true)
    List<TagToPost> findAllByPostId(int postId);

    @Query(value = "SELECT * FROM tag2post AS ttp WHERE ttp.tag_id = ?1", nativeQuery = true)
    List<TagToPost> findAllByTagId(int tagId);

}
