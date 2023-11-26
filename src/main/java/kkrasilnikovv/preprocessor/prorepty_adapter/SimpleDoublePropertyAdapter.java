package kkrasilnikovv.preprocessor.prorepty_adapter;

import com.google.gson.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.lang.reflect.Type;

public class SimpleDoublePropertyAdapter implements JsonSerializer<SimpleDoubleProperty>, JsonDeserializer<SimpleDoubleProperty> {
    @Override
    public JsonElement serialize(SimpleDoubleProperty src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.get());
    }

    @Override
    public SimpleDoubleProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return new SimpleDoubleProperty(json.getAsInt());
    }
}
