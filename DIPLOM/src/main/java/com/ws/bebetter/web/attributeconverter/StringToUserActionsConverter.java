package com.ws.bebetter.web.attributeconverter;

import com.ws.bebetter.web.dto.UserActions;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToUserActionsConverter implements Converter<String, UserActions> {

    @Override
    public UserActions convert(String source) {
        return UserActions.valueOf(source.toUpperCase());
    }

}
