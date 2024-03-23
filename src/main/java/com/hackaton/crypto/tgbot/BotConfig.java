package com.hackaton.crypto.tgbot;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix="bot")
public class BotConfig {
    String name;
    String token;
}
