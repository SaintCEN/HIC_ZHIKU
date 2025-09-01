package com.example.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.example.backend.mapper")
@EnableTransactionManagement
public class BackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

    @Bean
    public CommandLineRunner dbMigrate(JdbcTemplate jdbc) {
        return args -> {
            try { jdbc.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS college VARCHAR(100)"); } catch (Exception ignored) {}
            try { jdbc.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS bio TEXT"); } catch (Exception ignored) {}
            try { jdbc.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS hic TINYINT DEFAULT 0 COMMENT 'HIC认证状态：0-未认证，1-已认证'"); } catch (Exception ignored) {}
        };
    }
}
