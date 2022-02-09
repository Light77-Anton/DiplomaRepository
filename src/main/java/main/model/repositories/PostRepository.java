package main.model.repositories;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.Tag;
import main.dto.CountForPostId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

@Query(value = "SELECT COUNT(pv) FROM posts AS p INNER JOIN post_votes AS pv ON pv.post_id = p.id WHERE pv.value = 1 AND p.id = ?1", nativeQuery = true)
int findLikeCountById(int postId);

@Query(value = "SELECT COUNT(pv) FROM posts AS p INNER JOIN post_votes AS pv ON pv.post_id = p.id WHERE pv.value = 0 AND p.id = ?1", nativeQuery = true)
int findDislikeCountById(int postId);

@Query(value = "SELECT COUNT(p.view_count) FROM posts AS p WHERE p.id = ?1", nativeQuery = true)
int findViewCountById(int postId);

@Query(value = "SELECT SUBSTRING(p.text, 0, 150) FROM posts AS p WHERE p.id = ?1", nativeQuery = true)
String extractAnnounceFromTextById(int postId);

@Query(value = "SELECT * FROM posts AS p WHERE p.text LIKE '%' || ?1 || '%' AND p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now()", nativeQuery = true)
Page<Post> findByTextContaining(String query, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE DATE(p.time) = TO_DATE(?1, 'YYYY-MM-DD') AND p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now()", nativeQuery = true)
Page<Post> findByDate(String date, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p "
        + "LEFT JOIN tag2post AS ttp ON ttp.post_id = p.id "
        + "LEFT JOIN tags AS t ON ttp.tag_id = t.id "
        + "WHERE ttp.tag_id = ?1", nativeQuery = true)
Page<Post> findByTagContaining(int tagId, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE EXTRACT(YEAR FROM DATE(p.time)) = EXTRACT(YEAR FROM TO_DATE(?1, 'YYYY')) WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now()", nativeQuery = true)
List<Post> findByYear(String year);

@Query(value = "SELECT EXTRACT(YEAR FROM DATE(p.time)) FROM posts AS p WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now()", nativeQuery = true)
TreeSet<Integer> findAllYears();

@Query(value = "SELECT new CountForPostId(p.id, COUNT(pv.id)) FROM posts AS p "
        + "LEFT JOIN post_comments AS pc ON pc.post_id = p.id "
        + "WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now() "
        + "GROUP BY p.id ORDER BY COUNT(pc.id) desc", nativeQuery = true)
Page<CountForPostId> findAllAndOrderByCommentariesSize(Pageable pageable);

@Query(value = "SELECT new CountForPostId(p.id, COUNT(pv.id)) FROM posts AS p "
        + "LEFT JOIN post_votes AS pv ON pv.post_id = p.id AND pv.value = 1 "
        + "WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now() "
        + "GROUP BY p.id ORDER BY COUNT(pv.id) desc", nativeQuery = true)
Page<CountForPostId> findAllAndOrderByVotesCount(Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now() ORDER BY p.time DESC", nativeQuery = true)
Page<Post> findAllAndOrderByTimeDesc(Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now() ORDER BY p.time ASC", nativeQuery = true)
Page<Post> findAllAndOrderByTimeAsc(Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE p.is_active = false AND p.user_id = ?1", nativeQuery = true)
Page<Post> findAllInactivePosts(int userId, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE p.is_active = true AND p.moderation_status = 'NEW' AND p.user_id = ?1", nativeQuery = true)
Page<Post> findAllPendingPosts(int userId, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE p.is_active = true AND p.moderation_status = 'DECLINED' AND p.user_id = ?1", nativeQuery = true)
Page<Post> findAllDeclinedPosts(int userId, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.user_id = ?1", nativeQuery = true)
Page<Post> findAllAcceptedPosts(int userId, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE p.moderation_status = 'NEW'", nativeQuery = true)
List<Post> findAllNewPostsAsList();

@Query(value = "SELECT * FROM posts AS p WHERE p.moderation_status = 'NEW' AND p.is_active = true", nativeQuery = true)
Page<Post> findAllNewPostsAsPage(Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE p.moderation_status = 'ACCEPTED' AND p.moderator_id = ?1", nativeQuery = true)
Page<Post> findAllAcceptedPostsByMe(int myId, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE p.moderation_status = 'DECLINED' AND p.moderator_id = ?1", nativeQuery = true)
Page<Post> findAllDeclinedPostsByMe(int myId, Pageable pageable);

@Transactional
@Modifying
@Query(value = "UPDATE posts SET view_count = ?1 WHERE id = ?2", nativeQuery = true)
int setNewViewCount(int newViewCount, int postId);

@Transactional
@Modifying
@Query(value = "INSERT INTO posts (is_active, moderation_status, user_id, \"time\", title, text, view_count) VALUES (?1, ?2, ?3, ?4, ?5, ?6, 0)", nativeQuery = true)
void insertPost(boolean isActive, ModerationStatus status, int userId, LocalDateTime ldt, String title, String text);

@Query(value = "SELECT * FROM posts AS p WHERE p.id = ?1 AND p.user_id = ?2", nativeQuery = true)
Optional<Post> findByIdAndUserId(int id, int userId);

@Transactional
@Modifying
@Query(value = "UPDATE posts SET is_active = ?2, \"time\" = ?3, title = ?4, text = ?5 WHERE id = ?1", nativeQuery = true)
int updatePost(int id, boolean isActive, LocalDateTime ldt, String title, String text);

@Transactional
@Modifying
@Query(value = "UPDATE posts SET moderation_status = ?1, moderator_id = ?2 WHERE id = ?3", nativeQuery = true)
int moderatePost(ModerationStatus status, int moderatorId, int postId);

@Query(value = "SELECT COUNT(pv) FROM posts AS p INNER JOIN post_votes AS pv ON pv.post_id = p.id WHERE p.user_id = ?1 AND pv.value = 1", nativeQuery = true)
int findLikesCountByUserId(int userId);

@Query(value = "SELECT COUNT(pv) FROM posts AS p INNER JOIN post_votes AS pv ON pv.post_id = p.id WHERE p.user_id = ?1 AND pv.value = 0", nativeQuery = true)
int findDislikesCountByUserId(int userId);

@Query(value = "SELECT COUNT(p.view_count) FROM posts AS p WHERE p.user_id = ?1", nativeQuery = true)
int findViewCountByUserId(int userId);

@Query(value = "SELECT MIN(p.time) FROM posts AS p WHERE p.user_id = ?1", nativeQuery = true)
LocalDateTime findTheOldestPublicationTimeByUserId(int userId);

@Query(value = "SELECT COUNT(pv) FROM posts AS p INNER JOIN post_votes AS pv WHERE pv.value = 1", nativeQuery = true)
int findLikesCount();

@Query(value = "SELECT COUNT(pv) FROM posts AS p INNER JOIN post_votes AS pv WHERE pv.value = 0", nativeQuery = true)
int findDislikeCount();

@Query(value = "SELECT COUNT(p.view_count) FROM posts AS p", nativeQuery = true)
int findViewCount();

@Query(value = "SELECT MIN(p.time) FROM posts AS p", nativeQuery = true)
LocalDateTime findTheOldestPublicationTime();

@Query(value = "SELECT MAX(p.id) FROM posts AS p WHERE p.user_id = ?1", nativeQuery = true)
int findLastPostIdByUserId(int userId);
}
