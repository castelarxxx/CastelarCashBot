package com.financasbot.bot_financas_whatsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.financasbot.bot_financas_whatsapp.Repository")
@EnableScheduling  // ← ADICIONE ESTA ANOTAÇÃO
public class BotFinancasWhatsappApplication {
	public static void main(String[] args) {
		SpringApplication.run(BotFinancasWhatsappApplication.class, args);
	}
}