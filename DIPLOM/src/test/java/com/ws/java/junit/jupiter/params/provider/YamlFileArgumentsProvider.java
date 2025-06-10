package com.ws.java.junit.jupiter.params.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.AnnotationBasedArgumentsProvider;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class YamlFileArgumentsProvider extends AnnotationBasedArgumentsProvider<YamlFileSource> {

    @Override
    protected Stream<? extends Arguments> provideArguments(ExtensionContext context, YamlFileSource annotation) {

        String resource = annotation.resource();
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.findAndRegisterModules();
        Class<?> caseType = Arrays.stream(context.getRequiredTestMethod().getParameters())
                .findFirst()
                .map(Parameter::getType)
                .orElseThrow(() -> new IllegalArgumentException("Test method must have one parameter"));

        CollectionType collectionType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, caseType);

        try {
            List<Object> list = objectMapper.readValue(new ClassPathResource(resource).getFile(), collectionType);
            return list.stream().map(o -> Arguments.of(caseType.cast(o)));
        } catch (IOException e) {
            throw new RuntimeException("Yaml file %s has errors: %s".formatted(resource, e.getMessage()));
        }
    }
}
