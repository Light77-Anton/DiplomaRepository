package main.model;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "captcha_codes")
@Data
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDate time;

    private String code;

    @Column(name = "secret_code")
    private String secretCode;
}
