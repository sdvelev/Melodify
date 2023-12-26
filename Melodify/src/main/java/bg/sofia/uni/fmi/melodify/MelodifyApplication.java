package bg.sofia.uni.fmi.melodify;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MelodifyApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MelodifyApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Application started ...");
    }
}
