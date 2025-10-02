package com.ase.notificationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

@Configuration
public class ThymeleafEmailConfig {
  @Bean
  SpringResourceTemplateResolver emailTemplateResolver() {
    SpringResourceTemplateResolver springResourceTemplateResolver = new SpringResourceTemplateResolver();
    springResourceTemplateResolver.setPrefix("classpath:/templates/email/");
    springResourceTemplateResolver.setSuffix(".html");
    springResourceTemplateResolver.setTemplateMode("HTML");
    springResourceTemplateResolver.setCharacterEncoding("UTF-8");
    springResourceTemplateResolver.setCacheable(true);
    return springResourceTemplateResolver;
  }
  @Bean
  SpringTemplateEngine emailTemplateEngine(SpringResourceTemplateResolver emailTemplateResolver) {
    SpringTemplateEngine engine = new SpringTemplateEngine();
    engine.setTemplateResolver(emailTemplateResolver);
    return engine;
  }
}
