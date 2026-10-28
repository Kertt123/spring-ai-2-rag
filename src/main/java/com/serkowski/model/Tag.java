package com.serkowski.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;

public enum Tag {
    SETUP, USAGE, CLEANING, MAINTENANCE, FILTER, BATTERY, CHARGING, TROUBLESHOOTING,
    ERROR_CODES, WARRANTY, SAFETY, SPECS, ACCESSORIES, OTHER;

    @JsonValue
    public String value() {
        return name().toLowerCase().replace('_', '-');
    }

    @JsonCreator
    public static Tag from(String value) {
        return valueOf(value.trim().toUpperCase().replace('-', '_'));
    }

    /** Parses an LLM comma-separated answer, dropping anything that is not a known tag. */
    public static List<String> parse(String llmAnswer) {
        if (llmAnswer == null) {
            return List.of();
        }
        List<String> known = Arrays.stream(values()).map(Tag::value).toList();
        return Arrays.stream(llmAnswer.split(","))
                .map(t -> t.trim().toLowerCase())
                .filter(known::contains)
                .distinct()
                .toList();
    }
}
