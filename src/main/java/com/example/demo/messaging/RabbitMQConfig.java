package com.example.demo.messaging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQConfig {

    @Bean
    JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    TopicExchange produtoExchange(
            @Value("${app.messaging.exchange:produto.exchange}") String exchange) {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    Queue produtoQueue(
            @Value("${app.messaging.queue:produto.eventos}") String queue) {
        return new Queue(queue, true);
    }

    @Bean
    Binding produtoBinding(
            Queue produtoQueue,
            TopicExchange produtoExchange,
            @Value("${app.messaging.routing-key:produto.evento}") String routingKey) {
        return BindingBuilder.bind(produtoQueue)
                .to(produtoExchange)
                .with(routingKey);
    }
}
