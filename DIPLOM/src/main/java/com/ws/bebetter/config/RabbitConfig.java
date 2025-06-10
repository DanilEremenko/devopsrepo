package com.ws.bebetter.config;

import com.ws.bebetter.service.impl.CustomJsonGenericMessageConverter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.ws.bebetter.config.RabbitQueue.EMAIL_NOTIFICATION;
import static com.ws.bebetter.config.RabbitQueue.INTERNAL_NOTIFICATION;

@Configuration
public class RabbitConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        return converter;
    }

    @Bean
    public MessageConverter customJsonGenericMessageConverter() {
        return new CustomJsonGenericMessageConverter();
    }

    @Bean
    public Queue internalNotificationQueue() {
        return new Queue(INTERNAL_NOTIFICATION.getQueueName());
    }

    @Bean
    public Queue emailNotificationQueue() {
        return new Queue(EMAIL_NOTIFICATION.getQueueName());
    }

}
