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
@Query(value = "UPDATE users SET name = ?2,email = ?3, photo = ?4, password = ?5 WHERE id = ?1", nativeQuery = true)
int fullUpdateMyProfile(int userId, String name, String email, String photo, String password);

@Transactional
@Modifying
@Query(value = "UPDATE users SET name = ?2,email = ?3, photo = ?4 WHERE id = ?1", nativeQuery = true)
int photoUpdateMyProfile(int userId, String name, String email, String photo);

@Transactional
@Modifying
@Query(value = "UPDATE users SET name = ?2,email = ?3 WHERE id = ?1", nativeQuery = true)
int nameEmailUpdateMyProfile(int userId, String name, String email);

@Transactional
@Modifying
@Query(value = "UPDATE users SET name = ?2,email = ?3, password = ?4 WHERE id = ?1", nativeQuery = true)
int passwordUpdateMyProfile(int userId, String name, String email, String password);

@Transactional
@Modifying
@Query(value = "UPDATE users SET name = ?2,email = ?3, photo = NULL WHERE id = ?1", nativeQuery = true)
int removePhotoUpdateMyProfile(int userId, String name, String email);

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

}
