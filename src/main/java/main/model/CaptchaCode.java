package main.model;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "captcha_codes")
@Data
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime time;

    private String code;

    @Column(name = "secret_code")
    private String secretCode;
}
