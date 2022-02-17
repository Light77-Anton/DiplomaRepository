package main.model;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "captcha_codes")
@Getter
@Setter
@NoArgsConstructor
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private LocalDateTime time;

    private String code;

    @Column(name = "secret_code")
    private String secretCode;

}
