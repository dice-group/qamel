package org.aksw.qamel.RESTendpoints;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan({"org.aksw.qamel.OfflineQA","org.aksw.qamel.TeBaQA","org.aksw.qamel.KnowledgeCard"})
@SpringBootApplication
public class Application {
	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
