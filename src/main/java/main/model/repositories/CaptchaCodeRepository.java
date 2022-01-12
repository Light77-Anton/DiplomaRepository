package main.model.repositories;
import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Integer> {

@Query(value = "DELETE FROM captcha_codes AS c WHERE c.time > ?1", nativeQuery = true)
void deleteByTimeIsAfter(LocalDateTime localDateTime);

Optional<CaptchaCode> findBySecretCodeEquals(String secret); //  в теории может произойти исключение,если будут образованы 2 одинаковых секретных кода

}
