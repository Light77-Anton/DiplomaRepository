package main.model.repositories;
import main.model.Post;
import main.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

Page<Post> findByTextContaining(String query, Pageable pageable);

Page<Post> findByTimeEquals(String date, Pageable pageable);

Page<Post> findByTagsContaining(Tag tag, Pageable pageable);

List<Post> findByTimeContaining(String year);

@Query("select p from posts p order by p.commentaries.size() desc") // должен подсчитать кол-во комментов и выводить с большего
Page<Post> findAllAndOrderByCommentariesSize(Pageable pageable);

// должен вычислить кол-во лайков и выводить с большего
//@Query("select p from posts p order by count(p.votes v where v.value = 1) desc")
//@Query("with cte_likes_count as (select count(*) from post_votes v where v.value = 1) select p from posts p order by p.cte_likes_count desc")
@Query("with cte_likes_count as (select count(v) from post_votes v where v.value = 1)"
       + " select p from posts p inner join post_votes on post_votes.postId = posts.id order by p.cte_likes_count desc")
Page<Post> findAllAndOrderByVotesCount(Pageable pageable);

@Query("select p from posts p order by p.time desc") // должен выводить с самого раннего по времени
Page<Post> findAllAndOrderByTimeDesc(Pageable pageable);

@Query("select p from posts p order by p.time asc") // должен выводить с самого старого по времени
Page<Post> findAllAndOrderByTimeAsc(Pageable pageable);

}
