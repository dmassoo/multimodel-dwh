package com.dmasso.multidwh;

import com.dmasso.multidwh.processing.CypherQueryProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
@RequiredArgsConstructor
public class Application implements CommandLineRunner {
    public static final String EXIT = "exit";

    private final CypherQueryProcessor processor;


    @Override
    public void run(String... args) {
        Scanner scanner;
        while (true) {
            scanner = new Scanner(System.in);
            System.out.println("Enter query or type \"exit\":");
            String query = scanner.nextLine();
            if (EXIT.equals(query)) {
                System.exit(0);
            }
            processor.execute(query);
        }
    }
}
