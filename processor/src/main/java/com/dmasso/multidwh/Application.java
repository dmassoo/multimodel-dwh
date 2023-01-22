package com.dmasso.multidwh;

import com.dmasso.multidwh.translation.concrete.fromtext.CypherToSqlTranslator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
        // TODO: 21.01.2023 it works here, remove later
        CypherToSqlTranslator bean = run.getBean(CypherToSqlTranslator.class);
        String translate = bean.translate("""
                MATCH (a:Person) WHERE a.firstName IN ['foo', 'bar']
                RETURN a
                """
        );
        System.out.println(translate);
    }

}
