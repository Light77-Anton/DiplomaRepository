package main.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "global_settings")
@Getter
@Setter
@NoArgsConstructor
public class GlobalSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String code;

    private String name;

    private String value;
}
