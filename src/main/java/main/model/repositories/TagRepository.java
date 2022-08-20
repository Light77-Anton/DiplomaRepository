package main.model.repositories;
import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

@Query(value = "SELECT * FROM tags WHERE name = ?1", nativeQuery = true)
List<Tag> findAllByNameContaining(String stringTag);

@Query(value = "SELECT * FROM tags WHERE name = ?1", nativeQuery = true)
Optional<Tag> findByNameContaining(String query);

@Query(value = "SELECT COUNT(*) FROM posts", nativeQuery = true)
Double getPostsCount();

@Query(value = "SELECT COUNT(*) / ?2 FROM posts AS p "
        + "LEFT JOIN tag2post AS ttp ON ttp.post_id = p.id "
        + "RIGHT JOIN tags AS t ON t.id = ttp.tag_id "
        + "WHERE t.name = ?1", nativeQuery = true)
Double getIrrationedWeightByTagName(String tagName, double postsCount);

}
