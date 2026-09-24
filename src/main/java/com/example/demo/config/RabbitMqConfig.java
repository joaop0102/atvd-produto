package com.example.demo.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);

        return rabbitTemplate;
    }

    @Bean
    public ApplicationRunner testRabbit(ConnectionFactory connectionFactory) {
        return args -> {
            System.out.println("========== TESTANDO RABBITMQ ==========");

            try {
                var connection = connectionFactory.createConnection();

                System.out.println("RABBITMQ CONECTOU!");

                connection.close();
            } catch (Exception e) {
                System.out.println("ERRO AO CONECTAR NO RABBITMQ:");
                e.printStackTrace();
            }
        };
    }
}