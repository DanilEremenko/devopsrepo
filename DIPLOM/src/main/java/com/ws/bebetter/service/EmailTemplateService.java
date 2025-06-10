package com.ws.bebetter.service;

import lombok.RequiredArgsConstructor;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final VelocityEngine velocityEngine;

    public String buildEmailContent(String templateName, Map<String, Object> model) {
        VelocityContext context = new VelocityContext(model);
        StringWriter writer = new StringWriter();
        velocityEngine.mergeTemplate("templates/notifications/" + templateName, "UTF-8", context, writer);
        return writer.toString();
    }

}

