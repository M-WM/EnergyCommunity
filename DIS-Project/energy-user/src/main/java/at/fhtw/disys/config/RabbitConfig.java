package at.fhtw.disys.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("energy.exchange");
    }

    @Bean
    public Queue queue() {
        return new Queue("energy.queue");
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("energy.routing");
    }
}