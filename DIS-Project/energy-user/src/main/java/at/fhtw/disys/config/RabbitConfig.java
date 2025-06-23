package at.fhtw.disys.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "energy.exchange";
    public static final String ROUTING_KEY   = "energy.usage";
    public static final String QUEUE_NAME    = "usage.queue";

    @Bean
    public DirectExchange energyExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue usageQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public Binding binding(Queue usageQueue, DirectExchange energyExchange) {
        return BindingBuilder
                .bind(usageQueue)
                .to(energyExchange)
                .with(ROUTING_KEY);
    }
}