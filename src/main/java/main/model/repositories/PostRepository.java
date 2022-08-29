package main.model.repositories;
import main.model.Post;
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

    @Query(value = "SELECT COUNT(*) FROM posts", nativeQuery = true)
    double findPostsCount();

    @Query(value = "SELECT * FROM posts AS p WHERE p.user_id = ?1", nativeQuery = true)
    List<Post> findAllMyPosts(int userId);

    @Query(value = "SELECT * FROM posts AS p WHERE p.is_active = true", nativeQuery = true)
    List<Post> findAllActivePosts();

    @Query(value = "SELECT COUNT(pv) FROM posts AS p INNER JOIN post_votes AS pv ON pv.post_id = p.id WHERE pv.value = 1 AND p.id = ?1", nativeQuery = true)
    int findLikeCountById(int postId);

    @Query(value = "SELECT COUNT(pv) FROM posts AS p INNER JOIN post_votes AS pv ON pv.post_id = p.id WHERE pv.value = 0 AND p.id = ?1", nativeQuery = true)
    int findDislikeCountById(int postId);

    @Query(value = "SELECT p.view_count FROM posts AS p WHERE p.id = ?1", nativeQuery = true)
    int findViewCountById(int postId);

    @Query(value = "SELECT SUBSTRING(p.text, 0, 150) FROM posts AS p WHERE p.id = ?1", nativeQuery = true)
    String extractAnnounceFromTextById(int postId);

    @Query(value = "SELECT * FROM posts AS p WHERE p.text LIKE '%' || ?1 || '%' AND p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time < now() ORDER BY p.time DESC", nativeQuery = true)
    Page<Post> findByTextContaining(String query, Pageable pageable);

    @Query(value = "SELECT * FROM posts AS p WHERE DATE(p.time) = TO_DATE(?1, 'YYYY-MM-DD') AND p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time < now()", nativeQuery = true)
    Page<Post> findByDate(String date, Pageable pageable);

    @Query(value = "SELECT * FROM posts AS p "
            + "RIGHT JOIN tag2post AS ttp ON ttp.post_id = p.id "
            + "RIGHT JOIN tags AS t ON ttp.tag_id = t.id "
            + "WHERE ttp.tag_id = ?1 AND p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time < now()", nativeQuery = true)
    Page<Post> findByTagContaining(int tagId, Pageable pageable);

    @Query(value = "SELECT * FROM posts AS p WHERE EXTRACT(YEAR FROM DATE(p.time)) = EXTRACT(YEAR FROM TO_DATE(?1, 'YYYY')) AND p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time < now()", nativeQuery = true)
    List<Post> findByYear(String year);

    @Query(value = "SELECT EXTRACT(YEAR FROM DATE(p.time)) FROM posts AS p WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time < now()", nativeQuery = true)
    TreeSet<Integer> findAllYears();

    @Query(value = "SELECT * FROM posts AS p WHERE p.moderation_status = 'ACCEPTED' AND p.is_active = true AND p.time < now() " +
            "ORDER BY (SELECT COUNT(*) FROM post_comments AS c WHERE c.post_id = p.id) DESC", nativeQuery = true)
    Page<Post> findAllAndOrderByCommentariesSize(Pageable pageable);

    @Query(value = "SELECT * FROM posts AS p WHERE p.moderation_status = 'ACCEPTED' AND p.is_active = true AND p.time < now() " +
            "ORDER BY (SELECT COUNT(*) FROM post_votes AS v WHERE v.post_id = p.id AND v.value = 1) DESC", nativeQuery = true)
    Page<Post> findAllAndOrderByVotesCount(Pageable pageable);

    @Query(value = "SELECT * FROM posts AS p WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time < now() ORDER BY p.time DESC", nativeQuery = true)
    Page<Post> findAllAndOrderByTimeDesc(Pageable pageable);

    @Query(value = "SELECT * FROM posts AS p WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time < now() ORDER BY p.time ASC", nativeQuery = true)
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
    Page<Post> findAllAcceptedPostsByMe(int userId, Pageable pageable);

    @Query(value = "SELECT * FROM posts AS p WHERE p.moderation_status = 'DECLINED' AND p.moderator_id = ?1", nativeQuery = true)
    Page<Post> findAllDeclinedPostsByMe(int userId, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "UPDATE posts SET view_count = ?1 WHERE id = ?2", nativeQuery = true)
    void setNewViewCount(int newViewCount, int postId);

    @Query(value = "SELECT * FROM posts AS p WHERE p.id = ?1 AND p.user_id = ?2", nativeQuery = true)
    Optional<Post> findByIdAndUserId(int id, int userId);

    @Query(value = "SELECT * FROM posts AS p WHERE p.id = ?1 AND p.moderator_id = ?2", nativeQuery = true)
    Optional<Post> findByIdAndModeratorId(int id, int moderatorId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE posts SET is_active = ?2, \"time\" = ?3, title = ?4, text = ?5, moderation_status = ?6 WHERE id = ?1", nativeQuery = true)
    void updatePost(int id, boolean isActive, LocalDateTime ldt, String title, String text, String status);

    @Transactional
    @Modifying
    @Query(value = "UPDATE posts SET moderation_status = ?1, moderator_id = ?2 WHERE id = ?3", nativeQuery = true)
    void moderatePost(String status, int moderatorId, int postId);

    @Query(value = "SELECT COUNT(p.view_count) FROM posts AS p WHERE p.user_id = ?1", nativeQuery = true)
    int findViewCountByUserId(int userId);

    @Query(value = "SELECT MIN(p.time) FROM posts AS p WHERE p.user_id = ?1", nativeQuery = true)
    LocalDateTime findTheOldestPublicationTimeByUserId(int userId);

    @Query(value = "SELECT COUNT(pv) FROM posts AS p INNER JOIN post_votes AS pv WHERE pv.value = 1", nativeQuery = true)
    int findLikesCount();

    @Query(value = "SELECT COUNT(p.view_count) FROM posts AS p", nativeQuery = true)
    int findViewCount();

    @Query(value = "SELECT MAX(p.id) FROM posts AS p WHERE p.user_id = ?1", nativeQuery = true)
    int findLastPostIdByUserId(int userId);

    @Query(value = "SELECT * FROM posts WHERE id = ?1", nativeQuery = true)
    Optional<Post> findPostById(int postId);
}
