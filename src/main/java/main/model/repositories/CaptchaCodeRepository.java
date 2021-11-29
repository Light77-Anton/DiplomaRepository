package main.model.repositories;
import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CaptchaCodeRepository
        extends JpaRepository<CaptchaCode, Integer> {

long deleteByTimeIsAfter(LocalDate localDate);

Optional<CaptchaCode> findBySecretCodeEquals(String secret); //  в теории может произойти исключение,если будут образованы 2 одинаковых секретных кода

}
