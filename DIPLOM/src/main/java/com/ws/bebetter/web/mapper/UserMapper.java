package com.ws.bebetter.web.mapper;

import com.ws.bebetter.entity.User;
import com.ws.bebetter.web.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<User, UserDto> {
}
