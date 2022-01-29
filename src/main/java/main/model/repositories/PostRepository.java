package main.model.repositories;
import main.model.Post;
import main.model.Tag;
import main.support.dto.CountForPostId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.TreeSet;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

List<Post> findByTextContaining(String query, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE DATE(p.time) = TO_DATE(?1, 'YYYY-MM-DD')", nativeQuery = true)
List<Post> findByDate(String date, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p "
        + "LEFT JOIN tag2post AS ttp ON ttp.post_id = p.id "
        + "LEFT JOIN tags AS t ON ttp.tag_id = t.id", nativeQuery = true)
Page<Post> findByTagContaining(Tag tag, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE EXTRACT(YEAR FROM DATE(p.time)) = EXTRACT(YEAR FROM TO_DATE(?1, 'YYYY'))", nativeQuery = true)
List<Post> findByYear(String year);

@Query(value = "SELECT EXTRACT(YEAR FROM DATE(p.time)) FROM posts AS p", nativeQuery = true)
TreeSet<Integer> findAllYears();

@Query(value = "SELECT new main.support.dto.CountForPostId(p.id, COUNT(pv.id)) FROM posts AS p "
        + "LEFT JOIN post_comments AS pc ON pc.post_id = p.id "
        + "GROUP BY p.id ORDER BY COUNT(pc.id) desc", nativeQuery = true)
List<CountForPostId> findAllAndOrderByCommentariesSize(Pageable pageable);

@Query(value = "SELECT new main.support.dto.CountForPostId(p.id, COUNT(pv.id)) FROM posts AS p "
        + "LEFT JOIN post_votes AS pv ON pv.post_id = p.id AND pv.value = 1 "
        + "GROUP BY p.id ORDER BY COUNT(pv.id) desc", nativeQuery = true)
List<CountForPostId> findAllAndOrderByVotesCount(Pageable pageable);

@Query(value = "SELECT * FROM posts AS p ORDER BY p.time DESC", nativeQuery = true)
List<Post> findAllAndOrderByTimeDesc(Pageable pageable);

@Query(value = "SELECT * FROM posts AS p ORDER BY p.time ASC", nativeQuery = true)
List<Post> findAllAndOrderByTimeAsc(Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE p.is_active = false AND p.user_id = ?1", nativeQuery = true)
Page<Post> findAllInactivePosts(int userId, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE p.is_active = true AND p.moderation_status = 'NEW' AND p.user_id = ?1", nativeQuery = true)
Page<Post> findAllPendingPosts(int userId, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE p.is_active = true AND p.moderation_status = 'DECLINED' AND p.user_id = ?1", nativeQuery = true)
Page<Post> findAllDeclinedPosts(int userId, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.user_id = ?1", nativeQuery = true)
Page<Post> findAllAcceptedPosts(int userId, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE p.moderation_status = 'NEW'", nativeQuery = true)
List<Post> findAllNewPosts();

@Transactional
@Modifying
@Query(value = "UPDATE posts SET view_count = ?1 WHERE id = ?2", nativeQuery = true)
int setNewViewCount(int newViewCount, int postId);
}
