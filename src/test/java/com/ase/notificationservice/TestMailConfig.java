package com.ase.notificationservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

/**
 * Test configuration active for the 'test' profile.
 * This provides a mocked JavaMailSender so tests that load the full
 * application context don't fail due to missing mail configuration.
 */
@Configuration
@Profile("test")
public class TestMailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        // Provide a Mockito mock so tests that load the full context don't fail
        JavaMailSender mockSender = mock(JavaMailSender.class);
        try {
            // Return a simple MimeMessage when createMimeMessage is called to avoid NPE
            MimeMessage mime = new MimeMessage((Session) null);
            when(mockSender.createMimeMessage()).thenReturn(mime);
        } catch (Exception ignored) {
            // If something goes wrong, tests can still stub behaviour per-test
        }
        return mockSender;
    }
}
