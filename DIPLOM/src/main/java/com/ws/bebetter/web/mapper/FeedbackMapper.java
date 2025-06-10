package com.ws.bebetter.web.mapper;

import com.ws.bebetter.entity.Feedback;
import com.ws.bebetter.web.dto.FeedbackDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedbackMapper extends Mappable<Feedback, FeedbackDto>{
}
