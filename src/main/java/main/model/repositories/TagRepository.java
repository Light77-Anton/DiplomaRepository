package main.model.repositories;
import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

List<Tag> findAllByNameContaining(String stringTag);

Tag findByNameContaining(String query);

@Query("with cte_posts_count as (select count(*) from posts) select count(tag.posts) / cte_posts_count from tags tag where tag = :tag") // вычислить ненормированный вес у требуемого тэга
double getIrrationedWeightByTag(@Param("tag")Tag tag);

@Query("with cte_posts_count as (select count(*) from posts) select max(1 / (count(t.posts) / cte_posts_count)) from tags t") // вычислить самый популярный ненормированный тег и поделить на 1
double getTheMostPopularTagWeight();

double getMultiplicationByIrrationedWeightAndTheMostPopularTagWeight(double irrationedWeight,
                                                                     double theMostPopularTagWeight); // Результат умножить на ненормированный вес требуемого тэга










































}
