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

@Query(value = "WITH cte_posts_count as (SELECT count(*) FROM posts) "
           + "SELECT (count(*) / cte_posts_count) FROM posts "
           + "INNER JOIN tag2post on tag2post.postId = posts.id "
           + "INNER JOIN tags on tags.id = tag2post.tagId "
           + "WHERE tags.name = ?1", nativeQuery = true)
double getIrrationedWeightByTagName(String tagName);

@Query(value = "WITH cte_posts_count as (SELECT count(*) FROM posts) "
       + "SELECT MAX(1 / (count(*) / cte_posts_count)) from posts "
       + "INNER JOIN tag2post on tag2post.postId = posts.id "
       + "INNER JOIN tags on tags.id = tag2post.tagId", nativeQuery = true)
double getTheMostPopularTagWeight();










































}
