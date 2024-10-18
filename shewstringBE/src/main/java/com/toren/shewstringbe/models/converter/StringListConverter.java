package com.toren.shewstringbe.models.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {

        if (stringList == null || stringList.isEmpty()) {
            return "";
        }

        return String.join(",", stringList);
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {

        if (s == null || s.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.asList(s.split(","));
    }
}
