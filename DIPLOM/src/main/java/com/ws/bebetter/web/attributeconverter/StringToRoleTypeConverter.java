package com.ws.bebetter.web.attributeconverter;

import com.ws.bebetter.entity.RoleType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToRoleTypeConverter implements Converter<String, RoleType> {

    @Override
    public RoleType convert(String source) {
        if (source.length() == 0) {
            return null;
        }
        return RoleType.valueOf(source.toUpperCase());
    }
}
