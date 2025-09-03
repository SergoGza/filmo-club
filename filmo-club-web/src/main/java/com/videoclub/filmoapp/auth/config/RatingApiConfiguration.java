package com.videoclub.filmoapp.auth.config;

import io.netty.channel.ChannelOption;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties(RatingApiConfigurationProperties.class)
public class RatingApiConfiguration {

  @Bean
  public WebClient ratingApiClient(
      RatingApiConfigurationProperties ratingApiConfigurationProperties) {

    HttpClient httpClient =
        HttpClient.create()
            .option(
                ChannelOption.CONNECT_TIMEOUT_MILLIS,
                (int) ratingApiConfigurationProperties.timeout().toMillis())
            .responseTimeout(ratingApiConfigurationProperties.timeout());

    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(ratingApiConfigurationProperties.urlbase())
        .defaultHeader("Content-Type", "application/json")
        .build();
  }
}
