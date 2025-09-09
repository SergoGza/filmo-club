package com.videoclub.filmoapp.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FilmResourceConfigurationProperties.class)
public class FilmResourceConfiguration {}
