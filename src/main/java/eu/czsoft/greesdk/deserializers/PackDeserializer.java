package eu.czsoft.greesdk.deserializers;

import eu.czsoft.greesdk.packs.BindOkPack;
import eu.czsoft.greesdk.packs.BindPack;
import eu.czsoft.greesdk.packs.DatPack;
import eu.czsoft.greesdk.packs.DevicePack;
import eu.czsoft.greesdk.packs.Pack;
import eu.czsoft.greesdk.packs.ResultPack;
import eu.czsoft.greesdk.packs.StatusPack;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class PackDeserializer implements JsonDeserializer<Pack> {

    @Override
    public Pack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String packType = jsonObject.get("t").getAsString();

        if (packType.equalsIgnoreCase(BindOkPack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, BindOkPack.class);
        } else if (packType.equalsIgnoreCase(BindPack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, BindPack.class);
        } else if (packType.equalsIgnoreCase(DatPack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, DatPack.class);
        } else if (packType.equalsIgnoreCase(ResultPack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, ResultPack.class);
        } else if (packType.equalsIgnoreCase(StatusPack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, StatusPack.class);
        } else if (packType.equalsIgnoreCase(DevicePack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, DevicePack.class);
        }

        return null;
    }
}
