package at.fhtw.disys.usageservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "energy.exchange";
    public static final String USAGE_QUEUE = "usage.queue";

    @Bean
    public DirectExchange energyExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue usageQueue() {
        return QueueBuilder.durable(USAGE_QUEUE).build();
    }

    @Bean
    public Binding usageBinding(Queue usageQueue, DirectExchange energyExchange) {
        return BindingBuilder.bind(usageQueue)
                .to(energyExchange)
                .with("usage");
    }
}