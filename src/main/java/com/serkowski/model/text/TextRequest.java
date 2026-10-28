package com.serkowski.model.text;

import com.serkowski.model.DeviceType;
import com.serkowski.model.Tag;

import java.util.List;

/** {@code tags} is optional: when present, only chunks tagged with at least one of them are retrieved. */
public record TextRequest(String message, String conversationId, DeviceType type, List<Tag> tags) {
}
