package com.toren.shewstringbe.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public static class ExcludeNull {

        @Override
        public boolean equals(Object object) {
            return object == null;
        }
    }

    @Configuration
    public static class ModelMapperConfig {

        @Bean
        public ModelMapper modelMapper() {
            return new ModelMapper();
        }

    }
}
