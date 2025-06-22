package at.fhtw.disys.percentageservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "energy.exchange";
    public static final String PERCENT_QUEUE = "percent.queue";

    @Bean
    public DirectExchange energyExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue percentQueue() {
        return QueueBuilder.durable(PERCENT_QUEUE).build();
    }

    @Bean
    public Binding percentBinding(Queue percentQueue, DirectExchange energyExchange) {
        return BindingBuilder.bind(percentQueue)
                .to(energyExchange)
                .with("percent");
    }
}