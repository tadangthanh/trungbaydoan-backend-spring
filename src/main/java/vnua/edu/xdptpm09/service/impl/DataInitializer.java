//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.service.impl;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vnua.edu.xdptpm09.entity.Category;
import vnua.edu.xdptpm09.entity.MemberRole;
import vnua.edu.xdptpm09.entity.Role;
import vnua.edu.xdptpm09.entity.Technology;
import vnua.edu.xdptpm09.repository.CategoryRepo;
import vnua.edu.xdptpm09.repository.MemberRoleRepo;
import vnua.edu.xdptpm09.repository.RoleRepo;
import vnua.edu.xdptpm09.repository.TechnologyRepo;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepo roleRepo;
    private final MemberRoleRepo memberRoleRepo;
    private final CategoryRepo categoryRepo;
    private final TechnologyRepo technologyRepo;

    public void run(String... args) {
        List<Role> roles = this.initRole();
        roles.forEach((role) -> {
            if (!this.roleRepo.existsRoleByName(role.getName())) {
                this.roleRepo.save(role);
            }

        });
        List<MemberRole> memberRoles = this.initMemberRole();
        memberRoles.forEach((memberRole) -> {
            if (!this.memberRoleRepo.existsMemberRoleByName(memberRole.getName())) {
                this.memberRoleRepo.save(memberRole);
            }

        });
        List<Category> categories = this.initCategory();
        categories.forEach((category) -> {
            if (!this.categoryRepo.existsCategoryByName(category.getName())) {
                this.categoryRepo.save(category);
            }

        });
        List<Technology> technologies = this.initTechnology();
        technologies.forEach((technology) -> {
            if (!this.technologyRepo.existsTechnologyByName(technology.getName())) {
                this.technologyRepo.save(technology);
            }

        });
    }

    private List<Role> initRole() {
        Role r1 = new Role();
        r1.setName("ROLE_ADMIN");
        Role r2 = new Role();
        r2.setName("ROLE_TEACHER");
        Role r3 = new Role();
        r3.setName("ROLE_STUDENT");
        return List.of(r1, r2, r3);
    }

    private List<Category> initCategory() {
        Category c1 = new Category();
        c1.setName("Mobile");
        Category c2 = new Category();
        c2.setName("Web");
        Category c3 = new Category();
        c3.setName("Game");
        Category c4 = new Category();
        c4.setName("AI");
        Category c5 = new Category();
        c5.setName("Khác");
        return List.of(c1, c2, c3, c4, c5);
    }

    private List<MemberRole> initMemberRole() {
        MemberRole mr1 = new MemberRole();
        mr1.setName("ROLE_LEADER");
        MemberRole mr2 = new MemberRole();
        mr2.setName("ROLE_MEMBER");
        return List.of(mr1, mr2);
    }

    private List<Technology> initTechnology() {
        List<Technology> technologies = new ArrayList<>();
        technologies.add(new Technology("Java", "devicon-java-plain"));
        technologies.add(new Technology("Python", "devicon-python-plain"));
        technologies.add(new Technology("C#", "devicon-csharp-plain"));
        technologies.add(new Technology("C++", "devicon-cplusplus-plain"));
        technologies.add(new Technology("Ruby", "devicon-ruby-plain"));
        technologies.add(new Technology("PHP", "devicon-php-plain"));
        technologies.add(new Technology("JavaScript", "devicon-javascript-plain"));
        technologies.add(new Technology("HTML", "devicon-html5-plain"));
        technologies.add(new Technology("CSS", "devicon-css3-plain"));
        technologies.add(new Technology("SQL", "devicon-mysql-plain"));
        technologies.add(new Technology("TypeScript", "devicon-typescript-plain"));
        technologies.add(new Technology("Swift", "devicon-swift-plain"));
        technologies.add(new Technology("Kotlin", "devicon-kotlin-plain"));
        technologies.add(new Technology("Go", "devicon-go-plain"));
        technologies.add(new Technology("Rust", "devicon-rust-plain"));
        technologies.add(new Technology("NoSQL", "devicon-mongodb-plain"));
        technologies.add(new Technology("ReactJS", "devicon-react-original"));
        technologies.add(new Technology("Angular", "devicon-angularjs-plain"));
        technologies.add(new Technology("Vue.js", "devicon-vuejs-plain"));
        technologies.add(new Technology("Node.js", "devicon-nodejs-plain"));
        technologies.add(new Technology("Django", "devicon-django-plain"));
        technologies.add(new Technology("Flask", "devicon-flask-original"));
        technologies.add(new Technology("Spring Boot", "devicon-spring-plain"));
        technologies.add(new Technology(".NET Core", "devicon-dotnetcore-plain"));
        technologies.add(new Technology("Express.js", "devicon-express-original"));
        technologies.add(new Technology("Laravel", "devicon-laravel-plain"));
        technologies.add(new Technology("Symfony", "devicon-symfony-original"));
        technologies.add(new Technology("Ruby on Rails", "devicon-rails-plain"));
        technologies.add(new Technology("Flutter", "devicon-flutter-plain"));
        technologies.add(new Technology("React Native", "devicon-react-original"));
        technologies.add(new Technology("Ionic", "devicon-ionic-original"));
        technologies.add(new Technology("Docker", "devicon-docker-plain"));
        technologies.add(new Technology("Kubernetes", "devicon-kubernetes-plain"));
        technologies.add(new Technology("Jenkins", "devicon-jenkins-plain"));
        technologies.add(new Technology("Git", "devicon-git-plain"));
        technologies.add(new Technology("GitHub", "devicon-github-plain"));
        technologies.add(new Technology("GitLab", "devicon-gitlab-plain"));
        technologies.add(new Technology("Bitbucket", "devicon-bitbucket-plain"));
        technologies.add(new Technology("AWS", "devicon-amazonwebservices-plain"));
        technologies.add(new Technology("Azure", "devicon-azure-plain"));
        technologies.add(new Technology("Google Cloud", "devicon-googlecloud-plain"));
        technologies.add(new Technology("Firebase", "devicon-firebase-plain"));
        technologies.add(new Technology("Heroku", "devicon-heroku-plain"));
        technologies.add(new Technology("Redis", "devicon-redis-plain"));
        technologies.add(new Technology("MongoDB", "devicon-mongodb-plain"));
        technologies.add(new Technology("PostgreSQL", "devicon-postgresql-plain"));
        technologies.add(new Technology("MySQL", "devicon-mysql-plain"));
        technologies.add(new Technology("SQLite", "devicon-sqlite-plain"));
        technologies.add(new Technology("GraphQL", "devicon-graphql-plain"));
        technologies.add(new Technology("WebSocket", "devicon-websocket-plain"));
        return technologies;
    }
}
