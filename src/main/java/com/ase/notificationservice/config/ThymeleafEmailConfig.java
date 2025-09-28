package com.ase.notificationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

@Configuration
public class ThymeleafEmailConfig {
  @Bean
  SpringResourceTemplateResolver emailTemplateResolver() {
    var r = new SpringResourceTemplateResolver();
    r.setPrefix("classpath:/templates/email/");
    r.setSuffix(".html");
    r.setTemplateMode("HTML");
    r.setCharacterEncoding("UTF-8");
    r.setCacheable(true);
    return r;
  }
  @Bean
  SpringTemplateEngine emailTemplateEngine(SpringResourceTemplateResolver emailTemplateResolver) {
    var engine = new SpringTemplateEngine();
    engine.setTemplateResolver(emailTemplateResolver);
    return engine;
  }
}
