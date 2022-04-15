package main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
    }
}
/*
<url>jdbc:postgresql://localhost:5432/postgres?currentSchema=dfschema&amp;ssl=false</url>
                    <user>postgres</user>
                    <password>postgretest</password>
                    <schemas>
                        <schema>dfschema</schema>
                    </schemas>

                    <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>2.4.12</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
 */