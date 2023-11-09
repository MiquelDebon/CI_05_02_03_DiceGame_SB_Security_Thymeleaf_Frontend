package cat.itacademy.barcelonactiva.cognoms.nom.s05.t02.n01.S05T02N01DebonVillagrasaMiquel.security;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity  //Allow to use @PreAuthorize
public class SecurityConfiguration {
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "api/mysql/auth/**",
                        // -- Swagger UI v3 (OpenAPI)
                        "/v3/api-docs/**", "/swagger-ui/**",
                        "/images/**", "/css/**", "/static/**", "/error/**", "/img/**", "/json/**",
                        "/page/login", "/page/register", "/page/home", "/page/actionRegister", "/paginable")
                .permitAll()
                .requestMatchers("/page/players").authenticated()
                .requestMatchers("/admin/home").hasRole("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin(form -> form
                        .loginPage("/page/login")
                        .defaultSuccessUrl("/page/home")
                        .loginProcessingUrl("/page/login")
                        .failureUrl("/page/login?error=true")
                        .permitAll()
                )
                .logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/page/logout"))
                                .logoutSuccessUrl("/page/login?logout=true")
                                .invalidateHttpSession(true)
                                .permitAll()
                )
                .authenticationProvider(authProvider);
        return http.build();
    }



    /**
     * Swagger configuration
     */


//    @Value("${miqueldebon.openapi.dev-url}")
//    private String devUrl;
//    @Value("${miqueldebon.openapi.prod-url}")
//    private String prodUrl;
//
//    @Bean
//    public OpenAPI myOpenAPI() {
//        Server devServer = new Server();
//        devServer.setUrl(devUrl);
//        devServer.setDescription("Server URL in Development environment");
//
//        Server prodServer = new Server();
//        prodServer.setUrl(prodUrl);
//        prodServer.setDescription("Server URL in Production environment");
//
//        Contact contact = new Contact();
//        contact.setEmail("debon.miquel@gmail.com");
//        contact.setName("Miquel");
//        contact.setUrl("https://www.linkedin.com/in/miquel-debon-villagrasa/");
//
//
//        Info info = new Info()
//                .title("Tutorial Management API")
//                .version("1.0")
//                .contact(contact)
//                .description("This API exposes endpoints to manage tutorials.");
//
//        return new OpenAPI().info(info)
//                .servers(List.of(devServer, prodServer));
//    }

    @Bean
    public ClassLoaderTemplateResolver secondaryTemplateResolver() {
        ClassLoaderTemplateResolver secondaryTemplateResolver = new ClassLoaderTemplateResolver();
        secondaryTemplateResolver.setPrefix("/templates");
        secondaryTemplateResolver.setSuffix(".html");
        secondaryTemplateResolver.setTemplateMode(TemplateMode.HTML);
        secondaryTemplateResolver.setCharacterEncoding("UTF-8");
        secondaryTemplateResolver.setOrder(1);
        secondaryTemplateResolver.setCheckExistence(true);

        return secondaryTemplateResolver;
    }


}
