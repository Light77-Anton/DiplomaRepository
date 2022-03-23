package main.model.repositories;
import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Integer> {

@Transactional
@Modifying
@Query(value = "DELETE FROM captcha_codes AS c WHERE c.time < ?1", nativeQuery = true)
void deleteByTimeIsAfter(LocalDateTime localDateTime);

@Query(value = "SELECT * FROM captcha_codes as c WHERE c.secret_code = ?1", nativeQuery = true)
Optional<CaptchaCode> findBySecretCodeEquals(String secret);

}
