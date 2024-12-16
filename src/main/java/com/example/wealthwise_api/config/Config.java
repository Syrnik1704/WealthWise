package com.example.wealthwise_api.config;


import com.example.wealthwise_api.Entity.Categories;
import com.example.wealthwise_api.Entity.Role;
import com.example.wealthwise_api.Entity.UserEntity;
import com.example.wealthwise_api.Repository.CategoriesRepository;
import com.example.wealthwise_api.Repository.UserEntityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class Config {
    @Bean
    CommandLineRunner commandLineRunner(CategoriesRepository repository, UserEntityRepository userEntityRepository){
        return args -> {


            Categories  categories  = new Categories("Żywność");
            Categories  categories1 = new Categories("Chemia gospodarcza");
            Categories  categories2 = new Categories("Inne wydatki");
            Categories  categories3 = new Categories("Rachunki");
            Categories  categories5 = new Categories("Ubrania");
            Categories  categories6 = new Categories("Relaks");
            Categories  categories4 = new Categories("Transport");
            Categories  categories7 = new Categories("Mieszkanie");
            Categories categories8 = new Categories("Zdrowie");

            repository.saveAll(
                    List.of(categories, categories1, categories2, categories3,
                            categories5, categories6, categories4, categories7,categories8));


            PasswordEncoder passwordEncoder = passwordEncoderConfig();

            UserEntity userBase = new UserEntity(
                    "user@example.com",
                    passwordEncoder.encode("password123"),
                    "John",
                    "Doe",
                    "29-05-2000",
                    true,
                    Role.USER);

            UserEntity userAdmin = new UserEntity(
                    "admin@example.com",
                    passwordEncoder.encode("admin123"),
                    "Admin",
                    "Smith",
                    "29-05-2000",
                    true,
                    Role.ADMIN);

            userEntityRepository.saveAll(List.of(userBase, userAdmin));
        };

    }

    @Bean
    PasswordEncoder passwordEncoderConfig() {
        return new BCryptPasswordEncoder();
    }
}
