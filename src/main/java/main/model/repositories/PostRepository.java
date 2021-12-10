package main.model.repositories;
import main.model.Post;
import main.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.TreeSet;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

Page<Post> findByTextContaining(String query, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE DATE(p.time) = STR_TO_DATE(?1, '%Y-%m-%d')", nativeQuery = true)
Page<Post> findByDate(String date, Pageable pageable);

Page<Post> findByTagsContaining(Tag tag, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE YEAR(DATE(p.time)) = YEAR(STR_TO_DATE(?1, '%Y'))", nativeQuery = true)
List<Post> findByYear(String year);

@Query(value = "SELECT YEAR(DATE(p.time)) FROM posts AS p", nativeQuery = true)
TreeSet<Integer> findAllYears();

@Query(value = "SELECT p.* FROM posts AS p "
        + "INNER JOIN post_comments AS pc on p.id = pc.post_id "
        + "GROUP BY p ORDER BY COUNT(pc.id) desc", nativeQuery = true)
Page<Post> findAllAndOrderByCommentariesSize(Pageable pageable); // должен подсчитать кол-во комментов и выводить с большего

// должен вычислить кол-во лайков и выводить с большего
@Query(value = "SELECT p.* FROM posts AS p"
       + "INNER JOIN post_votes AS pv on pv.post_id = p.id AND pv.value = 1 "
       + "GROUP BY p ORDER BY COUNT(pv.id) desc", nativeQuery = true)
Page<Post> findAllAndOrderByVotesCount(Pageable pageable);

@Query("select p from Post p order by p.time desc") // должен выводить с самого раннего по времени
Page<Post> findAllAndOrderByTimeDesc(Pageable pageable);

@Query("select p from Post p order by p.time asc") // должен выводить с самого старого по времени
Page<Post> findAllAndOrderByTimeAsc(Pageable pageable);

}
