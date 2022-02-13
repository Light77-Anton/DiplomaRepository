package main.model.repositories;
import main.dto.MyPostDTO;
import main.dto.PostDTO;
import main.dto.UserDataDTO;
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

@Query(value = "SELECT COUNT(pv) FROM posts AS p INNER JOIN post_votes AS pv ON pv.post_id = p.id WHERE pv.value = 1 AND p.id = ?1", nativeQuery = true)
int findLikeCountById(int postId);

@Query(value = "SELECT COUNT(pv) FROM posts AS p INNER JOIN post_votes AS pv ON pv.post_id = p.id WHERE pv.value = 0 AND p.id = ?1", nativeQuery = true)
int findDislikeCountById(int postId);

@Query(value = "SELECT COUNT(p.view_count) FROM posts AS p WHERE p.id = ?1", nativeQuery = true)
int findViewCountById(int postId);

@Query(value = "SELECT SUBSTRING(p.text, 0, 150) FROM posts AS p WHERE p.id = ?1", nativeQuery = true)
String extractAnnounceFromTextById(int postId);

    @Query(value = "SELECT new main.dto.PostDTO(p.id, p.time, new main.dto.UserDataDTO(u.id, u.name), p.title, SUBSTRING(p.text, 0, 150), COUNT(pv.value = 1), COUNT(pv.value = 0), COUNT(pc), COUNT(p.view_count)) FROM posts AS p "
            + "INNER JOIN users AS u ON u.id = p.user_id "
            + "INNER JOIN post_votes AS pv ON pv.post_id = p.id "
            + "INNER JOIN post_comments AS pc ON pc.post_id = p.id "
            + "WHERE p.text LIKE '%' || ?1 || '%' AND p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now()", nativeQuery = true)
    Page<PostDTO> findByTextContainingTest(String query, Pageable pageable);

    @Query(value = "SELECT new main.dto.PostDTO(p.id, p.time, new main.dto.UserDataDTO(u.id, u.name), p.title, SUBSTRING(p.text, 0, 150), COUNT(pv.value = 1), COUNT(pv.value = 0), COUNT(pc), COUNT(p.view_count)) FROM posts AS p "
            + "INNER JOIN users AS u ON u.id = p.user_id "
            + "INNER JOIN post_votes AS pv ON pv.post_id = p.id "
            + "INNER JOIN post_comments AS pc ON pc.post_id = p.id "
            + "WHERE DATE(p.time) = TO_DATE(?1, 'YYYY-MM-DD') AND p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now()", nativeQuery = true)
    Page<PostDTO> findByDateTest(String date, Pageable pageable);

    @Query(value = "SELECT new main.dto.PostDTO(p.id, p.time, new main.dto.UserDataDTO(u.id, u.name), p.title, SUBSTRING(p.text, 0, 150), COUNT(pv.value = 1), COUNT(pv.value = 0), COUNT(pc), COUNT(p.view_count)) FROM posts AS p "
            + "INNER JOIN users AS u ON u.id = p.user_id "
            + "INNER JOIN post_votes AS pv ON pv.post_id = p.id "
            + "INNER JOIN post_comments AS pc ON pc.post_id = p.id "
            + "WHERE ttp.tag_id = ?1 AND p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now()", nativeQuery = true)
    Page<PostDTO> findByTagContainingTest(int tagId, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE EXTRACT(YEAR FROM DATE(p.time)) = EXTRACT(YEAR FROM TO_DATE(?1, 'YYYY')) WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now()", nativeQuery = true)
List<Post> findByYear(String year);

@Query(value = "SELECT EXTRACT(YEAR FROM DATE(p.time)) FROM posts AS p WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now()", nativeQuery = true)
TreeSet<Integer> findAllYears();

    @Query(value = "SELECT new main.dto.PostDTO(p.id, p.time, new main.dto.UserDataDTO(u.id, u.name), p.title, SUBSTRING(p.text, 0, 150), COUNT(pv.value = 1), COUNT(pv.value = 0), COUNT(pc), COUNT(p.view_count)) FROM posts AS p "
            + "INNER JOIN users AS u ON u.id = p.user_id "
            + "INNER JOIN post_votes AS pv ON pv.post_id = p.id "
            + "INNER JOIN post_comments AS pc ON pc.post_id = p.id "
            + "WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now() ORDER BY COUNT(pc.id) DESC", nativeQuery = true)
    Page<PostDTO> findAllAndOrderByCommentariesSizeTest(Pageable pageable);

    @Query(value = "SELECT new main.dto.PostDTO(p.id, p.time, new main.dto.UserDataDTO(u.id, u.name), p.title, SUBSTRING(p.text, 0, 150), COUNT(pv.value = 1), COUNT(pv.value = 0), COUNT(pc), COUNT(p.view_count)) FROM posts AS p "
            + "INNER JOIN users AS u ON u.id = p.user_id "
            + "INNER JOIN post_votes AS pv ON pv.post_id = p.id "
            + "INNER JOIN post_comments AS pc ON pc.post_id = p.id "
            + "WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now() ORDER BY COUNT(pv.id) DESC", nativeQuery = true)
    Page<PostDTO> findAllAndOrderByVotesCountTest(Pageable pageable);

    @Query(value = "SELECT new main.dto.PostDTO(p.id, p.time, new main.dto.UserDataDTO(u.id, u.name), p.title, SUBSTRING(p.text, 0, 150), COUNT(pv.value = 1), COUNT(pv.value = 0), COUNT(pc), COUNT(p.view_count)) FROM posts AS p "
            + "INNER JOIN users AS u ON u.id = p.user_id "
            + "INNER JOIN post_votes AS pv ON pv.post_id = p.id "
            + "INNER JOIN post_comments AS pc ON pc.post_id = p.id "
            + "WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now() ORDER BY p.time DESC", nativeQuery = true)
    Page<PostDTO> findAllAndOrderByTimeDescTest(Pageable pageable);

    @Query(value = "SELECT new main.dto.PostDTO(p.id, p.time, new main.dto.UserDataDTO(u.id, u.name), p.title, SUBSTRING(p.text, 0, 150), COUNT(pv.value = 1), COUNT(pv.value = 0), COUNT(pc), COUNT(p.view_count)) FROM posts AS p "
            + "INNER JOIN users AS u ON u.id = p.user_id "
            + "INNER JOIN post_votes AS pv ON pv.post_id = p.id "
            + "INNER JOIN post_comments AS pc ON pc.post_id = p.id "
            + "WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.time > now() ORDER BY p.time ASC", nativeQuery = true)
    Page<PostDTO> findAllAndOrderByTimeAscTest(Pageable pageable);

    @Query(value = "SELECT new main.dto.MyPostDTO(p.id, p.time, p.title, SUBSTRING(p.text, 0, 150), COUNT(pv.value = 1), COUNT(pv.value = 0), COUNT(pc), COUNT(p.view_count), new main.dto.UserDataDTO(u.id, u.name)) FROM posts AS p "
            + "INNER JOIN users AS u ON u.id = p.user_id "
            + "INNER JOIN post_votes AS pv ON pv.post_id = p.id "
            + "INNER JOIN post_comments AS pc ON pc.post_id = p.id "
            + "WHERE p.is_active = false AND p.user_id = ?1", nativeQuery = true)
    Page<MyPostDTO> findAllInactivePostsTest(int userId, Pageable pageable);

    @Query(value = "SELECT new main.dto.MyPostDTO(p.id, p.time, p.title, SUBSTRING(p.text, 0, 150), COUNT(pv.value = 1), COUNT(pv.value = 0), COUNT(pc), COUNT(p.view_count), new main.dto.UserDataDTO(u.id, u.name)) FROM posts AS p "
            + "INNER JOIN users AS u ON u.id = p.user_id "
            + "INNER JOIN post_votes AS pv ON pv.post_id = p.id "
            + "INNER JOIN post_comments AS pc ON pc.post_id = p.id "
            + "WHERE p.is_active = true AND p.user_id = ?1", nativeQuery = true)
    Page<MyPostDTO> findAllPendingPostsTest(int userId, Pageable pageable);

    @Query(value = "SELECT new main.dto.MyPostDTO(p.id, p.time, p.title, SUBSTRING(p.text, 0, 150), COUNT(pv.value = 1), COUNT(pv.value = 0), COUNT(pc), COUNT(p.view_count), new main.dto.UserDataDTO(u.id, u.name)) FROM posts AS p "
            + "INNER JOIN users AS u ON u.id = p.user_id "
            + "INNER JOIN post_votes AS pv ON pv.post_id = p.id "
            + "INNER JOIN post_comments AS pc ON pc.post_id = p.id "
            + "WHERE p.is_active = true AND p.moderation_status = 'DECLINED' p.user_id = ?1", nativeQuery = true)
    Page<MyPostDTO> findAllDeclinedPostsTest(int userId, Pageable pageable);

    @Query(value = "SELECT new main.dto.MyPostDTO(p.id, p.time, p.title, SUBSTRING(p.text, 0, 150), COUNT(pv.value = 1), COUNT(pv.value = 0), COUNT(pc), COUNT(p.view_count), new main.dto.UserDataDTO(u.id, u.name)) FROM posts AS p "
            + "INNER JOIN users AS u ON u.id = p.user_id "
            + "INNER JOIN post_votes AS pv ON pv.post_id = p.id "
            + "INNER JOIN post_comments AS pc ON pc.post_id = p.id "
            + "WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' p.user_id = ?1", nativeQuery = true)
    Page<MyPostDTO> findAllAcceptedPostsTest(int userId, Pageable pageable);

@Query(value = "SELECT * FROM posts AS p WHERE p.moderation_status = 'NEW'", nativeQuery = true)
List<Post> findAllNewPostsAsList();

    @Query(value = "SELECT new main.dto.MyPostDTO(p.id, p.time, p.title, SUBSTRING(p.text, 0, 150), COUNT(pv.value = 1), COUNT(pv.value = 0), COUNT(pc), COUNT(p.view_count), new main.dto.UserDataDTO(u.id, u.name)) FROM posts AS p "
            + "INNER JOIN users AS u ON u.id = p.user_id "
            + "INNER JOIN post_votes AS pv ON pv.post_id = p.id "
            + "INNER JOIN post_comments AS pc ON pc.post_id = p.id "
            + "WHERE p.is_active = true AND p.moderation_status = 'NEW'", nativeQuery = true)
    Page<MyPostDTO> findAllNewPostsAsPageTest(Pageable pageable);


    @Query(value = "SELECT new main.dto.MyPostDTO(p.id, p.time, p.title, SUBSTRING(p.text, 0, 150), COUNT(pv.value = 1), COUNT(pv.value = 0), COUNT(pc), COUNT(p.view_count), new main.dto.UserDataDTO(u.id, u.name)) FROM posts AS p "
            + "INNER JOIN users AS u ON u.id = p.user_id "
            + "INNER JOIN post_votes AS pv ON pv.post_id = p.id "
            + "INNER JOIN post_comments AS pc ON pc.post_id = p.id "
            + "WHERE p.is_active = true AND p.moderation_status = 'ACCEPTED' AND p.moderator_id = ?1", nativeQuery = true)
    Page<MyPostDTO> findAllAcceptedPostsByMeTest(int myId, Pageable pageable);

    @Query(value = "SELECT new main.dto.MyPostDTO(p.id, p.time, p.title, SUBSTRING(p.text, 0, 150), COUNT(pv.value = 1), COUNT(pv.value = 0), COUNT(pc), COUNT(p.view_count), new main.dto.UserDataDTO(u.id, u.name)) FROM posts AS p "
            + "INNER JOIN users AS u ON u.id = p.user_id "
            + "INNER JOIN post_votes AS pv ON pv.post_id = p.id "
            + "INNER JOIN post_comments AS pc ON pc.post_id = p.id "
            + "WHERE p.is_active = true AND p.moderation_status = 'DECLINED' AND p.moderator_id = ?1", nativeQuery = true)
    Page<MyPostDTO> findAllDeclinedPostsByMeTest(int myId, Pageable pageable);

@Transactional
@Modifying
@Query(value = "UPDATE posts SET view_count = ?1 WHERE id = ?2", nativeQuery = true)
int setNewViewCount(int newViewCount, int postId);


@Transactional
@Modifying
@Query(value = "INSERT INTO posts(is_active, user_id, \"time\", title, text, moderation_status) VALUES (?1, ?2, ?3, ?4, ?5, ?6)", nativeQuery = true)
void insertPost(boolean isActive, int userId, LocalDateTime ldt, String title, String text, String status);


@Query(value = "SELECT * FROM posts AS p WHERE p.id = ?1 AND p.user_id = ?2", nativeQuery = true)
Optional<Post> findByIdAndUserId(int id, int userId);

@Transactional
@Modifying
@Query(value = "UPDATE posts SET is_active = ?2, \"time\" = ?3, title = ?4, text = ?5, moderation_status = ?6 WHERE id = ?1", nativeQuery = true)
int updatePost(int id, boolean isActive, LocalDateTime ldt, String title, String text, String status);

@Transactional
@Modifying
@Query(value = "UPDATE posts SET moderation_status = ?1, moderator_id = ?2 WHERE id = ?3", nativeQuery = true)
int moderatePost(String status, int moderatorId, int postId);

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
