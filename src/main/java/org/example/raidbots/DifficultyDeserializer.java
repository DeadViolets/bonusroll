package org.example.raidbots;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * Normalises the polymorphic {@code difficulty} field to a plain string.
 *
 * <ul>
 *   <li>Raid items:    {@code "raid-mythic"}  → returned as-is</li>
 *   <li>Dungeon items: {@code {"id":"dungeon-mythic-weekly10",...}} → returns the {@code id} value</li>
 * </ul>
 */
public class DifficultyDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        if (node.isTextual()) {
            return node.asText();
        }
        if (node.isObject()) {
            return node.path("id").asText(null);
        }
        return null;
    }
}
