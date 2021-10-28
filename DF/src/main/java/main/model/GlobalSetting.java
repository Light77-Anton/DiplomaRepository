package main.model;
import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "global_settings")
@Data
public class GlobalSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String code;

    private String name;

    private String value;
}
