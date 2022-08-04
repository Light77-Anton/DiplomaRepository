package main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
public class Main extends WebSecurityConfigurerAdapter {
    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
    }

}