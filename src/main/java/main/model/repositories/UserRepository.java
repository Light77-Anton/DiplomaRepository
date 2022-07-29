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

    @Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE name = ?1", nativeQuery = true)
    Optional<User> findByName(String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET name = ?2 WHERE id = ?1", nativeQuery = true)
    void updateNameProfile(int userId ,String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET email = ?2 WHERE id = ?1", nativeQuery = true)
    void updateEmailProfile(int userId ,String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET password = ?2 WHERE id = ?1", nativeQuery = true)
    void updatePasswordProfile(int userId ,String password);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET photo = ?2 WHERE id = ?1", nativeQuery = true)
    void updatePhotoProfile(int userId ,String photo);

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET photo = NULL WHERE id = ?1", nativeQuery = true)
    void removePhotoProfile(int userId);

@Transactional
@Modifying
@Query(value = "UPDATE users SET code = ?2 WHERE id = ?1", nativeQuery = true)
void addRestoreCode(int userId, String code);

@Query(value = "SELECT * FROM users WHERE code = ?1", nativeQuery = true)
Optional<User> findByCode(String code);

@Transactional
@Modifying
@Query(value = "UPDATE users SET password = ?2 WHERE code = ?1", nativeQuery = true)
void findByCodeAndUpdatePassword(String code, String newPassword);

@Query(value = "SELECT u.photo FROM users AS u WHERE u.id = ?1", nativeQuery = true)
String findPhotoById(int userId);

}
