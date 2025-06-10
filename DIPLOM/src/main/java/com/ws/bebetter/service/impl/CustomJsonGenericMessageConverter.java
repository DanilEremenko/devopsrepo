package com.ws.bebetter.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

public class CustomJsonGenericMessageConverter implements MessageConverter {

    Jackson2JsonMessageConverter jacksonMessageConverter = new Jackson2JsonMessageConverter();
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        return jacksonMessageConverter.toMessage(object, messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        byte[] body = message.getBody();

        Class<?> clazz = determineClass(message);

        try {
            return objectMapper.readValue(body, clazz);
        } catch (Exception e) {
            throw new MessageConversionException("Ошибка при конвертации сообщения", e);
        }
    }

    private Class<?> determineClass(Message message) {
        String type = message.getMessageProperties().getHeader("__TypeId__");
        try {
            return Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Класс для типа %s не найден".formatted(type), e);
        }
    }
}

