package com.abi.quotes;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.spring.annotation.EnableVaadin;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "zitate-sammlung")
@PWA(name = "Zitate-Sammlung für das Abi 2026", shortName = "Abi-Zitate")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Override
    public void configurePage(AppShellSettings settings) {
      settings.addFavIcon("icon", "icons/favicon.ico", "32x32");
      settings.addLink("shortcut icon", "icons/favicon.ico");
    }

}
