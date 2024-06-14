package com.musicUpload.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");

        Contact myContact = new Contact();
        myContact.setName("Kovács Botond János");
        myContact.setEmail("kovacsbotondjanos03@gmail.com");

        Info information = new Info()
                .title("SongUpload API")
                .version("0.1")
                .description("This API exposes endpoints to manage song, albums and clients.")
                .contact(myContact);
        return new OpenAPI().info(information).servers(List.of(server));
    }
}