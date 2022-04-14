package main.model.repositories;
import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

Optional<User> findByEmail(String email);

Optional<User> findByName(String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET name = ?2 WHERE id = ?1", nativeQuery = true)
    int updateNameProfile(int userId ,String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET email = ?2 WHERE id = ?1", nativeQuery = true)
    int updateEmailProfile(int userId ,String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET password = ?2 WHERE id = ?1", nativeQuery = true)
    int updatePasswordProfile(int userId ,String password);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET photo = ?2 WHERE id = ?1", nativeQuery = true)
    int updatePhotoProfile(int userId ,String photo);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET photo = NULL WHERE id = ?1", nativeQuery = true)
    int removePhotoProfile(int userId);

@Transactional
@Modifying
@Query(value = "UPDATE users SET code = ?2 WHERE id = ?1", nativeQuery = true)
int addRestoreCode(int UserId, String code);

@Query(value = "SELECT * FROM users WHERE code = ?1", nativeQuery = true)
Optional<User> findByCode(String code);

@Transactional
@Modifying
@Query(value = "UPDATE users SET password = ?2 WHERE code = ?1", nativeQuery = true)
int findByCodeAndUpdatePassword(String code, String newPassword);

@Query(value = "SELECT u.photo FROM users AS u WHERE u.id = ?1", nativeQuery = true)
String findPhotoById(int userId);

}
