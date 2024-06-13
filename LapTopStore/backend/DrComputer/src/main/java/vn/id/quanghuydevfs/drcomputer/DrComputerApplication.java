package vn.id.quanghuydevfs.drcomputer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import vn.id.quanghuydevfs.drcomputer.dto.auth.RegisterDto;
import vn.id.quanghuydevfs.drcomputer.model.product.Category;
import vn.id.quanghuydevfs.drcomputer.model.user.Roles;
import vn.id.quanghuydevfs.drcomputer.repository.CategoryRepository;
import vn.id.quanghuydevfs.drcomputer.service.AuthService;

import java.util.ArrayList;
import java.util.List;

@EnableScheduling
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class DrComputerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrComputerApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner commandLineRunner(
//            CategoryRepository repository,
//             AuthService service
//    ) {
//        return args -> {
//            var admin = RegisterDto.builder()
//                    .fullname("Admin")
//                    .email("admin@gmail.com")
//                    .password("admin2002")
//                    .role(Roles.ADMIN)
//                    .build();
//            System.out.println("Admin token: " + service.register(admin).getAccessToken());
//
//            var manager = RegisterDto.builder()
//                    .fullname("Admin")
//                    .email("manager@gmail.com")
//                    .password("manager")
//                    .role(Roles.MANAGER)
//                    .build();
//            System.out.println("Manager token: " + service.register(manager).getAccessToken());
//            List<Category> categories = new ArrayList<>();
//            var laptop = Category.builder().name("Laptop").value("latop").build();
//            categories.add(laptop);
//            var mouse = Category.builder().name("Chuột").value("mouse").build();
//            categories.add(mouse);
//            var keyboard = Category.builder().name("Bàn phím").value("keyboard").build();
//            categories.add(keyboard);
//            var ram = Category.builder().name("Ram").value("ram").build();
//            categories.add(ram);
//            repository.saveAll(categories);
//
//        };
//    }
}
