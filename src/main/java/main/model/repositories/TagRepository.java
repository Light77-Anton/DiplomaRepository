package main.model.repositories;
import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

List<Tag> findAllByNameContaining(String stringTag);

Tag findByNameContaining(String query);

@Query(value = "SELECT COUNT(*) FROM posts", nativeQuery = true)
Double getPostsCount();

@Query(value = "SELECT COUNT(*) / ?2 FROM posts AS p "
        + "LEFT JOIN tag2post AS ttp ON ttp.post_id = p.id "
        + "RIGHT JOIN tags AS t ON t.id = ttp.tag_id "
        + "WHERE t.name = ?1", nativeQuery = true)
Double getIrrationedWeightByTagName(String tagName,double postsCount);

@Query(value = "SELECT MAX(ttp.id) FROM tag2post AS ttp "
        + "INNER JOIN posts AS p ON ttp.post_id = p.id "
        + "INNER JOIN tags AS t ON ttp.tag_id = t.id", nativeQuery = true)
Double getPostsCountWithTheMostPopularTag();












































}
