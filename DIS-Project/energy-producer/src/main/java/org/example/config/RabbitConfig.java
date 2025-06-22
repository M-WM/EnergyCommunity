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

    @Bean DirectExchange energyExchange() {
        return new DirectExchange(EXCHANGE);
    }
    @Bean Queue usageQueue() { return QueueBuilder.durable(USAGE_QUEUE).build(); }
    @Bean Queue percentQueue() { return QueueBuilder.durable(PERCENT_QUEUE).build(); }
    @Bean Binding usageBinding(Queue usageQueue, DirectExchange ex) {
        return BindingBuilder.bind(usageQueue).to(ex).with("usage");
    }
    @Bean Binding percentBinding(Queue percentQueue, DirectExchange ex) {
        return BindingBuilder.bind(percentQueue).to(ex).with("percent");
    }

    // JSON‐Converter mit JSR-310‐Support
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory cf,
            Jackson2JsonMessageConverter jsonMessageConverter) {
        RabbitTemplate rt = new RabbitTemplate(cf);
        rt.setMessageConverter(jsonMessageConverter);
        return rt;
    }
}
