package main.model;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "captcha_codes")
@Data
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    private String code;

    @Column(name = "secret_code")
    private String secretCode;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;
}
