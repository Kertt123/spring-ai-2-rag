package com.serkowski.model;

public record MetadataKeyword(String keyword, String value) {

    public MetadataKeyword {
        if ("type".equals(keyword)) {
            value = DeviceType.from(value).value();
        }
    }
}
