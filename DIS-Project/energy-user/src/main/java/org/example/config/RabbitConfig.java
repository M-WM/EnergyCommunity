package org.example.energyuser.config;  // <-- wichtig: im gleichen Base-Package wie Deine Application

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE      = "energy.exchange";
    public static final String USAGE_QUEUE   = "usage.queue";
    public static final String PERCENT_QUEUE = "percent.queue";

    @Bean
    public DirectExchange energyExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue usageQueue() {
        return QueueBuilder.durable(USAGE_QUEUE).build();
    }

    @Bean
    public Queue percentQueue() {
        return QueueBuilder.durable(PERCENT_QUEUE).build();
    }

    @Bean
    public Binding usageBinding(DirectExchange energyExchange, Queue usageQueue) {
        return BindingBuilder.bind(usageQueue).to(energyExchange).with("usage");
    }

    @Bean
    public Binding percentBinding(DirectExchange energyExchange, Queue percentQueue) {
        return BindingBuilder.bind(percentQueue).to(energyExchange).with("percent");
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonConverter() {
        // Erstelle einen ObjectMapper mit JSR-310 (LocalDateTime) Unterstützung
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                // Datum/Zeit als ISO-String, nicht als Timestamp:
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf,
                                         Jackson2JsonMessageConverter jacksonConverter) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(jacksonConverter);
        return template;
    }
}
