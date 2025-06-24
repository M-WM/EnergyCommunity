package at.fhtw.disys.usageservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Name des Exchanges, auf den Producer/User publishen
    public static final String EXCHANGE = "energy.exchange";

    // Routing Key für Usage-Update-Events
    public static final String UPDATE_ROUTING_KEY = "update";

    // Name der Queue, die der Usage-Service konsumiert
    public static final String USAGE_QUEUE = "usage.queue";

    // Name der Queue, die der Percentage-Service konsumiert
    public static final String PERCENTAGE_QUEUE = "percentage.queue";

    @Bean
    public DirectExchange energyExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue usageQueue() {
        return QueueBuilder.durable(USAGE_QUEUE).build();
    }

    @Bean
    public Queue percentageQueue() {
        return QueueBuilder.durable(PERCENTAGE_QUEUE).build();
    }

    @Bean
    public Binding bindProducerUser(Queue usageQueue, DirectExchange exchange) {
        // Binde sowohl PRODUCER als auch USER Nachrichten an dieselbe Queue
        return BindingBuilder
                .bind(usageQueue)
                .to(exchange)
                .with("producer"); // Producer sendet mit Routing-Key "producer"
    }

    @Bean
    public Binding bindUser(Queue usageQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(usageQueue)
                .to(exchange)
                .with("usage"); // User sendet mit Routing-Key "usage"
    }

    @Bean
    public Binding bindPercentage(Queue percentageQueue, DirectExchange exchange) {
        // Binde Update-Events an die Percentage-Service-Queue
        return BindingBuilder
                .bind(percentageQueue)
                .to(exchange)
                .with(UPDATE_ROUTING_KEY);
    }
}