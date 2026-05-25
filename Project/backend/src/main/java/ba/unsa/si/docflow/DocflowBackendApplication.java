package ba.unsa.si.docflow;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class DocflowBackendApplication {

    @PostConstruct
    public void configureDefaultTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Sarajevo"));
    }

    public static void main(String[] args) {
        SpringApplication.run(DocflowBackendApplication.class, args);
    }
}
