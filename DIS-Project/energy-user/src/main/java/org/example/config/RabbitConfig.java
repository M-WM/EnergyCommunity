package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.*;
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
    public Binding usageBinding(Queue usageQueue, DirectExchange energyExchange) {
        return BindingBuilder.bind(usageQueue).to(energyExchange).with("usage");
    }

    @Bean
    public Binding percentBinding(Queue percentQueue, DirectExchange energyExchange) {
        return BindingBuilder.bind(percentQueue).to(energyExchange).with("percent");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory cf,
            Jackson2JsonMessageConverter converter) {
        RabbitTemplate rt = new RabbitTemplate(cf);
        rt.setMessageConverter(converter);
        return rt;
    }
}
