package at.fhtw.disys.percentageservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Name des zentralen Exchanges (muss mit Usage-Service übereinstimmen)
    public static final String EXCHANGE = "energy.exchange";

    // Queue, auf die der Percentage-Service horcht
    public static final String PERCENTAGE_QUEUE = "percentage.queue";

    // Routing Key für Update-Events
    public static final String UPDATE_ROUTING_KEY = "update";

    @Bean
    public DirectExchange energyExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue percentageQueue() {
        return QueueBuilder.durable(PERCENTAGE_QUEUE).build();
    }

    @Bean
    public Binding bindPercentage() {
        return BindingBuilder
                .bind(percentageQueue())
                .to(energyExchange())
                .with(UPDATE_ROUTING_KEY);
    }
}