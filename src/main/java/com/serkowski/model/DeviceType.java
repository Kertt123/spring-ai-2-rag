package com.serkowski.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DeviceType {
    BRUSH, HEADPHONES, LIGHT, PURIFIER, VACUUM;

    @JsonValue
    public String value() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static DeviceType from(String value) {
        return valueOf(value.trim().toUpperCase());
    }
}
